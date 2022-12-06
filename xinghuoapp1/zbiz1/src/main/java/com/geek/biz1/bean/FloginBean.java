package com.geek.biz1.bean;

import java.io.Serializable;

public class FloginBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String token;
    private String url;

    public FloginBean() {
    }

    public FloginBean(String token, String url) {
        this.token = token;
        this.url = url;
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
}
