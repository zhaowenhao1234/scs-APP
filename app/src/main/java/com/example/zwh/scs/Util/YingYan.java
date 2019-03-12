package com.example.zwh.scs.Util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.animation.RotateAnimation;
import com.baidu.mapapi.animation.Transformation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.AddEntityRequest;
import com.baidu.trace.api.entity.AddEntityResponse;
import com.baidu.trace.api.entity.EntityInfo;
import com.baidu.trace.api.entity.EntityListRequest;
import com.baidu.trace.api.entity.EntityListResponse;
import com.baidu.trace.api.entity.FilterCondition;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.AddPointRequest;
import com.baidu.trace.api.track.AddPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProtocolType;
import com.baidu.trace.model.PushMessage;
import com.example.zwh.scs.Activity.MainActivity;
import com.example.zwh.scs.Listener.MyLocationListener;
import com.example.zwh.scs.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created at 2019/3/3 13:15 by wenhaoz
 */
public class YingYan {

    private static final String TAG = YingYan.class.getSimpleName();
    private static final int QUERY_ENTITYLIST = 1;//查询指令
    private static final int UPDATE_DIRECTION = 2;//更新位置指令

    //上下文
    private Context context;

    //鹰眼服务监听器
    public OnTraceListener mTraceListener;
    public OnEntityListener entityListener;
    public OnTrackListener trackListener;


    //鹰眼轨迹相关参数
    private int gatherInterval; //位置采集周期 (s)
    private int packInterval;   //打包周期 (s)
    private String entityName;  // entity标识
    private long serviceId;     //鹰眼服务ID

    public Trace mTrace = null;                 //实例化轨迹服务
    public LBSTraceClient mTraceClient = null;  //实例化轨迹服务客户端

    private List<EntityInfo> entities = null;   //实体信息存储列表
    private List<Marker> markerList = null;     //marker存储列表
    private BitmapDescriptor bitmapDescriptor = null;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_ENTITYLIST:
                    updateDirection();
                    queryEntityList();
                    break;
            }
            return false;
        }
    });

    public YingYan(Context context) {
        this.context = context;
        initPara();        //初始化各项参数
        initListener();     //初始化监听器
        openTraceService(); //开启鹰眼轨迹服务
    }

    /***
     *初始化鹰眼服务各项参数
     *@return void
     *@author wenhaoz
     *created at 2019/3/3 12:37
     */
    public void initPara() {
        bitmapDescriptor = ImageUtil.setImage(context, R.drawable.car, 0.4f, 0.4f);
        markerList = new ArrayList<>();
        gatherInterval = 2;
        packInterval = 12;
        entityName = GetIMEI.getImei(context);
        serviceId = 209693;
        // 初始化轨迹服务
        mTrace = new Trace(serviceId, entityName, false);
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(context);
    }

    /***
     *初始化所有监听器
     *@return void
     *@author wenhaoz
     *created at 2019/3/10 18:09
     */
    private void initListener() {
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
                    //初次将当前的entity设备信息上传到鹰眼服务
                    upLoadMyLocation();
                    //查询当前鹰眼服务中的设备实时位置
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

            //实时轨迹上传回调函数
            @Override
            public void onAddPointCallback(AddPointResponse addPointResponse) {
                Log.d(TAG, "onAddPointCallback: " + addPointResponse.toString());
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

            //获得所有service管理下的设备位置信息
            @Override
            public void onEntityListCallback(EntityListResponse entityListResponse) {
                getEntitiesInfo(entityListResponse);
            }


            //添加当前设备的位置信息回调
            @Override
            public void onAddEntityCallback(AddEntityResponse addEntityResponse) {
                Log.d(TAG, "onAddEntityCallback: " + addEntityResponse.toString());
                //上传成功后随即开启线程更新设备的方向属性
                updateDirection();
            }
        };
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

    /***
     *获取同一个ServiceId下所有Entity设备位置列表
     *@param
     *@return
     */
    public void queryEntityList() {
        //创建请求
        EntityListRequest request = new EntityListRequest(1, serviceId);
        //进行筛选 获得司机列表
        FilterCondition filterCondition = new FilterCondition();
        Map<String, String> map = new HashMap<>();
        map.put("is_taxi", "taxi");
        filterCondition.setColumns(map);
        request.setFilterCondition(filterCondition);
        //发起请求
        mTraceClient.queryEntityList(request, entityListener);
    }

    /***
     *首次添加自己的entity实体信息至鹰眼服务
     *@return void
     *@author wenhaoz
     *created at 2019/3/10 19:18
     */
    private void upLoadMyLocation() {
        AddEntityRequest addEntityRequest = new AddEntityRequest();
        addEntityRequest.setTag(1);
        addEntityRequest.setServiceId(serviceId);
        addEntityRequest.setEntityName(entityName);
        Map<String, String> map = new HashMap();
        map.put("is_taxi", "taxi");
        addEntityRequest.setColumns(map);
        mTraceClient.addEntity(addEntityRequest, entityListener);
    }

    /***
     *更新当前设备方向信息
     *@param
     *@return
     *@author wenhaoz
     *created at 2019/3/10 19:22
     */
    private void updateDirection() {
        //上传轨迹点
        AddPointRequest pointRequest = new AddPointRequest();
        pointRequest.setEntityName(entityName);
        pointRequest.setServiceId(serviceId);
        pointRequest.setTag(1);
        com.baidu.trace.model.Point point = new com.baidu.trace.model.Point();
        com.baidu.trace.model.LatLng latLng = new com.baidu.trace.model.LatLng(MyLocationListener.location.getLatitude(), MyLocationListener.location.getLongitude());
        point.setLocation(latLng);
        point.setDirection((int) MyLocationListener.location.getDirection());
        point.setLocTime(System.currentTimeMillis() / 1000);
        pointRequest.setPoint(point);
        mTraceClient.addPoint(pointRequest, trackListener);
    }

    /***
     *从onEntityListCallback回调中提取信息
     *@return void
     *@author wenhaoz
     *created at 2019/3/11 22:33
     */
    private void getEntitiesInfo(EntityListResponse entityListResponse) {
        entities = entityListResponse.getEntities();
        //synchronized (this) {
        if (entities.size() > 0) {
            for (int i = 0; i < entities.size(); i++) {
                //if (!entities.get(i).getEntityName().equals(entityName)) {

                int finalI = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addOtherMarker(entities.get(finalI));
                    }
                }).start();

                //}
            }
            Message message = Message.obtain();
            message.what = QUERY_ENTITYLIST;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.sendMessage(message);
                }
            }, 3000);
            Log.d("onEntityListCallback", "当前Entity设备的数量" + entities.size());
        } else {
            Toast.makeText(context, "未查询到在线的小白", Toast.LENGTH_SHORT).show();
        }
        //}
    }

    /***
     *首次绘制Marker
     *@param  【latLng, direction]
     *@return void
     *@author wenhaoz
     *created at 2019/3/12 15:15
     */
    private void firstAddMarker(String entityName, LatLng latLng, int direction) {
        MarkerOptions options = new MarkerOptions().position(latLng).icon(bitmapDescriptor);
        Marker marker = (Marker) MainActivity.mBaidumap.addOverlay(options);
        Bundle bundle = new Bundle();
        bundle.putString("name", entityName);
        marker.setExtraInfo(bundle);
        marker.setRotate(-direction);
        markerList.add(marker);
    }

    /***
     *在地图上显示其他人的位置
     *@return void
     *@author wenhaoz
     *created at 2019/3/12 16:04
     */
    private void addOtherMarker(EntityInfo entityInfo) {
        //获得设备唯一标识符
        String entityName = entityInfo.getEntityName();

        //获得设备旋转方向
        int direction = entityInfo.getLatestLocation().getDirection();

        //获得设备经纬度及其转换
        com.baidu.trace.model.LatLng latLng = entityInfo.getLatestLocation().getLocation();
        LatLng latLngConvert = new LatLng(latLng.getLatitude(), latLng.getLongitude());


        //开始进行marker绘制工作且设置相应动画
        if (markerList.size() == 0) {
            firstAddMarker(entityName, latLngConvert, direction);
        } else {
            for (int i = 0; i < markerList.size(); i++) {

                Marker marker = markerList.get(i);

                if (marker.getExtraInfo().getString("name").equals(entityName)) {

                    float fromDegree = marker.getRotate();
                    float toDegree = Math.abs(360 - direction);

                    Looper.prepare();
                    if (fromDegree != toDegree) {
                        RotateAnimation rotateAnimation = new RotateAnimation(fromDegree, toDegree);
                        rotateAnimation.setDuration(2000);
                        marker.setAnimation(rotateAnimation);
                        marker.startAnimation();
                    }
                    if (!(latLngConvert.longitude == latLng.getLongitude() && latLngConvert.latitude == latLng.getLatitude())) {
                        Transformation transformation = new Transformation(latLngConvert);
                        transformation.setDuration(2000);
                        marker.setAnimation(transformation);
                        marker.startAnimation();
                    }
                    Looper.loop();

                } else {
                    //检测到新的设备再次添加
                    firstAddMarker(entityName, latLngConvert, direction);
                }
            }
        }
    }

}
