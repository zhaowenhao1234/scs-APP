package com.example.zwh.scs.Wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zwh.scs.Activity.BaseActivity;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Wallet.Alipay.PayActivity;

public class WalletActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_wallet);
        initToolbarView("我的钱包", true, R.mipmap.im_titlebar_back_p);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_scanner).setVisible(false);
        return true;
    }
}
