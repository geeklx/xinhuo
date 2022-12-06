package com.geek.appindex.index;

import java.io.Serializable;

public class ShouyeFragmentBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String img;
    private String img_normal;
    private String img_press;
    private String name;
    private String url;
    private boolean enable;

    public ShouyeFragmentBean() {
    }

    public ShouyeFragmentBean(String id, String img, String name, String url, boolean enable) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.url = url;
        this.enable = enable;
    }

    public ShouyeFragmentBean(String id, String img, String img_normal, String img_press, String name, String url, boolean enable) {
        this.id = id;
        this.img = img;
        this.img_normal = img_normal;
        this.img_press = img_press;
        this.name = name;
        this.url = url;
        this.enable = enable;
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

    public String getImg_normal() {
        return img_normal;
    }

    public void setImg_normal(String img_normal) {
        this.img_normal = img_normal;
    }

    public String getImg_press() {
        return img_press;
    }

    public void setImg_press(String img_press) {
        this.img_press = img_press;
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
}
