package com.geek.appcommon.xpop;

import android.text.TextUtils;

public class BaseShowBean {

    private String shwoContent;

    private boolean select;

    public void setShwoContent(String shwoContent) {
        this.shwoContent = shwoContent;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public BaseShowBean() {
    }


    public BaseShowBean(String shwoContent) {
        this.shwoContent = shwoContent;
    }

    public String getShwoContent() {
        return TextUtils.isEmpty(showCommonContent()) ? shwoContent : showCommonContent();
    }


    protected String showCommonContent() {
        return "";
    }

}
