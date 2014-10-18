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
import android.widget.GridView;

import java.util.ArrayList;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.URLEntity;


public class NewsActivity extends Fragment {
    TwitterSession session;
    GridView newsGrid;
    ResponseList<Status> tweets;
    String title;
    String headlines[] = new String[5];
    ArrayList<NewsItem> newsItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.activity_news, container, false);
        newsItems = new ArrayList<NewsItem>();
        newsGrid = (GridView)view.findViewById(R.id.NewsGridView);
        NewsListItem adapter = new NewsListItem(getActivity(), newsItems);
        newsGrid.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = TwitterSession.getInstance(getActivity().getApplicationContext());
        News n = new News();
        n.execute((Void)null);

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

    class News extends AsyncTask<Void , Void, Boolean> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(tweets == null){
                tweets = session.getTweets();
            }
            int RTCount[] = new int[tweets.size()];
            int favCount[] = new int[tweets.size()];
            int top_11[] = new int[11];
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

            int j =0;
            for (i=0; i<11; i++){
                twitter4j.Status status= tweets.get(i);
                URLEntity urls[] = status.getURLEntities();
                try {
                    if(urls != null && urls[0].getExpandedURL()!=null || !urls[0].getExpandedURL().equals("")) {
                        RetrieveSiteData rs = new RetrieveSiteData(getActivity());
                        String data[] = { urls[0].getExpandedURL(), status.getUser().getName(),status.getUser().getScreenName() };
                        rs.execute(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (tweets != null) return true;
            else return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);
        }
    }
}

