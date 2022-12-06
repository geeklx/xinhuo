package com.geek.biz1.bean;

import java.io.Serializable;

public class Fytxsphy1Bean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String appToken;
    private String appId;
    private String userName;
    private String userId;

    public Fytxsphy1Bean() {
    }

    public Fytxsphy1Bean(String appToken, String appId, String userName, String userId) {
        this.appToken = appToken;
        this.appId = appId;
        this.userName = userName;
        this.userId = userId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
