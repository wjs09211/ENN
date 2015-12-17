package com.example.een;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

///
///登入功能
///
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btn_login = (Button)findViewById(R.id.btn_login); //登入按鈕
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        Button btnSignUp = (Button)findViewById(R.id.btn_SignUp); //註冊按鈕
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to SignUpActivity
                Intent i = new Intent();
                i.setClass(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
        //判斷是否開啟GPS定位
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Intent intent = new Intent(this, GPSService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "請開啟定位服務，開啟後重啟程式", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }
        //開啟定位服務

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //關閉定位GPS Service
        Intent intent = new Intent(this, GPSService.class);
        stopService(intent);
    }
    private void Login()
    {
        EditText edit_account = (EditText)findViewById(R.id.edit_account);
        EditText edit_password = (EditText)findViewById(R.id.edit_password);
        String account = edit_account.getText().toString();
        String password = edit_password.getText().toString();

        //比對帳號密碼是否正確
        //String sql = "queryStr=SELECT * FROM android_account_info WHERE account='" + account + "' AND password='" + password + "';";
        String sql = "queryStr=SELECT%20*%20FROM%20android_account_info%20WHERE%20account=%27" + account + "%27%20AND%20password=%27" + password + "%27;";
        //sql = URLEncoder.encode(sql);
        //執行sql指令 然後得到response
        String response = DBHandler.query(sql);
        Log.e("response",response);
        if( response.equals("") || response.equals("\uFEFFnull")){
            // login fail
            Toast.makeText(LoginActivity.this, getString(R.string.txt_Login_fail), Toast.LENGTH_SHORT).show();
        }
        else{
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonData = jsonArray.getJSONObject(0);
                Log.e( "jsonData",  jsonData.getString("account") + "  " + jsonData.get("password"));
                //暫存使用者帳號
                SharedPreferences settings;
                settings = getSharedPreferences("User",0);
                settings.edit().putString("account", jsonData.getString("account")).apply();
                //Go to MainActivity
                Intent i = new Intent();
                i.setClass(LoginActivity.this, MainActivity.class);
                startActivity(i);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
