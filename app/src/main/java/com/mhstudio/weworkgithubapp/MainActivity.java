package com.mhstudio.weworkgithubapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ReposFragment.RepositoryListener,
        IssuesFragment.IssueListener, IssueDetailsFragment.IssueDetailsListener {

    private Button mProfile, mRepo, mLogin;
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
    private String mPassword = "";
    private String mSelectedRepo = null;
    private int mSelectedIssueNum = -1;
    private String mSelectedIssueUrl = null;

    private GitHubClient mGithubClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.home_title);

        mGithubClient = new GitHubClient();

        mProfileModel = null;
        mReposArray = null;
        mIssuesArray = null;

        //Get the views' references from the layout
        mLogin = (Button) findViewById(R.id.btn_login);
        mProfile = (Button) findViewById(R.id.btn_profile);
        mRepo = (Button) findViewById(R.id.btn_repo);
        llButtons = (LinearLayout) findViewById(R.id.ll_buttons);

        mProfile.setVisibility(View.GONE);
        mRepo.setVisibility(View.GONE);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_login:
                        if(mLogin.getText().toString().equals(getString(R.string.signin))) {
                            initiateLoginProcess();
                        }else{
                            mLogin.setText(R.string.signin);
                            mProfile.setVisibility(View.GONE);
                            mRepo.setVisibility(View.GONE);
                        }
                        break;

                    case R.id.btn_profile:
                        (new ProfileFetcherTask(MainActivity.this, mUser)).execute();
                        break;

                    case R.id.btn_repo:
                        (new ReposFetcherTask(MainActivity.this, mUser)).execute();
                        break;
                }
            }
        };

        //Register click listeners for buttons
        mLogin.setOnClickListener(onClickListener);
        mProfile.setOnClickListener(onClickListener);
        mRepo.setOnClickListener(onClickListener);

        //check if proper fragment back navigation is happening
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                    llButtons.setVisibility(View.VISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setTitle(R.string.home_title);
                }else{
                    llButtons.setVisibility(View.GONE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Getters and setters
     * */
    public GitHubClient getGithubClient(){
        return mGithubClient;
    }

    public String getUser(){
        return mUser;
    }

    public String getSelectedRepo(){
        return mSelectedRepo;
    }

    public int getSelectedIssueNum(){
        return mSelectedIssueNum;
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

    /**
     * Fragment loaders
     * */
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

    public void loadProfileFragment(){
        if(mProfileFragment == null){
            mProfileFragment = ProfileFragment.newInstance();
        }
        startFragment(mProfileFragment);
    }

    public void loadReposFragment(){
        if(mReposFragment == null){
            mReposFragment = ReposFragment.newInstance();
        }
        startFragment(mReposFragment);
    }

    public void loadIssuesFragment(){
        if(mIssuesFragment == null){
            mIssuesFragment = IssuesFragment.newInstance();
        }
        if(!mIssuesFragment.isAdded()) startFragment(mIssuesFragment);
        else mIssuesFragment.setUpdatedIssues(mIssuesArray);
    }

    public void loadIssueDetailsFragment(){
        if(mIssueDetailsFragment == null){
            mIssueDetailsFragment = IssueDetailsFragment.newInstance();
        }
        if(!mIssueDetailsFragment.isAdded()) startFragment(mIssueDetailsFragment);
        else mIssueDetailsFragment.updateUI();
    }

    /**
     * Overridden callbacks for the implemented interfaces
     * */
    @Override
    public void onIssueSelected(String url, int issueNum) {
        mSelectedIssueUrl = url;
        mSelectedIssueNum = issueNum;
        (new IssueDetailsTask(MainActivity.this, url)).execute();
    }

    @Override
    public void requestIssuesDataUpdate() {
        onRepositorySelected(mSelectedRepo);
    }

    @Override
    public void onIssueChanged() {
        if(mSelectedIssueUrl != null) onIssueSelected(mSelectedIssueUrl, mSelectedIssueNum);
    }

    @Override
    public void onRepositorySelected(String repoName) {
        mSelectedRepo = repoName;
        String urlStr = "https://api.github.com/repos/"+mUser+"/"+repoName+"/issues?state=all";
        (new IssueFetcherTask(urlStr, MainActivity.this)).execute();
    }

    /**
     * Private helper methods and classes
     * */
    private void initiateLoginProcess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View loginView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_login, null);
        builder.setView(loginView);
        builder.setTitle(R.string.login);
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView userName = (TextView) loginView.findViewById(R.id.login_username);
                TextView password = (TextView) loginView.findViewById(R.id.login_password);
                mUser = userName.getText().toString().trim();
                mPassword = password.getText().toString().trim();

                mGithubClient.setCredentials(mUser, mPassword);

                (new AuthenticationTask()).execute();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private class AuthenticationTask extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {

            //Validating login
            RepositoryService rs = new RepositoryService(mGithubClient);
            try{
                Log.i("LIBTEST", rs.getRepositories().size()+"");
                return true;
            }catch (IOException e){
                Log.i("LIBTEST", e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isLoggedIn) {
            super.onPostExecute(isLoggedIn);
            //Setup the home UI based on the validation
            if(isLoggedIn){
                mLogin.setText(R.string.signout);
                mProfile.setVisibility(View.VISIBLE);
                mRepo.setVisibility(View.VISIBLE);
            }else{
                mLogin.setText(R.string.signin);
                mProfile.setVisibility(View.GONE);
                mRepo.setVisibility(View.GONE);

                Toast.makeText(MainActivity.this, "Failed to log in. Please check your credentials.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
