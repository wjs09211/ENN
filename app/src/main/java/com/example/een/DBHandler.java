package com.example.een;

/**
 * Created by 睡睡 on 2015/12/3.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//處理資料庫
public class DBHandler {

    //網站

    public final static String sleepUrl = "http://sleep-sleepsleep.rhcloud.com/test/query_db.php?";

    public DBHandler(){}
    //傳入SQL語法 空白鍵用%20 EX:queryStr=Select%20*%20From%20data;
    public static String query( final String strQuery){
        //要回傳的 string
        final String[] JSONStr = {""};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //網站+參數
                    String urlString = sleepUrl + strQuery;
                    String response = "";   //要回傳的string
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (MSIE 9.0; Windows NT 6.1; Trident/5.0)"); //User-Agent 應該是IE
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    Log.e("responseCode",responseCode+"");
                    if(responseCode == 200) { //如果成功
                        InputStream is = connection.getInputStream();
                        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        String temp = "";
                        while ((temp = streamReader.readLine()) != null) {
                            response += temp;
                        }
                        JSONStr[0] = response;
                    }
                    else
                        JSONStr[0] = "";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return JSONStr[0];
    }
}
