package com.mhstudio.issues;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mhstudio.weworkgithubapp.MainActivity;
import com.mhstudio.weworkgithubapp.R;

public class IssuesFragment extends Fragment {

    private MainActivity mActivity;
    private IssuesModel[] mIssuesArray;

    private ListView mListView;
    private IssueAdapter mAdapter;

    public interface IssueListener{
        void onIssueSelected(String url);
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

        mAdapter = new IssueAdapter(mActivity, mIssuesArray);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //goto that selected IssueDetails
                IssuesModel selected = (IssuesModel) mAdapter.getItem(position);
                mListener.onIssueSelected(selected.getUrl());
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
        mIssuesArray = mActivity.getIssuesArray();

        if(mActivity instanceof IssueListener){
            mListener = (IssueListener) mActivity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
