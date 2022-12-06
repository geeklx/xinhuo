package com.geek.biz1.bean;

import java.io.Serializable;
import java.util.List;

public class BjyyBeanYewu1 implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<BjyyBeanYewu2> data;

    public BjyyBeanYewu1() {
    }

    public BjyyBeanYewu1(List<BjyyBeanYewu2> data) {
        this.data = data;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<BjyyBeanYewu2> getData() {
        return data;
    }

    public void setData(List<BjyyBeanYewu2> data) {
        this.data = data;
    }
}
