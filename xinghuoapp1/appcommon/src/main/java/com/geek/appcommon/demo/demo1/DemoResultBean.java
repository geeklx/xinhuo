package com.geek.appcommon.demo.demo1;

import java.io.Serializable;
import java.util.List;

public class DemoResultBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<DemoBean> datas;

    public List<DemoBean> getDatas() {
        return datas;
    }

    public void setDatas(List<DemoBean> datas) {
        this.datas = datas;
    }
}
