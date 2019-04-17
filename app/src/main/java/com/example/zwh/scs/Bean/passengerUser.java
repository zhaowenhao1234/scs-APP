package com.example.zwh.scs.Bean;

import com.google.gson.Gson;

/**
 * created at 2019/3/17 13:43 by wenhaoz
 */
//乘客用户信息类
public class PassengerUser {


    /**
     * code : 0
     * user : {"id":4,"nickName":"赵文浩","password":"VQ4br+B3/wsLZ/TjLynXUQ==","sex":1,"grade":"大二","schoolNum":"201716080125","startLongtitude":null,"startLatitude":null,"rtLongtitude":null,"rtLatitude":null,"creditScore":null,"avator":null}
     */

    private int code;
    private UserBean user;

    public static PassengerUser objectFromData(String str) {

        return new Gson().fromJson(str, PassengerUser.class);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {
        /**
         * id : 4
         * nickName : 赵文浩
         * password : VQ4br+B3/wsLZ/TjLynXUQ==
         * sex : 1
         * grade : 大二
         * schoolNum : 201716080125
         * startLongtitude : null
         * startLatitude : null
         * rtLongtitude : null
         * rtLatitude : null
         * creditScore : null
         * avator : null
         */

        private int id;
        private String nickName;
        private String password;
        private int sex;
        private String grade;
        private String schoolNum;
        private Object startLongtitude;
        private Object startLatitude;
        private Object rtLongtitude;
        private Object rtLatitude;
        private Object creditScore;
        private Object avator;

        public static UserBean objectFromData(String str) {

            return new Gson().fromJson(str, UserBean.class);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getSchoolNum() {
            return schoolNum;
        }

        public void setSchoolNum(String schoolNum) {
            this.schoolNum = schoolNum;
        }

        public Object getStartLongtitude() {
            return startLongtitude;
        }

        public void setStartLongtitude(Object startLongtitude) {
            this.startLongtitude = startLongtitude;
        }

        public Object getStartLatitude() {
            return startLatitude;
        }

        public void setStartLatitude(Object startLatitude) {
            this.startLatitude = startLatitude;
        }

        public Object getRtLongtitude() {
            return rtLongtitude;
        }

        public void setRtLongtitude(Object rtLongtitude) {
            this.rtLongtitude = rtLongtitude;
        }

        public Object getRtLatitude() {
            return rtLatitude;
        }

        public void setRtLatitude(Object rtLatitude) {
            this.rtLatitude = rtLatitude;
        }

        public Object getCreditScore() {
            return creditScore;
        }

        public void setCreditScore(Object creditScore) {
            this.creditScore = creditScore;
        }

        public Object getAvator() {
            return avator;
        }

        public void setAvator(Object avator) {
            this.avator = avator;
        }
    }
}
