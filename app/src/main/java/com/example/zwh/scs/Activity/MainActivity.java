package com.example.zwh.scs.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
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
    public static BaiduMap mBaidumap = null;
    //布局view
    //地图模式状态标志
    private boolean mapMode;

    private Button my_location;
    private Button openYingYan;
    private Button map_mode;
    private Button traffic_mode;


    //地图有关类
    private MapView mMapView = null;
    private LocationClient locationClient = null;
    private MyLocationListener myLocationListener = null;

    //鹰眼服务类
    private YingYan yingYan = null;

    //地图标记集合
    private ArrayList<Marker> markerList = null;

    //地点位置详情
    private PopupWindow pw = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer();//在使用SDK各组件之前初始化context信息，传入ApplicationContext
        initActionBar();
        initContentView(R.layout.activity_main);
        initToolbarView("scs不等车", true, R.mipmap.oc_black_list_user);
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
        //位置采集周期
        // 在Android 6.0及以上系统，若定制手机使用到doze模式，请求将应用添加到白名单。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Context trackApp = getApplicationContext();
            String packageName = trackApp.getPackageName();
            PowerManager powerManager = (PowerManager) trackApp.getSystemService(Context.POWER_SERVICE);
            boolean isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName);
            if (!isIgnoring) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                try {
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
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
            permissionLists.clear();
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

        //获取地图控件引用
        mMapView = findViewById(R.id.bmapView);
        map_mode = (Button) findViewById(R.id.map_mode);
        traffic_mode = (Button) findViewById(R.id.traffic_mode);
        map_mode = findViewById(R.id.map_mode);
        traffic_mode = findViewById(R.id.traffic_mode);


        //获得baidumap实例
        mBaidumap = mMapView.getMap();

        my_location = findViewById(R.id.my_location);
        traffic_mode = findViewById(R.id.traffic_mode);
        openYingYan = findViewById(R.id.openYingYan);

        //设置监听
        openYingYan.setOnClickListener(this);
        my_location.setOnClickListener(this);
        map_mode.setOnClickListener(this);
        traffic_mode.setOnClickListener(this);
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

        mBaidumap.setOnMarkerClickListener(listener);

    }


    /***
     *初始化设置停车点Marker
     *@return void
     *@author wenhaoz
     *created at 2019/3/6 22:07
     */
    private void addMarker() {
        //获得停车站点的图标
        BitmapDescriptor bitmapDescriptor = ImageUtil.setImage(this, R.drawable.dgp_station_board_icon, 0.6f, 0.6f);
        //获得停车站点经纬度集合
        StationData stationData = new StationData();
        //覆盖物实例集合
        markerList = new ArrayList<Marker>();

        for (int i = 0; i < 11; i++) {
            MarkerOptions options = new MarkerOptions().position(stationData.stationMarker.get(i)).icon(bitmapDescriptor);
            Marker marker = (Marker) mBaidumap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putString("name", StationData.name[i]);
            bundle.putString("add", StationData.add[i]);
            marker.setExtraInfo(bundle);
            markerList.add(marker);
        }

        bitmapDescriptor.recycle();
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

        //关闭我的定位图层
        mBaidumap.setMyLocationEnabled(false);

        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;

        //注销位置监听
        locationClient.unRegisterLocationListener(myLocationListener);
        locationClient.stop();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.my_location:
                myLocationListener.showMyLocaton(myLocationListener.location);
                break;


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

    BaiduMap.OnMarkerClickListener listener = new BaiduMap.OnMarkerClickListener() {
        /**
         * 地图 Marker 覆盖物点击事件监听函数
         * @param marker 被点击的 marker
         */
        public boolean onMarkerClick(Marker marker) {
            String stationName = marker.getExtraInfo().getString("name");
            String stationAdd = marker.getExtraInfo().getString("add");
            com.baidu.mapapi.model.LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            MapStatusUpdate u = (MapStatusUpdate) MapStatusUpdateFactory.newLatLngZoom(latLng, 17);
            mBaidumap.setMapStatus(u);
            showEditPhotoWindow(stationName, stationAdd);
            return true;
        }
    };

    private void showEditPhotoWindow(String stationName, String stationAdd) {
        View contentView = getLayoutInflater().inflate(R.layout.popup_window, null);
        pw = new PopupWindow(contentView, getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), true);
        //设置popupwindow弹出动画
        pw.setAnimationStyle(R.style.popupwindow_anim_style);
        //设置popupwindow背景
        pw.setBackgroundDrawable(new ColorDrawable());
        pw.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        //处理popupwindow
        TextView station_name = (TextView) contentView.findViewById(R.id.station_name);
        TextView station_add = (TextView) contentView.findViewById(R.id.station_add);
        station_name.setText(stationName);
        station_add.setText(stationAdd);
        LinearLayout layout = (LinearLayout) contentView.findViewById(R.id.dialog_ll);
        station_name.setOnClickListener(pop);
        station_add.setOnClickListener(pop);
        layout.setOnClickListener(pop);
    }


    private View.OnClickListener pop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.station_name:
                    Toast.makeText(MainActivity.this, "相册 ", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.station_add:
                    Toast.makeText(MainActivity.this, "拍照 ", Toast.LENGTH_SHORT).show();
                    break;
                //点击提示框以外的地方关闭
                case R.id.dialog_ll:
                    if (pw != null) {
                        pw.dismiss();
                    }
                    break;
            }
        }
    };


}
