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
import com.baidu.trace.api.entity.UpdateEntityRequest;
import com.baidu.trace.api.entity.UpdateEntityResponse;
import com.baidu.trace.api.track.AddPointRequest;
import com.baidu.trace.api.track.AddPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.Point;
import com.baidu.trace.model.ProtocolType;
import com.baidu.trace.model.PushMessage;
import com.example.zwh.scs.Activity.MainActivity;
import com.example.zwh.scs.Listener.MyLocationListener;
import com.example.zwh.scs.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created at 2019/3/3 13:15 by wenhaoz
 */
public class YingYan {

    private final String TAG = YingYan.class.getSimpleName();
    private final int QUERY_SUCCESS = 0;
    private final int QUERY_ENTITYLIST = 1;//查询指令
    private final int UPDATE_DIRECTION = 2;
    //上下文
    private Context context;
    private ExecutorService cachedThreadPool = null;

    //鹰眼服务监听器
    public OnTraceListener mTraceListener;
    public OnEntityListener entityListener;
    public OnTrackListener trackListener;

    //鹰眼轨迹相关参数
    private String entityName;  // entity标识
    private long serviceId;     //鹰眼服务ID

    public Trace mTrace = null;                //鹰眼服务
    public LBSTraceClient mTraceClient = null;  //实例化轨迹服务客户端
    private List<EntityInfo> entities = null;   //实体信息存储列表
    private List<Marker> markerList = null;     //marker存储列表


    private Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_ENTITYLIST:
                    queryEntityList();
                    break;
                case UPDATE_DIRECTION:
                    updateDirection();
                    break;
            }
            return false;
        }
    });

    //构造函数
    public YingYan(Context context) {
        this.context = context;
        initListener();     //初始化监听器
        initPara();        //初始化各项参数
    }

    /***
     *初始化鹰眼服务各项参数
     *@return void
     *@author wenhaoz
     *created at 2019/3/3 12:37
     */
    public void initPara() {
        int gatherInterval = 2;//位置采集周期 (s)
        int packInterval = 10;//打包周期 (s)
        cachedThreadPool = Executors.newCachedThreadPool();//绘制Marker的线程池
        markerList = new ArrayList<>();                    //marker实例集合
        entities = new ArrayList<>();                      //实体信息集合
        entityName = GetIMEI.getImei(context);             //实体名称
        serviceId = 209693;                                //鹰眼服务ID

        //实例化轨迹服务
        mTrace = new Trace(serviceId, entityName, false);
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(context);
        //设置位置采集和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);
        //设置协议类型 http 或 https
        mTraceClient.setProtocolType(ProtocolType.HTTP);
        // 开启服务
        mTraceClient.startTrace(mTrace, mTraceListener);
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

            }

            @Override
            public void onStartTraceCallback(int i, String s) {
                //当启动服务成功时，开启位置收集服务
                if (i == QUERY_SUCCESS) {
                    mTraceClient.startGather(this);
                }
            }

            @Override
            public void onStopTraceCallback(int i, String s) {

            }

            @Override
            public void onStartGatherCallback(int i, String s) {
                if (i == QUERY_SUCCESS) {
                    //初次将当前的entity设备信息上传到鹰眼服务
                    upLoadMyEntity();

                }
                //开启子线程实时更新方向
                updateDirection();
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
                entities = entityListResponse.getEntities();
                handleEntitiesInfo();

                Message message = Message.obtain();
                message.what = QUERY_ENTITYLIST;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendMessage(message);
                    }
                }, 2000);
            }


            //添加当前设备的位置信息回调
            @Override
            public void onAddEntityCallback(AddEntityResponse addEntityResponse) {
                if (addEntityResponse.status == 3005) {
                    updateEntity();
                }
                queryEntityList();
            }

            @Override
            public void onUpdateEntityCallback(UpdateEntityResponse updateEntityResponse) {
                //查询当前鹰眼服务中的设备实时位置
                queryEntityList();
            }
        };
    }

    /***
     *获取同一个ServiceId下所有Entity设备位置列表
     *@param
     *@return
     */
    public void queryEntityList() {
        //创建请求
        EntityListRequest request = new EntityListRequest(entityName.hashCode(), serviceId);
        //进行筛选 获得司机列表
        FilterCondition filterCondition = new FilterCondition();
        HashMap<String, String> map = new HashMap<>();
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
    private void upLoadMyEntity() {
        AddEntityRequest addEntityRequest = new AddEntityRequest();
        addEntityRequest.setTag(entityName.hashCode());
        addEntityRequest.setServiceId(serviceId);
        addEntityRequest.setEntityName(entityName);
        Map<String, String> map = new HashMap();
        map.put("is_taxi", "taxi");
        addEntityRequest.setColumns(map);
        mTraceClient.addEntity(addEntityRequest, entityListener);
    }

    /***
     *更新设备信息
     *@return void
     *@author wenhaoz
     *created at 2019/3/12 19:29
     */
    private void updateEntity() {
        UpdateEntityRequest updateEntityRequest = new UpdateEntityRequest();
        updateEntityRequest.setTag(entityName.hashCode());
        updateEntityRequest.setServiceId(serviceId);
        updateEntityRequest.setEntityName(entityName);
        Map<String, String> map = new HashMap();
        map.put("is_taxi", "taxi");
        updateEntityRequest.setColumns(map);
        mTraceClient.updateEntity(updateEntityRequest, entityListener);
    }

    /***
     *更新当前设备方向信息
     *@param
     *@return
     *@author wenhaoz
     *created at 2019/3/10 19:22
     */
    private void updateDirection() {
        Point point = new Point();
        com.baidu.trace.model.LatLng latLng = new com.baidu.trace.model.LatLng();

        //创建AddPointRequest对象，准备添加实时轨迹点
        AddPointRequest pointRequest = new AddPointRequest();
        pointRequest.setEntityName(entityName);
        pointRequest.setServiceId(serviceId);
        pointRequest.setTag(entityName.hashCode());

        //创建经纬度对象
        latLng.setLatitude(MyLocationListener.location.getLatitude());
        latLng.setLongitude(MyLocationListener.location.getLongitude());

        //封装轨迹点信息
        point.setLocation(latLng);
        point.setDirection((int) MyLocationListener.location.getDirection());
        point.setLocTime(System.currentTimeMillis() / 1000);
        pointRequest.setPoint(point);

        //上传轨迹点
        mTraceClient.addPoint(pointRequest, trackListener);

        //每一秒钟更新一次轨迹点
        Message message = Message.obtain();
        message.what = UPDATE_DIRECTION;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendMessage(message);
            }
        }, 1000);
    }

    /***
     *从onEntityListCallback回调中提取信息并做出绘制marker处理
     *@return void
     *@author wenhaoz
     *created at 2019/3/11 22:33
     */
    private void handleEntitiesInfo() {

        if (entities.size() > 0) {
            for (int i = 0; i < entities.size(); i++) {
                EntityInfo entityInfo = entities.get(i);

                //获得设备唯一标识符
                String entityName = entityInfo.getEntityName();

                //获得设备旋转方向
                int direction = entityInfo.getLatestLocation().getDirection();

                //获得设备经纬度及其转换后的经纬度
                com.baidu.trace.model.LatLng latLng = entityInfo.getLatestLocation().getLocation();
                LatLng latLngConvert = new LatLng(latLng.getLatitude(), latLng.getLongitude());

                //执行绘制线程
                cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        startMarkerAnimation(entityName, latLngConvert, direction);
                    }
                });
            }
        } else {
            Toast.makeText(context, "未查询到在线的小白", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     *首次绘制Marker
     *@param  【latLng, direction]
     *@return void
     *@author wenhaoz
     *created at 2019/3/12 15:15
     */
    private void addMarker(String entityName, LatLng latLng, int direction) {
        BitmapDescriptor bitmapDescriptor = ImageUtil.setImage(context, R.drawable.car, 0.4f, 0.4f);
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
    private void startMarkerAnimation(String entityName, LatLng latLngConvert, int direction) {

        if (markerList.size() == 0) {
            addMarker(entityName, latLngConvert, direction);
        } else {
            boolean isExist = false;
            for (int i = 0; i < markerList.size(); i++) {

                Marker marker = markerList.get(i);

                if (marker.getExtraInfo().getString("name").equals(entityName)) {
                    isExist = true;

                    //获得旋转角度
                    float fromDegree = marker.getRotate();
                    float toDegree = 360 - direction;

                    //若旋转角度之差小于等于九十度则设置动画
                    if (Math.abs(fromDegree - toDegree) <= 90) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                RotateAnimation rotateAnimation = new RotateAnimation(fromDegree, toDegree);
                                rotateAnimation.setDuration(2000);
                                marker.setAnimation(rotateAnimation);
                                marker.startAnimation();
                            }
                        });
                    }

                    //若当前的位置并没有发生改变则不进行绘制
                    if ((!latLngConvert.equals(marker.getPosition()))) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Transformation transformation = new Transformation(latLngConvert);
                                transformation.setDuration(2000);
                                marker.setAnimation(transformation);
                            }
                        });
                    }
                }
            }
            if (!isExist) {
                addMarker(entityName, latLngConvert, direction);
            }
        }
    }

}
