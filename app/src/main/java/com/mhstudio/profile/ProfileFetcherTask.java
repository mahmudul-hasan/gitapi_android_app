package com.mhstudio.profile;

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

public class ProfileFetcherTask extends AsyncTask<Void, Void, ProfileModel> {

    private String userName;
    private MainActivity context;

    public ProfileFetcherTask(MainActivity context, String userName) {
        this.userName = userName;
        this.context = context;
    }

    @Override
    protected ProfileModel doInBackground(Void... params) {
        String urlString = "https://api.github.com/users/"+userName;
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
            ProfileModel profileModel = gson.fromJson(resp.toString(), ProfileModel.class);

            return profileModel;

        }catch (IOException e){

        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(ProfileModel response) {
        super.onPostExecute(response);

        //this method executes on UI thread
        context.setProfileModel(response);

        //Once the profile is fetched load up the ProfileFragment
        context.loadProfileFragment();
    }
}
