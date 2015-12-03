package com.example.een;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
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
    private void Login()
    {
        EditText editAccount = (EditText)findViewById(R.id.edit_account);
        EditText editPassword = (EditText)findViewById(R.id.edit_password);
        String account = editAccount.getText().toString();
        String password = editPassword.getText().toString();
        Log.e("account",account);
        String sql = "queryStr=SELECT%20*%20FROM%20android_account_info%20WHERE%20account=%27" + account + "%27%20AND%20password=%27" + password + "%27;";
        //String sql = "queryStr=SELECT * FROM android_account_info WHERE account='" + account + "' AND password='" + password + "';";
        //sql = URLEncoder.encode(sql);
        Log.e("sql", sql);
        String response = DBHandler.query(sql);
        Log.e("response",response);
        if( response.equals("") || response.equals("\uFEFFnull")){
            // login fail
            Log.e("login","fail");
        }
        else{
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonData = jsonArray.getJSONObject(0);
                Log.e( "jsonData",  jsonData.getString("account") + "  " + jsonData.get("password"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
