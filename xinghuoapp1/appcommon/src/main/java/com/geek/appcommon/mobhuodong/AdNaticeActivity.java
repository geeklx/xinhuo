package com.geek.appcommon.mobhuodong;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.common.R;
import com.geek.libbase.base.SlbBaseActivity;
import com.geek.libbase.widgets.NavigationBarUtil;
import com.geek.libwebview.hois2.HiosHelper;
import com.haier.cellarette.baselibrary.btnonclick.view.BounceView;

import me.jessyan.autosize.AutoSizeCompat;

/**
 * 公告通知
 */
public class AdNaticeActivity extends SlbBaseActivity {

    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        AutoSizeCompat.autoConvertDensity((super.getResources()), 667, false);//如果有自定义需求就用这个方法
        return super.getResources();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hios_adnatice_activity;
    }

    private TextView tvContent;//内容
    private TextView btnClose;//关闭
    private TextView btnOk;//确定确定
    private String Moburl1;//url
    private String Mobcontent;//内容

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        //虚拟键
        if (NavigationBarUtil.hasNavigationBar(this)) {
            NavigationBarUtil.hideBottomUIMenu(this);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);// topbar
        Moburl1 = getIntent().getStringExtra("url1");
        Mobcontent = getIntent().getStringExtra("content");
        tvContent = findViewById(R.id.tv_content);
        btnClose = findViewById(R.id.btn_cancle);
        btnOk = findViewById(R.id.btn_ok);
        /*第二种样式*/
        //btnClose.setBackgroundResource(R.drawable.common_dialog_notice1);
        //btnOk.setVisibility(View.GONE);
        /*第三种样式*/
        //btnClose.setBackgroundResource(R.drawable.common_dialog_notice);
        //btnOk.setVisibility(View.VISIBLE);
        tvContent.setText(Mobcontent);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        BounceView.addAnimTo(btnOk);
        BounceView.addAnimTo(btnClose);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiosHelper.resolveAd(AdNaticeActivity.this, AdNaticeActivity.this, Moburl1);
                finish();
                return;
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("点击了知道了");
                finish();
                return;
            }
        });

        Log.e("gongshi", (Math.sqrt(Math.pow(667, 2) + (Math.pow(375, 2))) / 25.4 + ""));

    }

}
