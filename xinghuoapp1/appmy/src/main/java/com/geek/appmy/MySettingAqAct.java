package com.geek.appmy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.slbyanzheng.zhiwen2.CustomDialog;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.interfaces.CommonXPopupListener;
import com.geek.appcommon.SlbBase;
import com.geek.appcommon.bean.AuthorStatus;
import com.geek.biz1.bean.FaqzxBean;
import com.geek.biz1.bean.FconfigBean;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.presenter.FaqzxPresenter;
import com.geek.biz1.presenter.Fconfig1Presenter;
import com.geek.biz1.presenter.FgrxxPresenter;
import com.geek.biz1.presenter.FtipsPresenter;
import com.geek.biz1.view.FaqzxView;
import com.geek.biz1.view.Fconfig1View;
import com.geek.biz1.view.FgrxxView;
import com.geek.biz1.view.FtipsView;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.geek.libswipebacklayout.SwipeBack;
import com.haier.cellarette.baselibrary.switchbutton.SwitchButtonK;
import com.lib.lock.fingerprint.core.FingerprintCore;
import com.lib.lock.fingerprint.core.MyListener;
import com.lib.lock.fingerprint.utils.FingerprintUtil;
import com.lib.lock.fingerprint.utils.FingerprintUtil2;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.util.XPopupUtils;

@SwipeBack(value = true)
public class MySettingAqAct extends SlbBase implements Fconfig1View, FaqzxView, FtipsView, FgrxxView {

    private TextView tv_left;
    private TextView tv_center;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private SwitchButtonK sb_ios;
    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private RelativeLayout rl3;
    private RelativeLayout rl4;
    private Fconfig1Presenter fconfig1Presenter;
    private FaqzxPresenter faqzxPresenter;
    private FtipsPresenter ftipsPresenter;
    private FgrxxPresenter fgrxxPresenter;
    private String url;
    private String url1;
    private AuthorStatus needAuthorConfig = AuthorStatus.DEFAULT;


    @Override
    protected void onDestroy() {
        if (fconfig1Presenter != null) {
            fconfig1Presenter.onDestory();
        }
        if (faqzxPresenter != null) {
            faqzxPresenter.onDestory();
        }
        if (ftipsPresenter != null) {
            ftipsPresenter.onDestory();
        }
        if (fgrxxPresenter != null) {
            fgrxxPresenter.onDestory();
        }
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mysettingaq;
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
        tv_center.setText(getApplication().getResources().getString(R.string.appmy14));
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        sb_ios = findViewById(R.id.sb_ios5);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);

        retryData();
        //
//        sb_ios.setChecked(Boolean.parseBoolean(MmkvUtils.getInstance().get_common("zhiwendenglu", "false")));
        sb_ios.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    sb_ios.setEnabled(isChecked);
                // zhiwen
                if (!FingerprintCore.getInstance().isSupport()) {
                    ToastUtils.showLong("系统不支持指纹识别");
                    sb_ios.setChecked(!isChecked);
                    return;
                }
                if (!FingerprintCore.getInstance().isHasEnrolledFingerprints()) {
                    ToastUtils.showLong("请检查是否录入指纹");
                    final CustomDialog selfDialog = new CustomDialog(MySettingAqAct.this);
                    View view = LayoutInflater.from(MySettingAqAct.this).inflate(com.example.slbyanzheng.R.layout.login_finger_change_dialog, null, false);
                    view.findViewById(com.example.slbyanzheng.R.id.open_fingerprint).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FingerprintUtil.openFingerPrintSettingPage(getApplicationContext());
                        }
                    });
                    selfDialog.setCustom(view);
                    selfDialog.show();
                    sb_ios.setChecked(!isChecked);
                    return;
                }
                FingerprintUtil.startFingerprintRecognition(MySettingAqAct.this, new MyListener() {
                    @Override
                    public void onAuthenticateSuccess() {
                        MyLogUtil.e("FingerprintUtil", "onAuthenticateSuccess");
                        MmkvUtils.getInstance().set_common(AppCommonUtils.zhiwen, FingerprintUtil2.getFingerprint(MySettingAqAct.this) + "");
                        setupdate("", "");
                    }

                    @Override
                    public void onAuthenticateFailed(int helpId, String errString) {
                        String aaaa = "";
                        MyLogUtil.e("FingerprintUtil", errString + "");
                        sb_ios.setChecked(!isChecked);
                    }

                    @Override
                    public void onAuthenticateError(int errMsgId) {
                        String aaaa = "";
                        MyLogUtil.e("FingerprintUtil", errMsgId + "");
                        sb_ios.setChecked(!isChecked);
                    }

                    @Override
                    public void onStartAuthenticateResult(boolean isSuccess) {
                        String aaaa = "";
                        MyLogUtil.e("FingerprintUtil", isSuccess + "");
                    }
                });
            }
        });
        //
        fconfig1Presenter = new Fconfig1Presenter();
        fconfig1Presenter.onCreate(this);
        fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "resource");
        faqzxPresenter = new FaqzxPresenter();
        faqzxPresenter.onCreate(this);
        ftipsPresenter = new FtipsPresenter();
        ftipsPresenter.onCreate(this);
        fgrxxPresenter = new FgrxxPresenter();
        fgrxxPresenter.onCreate(this);
        //
        rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.PhoneAct2");
                intent.putExtra("title", "修改密码");
                startActivity(intent);
            }
        });
        rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new XPopup.Builder(MySettingAqAct.this)
                        .maxWidth((int) (XPopupUtils.getWindowWidth(MySettingAqAct.this) * 0.8f))
                        .hasStatusBarShadow(false)
                        //.dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        .autoOpenSoftInput(true)
                        .isDarkTheme(false)
                        .setPopupCallback(new CommonXPopupListener())
//                        .autoFocusEditText(false) //是否让弹窗内的EditText自动获取焦点，默认是true
                        //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                        .asInputConfirm(getApplication().getResources().getString(R.string.applogin1),
                                getApplication().getResources().getString(R.string.appmy16), null,
                                getApplication().getResources().getString(R.string.appmy21),
                                new OnInputConfirmListener() {
                                    @Override
                                    public void onConfirm(String text) {
                                        tv1.setText(text);
                                        //jiekoubufen
                                        setupdate("telephone", text);

                                    }
                                })
                        .show();
            }
        });
        rl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new XPopup.Builder(MySettingAqAct.this)
                        .maxWidth((int) (XPopupUtils.getWindowWidth(MySettingAqAct.this) * 0.8f))
                        .hasStatusBarShadow(false)
                        //.dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        .autoOpenSoftInput(true)
                        .isDarkTheme(false)
                        .setPopupCallback(new CommonXPopupListener())
//                        .autoFocusEditText(false) //是否让弹窗内的EditText自动获取焦点，默认是true
                        //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                        .asInputConfirm(getApplication().getResources().getString(R.string.applogin1),
                                getApplication().getResources().getString(R.string.appmy17), null,
                                getApplication().getResources().getString(R.string.appmy21),
                                new OnInputConfirmListener() {
                                    @Override
                                    public void onConfirm(String text) {
                                        tv2.setText(text);
                                        //jiekoubufen
                                        setupdate("fixedTelephone", text);

                                    }
                                })
                        .show();
            }
        });
        rl4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new XPopup.Builder(MySettingAqAct.this)
                        .maxWidth((int) (XPopupUtils.getWindowWidth(MySettingAqAct.this) * 0.8f))
                        .hasStatusBarShadow(false)
                        //.dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        .autoOpenSoftInput(true)
                        .isDarkTheme(false)
                        .setPopupCallback(new CommonXPopupListener())
//                        .autoFocusEditText(false) //是否让弹窗内的EditText自动获取焦点，默认是true
                        //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                        .asInputConfirm(getApplication().getResources().getString(R.string.applogin1),
                                getApplication().getResources().getString(R.string.appmy18), null,
                                getApplication().getResources().getString(R.string.appmy21),
                                new OnInputConfirmListener() {
                                    @Override
                                    public void onConfirm(String text) {
                                        tv3.setText(text);
                                        //jiekoubufen
                                        setupdate("email", text);

                                    }
                                })
                        .show();
            }
        });
    }

    private void retryData() {
        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        if (fgrxxBean != null) {
            tv1.setText(fgrxxBean.getPhonenum());
            tv2.setText(fgrxxBean.getFixedTelephone());
            tv3.setText(fgrxxBean.getHostmail());
        }
    }

    private void setupdate(String type, String text) {
        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        if (fgrxxBean != null) {
            if (type.equals("telephone")) {
                ftipsPresenter.gettips_aq2(url, fgrxxBean.getId(), text, "", "");
            } else if (type.equals("email")) {
                ftipsPresenter.gettips_aq2(url, fgrxxBean.getId(), "", text, "");
            } else if (type.equals("fixedTelephone")) {
                ftipsPresenter.gettips_aq2(url, fgrxxBean.getId(), "", "", text);
            }
        }
//        ftipsPresenter.gettips_aq1(url,
//                tv1.getText().toString().trim(),
//                tv3.getText().toString().trim(),
//                tv2.getText().toString().trim(),
//                sb_ios.isChecked() + "",
//                MmkvUtils.getInstance().get_common(AppCommonUtils.zhiwen, ""));
    }

    @Override
    public void OnaqzxSuccess(FaqzxBean bean) {
        tv1.setText(bean.getTelphone());
        tv2.setText(bean.getLandline());
        tv3.setText(bean.getHostmail());
        //sb_ios.setChecked(Boolean.parseBoolean(bean.getIsfinger() + ""));
    }

    @Override
    public void OnaqzxNodata(String bean) {
        ToastUtils.showLong(bean);
    }

    @Override
    public void OnaqzxFail(String msg) {
        ToastUtils.showLong(msg);
    }

    @Override
    public void Onconfig2Success(String authorizedType, FconfigBean bean) {
//        if (TextUtils.equals("resource", bean.getServerType())) {
//            url1 = bean.getIdentity();
//            faqzxPresenter.getaqzx(url1);
//        }

        //重新获取author的路径
        if (needAuthorConfig == AuthorStatus.DEFAULT) {
            url = bean.getIdentity();
            needAuthorConfig = AuthorStatus.Loading;
            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
        } else if (needAuthorConfig == AuthorStatus.Loading) {
            needAuthorConfig = AuthorStatus.Loaded;
            url1 = bean.getIdentity();
        }
//        faqzxPresenter.getaqzx(url);
    }

    @Override
    public void Onconfig2Nodata(String bean) {

    }

    @Override
    public void Onconfig2Fail(String msg) {

    }

    @Override
    public void OntipsSuccess(String bean) {
        ToastUtils.showLong(bean);
        if (url1 != null && !TextUtils.isEmpty(url1)) {
            fgrxxPresenter.getgrxx(url1);
        }

    }

    @Override
    public void OntipsNodata(String bean) {
        ToastUtils.showLong(bean);
    }

    @Override
    public void OntipsFail(String msg) {
        ToastUtils.showLong(msg);
    }

    @Override
    public void OngrxxSuccess(FgrxxBean bean) {
        MmkvUtils.getInstance().set_common_json2(AppCommonUtils.userInfo, bean);
        retryData();
        Intent intent = new Intent();
        intent.setAction("refreshAction");
        LocalBroadcastManagers.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void OngrxxNodata(String bean) {
        ToastUtils.showLong(bean);
    }

//    @Override
//    protected void refreshToken() {
//        if (fgrxxPresenter != null) {
//            fgrxxPresenter.getgrxx(url1);
//        }
//    }

    @Override
    public void OngrxxFail(String msg) {
        ToastUtils.showLong(msg);
    }
}
