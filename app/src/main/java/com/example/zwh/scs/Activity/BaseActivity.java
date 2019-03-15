package com.example.zwh.scs.Activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.IntentUtils;
import com.example.zwh.scs.Util.StatusbarUtil;
import com.example.zwh.scs.Wallet.WalletActivity;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout; //滑动菜单
    private NavigationView navView;
    private FrameLayout viewContent;
    private static final int QR_REQUEST_CODE = 10086;
    protected CircleImageView portraitImage;//更改头像
    protected TextView emailText;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        StatusbarUtil.setTransparentWindow(this, true);
        initToolbarView("SCSTaxing", true, R.mipmap.oc_black_list_user);
        ZXingLibrary.initDisplayOpinion(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scanner:
                Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                startActivityForResult(intent, QR_REQUEST_CODE);
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    public android.support.v7.widget.Toolbar initToolbar(int id, int titleId, String titleString) {
        android.support.v7.widget.Toolbar toolbar =
                findViewById(id);
//        toolbar.setTitle("");
        TextView textView = findViewById(titleId);
        textView.setText(titleString);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        return toolbar;
    }

    protected void initToolbarView(String titleString, boolean isShown, int iconid) {
        android.support.v7.widget.Toolbar toolbar = initToolbar(R.id.toolbar_activity_base,
                R.id.toolbar_title, titleString);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout_activity_base);

        initNavigationView();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(isShown);
            actionBar.setHomeAsUpIndicator(iconid);
        }
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initNavigationView() {
        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);//  取消导航栏图标着色
        View headView = navView.getHeaderView(0);
        portraitImage = headView.findViewById(R.id.icon_image);
        emailText = headView.findViewById(R.id.mail);
        portraitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.SetIntent(getApplicationContext(), LoginActivity.class);
            }
        });
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams para = navView.getLayoutParams();
        para.width = width / 5 * 3;
        navView.setLayoutParams(para);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_task:
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_payment:
                        IntentUtils.SetIntent(getApplication(), WalletActivity.class);
                        //mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_msgboard:
                        IntentUtils.SetIntent(getApplication(), MessageActivity.class);
                        //mDrawerLayout.closeDrawers();
                    default:
                        //mDrawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });
    }

    protected void initContentView(int r) {
        viewContent = findViewById(R.id.viewContent);
        LayoutInflater.from(BaseActivity.this).inflate(
                r, viewContent);
    }

    @SuppressLint("WrongConstant")
    private void handleQRCodeScanner(String result) {
        if (result != null) {
            if (result.indexOf("wxp") != -1) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
                    intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
                    intent.setFlags(335544320);
                    intent.setAction("android.intent.action.VIEW");
                    this.startActivity(intent);
                } catch (Exception e) {
                    //若无法正常跳转，在此进行错误处理
                    Toast.makeText(getApplicationContext(), "无法跳转到微信，请检查是否安装了微信", Toast.LENGTH_SHORT).show();
                }
            } else if (result.indexOf("alipay") != -1) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(result));
                startActivity(intent);
            } else if (result.indexOf("ALIPAY") != -1) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(IntentUtils.handleAlipayUpperCase(result)));
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "解析结果:" + result, Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理二维码扫描结果
        if (requestCode == QR_REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    handleQRCodeScanner(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getApplicationContext(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_newMeg).setVisible(false);
        return true;
    }
}
