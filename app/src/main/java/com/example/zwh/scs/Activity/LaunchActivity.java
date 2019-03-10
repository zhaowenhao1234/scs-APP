package com.example.zwh.scs.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.IntentUtils;

/**
 * created at 2019/3/7 21:33 by wenhaoz
 */
public class LaunchActivity extends AppCompatActivity {
    private ImageView launch_bg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
//        launch_bg = (ImageView) findViewById(R.id.launch_bg);
//        Glide.with(this)
//                .asBitmap()
//                .load(R.drawable.start)
//                .into(launch_bg);
        //准备启动主活动
        waitForMainActivity();
    }

    /***
     *等待三秒启动MainActivtiy
     *@return void
     *@author wenhaoz
     *created at 2019/3/7 21:59
     */
    private void waitForMainActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    IntentUtils.SetIntent(LaunchActivity.this, MainActivity.class);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
