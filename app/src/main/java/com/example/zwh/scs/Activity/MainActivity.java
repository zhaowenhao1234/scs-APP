package com.example.zwh.scs.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.example.zwh.scs.Data.StationData;
import com.example.zwh.scs.Listener.MyLocationListener;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.ImageUtil;
import com.example.zwh.scs.Util.IntentUtils;
import com.example.zwh.scs.Util.YingYan;
import com.example.zwh.scs.Util.StatusbarUtil;
import com.example.zwh.scs.Wallet.WalletActivity;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity".getClass().getSimpleName();
    private static final int PERMISSION_REQUESTCODE = 1;
    private static final int QR_REQUEST_CODE = 10086;

    //地图模式状态标志
    private boolean mapMode;
    //布局view
    private DrawerLayout mDrawerLayout; //滑动菜单
    private NavigationView navView;
    private Button user_message;
    private Button my_location;
    private Button openYingYan;
    private Button mQRCodeScanner;
    private Button map_mode;
    private Button traffic_mode;
    private Button notice;

    //地图有关类
    private MapView mMapView = null;
    public static BaiduMap mBaidumap = null;
    private LocationClient locationClient = null;
    private MyLocationListener myLocationListener = null;

    //鹰眼服务类
    private YingYan yingYan = null;

    //地图标记集合
    private ArrayList<Marker> markerList = null;
    private BitmapDescriptor bitmapDescriptor = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer();//在使用SDK各组件之前初始化context信息，传入ApplicationContext
        initActionBar();
        setContentView(R.layout.activity_main);
        permission();
        initView();//初始化视图
        initBaiduMap();//初始化百度地图
        StatusbarUtil.setTransparentWindow(this, true);
        ZXingLibrary.initDisplayOpinion(this);
    }

    /***
     *请求权限
     *@return void
     *@author wenhaoz
     *created at 2019/2/25 19:45
     */
    private void permission() {
        String[] permissions = new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

        List<String> permissionLists = new ArrayList<>();

        permissionLists.clear();

        //判断哪些权限还未获取
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionLists.add(permissions[i]);
            }
        }

        if (permissionLists.isEmpty()) {//说明肯定有拒绝的权限
            Toast.makeText(this, "权限全部授予", Toast.LENGTH_SHORT).show();
        } else {
            String[] permissionss = permissionLists.toArray(new String[permissionLists.size()]);
            ActivityCompat.requestPermissions(this, permissionss, 1);
        }
    }

    /***
     *权限请求结果回调
     *@param  permissions, grantResults
     *@return void
     *@author wenhaoz
     *created at 2019/2/25 19:46
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUESTCODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                            if (showRequestPermission) {
                                permission();//重新申请权限
                                return;
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }


    /***
     *百度地图SDK初始化
     *@return void
     *@author wenhaoz
     *created at 2019/3/6 18:20
     */
    private void SDKInitializer() {
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        SDKInitializer.initialize(getApplicationContext());
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    /***
     *初始化状态栏
     *@return void
     *@author wenhaoz
     *created at 2019/3/4 20:33
     */
    private void initActionBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);//仿百度地图
        }
    }

    private void initYingYan() {
        yingYan = new YingYan(getApplicationContext());
        Toast.makeText(this, "查询司机位置", Toast.LENGTH_SHORT).show();
    }


    /***
     *初始化布局界面
     *@return void
     *@author wenhaoz
     *created at 2019/3/6 18:20
     */
    private void initView() {
        bitmapDescriptor = ImageUtil.setImage(this, R.drawable.bus_stop_board, 0.15f, 0.15f);
        //获取地图控件引用
        mMapView = findViewById(R.id.bmapView);
        map_mode = (Button) findViewById(R.id.map_mode);
        traffic_mode = (Button) findViewById(R.id.traffic_mode);
        notice = (Button) findViewById(R.id.notice);


        //获得baidumap实例
        mBaidumap = mMapView.getMap();

        user_message = findViewById(R.id.user_message);
        my_location = findViewById(R.id.my_location);
        traffic_mode = (Button) findViewById(R.id.traffic_mode);
        openYingYan = (Button) findViewById(R.id.openYingYan);

        //设置监听
        openYingYan.setOnClickListener(this);
        my_location.setOnClickListener(this);
        user_message.setOnClickListener(this);
        map_mode.setOnClickListener(this);
        traffic_mode.setOnClickListener(this);
        notice.setOnClickListener(this);
        findViewById(R.id.btn_scanQRcode).setOnClickListener(this);
        //获得实例
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mQRCodeScanner = findViewById(R.id.btn_scanQRcode);
        mQRCodeScanner.setOnClickListener(this);
        //设置view
        initNavigationView();
    }


    /***
     *初始化设置NavigationView
     *@return void
     *@author wenhaoz
     *created at 2019/3/2 17:39
     */
    private void initNavigationView() {
        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);//  取消导航栏图标着色
        navView.setCheckedItem(R.id.nav_register);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_register://点击进入注册界面
                        IntentUtils.SetIntent(MainActivity.this, RegisterActivity.class);
                        break;
                    case R.id.nav_login://点击进入登录界面
                        IntentUtils.SetIntent(MainActivity.this, LoginActivity.class);
                    case R.id.nav_task:
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_payment:
                        IntentUtils.SetIntent(MainActivity.this, WalletActivity.class);
                        mDrawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });
    }


    /***
     *初始化百度地图各项参数
     *@return void
     *@author wenhaoz
     *created at 2019/3/2 17:40
     */
    private void initBaiduMap() {

        mapMode = true;
        //获得baidumap实例
        mBaidumap = mMapView.getMap();
        //开启我的定位图层
        mBaidumap.setMyLocationEnabled(true);

        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        locationClient = new LocationClient(getApplicationContext());

        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        //设置扫描时间
        locationOption.setScanSpan(1000);
        //设置地图经纬度类型
        locationOption.setCoorType("bd09ll");
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //设置是否打开GPS
        locationOption.setOpenGps(true);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);

        //实例化定位监听
        myLocationListener = new MyLocationListener();
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);

        //开始定位
        locationClient.start();

        //添加停车点标记
        addMarker();

    }


    /***
     *初始化设置停车点Marker
     *@return void
     *@author wenhaoz
     *created at 2019/3/6 22:07
     */
    private void addMarker() {
        //获得停车站点经纬度集合
        StationData stationData = new StationData();
        //覆盖物实例集合
        markerList = new ArrayList<Marker>();

        for (int i = 0; i < 11; i++) {
            MarkerOptions options = new MarkerOptions().position(stationData.stationMarker.get(i)).icon(bitmapDescriptor);
            Marker marker = (Marker) mBaidumap.addOverlay(options);
            markerList.add(marker);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();

    }

    @Override
    protected void onDestroy() {
        //注销所有鹰眼相关服务
        yingYan.mTraceClient.stopGather(yingYan.mTraceListener);
        yingYan.mTraceClient.clear();
        yingYan = null;

        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
        mBaidumap.setMyLocationEnabled(false);

        //注销位置监听
        locationClient.unRegisterLocationListener(myLocationListener);
        locationClient.stop();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.my_location:
                myLocationListener.showMyLocaton(MyLocationListener.location);
                break;

            case R.id.user_message:
                mDrawerLayout.openDrawer(GravityCompat.END);
                break;

            case R.id.notice:
                break;

            case R.id.openYingYan:
                initYingYan();
                break;

            case R.id.btn_scanQRcode:
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, QR_REQUEST_CODE);
                break;

            case R.id.map_mode:
                changeMapMode();
                break;

            case R.id.traffic_mode:
                setTrafficOverlay();
                break;

            default:
                break;
        }
    }

    /**
     * 设置交通图层是否开启
     *
     * @return void
     * @author wenhaoz
     * created at 2019/3/9 15:58
     */
    private void setTrafficOverlay() {
        if (mBaidumap.isTrafficEnabled()) {
            mBaidumap.setTrafficEnabled(false);
            Toast.makeText(this, "关闭交通图层", Toast.LENGTH_SHORT).show();
        } else {
            mBaidumap.setTrafficEnabled(true);
            Toast.makeText(this, "开启交通图层", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     *切换地图样式
     *@return void
     *@author wenhaoz
     *created at 2019/3/8 13:02
     */
    private void changeMapMode() {
        if (mapMode) {
            mBaidumap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            Toast.makeText(this, "已切换卫星地图", Toast.LENGTH_SHORT).show();
        } else {
            mBaidumap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            Toast.makeText(this, "已切换普通地图", Toast.LENGTH_SHORT).show();

        }
        mapMode = !mapMode;
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
                    Toast.makeText(this, "无法跳转到微信，请检查是否安装了微信", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}
