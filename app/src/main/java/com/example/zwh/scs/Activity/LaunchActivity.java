package com.example.zwh.scs.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.IntentUtils;

/**
 * created at 2019/3/7 21:33 by wenhaoz
 */
public class LaunchActivity extends AppCompatActivity {

    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        imageView = findViewById(R.id.launch_bg);
        if(Math.random() <0.5){
            imageView.setImageResource(R.drawable.start);
        }else{
            imageView.setImageResource(R.drawable.start1);
        }

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
