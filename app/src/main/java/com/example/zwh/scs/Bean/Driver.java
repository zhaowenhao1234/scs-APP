package com.example.zwh.scs.Bean;

import com.google.gson.Gson;

/**
 * created at 2019/4/15 19:25 by wenhaoz
 */
public class Driver {

    /**
     * code : 0
     * driver : {"id":1,"name":"zzz","password":"/KB4nniRy8BYMpiiODFhIg==","phoneNum":"13033087352","idCard":"430223199901011234","carId":7,"emptyNum":9,"isOnline":true,"rlLongtitude":"123.312","rlLatitude":"123.321","endLongtitude":"321.312","endLatitude":"132.322","level":null,"photo":"","driverLicense":"","pic":"","online":true}
     */

    private int code;
    private DriverBean driver;

    public static Driver objectFromData(String str) {

        return new Gson().fromJson(str, Driver.class);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DriverBean getDriver() {
        return driver;
    }

    public void setDriver(DriverBean driver) {
        this.driver = driver;
    }

    public static class DriverBean {
        /**
         * id : 1
         * name : zzz
         * password : /KB4nniRy8BYMpiiODFhIg==
         * phoneNum : 13033087352
         * idCard : 430223199901011234
         * carId : 7
         * emptyNum : 9
         * isOnline : true
         * rlLongtitude : 123.312
         * rlLatitude : 123.321
         * endLongtitude : 321.312
         * endLatitude : 132.322
         * level : null
         * photo :
         * driverLicense :
         * pic :
         * online : true
         */

        private int id;
        private String name;
        private String password;
        private String phoneNum;
        private String idCard;
        private int carId;
        private int emptyNum;
        private boolean isOnline;
        private String rlLongtitude;
        private String rlLatitude;
        private String endLongtitude;
        private String endLatitude;
        private Object level;
        private String photo;
        private String driverLicense;
        private String pic;
        private boolean online;

        public static DriverBean objectFromData(String str) {

            return new Gson().fromJson(str, DriverBean.class);
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

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public int getCarId() {
            return carId;
        }

        public void setCarId(int carId) {
            this.carId = carId;
        }

        public int getEmptyNum() {
            return emptyNum;
        }

        public void setEmptyNum(int emptyNum) {
            this.emptyNum = emptyNum;
        }

        public boolean isIsOnline() {
            return isOnline;
        }

        public void setIsOnline(boolean isOnline) {
            this.isOnline = isOnline;
        }

        public String getRlLongtitude() {
            return rlLongtitude;
        }

        public void setRlLongtitude(String rlLongtitude) {
            this.rlLongtitude = rlLongtitude;
        }

        public String getRlLatitude() {
            return rlLatitude;
        }

        public void setRlLatitude(String rlLatitude) {
            this.rlLatitude = rlLatitude;
        }

        public String getEndLongtitude() {
            return endLongtitude;
        }

        public void setEndLongtitude(String endLongtitude) {
            this.endLongtitude = endLongtitude;
        }

        public String getEndLatitude() {
            return endLatitude;
        }

        public void setEndLatitude(String endLatitude) {
            this.endLatitude = endLatitude;
        }

        public Object getLevel() {
            return level;
        }

        public void setLevel(Object level) {
            this.level = level;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getDriverLicense() {
            return driverLicense;
        }

        public void setDriverLicense(String driverLicense) {
            this.driverLicense = driverLicense;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public boolean isOnline() {
            return online;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }
    }
}
