package com.example.zwh.scs.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.MD5Utils;
import com.example.zwh.scs.Util.StatusbarUtil;
import com.example.zwh.scs.Util.UserInfoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {


    private static final int DRIVER_OPTION = 1;
    private static final int USER_OPTION = 2;
    private String str_username = "";//获取用户名
    private String str_password = "";//用户密码
    private String code;//服务器返回的值
    private EditText accountEditl;
    private EditText passwordEditl;
    private Button login;

    private Button jumpToRegister;
    private Button back;
    private RadioGroup radio_group_login;
    private int flag;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            code = jsonToJsonObject(msg.obj.toString());
            if (code.equals("0")) {
                MainActivity.isLogin = true;
                //保存当前信息
                UserInfoUtil.saveCurrentInfo(getApplicationContext(),msg.arg1,str_username);

                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusbarUtil.setTransparentWindow(this, false);

        initView();


    }

    /***
    *初始化布局
    *@param
    *@return void
    *@author wenhaoz
    *created at 2019/3/19 23:13
    */
    private void initView() {
        MainActivity.isLogin = false;
        accountEditl = findViewById(R.id.accountl);
        passwordEditl = findViewById(R.id.passwordl);
        login = findViewById(R.id.login_btn);
        back = findViewById(R.id.jumpToMainl);
        radio_group_login = findViewById(R.id.radio_group_login);
        jumpToRegister = findViewById(R.id.jumpToRegister);


        login.setOnClickListener(this);
        jumpToRegister.setOnClickListener(this);
        back.setOnClickListener(this);
        radio_group_login.setOnCheckedChangeListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_btn:
                sendRequestWithOkHttp();
                break;
            case R.id.jumpToRegister:
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                break;
            case R.id.jumpToMainl:
                Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent2);
                break;
        }

    }

    private void sendRequestWithOkHttp() {
        str_username = accountEditl.getText().toString();//getText()要放到监听里面
        str_password = passwordEditl.getText().toString();

        //密码进行加密
        String str_password_MD5 = "";
        str_password_MD5 = MD5Utils.encode(str_password);
        String finalStr_password_MD = str_password_MD5;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("123456", "run: "+flag);
                try {
                    if (flag == DRIVER_OPTION) {
                        requestNet(finalStr_password_MD, "http://129.204.119.172:8080/driver/login",DRIVER_OPTION);
                    } else if (flag == USER_OPTION) {
                        requestNet(finalStr_password_MD, "http://129.204.119.172:8080/user/login",USER_OPTION);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /***
    *网络请求
    *@return void
    *@author wenhaoz
    *created at 2019/3/19 23:17
    */
    private void requestNet(String finalStr_password_MD, String s, int option) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody responseBody = null;
        if(flag == DRIVER_OPTION){
            responseBody = new FormBody.Builder().add("name", str_username).add("password", finalStr_password_MD).build();
        }else if(flag == USER_OPTION){
            responseBody = new FormBody.Builder().add("nick_name", str_username).add("password", finalStr_password_MD).build();

        }
        Request request = new Request.Builder().url(s).post(responseBody).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        Message message = handler.obtainMessage();
        message.obj = response.body().string();
        message.arg1 = option;
        handler.sendMessage(message);
    }

    /***
    *解析状态码
    *@return java.lang.String
    *@author wenhaoz
    *created at 2019/3/19 23:18
    */
    public String jsonToJsonObject(String json) {
        String code = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            code = jsonObject.optString("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        flag = 0;

        switch (checkedId) {
            case R.id.driverL:
                flag = 1;
                break;
            case R.id.userL:
                flag = 2;
                break;
        }
    }
}