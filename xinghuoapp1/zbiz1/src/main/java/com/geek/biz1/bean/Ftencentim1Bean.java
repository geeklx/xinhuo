package com.geek.biz1.bean;

import java.io.Serializable;

public class Ftencentim1Bean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String expireTime;
    private String userSign;
    private String sdkAppId;
    private String secretKey;
    private String hwPushBuzId;
    private String hwPushAppId;
    private String xmPushBuzId;
    private String xmPushAppId;
    private String xmPushAppKey;

    public Ftencentim1Bean() {
    }

    public Ftencentim1Bean(String userId, String expireTime, String userSign, String sdkAppId, String secretKey, String hwPushBuzId, String hwPushAppId, String xmPushBuzId, String xmPushAppId, String xmPushAppKey) {
        this.userId = userId;
        this.expireTime = expireTime;
        this.userSign = userSign;
        this.sdkAppId = sdkAppId;
        this.secretKey = secretKey;
        this.hwPushBuzId = hwPushBuzId;
        this.hwPushAppId = hwPushAppId;
        this.xmPushBuzId = xmPushBuzId;
        this.xmPushAppId = xmPushAppId;
        this.xmPushAppKey = xmPushAppKey;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }

    public String getSdkAppId() {
        return sdkAppId;
    }

    public void setSdkAppId(String sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getHwPushBuzId() {
        return hwPushBuzId;
    }

    public void setHwPushBuzId(String hwPushBuzId) {
        this.hwPushBuzId = hwPushBuzId;
    }

    public String getHwPushAppId() {
        return hwPushAppId;
    }

    public void setHwPushAppId(String hwPushAppId) {
        this.hwPushAppId = hwPushAppId;
    }

    public String getXmPushBuzId() {
        return xmPushBuzId;
    }

    public void setXmPushBuzId(String xmPushBuzId) {
        this.xmPushBuzId = xmPushBuzId;
    }

    public String getXmPushAppId() {
        return xmPushAppId;
    }

    public void setXmPushAppId(String xmPushAppId) {
        this.xmPushAppId = xmPushAppId;
    }

    public String getXmPushAppKey() {
        return xmPushAppKey;
    }

    public void setXmPushAppKey(String xmPushAppKey) {
        this.xmPushAppKey = xmPushAppKey;
    }
}
