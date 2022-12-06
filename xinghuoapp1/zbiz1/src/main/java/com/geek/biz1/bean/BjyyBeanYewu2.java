package com.geek.biz1.bean;

import java.io.Serializable;
import java.util.List;

public class BjyyBeanYewu2 implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String img;
    private String name;
    private String url;
    private boolean enable;
    private List<BjyyBeanYewu3> data;

    public BjyyBeanYewu2() {
    }

    public BjyyBeanYewu2(String id, String img, String name, String url, boolean enable, List<BjyyBeanYewu3> data) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.url = url;
        this.enable = enable;
        this.data = data;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<BjyyBeanYewu3> getData() {
        return data;
    }

    public void setData(List<BjyyBeanYewu3> data) {
        this.data = data;
    }
}
