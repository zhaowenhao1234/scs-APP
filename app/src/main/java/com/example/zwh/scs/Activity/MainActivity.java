package com.example.zwh.scs.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.zwh.scs.Bean.Driver;
import com.example.zwh.scs.Data.StationData;
import com.example.zwh.scs.Listener.MyLocationListener;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.ImageUtil;
import com.example.zwh.scs.Util.NetWorkUtil;
import com.example.zwh.scs.Util.RoutePlanUtil;
import com.example.zwh.scs.Util.UploadUtil;
import com.example.zwh.scs.Util.UserInfoUtil;
import com.example.zwh.scs.Util.YingYan;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity".getClass().getSimpleName();
    private static final int PERMISSION_REQUESTCODE = 1;
    private static final int CLOSE_ROUTE = 123;
    public static BaiduMap mBaidumap = null;

    public boolean isRoutePlan = false;
    //布局view
    //地图模式状态标志
    private boolean mapMode;
    private boolean yingyanMode;

    private Button my_location;
    private Button openYingYan;
    private Button map_mode;
    private Button traffic_mode;

    private View mPopView;
    private PopupWindow mPopupWindow;
    private TextView pop_tittle;
    private TextView tv_pop_content;
    private Button btn_pop_ok;
    private Button btn_pop_cancel;

    //地图有关类
    private MapView mMapView = null;
    public static LocationClient locationClient = null;
    private MyLocationListener myLocationListener = null;

    //鹰眼服务类
    private YingYan yingYan = null;

    //地图标记集合
    private ArrayList<Marker> markerList = null;

    //地点位置详情
    private PopupWindow pw = null;
    private Button close_route = null;
    private LinearLayout close_route_view = null;
    private RoutePlanUtil search = null;


    public boolean status;
    public int mode;
    private RoutePlanUtil routePlanUtil;

    private String showCarId;
    private int currentEmptyNum;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                if (mPopupWindow.isShowing()) {
                    getEmptyNum(showCarId);
                }
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer();//在使用SDK各组件之前初始化context信息，传入ApplicationContext
        initActionBar();
        initContentView(R.layout.activity_main);
        initToolbarView("scs不等车", true, R.mipmap.oc_black_list_user);
        permission();
        InitPopWindow();
        initView();//初始化视图
        initBaiduMap();//初始化百度地图
        if (NetWorkUtil.isNetworkConnected(getApplicationContext())) {
            Toast.makeText(this, "网络连接正常", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "请检查网络是否正常连接！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        status = UserInfoUtil.getCurrentInfoUserState(getApplicationContext());
        mode = UserInfoUtil.getCurrentInfoUserMode(getApplicationContext());
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
            permissionLists.clear();
        } else {
            String[] permissionss = permissionLists.toArray(new String[permissionLists.size()]);
            ActivityCompat.requestPermissions(this, permissionss, 1);
        }

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

    /**
     *
     */
    private void InitPopWindow() {
        String id = UserInfoUtil.getCurrentInfoUserID(getApplicationContext());
        // TODO Auto-generated method stub
        // 将布局文件转换成View对象，popupview 内容视图
        mPopView = getLayoutInflater().inflate(R.layout.car_message_popwindow, null);
        // 将转换的View放置到 新建一个popuwindow对象中
        mPopupWindow = new PopupWindow(mPopView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 点击popuwindow外让其消失
        mPopupWindow.setOutsideTouchable(true);
        // mpopupWindow.setBackgroundDrawable(background);
        pop_tittle = (TextView) mPopView.findViewById(R.id.pop_tittle);
        tv_pop_content = (TextView) mPopView.findViewById(R.id.tv_pop_content);
        btn_pop_ok = (Button) mPopView.findViewById(R.id.btn_pop_ok);
        btn_pop_cancel = (Button) mPopView.findViewById(R.id.btn_pop_cancel);
        btn_pop_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                uploadEmptyNum(showCarId, 1);

                Toast.makeText(getApplicationContext(), "您已上车！", Toast.LENGTH_SHORT).show();
            }
        });

        btn_pop_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                uploadEmptyNum(showCarId, 0);
                Toast.makeText(getApplicationContext(), "您已下车！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ShowPopWindow() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            routePlanUtil.removeAllOverlay();
        } else {
            // 设置PopupWindow 显示的形式 底部或者下拉等
            // 在某个位置显示

            mPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style2);
            mPopupWindow.showAtLocation(mPopView, Gravity.TOP, 0, getSupportActionBar().getHeight());
            // 作为下拉视图显示
            // mPopupWindow.showAsDropDown(mPopView, Gravity.CENTER, 200, 300);

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
        if (yingyanMode) {
            if (status) {
                if (mode == LoginActivity.DRIVER_OPTION) {
                    Toast.makeText(this, "上传司机位置", Toast.LENGTH_SHORT).show();
                } else if (mode == LoginActivity.USER_OPTION) {
                    Toast.makeText(this, "查询司机位置", Toast.LENGTH_SHORT).show();
                }
                yingYan = new YingYan(getApplicationContext(), mode);
            } else {
                Toast.makeText(this, "请登录或注册后使用", Toast.LENGTH_SHORT).show();
            }
        } else {

            if (mode == LoginActivity.DRIVER_OPTION) {
                yingYan.mTraceClient.stopGather(yingYan.mTraceListener);
                Toast.makeText(this, "司机位置采集服务已停止！", Toast.LENGTH_SHORT).show();
            } else if (mode == LoginActivity.USER_OPTION) {
                yingYan.mTraceClient.stopTrace(yingYan.mTrace, yingYan.mTraceListener);
                Toast.makeText(this, "司机查找功能已关闭！", Toast.LENGTH_SHORT).show();
            }
        }
        yingyanMode = !yingyanMode;

    }


    /***
     *初始化布局界面
     *@return void
     *@author wenhaoz
     *created at 2019/3/6 18:20
     */
    private void initView() {
//        isLogin = UserInfoUtil.getCurrentInfoUserState(getApplicationContext());

        //获取地图控件引用
        mMapView = findViewById(R.id.bmapView);
        search = new RoutePlanUtil(MainActivity.this);

        //获得baidumap实例
        mBaidumap = mMapView.getMap();
        my_location = findViewById(R.id.my_location);
        traffic_mode = findViewById(R.id.traffic_mode);
        openYingYan = findViewById(R.id.openYingYan);
        map_mode = findViewById(R.id.map_mode);
        close_route_view = findViewById(R.id.close_route);
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
        yingyanMode = true;
        //获得baidumap实例
        mBaidumap = mMapView.getMap();
        //开启我的定位图层
        mBaidumap.setMyLocationEnabled(true);

        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        locationClient = new LocationClient(getApplicationContext());
        //实例化定位监听
        myLocationListener = new MyLocationListener(getApplicationContext());
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);

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
            bundle.putString("mode", "station");
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

            case CLOSE_ROUTE:
                cancelRoutePlan();
                break;

            default:
                break;
        }
    }

    /***
     *取消行程
     *@return void
     *@author wenhaoz
     *created at 2019/3/27 21:47
     */
    private void cancelRoutePlan() {
        isRoutePlan = false;
        search.removeAllOverlay();
        Toast.makeText(MainActivity.this, "行程已取消", Toast.LENGTH_SHORT).show();
        close_route_view.removeAllViews();
        UploadUtil.uploadRoute(getApplicationContext(), new LatLng(0, 0), new LatLng(0, 0));
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

            String id = (String) marker.getExtraInfo().get("id");
            showCarId = id;
            com.baidu.mapapi.model.LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            if (!isRoutePlan) {
                if ((marker.getExtraInfo().get("mode") != null) && (marker.getExtraInfo().get("mode").equals("station"))) {
                    String stationName = marker.getExtraInfo().getString("name");
                    String stationAdd = marker.getExtraInfo().getString("add");
                    MapStatusUpdate u = (MapStatusUpdate) MapStatusUpdateFactory.newLatLngZoom(latLng, 17);
                    mBaidumap.setMapStatus(u);
                    showStationWindow(stationName, stationAdd, latLng);
                } else {
                    Log.d(TAG, "onMarkerClick: " + id);
                    getEmptyNum(id);
                    ShowPopWindow();
                    getRoute(id);

//                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    //第一个参数为xml文件中view的id，第二个参数为此view的父组件，可以为null，android会自动寻找它是否拥有父组件
//                    View view = inflater.inflate(R.layout.car_message_window, null);
//                    //响应点击的OnInfoWindowClickListener
//
//                    InfoWindow mInfoWindow = new InfoWindow(view, latLng, -100, listener);
//
//                    //使InfoWindow生效
//                    mBaidumap.showInfoWindow(mInfoWindow);
                }
            }
            return true;
        }
    };


    private void showStationWindow(String stationName, String stationAdd, LatLng latLng) {

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
        ImageView go_here = (ImageView) contentView.findViewById(R.id.go_here);
        station_name.setText(stationName);
        station_add.setText(stationAdd);
        LinearLayout layout = (LinearLayout) contentView.findViewById(R.id.dialog_ll);
        station_name.setOnClickListener(pop);
        station_add.setOnClickListener(pop);
        layout.setOnClickListener(pop);
        go_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search.start(myLocationListener.getMylocation(), latLng);
                //search.start(new LatLng(28.072517, 113.008644), latLng);
                if (pw != null) {
                    pw.dismiss();
                }
                isRoutePlan = true;
                Toast.makeText(MainActivity.this, "开启行程！", Toast.LENGTH_SHORT).show();
                addButton();
            }
        });
    }


    /***
     *动态添加取消行程按钮
     *@return void
     *@author wenhaoz
     *created at 2019/3/27 21:39
     */
    private void addButton() {
        close_route = new Button(MainActivity.this);
        close_route.setText("关闭行程");
        close_route.setId(CLOSE_ROUTE);
        close_route_view.addView(close_route);
        close_route.setOnClickListener(MainActivity.this);
    }


    private View.OnClickListener pop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.station_name:
                    Toast.makeText(MainActivity.this, "点击 ", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.station_add:
                    Toast.makeText(MainActivity.this, "点击 ", Toast.LENGTH_SHORT).show();
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

    /***
     *如果是用户的话，则可以点击下小白图标进行当前录像的显示
     *@return void
     *@author wenhaoz
     *created at 2019/4/15 21:08
     */
    public void getRoute(String id) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("id", UserInfoUtil.getCurrentInfoUserID(getApplicationContext()));
        String url = "http://129.204.119.172:8080/driver/getDriver";
        Request request = new Request.Builder()//创建Request 对象。
                .url(url).post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

//                Log.d("uploadRoute", "onResponse: " + response.body().toString());
                Driver driver = new Driver();
                Gson gson = new Gson();
                driver = gson.fromJson(response.body().string(), Driver.class);
                String stlongtitude = driver.getDriver().getRlLongtitude();
                String stlatitude = driver.getDriver().getRlLatitude();
                String edlongtitude = driver.getDriver().getEndLongtitude();
                String edlatitude = driver.getDriver().getEndLatitude();
                if (stlongtitude.equals("0.0") && stlatitude.equals("0.0") && edlongtitude.equals("0.0") && edlatitude.equals("0.0")) {
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "该小白当前并未进行路线规划！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    LatLng latLng1 = new LatLng(Double.parseDouble(stlatitude), Double.parseDouble(stlongtitude));
                    LatLng latLng2 = new LatLng(Double.parseDouble(edlatitude), Double.parseDouble(edlongtitude));
                    routePlanUtil = new RoutePlanUtil(MainActivity.this);
                    routePlanUtil.start(latLng1, latLng2);
                }
            }
        });

    }

    public void getEmptyNum(String id) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("id", id);
        String url = "http://129.204.119.172:8080/driver/getDriver";
        Request request = new Request.Builder()//创建Request 对象。
                .url(url).post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Driver driver = new Driver();
                Gson gson = new Gson();
                String json = response.body().string();
                driver = gson.fromJson(json, Driver.class);
                updateEmptyNum(driver.getDriver().getId(), driver.getDriver().getEmptyNum());
            }
        });
    }

    /***
     *在弹窗中显示小车空座数量
     *@return void
     *@author wenhaoz
     *created at 2019/4/15 21:29
     * @param id
     * @param emptyNum
     */
    private void updateEmptyNum(int id, int emptyNum) {
        currentEmptyNum = emptyNum;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pop_tittle.setText("司机 " + id + " 号");
                tv_pop_content.setText("当前空座数量 ：" + emptyNum + " 个");
                Message message = Message.obtain();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        message.arg1 = 1;
                        handler.sendMessage(message);
                    }
                }, 2000);
            }
        });
    }

    public void uploadEmptyNum(String id, int status) {
        if (status == 1) {
            currentEmptyNum--;
        } else {
            currentEmptyNum++;
        }
        if (currentEmptyNum >= 0 && currentEmptyNum <= 17) {
            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
            FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
            formBody.add("id", id);
            formBody.add("emptyNum", String.valueOf(currentEmptyNum));
            String url = "http://129.204.119.172:8080/driver/update";
            Request request = new Request.Builder()//创建Request 对象。
                    .url(url).post(formBody.build())//传递请求体
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Log.d("uploadRoute", "onResponse: " + response.body().toString());

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "当前座位已满！", Toast.LENGTH_SHORT).show();
        }

    }

}
