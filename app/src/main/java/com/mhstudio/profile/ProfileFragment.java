package com.mhstudio.profile;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mhstudio.weworkgithubapp.MainActivity;
import com.mhstudio.weworkgithubapp.R;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private MainActivity mActivity;
    private ProfileModel mModel;

    private TextView mName, mUserName, mCompany, mLocation, mEmail, mLink, mRepos, mGists, mFollowers, mFollowing;
    private ImageView mProfilePic;

    public interface OnProfileFragmentListener {
        void onFragmentInteraction(Uri uri);
    }
    private OnProfileFragmentListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting the references of the views
        mProfilePic = (ImageView) view.findViewById(R.id.profile_pic);
        mName = (TextView) view.findViewById(R.id.profile_name);
        mUserName = (TextView) view.findViewById(R.id.profile_username);
        mCompany = (TextView) view.findViewById(R.id.profile_company);
        mLocation = (TextView) view.findViewById(R.id.profile_location);
        mEmail = (TextView) view.findViewById(R.id.profile_email);
        mLink = (TextView) view.findViewById(R.id.profile_weblink);
        mRepos = (TextView) view.findViewById(R.id.profile_repos);
        mGists = (TextView) view.findViewById(R.id.profile_gists);
        mFollowers = (TextView) view.findViewById(R.id.profile_followers);
        mFollowing = (TextView) view.findViewById(R.id.profile_following);

        //Setting the values to the views
        if(mModel != null){
            Picasso.with(mActivity).load(mModel.getAvatarUrl()).into(mProfilePic);
            mName.setText(mModel.getName());
            mUserName.setText(mModel.getLogin());
            mCompany.setText((String) mModel.getCompany());
            mLocation.setText((String) mModel.getLocation());
            mEmail.setText((String) mModel.getEmail());
            mLink.setText((String) mModel.getBlog());
            mRepos.setText(getString(R.string.profile_repos) + String.valueOf(mModel.getPublicRepos()));
            mGists.setText(getString(R.string.profile_gists) + String.valueOf(mModel.getPublicGists()));
            mFollowers.setText(getString(R.string.profile_followers) + String.valueOf(mModel.getFollowers()));
            mFollowing.setText(getString(R.string.profile_following) + String.valueOf(mModel.getFollowing()));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
        mModel = mActivity.getProfileModel();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
