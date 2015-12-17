package com.example.een;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by 睡睡 on 2015/12/17.
 */
public class RecordItem {
    private String url = "";
    private String location = "";
    private String date = "";
    private boolean solve = false;
    public RecordItem(){}
    public String getUrl(){ return url;}
    public String getLocation(){ return location; }
    public String getDate(){ return  date; }
    public boolean getSolve(){ return solve; }
    public void setUrl( String str){ url = str; }
    public void setLocation( String str){ location = str;}
    public void setDate( String str ){ date = str;}
    public void setSolve( boolean b ){ solve = b; }

}
