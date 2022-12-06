package com.geek.appcommon.video;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.geek.appcommon.SlbBase;
import com.geek.common.R;

public class TikTokListActivityDK extends SlbBase {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_tiktok_list;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        initView();
        super.setup(savedInstanceState);
        TikTokListFragmentDk tikTokListFragmentDk = new TikTokListFragmentDk();
        tikTokListFragmentDk.setArguments(new Bundle());
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, tikTokListFragmentDk).commitAllowingStateLoss();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = this.getWindow().getDecorView();
//            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
//                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
//                return defaultInsets.replaceSystemWindowInsets(defaultInsets.getSystemWindowInsetLeft(), 0, defaultInsets.getSystemWindowInsetRight(), defaultInsets.getSystemWindowInsetBottom());
//            });
//            ViewCompat.requestApplyInsets(decorView);
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorE60000));
        }
    }
}
