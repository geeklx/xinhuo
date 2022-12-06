package com.geek.appindex.index.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.geek.appcommon.SlbBase;
import com.geek.appindex.R;
import com.geek.appindex.index.fragment.ShouyeF9;

public class GztFragmentShuzixiangcunAct extends SlbBase {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_gztfragmentshouye;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        Fragment mFragment = null;

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_framelayout, mFragment = ShouyeF9.getInstance(new Bundle()), ShouyeF9.class.getName())
                .show(mFragment)
                .commit();
    }
}
