package com.example.zwh.scs.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private String str1 = "";//获取用户名
    private String str2 = "";//获取用户密码
    private String str3 = "";//获取用户电话号码
    private String str4 = "";//获取验证码
  

    private EditText editText_accountr;//注意实例化的位置
    private EditText editText_passwordr;//
    private Button securityCode;
    private EditText phoneNumber;
    private Button register;
    private Button jumpToMainr;
    private EditText validcheck;
    private RadioGroup radio_group;
    int flag = 0;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            String code = jsonToJsonObject(msg.obj.toString());

            switch (msg.arg1) {
                case 1:
                    if (code.equals("0")) {
                        Toast.makeText(RegisterActivity.this, "验证码获取成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "验证码获取失败"+code, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Log.d("123456789", "handleMessage: "+code);
                    if (code.equals("0")) {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }


            return true;
        }
    });


    public String jsonToJsonObject(String json) {
        String code = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            code = jsonObject.optString("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("123456789", "jsonToJsonObject: "+code);
        return code;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    /***
     *布局初始化
     *@return void
     *@author wenhaoz
     *created at 2019/3/20 18:27
     */
    private void initView() {
        StatusbarUtil.setTransparentWindow(this, false);
        register = findViewById(R.id.register_btn);
        editText_accountr = findViewById(R.id.account_register);//须再onCreate里实例化
        editText_passwordr = findViewById(R.id.password_register);//须再onCreate里实例化
        jumpToMainr = findViewById(R.id.jumpToMainr);
        securityCode = findViewById(R.id.securityCode);
        radio_group = findViewById(R.id.radio_group);
        phoneNumber = findViewById(R.id.phoneNumber);
        validcheck = findViewById(R.id.validcheck);
        securityCode.setText("获取验证码");
        register.setOnClickListener(this);
        jumpToMainr.setOnClickListener(this);
        securityCode.setOnClickListener(this);
        radio_group.setOnCheckedChangeListener(this);
    }

    private void sendRequestWithOkHttp_validCheck() {

        String phoneStr = phoneNumber.getText().toString();//获取用户电话号码;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody responseBody = new FormBody.Builder().add("phoneNumber", phoneStr).build();
                    Request request = new Request.Builder().url("http://129.204.119.172:8080/getMessage").post(responseBody).build();
                    Call call = client.newCall(request);
                    Response response = call.execute();
                    Message message = handler.obtainMessage();
                    message.arg1 = 1;
                    message.obj = response.body().string();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private void sendRequestWithOkHttp() {

        str1 = editText_accountr.getText().toString();//getText()要放到监听里面//获取用户名
        str2 = editText_passwordr.getText().toString();//获取用户密码
        str3 = phoneNumber.getText().toString();//获取用户电话号码
        str4 = validcheck.getText().toString();//获取验证码

        String str5 = MD5Utils.encode(str2);

        final String finalStr = str5;


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (flag == 1) {
                        String url = "http://129.204.119.172:8080/driver/register";
                        requestHttp(url, finalStr);

                    } else if (flag == 2) {
                        String url = "http://129.204.119.172:8080/user/register";
                        requestHttp(url, finalStr);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /***
     *联网注册请求
     *@return void
     *@author wenhaoz
     *created at 2019/3/20 18:50
     */
    private void requestHttp(String url, String finalStr) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody responseBody = null;
        if(flag == 1){
            responseBody = new FormBody.Builder()
                    .add("name", str1)
                    .add("password", finalStr)
                    .add("phoneNumber", str3)
                    .add("validateNum", str4)
                    .build();
        }else if(flag == 2){
            responseBody = new FormBody.Builder()
                    .add("nickName", str1)
                    .add("password", finalStr)
                    .add("phoneNumber", str3)
                    .add("validateNum", str4)
                    .build();
        }
        
        Request request = new Request.Builder().url(url).post(responseBody).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        Message message = handler.obtainMessage();
        message.obj = response.body().string();
        message.arg1 = 2;
        handler.sendMessage(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.securityCode:
                sendRequestWithOkHttp_validCheck();
                break;
            case R.id.register_btn:
                sendRequestWithOkHttp();
                break;
            case R.id.jumpToMainr: {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }

        }


    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        flag = 0;
        switch (checkedId) {
            case R.id.driverP:
                flag = 1;
                break;
            case R.id.userP:
                flag = 2;
                break;
        }
    }
}