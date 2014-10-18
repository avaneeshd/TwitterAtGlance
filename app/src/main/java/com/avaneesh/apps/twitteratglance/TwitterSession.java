package com.avaneesh.apps.twitteratglance;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

/**
 * Created by Avaneesh on 05/10/2014.
 */
public class TwitterSession {

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private AccessToken accessToken;
    private Twitter twitter;
    private User user;
    private static TwitterSession session = null;
    ResponseList<Status> tweets;

    private TwitterSession(Context context){
            mContext = context;
            mSharedPreferences = mContext.getSharedPreferences("MyPref", 0);
            TwitterFactory factory = new TwitterFactory();
            twitter = factory.getInstance();
            twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
            getAccessToken();
     }

    static TwitterSession getInstance(Context context){
        if(session == null){
            session = new TwitterSession(context);
        }
        return session;
    }

    public void loadTweets(){
       LoadTweets lt= new LoadTweets();
       lt.execute((Void)null);
    }

    public ResponseList<Status> getTweets(){
        if(tweets == null){
            loadTweets();
        }
        return tweets;
    }

    public AccessToken getAccessToken(){
        System.out.println("AccessToken");

        Log.d("Access token key",mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN,""));
        Log.d("Access token secret",mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET,""));
        accessToken = new AccessToken(mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN,""), mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET,""));
        System.out.println("Access Token " + accessToken.toString());
        if(accessToken != null){
          twitter.setOAuthAccessToken(accessToken);
           RetrieveUser ru = new RetrieveUser();
           ru.execute((Void) null);
        }
        return null;
    }

    public User getUser(){
        if(user!= null) return user;
        return null;
    }

    public Twitter getTwitterInstance(){
      return twitter;
    }

    public void saveSession(AccessToken accessToken , Twitter twitter, String verifier){
        try {
            long userID = accessToken.getUserId();
            User user = twitter.showUser(userID);
            String username = user.getName();

            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putString(Constants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(Constants.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, true);
            e.putString(Constants.TWITTER_USER_NAME, username);
            System.out.println(verifier);
            e.putString(Constants.URL_TWITTER_OAUTH_VERIFIER, verifier);

            e.commit();
        }
        catch(TwitterException e) {
            e.printStackTrace();
        }
    }

    public boolean isTwitterLoggedInAlready(){
        return mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false);
    }

    public String getDefaultAccessToken(){
        return mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN, "");
    }

    public String getDefaultSecret(){
        return mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET, "");
    }

    public String getUsername(){
        return mSharedPreferences.getString(Constants.TWITTER_USER_NAME, "");
    }

    public void logout(){
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.remove(Constants.PREF_KEY_OAUTH_SECRET);
        e.remove(Constants.PREF_KEY_OAUTH_TOKEN);
        e.remove(Constants.PREF_KEY_TWITTER_LOGIN);
        e.commit();
    }

    class RetrieveUser extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                user = twitter.showUser(accessToken.getUserId());

            }catch(TwitterException e){
                e.printStackTrace();
            }
            if(user != null) return true;
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(!result) return;
            else{
                System.out.print("Access token created!!");
            }

        }
        @Override
        protected void onCancelled() {
            super.onCancelled();

        }

    }

    class LoadTweets extends AsyncTask<Void , Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                tweets = getTwitterInstance().getHomeTimeline(new Paging(1, 200));
            }catch (TwitterException e){e.printStackTrace();}
            if(tweets == null) return false;
            else return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }
}