package com.example.zwh.scs.Wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.zwh.scs.R;
import com.example.zwh.scs.Wallet.Alipay.PayActivity;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        findViewById(R.id.rl_deposite).setOnClickListener(this);
        findViewById(R.id.rl_withdraw).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_deposite:
                Intent intent1 = new Intent(WalletActivity.this, PayActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_withdraw:
                Intent intent2 = new Intent(WalletActivity.this, WithdrawActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }

    }
}
