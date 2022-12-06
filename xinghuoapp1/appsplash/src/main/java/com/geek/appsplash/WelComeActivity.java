package com.geek.appsplash;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.biz1.bean.FinitBean;
import com.geek.biz1.presenter.FinitPresenter;
import com.geek.biz1.view.FinitView;
import com.geek.libbase.utils.CommonUtils;
import com.geek.libutils.app.BaseApp;
import com.geek.libutils.app.MobileUtils;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.haier.cellarette.baselibrary.btnonclick.view.BounceView;
import com.haier.cellarette.baselibrary.zothers.SpannableStringUtils;
import com.just.agentweb.geek.hois3.HiosHelperNew;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.mob.tools.utils.Hashon;

import java.util.HashMap;
import java.util.Set;

public class WelComeActivity extends AppCompatActivity implements FinitView {

    private TextView tv1;
    private ImageView iv1;
    private int key_token;
    private CountDownTimer mCuntDownTimer;
    private FinitPresenter fconfigPresenter;
    private BasePopupView loadingPopup = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) { // 当前类不是该Task的根部，那么之前启动
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) { // 当前类是从桌面启动的
                    super.onCreate(savedInstanceState);
                    finish(); // finish掉该类，直接打开该Task中现存的Activity
                    return;
                }
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        key_token = SPUtils.getInstance().getInt("key_token", -1);
        configNDK();
    }

    private String URL = "http://cdn2.cdn.haier-jiuzhidao.com/1398.png";
    private String aaaa;
    private String bbbb;
    private String cccc;

    private void jump() {
        //   if (TextUtils.equals(hSettingBean.getForceLogin(), "1")) { // 强制登录bufen
        FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
        if (fconfigBean != null) {
            if (!TextUtils.isEmpty(fconfigBean.getShort_style_style()) && TextUtils.equals(fconfigBean.getShort_style_style(), "true")) {
                SPUtils.getInstance().put("zhihui", true);
            } else {
                SPUtils.getInstance().put("zhihui", false);
            }
            if (!TextUtils.isEmpty(fconfigBean.getLogin_login()) && TextUtils.equals("true", fconfigBean.getLogin_login())) {
//        if (TextUtils.isEmpty(SPUtils.getInstance().getString("token", ""))) {
                Intent loginIntent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.SlbLoginActivity");
                startActivity(loginIntent);
                finish();
                return;
            }
        }
        // 根据业务跳转
        // 根据HIOS协议三方平台跳转
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        if (appLinkIntent != null) {
            String appLinkAction = appLinkIntent.getAction();
//                if (appLinkAction != null) {
            Uri appLinkData = appLinkIntent.getData();
            if (appLinkData != null) {
                aaaa = appLinkData.getQueryParameter("query1");// ShouyeActivity
                bbbb = appLinkData.getQueryParameter("query2");// condition=login
                cccc = appLinkData.getQueryParameter("query3");// 本地应用
//                ToastUtils.showLong("query1->" + aaaa + ",query2->" + bbbb + ",query3->" + cccc);
//                dataability://cs.znclass.com/com.fosung.lighthouse.dt2.hs.act.slbapp.WelComeActivity?query1=FenleiAct2&query2=condition=login&query3=1111
//                "dataability://com.github.geekcodesteam.app.hs.act.slbapp.FenleiAct2{act}?condition=login&query1={s}1&query2={s}本地应用&query3={s}5"
//                String dddd = "dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp."
//                        + aaaa + "{act}?"
//                        + bbbb
//                        + "&query3={s}" + cccc;
//                HiosHelperNew.resolveAd(WelComeActivity.this, WelComeActivity.this, dddd);
//                set_url_hios_finish(dddd);
                //
//                HiosHelperNew.resolveAd(WelComeAct.this, WelComeAct.this, dddd);
//                finish();
            } else {
                startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye"));// ShouyeActivity
                finish();
            }
        }
    }

    private void configNDK() {
        // 欢迎页声音
//        GetAssetsFileMP3TXTJSONAPKUtil.getInstance(this).playMusic(App.get(), true, "android.resource://" + getPackageName() + "/" + R.raw.demo);
        fconfigPresenter = new FinitPresenter();
        fconfigPresenter.onCreate(this);
        fconfigPresenter.getinit(AppCommonUtils.auth_url + "/init");
    }

    @Override
    protected void onDestroy() {
        if (mCuntDownTimer != null) {
            mCuntDownTimer.cancel();
        }
        if (fconfigPresenter != null) {
            fconfigPresenter.onDestory();
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
//      overridePendingTransition(0, 0);
//        overridePendingTransition(com.geek.liblanguage.R.anim.activity_alpha_in, com.geek.liblanguage.R.anim.activity_alpha_out);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void set_view(String Advertlinkurl, String Advertimage) {
        // Log.e("aaaaaaa", "updateSplashData: " + "set_view");
        // 先隐私协议弹窗 后倒计时 后进入登录 后进入首页
        if (initPop(Advertlinkurl, Advertimage)) {
            initSplashView(Advertlinkurl, Advertimage);
        }
    }

    //隐私政策
    private Boolean initPop(String Advertlinkurl, String Advertimage) {
        if (!MmkvUtils.getInstance().get_xiancheng_bool(CommonUtils.MMKV_forceLogin)) {
            if (loadingPopup != null) {
                if (!loadingPopup.isShow()) {
                    loadingPopup.show();
                }
                return false;
            }
            loadingPopup = new XPopup.Builder(this)
                    .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
//              .isViewMode(true)
                    .dismissOnTouchOutside(false)
                    .asCustom(new CenterPopupView(this) {
                        private TextView tvContent;
                        private CheckBox radAgreement;
                        private TextView btnClose;
                        private TextView btnOk;

                        @Override
                        protected int getImplLayoutId() {
                            return R.layout.dialog_agreement1;
                        }

                        @Override
                        protected void onCreate() {
                            super.onCreate();
                            tvContent = findViewById(R.id.tv_content);
                            radAgreement = findViewById(R.id.rad_agreement);
                            btnClose = findViewById(R.id.btn_cancle);
                            btnOk = findViewById(R.id.btn_ok);
                            FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
                            tvContent.setText(SpannableStringUtils.getInstance(WelComeActivity.this)
                                    .getBuilder("欢迎使用移动端平台！灯塔非常重视您的隐私保护和个人信息保护。在您使用移动端平台前，请认真阅读")
                                    .append("《用户协议》")
                                    .setClickSpan(new ClickableSpan() {
                                        @Override
                                        public void onClick(View widget) {
                                            HiosHelperNew.resolveAd(WelComeActivity.this, WelComeActivity.this, fconfigBean.getUser());
                                        }

                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            ds.setColor(ContextCompat.getColor(BaseApp.get(), R.color.red));
                                            ds.setUnderlineText(false);
                                        }
                                    })
                                    .append("及")
                                    .append("《隐私政策》")
                                    .setClickSpan(new ClickableSpan() {
                                        @Override
                                        public void onClick(View widget) {
//                                        Uri url = Uri.parse("http://blog.51cto.com/liangxiao");
//                                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                                        intent.setData(url);
//                                        startActivity(intent);
//                                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, MmkvUtils.getInstance().get_common(CommonUtils.MMKV_privacyPolicy));
                                            HiosHelperNew.resolveAd(WelComeActivity.this, WelComeActivity.this, fconfigBean.getPrivacy());
                                        }

                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            ds.setColor(ContextCompat.getColor(WelComeActivity.this, R.color.red));
                                            ds.setUnderlineText(false);
                                        }
                                    })
                                    .append("，您同意并接受全部条款后方可使用。")
                                    .create());
                            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                            BounceView.addAnimTo(btnOk);
                            BounceView.addAnimTo(btnClose);
                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!radAgreement.isChecked()) {
                                        com.hjq.toast.ToastUtils.show("请勾选用户协议及隐私政策");
                                        return;
                                    }
                                    dismiss();
                                    MmkvUtils.getInstance().set_xiancheng(CommonUtils.MMKV_forceLogin, true);
                                    initSplashView(Advertlinkurl, Advertimage);
                                    return;
                                }
                            });
                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismiss();
                                    ActivityUtils.finishAllActivities();
                                    finish();
                                }
                            });
                        }
                        //        @Override
                        //        protected int getMaxHeight() {
                        //            return 200;
                        //        }
                        //
                        //返回0表示让宽度撑满window，或者你可以返回一个任意宽度
                        //        @Override
                        //        protected int getMaxWidth() {
                        //            return 1200;
                        //        }
                    });
            if (!loadingPopup.isShow()) {
                loadingPopup.show();
            }
            return false;
        } else {
            return true;
        }


    }

    private void initSplashView(String Advertlinkurl, String Advertimage) {
        SplashView.showSplashView(this, 3, R.drawable.icon_hyy1, new SplashView.OnSplashViewActionListener() {
            @Override
            public void onSplashImageClick(String actionUrl) {
                if (!TextUtils.isEmpty(actionUrl)) {
                    HiosHelperNew.resolveAd(WelComeActivity.this, WelComeActivity.this, actionUrl);
                    finish();
                }
            }

            @Override
            public void onSplashViewDismiss(boolean initiativeDismiss) {
                if (key_token == -1) {
                    Intent intent;
                    if (checkGuideImg()) {
                        intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.SplshActivity");
                    } else {
                        intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.SlbLoginActivity");
                    }
                    startActivity(intent);
                    finish();
                    return;
                }
                if (key_token == 0) {
                    if (!MobileUtils.isNetworkConnected(WelComeActivity.this)) {
                        startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.SlbLoginActivity"));// ShouyeActivity
                        finish();
                        return;
                    }
                    MyLogUtil.e("ssssssssss", "configtencentIm login success  onSplashViewDismiss");
                    jump();
                }
            }
        });
        Log.e("TAG", "OnFinitSuccess: " + Advertlinkurl);
        if (TextUtils.isEmpty(Advertlinkurl)) {//等于空的话走默认的图片
            //Log.e("aaaaaaa", "OnFinitSuccess: " + "广告地址为空");
            SplashView.updateSplashData(this, "http://cdn2.cdn.haier-jiuzhidao.com/1398.png", "https://www.baidu.com/");
        } else {
            //Log.e("aaaaaaa", "OnFinitSuccess: " + "广告地址不为空");
            SplashView.updateSplashData(this, Advertimage, Advertlinkurl);
        }
    }

    private boolean checkGuideImg() {
        FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
        return fconfigBean != null && fconfigBean.getGuideimage() != null && fconfigBean.getGuideimage().size() > 0;
    }

    @Override
    public void OnFinitSuccess(FinitBean bean) {
        if (bean != null && !"".equals(bean)) {
            set_view(bean.getAdvertlinkurl(), bean.getAdvertimage());
        }
        MmkvUtils.getInstance().set_common_json2("config", bean);//保存初始化配置信息
        SPUtils.getInstance().put("accessSecret", bean.getAuthorization_sk());
        SPUtils.getInstance().put("accessKey", bean.getAuthorization_ak());
        SPUtils.getInstance().put("version", bean.getAuthorization_version());
    }

    @Override
    public void OnFinitNodata(String bean) {
        set_view("", "");
    }

    @Override
    public void OnFinitFail(String msg) {
        set_view("", "");
    }

    @Override
    public String getIdentifier() {
        return null;
    }
}