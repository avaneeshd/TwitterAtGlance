package com.avaneesh.apps.twitteratglance;

import android.graphics.Bitmap;

import java.util.Date;

import twitter4j.Status;

/**
 * Created by Avaneesh on 10/10/2014.
 */
public class Tweet {
    long ID;
    String name;
    String handle;
    String tweetData;
    String profilePicUrl;
    String timeAgo;
    int RTCount;
    int favCount;
    boolean isTweetOfTheDay;


    Tweet(Status tweet, boolean isTOD){
        this.ID = tweet.getId();
        this.name = tweet.getUser().getName();
        this.tweetData = tweet.getText();
        this.handle = tweet.getUser().getScreenName();
        this.timeAgo = getTimeAgo(tweet.getCreatedAt());
        this.isTweetOfTheDay = isTOD;
        this.profilePicUrl = tweet.getUser().getMiniProfileImageURL();
        this.RTCount = tweet.getRetweetCount();
        this.favCount = tweet.getFavoriteCount();
    }

    String getTimeAgo(Date created){
        Date date = new Date();
        Long minutesAgo = date.getTime() - created.getTime();
        String timeAgo = "";
        if(minutesAgo > 0){
            minutesAgo = minutesAgo/(60*1000);
            if(minutesAgo >= 60){
                minutesAgo = minutesAgo/60;
                timeAgo =  Integer.toString(minutesAgo.intValue())+"h";
            }
            else {
                if (minutesAgo > 1)
                    timeAgo = Integer.toString(minutesAgo.intValue()) + "m";
                else timeAgo = "Just now";
            }
        }
        return timeAgo;
    }
}
