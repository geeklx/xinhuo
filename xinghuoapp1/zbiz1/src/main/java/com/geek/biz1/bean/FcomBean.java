package com.geek.biz1.bean;

import java.io.Serializable;

public class FcomBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String token;
    private String url;
    private String authorizedType;

    public FcomBean() {
    }

    public FcomBean(String token, String url, String authorizedType) {
        this.token = token;
        this.url = url;
        this.authorizedType = authorizedType;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthorizedType() {
        return authorizedType;
    }

    public void setAuthorizedType(String authorizedType) {
        this.authorizedType = authorizedType;
    }
}
