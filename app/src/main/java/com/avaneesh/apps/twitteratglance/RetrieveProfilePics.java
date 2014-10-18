package com.avaneesh.apps.twitteratglance;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.GridView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Avaneesh on 10/10/2014.
 */
public class RetrieveProfilePics extends AsyncTask<String, Void, Boolean> {
    Activity activity;
    ListView list;
    ImageCache cache;
    String key;

    RetrieveProfilePics(Activity context, ListView listview){
        activity = context;
        list = listview;
        cache = ImageCache.getInstance(context);
    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... urls) {
       if(urls[0] != null) {
           System.out.println(urls[0]);
           key = urls[1];
           cache.addImageToCache(urls[1], getImageBitmap(urls[0]));
       }
        return true;

    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);

            bis.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(true);
        if(list != null) {
            ((TweetListItem) list.getAdapter()).notifyUpdate();
        }
        else{
            Drawable d = new BitmapDrawable(activity.getResources(),ImageCache.getInstance(activity).findImage(key));
           activity.getActionBar().setIcon(d);
        }

    }


}
