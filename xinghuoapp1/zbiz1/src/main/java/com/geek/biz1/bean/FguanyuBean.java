package com.geek.biz1.bean;

import java.io.Serializable;
import java.util.List;

public class FguanyuBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String img;
    private String name;
    private String remark;
    private String user;
    private String privacy;
    private String phone;

    public FguanyuBean() {
    }

    public FguanyuBean(String img, String name, String remark, String user, String privacy, String phone) {
        this.img = img;
        this.name = name;
        this.remark = remark;
        this.user = user;
        this.privacy = privacy;
        this.phone = phone;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
