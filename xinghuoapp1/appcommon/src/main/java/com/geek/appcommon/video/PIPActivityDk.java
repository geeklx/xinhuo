package com.geek.appcommon.video;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.geek.common.R;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import xyz.doikki.dkplayer.activity.BaseActivityDk;
import xyz.doikki.dkplayer.util.PIPManagerDk;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.player.VideoView;

public class PIPActivityDk extends BaseActivityDk {
    private PIPManagerDk mPIPManager;

    public PIPActivityDk() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_pipdk);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("画中画");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FrameLayout playerContainer = (FrameLayout) this.findViewById(R.id.player_container);
        this.mPIPManager = PIPManagerDk.getInstance();
        VideoView videoView = this.getVideoViewManager().get("pip");
        StandardVideoController controller = new StandardVideoController(this);
        controller.addDefaultControlComponent(this.getString(R.string.str_pip), false);
        videoView.setVideoController(controller);
        if (this.mPIPManager.isStartFloatWindow()) {
            this.mPIPManager.stopFloatWindow();
            controller.setPlayerState(videoView.getCurrentPlayerState());
            controller.setPlayState(videoView.getCurrentPlayState());
        } else {
            this.mPIPManager.setActClass(xyz.doikki.dkplayer.activity.pip.PIPActivityDk.class);
            ImageView thumb = (ImageView) controller.findViewById(R.id.thumb);
            ((RequestBuilder) Glide.with(this).load("http://sh.people.com.cn/NMediaFile/2016/0112/LOCAL201601121344000138197365721.jpg").placeholder(android.R.color.darker_gray)).into(thumb);
            videoView.setUrl("http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4");
        }

        playerContainer.addView(videoView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mPIPManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mPIPManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mPIPManager.reset();
    }

    @Override
    public void onBackPressed() {
        if (!this.mPIPManager.onBackPress()) {
            super.onBackPressed();
        }
    }

    public void startFloatWindow(View view) {
        XXPermissions.with(this).permission("android.permission.SYSTEM_ALERT_WINDOW").request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                PIPActivityDk.this.mPIPManager.startFloatWindow();
                PIPActivityDk.this.mPIPManager.resume();
                PIPActivityDk.this.finish();
            }
        });
    }
}
