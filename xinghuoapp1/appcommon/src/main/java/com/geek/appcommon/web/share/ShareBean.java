package com.geek.appcommon.web.share;

/**
 * @author: lhw
 * @date: 2021/12/30
 * @desc
 */
public class ShareBean {
    public String name;
    public int icon;
    public String platform;

    public ShareBean(String name, int icon, String platform) {
        this.name = name;
        this.icon = icon;
        this.platform = platform;
    }
}
