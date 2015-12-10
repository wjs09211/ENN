package com.example.een;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private Button btn_OK;
    private EditText edit_account;
    private EditText edit_password;
    private TextView txt_account_error;
    private TextView txt_password_error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initComponent();
        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    private void initComponent(){
        btn_OK = (Button)findViewById(R.id.btn_Sign_OK);
        edit_account = (EditText)findViewById(R.id.edit_SignUp_account);
        edit_password = (EditText)findViewById(R.id.edit_SignUp_password);
        txt_account_error = (TextView)findViewById(R.id.txt_SignUp_account_error);
        txt_password_error = (TextView)findViewById(R.id.txt_SignUp_password_error);
        txt_account_error.setVisibility(View.GONE);
        txt_password_error.setVisibility(View.GONE);
    }
    private void register(){
        String str_account = edit_account.getText().toString();
        String str_password = edit_password.getText().toString();
        //檢查帳號格式
        if(!checkInput(str_account)){
            txt_account_error.setVisibility(View.VISIBLE);
            return;
        }
        //檢查密碼格式
        if(!checkInput(str_password)){
            txt_password_error.setVisibility(View.VISIBLE);
            return;
        }
        //是否有重複帳號
        if ( checkDuplicate( str_account ) ){
            //重複
            Toast.makeText(SignUpActivity.this, getString(R.string.txt_SignUp_duplicate), Toast.LENGTH_SHORT).show();
        }
        else{
            //成功註冊
            //queryStr=INSERT INTO android_account_info (account, password)VALUES ('str_account', 'str_account');
            DBHandler.query("queryStr=INSERT%20INTO%20android_account_info%20(account,%20password)%20" +
                    "VALUES%20(%27" + str_account + "%27,%20%27" + str_account + "%27);");
            Toast.makeText(SignUpActivity.this, getString(R.string.txt_SignUp_success), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    //判斷英文或數字
    private boolean checkInput( String str){
        if( str.length() < 8 ){
            return false;
        }
        else {
            Pattern pattern = Pattern.compile("^[a-zA-Z\\d]+$");
            return pattern.matcher(str).matches();
        }
    }
    //判斷重複
    private boolean checkDuplicate( String str){
        String sql = "queryStr=SELECT%20*%20FROM%20android_account_info%20WHERE%20account=%27" + str + "%27;";
        String response = DBHandler.query(sql);
        Log.e("response",response);
        return response.contains("account");
    }
}
