package com.example.een;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        Button btnSignUp = (Button)findViewById(R.id.btn_SignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to SignUpActivity
                Intent i = new Intent();
                i.setClass(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }
    /*@Override
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
    }*/
    private void Login()
    {
        EditText edit_account = (EditText)findViewById(R.id.edit_account);
        EditText edit_password = (EditText)findViewById(R.id.edit_password);
        String account = edit_account.getText().toString();
        String password = edit_password.getText().toString();
        Log.e("account",account);
        String sql = "queryStr=SELECT%20*%20FROM%20android_account_info%20WHERE%20account=%27" + account + "%27%20AND%20password=%27" + password + "%27;";
        //String sql = "queryStr=SELECT * FROM android_account_info WHERE account='" + account + "' AND password='" + password + "';";
        //sql = URLEncoder.encode(sql);
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
                SharedPreferences settings;
                settings = getSharedPreferences("User",0);
                settings.edit().putString("account", jsonData.getString("account")).apply();
                Intent i = new Intent();
                i.setClass(LoginActivity.this, MainActivity.class);
                startActivity(i);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
