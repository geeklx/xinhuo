package com.geek.applogin.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
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

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.SlbBase;
import com.geek.appcommon.bean.DwCity;
import com.geek.applogin.R;
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
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libutils.app.BaseApp;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
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

public class LoginFragment extends SlbBaseLazyFragmentNew implements View.OnClickListener, Fconfig1View, FloginView, FgrxxView, Fzwdl1View, Fim1View {

    private String tablayoutId;
    private MessageReceiverIndex mMessageReceiver;
    private ImageView iv_zw;
    private ImageView iv_wx;
    private Button im_look;

    private EditText edt1;
    private EditText edt2;
    private Button tv1;
    private TextView tv2;
    private CheckBox radAgreement;
    private TextView tv_tips12;

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
    private boolean isShwoAsPossword = true;

    public class MessageReceiverIndex extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if ("IndexF1".equals(intent.getAction())) {
//                    //TODO 发送广播bufen
//                    Intent msgIntent = new Intent();
//                    msgIntent.setAction("IndexF1");
//                    msgIntent.putExtra("query1", 0);
//                    LocalBroadcastManagers.getInstance(getActivity()).sendBroadcast(msgIntent);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void call(Object value) {
        tablayoutId = (String) value;
        ToastUtils.showLong("call->" + tablayoutId);
        MyLogUtil.e("tablayoutId->", "call->" + tablayoutId);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManagers.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        // 从缓存中拿出头像bufen

        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
//        Bundle arg = getArguments();
        if (getArguments() != null) {
            tablayoutId = getArguments().getString("tablayoutId");
            MyLogUtil.e("tablayoutId->", "onCreate->" + tablayoutId);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void setup(final View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        mMessageReceiver = new MessageReceiverIndex();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("IndexF1");
        LocalBroadcastManagers.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);

        findview(rootView);
        onclick();
        donetwork();

    }

    private void findview(View rootView) {
        tv2 = rootView.findViewById(R.id.tv2);
        radAgreement = rootView.findViewById(R.id.rad_agreement);
        iv_zw = rootView.findViewById(R.id.iv_zw);
        iv_wx = rootView.findViewById(R.id.iv_wx);
        edt1 = rootView.findViewById(R.id.edt1);
        edt2 = rootView.findViewById(R.id.edt2);
        tv1 = rootView.findViewById(R.id.tv1);
        tv1.setEnabled(false);
        tv1.setBackgroundResource(R.drawable.btn_denglu2_enpressed);
        tv_tips12 = rootView.findViewById(R.id.tv_tips12);
        im_look = rootView.findViewById(R.id.im_look);

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv1) {
            // 登录bufen
            //
//            MobclickAgent.onEvent(this, "Loginpage_button");
            if (!is_login()) {
                return;
            }
//            configtencentIm(edt1.getText().toString().trim());
//            SPUtils.getInstance().put("token", "dawdawdadadwds");
            denglu();
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
            Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.PhoneAct2");
            intent.putExtra("title", "忘记密码");
            startActivity(intent);
        } else if (i == R.id.tv3) {
            // 立即注册
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.PhoneAct3"));
        } else if (i == R.id.im_look) {
            if (isShwoAsPossword) {
                isShwoAsPossword = false;
                edt2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                im_look.setSelected(true);
                Editable etable = edt2.getText();
                Selection.setSelection(etable, etable.length());

            } else {
                isShwoAsPossword = true;
                edt2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                im_look.setSelected(false);
                Editable etable = edt2.getText();
                Selection.setSelection(etable, etable.length());

            }
        }

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
//        if (!radAgreement.isChecked()) {
//            com.hjq.toast.ToastUtils.show("请勾选用户须知及隐私权政策");
//            return false;
//        }
        return true;
    }

    private LoadingPopupView loadingPopupView;

    /**
     * 登录
     */
    private void denglu() {
        loadingPopupView = new XPopup.Builder(getActivity()).isDestroyOnDismiss(true).asLoading("");
        loadingPopupView.show();
        url = AppCommonUtils.auth_url;
        floginPresenter.getlogin2(url, edt2.getText().toString().trim(), edt1.getText().toString().trim(), "");
    }

    private void zhiwen() {
        FingerprintUtil.startFingerprintRecognition(getActivity(), new MyListener() {
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
                ToastUtils.showLong("微信登录成功");
//                onLoginSuccess();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                ToastUtils.showLong("微信登录失败");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                ToastUtils.showLong("微信登录取消");
            }
        });
        wechat.showUser(null);
    }

    private void onLoginSuccess() {

        // tencentIm
//        configtencentIm(edt1.getText().toString().trim());
        ((SlbBase) getActivity()).onLoginSuccess(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye");
//        Intent msgIntent = new Intent();
//        msgIntent.setAction("ShouyeActivity");
//        msgIntent.putExtra("updatedata", "1");
//        LocalBroadcastManagers.getInstance(getActivity()).sendBroadcast(msgIntent);
//        onLoginSuccess(AppUtils.getAppPackageName() + ".hs.act.slbapp.IndexAct");
    }

    private void onLoginCanceled() {
        ((SlbBase) getActivity()).onLoginCanceled(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye");
    }

//    private void configtencentIm(String userid) {
//        UserInfo.getInstance().setUserId(userid);
//        // 获取userSig函数
//        String userSig = GenerateTestUserSig2.genTestUserSig(userid);
//        UserInfo.getInstance().setUserSig(userSig);
//        TUIUtils.login(userid, userSig, new V2TIMCallback() {
//            @Override
//            public void onError(final int code, final String desc) {
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        ToastUtil.toastLongMessage(getString(com.tencent.qcloud.tim.demo.R.string.failed_login_tip) + ", errCode = " + code + ", errInfo = " + desc);
////                    }
////                });
//                MyLogUtil.e("TencentIM", "imLogin errorCode = " + code + ", errorInfo = " + desc);
//            }
//
//            @Override
//            public void onSuccess() {
//                MyLogUtil.e("TencentIM", "configtencentIm login success");
////                UserInfo.getInstance().setUserSig(userSig);
//                UserInfo.getInstance().setAutoLogin(true);
////                Intent intent = new Intent(SlbLoginActivity.this, MainActivity.class);
////                startActivity(intent);
////                getActivity().finish();
//                ((SlbLoginNewActivity) getActivity()).onLoginSuccess(AppUtils.getAppPackageName() + ".hs.act.slbapp.IndexAct");
//
//            }
//        });
//    }

    private void onclick() {
        iv_zw.setOnClickListener(this);
        iv_wx.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        im_look.setOnClickListener(this);
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
//            if (TextUtils.isEmpty(a)) {
//                iv_xx.setVisibility(View.GONE);
//            } else {
//                iv_xx.setVisibility(View.VISIBLE);
//            }
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


    private void donetwork() {
        //
        edt1.setText("15137615080");
        edt2.setText("123456");
        //
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tv_tips12.setText(Html.fromHtml("登录即代表您同意" + "<font color=\"#4EC0FF\">用户协议</font>" + "和" + "<font color=\"#4EC0FF\">隐私政策</font>", Html.FROM_HTML_MODE_COMPACT));
//        } else {
//            tv_tips12.setText(Html.fromHtml("登录即代表您同意" + "<font color=\"#4EC0FF\">用户协议</font>" + "和" + "<font color=\"#4EC0FF\">隐私政策</font>"));
//        }
        fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
        if (fconfigBean != null) {
//            tv_title1.setText(getApplication().getResources().getString(R.string.applogin5) + fconfigBean.getTitle_title());
            tv_tips12.setText(SpannableStringUtils.getInstance(getActivity().getApplicationContext()).getBuilder(getActivity().getApplication().getResources().getString(R.string.applogin6)).append(getActivity().getApplication().getResources().getString(R.string.applogin7)).setClickSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    HiosHelperNew.resolveAd(getActivity(), getActivity(), fconfigBean.getUser());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
//                        ds.setColor(Utils.getApp().getResources().getColor(R.color.blue519AF4));
                    ds.setColor(ContextCompat.getColor(getActivity(), R.color.defaultred));
                    ds.setUnderlineText(false);
                }
            }).append(getActivity().getApplication().getResources().getString(R.string.applogin8)).append(getActivity().getApplication().getResources().getString(R.string.applogin9)).setClickSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    HiosHelperNew.resolveAd(getActivity(), getActivity(), fconfigBean.getPrivacy());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(getActivity(), R.color.defaultred));
                    ds.setUnderlineText(false);
                }
            }).create());
            tv_tips12.setMovementMethod(LinkMovementMethod.getInstance());

//            // 强制登录bufen
//            if (TextUtils.equals("1", fconfigBean.getTitle_title())) {
//                iv1.setVisibility(View.INVISIBLE);
//            } else {
//                iv1.setVisibility(View.VISIBLE);
//            }
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
        if (fconfigBean != null && TextUtils.equals(fconfigBean.getFingerprint_fingerprint(), "true")) {
            FingerprintUtil.startFingerprintRecognition(getActivity(), new MyListener() {
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
        loadingPopupView.dismiss();
        ToastUtils.showLong(bean);
    }

    @Override
    public void Onlogin2Fail(String msg) {
        loadingPopupView.dismiss();
        ToastUtils.showLong(msg);
    }

    // 获取业务地址接口bufen
    @Override
    public void Onconfig2Success(String authorizedType, FconfigBean bean) {
        url = bean.getIdentity();
//        fzwdl1Presenter.getzwdl1(url, MmkvUtils.getInstance().get_common(AppCommonUtils.zhiwen, ""));
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
        if (!TextUtils.isEmpty(bean.getCityName())) {
            DwCity dwCity = new DwCity();
            dwCity.setName(bean.getCityName());
            MmkvUtils.getInstance().set_common_json2("city", dwCity);
        }
        // tencentImbufen
        FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
        // 灯塔2.0老系统bufen
        SPUtils.getInstance().put("password_dt20", edt2.getText().toString().trim());
        SPUtils.getInstance().put("username_dt20", edt1.getText().toString().trim());
        //
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

    @Override
    public void onDestroyView() {
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
        super.onDestroyView();
    }

}
