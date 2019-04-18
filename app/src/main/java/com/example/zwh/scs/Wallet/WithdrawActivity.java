package com.example.zwh.scs.Wallet;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zwh.scs.Activity.BaseActivity;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.IntentUtils;
import com.example.zwh.scs.Wallet.Alipay.OrderInfoUtil2_0;

public class WithdrawActivity extends BaseActivity {

    private String BizContent;
    private String Account = "0";
    private String Amount = "0";


    EditText withdrawAccount;
    EditText withdrawAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_withdraw);
        initToolbarView("提现", true, R.mipmap.im_titlebar_back_p);
        withdrawAccount = findViewById(R.id.ed_withdraw_account);
        withdrawAmount = findViewById(R.id.ed_withdraw_amount);

        findViewById(R.id.btn_withdrawToAlipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String withAmount = withdrawAmount.getText().toString();
                String withAccount = withdrawAccount.getText().toString();
                Log.d("YZP", withAccount + withAmount);
                if (withAmount.equals("")) {
                    Toast.makeText(WithdrawActivity.this, "提现失败，请输入大于0的提现金额", Toast.LENGTH_SHORT).show();
                } else if (withAccount.equals("")) {
                    Toast.makeText(WithdrawActivity.this, "提现失败，请输入正确的支付宝账号", Toast.LENGTH_SHORT).show();
                } else {
                    if (Integer.parseInt(withAmount) > WalletActivity.walletBalance) {
                        Toast.makeText(WithdrawActivity.this, "提现失败，余额不足", Toast.LENGTH_SHORT).show();
                    } else {
                        WalletActivity.walletBalance -= Integer.parseInt(withAmount);
                        Toast.makeText(WithdrawActivity.this, "提现成功", Toast.LENGTH_SHORT).show();
                        IntentUtils.SetIntent(WithdrawActivity.this, WalletActivity.class);
                    }
                }
            }
        });


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
        menu.findItem(R.id.menu_newMeg).setVisible(false);
        return true;
    }

    private String getBizContent() {
        return "{" + "\"out_biz_no\":\"" + OrderInfoUtil2_0.getOutTradeNo() + "\"," + "\"payee_type\":\"ALIPAY_LOGONID\"," + "\"payee_account\":\"" + Account + "\"," + "\"amount\":\"" + Amount + "\"," + "\"payer_show_name\":\"余额提现\"," + "\"payee_real_name\":\"SCS Inc.\"," + "  }";
    }
}
