package com.geek.biz1.bean;

import java.io.Serializable;
import java.util.List;

public class FBannerBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<BjyyBeanYewu3> data;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<BjyyBeanYewu3> getData() {
        return data;
    }

    public void setData(List<BjyyBeanYewu3> data) {
        this.data = data;
    }
}
