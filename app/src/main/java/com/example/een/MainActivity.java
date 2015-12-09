package com.example.een;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private String str_User = "";
    private TextView txt_main_user;
    private Button btn_pictureReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        btn_pictureReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, PictureReportActivity.class);
                startActivity(i);
            }
        });
    }
    void initComponent(){
        //取得使用者帳號　從SharedPreferences中取得
        SharedPreferences settings;
        settings = getSharedPreferences("User",0);
        str_User = settings.getString("account", "");
        //初始化
        txt_main_user = (TextView)findViewById(R.id.txt_main_user);
        txt_main_user.setText(str_User + "，您好");
        btn_pictureReport = (Button)findViewById(R.id.btn_main_pictureReport);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            // Show home screen when pressing "back" button,
            //  so that this app won't be closed accidentally
            Intent intentHome = new Intent(Intent.ACTION_MAIN);
            intentHome.addCategory(Intent.CATEGORY_HOME);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentHome);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
