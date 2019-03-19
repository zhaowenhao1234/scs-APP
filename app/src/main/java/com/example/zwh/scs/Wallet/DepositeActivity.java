package com.example.zwh.scs.Wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.zwh.scs.R;
import com.example.zwh.scs.Wallet.Alipay.OrderInfoUtil2_0;


public class DepositeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposite);
        findViewById(R.id.yuan10).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yuan10:
                OrderInfoUtil2_0.payAmount = 10;
                break;
            default:
                break;
        }
    }
}
