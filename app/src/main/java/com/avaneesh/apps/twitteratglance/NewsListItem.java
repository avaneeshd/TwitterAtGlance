package com.avaneesh.apps.twitteratglance;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Avaneesh on 08/10/2014.
 */
public class NewsListItem extends ArrayAdapter<NewsItem> {
    private final Activity context;
    ArrayList<NewsItem> newsItems;

    private static LayoutInflater inflater=null;
    public NewsListItem(Activity context, ArrayList<NewsItem> items) {
        super(context, R.layout.tweet_item, items);
        this.context = context;
        newsItems = items;
    }

    static class ViewHolder{
        TextView txtName;
        TextView txtHandle;
        TextView txtPreview;
        ImageView imageView;
        TextView txtHeadline;
    }

    void addItem(NewsItem newsItem){
        newsItems.add(newsItem);
        notifyDataSetChanged();
    }

    void notifyUpdate(){
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        inflater = context.getLayoutInflater();
        View rowView = null;
        ViewHolder holder = new ViewHolder();
        if(rowView == null) {
                rowView = inflater.inflate(R.layout.news_item, null, true);
                holder.txtName = (TextView) rowView.findViewById(R.id.txtNewsname);
                holder.txtHandle = (TextView) rowView.findViewById(R.id.txtNewshandle);
                holder.txtPreview = (TextView) rowView.findViewById(R.id.txtPreview);
                holder.imageView = (ImageView) rowView.findViewById(R.id.newsImage);
                holder.txtHeadline = (TextView) rowView.findViewById(R.id.txtHeadlines);
                rowView.setTag(holder);

            holder.txtName.setText(newsItems.get(position).name);
            holder.txtHandle.setText("@"+newsItems.get(position).handle);
            if(newsItems.get(position).image != null)
                holder.imageView.setImageBitmap(newsItems.get(position).image);
            else
                holder.imageView.setImageResource(R.drawable.bg);
            holder.txtPreview.setText(newsItems.get(position).previewText);
            holder.txtHeadline.setText(newsItems.get(position).headlines);
        }
        return rowView;
    }
}
