package com.geek.applogin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.SlbBase;
import com.geek.biz1.bean.FImBean;
import com.geek.biz1.bean.FconfigBean;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.bean.FinitBean;
import com.geek.biz1.bean.FloginBean;
import com.geek.biz1.presenter.Fconfig1Presenter;
import com.geek.biz1.presenter.FgrxxPresenter;
import com.geek.biz1.presenter.Fim1Presenter;
import com.geek.biz1.presenter.FloginPresenter;
import com.geek.biz1.presenter.Fzwdl1Presenter;
import com.geek.biz1.view.Fconfig1View;
import com.geek.biz1.view.FgrxxView;
import com.geek.biz1.view.Fim1View;
import com.geek.biz1.view.FloginView;
import com.geek.biz1.view.Fzwdl1View;
import com.geek.libutils.app.BaseApp;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.haier.cellarette.baselibrary.qcode.ExpandViewRect;
import com.haier.cellarette.baselibrary.zothers.SpannableStringUtils;
import com.just.agentweb.geek.hois3.HiosHelperNew;
import com.lib.lock.fingerprint.core.FingerprintCore;
import com.lib.lock.fingerprint.core.MyListener;
import com.lib.lock.fingerprint.utils.FingerprintUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

public class SlbLoginActivity extends SlbBase implements View.OnClickListener, Fconfig1View, FloginView, FgrxxView, Fzwdl1View, Fim1View {

    private TextView tv_title0;
    private TextView tv_title1;
    private TextView tv_tips12;
    private TextView tv2;
    private TextView tv3;
    private CheckBox radAgreement;
    private ImageView iv1;
    private ImageView iv_bg1;
    private ImageView iv_xx;
    private ImageView iv_zw;
    private ImageView iv_wx;
    private TextInputLayout textinputlayout1;
    private TextInputLayout textinputlayout2;
    private EditText edt1;
    private EditText edt2;
    private Button tv1;
    //
    //
    private Fconfig1Presenter fconfig1Presenter;
    private FloginPresenter floginPresenter;
    private FgrxxPresenter fgrxxPresenter;
    private Fzwdl1Presenter fzwdl1Presenter;
    private Fim1Presenter fim1Presenter;
    private String url;
    //
    private String openid;
    private String unionid;
    private String gender;
    private String nickName;
    private String avatar;
    private String other_login_name = "Wechat";
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private FinitBean fconfigBean;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_slblogin;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        for (int i = 0; i < ActivityUtils.getActivityList().size(); i++) {
            if (TextUtils.equals(ActivityUtils.getActivityList().get(i).getClass().getSimpleName(), "com.geek.applogin.SlbLoginActivity")) {
                finish();
                return;
            }
        }
        findview();
        onclick();
        donetwork();
        //
//        MobclickAgent.onEvent(this, "Loginpage");
        
    }

    private void donetwork() {
        //
        edt1.setText("15137615080");
        edt2.setText("15137615080");
        //
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tv_tips12.setText(Html.fromHtml("登录即代表您同意" + "<font color=\"#4EC0FF\">用户协议</font>" + "和" + "<font color=\"#4EC0FF\">隐私政策</font>", Html.FROM_HTML_MODE_COMPACT));
//        } else {
//            tv_tips12.setText(Html.fromHtml("登录即代表您同意" + "<font color=\"#4EC0FF\">用户协议</font>" + "和" + "<font color=\"#4EC0FF\">隐私政策</font>"));
//        }
        fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
        tv_title1.setText(getApplication().getResources().getString(R.string.applogin5) + fconfigBean.getTitle_title());
        tv_tips12.setText(SpannableStringUtils.getInstance(this.getApplicationContext())
                .getBuilder(getApplication().getResources().getString(R.string.applogin6))
                .append(getApplication().getResources().getString(R.string.applogin7))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        HiosHelperNew.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, fconfigBean.getUser());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
//                        ds.setColor(Utils.getApp().getResources().getColor(R.color.blue519AF4));
                        ds.setColor(ContextCompat.getColor(SlbLoginActivity.this, R.color.blue519AF4));
                        ds.setUnderlineText(false);
                    }
                })
                .append(getApplication().getResources().getString(R.string.applogin8))
                .append(getApplication().getResources().getString(R.string.applogin9))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        HiosHelperNew.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, fconfigBean.getPrivacy());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(ContextCompat.getColor(SlbLoginActivity.this, R.color.blue519AF4));
                        ds.setUnderlineText(false);
                    }
                })
                .create());
        tv_tips12.setMovementMethod(LinkMovementMethod.getInstance());

        // 强制登录bufen
        if (TextUtils.equals("true", fconfigBean.getLogin_login())) {
            iv1.setVisibility(View.INVISIBLE);
        } else {
            iv1.setVisibility(View.VISIBLE);
        }
        //
        fconfig1Presenter = new Fconfig1Presenter();
        fconfig1Presenter.onCreate(this);
        fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
        floginPresenter = new FloginPresenter();
        floginPresenter.onCreate(this);
        fgrxxPresenter = new FgrxxPresenter();
        fgrxxPresenter.onCreate(this);
        fzwdl1Presenter = new Fzwdl1Presenter();
        fzwdl1Presenter.onCreate(this);
        fim1Presenter = new Fim1Presenter();
        fim1Presenter.onCreate(this);
//        jPushDengluUtils.shouquan_cancel(other_login_name);
//        if (!TextUtils.isEmpty(SPUtils.getInstance().getString("token", ""))) {
//            set_zw();
//        }
        is_finger();
    }


    // 指纹登录bufen
    private void is_finger() {
        FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
        if (TextUtils.equals(fconfigBean.getFingerprint_fingerprint(), "true")) {
            FingerprintUtil.startFingerprintRecognition(SlbLoginActivity.this, new MyListener() {
                @Override
                public void onAuthenticateSuccess() {
                    MyLogUtil.e("FingerprintUtil", "onAuthenticateSuccess");
                    fzwdl1Presenter.getzwdl1(url, MmkvUtils.getInstance().get_common(AppCommonUtils.zhiwen, ""));
                }

                @Override
                public void onAuthenticateFailed(int helpId, String errString) {
                    String aaaa = "";
                    MyLogUtil.e("FingerprintUtil", errString + "");
                }

                @Override
                public void onAuthenticateError(int errMsgId) {
                    String aaaa = "";
                    MyLogUtil.e("FingerprintUtil", errMsgId + "");
                }

                @Override
                public void onStartAuthenticateResult(boolean isSuccess) {
                    String aaaa = "";
                    MyLogUtil.e("FingerprintUtil", isSuccess + "");
                }
            });
        }
    }

    private void onclick() {
        iv_zw.setOnClickListener(this);
        iv_xx.setOnClickListener(this);
        iv1.setOnClickListener(this);
        iv_wx.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
//        jPushDengluUtils = new JPushDengluUtils(new OnResultInfoLitener() {
//            @Override
//            public void onResults(String platform, String toastMsg, String data) {
////                Toasty.normal(BaseApp.get(), platform + "---" + toastMsg + "---" + data).show();
//                // 微信登录成功bufen
//                JSONObject jsonObject = JSONObject.parseObject(data);
//                if (jsonObject == null) {
//                    return;
//                }
//                openid = (String) jsonObject.get("openid");
//                nickName = (String) jsonObject.get("nickname");
//                gender = (int) jsonObject.get("sex") + "";
//                avatar = (String) jsonObject.get("headimgurl");
//                unionid = (String) jsonObject.get("unionid");
////                presenter.getWeChatLoginData(openid, unionid, gender, nickName, avatar);
//            }
//        });
        edt1.addTextChangedListener(textWatcher);
        edt2.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String a = edt1.getText().toString().trim();
            String b = edt2.getText().toString().trim();
            if (TextUtils.isEmpty(a)) {
                iv_xx.setVisibility(View.GONE);
            } else {
                iv_xx.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(a) && !TextUtils.isEmpty(b) && charSequence.length() > 0) {
                tv1.setEnabled(true);
                tv1.setBackgroundResource(R.drawable.btn_denglu2);
            } else {
                tv1.setEnabled(false);
                tv1.setBackgroundResource(R.drawable.btn_denglu2_enpressed);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void findview() {
        tv_title0 = findViewById(R.id.tv_title0);
        tv_title0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zhiwen();
            }
        });
        tv_title1 = findViewById(R.id.tv_title1);
        tv_tips12 = findViewById(R.id.tv_tips12);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        radAgreement = findViewById(R.id.rad_agreement);
        iv1 = findViewById(R.id.iv1);
        iv_bg1 = findViewById(R.id.iv_bg1);
        iv_xx = findViewById(R.id.iv_xx);
        ExpandViewRect.expandViewTouchDelegate(iv_xx, 30, 30, 30, 30);
        iv_zw = findViewById(R.id.iv_zw);
        iv_wx = findViewById(R.id.iv_wx);
        textinputlayout1 = findViewById(R.id.textinputlayout1);
        textinputlayout2 = findViewById(R.id.textinputlayout2);
        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);
        tv1 = findViewById(R.id.tv1);
        tv1.setEnabled(false);
        tv1.setBackgroundResource(R.drawable.btn_denglu2_enpressed);

    }

    @Override
    protected void onActResult(int requestCode, int resultCode, Intent data) {
        super.onActResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv1) {
            // 登录bufen
            //
//            MobclickAgent.onEvent(this, "Loginpage_button");
            if (!is_login()) {
                return;
            }
            denglu();
        } else if (i == R.id.iv1) {
            // 关闭bufen
            onBackPressed();
        } else if (i == R.id.iv_zw) {
            // 指纹登录
            zhiwen();
//            fzwdl1Presenter.getzwdl1(url, MmkvUtils.getInstance().get_common(AppCommonUtils.zhiwen, ""));
        } else if (i == R.id.iv_wx) {
            // 微信登录
            wxdl();
        } else if (i == R.id.iv_xx) {
            // XX
            edt1.setText("");
        } else if (i == R.id.tv2) {
            // 忘记密码
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.PhoneAct2"));
        } else if (i == R.id.tv3) {
            // 立即注册
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.PhoneAct3"));
        }
    }

    private void onLoginSuccess() {
        onLoginSuccess(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye");
    }

    private void onLoginCanceled() {
        onLoginCanceled(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye");
    }

    /**
     * 不登录操作
     */
    private void donetloginno() {
        //step 请求服务器成功后清除sp中的数据
//        SpUtils.get(this).get("", "");
        onLoginCanceled();
    }

    private boolean is_login() {
        String a = edt1.getText().toString().trim();
        String b = edt2.getText().toString().trim();
        if (TextUtils.isEmpty(a)) {
            ToastUtils.showLong(getResources().getString(R.string.yhzc_tip4));
//            YanzhengUtil.showError(textinputlayout1, "请输入您的手机号");
            return false;
        }
        if (!RegexUtils.isMobileSimple(a)) {
            ToastUtils.showLong("手机号格式不正确");
//            YanzhengUtil.showError(textinputlayout1, "请输入您的手机号");
            return false;
        }
        if (TextUtils.isEmpty(b)) {
            ToastUtils.showLong(getResources().getString(R.string.yhzc_tip422));
//                YanzhengUtil.showError(textinputlayout2, "请输入验证码");
            return false;
        }
        if (!radAgreement.isChecked()) {
            com.hjq.toast.ToastUtils.show("请勾选用户须知及隐私权政策");
            return false;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        if (fconfig1Presenter != null) {
            fconfig1Presenter.onDestory();
        }
        if (floginPresenter != null) {
            floginPresenter.onDestory();
        }
        if (fgrxxPresenter != null) {
            fgrxxPresenter.onDestory();
        }
        if (fzwdl1Presenter != null) {
            fzwdl1Presenter.onDestory();
        }
        if (fim1Presenter != null) {
            fim1Presenter.onDestory();
        }
        FingerprintCore.getInstance().cancelAuthenticate();
        FingerprintCore.getInstance().setFingerprintManager(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        MobclickAgent.onEvent(this, "Loginpage_closebutton");
        // 强制登录bufen
        if (TextUtils.equals("true", fconfigBean.getLogin_login())) {
            // tuichu
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } else {
            // hui index
            no_denglu();
        }
        super.onBackPressed();
    }

    /**
     * ----------------------------------下面为业务逻辑--分割线------------------------------
     */

    private LoadingPopupView loadingPopupView;

    /**
     * 登录
     */
    private void denglu() {
        loadingPopupView = new XPopup.Builder(SlbLoginActivity.this)
                .isDestroyOnDismiss(true)
                .asLoading("");
        loadingPopupView.show();
        floginPresenter.getlogin2(url, edt2.getText().toString().trim(), edt1.getText().toString().trim(), "");
    }

    private void zhiwen() {
        FingerprintUtil.startFingerprintRecognition(SlbLoginActivity.this, new MyListener() {
            @Override
            public void onAuthenticateSuccess() {
                MyLogUtil.e("FingerprintUtil", "onAuthenticateSuccess");
//                finish();
//                onLoginSuccess();
                fzwdl1Presenter.getzwdl1(url, MmkvUtils.getInstance().get_common(AppCommonUtils.zhiwen, ""));
            }

            @Override
            public void onAuthenticateFailed(int helpId, String errString) {
                String aaaa = "";
                MyLogUtil.e("FingerprintUtil", errString + "");
            }

            @Override
            public void onAuthenticateError(int errMsgId) {
                String aaaa = "";
                MyLogUtil.e("FingerprintUtil", errMsgId + "");
            }

            @Override
            public void onStartAuthenticateResult(boolean isSuccess) {
                String aaaa = "";
                MyLogUtil.e("FingerprintUtil", isSuccess + "");
            }
        });
    }


    /**
     * 不登录 返回原来的
     */
    private void no_denglu() {
        donetloginno();
    }

    // 微信登录
    private void wxdl() {
//        jPushDengluUtils.shouquan(other_login_name);
//        ShowLoadingUtil.showLoading(SlbLoginActivity.this, "", null);
        // test 可注掉
//        SPUtils.getInstance().put("token", "token");
        //
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //输出所有授权信息
                MyLogUtil.e("微信登录", platform.getDb().exportData());
                // 请求接口bufen
                onLoginSuccess();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        wechat.showUser(null);
    }

//    // 获取验证码bufen
//    @Override
//    public void OnErweimaSuccess(String s) {
//        edt2.requestFocus();
//    }
//
//    @Override
//    public void OnErweimaNodata(String s) {
//        ToastUtils.showLong(s);
//    }
//
//    @Override
//    public void OnErweimaFail(String s) {
//        ToastUtils.showLong(s);
//    }
//
//    @Override
//    public void OnYonghudengluSuccess(HLoginBean hLoginBean) {
////        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_forceLogin, "0");
////        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_TOKEN, hLoginBean.getToken());
//        userInfoPresenter.get_userinfo();
////        onLoginSuccess();
//    }
//
//    @Override
//    public void OnYonghudengluNodata(String s) {
//        ToastUtils.showLong(s);
////        set_token_out();
//    }
//
//    @Override
//    public void OnYonghudengluFail(String s) {
//        ToastUtils.showLong(s);
//        //TODO test
//        userInfoPresenter.get_userinfo();
////        set_token_out();
//    }
//
//    private void set_token_out() {
//        ToastUtils.showLong("登录失效，请重新登录！");
////        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_SEX);
////        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_IMG);
////        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_TEL);
////        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_NAME);
////        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_forceLogin);
////        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_TOKEN);
//    }
//
//    @Override
//    public void OnUserInfoSuccess(HUserInfoBean bean) {
////        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_SEX, bean.getChildGender());
////        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_IMG, bean.getChildAvatar());
////        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_NAME, bean.getNikeName());
////        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_TEL, bean.getPhone());
//        onLoginSuccess();
//    }
//
//    @Override
//    public void OnUserInfoNodata(String s) {
//        onLoginSuccess();
//    }
//
//    @Override
//    public void OnUserInfoFail(String s) {
//        onLoginSuccess();
//    }

    // 登录1bufen
    @Override
    public void Onlogin2Success(FloginBean bean) {
        //
        SPUtils.getInstance().put("token", bean.getToken());
        fgrxxPresenter.getgrxx(url);
//        onLoginSuccess();
    }

    @Override
    public void Onlogin2Nodata(String bean) {
        ToastUtils.showLong(bean);
    }

    @Override
    public void Onlogin2Fail(String msg) {
        ToastUtils.showLong(msg);
    }

    // 获取业务地址接口bufen
    @Override
    public void Onconfig2Success(String authorizedType, FconfigBean bean) {
        url = bean.getIdentity();
//        url = url;
//        fzwdl1Presenter.getzwdl1(url, MmkvUtils.getInstance().get_common(AppCommonUtils.zhiwen, ""));
//        denglu();
    }

    @Override
    public void Onconfig2Nodata(String bean) {

    }

    @Override
    public void Onconfig2Fail(String msg) {

    }

    // 获取个人信息bufen
    @Override
    public void OngrxxSuccess(FgrxxBean bean) {
        MmkvUtils.getInstance().set_common_json2(AppCommonUtils.userInfo, bean);
        // tencentImbufen
        FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
        fim1Presenter.getim1("/gwapi/workbenchserver/api/workbench/third/init", bean.getUserId(), bean.getUsername());
    }

    @Override
    public void OngrxxNodata(String bean) {

    }

    @Override
    public void OngrxxFail(String msg) {

    }

    // 指纹登录bufen
    @Override
    public void Onzwdl1Success(FloginBean bean) {
        SPUtils.getInstance().put("token", bean.getToken());
        fgrxxPresenter.getgrxx(url);
    }

    @Override
    public void Onzwdl1Nodata(String bean) {

    }

    @Override
    public void Onzwdl1Fail(String msg) {

    }

    @Override
    public void OnFim1Success(FImBean bean) {
        onLoginSuccess();
    }

    @Override
    public void OnFim1Nodata(String bean) {
        loadingPopupView.dismiss();
    }

    @Override
    public void OnFim1Fail(String msg) {
        loadingPopupView.dismiss();
    }
}
