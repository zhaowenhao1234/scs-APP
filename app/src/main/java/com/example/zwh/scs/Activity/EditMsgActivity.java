package com.example.zwh.scs.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.UserInfoUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditMsgActivity extends BaseActivity {

    private EditText editText;
    private String para;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_edit_msg);
        Bundle bundle = this.getIntent().getExtras();
        name = (String) bundle.get("name");
        editText = findViewById(R.id.et_editMsg);
        judge(name);
        setTile(name);

    }

    public void judge(String name) {
        if (name.equals("上传头像")) {
            para = "avator";
        } else if (name.equals("设置用户名")) {
            para = "nickName";
        } else if (name.equals("设置性别")) {
            para = "sex";
        } else if (name.equals("设置年级")) {
            para = "grade";
        } else if (name.equals("设置学号")) {
            para = "schoolNum";
        }
    }


    public void setTile(String name) {
        editText.setHint(name);
        initToolbarView(name, true, R.mipmap.im_titlebar_back_p);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_scanner).setVisible(false);
        menu.findItem(R.id.menu_newMeg).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.menu_newMeg:
                if (!name.equals("发布信息")) {
                    updateMyMessage(para);
                } else {
                    sendMsgWithOkhttp();
                }
                Intent data = new Intent();
                setResult(2, data);
                finish();
                break;

            default:
                break;
        }
        return true;
    }

    private void sendMsgWithOkhttp() {
        Date currentTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(currentTime);
        String contents = editText.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("driverId", "123").add("time", dateString).add("contents", contents).add("pic", "unknown").build();
                    Request request = new Request.Builder().url("http://129.204.119.172:8080/found/insert").post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    Log.d("MSG", "send response" + response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.d("MSG", "SEND2");
    }

    public void updateMyMessage(String para) {
        String contents = editText.getText().toString();
        if (para.equals("sex")) {
            if (contents.equals("男")) {
                contents = "1";
            } else if (contents.equals("女")) {
                contents = "0";
            }
        }
        Log.d("hello5", "updateMyMessage: " + contents);
        String finalContents = contents;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("id", UserInfoUtil.getCurrentInfoUserID(getApplicationContext())).add(para, finalContents).build();

                    Request request = new Request.Builder().url("http://129.204.119.172:8080/user/update").post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    Log.d("MSG", "send response" + response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
