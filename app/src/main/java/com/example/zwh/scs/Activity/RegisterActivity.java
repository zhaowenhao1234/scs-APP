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
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.MD5Utils;
import com.example.zwh.scs.Util.StatusbarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    String str1 = "";//获取用户名
    String str2 = "";//获取用户密码
    String str3 = "";//获取用户电话号码
    String str4 = "";//获取验证码
    String code = "";//返回服务器的值
   // private TextView responseTextr;
    private EditText editText_accountr;//注意实例化的位置
    private EditText editText_passwordr;//
    private Button securityCode;
    private EditText phoneNumber;
    private Button register;
    private Button jumpToMainr;
    private EditText validcheck;
    private RadioGroup radio_group;
    int flag=0;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            code = jsonToJsonObject(msg.obj.toString());
            if (code.equals("0")) {
                Toast.makeText(RegisterActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
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
        return code;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusbarUtil.setTransparentWindow(this, false);
        register = findViewById(R.id.register_btn);
        editText_accountr = findViewById(R.id.account_register);//须再onCreate里实例化
        editText_passwordr = findViewById(R.id.password_register);//须再onCreate里实例化
      //  responseTextr = findViewById(R.id.response_text);
        jumpToMainr = findViewById(R.id.jumpToMainr);
        securityCode = findViewById(R.id.securityCode);
        radio_group=findViewById(R.id.radio_group);
        phoneNumber = findViewById(R.id.phoneNumber);
        validcheck = findViewById(R.id.validcheck);
        securityCode.setText("获取"+'\n'+"验证码");
        register.setOnClickListener(this);
        jumpToMainr.setOnClickListener(this);
        securityCode.setOnClickListener(this);
        radio_group.setOnCheckedChangeListener(this);
    }

    private void sendRequestWithOkHttp_validCheck() {

        String str="";
        str = phoneNumber.getText().toString();//获取用户电话号码

        String finalStr = str;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody responseBody = new FormBody.Builder().add("phoneNumber", finalStr).build();
                    Request request = new Request.Builder().url("http://129.204.119.172:8080/getMessage").post(responseBody).build();
                    Call call = client.newCall(request);
                    Response response = call.execute();
                    Message message = handler.obtainMessage();
                    message.obj = response.body().string();
                    handler.sendMessage(message);
                    Log.d("电话号码", finalStr);
                    //String responseData = response.body().string();//这一句代码在方法体里面只能用一次(包括打印输出的使用)
                    // showResponse(responseData);
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

        String str5 = "";
        str5 = MD5Utils.encode(str2);
        Toast.makeText(RegisterActivity.this, str2, Toast.LENGTH_SHORT).show();
        //Toast.makeText(RegisterActivity.this, str4,Toast.LENGTH_SHORT).show();

        final String finalStr = str5;


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(flag==1){
                        OkHttpClient client = new OkHttpClient();
                        RequestBody responseBody = new FormBody.Builder().add("nickName", str1)
                                .add("password", finalStr)
                                .add("phoneNumber",str3)
                                .add("validateNum",str4).build();
                        Request request = new Request.Builder().url("http://129.204.119.172:8080/driver/register").post(responseBody).build();
                        Call call = client.newCall(request);
                        Response response = call.execute();
                        Message message = handler.obtainMessage();
                        message.obj = response.body().string();
                        handler.sendMessage(message);
                        //String responseData = response.body().string();//这一句代码在方法体里面只能用一次(包括打印输出的使用)
                        // showResponse(responseData);

                    }else if(flag==2){
                        OkHttpClient client = new OkHttpClient();
                        RequestBody responseBody = new FormBody.Builder().add("nickName", str1)
                                .add("password", finalStr)
                                .add("phoneNumber",str3)
                                .add("validateNum",str4).build();
                        Request request = new Request.Builder().url("http://129.204.119.172:8080/user/register").post(responseBody).build();
                        Call call = client.newCall(request);
                        Response response = call.execute();
                        Message message = handler.obtainMessage();
                        message.obj = response.body().string();
                        handler.sendMessage(message);
                        //String responseData = response.body().string();//这一句代码在方法体里面只能用一次(包括打印输出的使用)
                        // showResponse(responseData);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.securityCode:
            sendRequestWithOkHttp_validCheck();break;
            case R.id.register_btn:
                sendRequestWithOkHttp();break;
                case R.id.jumpToMainr:{
                    Intent intent =new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);break;
                }

        }


    }

    private void showResponse(final String responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // responseTextr.setText(responseData);
                Log.d("123", "run: responseData" + responseData);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        flag=0;
        switch (checkedId){
            case R.id.driverP:
                flag=1;
            case R.id.userP:
                flag=2;
        }
    }
}
