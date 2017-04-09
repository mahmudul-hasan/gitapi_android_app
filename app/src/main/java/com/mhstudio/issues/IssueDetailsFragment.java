package com.mhstudio.issues;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhstudio.weworkgithubapp.MainActivity;
import com.mhstudio.weworkgithubapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IssueDetailsFragment extends Fragment {

    private MainActivity mActivity;
    private IssuesModel mModel;

    private TextView mTitle, mBody, mAuthor;
    private ListView mCommentsListView;

    private CommentModel[] mComments;
    private CommentAdapter mAdapter;

    public IssueDetailsFragment() {
        // Required empty public constructor
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
        mCommentsListView = (ListView) view.findViewById(R.id.issue_detail_commentlist);

        mTitle.setText(mModel.getTitle() + " #" + mModel.getNumber());
        mBody.setText(mModel.getBody());
        mAuthor.setText(mModel.getUser().getLogin());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
        mModel = mActivity.getIssuesModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        (new CommentsFetcherTask(mModel.getCommentsUrl())).execute();
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
}
