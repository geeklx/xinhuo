package com.geek.appmy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;

import com.geek.libbase.base.SlbBaseActivity;
import com.geek.libswipebacklayout.SwipeBack;


@SwipeBack(value = true)
public class MySettingFunctionAct extends SlbBaseActivity {


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mysetting_function;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        //
        BarUtils.setStatusBarLightMode(this, true);
        super.setup(savedInstanceState);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySettingFunctionAct.this.finish();
            }
        });
        TextView tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("功能简介");
        findViewById(R.id.ll_right).setVisibility(View.GONE);
        TextView tvContent = findViewById(R.id.tv_content);
        String content = getIntent().getStringExtra("remark");
        tvContent.setText(content);
    }
}
