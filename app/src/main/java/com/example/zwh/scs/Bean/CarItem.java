package com.example.zwh.scs.Bean;




/**
 * created at 2019/3/23 14:17 by wenhaoz
 */
public class CarItem {


    /**
     * id : 1
     * name : zzz
     * password : /KB4nniRy8BYMpiiODFhIg==
     * idCard : null
     * carId : null
     * emptyNum : null
     * isOnline : null
     * rlLongtitude : null
     * rlLatitude : null
     * level : null
     */

    private int id;
    private String name;
    private String password;
    private String phoneNum;
    private Object idCard;
    private Object carId;
    private Object emptyNum;
    private Object isOnline;
    private Object rlLongtitude;
    private Object rlLatitude;
    private Object level;

    public CarItem (int id,String name,String password,Object idCard,Object carId,Object emptyNum,Object isOnline,Object rlLongtitude,Object rlLatitude,Object level) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.idCard = idCard;
        this.carId = carId;
        this.emptyNum = emptyNum;
        this.isOnline = isOnline;
        this.rlLongtitude =rlLongtitude;
        this.rlLatitude =rlLatitude;
        this.level =level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Object getIdCard() {
        return idCard;
    }

    public void setIdCard(Object idCard) {
        this.idCard = idCard;
    }

    public Object getCarId() {
        return carId;
    }

    public void setCarId(Object carId) {
        this.carId = carId;
    }

    public Object getEmptyNum() {
        return emptyNum;
    }

    public void setEmptyNum(Object emptyNum) {
        this.emptyNum = emptyNum;
    }

    public Object getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Object isOnline) {
        this.isOnline = isOnline;
    }

    public Object getRlLongtitude() {
        return rlLongtitude;
    }

    public void setRlLongtitude(Object rlLongtitude) {
        this.rlLongtitude = rlLongtitude;
    }

    public Object getRlLatitude() {
        return rlLatitude;
    }

    public void setRlLatitude(Object rlLatitude) {
        this.rlLatitude = rlLatitude;
    }

    public Object getLevel() {
        return level;
    }

    public void setLevel(Object level) {
        this.level = level;
    }

}
