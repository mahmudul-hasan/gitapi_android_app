package com.mhstudio.issues;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhstudio.weworkgithubapp.MainActivity;
import com.mhstudio.weworkgithubapp.R;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IssueDetailsFragment extends Fragment {

    private MainActivity mActivity;
    private IssuesModel mModel;

    private TextView mTitle, mBody, mAuthor, mState;
    private Button mEdit, mChangeState;
    private ImageView mCurrentStateImg;
    private ListView mCommentsListView;

    private CommentModel[] mComments;
    private CommentAdapter mAdapter;

    private String mTitleStr, mBodyStr;

    public interface IssueDetailsListener{
        void onIssueChanged();
    }
    private IssueDetailsListener mListener;

    public IssueDetailsFragment() {

    }

    public static IssueDetailsFragment newInstance() {
        IssueDetailsFragment fragment = new IssueDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_issue_details, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitle = (TextView) view.findViewById(R.id.issue_detail_title);
        mBody = (TextView) view.findViewById(R.id.issue_detail_body);
        mAuthor = (TextView) view.findViewById(R.id.issue_detail_author);
        mState = (TextView) view.findViewById(R.id.issue_detail_state);
        mEdit = (Button) view.findViewById(R.id.issue_detail_edit);
        mChangeState = (Button) view.findViewById(R.id.issue_detail_changestate);
        mCurrentStateImg = (ImageView) view.findViewById(R.id.issue_detail_state_image);
        mCommentsListView = (ListView) view.findViewById(R.id.issue_detail_commentlist);

        //Initially populate these views
        populateUI();

        //Set the click event listeners
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateIssueUpdate();
            }
        });

        mChangeState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IssueEditTask().execute("state");
                setupStateIcons();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
        mModel = mActivity.getIssuesModel();

        if(mActivity instanceof IssueDetailsListener){
            mListener = mActivity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set the page title on the actionbar
        mActivity.getSupportActionBar().setTitle(R.string.issuedetail_title);

        //Setup the Open/Close state images and buttons according to the data
        setupStateIcons();

        //Fetch the comments if any, associated with this issue
        (new CommentsFetcherTask(mModel.getCommentsUrl())).execute();
    }

    /**
     * Helpr methods and private classes
     */
    private void populateUI() {
        if(mModel != null) {
            mTitle.setText(mModel.getTitle() + " #" + mModel.getNumber());
            mBody.setText(mModel.getBody());
            mAuthor.setText(mModel.getUser().getLogin());
            mState.setText(mModel.getState().equals("open") ? "Open" : "Closed");
        }
    }

    private void setupStateIcons(){
        if(mModel.getState().equals("open")){
            mCurrentStateImg.setImageResource(R.drawable.ic_open);
            mChangeState.setBackgroundResource(R.drawable.ic_close);
        }else{
            mCurrentStateImg.setImageResource(R.drawable.ic_close);
            mChangeState.setBackgroundResource(R.drawable.ic_open);
        }
    }

    public void updateUI(){
        mModel = mActivity.getIssuesModel();
        populateUI();
        setupStateIcons();
    }

    private void initiateIssueUpdate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final View issueCreateView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_issue_create, null);
        builder.setView(issueCreateView);
        builder.setTitle(getString(R.string.edit_issue));

        final EditText title = (EditText) issueCreateView.findViewById(R.id.issuecreate_title);
        final EditText body = (EditText) issueCreateView.findViewById(R.id.issuecreate_body);

        title.setText(mTitle.getText().toString().trim());
        body.setText(mBody.getText().toString().trim());

        builder.setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTitleStr = title.getText().toString().trim();
                mBodyStr = body.getText().toString().trim();
                if(!TextUtils.isEmpty(mTitleStr)) new IssueEditTask().execute("titlebody");
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private class CommentsFetcherTask extends AsyncTask<Void, Void, CommentModel[]> {

        private String urlStr;

        public CommentsFetcherTask(String urlStr) {
            this.urlStr = urlStr;
        }

        @Override
        protected CommentModel[] doInBackground(Void... params) {

            HttpURLConnection connection = null;

            try {
                URL url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String inline;
                StringBuffer resp = new StringBuffer();
                while ((inline = in.readLine()) != null) {
                    resp.append(inline);
                }
                in.close();

                GsonBuilder gbuilder = new GsonBuilder();
                Gson gson = gbuilder.create();
                CommentModel[] comments = gson.fromJson(resp.toString(), CommentModel[].class);

                return comments;

            }catch (IOException e){

            }finally {
                if(connection != null){
                    connection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(CommentModel[] comments) {
            super.onPostExecute(comments);

            mComments = comments;

            //Now setup the adapter and listview
            mAdapter = new CommentAdapter(mActivity, mComments);
            mCommentsListView.setAdapter(mAdapter);
        }
    }

    private class IssueEditTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... params) {
            String editType = params[0];
            IssueService issueService = new IssueService(mActivity.getGithubClient());
            try {
                Issue issue = issueService.getIssue(mActivity.getUser(), mActivity.getSelectedRepo(), mActivity.getSelectedIssueNum());
                if(editType.equals("state")) {
                    if (issue.getState().equals("open")) {
                        issue.setState("closed");
                    } else {
                        issue.setState("open");
                    }
                }else if(editType.equals("titlebody")){
                    issue.setTitle(mTitleStr).setBody(mBodyStr);
                }
                issueService.editIssue(mActivity.getUser(), mActivity.getSelectedRepo(), issue);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            if(isSuccess){
                if(mListener != null) mListener.onIssueChanged();
            }else{
                Toast.makeText(mActivity, "Could not edit the issue.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
