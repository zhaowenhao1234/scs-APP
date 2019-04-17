package com.example.zwh.scs.Util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.zwh.scs.Activity.MainActivity;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.overlayutil.BikingRouteOverlay;


/**
 * 路线规划页
 */

public class RoutePlanUtil {
    private RoutePlanSearch mSearch;
    private Context context;
    private MyBikingRouteOverlay overlay = null;
    private LatLng st;
    private LatLng en;

    public RoutePlanUtil(Context context) {
        this.context = context;
    }


    public void start(LatLng st, LatLng en) {
        this.st = st;
        this.en = en;
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(listener);
        PlanNode stNode = PlanNode.withLocation(st);
        PlanNode enNode = PlanNode.withLocation(en);


        //通过设ridingType，可以区分普通自行车，和电动车线路
        //ridingType(int ridingType)
        mSearch.bikingSearch((new BikingRoutePlanOption()).from(stNode).to(enNode).ridingType(0));


    }

    OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        //获取普通骑行路规划结果
        @Override
        public void onGetBikingRouteResult(BikingRouteResult result) {
            int option = UserInfoUtil.getCurrentInfoUserMode(context);
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                MainActivity.locationClient.stop();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                SuggestAddrInfo suggestAddrInfo = result.getSuggestAddrInfo();
                Toast.makeText(context, "你好，请检查起点或终点信息，建议输入起点为：" + suggestAddrInfo.getSuggestStartNode() + "建议输入起点为：" + suggestAddrInfo.getSuggestEndNode(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                if (result.getRouteLines().size() >= 1) {
                    overlay = new MyBikingRouteOverlay(MainActivity.mBaidumap);
                    MainActivity.mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();

                    //司机
                    if (option == 1) {
                        UploadUtil.uploadRoute(context, st, en);
                    }
                } else {
                    Log.d("route result", "结果数<0");
                    return;
                }
            }
        }

    };


    boolean useDefaultIcon = false;//使用默认ICON


    //自行车
    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    public void removeAllOverlay() {
        overlay.removeFromMap();
    }

}