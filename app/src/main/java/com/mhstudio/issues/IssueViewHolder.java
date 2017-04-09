package com.mhstudio.issues;

import android.view.View;
import android.widget.TextView;

import com.mhstudio.weworkgithubapp.R;

/**
 * Created by mahmudul on 4/8/17.
 */

public class IssueViewHolder {
    public TextView tvIssueName, tvIssueNum, tvIssueState;

    public IssueViewHolder(View view) {
        this.tvIssueName = (TextView) view.findViewById(R.id.issue_name);
        this.tvIssueNum = (TextView) view.findViewById(R.id.issue_number);
        this.tvIssueState = (TextView) view.findViewById(R.id.issue_open_close);
    }
}
