package com.mhstudio.repo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mhstudio.weworkgithubapp.MainActivity;
import com.mhstudio.weworkgithubapp.R;

/**
 * Created by mahmudul on 4/7/17.
 */

public class RepoAdapter extends BaseAdapter {

    private MainActivity mActivity;
    private ReposModel[] mModelArray;

    public RepoAdapter(Context context, ReposModel[] data){
        this.mActivity = (MainActivity) context;
        this.mModelArray = data;
    }

    @Override
    public int getCount() {
        return mModelArray != null ? mModelArray.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return mModelArray != null ? mModelArray[position] : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View theView = convertView;
        RepoViewHolder viewHolder;
        ReposModel model = (ReposModel) getItem(position);

        if(theView == null){
            LayoutInflater li = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            theView = li.inflate(R.layout.listelement_repo, null);
            viewHolder = new RepoViewHolder(theView);

            setValuesToViews(viewHolder, model);

            theView.setTag(viewHolder);
        }else{
            viewHolder = (RepoViewHolder) theView.getTag();
        }

        if(viewHolder != null) {
            setValuesToViews(viewHolder, model);
        }

        return theView;
    }

    private void setValuesToViews(RepoViewHolder viewHolder, ReposModel model) {
        viewHolder.tvRepoName.setText(model.getName());
        viewHolder.tvRepoDesc.setText((String) model.getDescription());
        viewHolder.tvRepoLanguage.setText((String) model.getLanguage());
    }
}
