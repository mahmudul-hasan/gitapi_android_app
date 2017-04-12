package com.mhstudio.issues;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mhstudio.weworkgithubapp.MainActivity;
import com.mhstudio.weworkgithubapp.R;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;

public class IssuesFragment extends Fragment {

    private MainActivity mActivity;
    private IssuesModel[] mIssuesArray;

    private TextView mNoIssueYet;
    private ListView mListView;
    private FloatingActionButton mAddIssue;
    private IssueAdapter mAdapter;

    public interface IssueListener{
        void onIssueSelected(String url, int issueNum);
        void requestIssuesDataUpdate();
    }
    private IssueListener mListener;

    public IssuesFragment() {

    }

    public static IssuesFragment newInstance() {
        IssuesFragment fragment = new IssuesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_issues, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNoIssueYet = (TextView) view.findViewById(R.id.issues_not_yet);
        mListView = (ListView) view.findViewById(R.id.issues_listview);
        mAddIssue = (FloatingActionButton) view.findViewById(R.id.issues_create_fab);

        mAdapter = new IssueAdapter(mActivity, mIssuesArray);
        mListView.setAdapter(mAdapter);

        //Setting click events
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //goto that selected IssueDetails
                IssuesModel selected = (IssuesModel) mAdapter.getItem(position);
                mListener.onIssueSelected(selected.getUrl(), selected.getNumber());
            }
        });

        mAddIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateIssueCreation();
            }
        });

        //Initially check if the selected repo has no issue yet
        noIssueYet();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
        mIssuesArray = mActivity.getIssuesArray();

        if(mActivity instanceof IssueListener){
            mListener = mActivity;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Setting up the page name on the actionbar
        mActivity.getSupportActionBar().setTitle(R.string.issues_title);

        //When coming back from the IssueDetailsFragment after something updated there, request
        //the updated list and refresh the issues
        mListener.requestIssuesDataUpdate();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void noIssueYet() {
        if(mIssuesArray.length > 0){
            mNoIssueYet.setVisibility(View.GONE);
        }else{
            mNoIssueYet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Helper methods and private classes
     */
    public void setUpdatedIssues(IssuesModel[] data){
        mIssuesArray = data;
        //once the updated issue data is obtained check if we still have no issue
        noIssueYet();

        //Then update the adapter and the listview with the new data
        mAdapter.setData(mIssuesArray);
        mAdapter.notifyDataSetChanged();
        ((IssueAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    private void initiateIssueCreation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final View issueCreateView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_issue_create, null);
        builder.setView(issueCreateView);
        builder.setTitle(getString(R.string.create_issue));
        builder.setPositiveButton(getString(R.string.create), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //If we click Create button it will proceed with the AddIssueTask to create new issue
                EditText title = (EditText) issueCreateView.findViewById(R.id.issuecreate_title);
                EditText body = (EditText) issueCreateView.findViewById(R.id.issuecreate_body);

                String titleStr = title.getText().toString().trim();
                String bodyStr = body.getText().toString().trim();

                if(!TextUtils.isEmpty(titleStr)) (new AddIssueTask(titleStr, bodyStr)).execute();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public boolean submitNewIssue(String title, String body) {
        Issue issue = null;

        if (TextUtils.isEmpty(title)) {
            return false;
        }
        issue = new Issue().setTitle(title).setBody(body);

        RepositoryService repoService = new RepositoryService(mActivity.getGithubClient());
        try {
            Repository repository = repoService.getRepository(mActivity.getUser(), mActivity.getSelectedRepo());
            IssueService issueService = new IssueService(mActivity.getGithubClient());
            issueService.createIssue(repository, issue);

            return true;
        } catch (IOException ex) {
            Log.i("ISSUECREATE", ex.getMessage());
            return false;
        }
    }

    private class AddIssueTask extends AsyncTask<Void, Void, Boolean>{

        String issueTitle, issueBody;

        public AddIssueTask(String issueTitle, String issueBody) {
            this.issueTitle = issueTitle;
            this.issueBody = issueBody;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return submitNewIssue(issueTitle, issueBody);
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(isSuccess) mListener.requestIssuesDataUpdate();
            else Toast.makeText(mActivity, "Something went wrong while creating the issue.", Toast.LENGTH_LONG).show();
        }
    }
}
