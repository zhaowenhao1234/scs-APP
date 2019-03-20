package com.example.zwh.scs.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.zwh.scs.R;

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login_out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        login_out = findViewById(R.id.login_out);
        login_out.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_out:
                MainActivity.isLogin = false;
                finish();
                break;
        }
    }
}
