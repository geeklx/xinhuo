package com.geek.appindex.musicplayer.liebiao;

import java.io.Serializable;

public class MusicBean1 implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private int drawable;
    private String url;
    private String content1;
    private boolean enable;

    public MusicBean1() {
    }

    public MusicBean1(String id, String name, int drawable, String url, String content1, boolean enable) {
        this.id = id;
        this.name = name;
        this.drawable = drawable;
        this.url = url;
        this.content1 = content1;
        this.enable = enable;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent1() {
        return content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
