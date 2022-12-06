package com.geek.biz1.bean;

import java.io.Serializable;
import java.util.List;

public class BjyyActFragment251Bean2 implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<BjyyActFragment251Bean> data;

    public BjyyActFragment251Bean2() {
    }

    public BjyyActFragment251Bean2(List<BjyyActFragment251Bean> data) {
        this.data = data;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<BjyyActFragment251Bean> getData() {
        return data;
    }

    public void setData(List<BjyyActFragment251Bean> data) {
        this.data = data;
    }
}