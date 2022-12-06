package com.geek.applogin;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.SlbBase;
import com.geek.biz1.bean.FconfigBean;
import com.geek.biz1.presenter.Fconfig1Presenter;
import com.geek.biz1.presenter.FtipsPresenter;
import com.geek.biz1.view.Fconfig1View;
import com.geek.biz1.view.FtipsView;
import com.geek.libutils.SlbLoginUtil;
import com.geek.libutils.data.MmkvUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

public class SlbLoginOutActivity extends SlbBase implements Fconfig1View, FtipsView {

    private LinearLayout ll_cancel;
    private LinearLayout ll_ok;
    //
    private Fconfig1Presenter fconfig1Presenter;
    private FtipsPresenter ftipsPresenter;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(R.style.ThemeTransparent2, true);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_slbloginout);
//        findview();
//        onclick();
//        donetwork();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_slbloginout;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        findview();
        onclick();
        donetwork();
    }


    private void donetwork() {
        fconfig1Presenter = new Fconfig1Presenter();
        fconfig1Presenter.onCreate(this);
        fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
        ftipsPresenter = new FtipsPresenter();
        ftipsPresenter.onCreate(this);

    }

    private LoadingPopupView loadingPopupView;

    private void onclick() {
        ll_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginOutCanceled();
            }
        });
        ll_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingPopupView = new XPopup.Builder(SlbLoginOutActivity.this)
                        .isDestroyOnDismiss(true)
                        .asLoading("");
                loadingPopupView.show();
//        loadingPopupView.dismiss();
//                loadingPopupView.delayDismiss(1200);
                ftipsPresenter.gettips_token(url, SPUtils.getInstance().getString("token", ""));

            }
        });
    }

    private void onLoginOutSuccess() {
        setResult(SlbLoginUtil.LOGINOUT_RESULT_OK);
        finish();
    }

    private void onLoginOutCanceled() {
        setResult(SlbLoginUtil.LOGINOUT_RESULT_CANCELED);
        finish();
    }

    /**
     * 登出操作
     */
    private void donetloginout() {
        //step 请求服务器成功后清除sp中的数据
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_SEX);
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_IMG);
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_TEL);
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_NAME);
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_forceLogin);
        MmkvUtils.getInstance().remove_common(AppCommonUtils.userInfo);
//        MmkvUtils.getInstance().remove_common(AppCommonUtils.zhiwen);
        SPUtils.getInstance().put("token", "");
        SPUtils.getInstance().put("token_dt20", "");
        SPUtils.getInstance().put("refresh_token_dt20", "");
        onLoginOutSuccess();
    }

    private void findview() {
        ll_ok = findViewById(R.id.ll_ok);
        ll_cancel = findViewById(R.id.ll_cancel);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

    @Override
    protected void onDestroy() {
        if (fconfig1Presenter != null) {
            fconfig1Presenter.onDestory();
        }
        if (ftipsPresenter != null) {
            ftipsPresenter.onDestory();
        }
        super.onDestroy();
    }

//    /**
//     * 隐藏软键盘
//     */
//    @Override
//    protected void hideSoftKeyboard() {
//        if (getCurrentFocus() != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                    InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }

    // 退出登录bufen
//    @Override
//    public void OnTuichudengluSuccess(String s) {
//        ToastUtils.showLong(s);
//        loadingPopupView.dismiss();
//        donetloginout();
//
//    }
//
//    @Override
//    public void OnTuichudengluNodata(String s) {
//        ToastUtils.showLong(s);
//        loadingPopupView.dismiss();
//        donetloginout();
//
//    }
//
//    @Override
//    public void OnTuichudengluFail(String s) {
//        ToastUtils.showLong(s);
//        loadingPopupView.dismiss();
//        donetloginout();
//    }

    @Override
    public void Onconfig2Success(String authorizedType, FconfigBean bean) {
        url = bean.getIdentity();
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
        loadingPopupView.dismiss();
        donetloginout();
    }

    @Override
    public void OntipsNodata(String bean) {
        ToastUtils.showLong(bean);
        loadingPopupView.dismiss();
//        donetloginout();
    }

    @Override
    public void OntipsFail(String msg) {
        ToastUtils.showLong(msg);
        loadingPopupView.dismiss();
//        donetloginout();
    }

    @Override
    public String getIdentifier() {
        return null;
    }
}
