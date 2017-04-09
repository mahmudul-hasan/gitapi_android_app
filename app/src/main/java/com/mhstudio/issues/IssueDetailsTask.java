package com.mhstudio.issues;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhstudio.weworkgithubapp.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mahmudul on 4/9/17.
 */

public class IssueDetailsTask extends AsyncTask<Void, Void, IssuesModel> {

    private MainActivity context;
    private String urlStr;
    private HttpURLConnection connection = null;

    public IssueDetailsTask(MainActivity context, String urlStr) {
        this.context = context;
        this.urlStr = urlStr;
    }

    @Override
    protected IssuesModel doInBackground(Void... params) {

        try {
            URL url = new URL(urlStr);
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
            IssuesModel issuesModel = gson.fromJson(resp.toString(), IssuesModel.class);

            return issuesModel;

        }catch (IOException e){

        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(IssuesModel issuesModel) {
        super.onPostExecute(issuesModel);

        //Set the fetched model
        context.setIssuesModel(issuesModel);

        //Load up the IssueDetailsFragment
        context.loadIssueDetailsFragment();
    }
}
