package com.geek.biz1.bean;

import java.io.Serializable;

public class Fytxim2Bean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String secretKey;
    private String userId;
    private String sdkAppId;
    private String corpId;

    public Fytxim2Bean() {
    }

    public Fytxim2Bean(String secretKey, String userId, String sdkAppId, String corpId) {
        this.secretKey = secretKey;
        this.userId = userId;
        this.sdkAppId = sdkAppId;
        this.corpId = corpId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSdkAppId() {
        return sdkAppId;
    }

    public void setSdkAppId(String sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }
}
