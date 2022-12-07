package com.spark.peak.utlis;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by 李昊 on 2017/7/7.
 * 背单词专项，学习、复习检测页面单词发音
 */

public class WordSounder implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private CompletionListener completionListener;


    public void setCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (completionListener!=null) completionListener.onCompletion();
    }

    public interface CompletionListener {
        void onCompletion();

        void onPause(boolean isPlay);
    }


    public WordSounder() {
        init();
    }

    private void init() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }


    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            completionListener.onPause(false);
        } else {
            mediaPlayer.start();
            completionListener.onPause(true);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            completionListener.onPause(false);
        }
    }


    public void playUrl(String url) {
        if (url==null||url.isEmpty()) return;
        try {
            if (mediaPlayer == null) {
                init();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }


    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
