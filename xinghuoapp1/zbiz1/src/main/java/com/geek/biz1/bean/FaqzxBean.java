package com.geek.biz1.bean;

import java.io.Serializable;

public class FaqzxBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String telphone;
    private String hostmail;
    private String landline;
    private String isfinger;

    public FaqzxBean() {
    }

    public FaqzxBean(String telphone, String hostmail, String landline, String isfinger) {
        this.telphone = telphone;
        this.hostmail = hostmail;
        this.landline = landline;
        this.isfinger = isfinger;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getHostmail() {
        return hostmail;
    }

    public void setHostmail(String hostmail) {
        this.hostmail = hostmail;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getIsfinger() {
        return isfinger;
    }

    public void setIsfinger(String isfinger) {
        this.isfinger = isfinger;
    }
}
