package com.example.zwh.scs.Activity;

import android.annotation.SuppressLint;
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

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    String str1 = "";//获取用户名
    String str3 = "";//获取用户密码（输入密码）
    String str5 = "";//确认密码
    private TextView responseText;
    private EditText editText_accountr;//注意实例化的位置
    private EditText editText_passwordr;//
    private EditText editText_password_again;
    private Button register;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            responseText.setText(msg.obj.toString());
            return true;
        }
    });

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusbarUtil.setTransparentWindow(this, false);
        register = findViewById(R.id.register_btn);
        editText_accountr = findViewById(R.id.accountr);//须再onCreate里实例化
        editText_passwordr = findViewById(R.id.passwordr);//须再onCreate里实例化
        editText_password_again = findViewById(R.id.password_again);
        responseText = findViewById(R.id.response_text);

        register.setOnClickListener(this);
    }

    private void sendRequestWithOkHttp() {

        str1 = editText_accountr.getText().toString();//getText()要放到监听里面
        str3 = editText_passwordr.getText().toString();
        str5 = editText_password_again.getText().toString();

        String str2 = "";
        String str4 = "";
        str2 = MD5Utils.encode(str1);
        str4 = MD5Utils.encode(str3);
        Toast.makeText(RegisterActivity.this, str2, Toast.LENGTH_SHORT).show();
        //Toast.makeText(RegisterActivity.this, str4,Toast.LENGTH_SHORT).show();

        final String finalStr = str2;
        final String finalStr1 = str4;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody responseBody = new FormBody.Builder().add("userName", str1).add("password", finalStr1).build();
                    Request request = new Request.Builder().url("http://129.204.119.172:8080/scs/register").post(responseBody).build();
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

    @Override
    public void onClick(View v) {
        Log.d("12324", "onClick: ");
        str3 = editText_passwordr.getText().toString();
        str5 = editText_password_again.getText().toString();
        //判断输入密码和确认密码是否一致
        if (v.getId() == R.id.register_btn) {
            if (str3.equals(str5)) {
                sendRequestWithOkHttp();
            } else {
                Toast.makeText(RegisterActivity.this, "两次输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showResponse(final String responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(responseData);
                Log.d("123", "run: responseData" + responseData);
            }
        });
    }

}
