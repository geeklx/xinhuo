package com.geek.appindex.index.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.geek.appcommon.SlbBase;
import com.geek.appindex.R;

public class GztFragmentPeopleAct2 extends SlbBase {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_gztfragments_people;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        Fragment mFragment = null;
        TextView tv_left = findViewById(R.id.tv_left);
        TextView tv_center = findViewById(R.id.tv_center);
        tv_left.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                onBackPressed();
            }
        });
        tv_center.setText("通讯录");

//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.container_framelayout, mFragment = new TabContactFragment())
//                .show(mFragment)
//                .commit();
    }
}
