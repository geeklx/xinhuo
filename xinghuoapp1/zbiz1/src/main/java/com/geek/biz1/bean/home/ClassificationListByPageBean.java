package com.geek.biz1.bean.home;

import java.io.Serializable;
import java.util.List;

public class ClassificationListByPageBean implements Serializable {

    private List<ClassificationBean> data;

    public List<ClassificationBean> getData() {
        return data;
    }

    public void setData(List<ClassificationBean> data) {
        this.data = data;
    }
}
