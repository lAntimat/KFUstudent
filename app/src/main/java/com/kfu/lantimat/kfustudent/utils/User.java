package com.kfu.lantimat.kfustudent.utils;

/**
 * Created by lAntimat on 27.02.2018.
 */

public class User {
    String id;
    String fullName;
    String bornDate;
    String institude;
    String group;
    String imgUrl;

    public User() {
    }

    public User(String id, String fullName, String bornDate, String institude, String group, String imgUrl) {
        this.id = id;
        this.fullName = fullName;
        this.bornDate = bornDate;
        this.institude = institude;
        this.group = group;
        this.imgUrl = imgUrl;
    }

    public String getBornDate() {
        return bornDate;
    }

    public void setBornDate(String bornDate) {
        this.bornDate = bornDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInstitude() {
        return institude;
    }

    public void setInstitude(String institude) {
        this.institude = institude;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
