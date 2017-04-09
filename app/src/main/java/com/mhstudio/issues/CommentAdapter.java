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
 * Created by mahmudul on 4/9/17.
 */

public class CommentAdapter extends BaseAdapter {

    private MainActivity mActivity;
    private CommentModel[] mModelArray;

    public CommentAdapter(MainActivity mActivity, CommentModel[] mModelArray) {
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
    public View getView(int position, View convertView, ViewGroup parent) {

        View theView = convertView;
        CommentModel model = (CommentModel) getItem(position);

        if(theView == null){
            LayoutInflater li = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            theView = li.inflate(R.layout.listelement_comment, null);
        }

        TextView commentText = (TextView) theView.findViewById(R.id.comment_text);
        TextView commentAuthor = (TextView) theView.findViewById(R.id.comment_author);

        commentText.setText(model.getBody());
        commentAuthor.setText("- "+model.getUser().getLogin());

        return theView;
    }
}
