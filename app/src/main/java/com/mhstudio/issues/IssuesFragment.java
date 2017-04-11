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

    private ListView mListView;
    private FloatingActionButton mAddIssue;
    private IssueAdapter mAdapter;

    public interface IssueListener{
        void onIssueSelected(String url, int issueNum);
        void requestIssuesDataUpdate();
    }
    private IssueListener mListener;

    public IssuesFragment() {
        // Required empty public constructor
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

        mListView = (ListView) view.findViewById(R.id.issues_listview);
        mAddIssue = (FloatingActionButton) view.findViewById(R.id.issues_create_fab);

        mAdapter = new IssueAdapter(mActivity, mIssuesArray);
        mListView.setAdapter(mAdapter);

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
        mListener.requestIssuesDataUpdate();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setUpdatedIssues(IssuesModel[] data){
        mIssuesArray = data;
        mAdapter.setData(mIssuesArray);
        ((IssueAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    private void initiateIssueCreation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final View issueCreateView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_issue_create, null);
        builder.setView(issueCreateView);
        builder.setTitle(getString(R.string.create_issue));
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText title = (EditText) issueCreateView.findViewById(R.id.issuecreate_title);
                EditText body = (EditText) issueCreateView.findViewById(R.id.issuecreate_body);

                String titleStr = title.getText().toString().trim();
                String bodyStr = body.getText().toString().trim();

                if(!TextUtils.isEmpty(titleStr)) (new AddIssueTask(titleStr, bodyStr)).execute();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        }
    }
}
