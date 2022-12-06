package com.geek.biz1.bean;

import java.io.Serializable;

public class FWechatJumpBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String appletPath;
    private String appletId;
    private String frontUrl;

    public FWechatJumpBean() {
    }

    public FWechatJumpBean(String appletPath, String appletId, String frontUrl) {
        this.appletPath = appletPath;
        this.appletId = appletId;
        this.frontUrl = frontUrl;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAppletPath() {
        return appletPath;
    }

    public void setAppletPath(String appletPath) {
        this.appletPath = appletPath;
    }

    public String getAppletId() {
        return appletId;
    }

    public void setAppletId(String appletId) {
        this.appletId = appletId;
    }

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }
}
