package com.geek.biz1.bean;

import java.io.Serializable;
import java.util.List;

public class BjyyBeanYewu4 implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<BjyyBeanYewu3> data;

    public BjyyBeanYewu4() {
    }

    public BjyyBeanYewu4(List<BjyyBeanYewu3> data) {
        this.data = data;
    }

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