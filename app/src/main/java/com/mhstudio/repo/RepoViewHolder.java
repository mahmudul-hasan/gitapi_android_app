package com.mhstudio.repo;

import android.view.View;
import android.widget.TextView;

import com.mhstudio.weworkgithubapp.R;

/**
 * Created by mahmudul on 4/7/17.
 */

public class RepoViewHolder {
    TextView tvRepoName, tvRepoDesc, tvRepoLanguage;

    public RepoViewHolder(View view){
        this.tvRepoName = (TextView) view.findViewById(R.id.repo_name);
        this.tvRepoDesc = (TextView) view.findViewById(R.id.repo_desc);
        this.tvRepoLanguage = (TextView) view.findViewById(R.id.repo_language);
    }
}
