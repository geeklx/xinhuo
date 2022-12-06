package com.geek.appcommon.demo.demo1;

import java.io.Serializable;

public class DemoBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String msgId;
    private String title;
    private String content;
    private int status;  //状态， 0:未读，1：已读
    private String createTime;
    private String linkUrl;    //web跳转地址
    private String appLinkUrl; //app跳转地址
    private String unreadnum; //未处理数量

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getAppLinkUrl() {
        return appLinkUrl;
    }

    public void setAppLinkUrl(String appLinkUrl) {
        this.appLinkUrl = appLinkUrl;
    }

    public String getUnreadnum() {
        return unreadnum;
    }

    public void setUnreadnum(String unreadnum) {
        this.unreadnum = unreadnum;
    }
}
