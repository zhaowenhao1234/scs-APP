package com.example.zwh.scs.Wallet.Alipay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.zwh.scs.Activity.BaseActivity;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.IntentUtils;
import com.example.zwh.scs.Wallet.WalletActivity;

import java.util.Map;

/**
 * 重要说明：
 * 本 Demo 只是为了方便直接向商户展示支付宝的整个支付流程，所以将加签过程直接放在客户端完成
 * （包括OrderInfoUtil2_0）。
 * 在真实 App 中，私钥（如 RSA_PRIVATE 等）数据严禁放在客户端，同时加签过程务必要放在服务端完成，
 * 否则可能造成商户私密数据泄露或被盗用，造成不必要的资金损失，面临各种安全风险。
 */

public class PayActivity extends BaseActivity {

    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2016092800615969";
    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    public static final String PID = "";
    /**
     * 用于支付宝账户登录授权业务的入参 target_id。
     */
    public static final String TARGET_ID = "";
    /**
     * pkcs8 格式的商户私钥。
     * 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * RSA2_PRIVATE。
     * 建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCmVSbh/38AGvOBcVCC8biFGpBM31X7PYE3dnHOwNEr5n42CnQTG5uIdXmxwjJXb9LogHs/wHhfrsEzfWQZ7L4MnUFbmd+0QgaKZ6wgMvSCQvBtEBv0MWWKuD5Ybko7ZWs/uDu8yXzgEXh0FpSso/PIIXG7Nrcu3VJ25pHjXB+7jssecHt6vo62AQwUJghyso+bknYtjBgKETFQbr0gMh3GRibDBejlxyOxu69uykG5ECSP3C9BV5CAQ1g41Pa/Z6Yx6LvYKryMCDZwFsb4QfdgEeuSQL0YJ4EVGYal84wMYjMnyp2ELfr913HYXUJCfk+/leU7cT3+bsPeeA3BsAZpAgMBAAECggEAWrKj+SfwgIYxsauwUzarfyj09nXx1IW9KBkWBb9uT5nnyF/621B6hvZH3X4fJT58qvskOach/Eq2XvVI2DoXh5lYQjKtoQU/u8i8OvKOEVme8FmQZJ9q1zGQDXhWUf0DdkNnj5Hu3d+o5uRJPbpI/CAAfC1hxdQ5W0xu0KMzSnm5Dd7UXlHnJJjSVlgbOFBDd/ydUMUD0BwDNMTp0VLQqCs7dsgrxcZ4ARtMmw/MmYFb4/0TsiFpJoj/Q/4voOdeFYRFJPIFhQwyRzo240VSvGBnjIEUglbDL+rClE6nmlCxXzNvY7b7ItNnxcNDqaCZusy4taaimjETyM/+2FzaYQKBgQDcjXjSEW2+3tT2H+SHSHR55HS1Tf692mkpGaVqlSnKlkJZeb1+dpxJQhTsTeWJsxPYzo37ZM0t9LY2PT2aVRSI6oFEAlTMtf75tSZWzHy6rRzqtGkP4Tsub3kU5vo0bL4QcstVY2NNfKnc3A4RoquRh40uTqyI2Ni0UksleS5H+wKBgQDBENMydT+Uep5A080n39SNM/4GAb/AY5RomjeJvpPbyoy+qHKFClgzYgukiYImRt/GTRNZLOiij1F9Wuolk7WFfUIGssS5+N4lRCmWwg/s2u+z7CpPJU5IKzqlN18nE2dN3JA6CT/Tbilv5e/f5eG75vPi3S9brmoBJkCudKpp6wKBgQDNeP+WtaYIrJC6u/usDVR2OuCACKnLNi/CmqIBKfZFRreJpGFl8BqqJWZYwDmYj71tvwGHs+FzbwhSf7tkjN8Ur2S+d22JSgTBnoKZWujZAW5vOqSmpq78E946GvX+4VAxAsFsS6u4BOw7VsfEpkgwzJg7DBCxbVR2qjRYNQ1pAQKBgAw3d1XDC5HmrGrnvByg0j9ZIeLZa3vOEU8JKyiBMbP/viY2XIDEpc4ijyALP3wSkghnSikjaVkX/o0TGqvkC+F1ip8H4uDtuYjcJlGO2BkhxXc6I3c8ohZ4/c4EkfXUCX5ozYuOmEZVuzOEdkhRsJYGSDp7yopfn/+Qnxkq8rmfAoGBAKdpKEISNlKXNlTDMhm5Jc4JTKkrWsqutChAc5D7r80rTVSgDdw5+MCMuICx5SoRuE6JCdbyXFg99L3RhdrPAHHdwCaQEAj/oq3Jld8P3alNN7knkX+C05GbTANih0NzE05pru9dXy3uAQ/7n4EgEk4WY8SHBDIH0TWJxdGdyIWO";
    public static final String RSA_PRIVATE = "";
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private EditText editText;
    private String amount;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked") PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        WalletActivity.walletBalance += Integer.parseInt(amount);
                        IntentUtils.SetIntent(PayActivity.this, WalletActivity.class);
                    } else {
                        Toast.makeText(PayActivity.this, "充值失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//run in sandbox
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_deposite);
        initToolbarView("充值", true, R.mipmap.im_titlebar_back_p);
        editText = findViewById(R.id.ed_chongzhiRMB);
        findViewById(R.id.cz_xyb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = editText.getText().toString();
                if (!amount.equals("")) {
                    OrderInfoUtil2_0.payAmount = Integer.parseInt(amount);
                    payV2();
                } else {
                    Toast.makeText(PayActivity.this, "请输入充值金额", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 获取权限使用的 RequestCode
     */
    private static final int PERMISSIONS_REQUEST_CODE = 1002;

    /**
     * 检查支付宝 SDK 所需的权限，并在必要的时候动态获取。
     * 在 targetSDK = 23 以上，READ_PHONE_STATE 和 WRITE_EXTERNAL_STORAGE 权限需要应用在运行时获取。
     * 如果接入支付宝 SDK 的应用 targetSdk 在 23 以下，可以省略这个步骤。
     */
    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);

        } else {
            showToast(this, getString(R.string.permission_already_granted));
        }
    }

    /**
     * 权限获取回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {

                // 用户取消了权限弹窗
                if (grantResults.length == 0) {
                    showToast(this, getString(R.string.permission_rejected));
                    return;
                }

                // 用户拒绝了某些权限
                for (int x : grantResults) {
                    if (x == PackageManager.PERMISSION_DENIED) {
                        showToast(this, getString(R.string.permission_rejected));
                        return;
                    }
                }

            }
        }
    }


    /**
     * 支付宝支付业务示例
     */
    public void payV2() {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            showAlert(this, getString(R.string.error_missing_appid_rsa_private));
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx).setMessage(info).setPositiveButton(R.string.confirm, null).setOnDismissListener(onDismiss).show();
    }

    private static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
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

}
