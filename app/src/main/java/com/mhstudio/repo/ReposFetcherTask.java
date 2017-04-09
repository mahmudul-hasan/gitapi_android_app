package com.mhstudio.repo;

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

public class ReposFetcherTask extends AsyncTask<Void, Void, ReposModel[]> {

    private MainActivity context;
    private String userName;

    public ReposFetcherTask(MainActivity context, String userName) {
        this.context = context;
        this.userName = userName;
    }

    @Override
    protected ReposModel[] doInBackground(Void... params) {
        String urlString = "https://api.github.com/users/"+userName+"/repos";
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inline;
            StringBuffer resp = new StringBuffer();
            while((inline = in.readLine()) != null){
                resp.append(inline);
            }
            in.close();

            GsonBuilder gbuilder = new GsonBuilder();
            Gson gson = gbuilder.create();
            ReposModel[] repos = gson.fromJson(resp.toString(), ReposModel[].class);

            return repos;

        }catch (IOException e){

        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(ReposModel[] repos) {
        super.onPostExecute(repos);
        //onPostExecute() method executes on UI thread

        //Set the data
        context.setReposArray(repos);

        //Once the repositories are fetched load up the ReposFragment
        context.loadReposFragment();
    }
}
