package com.geek.biz1.bean.home;

import java.io.Serializable;
import java.util.List;

public class ClassificationListBean implements Serializable {
    private List<ClassificationListData> data;

    public List<ClassificationListData> getData() {
        return data;
    }

    public void setData(List<ClassificationListData> data) {
        this.data = data;
    }
}


