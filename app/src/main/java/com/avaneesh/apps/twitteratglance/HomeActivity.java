package com.avaneesh.apps.twitteratglance;

import android.app.Activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import java.util.ArrayList;
import twitter4j.ResponseList;
import twitter4j.Status;


public class HomeActivity extends Fragment {
    ResponseList<Status> tweets;
    ArrayList<Tweet> tweetsData;
    TwitterSession session;
    ListView topTweets;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        final View view = inflater.inflate(R.layout.activity_home, container, false);
        topTweets = (ListView)view.findViewById(R.id.topTweets);
        topTweets.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //int lastInScreen = totalItemCount - visibleItemCount;
                if(visibleItemCount >0 && firstVisibleItem >= 1){
                    view.findViewById(R.id.txtTopTweets).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.separator).setVisibility(View.VISIBLE);
                }
                else {
                    view.findViewById(R.id.txtTopTweets).setVisibility(View.GONE);
                    view.findViewById(R.id.separator).setVisibility(View.GONE);
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

        Tweets t = new Tweets();
        t.execute((Void) null);
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

    class Tweets extends AsyncTask<Void , Void, Boolean> {

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params){
           if(tweets == null){
               tweets = session.getTweets();
           }
           if(tweets != null) return true;
           else return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);

            int RTCount[] = new int[tweets.size()];
            int favCount[] = new int[tweets.size()];
            int top_11[] = new int[11];
            if(result){
                int i = 0;
                for(twitter4j.Status status : tweets){
                    RTCount[i] = status.getRetweetCount();
                    favCount[i] = status.getFavoriteCount();
                    i++;
                }
                for(i=0; i<11; i++) {
                    int maxIndex = i;
                    int maxValue = RTCount[i];
                    for (int j = i + 1; j < tweets.size(); j++) {
                        if (RTCount[j] > maxValue){
                            maxIndex = j;
                            maxValue = RTCount[j];
                        }
                    }
                    int temp = RTCount[maxIndex];
                    RTCount[maxIndex] = RTCount[i];
                    RTCount[i] = temp;
                    top_11[i]= maxIndex;
                }

                boolean isTOD;
                for(i=0; i<11 ;i++){
                    twitter4j.Status status= tweets.get(top_11[i]);
                    if(i ==0 ) isTOD = true;
                    else isTOD = false;
                    tweetsData.add(new Tweet(status, isTOD));
                    String handle = status.getUser().getScreenName();
                    if(ImageCache.getInstance(getActivity()).findImage(handle) == null) {
                        RetrieveProfilePics rpp = new RetrieveProfilePics(getActivity(), topTweets);
                        String urls[]={status.getUser().getBiggerProfileImageURL(), handle};
                        rpp.execute(urls);
                    }
                }
                TweetListItem adapter = new TweetListItem(getActivity(), tweetsData);
                // Assign adapter to ListView
                topTweets.setAdapter(adapter);
            }
            dialog.dismiss();

        }
    }
}
