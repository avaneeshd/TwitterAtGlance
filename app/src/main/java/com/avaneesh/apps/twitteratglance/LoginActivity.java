package com.avaneesh.apps.twitteratglance;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class LoginActivity extends Activity {
    SharedPreferences pref;
    public String TAG = "LoginActivity";

    private TwitterSession session;
    private ConnectionDetector con;
    private Twitter twitter;
    private RequestToken requestToken, bkpRequestToken;
    private AuthenticationTask mAuthTask;
    private RetriveAccessTokenTask mAccessTokenTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Create Activity");
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        session = TwitterSession.getInstance(this);
        con = new ConnectionDetector(this);
        if(!con.isConnectingToInternet()){
            Toast.makeText(LoginActivity.this,getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            return;
        }
        if(Constants.CONSUMER_KEY.equals("") || Constants.CONSUMER_SECRET.equals("")){
            Toast.makeText(LoginActivity.this,"Oauth Tokens Missing" , Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "inside onResume");
        System.out.println("Intent"+ getIntent().getData());
        onNewIntent(getIntent());
        if(session!=null)
            if(session.isTwitterLoggedInAlready()) start();

    }

    @Override
    public void onPause() {
        super.onPause();
        if(session!=null)
            if(session.isTwitterLoggedInAlready()) finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if(mAccessTokenTask != null)
            return;
        if(!session.isTwitterLoggedInAlready()){
            if(uri != null && uri.getScheme().equals("oauth")){
                String verifier = uri.getQueryParameter(Constants.URL_TWITTER_OAUTH_VERIFIER);
                mAccessTokenTask = new RetriveAccessTokenTask();
                mAccessTokenTask.execute(verifier);
            }
        }
    }

    public void onAuth(View view){
        Log.d(TAG, "inside OnAuth");
        if(mAuthTask != null) return;
        if(!session.isTwitterLoggedInAlready()){
            mAuthTask = new AuthenticationTask();
            mAuthTask.execute((Void) null);
        }else{
            Toast.makeText(LoginActivity.this, "Already Logged in on Twitter",Toast.LENGTH_LONG).show();
            return;
        }
    }

    public class RetriveAccessTokenTask extends AsyncTask<String, Integer, Boolean>{
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // pDialog = new ProgressDialog(LoginActivity.this);
           // pDialog.setMessage("Please Wait while retrieving your Access Token...");
           // pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params){
            try {
               // Log.d(TAG, "requestToken=>" +bkpRequestToken);
//                Log.d(TAG, "Params=>" +params[0]);
                AccessToken accessToken = twitter.getOAuthAccessToken(params[0]);
                if (accessToken != null) {
                    session.saveSession(accessToken, twitter, params[0]);
                    String username = session.getUsername();
                    Log.d(TAG, "Username=>" + username);
                    return true;
                } else {
                    session.logout();
                    return false;
                }
            }catch(TwitterException e) {
                e.printStackTrace();
                Log.e("Twitter Exception" , "=>"+ e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);
            mAccessTokenTask = null;
//            pDialog.dismiss();
            if(result) start();
            else Toast.makeText(LoginActivity.this, "Please Login again", Toast.LENGTH_LONG).show();
        }


        protected void OnCancelled(){
            super.onCancelled();
            mAccessTokenTask = null;
            pDialog.dismiss();
        }

    }

    private void start(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public class AuthenticationTask extends AsyncTask<Void, Void, Boolean>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog=new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Please Wait...");
            dialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(Constants.CONSUMER_KEY);
            builder.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
            Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();
            try {
                LoginActivity.this.requestToken = twitter.getOAuthRequestToken(Constants.TWITTER_CALLBACK_URL);
                if (requestToken!=null) {
                    return true;
                }
            } catch (TwitterException e) {
                e.printStackTrace();
                Log.e(TAG, "TwitterException=>" + e.getMessage());
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {

            super.onPostExecute(result);
            mAuthTask=null;
            dialog.cancel();
            if(!result) Toast.makeText(LoginActivity.this, "Retry",Toast.LENGTH_SHORT).show();
            else {
               System.out.println("Before getAuth" + requestToken.getAuthenticationURL().toString());
               Intent i = new Intent(LoginActivity.this, TwitterWebViewActivity.class);
               i.putExtra("url", requestToken.getAuthenticationURL().toString());
                startActivityForResult(i, 1);
            }
        }
        @Override
        protected void onCancelled() {

            super.onCancelled();
            mAuthTask=null;
            dialog.cancel();
        }
    }

 }
