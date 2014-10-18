package com.avaneesh.apps.twitteratglance;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by Avaneesh on 10/10/2014.
 */
public class ImageCache {
    static ImageCache ic = null;
    static private LruCache<String,Bitmap> imageMap;

    private ImageCache(Context context){
        final int memClass = ((ActivityManager)context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;
       imageMap = new LruCache<String, Bitmap>(cacheSize);
    }

    static ImageCache getInstance(Context context){
        if(ic == null){
            ic= new ImageCache(context);
        }
        return ic;
    }

    Bitmap findImage(String key){
        if(imageMap != null && key != "")
           return imageMap.get(key);
        else return null;
    }

    void addImageToCache(String key, Bitmap b){
        if(imageMap.get(key) == null)
            imageMap.put(key, b);
    }
}


