package com.geek.biz1.bean.home;

import java.io.Serializable;
import java.util.List;

public class ClassificationListData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String belongId;
    private String id;
    private List<ClassificationBean> list;
    private String belongTypeId;
    private String notice;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBelongId() {
        return belongId;
    }

    public void setBelongId(String belongId) {
        this.belongId = belongId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ClassificationBean> getList() {
        return list;
    }

    public void setList(List<ClassificationBean> list) {
        this.list = list;
    }

    public String getBelongTypeId() {
        return belongTypeId;
    }

    public void setBelongTypeId(String belongTypeId) {
        this.belongTypeId = belongTypeId;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }
}


