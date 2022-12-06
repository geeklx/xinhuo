package com.geek.appindex.musicplayer.liebiao;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appindex.R;
import com.geek.libbase.base.SlbBaseActivity;
import com.geek.libbase.widgets.XRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MusicAct1 extends SlbBaseActivity implements View.OnClickListener {

    private File filePath;
    private TextView tv1;
    private ImageView iv1;
    private TextView music_name;
    private SeekBar sb_progress;
    private LinearLayout btn_bar;
    private TextView tv_play_time;
    private ImageView btn_play_mode;
    private ImageView btn_pre;
    private ImageView btn_play;
    private ImageView btn_next;
    private ImageView btn_del;
    private TextView tv_total_time;
    //
    private MusicService.MyBinder myBinder;
    //
    private MyConnect myConnect;
    // 时间计时器
    private ScheduledExecutorService mExecutorService;
    // 进度条
    private SeekBar skPro;
    // 当前进度
    private TextView tvCurrTime;
    // 总时长
    private TextView tvTotalTime;
    // 歌曲名字
    private TextView tvMusicName;
    // 歌曲列表
//    private ListView lvMusic;
    private XRecyclerView lvMusic;
    // 适配器
    private MusicAdapter1 mAdapter1;
    // 存放歌曲的名字
    private ArrayList<String[]> musicList = null;
    //private ArrayList<String> musicTime = null;
    // 存放当前音乐在ListView里的下标
    private int musicPosition = 0;
    // 0顺序，1循环，2随机，3单曲
    private int playModeIndex = 0;
    // 播放模式的图片
    private int[] playModeIcon = {R.drawable.list_order, R.drawable.list_cycle,
            R.drawable.random_play, R.drawable.single_cycle};
    private boolean fromUser_seekbar;// false 正常 true 滚动

    @Override
    protected int getLayoutId() {
        return R.layout.activity_musicact1;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]
                {"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        findview();
        onclick();
        donetwork();
    }


    private List<MusicBean1> setData() {
        // 测试本地目录
//        filePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        String absolutePath = filePath.getAbsolutePath();
        // 获取music目录下的MP3
        musicList = MusicPlayerUtil.getMusicName(absolutePath, ".mp3");
        List<MusicBean1> list = new ArrayList<>();
        for (int i = 0; i < musicList.size(); i++) {
            String name = musicList.get(i)[0];
            String url = new File(filePath, name).getAbsolutePath();
            String time = musicList.get(i)[1];
            if (i % 2 == 0) {
                MusicBean1 bean1 = new MusicBean1(i + "", name, R.drawable.music1, url, time, false);
                list.add(bean1);
            } else {
                MusicBean1 bean1 = new MusicBean1(i + "", name, R.drawable.music2, url, time, false);
                list.add(bean1);
            }
        }
        return list;
    }

    private void donetwork() {
        // 加载数据
        mAdapter1.setNewData(setData());
        lvMusic.setAdapter(mAdapter1);
        // 动画bufen
        iv1.setBackgroundResource(R.drawable.music2);
        // 数据加载完成后开启音乐服务bufen
        if (myConnect == null) {
            myConnect = new MyConnect();
            Intent intent = new Intent(this, MusicService.class);
            bindService(intent, myConnect, BIND_AUTO_CREATE);
        }
    }

    /**
     * 创建服务
     */
    private class MyConnect implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("TAG", "onServiceConnected");
            myBinder = (MusicService.MyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    private void onclick() {
        btn_pre.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_play_mode.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        skPro.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // 拖动过程中的事件
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (myBinder != null) {
                    tvCurrTime.setText(MusicPlayerUtil.format(seekBar.getProgress()));
                }
                fromUser_seekbar = fromUser;

            }

            // 开始拖到时的事件
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //timer.purge();
            }

            // 结束拖动时的事件
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (fromUser_seekbar) {
                    fromUser_seekbar = false;
                    if (MusicAnimationUtils.getInstance().getState_ani() == MusicAnimationUtils.getInstance().STATE_STOP) {
//                            MusicUtils.getInstance().initMusic(((View) viewpager.getmCurrentItem().getObject()).findViewById(R.id.iv1));
                        MusicAnimationUtils.getInstance().initMusic(iv1);
                    } else {
                        MusicAnimationUtils.getInstance().setState_ani(MusicAnimationUtils.getInstance().STATE_PAUSE);
                    }
                    MusicAnimationUtils.getInstance().playMusic(iv1);
                    if (myBinder != null && !myBinder.isNull()) {
                        Log.i("S", "1");
                        myBinder.seekToProgress(seekBar.getProgress());
                    } else {

                    }
                    //addTimer();
                }
            }
        });
        mAdapter1.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                MusicBean1 bean1 = (MusicBean1) adapter.getItem(position);
                musicPosition = position;
                int i = view.getId();
                if (i == R.id.item_tv) {
                    set_status(bean1, musicPosition);
                }
            }
        });
    }

    /**
     * 播放点击事件
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_play) {
            // 播放/暂停
            if (mAdapter1.getCurrentPos() == -1) {
                Toast.makeText(this, "请选择一首歌曲", Toast.LENGTH_SHORT).show();
                return;
            }
            MusicBean1 bean1 = mAdapter1.getData().get(musicPosition);
            if (mAdapter1.getData().size() == 0) {
                Toast.makeText(this, "歌单没有歌曲", Toast.LENGTH_SHORT).show();
                return;
            }
            String path = bean1.getUrl();
            if (!TextUtils.isEmpty(path)) {
                if (!myBinder.getIsPlaying()) {
                    myBinder.play(path);
                    addTimer();
                    btn_play.setBackgroundResource(R.drawable.pause);
                    MusicAnimationUtils.getInstance().resumeMusic2(iv1);
                } else {
                    myBinder.pause();
                    btn_play.setBackgroundResource(R.drawable.play);
                    MusicAnimationUtils.getInstance().pauseMusic2(iv1);
                }
            } else {
                Log.i("TAG", "文件不存在");
            }
        } else if (id == R.id.btn_pre) {
            // 上一首
            if (mAdapter1.getData().size() == 0) {
                Toast.makeText(this, "歌单没有歌曲", Toast.LENGTH_SHORT).show();
                return;
            }
            preMusic();
        } else if (id == R.id.btn_next) {
            // 下一首
            if (mAdapter1.getData().size() == 0) {
                Toast.makeText(this, "歌单没有歌曲", Toast.LENGTH_SHORT).show();
                return;
            }
            nextMusic();
        } else if (id == R.id.btn_play_mode) {
            // 切换模式
            playModeIndex = (playModeIndex + 1) % 4;
            String[] modeName = {"列表顺序播放", "列表循环播放", "随机播放", "单曲循环播放"};
            Toast.makeText(this, modeName[playModeIndex], Toast.LENGTH_SHORT).show();
            btn_play_mode.setBackgroundResource(playModeIcon[playModeIndex]);
        } else if (id == R.id.btn_del) {
            // 删除播放列表
            if (mAdapter1.getData().size() == 0) {
                Toast.makeText(this, "歌单没有歌曲", Toast.LENGTH_SHORT).show();
                return;
            } else {
                delete(musicPosition);
            }
        }
//        if (mAdapter1.getData().size() > 0) {
//            // 标题bufen
//            String name = mAdapter1.getData().get(musicPosition).getName();
//            tvMusicName.setText(name.substring(0, name.length() - 4));
//        }
    }

    private void addTimer() {
        // 刷新进度条bufen
        if (mExecutorService != null) {
            return;
        }
        mExecutorService = Executors.newScheduledThreadPool(1);
        mExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                int currTime = myBinder.getCurrTime();      // 获取当前进度，毫秒
                int totalTime = myBinder.getTotalTime();    // 获取总进度，毫秒
                Bundle bundle = new Bundle();       // 将数据封装到Bundle
                bundle.putInt("currTime", currTime);
                bundle.putInt("totalTime", totalTime);
                Message message = handler.obtainMessage();
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }, 500, 5, TimeUnit.MILLISECONDS);
    }

    /**
     * 配置handler
     */
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int currTime = bundle.getInt("currTime");
            int totalTime = bundle.getInt("totalTime");
            skPro.setMax(totalTime);
            if (!fromUser_seekbar) {
                skPro.setProgress(currTime);
            }
            tvCurrTime.setText(MusicPlayerUtil.format(currTime));
            tvTotalTime.setText(MusicPlayerUtil.format(totalTime));
            if (totalTime - currTime < 500 && myBinder.getIsPlaying() && currTime != 0 && totalTime != 0) {
                nextMusic();
            }
        }
    };

    /**
     * 上一首
     */
    public void preMusic() {
        int temp = MusicPlayerUtil.getPreMusicPosition(musicPosition, playModeIndex, mAdapter1.getData().size());
        if (temp == -1) {
            Toast.makeText(this, "已经是第一首了", Toast.LENGTH_SHORT).show();
            return;
        }
        musicPosition = temp;
        MusicBean1 bean1 = mAdapter1.getData().get(musicPosition);
        set_status(bean1, musicPosition);
    }

    /**
     * 下一首
     */
    public void nextMusic() {
        int temp = MusicPlayerUtil.getNextMusicPosition(musicPosition, playModeIndex, mAdapter1.getData().size());
        if (temp == -1) {
            Toast.makeText(this, "已经是最后一首了", Toast.LENGTH_SHORT).show();
            return;
        }
        musicPosition = temp;
        MusicBean1 bean1 = mAdapter1.getData().get(musicPosition);
        set_status(bean1, musicPosition);
    }

    /**
     * 设置播放view状态bufen
     *
     * @param musicBean1
     * @param musicPosition
     */
    private void set_status(MusicBean1 musicBean1, int musicPosition) {
        myBinder.cut(musicBean1.getUrl());
        addTimer();
        mAdapter1.setCurrentPos(musicPosition);
        mAdapter1.notifyDataSetChanged();
        //
        btn_play.setBackgroundResource(R.drawable.pause);
        // 标题bufen
        String name = musicBean1.getName();
        tvMusicName.setText(name.substring(0, name.length() - 4));
        // 动画bufen
        iv1.setBackgroundResource(musicBean1.getDrawable());
        MusicAnimationUtils.getInstance().initMusic(iv1);
        MusicAnimationUtils.getInstance().playNextMusic(iv1);
    }

    public void delete(int musicPosition) {
        mAdapter1.remove(musicPosition);
    }

    private void findview() {
        tv1 = findViewById(R.id.tv1);
        iv1 = findViewById(R.id.iv1);
        lvMusic = findViewById(R.id.recyclerView1);
        btn_bar = findViewById(R.id.btn_bar);
        btn_play_mode = findViewById(R.id.btn_play_mode);
        btn_pre = findViewById(R.id.btn_pre);
        btn_play = findViewById(R.id.btn_play);
        btn_next = findViewById(R.id.btn_next);
        btn_del = findViewById(R.id.btn_del);
        tvMusicName = findViewById(R.id.music_name);
        skPro = findViewById(R.id.sb_progress);
        tvCurrTime = findViewById(R.id.tv_play_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        //
        lvMusic.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        lvMusic.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mAdapter1 = new MusicAdapter1();

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


}
