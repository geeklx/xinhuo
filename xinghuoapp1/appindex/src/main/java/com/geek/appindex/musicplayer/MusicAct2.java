package com.geek.appindex.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.geek.appindex.R;
import com.geek.libutils.app.MyLogUtil;
import com.haier.cellarette.baselibrary.musicutils.musicplayer.DemoMusicImageView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MusicAct2 extends AppCompatActivity {

    private DemoMusicImageView Image;
    private TextView MusicStatus;
    private TextView MusicTime;
    private SeekBar MusicSeekBar;
    private TextView MusicTotal;
    private TextView BtnPlayorPause;
    private TextView BtnStop;
    private TextView BtnQuit;
    //
    private MusicService2.MyBinder mBinder;
    private boolean flag;
    private boolean is_fis = true;
    private boolean is_bind_services = true;
    private boolean fromUser_seekbar;// false 正常 true 滚动
    private int seekbar_progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicact2);
        Image = findViewById(R.id.Image);
        Image.setBackgroundResource(R.drawable.music1);
        MusicStatus = findViewById(R.id.MusicStatus);
        MusicTime = findViewById(R.id.MusicTime);
        MusicSeekBar = findViewById(R.id.MusicSeekBar);
        MusicTotal = findViewById(R.id.MusicTotal);
        BtnPlayorPause = findViewById(R.id.BtnPlayorPause);
        BtnStop = findViewById(R.id.BtnStop);
        BtnQuit = findViewById(R.id.BtnQuit);

        // 音乐
        startMusic();

        BtnPlayorPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image.playMusic();
                if (mBinder != null && mBinder.getService() != null && mBinder.getService().getmPlayer() != null) {
                    if (mBinder.getService().getmPlayer().isPlaying()) {
                        // 暂停播放
                        BtnPlayorPause.setText("开始");
                        mBinder.musicPause();
                    } else {
                        // 开始播放
                        BtnPlayorPause.setText("暂停");
                        if (is_fis) {
                            is_fis = false;
                            // 音乐bufen
                            if (flag) {
                                mBinder.musicStart(MusicAct2.this, "mp3/" + "11.mp3");// 崩溃了
                                set_time_change();
                            }
                        } else {
                            // 开始
                            mBinder.musicContinue();
                        }
                    }
                }
                // old
//                if (Image.getState() == Image.STATE_PLAYING) {
//                    BtnPlayorPause.setText("开始");
//                    // 暂停
//                    if (mBinder != null && mBinder.getService() != null && mBinder.getService().getmPlayer().isPlaying()) {
//                        mBinder.musicPause();
//                    }
//                }
//                if (Image.getState() == Image.STATE_STOP ||
//                        Image.getState() == Image.STATE_PAUSE) {
//                    BtnPlayorPause.setText("暂停");
//                    if (is_fis) {
//                        is_fis = false;
//                        // 音乐
//                        Intent intent = new Intent(MusicAct2.this, MusicService.class);
//                        intent.putExtra("flag", flag);
//                        bindService(intent, conn, BIND_AUTO_CREATE);
//                    } else {
//                        // 开始
//                        if (mBinder != null && mBinder.getService() != null && !mBinder.getService().getmPlayer().isPlaying()) {
//                            mBinder.musicContinue();
//                        }
//                    }
//                }
//                // 动画
//                Image.playMusic();
            }
        });
        BtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image.stopMusic();
                BtnPlayorPause.setText("开始");
                set_time_change();
                // 暂停
                if (mBinder != null
                        && mBinder.getService() != null
                        && mBinder.getService().getmPlayer() != null
                        && mBinder.getService().getmPlayer().isPlaying()) {
                    mBinder.musicStop();
                    is_fis = true;
                }
            }
        });
        BtnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_next();
                set_time_change();
            }
        });
        MusicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MyLogUtil.e("-seekbar-onProgressChanged--", "");
//                seekBar.setTag(progress);
                fromUser_seekbar = fromUser;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MyLogUtil.e("-seekbar-onStartTrackingTouch--", "");
//                ListenMusicUtil.getInstance().setState_ani(ListenMusicUtil.getInstance().STATE_PLAYING);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MyLogUtil.e("-seekbar-onStopTrackingTouch--", "");
                seekbar_progress = seekBar.getProgress();
                if (fromUser_seekbar) {
                    fromUser_seekbar = false;
                    if (MusicUtils.getInstance().getState_ani() == MusicUtils.getInstance().STATE_STOP) {
//                            MusicUtils.getInstance().initMusic(((View) viewpager.getmCurrentItem().getObject()).findViewById(R.id.iv1));
                        MusicUtils.getInstance().initMusic(Image);
                    } else {
                        MusicUtils.getInstance().setState_ani(MusicUtils.getInstance().STATE_PAUSE);
                    }
                    MusicUtils.getInstance().playMusic(Image);
                    if (mBinder != null && mBinder.getService() != null) {
//                        mBinder.getService().setCurrent(current_url);
                        mBinder.musicSeekTo(seekBar.getProgress());
                    }
                }
            }
        });
    }

    private void startMusic() {
        // 音乐
        Intent intent = new Intent(MusicAct2.this, MusicService2.class);
        intent.putExtra("flag", flag);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private ScheduledExecutorService mExecutorService;

    private void set_time_change() {
        // 刷新进度条bufen
        if (mExecutorService != null) {
            return;
        }
        mExecutorService = Executors.newScheduledThreadPool(1);
        mExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
//                if (mBinder.getService().get_time().equals(mBinder.getService().get_maxtime())) {
////                    mExecutorService.shutdown();
//
//                }
                // 数据显示bufen
                MusicTotal.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mBinder != null && mBinder.getService() != null && mBinder.getService().getmPlayer() != null) {
                            MusicTotal.setText(mBinder.getService().get_maxtime());
                            MusicSeekBar.setMax(mBinder.getService().getmPlayer().getDuration());
                        }

                    }
                });
                MusicTime.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mBinder != null && mBinder.getService() != null && mBinder.getService().getmPlayer() != null) {
                            MusicTime.setText(mBinder.getService().get_time());
                            if (!fromUser_seekbar) {
                                MusicSeekBar.setProgress(mBinder.getService().getmPlayer().getCurrentPosition());
                            }
                        }
                    }
                });

            }
        }, 1000, 100, TimeUnit.MILLISECONDS);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (MusicService2.MyBinder) service;
            flag = true;
            mBinder.getService().setOnPrepared(new MusicService2.DemoPreparedCallBack() {
                @Override
                public void isPrepared(boolean prepared) {
                    // 监听音乐播放完成bufen
                    if (is_bind_services) {
                        is_bind_services = false;
                        return;
                    }
                    if (!prepared) {
                        // next
                        play_next();
                    }
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void play_next() {
        Image.setBackgroundResource(R.drawable.ic_zhaoliying);
        BtnPlayorPause.setText("暂停");
        Image.playNextMusic();
        if (mBinder != null) {
            mBinder.musicNext(MusicAct2.this, "mp3/" + "22.mp3");
        }
    }

    @Override
    public void finish() {
        Image.stopMusic();
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
        if (mBinder != null && mBinder.isService()) {
            mBinder.musicDestroy();
            mBinder = null;
            unbindService(conn);
            stopService(new Intent(this, MusicService2.class));
//            this.finish();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


}
