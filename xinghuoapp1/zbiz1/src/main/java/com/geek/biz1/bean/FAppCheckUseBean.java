package com.geek.biz1.bean;

import java.io.Serializable;

public class FAppCheckUseBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String address;
    private String maintainMessage;
    private String maintainTitle;
    private boolean maintain;
    private String maintainBackgroundUrl;
    private String xz_token;

    public String getXz_token() {
        return xz_token;
    }

    public void setXz_token(String xz_token) {
        this.xz_token = xz_token;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMaintainMessage() {
        return maintainMessage;
    }

    public void setMaintainMessage(String maintainMessage) {
        this.maintainMessage = maintainMessage;
    }

    public String getMaintainTitle() {
        return maintainTitle;
    }

    public void setMaintainTitle(String maintainTitle) {
        this.maintainTitle = maintainTitle;
    }

    public String getMaintainBackgroundUrl() {
        return maintainBackgroundUrl;
    }

    public void setMaintainBackgroundUrl(String maintainBackgroundUrl) {
        this.maintainBackgroundUrl = maintainBackgroundUrl;
    }

    public boolean isMaintain() {
        return maintain;
    }

    public void setMaintain(boolean maintain) {
        this.maintain = maintain;
    }
}
