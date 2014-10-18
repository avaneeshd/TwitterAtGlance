package com.avaneesh.apps.twitteratglance;

/**
 * Created by Avaneesh on 07/10/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import twitter4j.TwitterException;


public class TweetListItem extends ArrayAdapter<Tweet> {
   private final Activity context;
   ArrayList<Tweet> tweets;
    private static LayoutInflater inflater=null;
    public TweetListItem(Activity context, ArrayList<Tweet> twts) {
        super(context, R.layout.tweet_item, twts);
        this.context = context;
        tweets = twts;
    }

    void notifyUpdate(){
       notifyDataSetChanged();
    }

    static class ViewHolder{
         TextView txtName;
         TextView txtHandle;
         TextView txtTweet;
         ImageView imageView;
         TextView time;
         TextView RTCount;
         TextView favCount;
         ImageButton RT;
         ImageButton Fav;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

       inflater = context.getLayoutInflater();
        View rowView = null;
        ViewHolder holder = new ViewHolder();
        if(rowView == null) {
            int type = getItemViewType(position);
              if (tweets.get(position).isTweetOfTheDay) {
                // inflate the row for Tweet of the day
                rowView = inflater.inflate(R.layout.layout_tweetoftheday, null, true);
                holder.txtName = (TextView) rowView.findViewById(R.id.TODName);
                holder.txtHandle = (TextView) rowView.findViewById(R.id.TODHandle);
                holder.txtTweet = (TextView) rowView.findViewById(R.id.tweetoftheday);
                holder.imageView = (ImageView) rowView.findViewById(R.id.TODpropic);
                holder.time = (TextView) rowView.findViewById(R.id.TODTime);
                rowView.setTag(holder);
            }
            else {
                // inflate the ordinary row
                rowView = inflater.inflate(R.layout.tweet_item, null, true);
                holder.txtName = (TextView) rowView.findViewById(R.id.name);
                holder.txtHandle = (TextView) rowView.findViewById(R.id.handle);
                holder.txtTweet = (TextView) rowView.findViewById(R.id.tweet);
                holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
                holder.time = (TextView) rowView.findViewById(R.id.time);
                holder.favCount = (TextView) rowView.findViewById(R.id.favCount);
                holder.RTCount = (TextView) rowView.findViewById(R.id.RTCount);
                rowView.setTag(holder);
            }



            holder.txtName.setText(tweets.get(position).name);
            holder.txtHandle.setText("@"+tweets.get(position).handle);
            holder.imageView.setImageBitmap(ImageCache.getInstance(context).findImage(tweets.get(position).handle));
            holder.txtTweet.setText(tweets.get(position).tweetData);
            holder.time.setText(tweets.get(position).timeAgo);

            if(holder.RTCount!=null && holder.favCount!=null) {
                holder.favCount.setText(Integer.toString(tweets.get(position).favCount));
                holder.RTCount.setText(Integer.toString(tweets.get(position).RTCount));
            }
        }
        return rowView;
    }
}