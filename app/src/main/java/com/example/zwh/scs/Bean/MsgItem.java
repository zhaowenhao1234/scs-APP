package com.example.zwh.scs.Bean;

/**
 * desc
 * author 杨肇鹏
 * created on 2019/3/19 16:41
 */
public class MsgItem {
    private String id;
    private String driverId;
    private String userId;
    private String time;
    private String contents;
    private String pic;

    public MsgItem(String id, String driverId, String userId, String time, String contents, String pic) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
        this.time = time;
        this.contents = contents;
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

}
