package com.example.slbappsearch;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.geek.libbase.plugins.plugins.PluginBaseActivity;


public class SecondActivity extends PluginBaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //开启插件服务
        Intent serviceIntent = new Intent(that, OneService.class);
        startService(serviceIntent);
    }
}
