package com.geek.applogin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.SlbBase;
import com.geek.appcommon.util.XZUtil;
import com.geek.biz1.bean.FconfigBean;
import com.geek.biz1.presenter.Fconfig1Presenter;
import com.geek.biz1.presenter.FtipsPresenter;
import com.geek.biz1.view.Fconfig1View;
import com.geek.biz1.view.FtipsView;
import com.geek.libswipebacklayout.SwipeBack;
import com.haier.cellarette.baselibrary.toasts3.MProgressDialog;

@SwipeBack(value = true)
public class MyChangePwdAct extends SlbBase implements Fconfig1View, FtipsView {

    private TextView tv_left;
    private TextView tv_center;
    private EditText edt1;
    private EditText edt2;
    private EditText edt3;
    private Button tv1;
    private Fconfig1Presenter fconfig1Presenter;
    private FtipsPresenter ftipsPresenter;
    private String url;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_pwd;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        findview();
        onclick();
        donetwork();
    }

    private void donetwork() {
        tv_center.setText(getApplication().getResources().getString(R.string.appmy15));
        if (fconfig1Presenter == null) {
            fconfig1Presenter = new Fconfig1Presenter();
            fconfig1Presenter.onCreate(this);
            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
        }

        if (ftipsPresenter == null) {
            ftipsPresenter = new FtipsPresenter();
            ftipsPresenter.onCreate(this);
        }
    }

    private void onclick() {
        tv_left.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                onBackPressed();
            }
        });


        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 确定bufen
                String oldpwd = edt1.getText().toString().trim();
                String pwd = edt2.getText().toString().trim();
                String newpwd = edt3.getText().toString().trim();

                if (TextUtils.isEmpty(oldpwd)) {
                    ToastUtils.showLong("请输入原密码");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin22));
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin23));
                    return;
                }
                if (!TextUtils.equals(pwd, newpwd)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin28));
                    return;
                }

                if (!XZUtil.validatePassword(pwd)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin33));
                    return;
                }

                MProgressDialog.showProgress(MyChangePwdAct.this, "请稍等...");
                ftipsPresenter.gettips_change_pwd(url, oldpwd, pwd);
            }
        });

        edt1.addTextChangedListener(textWatcher);
        edt2.addTextChangedListener(textWatcher);
        edt3.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String a = edt1.getText().toString().trim();
            String b = edt2.getText().toString().trim();
            String c = edt3.getText().toString().trim();

            if (!TextUtils.isEmpty(a) && !TextUtils.isEmpty(b) && !TextUtils.isEmpty(c) && charSequence.length() > 0) {
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
        tv_left = findViewById(R.id.tv_left);
        tv_center = findViewById(R.id.tv_center);
        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);
        edt3 = findViewById(R.id.edt3);
        tv1 = findViewById(R.id.tv1);
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
        MProgressDialog.dismissProgress();
        ToastUtils.showLong(bean);
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
}
