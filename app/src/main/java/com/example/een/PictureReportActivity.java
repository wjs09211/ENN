package com.example.een;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class PictureReportActivity extends AppCompatActivity {

    private Uri outputFileUri;
    private String str_User = "";
    private String str_address = "";
    private String str_longitude = "";
    private String str_latitude = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_report);

        //初始化元件
        initComponent();
        //利用intent去開啟android本身的照相介面
        Intent intent =  new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File tmpFile = new File(
                Environment.getExternalStorageDirectory(),
                "image.jpg");
        outputFileUri = Uri.fromFile(tmpFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 0);
    }
    //初始化元件
    private void initComponent(){
        SharedPreferences settings;
        settings = getSharedPreferences("User",0);
        str_User = settings.getString("account", "");
    }
    //取得GPS資料
    private void getGPSAddress()
    {
        SharedPreferences settings;
        settings = getSharedPreferences("GPS", 0);
        str_longitude = settings.getString("longitude", "");
        str_latitude = settings.getString("latitude", "");
        try {
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            List<Address> lstAddress = null;
            Double longitude = Double.parseDouble(str_longitude);	//取得經度
            Double latitude = Double.parseDouble(str_latitude);	//取得緯度
            //取得地址資訊
            lstAddress = gc.getFromLocation(latitude, longitude, 1);
            String returnAddress = lstAddress.get(0).getAddressLine(0);
            Log.e("returnAddress",returnAddress);
            str_address = returnAddress;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //重照相機回來
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            getGPSAddress();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    uploadFile();
                    showToast("上傳成功");
                    Message msgg = new Message();//component要交給Handler處理
                    msgg.what = 1;
                    handler.sendMessage(msgg);
                }
            });
            t.start();
        }
        finish();
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msgg) {
            switch(msgg.what){
                case 1:
                    finish();//更新listView
                    break;
            }
            super.handleMessage(msgg);
        }
    };
    //上傳
    private void uploadFile()
    {
        final String BOUNDARY 	= "==================================";
        final String HYPHENS 	= "--";
        final String CRLF 		= "\r\n";
        try {
            File sourceFile = new File(outputFileUri.getPath());
            FileInputStream fileInputStream = new FileInputStream(sourceFile);

            URL url = new URL("http://sleep-sleepsleep.rhcloud.com/test/uploadImage.php");
            HttpURLConnection conn 	= (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());

            //第一筆資料--帳號
            dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF);        // 寫--==================================
            dataOS.writeBytes("Content-Disposition: form-data; name=\"account\"" + CRLF);    // 寫(Disposition)
            dataOS.writeBytes(CRLF);
            dataOS.writeBytes(str_User + CRLF);    //account = str_User
            //第二筆資料--地址
            dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF);        // 寫--==================================
            dataOS.writeBytes("Content-Disposition: form-data; name=\"location\"" + CRLF);    // 寫(Disposition)
            dataOS.writeBytes(CRLF);
            dataOS.writeBytes(URLEncoder.encode(str_address, "UTF-8") + CRLF);    //location = location
            //第三筆資料--經度
            dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF);        // 寫--==================================
            dataOS.writeBytes("Content-Disposition: form-data; name=\"longitude\"" + CRLF);    // 寫(Disposition)
            dataOS.writeBytes(CRLF);
            dataOS.writeBytes(str_longitude + CRLF);    //longitude = str_longitude
            //第四筆資料--緯度
            dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF);        // 寫--==================================
            dataOS.writeBytes("Content-Disposition: form-data; name=\"latitude\"" + CRLF);    // 寫(Disposition)
            dataOS.writeBytes(CRLF);
            dataOS.writeBytes(str_latitude + CRLF);    //latitude = str_latitude

            //圖片資料
            dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF);        // 寫--==================================
            dataOS.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"image\"" + CRLF);    // 寫(Disposition)
            dataOS.writeBytes(CRLF);
            int iBytesAvailable = fileInputStream.available();
            byte[] byteData = new byte[iBytesAvailable];
            int iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
            while (iBytesRead > 0) {
                dataOS.write(byteData, 0, iBytesAvailable);	// 開始寫內容
                iBytesAvailable = fileInputStream.available();
                iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
            }
            dataOS.writeBytes(CRLF);

            dataOS.writeBytes(HYPHENS + BOUNDARY + HYPHENS + CRLF);	// (結束)寫--==================================--

            int serverResponseCode = conn.getResponseCode();        //ResponseCode
            String serverResponseMessage = conn.getResponseMessage();
            Log.e("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);
            dataOS.flush();
            dataOS.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showToast(final String toast)   //把它寫成function  有些時候可以避免Bug 例如在thread裡使用他
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(PictureReportActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
