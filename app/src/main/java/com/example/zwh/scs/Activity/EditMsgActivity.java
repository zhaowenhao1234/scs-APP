package com.example.zwh.scs.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.zwh.scs.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditMsgActivity extends BaseActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_edit_msg);
        initToolbarView("发布留言", true, R.mipmap.im_titlebar_back_p);
        editText = findViewById(R.id.et_editMsg);
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
                sendMsgWithOkhttp();
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
                    RequestBody requestBody = new FormBody.Builder()
                            .add("driverId", "123")
                            .add("time", dateString)
                            .add("contents", contents)
                            .add("pic", "unknown")
                            .build();
                    Request request = new Request.Builder()
                            .url("http://129.204.119.172:8080/found/insert")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    Log.d("MSG", "send response" + response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.d("MSG", "SEND2");
    }
}
