package com.geek.biz1.bean;

import java.io.Serializable;

public class FSearchBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String itemId;// id
    private String hios;// 跳转地址
    private String title;// 标题1
    private String titleHtml;// 副标题
    private String imgUrl;// 图片地址

    public FSearchBean() {
    }

    public FSearchBean(String itemId, String hios, String title, String titleHtml, String imgUrl) {
        this.itemId = itemId;
        this.hios = hios;
        this.title = title;
        this.titleHtml = titleHtml;
        this.imgUrl = imgUrl;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getHios() {
        return hios;
    }

    public void setHios(String hios) {
        this.hios = hios;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleHtml() {
        return titleHtml;
    }

    public void setTitleHtml(String titleHtml) {
        this.titleHtml = titleHtml;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
