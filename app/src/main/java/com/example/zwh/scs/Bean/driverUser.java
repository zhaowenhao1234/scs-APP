package com.example.zwh.scs.Bean;

/**
 * created at 2019/3/17 13:41 by wenhaoz
 */
//司机信息表
public class driverUser {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Object getPhoto() {
        return photo;
    }

    public void setPhoto(Object photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public Object getDriver_license() {
        return driver_license;
    }

    public void setDriver_license(Object driver_license) {
        this.driver_license = driver_license;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public int getEmpty_num() {
        return empty_num;
    }

    public void setEmpty_num(int empty_num) {
        this.empty_num = empty_num;
    }

    public Object getPic() {
        return pic;
    }

    public void setPic(Object pic) {
        this.pic = pic;
    }

    public boolean isIs_online() {
        return is_online;
    }

    public void setIs_online(boolean is_online) {
        this.is_online = is_online;
    }

    public String getRealtime_longtitude() {
        return realtime_longtitude;
    }

    public void setRealtime_longtitude(String realtime_longtitude) {
        this.realtime_longtitude = realtime_longtitude;
    }

    public String getRealtime_latitude() {
        return realtime_latitude;
    }

    public void setRealtime_latitude(String realtime_latitude) {
        this.realtime_latitude = realtime_latitude;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    private int id;         //id
    public int sex;        //性别
    public Object photo;   //照片
    public String name;    //姓名
    private String password;//密码
    private String phone_number;//手机号
    private String id_card; //身份证号
    private Object driver_license;//驾驶证首页
    public int car_id;     //车号
    public int empty_num;  //车辆空位
    public Object pic;     //车辆图片
    public boolean is_online;//是否在线
    private String realtime_longtitude;//实施位置经度
    private String realtime_latitude;   //实时位置纬度
    private String level;   //司机星级

}
