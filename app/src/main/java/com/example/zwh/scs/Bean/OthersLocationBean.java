package com.example.zwh.scs.Bean;

import java.util.List;

/**
 * created at 2019/2/27 18:12 by wenhaoz
 */
public class OthersLocationBean {

    /**
     * code : 100
     * msg : 处理成功！
     * extend : {"ListOfUser":[{"userId":1,"userName":"tt","password":"2134","userPosition":"113.01753 28.069279"}]}
     */

    private int code;
    private String msg;
    private ExtendBean extend;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ExtendBean getExtend() {
        return extend;
    }

    public void setExtend(ExtendBean extend) {
        this.extend = extend;
    }

    public static class ExtendBean {
        private List<ListOfUserBean> ListOfUser;


        public List<ListOfUserBean> getListOfUser() {
            return ListOfUser;
        }

        public void setListOfUser(List<ListOfUserBean> ListOfUser) {
            this.ListOfUser = ListOfUser;
        }

        public static class ListOfUserBean {
            /**
             * userId : 1
             * userName : tt
             * password : 2134
             * userPosition : 113.01753 28.069279
             */

            private int userId;
            private String userName;
            private String password;
            private String userPosition;


            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getUserPosition() {
                return userPosition;
            }

            public void setUserPosition(String userPosition) {
                this.userPosition = userPosition;
            }
        }
    }
}
