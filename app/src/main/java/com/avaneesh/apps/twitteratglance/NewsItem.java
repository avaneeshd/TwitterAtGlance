package com.avaneesh.apps.twitteratglance;

import android.graphics.Bitmap;

/**
 * Created by Avaneesh on 09/10/2014.
 */
public class NewsItem {
    String headlines;
    String previewText;
    String name;
    String handle;
    String imageURL;
    Bitmap image;

    NewsItem(String headlines, String previewText, String name, String handle, String imageURL){
        this.headlines = headlines;
        this.previewText = previewText;
        this.name= name;
        this.handle = handle;
        this.imageURL = imageURL;
    }

    void setBitmap(Bitmap b){
        this.image = b;
    }
}
