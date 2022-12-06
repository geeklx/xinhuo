package com.geek.biz1.bean;

import java.io.Serializable;

public class BjyyActFragment251Bean implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int style1 = 1;
    public static final int style11 = 11;
    public static final int style5 = 5;
    //党建引领
    public static final int STYLE_DJYL = 110;

    public int type;
    private BjyyBeanYewu3 mbean;

    public BjyyActFragment251Bean() {
    }

    public BjyyActFragment251Bean(int type) {
        this.type = type;
    }

    public BjyyActFragment251Bean(int type, BjyyBeanYewu3 mbean) {
        this.type = type;
        this.mbean = mbean;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BjyyBeanYewu3 getmBean() {
        return mbean;
    }

    public void setmBean(BjyyBeanYewu3 mbean) {
        this.mbean = mbean;
    }
}
