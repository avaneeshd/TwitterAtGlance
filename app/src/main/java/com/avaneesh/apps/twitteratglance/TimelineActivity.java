package com.avaneesh.apps.twitteratglance;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;


public class TimelineActivity extends Fragment {

    TwitterSession session;
    ResponseList<Status> tweets;
    ArrayList<Tweet> tweetsData;
    ListView listView;
    int page = 1;
    boolean loadmore = false , refresh = false;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.activity_timeline, container, false);


        listView = (ListView)view.findViewById(R.id.listTweets);
        mSwipeRefreshLayout =(SwipeRefreshLayout)view.findViewById(R.id.timelineSwipeRefresh);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.blue, R.color.actionBarcolor, R.color.white, R.color.gray);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if(lastInScreen == totalItemCount && totalItemCount!=0 && !loadmore){
                    page++;
                    Tweets timeline = new Tweets();
                    timeline.execute((Void) null);
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("Timeline Activity", "onRefresh called from SwipeRefreshLayout");
                if(!refresh) {
                    refresh = true;
                    initiateRefresh();
                }
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = TwitterSession.getInstance(getActivity().getApplicationContext());
        tweetsData = new ArrayList<Tweet>();


        // ((TextView)findViewById(R.id.username)).setText("Hello " + user.getName());
        Tweets timeline = new Tweets();
        timeline.execute((Void) null);
    }

    public void initiateRefresh(){
        RefreshTweets refreshTimeline = new RefreshTweets();
        refreshTimeline.execute((Void) null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class RefreshTweets extends  AsyncTask<Void , Void , Boolean>{
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadmore = true;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Refreshing...");
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params){
            try {
                 tweets = session.getTwitterInstance().getHomeTimeline(new Paging(1, 40));
                if(tweets != null) return true;
                else return false;
            }catch(TwitterException e) {
                e.printStackTrace();
                Log.e("Twitter Exception" , "=>"+ e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){

            super.onPostExecute(result);
            if(result) {
                tweetsData.clear();
                pDialog.dismiss();
                for (int i = 0; i < tweets.size(); i++) {
                    twitter4j.Status tweet = tweets.get(i);
                    String handle = tweet.getUser().getScreenName();
                    if(ImageCache.getInstance(getActivity()).findImage(handle) == null) {
                        RetrieveProfilePics rpp = new RetrieveProfilePics(getActivity(), listView);
                        String urls[]={tweet.getUser().getBiggerProfileImageURL(), handle};
                        rpp.execute(urls);
                    }
                    tweetsData.add(new Tweet(tweet, false));
                }
                TweetListItem adapter = new TweetListItem(getActivity(), tweetsData);

                listView.setAdapter(adapter);
                refresh = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
    class Tweets extends AsyncTask<Void , Void, Boolean>{
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
             loadmore = true;
             pDialog = new ProgressDialog(getActivity());
             pDialog.setMessage("Loading Timeline...");
             pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params){
            try {
                if(tweets == null){
                    tweets = session.getTwitterInstance().getHomeTimeline(new Paging(page, 40));
                }
                else {
                    ResponseList<twitter4j.Status> temp = session.getTwitterInstance().getHomeTimeline(new Paging(page, 40));
                    if (temp.size() > 0) {
                        for (twitter4j.Status status : temp) {
                            tweets.add(status);
                        }
                    }
                }
               if(tweets != null) return true;
                else return false;
            }catch(TwitterException e) {
                e.printStackTrace();
                Log.e("Twitter Exception" , "=>"+ e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){

            super.onPostExecute(result);
            if(result) {
                pDialog.dismiss();
                for (int i = 0; i < tweets.size(); i++) {
                    twitter4j.Status tweet = tweets.get(i);
                    String handle = tweet.getUser().getScreenName();
                    if(ImageCache.getInstance(getActivity()).findImage(handle) == null) {
                        RetrieveProfilePics rpp = new RetrieveProfilePics(getActivity(), listView);
                        String urls[]={tweet.getUser().getBiggerProfileImageURL(), handle};
                        rpp.execute(urls);
                    }
                    tweetsData.add(new Tweet(tweet, false));
                }
                TweetListItem adapter = new TweetListItem(getActivity(), tweetsData);
                // Assign adapter to ListView
                listView.setAdapter(adapter);
                loadmore = false;
            }
        }
    }

}
