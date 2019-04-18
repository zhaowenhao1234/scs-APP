package com.example.zwh.scs.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.zwh.scs.Bean.PassengerUser;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.NetWorkUtil;
import com.example.zwh.scs.Util.UserInfoUtil;
import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login_out;
    private Button personal_back;
    private Button head_img;
    private Button nick_name;
    private Button sex;
    private Button grade;
    private Button school_id;

    private TextView nick_name_text;
    private TextView sex_text;
    private TextView grade_text;
    private TextView school_id_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initView();
        loadMyMessage();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadMyMessage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMyMessage();
    }

    private void loadMyMessage() {
        if (NetWorkUtil.isNetworkConnected(getApplicationContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder().add("id", UserInfoUtil.getCurrentInfoUserID(getApplicationContext())).build();
                        Request request = new Request.Builder().url("http://129.204.119.172:8080/user/getById").post(requestBody).build();
                        Response response = client.newCall(request).execute();
                        String json = response.body().string();
                        jsonToJsonObject(json);
                        Log.d("hello2", "send response" + json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else {
            Bundle bundle = UserInfoUtil.getCurrentMessage(getApplicationContext());
            String nickName = bundle.getString("nickName");
            int sex = bundle.getInt("sex");
            String grade = bundle.getString("grade");
            String schoolId = bundle.getString("schoolId");
            updateMyMessage(nickName, grade, schoolId, sex);
        }

    }

    /***
     *解析状态码
     *@return java.lang.String
     *@author wenhaoz
     *created at 2019/3/19 23:18
     */
    public void jsonToJsonObject(String json) {
        Gson gson = new Gson();
        PassengerUser user = new PassengerUser();
        user = gson.fromJson(json, PassengerUser.class);
        String nickName = user.getUser().getNickName();
        String grade = user.getUser().getGrade();
        String schoolId = user.getUser().getSchoolNum();

        int sex = user.getUser().getSex();

        Bundle bundle = new Bundle();
        bundle.putString("nickName", nickName);
        bundle.putString("grade", grade);
        bundle.putString("schoolId", schoolId);
        bundle.putInt("sex", sex);

        updateMyMessage(nickName, grade, schoolId, sex);

        UserInfoUtil.saveCurrentMessage(this, bundle);

    }

    private void updateMyMessage(String nickName, String grade, String schoolId, int sex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nick_name_text.setText(nickName);

                if (sex == 1) {
                    sex_text.setText("男");
                } else if (sex == 0) {
                    sex_text.setText("女");
                }

                grade_text.setText(grade);
                school_id_text.setText(schoolId);

                nick_name_text.setTextColor(Color.BLACK);
                sex_text.setTextColor(Color.BLACK);
                grade_text.setTextColor(Color.BLACK);
                school_id_text.setTextColor(Color.BLACK);
            }
        });
    }


    private void initView() {
        login_out = findViewById(R.id.login_out);
        personal_back = findViewById(R.id.personal_back);
        head_img = findViewById(R.id.head_img);
        nick_name = findViewById(R.id.nick_name);
        sex = findViewById(R.id.sex);
        grade = findViewById(R.id.grade);
        school_id = findViewById(R.id.school_id);
        nick_name_text = findViewById(R.id.nick_name_text);
        sex_text = findViewById(R.id.sex_text);
        grade_text = findViewById(R.id.grade_text);
        school_id_text = findViewById(R.id.school_id_text);
        head_img.setOnClickListener(this);
        nick_name.setOnClickListener(this);
        sex.setOnClickListener(this);
        grade.setOnClickListener(this);
        school_id.setOnClickListener(this);
        login_out.setOnClickListener(this);
        personal_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_out:
                //更新当前状态
                UserInfoUtil.setCurrentInfoUserState(getApplicationContext(), false);

                finish();
                break;
            case R.id.personal_back:
                finish();
                break;
            case R.id.head_img:
                jumpIntent("上传头像");
                break;
            case R.id.nick_name:
                jumpIntent("设置用户名");
                break;
            case R.id.sex:
                jumpIntent("设置性别");
                break;
            case R.id.grade:
                jumpIntent("设置年级");
                break;
            case R.id.school_id:
                jumpIntent("设置学号");
                break;
        }
    }

    public void jumpIntent(String name) {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        Intent intent = new Intent(this, EditMsgActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
