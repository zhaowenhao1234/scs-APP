package com.example.zwh.scs.model;

/**
 * created at 2019/3/17 13:43 by wenhaoz
 */
//乘客用户信息类
public class passengerUser {
    private int id;         //id
    private int sex;        //性别
    public Object avatar;  //头像
    public String nick_name;//昵称
    private String password;//密码
    private String phone_number;//手机号
    private String start_longtitude;//其实位置经度
    private String start_latitude;//起始位置纬度
    private String realtime_longtitude;//实时位置经度
    private String realtime_latitude;//实时位置纬度
    public int credit_score;//信用积分

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

    public Object getAvatar() {
        return avatar;
    }

    public void setAvatar(Object avatar) {
        this.avatar = avatar;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
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

    public String getStart_longtitude() {
        return start_longtitude;
    }

    public void setStart_longtitude(String start_longtitude) {
        this.start_longtitude = start_longtitude;
    }

    public String getStart_latitude() {
        return start_latitude;
    }

    public void setStart_latitude(String start_latitude) {
        this.start_latitude = start_latitude;
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

    public int getCredit_score() {
        return credit_score;
    }

    public void setCredit_score(int credit_score) {
        this.credit_score = credit_score;
    }




}
