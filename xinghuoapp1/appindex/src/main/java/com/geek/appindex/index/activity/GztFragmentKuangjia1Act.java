package com.geek.appindex.index.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.geek.appcommon.SlbBase;
import com.geek.appindex.R;
import com.geek.appindex.index.fragment.ShouyeF1;

public class GztFragmentKuangjia1Act extends SlbBase {
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
                .add(R.id.container_framelayout, mFragment = ShouyeF1.getInstance(new Bundle()), ShouyeF1.class.getName())
                .show(mFragment)
                .commit();
    }
}
