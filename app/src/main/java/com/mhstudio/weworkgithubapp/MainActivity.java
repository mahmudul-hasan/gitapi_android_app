package com.mhstudio.weworkgithubapp;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhstudio.issues.IssueDetailsFragment;
import com.mhstudio.issues.IssueDetailsTask;
import com.mhstudio.issues.IssueFetcherTask;
import com.mhstudio.issues.IssuesFragment;
import com.mhstudio.issues.IssuesModel;
import com.mhstudio.profile.ProfileFetcherTask;
import com.mhstudio.profile.ProfileFragment;
import com.mhstudio.profile.ProfileModel;
import com.mhstudio.repo.ReposFetcherTask;
import com.mhstudio.repo.ReposFragment;
import com.mhstudio.repo.ReposModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ReposFragment.RepositoryListener, IssuesFragment.IssueListener {

    private Button mProfile, mRepo;
    private LinearLayout llButtons;

    private ProfileFragment mProfileFragment;
    private ReposFragment mReposFragment;
    private IssuesFragment mIssuesFragment;
    private IssueDetailsFragment mIssueDetailsFragment;

    private ProfileModel mProfileModel;
    private ReposModel[] mReposArray;
    private IssuesModel[] mIssuesArray;
    private IssuesModel mIssuesModel;

    private String mUser = "octocat";
    private String mSelectedRepo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProfileModel = null;
        mReposArray = null;
        mIssuesArray = null;

        //Get the views' references from the layout
        mProfile = (Button) findViewById(R.id.btn_profile);
        mRepo = (Button) findViewById(R.id.btn_repo);
        llButtons = (LinearLayout) findViewById(R.id.ll_buttons);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_profile:
                        (new ProfileFetcherTask(MainActivity.this, mUser)).execute();
                        Log.i("BUTTON", "profile");
                        break;

                    case R.id.btn_repo:
                        (new ReposFetcherTask(MainActivity.this, mUser)).execute();
                        Log.i("BUTTON", "repo");
                        break;
                }
            }
        };

        //Register click listeners for buttons
        mProfile.setOnClickListener(onClickListener);
        mRepo.setOnClickListener(onClickListener);

        //check if proper fragment back navigation is happening
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.i("BACKENTRYCOUNT", ""+getSupportFragmentManager().getBackStackEntryCount());
                if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                    llButtons.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void startFragment(Fragment fragment){
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragMan.beginTransaction();
        if (fragment != null){
            //replace or put the fragment on the container
            fragTrans.replace(R.id.fragment_container, fragment);

            //put the fragment into the backstack for the proper back navigation
            fragTrans.addToBackStack(fragment.getTag());

            //commit the fragment transaction
            fragTrans.commit();
        }
    }

    public void setProfileModel(ProfileModel model){
        mProfileModel = model;
    }
    public ProfileModel getProfileModel(){
        return mProfileModel;
    }

    public void setReposArray(ReposModel[] data){
        mReposArray = data;
    }
    public ReposModel[] getReposArray(){
        return mReposArray;
    }

    public void setIssuesArray(IssuesModel[] data){
        mIssuesArray = data;
    }
    public IssuesModel[] getIssuesArray(){
        return mIssuesArray;
    }

    public void setIssuesModel(IssuesModel model){
        mIssuesModel = model;
    }
    public IssuesModel getIssuesModel(){
        return mIssuesModel;
    }

    public void loadProfileFragment(){
        if(mProfileFragment == null){
            mProfileFragment = ProfileFragment.newInstance();
        }
        startFragment(mProfileFragment);
        llButtons.setVisibility(View.GONE);
    }

    public void loadReposFragment(){
        if(mReposFragment == null){
            mReposFragment = ReposFragment.newInstance();
        }
        startFragment(mReposFragment);
        llButtons.setVisibility(View.GONE);
    }

    public void loadIssuesFragment(){
        if(mIssuesFragment == null){
            mIssuesFragment = IssuesFragment.newInstance();
        }
        startFragment(mIssuesFragment);
    }

    public void loadIssueDetailsFragment(){
        if(mIssueDetailsFragment == null){
            mIssueDetailsFragment = IssueDetailsFragment.newInstance();
        }
        startFragment(mIssueDetailsFragment);
    }

    @Override
    public void onIssueSelected(String url) {
        (new IssueDetailsTask(MainActivity.this, url)).execute();
    }

    @Override
    public void onRepositorySelected(String repoName) {
        mSelectedRepo = repoName;
        String urlStr = "https://api.github.com/repos/"+mUser+"/"+repoName+"/issues?state=all";
        (new IssueFetcherTask(urlStr, MainActivity.this)).execute();
    }
}
