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
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.biz1.bean.FguanyuBean;
import com.geek.biz1.bean.FshengjiBean;
import com.geek.biz1.presenter.FguanyuPresenter;
import com.geek.biz1.presenter.FshengjiPresenter;
import com.geek.biz1.view.FguanyuView;
import com.geek.biz1.view.FshengjiView;
import com.geek.libbase.base.SlbBaseActivity;
import com.geek.libglide47.base.GlideImageView;
import com.geek.libglide47.base.ShapeImageView;
import com.geek.libupdateapp.util.UpdateAppUtils;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.jiami.Md5Utils;
import com.geek.libswipebacklayout.SwipeBack;
import com.just.agentweb.geek.hois3.HiosHelperNew;

@SwipeBack(value = true)
public class MySettingAboutAct extends SlbBaseActivity implements FshengjiView, FguanyuView {

    private TextView tv_left;
    private TextView tv_center;
    private GlideImageView iv0;
    private TextView tv0;
    private TextView tv1;
    private TextView tv2;
    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private RelativeLayout rl3;
    private RelativeLayout rl4;
    private RelativeLayout rl5;
    private FguanyuBean fguanyuBean;
    private FshengjiPresenter fshengjiPresenter;
    private FguanyuPresenter fguanyuPresenter;
    private String apkPath = "";
    private int serverVersionCode = 0;
    private String serverVersionName = "";
    private String updateInfoTitle = "";
    private String updateInfo = "";
    private String md5 = "";
    private String appPackageName = "";

    @Override
    protected void onDestroy() {
        if (fshengjiPresenter != null) {
            fshengjiPresenter.onDestory();
        }
        if (fguanyuPresenter != null) {
            fguanyuPresenter.onDestory();
        }
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mysettinggy;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        //
        BarUtils.setStatusBarLightMode(this, true);
        super.setup(savedInstanceState);
        tv_left = findViewById(R.id.tv_left);
        tv_center = findViewById(R.id.tv_center);
        tv_left.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                onBackPressed();
            }
        });
        tv_center.setText("");
        iv0 = findViewById(R.id.iv0);
        tv0 = findViewById(R.id.tv0);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv_tel);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);
        rl5 = findViewById(R.id.rl5);
        //
        rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fshengjiPresenter != null) {
                    apkPath = "";
                    updateInfoTitle = "";
                    updateInfo = "";
                    serverVersionCode = AppUtils.getAppVersionCode();
                    serverVersionName = AppUtils.getAppVersionName();
                    appPackageName = AppUtils.getAppPackageName();
                    md5 = Md5Utils.get32Md5LowerCase(appPackageName);
                    MyLogUtil.e("ssssssssssssss", md5);
                    fshengjiPresenter.getshengji(AppCommonUtils.auth_url,
                            serverVersionCode + "",
                            serverVersionName,
                            appPackageName,
                            md5);
                }
            }
        });
        rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + fguanyuBean.getPhone()));
                    startActivity(intent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });
        rl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HiosHelperNew.resolveAd(MySettingAboutAct.this, MySettingAboutAct.this, fguanyuBean.getRemark());
                Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingFunctionAct");
                intent.putExtra("remark",fguanyuBean.getRemark());
                startActivity(intent);
            }
        });
        rl4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiosHelperNew.resolveAd(MySettingAboutAct.this, MySettingAboutAct.this, fguanyuBean.getUser());
            }
        });
        rl5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiosHelperNew.resolveAd(MySettingAboutAct.this, MySettingAboutAct.this, fguanyuBean.getPrivacy());
            }
        });
        //
        fshengjiPresenter = new FshengjiPresenter();
        fshengjiPresenter.onCreate(this);

        fguanyuPresenter = new FguanyuPresenter();
        fguanyuPresenter.onCreate(this);
        fguanyuPresenter.getguanyu(AppCommonUtils.auth_url);

    }

    @Override
    public void OnFguanyuSuccess(FguanyuBean bean) {
        if (bean != null) {
            fguanyuBean = bean;
//            Glide.with(this).load(bean.getImg()).into(iv0);
            iv0.setShapeType(ShapeImageView.ShapeType.RECTANGLE);
            iv0.setRadius(10);
            iv0.loadImage(bean.getImg(), R.drawable.ic_def_loading);
            tv0.setText(bean.getName());
            tv1.setText("V" + AppUtils.getAppVersionName());
            tv2.setText(bean.getPhone());

        }
    }

    @Override
    public void OnFguanyuNodata(String bean) {

    }

    @Override
    public void OnFguanyuFail(String msg) {

    }

    @Override
    public void OnFshengjiSuccess(FshengjiBean bean) {
        apkPath = bean.getApkPath();
        serverVersionCode = Integer.valueOf(bean.getServerVersionCode());
        serverVersionName = bean.getServerVersionName();
        updateInfoTitle = bean.getUpdateInfoTitle();
        updateInfo = bean.getUpdateInfo();
        if (TextUtils.isEmpty(apkPath)) {
            return;
        }
        UpdateAppUtils.from(MySettingAboutAct.this)
                .serverVersionCode(serverVersionCode)
                .serverVersionName(serverVersionName)
                .downloadPath("apks/" + getPackageName() + ".apk")
                .showProgress(true)
                .isForce(bean.getIsForce())
                .apkPath(apkPath)
                .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)    //default
                .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //default
                .updateInfoTitle(updateInfoTitle)
                .updateInfo(updateInfo.replace("|", "\n"))
//                    .showNotification(true)
//                    .needFitAndroidN(true)
                .update();
    }

    @Override
    public void OnFshengjiNodata(String msg) {
        ToastUtils.showLong(msg);
    }

    @Override
    public void OnFshengjiFail(String msg) {
        ToastUtils.showLong(msg);
    }
}
