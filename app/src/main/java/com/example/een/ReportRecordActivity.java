package com.example.een;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;

public class ReportRecordActivity extends AppCompatActivity {

    private String str_User = "";
    private ArrayList<RecordItem> recordList = new ArrayList<RecordItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_record);
        setTitle(R.string.txt_ReportRecord_title);
        str_User = getUserAccount();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getRecord();
            }
        }).start();

    }
    private String getUserAccount()
    {
        Bundle bundle = getIntent().getExtras();
        return bundle.getString("account");
    }
    private void getRecord()
    {
        //SELECT * FROM android_accident_record WHERE account = 'str_User';
        String sql = "queryStr=SELECT%20*%20FROM%20android_accident_record%20WHERE%20account%20=%20%27"+ str_User +"%27";
        String response = DBHandler.query(sql);
        Log.e("response", response);
        if( response.equals("") || response.equals("\uFEFFnull")){
            showToast(getString(R.string.txt_NoRecord));
        }
        else{
            try {
                JSONArray jsonArray = new JSONArray(response);
                for( int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    RecordItem item = new RecordItem();
                    item.setDate(jsonData.getString("date"));
                    item.setUrl(jsonData.getString("image_path"));
                    if( jsonData.getString("visible").equals(("1")) ){
                        item.setSolve(false);
                    } else{
                        item.setSolve(true);
                    }
                    String s = jsonData.getString("map_location");
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                        item.setLocation(s);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    recordList.add(item);
                }
                Message msgg = new Message();//component要交給Handler處理
                msgg.what = 1;
                mHandler.sendMessage(msgg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msgg) {
            switch(msgg.what){
                case 1:
                    Log.e("updataListView","updataListView");
                    updataListView();//更新listView
                    break;
            }
            super.handleMessage(msgg);
        }
    };
    public void updataListView() {
        ListView listView;
        listView = (ListView) findViewById(R.id.listView);
        MyAdapter adapter = new MyAdapter(this, recordList, listView );
        listView.setAdapter(adapter);
        System.gc();
    }

    public void showToast(final String toast)   //把它寫成function  有些時候可以避免Bug 例如在thread裡使用他
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ReportRecordActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
