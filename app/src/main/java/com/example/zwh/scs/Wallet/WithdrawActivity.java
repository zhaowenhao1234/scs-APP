package com.example.zwh.scs.Wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zwh.scs.R;
import com.example.zwh.scs.Wallet.Alipay.OrderInfoUtil2_0;

public class WithdrawActivity extends AppCompatActivity {

    private String BizContent;
    private String Account = "0";
    private String Amount = "0";


    EditText withdrawAccount;
    EditText withdrawAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        withdrawAccount = findViewById(R.id.ed_withdraw_account);
        withdrawAmount = findViewById(R.id.ed_withdraw_amount);

        findViewById(R.id.btn_withdrawToAlipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String withAmount = withdrawAmount.getText().toString();
                String withAccount = withdrawAccount.getText().toString();
                Log.d("YZP", withAccount + withAmount);
                if (withAmount.equals("")) {
                    Toast.makeText(WithdrawActivity.this, "提现失败，请输入大于0的提现金额", Toast.LENGTH_LONG).show();
                } else if (withAccount.equals("")) {
                    Toast.makeText(WithdrawActivity.this, "提现失败，请输入正确的支付宝账号", Toast.LENGTH_LONG).show();
                } else {
                    Account = withAccount;
                    Amount = withAmount;
                }
            }
        });


    }

    private String getBizContent() {
        return "{" + "\"out_biz_no\":\"" + OrderInfoUtil2_0.getOutTradeNo() + "\"," + "\"payee_type\":\"ALIPAY_LOGONID\"," + "\"payee_account\":\"" + Account + "\"," + "\"amount\":\"" + Amount + "\"," + "\"payer_show_name\":\"余额提现\"," + "\"payee_real_name\":\"SCS Inc.\"," + "  }";
    }
}
