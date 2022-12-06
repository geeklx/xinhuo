package com.geek.appcommon.xpop;

import java.io.Serializable;
import java.util.List;

public class PopulationLabelListBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<PopulationLabelListItemBean> list;

    public List<PopulationLabelListItemBean> getList() {
        return list;
    }

    public void setList(List<PopulationLabelListItemBean> list) {
        this.list = list;
    }
}
