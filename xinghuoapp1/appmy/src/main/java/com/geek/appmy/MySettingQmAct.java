package com.geek.appmy;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.bean.AuthorStatus;
import com.geek.appcommon.util.MProgressDialogUtils;
import com.geek.biz1.bean.FconfigBean;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.presenter.Fconfig1Presenter;
import com.geek.biz1.presenter.FgrxxPresenter;
import com.geek.biz1.presenter.FtipsPresenter;
import com.geek.biz1.view.Fconfig1View;
import com.geek.biz1.view.FgrxxView;
import com.geek.biz1.view.FtipsView;
import com.geek.libbase.base.SlbBaseActivity;
import com.geek.libutils.app.BaseApp;
import com.geek.libutils.data.MmkvUtils;
import com.geek.libswipebacklayout.SwipeBack;
import com.haier.cellarette.baselibrary.toasts3.MProgressDialog;
import com.haier.cellarette.baselibrary.zothers.SpannableStringUtils;

@SwipeBack(value = true)
public class MySettingQmAct extends SlbBaseActivity implements Fconfig1View, FtipsView, FgrxxView {

    private TextView tv_left;
    private TextView tv_center;
    private TextView tv_right;
    private TextView content_num;
    private EditText et_content;
    private TextView tv_clear;
    private int num = 50;
    private String url;
    private String url4;
    private AuthorStatus needAuthorConfig = AuthorStatus.DEFAULT;
    private Fconfig1Presenter fconfig1Presenter;
    private FtipsPresenter ftipsPresenter;
    private FgrxxPresenter fgrxxPresenter;//刷新用户信息


    @Override
    protected int getLayoutId() {
        return R.layout.activity_mysettingeqm;
    }

    @Override
    public void onResume() {
        // 从缓存中拿出头像bufen
        if (fconfig1Presenter != null) {
//            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "resource");
        }
        super.onResume();
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);

        tv_left = findViewById(R.id.tv_left);
        tv_center = findViewById(R.id.tv_center);
        tv_right = findViewById(R.id.tv_right);
        tv_clear = findViewById(R.id.tv_clear);
        tv_left.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                onBackPressed();
            }
        });

        tv_center.setText("个性签名");
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("完成");

        content_num = findViewById(R.id.content_num);
        et_content = findViewById(R.id.edt_content);
        et_content.setText(fgrxxBean.getSignature());
        setEtSpan(et_content.getText().length());
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > num) {
                    setEtSpan(num);
                } else {
                    setEtSpan(et_content.getText().length());
                }
                et_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(num)});
            }
        });

        tv_clear.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                et_content.setText("");
            }
        });

        tv_right.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (!TextUtils.isEmpty(et_content.getText().toString().trim())) {

                    MProgressDialogUtils.showMprogressDialog(MySettingQmAct.this, "请稍等...");

                    //修改签名
                    ftipsPresenter.gettips_img2(url, fgrxxBean.getId(), "", et_content.getText().toString().trim(), "", "", "");
                } else {
                    ToastUtils.showLong("请输入签名内容");
                }
            }
        });


        fconfig1Presenter = new Fconfig1Presenter();
        fconfig1Presenter.onCreate(this);

        ftipsPresenter = new FtipsPresenter();
        ftipsPresenter.onCreate(this);

        fgrxxPresenter = new FgrxxPresenter();
        fgrxxPresenter.onCreate(this);

    }

    private void setEtSpan(int contentNum) {
        content_num.setText(SpannableStringUtils.getInstance(MySettingQmAct.this)
                .getBuilder("")
                .append("" + contentNum)
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(ContextCompat.getColor(BaseApp.get(), R.color.red));
                        ds.setUnderlineText(false);
                    }
                })
                .append("/50")
                .create());
    }

    @Override
    public void OntipsSuccess(String bean) {
        if (fgrxxPresenter != null) {
            fgrxxPresenter.getgrxx(url4);
        }
    }

    @Override
    public void OntipsNodata(String bean) {
        MProgressDialog.dismissProgress();
        ToastUtils.showLong(bean);
    }

    @Override
    public void OntipsFail(String msg) {
        MProgressDialog.dismissProgress();
        ToastUtils.showLong(msg);
    }

    @Override
    public void Onconfig2Success(String authorizedType, FconfigBean bean) {
        //重新获取author的路径
        if (needAuthorConfig == AuthorStatus.DEFAULT) {
            url = bean.getIdentity();
            needAuthorConfig = AuthorStatus.Loading;
            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
        } else if (needAuthorConfig == AuthorStatus.Loading) {
            needAuthorConfig = AuthorStatus.Loaded;
            url4 = bean.getIdentity();
        }
    }

    @Override
    public void Onconfig2Nodata(String bean) {
        ToastUtils.showLong(bean);
    }

    @Override
    public void Onconfig2Fail(String msg) {
        ToastUtils.showLong(msg);
    }

    @Override
    public void OngrxxSuccess(FgrxxBean bean) {
        MProgressDialog.dismissProgress();
        ToastUtils.showLong("保存成功");
        MmkvUtils.getInstance().set_common_json2(AppCommonUtils.userInfo, bean);
        finish();
    }

    @Override
    public void OngrxxNodata(String bean) {
        MProgressDialog.dismissProgress();
        ToastUtils.showLong(bean);
    }

    @Override
    public void OngrxxFail(String msg) {
        MProgressDialog.dismissProgress();
        ToastUtils.showLong(msg);
    }

    @Override
    public void onDestroy() {
        if (fconfig1Presenter != null) {
            fconfig1Presenter.onDestory();
        }

        if (ftipsPresenter != null) {
            ftipsPresenter.onDestory();
        }

        if (fgrxxPresenter != null) {
            fgrxxPresenter.onDestory();
        }

        super.onDestroy();
    }
}
