package com.mhstudio.issues;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mhstudio.weworkgithubapp.MainActivity;
import com.mhstudio.weworkgithubapp.R;

/**
 * Created by mahmudul on 4/8/17.
 */

public class IssueAdapter extends BaseAdapter {

    private MainActivity mActivity;
    private IssuesModel[] mModelArray;

    public IssueAdapter(MainActivity mActivity, IssuesModel[] mModelArray) {
        this.mActivity = mActivity;
        this.mModelArray = mModelArray;
    }

    @Override
    public int getCount() {
        return mModelArray!=null ? mModelArray.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return mModelArray!=null ? mModelArray[position] : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View theView = convertView;
        IssuesModel model = (IssuesModel) getItem(position);
        IssueViewHolder viewHolder;

        if(theView == null){
            LayoutInflater li = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            theView = li.inflate(R.layout.listelement_issue, null);

            viewHolder = new IssueViewHolder(theView);

            theView.setTag(viewHolder);
        }else{
            viewHolder = (IssueViewHolder) theView.getTag();
        }

        if(viewHolder != null){
            setValuesToViews(model, viewHolder);
        }

        return theView;
    }

    private void setValuesToViews(IssuesModel model, IssueViewHolder viewHolder) {
        viewHolder.tvIssueName.setText(model.getTitle());
        viewHolder.tvIssueNum.setText("#"+model.getNumber());
        viewHolder.tvIssueState.setText(model.getState().equals("open") ? "Open" : "Closed");
    }

    public void setData(IssuesModel[] data){
        mModelArray = data;
    }
}
