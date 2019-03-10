package com.example.zwh.scs.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.internal.NavigationMenuItemView;
import android.util.Log;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.EntityInfo;
import com.baidu.trace.api.entity.EntityListRequest;
import com.baidu.trace.api.entity.EntityListResponse;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProtocolType;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.TraceLocation;
import com.example.zwh.scs.Activity.MainActivity;
import com.example.zwh.scs.R;
import com.example.zwh.scs.model.CurrentLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * created at 2019/3/3 13:15 by wenhaoz
 */
public class YingYan {
    private static final String TAG = "YingYan";
    private static final int QUERY_ENTITYLIST = 1;
    public OnTraceListener mTraceListener;
    public OnEntityListener entityListener;
    public OnTrackListener trackListener;
    private Context context;
    //鹰眼轨迹
    private int gatherInterval;  //位置采集周期 (s)
    private int packInterval;  //打包周期 (s)
    private String entityName;  // entity标识
    private long serviceId;// 鹰眼服务ID

    public Trace mTrace = null;//实例化轨迹服务
    public LBSTraceClient mTraceClient = null;  //轨迹服务客户端


    private LocRequest locRequest = null;
    private boolean isOpen;
    private List<EntityInfo> entities = null;
    private Map<String, com.baidu.trace.model.LatLng> positions = null;
    private List<Overlay> markerList = null;
    private Overlay mCar = null;
    private BitmapDescriptor bitmapDescriptor = null;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == QUERY_ENTITYLIST) {
                queryEntityList();
            }
            return false;
        }
    });

    public YingYan(Context context) {
        this.context = context;
        isOpen = false;
        bitmapDescriptor = ImageUtil.setImage(context, R.drawable.car, 0.1f, 0.1f);

        markerList = new ArrayList<>();
        positions = new HashMap<>();
        //初始化各项参数
        initTracePara();

        // 初始化轨迹服务
        mTrace = new Trace(serviceId, entityName, false);
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(context);

        /***
         *初始化轨迹服务监听器
         *@return void
         *@author wenhaoz
         *created at 2019/3/3 13:40
         */
        mTraceListener = new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {
                Log.d(TAG, "onBindServiceCallback: " + i);
            }

            @Override
            public void onStartTraceCallback(int i, String s) {
                //当启动服务成功时，开启位置收集服务
                if (i == 0) {
                    mTraceClient.startGather(this);
                    Log.d(TAG, "onStartTraceCallback: " + i);
                }
            }

            @Override
            public void onStopTraceCallback(int i, String s) {

            }

            @Override
            public void onStartGatherCallback(int i, String s) {
                if (i == 0) {
//                    getCurrentLocation();
                    queryEntityList();
                }
                Log.d(TAG, "onStartGatherCallback: " + i);
            }

            @Override
            public void onStopGatherCallback(int i, String s) {

            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {

            }

            @Override
            public void onInitBOSCallback(int i, String s) {

            }
        };


        /***
         *初始化轨迹跟踪监听器
         *@param
         *@return
         *@author wenhaoz
         *created at 2019/3/3 16:50
         */
        trackListener = new OnTrackListener() {

            //实时轨迹监听器
            @Override
            public void onLatestPointCallback(LatestPointResponse response) {
                LatestPoint point = response.getLatestPoint();

                LatLng currentLatLng = MapUtil.convertTrace2Map(point.getLocation());

                CurrentLocation.locTime = point.getLocTime();
                CurrentLocation.latitude = currentLatLng.latitude;
                CurrentLocation.longitude = currentLatLng.longitude;
                Log.d(TAG, "onLatestPointCallback: " + CurrentLocation.longitude + " " + CurrentLocation.latitude);
            }
        };

        /****
         *初始化实体状态监听器
         *@param
         *@return
         *@author wenhaoz
         *created at 2019/3/3 13:40
         */
        //Entity监听器(用于接收实时定位回调)
        entityListener = new OnEntityListener() {
            @Override
            public void onReceiveLocation(TraceLocation location) {
                //将回调的当前位置location显示在地图MapView上，地图显示位置不清楚的可以篇头阅读百度文章(二)
                //这里位置点的返回间隔时间为Handler.postDelayed的延时时间
                Log.d(TAG, "onReceiveLocation: " + location.getLongitude() + " " + location.getLongitude());
                //getCurrentLocation();

            }

            //获得所有service管理下的设备位置信息
            @Override
            public void onEntityListCallback(EntityListResponse entityListResponse) {
                synchronized (this) {
                    entities = entityListResponse.getEntities();
                    for (int i = 0; i < entities.size(); i++) {
                        String entityName = entities.get(i).getEntityName();
                        com.baidu.trace.model.LatLng latLng = entities.get(i).getLatestLocation().getLocation();
                        LatLng latLng1 = new LatLng(latLng.getLatitude(), latLng.getLongitude());
                        addOtherMarker(entityName, latLng1);
                    }
                    Message message = Message.obtain();
                    message.what = QUERY_ENTITYLIST;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendMessage(message);
                        }
                    }, 3000);
                    Log.d(TAG, "当前Entity设备的数量" + entities.size());
                }


            }
        };

        //开启鹰眼轨迹服务
        openTraceService();

    }


    /***
     *在地图上显示其他人的位置
     *@return void
     *@author wenhaoz
     *created at 2019/3/9 12:50
     * @param entityName
     * @param latLng
     */
    private void addOtherMarker(String entityName, LatLng latLng) {
        for (int i = 0; i < markerList.size(); i++) {
            if (markerList.get(i).getExtraInfo().getString("name") == entityName) {
                markerList.get(i).remove();
                markerList.remove(i);
            }
        }
        MarkerOptions options = new MarkerOptions().position(latLng).icon(bitmapDescriptor);
        Marker marker = (Marker) MainActivity.mBaidumap.addOverlay(options);
        Bundle bundle = new Bundle();
        bundle.putString("name", entityName);
        marker.setExtraInfo(bundle);
        markerList.add(marker);
    }


    /***
     *初始化鹰眼服务各项参数
     *@return void
     *@author wenhaoz
     *created at 2019/3/3 12:37
     */
    public void initTracePara() {
        gatherInterval = 2;
        packInterval = 12;
        entityName = GetIMEI.getImei(context);
        serviceId = 209693;
        locRequest = new LocRequest(serviceId);
    }

    /***
     *开启鹰眼轨迹服务
     *@return void
     *@author wenhaoz
     *created at 2019/3/3 12:16
     */
    public void openTraceService() {
        //设置位置采集和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);
        //设置协议类型 http 或 https
        mTraceClient.setProtocolType(ProtocolType.HTTP);
        // 开启服务
        mTraceClient.startTrace(mTrace, mTraceListener);
    }


    /**
     * 获取该设备当前位置
     */
    public void getCurrentLocation() {
        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
//        if (NetUtil.isNetworkAvailable(context)) {
//            LatestPointRequest request = new LatestPointRequest(1, serviceId, entityName);
//            ProcessOption processOption = new ProcessOption();
//            processOption.setNeedDenoise(true);
//            processOption.setRadiusThreshold(100);
//            request.setProcessOption(processOption);
//            mTraceClient.queryLatestPoint(request, trackListener);
//        } else {
        mTraceClient.queryRealTimeLoc(locRequest, entityListener);
//        }
    }

    /***
     *获取同一个ServiceId下所有Entity设备位置列表
     *@param
     *@return
     */
    public void queryEntityList() {
        EntityListRequest request = new EntityListRequest(1, serviceId);
        mTraceClient.queryEntityList(request, entityListener);
    }


}
