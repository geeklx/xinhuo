package com.geek.biz1.bean;

import java.io.Serializable;

public class FconfigBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String identity;
    private String serverType;

    public FconfigBean() {
    }

    public FconfigBean(String identity, String serverType) {
        this.identity = identity;
        this.serverType = serverType;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
}
