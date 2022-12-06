package com.geek.appcommon.demo.demo2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.geek.appcommon.SlbBase;
import com.geek.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: lhw
 * @date: 2022/7/18
 * @desc
 */
public class Demo2Activity extends SlbBase {

    private RelativeLayout page1;
    private RelativeLayout page2;
    private RelativeLayout page3;

    private Fragment currFragment;
    private List<Fragment> fragments;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_demo2;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        findView();
        setClick();
        initFragment();
    }

    private void findView() {
        page1 = findViewById(R.id.page_1);
        page2 = findViewById(R.id.page_2);
        page3 = findViewById(R.id.page_3);
    }

    private void setClick() {
        page1.setOnClickListener(new ClickListener());
        page2.setOnClickListener(new ClickListener());
        page3.setOnClickListener(new ClickListener());
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(Demo2Fragment1.newInstance(null));
        fragments.add(Demo2Fragment2.newInstance(null));
        fragments.add(Demo2Fragment3.newInstance(null));
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.page_1) {
                showAssignedFragment(0);
                page2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                page3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                page1.setBackgroundColor(Color.parseColor("#F8F8F8"));
            } else if (id == R.id.page_2) {
                showAssignedFragment(1);
                page2.setBackgroundColor(Color.parseColor("#F8F8F8"));
                page1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                page3.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else if (id == R.id.page_3) {
                showAssignedFragment(2);
                page1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                page3.setBackgroundColor(Color.parseColor("#F8F8F8"));
                page2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    private void showAssignedFragment(int fragmentIndex) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = fragments.get(fragmentIndex);
        currFragment = fragment;
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

}
