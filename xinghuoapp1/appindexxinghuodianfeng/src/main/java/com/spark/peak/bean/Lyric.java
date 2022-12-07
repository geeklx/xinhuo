package com.spark.peak.bean;

import java.io.Serializable;


public class Lyric implements Serializable {
    public String id;//歌词id
    public String lyricText;//歌词内容
    public double startTime ;//开始时间
    public double duringTime = 0;

    public Lyric(long time, String text) {
        startTime = time;
        lyricText = text;
    }

    public Lyric(){

    }

}
