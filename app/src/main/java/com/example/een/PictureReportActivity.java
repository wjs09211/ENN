package com.example.een;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
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
    private LocationManager locationMgr;
    private String provider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_report);

        initComponent();

        //region取得目前位置
        //判斷是否開啟定位
        if (initLocationProvider()) {
            whereAmI();
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }
        //endregion

        Intent intent =  new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);//利用intent去開啟android本身的照相介面
        File tmpFile = new File(
                Environment.getExternalStorageDirectory(),
                "image.jpg");
        outputFileUri = Uri.fromFile(tmpFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 0);
    }

    void initComponent(){
        SharedPreferences settings;
        settings = getSharedPreferences("User",0);
        str_User = settings.getString("account", "");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bmp = BitmapFactory.decodeFile(outputFileUri.getPath()); //利用BitmapFactory去取得剛剛拍照的圖像
            Log.e("path", outputFileUri.getPath());
            ImageView ivTest = (ImageView)findViewById(R.id.imageView);
            ivTest.setImageBitmap(bmp);
            Thread t = new Thread(new Runnable() {
                public void run() {
                    uploadFile();
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        }
    }
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
            conn.setRequestMethod("POST");                        // method一定要是POST
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
            //dataOS.writeBytes("accoun1t=tttttttttfuck&password1=tttttttttfuck&email1=tttttttttfuc&phone1=tttttttttfuc");

            dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF);        // 寫--==================================
            dataOS.writeBytes("Content-Disposition: form-data; name=\"account\"" + CRLF);    // 寫(Disposition)
            dataOS.writeBytes(CRLF);
            dataOS.writeBytes(str_User + CRLF);    //account = str_User

            dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF);        // 寫--==================================
            dataOS.writeBytes("Content-Disposition: form-data; name=\"location\"" + CRLF);    // 寫(Disposition)
            dataOS.writeBytes(CRLF);
            dataOS.writeBytes(URLEncoder.encode(str_address, "UTF-8") + CRLF);    //location = location

            dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF);        // 寫--==================================
            dataOS.writeBytes("Content-Disposition: form-data; name=\"longitude\"" + CRLF);    // 寫(Disposition)
            dataOS.writeBytes(CRLF);
            dataOS.writeBytes(str_longitude + CRLF);    //longitude = str_longitude

            dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF);        // 寫--==================================
            dataOS.writeBytes("Content-Disposition: form-data; name=\"latitude\"" + CRLF);    // 寫(Disposition)
            dataOS.writeBytes(CRLF);
            dataOS.writeBytes(str_latitude + CRLF);    //latitude = str_latitude


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

            int serverResponseCode = conn.getResponseCode();
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

    private boolean initLocationProvider() {
        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //2.選擇使用GPS提供器
        if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            return true;
        }
        //3.選擇使用網路提供器
        if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
            return true;
        }
        return false;
    }
    private void whereAmI() {
        //取得上次已知的位置
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationMgr.getLastKnownLocation(provider);
        getLocation(location);
    }
    private void getLocation(Location location) {	//將定位資訊顯示在畫面中
        if(location != null) {

            Double longitude = location.getLongitude();	//取得經度
            Double latitude = location.getLatitude();	//取得緯度
            str_longitude = ""+longitude;
            str_latitude = ""+latitude;
            Geocoder gc = new Geocoder(this, Locale.TRADITIONAL_CHINESE);
            List<Address> lstAddress = null;
            try {
                lstAddress = gc.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String returnAddress = lstAddress.get(0).getAddressLine(0);
            str_address = returnAddress;
            Log.e("str_address",str_address);
        }
        else {
            Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        }
    }

}
