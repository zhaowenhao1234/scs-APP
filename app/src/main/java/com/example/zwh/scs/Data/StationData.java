package com.example.zwh.scs.Data;


import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * created at 2019/3/6 17:04 by wenhaoz
 */
public class StationData {
    public List<LatLng> stationMarker = null;

    LatLng latLng = null;

    //小白站点的固定位置
    //东门
    private double eastGateLong = 113.018875;
    private double eastGateLati = 28.074007;

    //西门
    private double westGateLong = 113.008644;
    private double westGateLati = 28.072517;

    //南门
    private double southGateLong = 113.018534;
    private double southGateLati = 28.069329;

    //汀香
    private double tingXiangLong = 113.013086;
    private double tingXiangLati = 28.072791;

    //至诚1
    private double zhiChengLong = 113.013791;
    private double zhiChengLati = 28.071341;


    //工科一楼
    private double gongKeLong = 113.015691;
    private double gongKeLati = 28.070066;

    //文科
    private double artsLong = 113.019482;
    private double artsLati = 28.071524;

    //大会堂
    private double hallLong = 113.020039;
    private double hallLati = 28.072556;

    //行健轩东
    private double xingJianLong = 113.017222;
    private double xingJianLati = 28.076003;

    //甘怡园
    private double ganYiLong = 113.016252;
    private double ganYiLati = 28.075991;

    //学活
    private double xueHuoLong = 113.014011;
    private double xueHuoLati = 28.075329;


    public static String[] name={"东门","西门","南门","汀香","至诚轩一栋","工科一楼","文科楼"
            ,"大会堂","行健轩东","甘怡园","学生活动中心"};
    public static String[] add={"长沙理工大学云塘校区东门",
            "长沙理工大学云塘校区西门"
            ,"长沙理工大学云塘校区南门"
            ,"长沙理工大学云塘校区汀香餐厅"
            ,"长沙理工大学云塘校区至诚轩一栋楼"
            ,"长沙理工大学云塘校区工科一楼"
            ,"长沙理工大学云塘校区文科楼"
            ,"长沙理工大学云塘校区大会堂"
            ,"长沙理工大学云塘校区行健轩南约200米东"
            ,"长沙理工大学云塘校区甘怡园餐厅"
            ,"长沙理工大学云塘校区学生活动中心"};
    public StationData() {
        stationMarker = new ArrayList<>();
        //添加停车站点信息
        addStationData();
    }


    private void addStationData() {
        latLng = new LatLng(eastGateLati, eastGateLong);
        stationMarker.add(latLng);

        latLng = new LatLng(westGateLati, westGateLong);
        stationMarker.add(latLng);

        latLng = new LatLng(southGateLati, southGateLong);
        stationMarker.add(latLng);

        latLng = new LatLng(tingXiangLati, tingXiangLong);
        stationMarker.add(latLng);

        latLng = new LatLng(zhiChengLati, zhiChengLong);
        stationMarker.add(latLng);

        latLng = new LatLng(gongKeLati, gongKeLong);
        stationMarker.add(latLng);

        latLng = new LatLng(artsLati, artsLong);
        stationMarker.add(latLng);

        latLng = new LatLng(hallLati, hallLong);
        stationMarker.add(latLng);

        latLng = new LatLng(xingJianLati, xingJianLong);
        stationMarker.add(latLng);

        latLng = new LatLng(ganYiLati, ganYiLong);
        stationMarker.add(latLng);

        latLng = new LatLng(xueHuoLati, xueHuoLong);
        stationMarker.add(latLng);
    }

}
