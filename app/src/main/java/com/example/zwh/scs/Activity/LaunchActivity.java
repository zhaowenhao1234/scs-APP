package com.example.zwh.scs.Activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.flags |= flagTranslucentStatus | flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }

        imageView = findViewById(R.id.launch_bg);
        if (Math.random() > 0 && Math.random() < 0.2) {
            imageView.setImageResource(R.drawable.start);
        } else if (Math.random() > 0.2 && Math.random() < 0.4) {
            imageView.setImageResource(R.drawable.start1);
        } else if (Math.random() > 0.4 && Math.random() < 0.6) {
            imageView.setImageResource(R.drawable.start3);
        } else if (Math.random() > 0.6 && Math.random() < 0.9) {
            imageView.setImageResource(R.drawable.start4);
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
                    Thread.sleep(2000);
                    IntentUtils.SetIntent(LaunchActivity.this, MainActivity.class);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
