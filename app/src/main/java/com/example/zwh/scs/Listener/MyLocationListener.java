package com.example.zwh.scs.Listener;

import android.content.Context;
import android.media.Image;
import android.service.notification.ConditionProviderService;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.zwh.scs.Activity.MainActivity;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.ImageUtil;


/**
 * created at 2019/2/25 20:51 by wenhaoz
 */


public class MyLocationListener extends BDAbstractLocationListener {
    private static final String TAG = "MyLocationListener".getClass().getSimpleName();
    public static BDLocation location = null;
    private boolean isFirst;
    private MyLocationConfiguration mConfig = null;
    private BitmapDescriptor bitmapDescriptor = null;
    private Context context = null;

    public MyLocationListener(Context context) {
        super();
        this.context = context;
        bitmapDescriptor = ImageUtil.setImage(context, R.drawable.my_location,0.5f,0.5f);
        isFirst = true;
    }


    /***
     *BaiduListener 监听回调用户位置
     *@return void
     *@author wenhaoz
     *created at 2019/3/2 18:27
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        this.location = bdLocation;
        Log.d(TAG, "onReceiveLocation: "+bdLocation.getLocTypeDescription());
        //如果位置为空的话变现实北京地图
        if (bdLocation.getAdCode() == null) {
            LatLng latLng = new LatLng(39.914935, 116.403694);
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(latLng, 17);
            MainActivity.mBaidumap.animateMapStatus(update);
        } else {

            //得到当前位置描述
            getMyAddressDescription();
            //获得我的数据信息
            MyLocationData locData = new MyLocationData.Builder().accuracy(bdLocation.getRadius()).latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
            //封装经纬度
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            //设置以改经纬度为中心的，同时设定地图缩放比例
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 17);
            //在地图上设置我的位置数据
            MainActivity.mBaidumap.setMyLocationData(locData);

            if (isFirst) {
                //设置定位模式
                mConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDescriptor);
                MainActivity.mBaidumap.setMyLocationConfiguration(mConfig);
                //地图缩放行为
                MainActivity.mBaidumap.animateMapStatus(u);
                isFirst = false;
            }
        }
    }


    /***
     *得到用户当前位置信息
     *@return void
     *@author wenhaoz
     *created at 2019/3/2 18:21
     */
    public String getMyAddressDescription() {
        String type = location.getLocationDescribe();//获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
        String addr = location.getAddrStr(); //获取详细地址信息
        Log.d(TAG, "onReceiveLocation: " + addr + "  " + type + "方向" + location.getDirection());
        return addr;
    }


    /***
     *在地图上绘制蓝色点标记
     *@return com.baidu.mapapi.map.MapStatusUpdate
     *@author wenhaoz
     *created at 2019/3/2 18:22
     */
    @NonNull
    public void showMyLocaton(BDLocation bdLocation) {
        if(bdLocation != null){
            //获得我的数据信息
            MyLocationData locData = new MyLocationData.Builder().accuracy(bdLocation.getRadius()).latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
            //封装经纬度
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            //设置以改经纬度为中心的，同时设定地图缩放比例
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 17);
            //在地图上设置我的位置数据
            MainActivity.mBaidumap.setMyLocationData(locData);
            //设置定位模式
            mConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDescriptor);
            MainActivity.mBaidumap.setMyLocationConfiguration(mConfig);
            //地图缩放行为
            MainActivity.mBaidumap.animateMapStatus(u);
        }else{
            Toast.makeText(context,"请检查是否已开启网络连接且是否开始GPS",Toast.LENGTH_SHORT).show();
        }

    }


}
