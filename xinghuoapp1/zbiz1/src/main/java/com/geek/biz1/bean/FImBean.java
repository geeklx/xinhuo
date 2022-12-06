package com.geek.biz1.bean;

import java.io.Serializable;

public class FImBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private Fytxsphy1Bean meeting;
    private Fytxim2Bean rlResult;
    private Ftencentim1Bean txResult;

    public FImBean() {
    }

    public FImBean(Fytxsphy1Bean meeting, Fytxim2Bean rlResult, Ftencentim1Bean txResult) {
        this.meeting = meeting;
        this.rlResult = rlResult;
        this.txResult = txResult;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Fytxsphy1Bean getMeeting() {
        return meeting;
    }

    public void setMeeting(Fytxsphy1Bean meeting) {
        this.meeting = meeting;
    }

    public Fytxim2Bean getRlResult() {
        return rlResult;
    }

    public void setRlResult(Fytxim2Bean rlResult) {
        this.rlResult = rlResult;
    }

    public Ftencentim1Bean getTxResult() {
        return txResult;
    }

    public void setTxResult(Ftencentim1Bean txResult) {
        this.txResult = txResult;
    }
}
