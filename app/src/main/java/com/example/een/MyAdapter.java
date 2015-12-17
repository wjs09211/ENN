package com.example.een;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by 睡睡 on 2015/12/17.
 */
public class MyAdapter extends BaseAdapter {
    Context context;
    List<RecordItem> rowItems;
    public MyAdapter(Context context, List<RecordItem> items) {
        this.context = context;
        this.rowItems = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtDate;
        TextView txtLocation;
        TextView txtState;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            holder.txtLocation = (TextView) convertView.findViewById(R.id.txt_location);
            holder.txtState = (TextView) convertView.findViewById(R.id.txt_state);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        RecordItem rowItem = (RecordItem) getItem(position);

        holder.txtDate.setText("時間 : " + rowItem.getDate());
        holder.txtLocation.setText("地點 : " + rowItem.getLocation());
        if( rowItem.getSolve() ) {
            holder.txtState.setText("已解決");
            holder.txtState.setTextColor(Color.BLACK);
        }
        else {
            holder.txtState.setText("未解決");
            holder.txtState.setTextColor(Color.RED);
        }
        return convertView;
    }
    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }
}
