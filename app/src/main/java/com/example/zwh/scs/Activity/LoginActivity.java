package com.example.zwh.scs.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText accountEditl;
    private EditText passwordEditl;
    private Button login;
    private TextView responseText;

    String str_username = "";//获取用户名
    String str_password = "";//用户密码
    String Code;             //服务器返回的值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusbarUtil.setTransparentWindow(this, false);
        accountEditl = findViewById(R.id.accountl);
        passwordEditl = findViewById(R.id.passwordl);
        login = findViewById(R.id.login_btn);
        login.setOnClickListener(this);
        responseText = findViewById(R.id.response_text_l);
    }

    @Override
    public void onClick(View v) {
        Log.d("12324", "onClick: ");

        if (v.getId() == R.id.login_btn) {
            sendRequestWithOkHttp();
        }
    }

    private void sendRequestWithOkHttp() {
        str_username = accountEditl.getText().toString();//getText()要放到监听里面
        str_password = passwordEditl.getText().toString();

        String str_username_MD5 = "";
        String str_password_MD5 = "";
        str_username_MD5 = MD5Utils.encode(str_username);
        str_password_MD5 = MD5Utils.encode(str_password);
        //Toast.makeText(LoginActivity.this, str_username,Toast.LENGTH_SHORT).show();
        //Toast.makeText(LoginActivity.this, str4,Toast.LENGTH_SHORT).show();

        final String finalStr2 = str_username_MD5;
        final String finalStr3 = str_password_MD5;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody responseBody = new FormBody.Builder().add("userName", str_username).add("password", finalStr3).build();
                    Request request = new Request.Builder().url("http://129.204.119.172:8080/scs/login").post(responseBody).build();
                    Call call = client.newCall(request);
                    Response response = call.execute();
                    Message message = handler.obtainMessage();
                    message.obj = response.body().string();
                    handler.sendMessage(message);
                    //String responseData = response.body().string();//这一句代码在方法体里面只能用一次(包括打印输出的使用)
                    // showResponse(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            responseText.setText(msg.obj.toString());
            Code = jsonToJsonObject(msg.obj.toString());
            if (Code.equals("100")) {
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
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
}
