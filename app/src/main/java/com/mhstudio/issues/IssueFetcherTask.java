package com.mhstudio.issues;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhstudio.weworkgithubapp.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mahmudul on 4/9/17.
 */

public class IssueFetcherTask extends AsyncTask<Void, Void, IssuesModel[]> {

    private String urlString;
    private MainActivity context;

    public IssueFetcherTask(String urlString, MainActivity context) {
        this.urlString = urlString;
        this.context = context;
    }

    @Override
    protected IssuesModel[] doInBackground(Void... params) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inline;
            StringBuffer resp = new StringBuffer();
            while ((inline = in.readLine()) != null) {
                resp.append(inline);
            }
            in.close();

            GsonBuilder gbuilder = new GsonBuilder();
            Gson gson = gbuilder.create();
            IssuesModel[] issues = gson.fromJson(resp.toString(), IssuesModel[].class);

            return issues;

        }catch (IOException e){

        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(IssuesModel[] issues) {
        super.onPostExecute(issues);

        for(int i=0; i<issues.length; i++){
            Log.i("JSONRESP", issues[i].getPullRequest()==null? "null" : "PR");
        }
        Log.i("JSONRESP", issues.length+"");

        //Filtering out the pull requests
        ArrayList<IssuesModel> issueList = new ArrayList<>();
        for (IssuesModel issue : issues){
            if(issue.getPullRequest() == null){
                issueList.add(issue);
            }
        }
        IssuesModel[] issuesOnly = new IssuesModel[issueList.size()];
        context.setIssuesArray(issueList.toArray(issuesOnly));

        //After the data is fetched notify it
        context.loadIssuesFragment();
    }
}
