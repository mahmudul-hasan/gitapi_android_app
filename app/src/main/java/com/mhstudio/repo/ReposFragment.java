package com.mhstudio.repo;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mhstudio.weworkgithubapp.MainActivity;
import com.mhstudio.weworkgithubapp.R;

public class ReposFragment extends Fragment {

    private MainActivity mActivity;
    private ReposModel[] mModelArray;
    private RepoAdapter mAdapter;

    private ListView mListView;

    public interface RepositoryListener{
        void onRepositorySelected(String repoName);
    }
    private RepositoryListener mListener;

    public ReposFragment() {

    }

    public static ReposFragment newInstance() {
        ReposFragment fragment = new ReposFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_repos, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.repos_listview);

        //Setting up the adapter with the data obtained from JSON and populate the ListView
        mAdapter = new RepoAdapter(mActivity, mModelArray);
        mListView.setAdapter(mAdapter);

        //List item selection event handler setup
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReposModel selected = (ReposModel) mAdapter.getItem(position);
                mListener.onRepositorySelected(selected.getName());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //Setting up the page name on the actionbar
        mActivity.getSupportActionBar().setTitle(R.string.repos_title);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
        mModelArray = mActivity.getReposArray();

        if(mActivity instanceof RepositoryListener){
            mListener = mActivity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
