package com.example.een;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PictureReportActivity extends AppCompatActivity {

    private Uri outputFileUri;
    private String str_User = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_report);

        initComponent();

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
            new Thread(new Runnable() {
                public void run() {
                    uploadFile();
                }
            }).start();
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
            dataOS.writeBytes("location" + CRLF);    //location = location

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
}
