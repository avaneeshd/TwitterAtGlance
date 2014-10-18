package com.avaneesh.apps.twitteratglance;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.GridView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Avaneesh on 08/10/2014.
 */
public class RetrieveSiteData extends AsyncTask<String, Void, Boolean> {
    NewsItem news;
    Activity activity;
    GridView newsGrid;
    RetrieveSiteData(Activity context){
        activity = context;
    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        try {
            Document document = Jsoup.connect(urls[0]).get();
            // Get the html document title
            news = new NewsItem("","","","","");
            news.headlines = document.title();
            news.previewText = getMetaTag(document, "description");
            news.imageURL = getMetaTag(document, "og:image");
            if(news.imageURL != null){
                System.out.println(news.imageURL);
                news.setBitmap(getImageBitmap(news.imageURL));
            }
            news.name = urls[1];
            news.handle = urls[2];
        }
        catch(IOException e){
            e.printStackTrace();
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

    String getMetaTag(Document document, String attr) {
        Elements elements = document.select("meta[name=" + attr + "]");
        for (Element element : elements) {
            final String s = element.attr("content");
            if (s != null) return s;
        }
        elements = document.select("meta[property=" + attr + "]");
        for (Element element : elements) {
            final String s = element.attr("content");
            if (s != null) return s;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(true);
        if(result){
            newsGrid = (GridView)activity.findViewById(R.id.NewsGridView);
            ((NewsListItem)newsGrid.getAdapter()).addItem(news);
        }
    }
}