package com.example.zwh.scs.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.example.zwh.scs.Util.YingYan;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity".getClass().getSimpleName();
    private static final int PERMISSION_REQUESTCODE = 1;


    //地图模式状态标志
    private boolean mapMode;
    //布局view

    private NavigationView navView;
    //    private Button user_message;
//    private Button my_location;
    private Button openYingYan;
    private Button mQRCodeScanner;
    private Button map_mode;
    private Button traffic_mode;

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
        //setLayout(R.layout.activity_main);
        SDKInitializer();//在使用SDK各组件之前初始化context信息，传入ApplicationContext
        initActionBar();
        initContentView(R.layout.activity_main);
        permission();
        initView();//初始化视图
        initBaiduMap();//初始化百度地图
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
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);//仿百度地图
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        map_mode = findViewById(R.id.map_mode);
        traffic_mode = findViewById(R.id.traffic_mode);


        //获得baidumap实例
        mBaidumap = mMapView.getMap();

//        user_message = findViewById(R.id.user_message);
//        my_location = findViewById(R.id.my_location);
        traffic_mode = findViewById(R.id.traffic_mode);
        openYingYan = findViewById(R.id.openYingYan);
        //设置监听
        openYingYan.setOnClickListener(this);
        //my_location.setOnClickListener(this);
        //user_message.setOnClickListener(this);
        map_mode.setOnClickListener(this);
        traffic_mode.setOnClickListener(this);
        //findViewById(R.id.btn_scanQRcode).setOnClickListener(this);
        //获得实例
        //mDrawerLayout = findViewById(R.id.drawer_layout);
        //mQRCodeScanner = findViewById(R.id.btn_scanQRcode);
        //mQRCodeScanner.setOnClickListener(this);
        //设置view
        //initNavigationView();
    }


    /***
     *初始化设置NavigationView
     *@return void
     *@author wenhaoz
     *created at 2019/3/2 17:39
     */
//    private void initNavigationView() {
//        navView = findViewById(R.id.nav_view);
//        navView.setItemIconTintList(null);//  取消导航栏图标着色
//        navView.setCheckedItem(R.id.nav_register);
//        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.nav_register://点击进入注册界面
//                        IntentUtils.SetIntent(MainActivity.this, RegisterActivity.class);
//                        break;
//                    case R.id.nav_login://点击进入登录界面
//                        IntentUtils.SetIntent(MainActivity.this, LoginActivity.class);
//                    case R.id.nav_task:
//                        mDrawerLayout.closeDrawers();
//                        break;
//                    case R.id.nav_payment:
//                        IntentUtils.SetIntent(MainActivity.this, WalletActivity.class);
//                        mDrawerLayout.closeDrawers();
//                        break;
//                }
//                return true;
//            }
//        });
//    }


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
        yingYan.mTraceClient.stopTrace(yingYan.mTrace, yingYan.mTraceListener);
        yingYan.mTraceClient.stopGather(yingYan.mTraceListener);
        yingYan.mTraceClient.clear();

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

//            case R.id.my_location:
//                myLocationListener.showMyLocaton(myLocationListener.location);
//                break;
//
//            case R.id.user_message:
//                //mDrawerLayout.openDrawer(GravityCompat.END);
//                break;

            case R.id.openYingYan:
                initYingYan();
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


}
