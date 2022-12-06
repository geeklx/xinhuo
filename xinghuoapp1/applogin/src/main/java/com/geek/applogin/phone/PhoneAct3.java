package com.geek.applogin.phone;

import android.os.Bundle;
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

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.SlbBase;
import com.geek.applogin.R;
import com.geek.biz1.bean.FconfigBean;
import com.geek.biz1.bean.FinitBean;
import com.geek.biz1.presenter.Fconfig1Presenter;
import com.geek.biz1.presenter.FtipsPresenter;
import com.geek.biz1.view.Fconfig1View;
import com.geek.biz1.view.FtipsView;
import com.geek.libbase.base.SlbBaseActivity;
import com.geek.libbase.utils.YanzhengUtil;
import com.geek.libutils.data.MmkvUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.haier.cellarette.baselibrary.qcode.ExpandViewRect;
import com.haier.cellarette.baselibrary.zothers.SpannableStringUtils;
import com.just.agentweb.geek.hois3.HiosHelperNew;

public class PhoneAct3 extends SlbBase implements View.OnClickListener, Fconfig1View, FtipsView {

    private TextView tvLeft;
    private TextView tvCenter;
    private TextView tv_right;
    private TextInputLayout textinputlayout1;
    private TextInputLayout textinputlayout2;
    private TextInputLayout textinputlayout3;
    private TextInputLayout textinputlayout4;
    private EditText edt1;
    private EditText edt2;
    private EditText edt3;
    private EditText edt4;
    private ImageView iv_xx;
    private Button tv_hqyzm;
    private Button tv1;
    private TextView tv_tips12;
    private CheckBox radAgreement;
    private String title;
    //
    private Fconfig1Presenter fconfig1Presenter;
    private FtipsPresenter ftipsPresenter;
    private String url;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zc1;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        findview();
        onclick();
        donetwork();
    }

    private void donetwork() {
        title = getIntent().getStringExtra("title");
        tvCenter.setText(title);
        //
        fconfig1Presenter = new Fconfig1Presenter();
        fconfig1Presenter.onCreate(this);
        fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
        ftipsPresenter = new FtipsPresenter();
        ftipsPresenter.onCreate(this);
//        YanzhengUtil.showSoftInputFromWindow(this, edt1);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

    }

    private void onclick() {
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_xx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // XX
                edt1.setText("");
            }
        });
        tv_hqyzm.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 获取验证码bufen
                String aaa = edt1.getText().toString().trim();
                if (TextUtils.isEmpty(aaa)) {
                    ToastUtils.showLong(getResources().getString(R.string.yhzc_tip4));
                    return;
                }
                YanzhengUtil.startTime(60 * 1000, tv_hqyzm);
                //接口通了后执行下一步
//        presentercode.get_erweima(edt1.getText().toString().trim());
                ftipsPresenter.gettips(url, edt1.getText().toString().trim());
            }
        });
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 确定bufen
                String aaa = edt1.getText().toString().trim();
                String bbb = edt2.getText().toString().trim();
                String ccc = edt3.getText().toString().trim();
                String ddd = edt4.getText().toString().trim();
                if (TextUtils.isEmpty(aaa)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin10));
                    return;
                }
                if (TextUtils.isEmpty(bbb)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin19));
                    return;
                }
                if (TextUtils.isEmpty(ccc)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin22));
                    return;
                }
                if (TextUtils.isEmpty(ddd)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin23));
                    return;
                }
                if (!TextUtils.equals(ccc, ddd)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin28));
                    return;
                }
                if (!radAgreement.isChecked()) {
                    com.hjq.toast.ToastUtils.show(getResources().getString(R.string.applogin29));
                    return;
                }
                //接口bufen
//                ToastUtils.showLong("接口部分");
                ftipsPresenter.gettips(url + "/api/registered", ccc, aaa, bbb);
            }
        });
        edt1.addTextChangedListener(textWatcher);
        edt2.addTextChangedListener(textWatcher);
        edt3.addTextChangedListener(textWatcher);
        edt4.addTextChangedListener(textWatcher);
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
            String d = edt4.getText().toString().trim();
            if (TextUtils.isEmpty(a)) {
                iv_xx.setVisibility(View.GONE);
            } else {
                iv_xx.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(a) && !TextUtils.isEmpty(b) && !TextUtils.isEmpty(c) && !TextUtils.isEmpty(d) /*&& radAgreement.isChecked() */ && charSequence.length() > 0) {
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
        tvLeft = findViewById(R.id.tv_left);
        tvCenter = findViewById(R.id.tv_center);
        tv_right = findViewById(R.id.tv_right);
        iv_xx = findViewById(R.id.iv_xx);
        ExpandViewRect.expandViewTouchDelegate(iv_xx, 30, 30, 30, 30);
        textinputlayout1 = findViewById(R.id.textinputlayout1);
        textinputlayout2 = findViewById(R.id.textinputlayout2);
        textinputlayout3 = findViewById(R.id.textinputlayout3);
        textinputlayout4 = findViewById(R.id.textinputlayout4);
        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);
        edt3 = findViewById(R.id.edt3);
        edt4 = findViewById(R.id.edt4);
        tv_hqyzm = findViewById(R.id.tv_hqyzm);
        tv_tips12 = findViewById(R.id.tv_tips12);
        radAgreement = findViewById(R.id.rad_agreement);
        tv1 = findViewById(R.id.tv1);
        tv1.setEnabled(false);
        tv1.setBackgroundResource(R.drawable.btn_denglu2_enpressed);
        FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
        tv_tips12.setText(SpannableStringUtils.getInstance(this.getApplicationContext())
                .getBuilder(getApplication().getResources().getString(R.string.applogin6))
                .append(getApplication().getResources().getString(R.string.applogin7))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
//                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, MmkvUtils.getInstance().get_common(CommonUtils.MMKV_serviceProtocol));
                        HiosHelperNew.resolveAd(PhoneAct3.this, PhoneAct3.this, fconfigBean.getUser());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
//                        ds.setColor(Utils.getApp().getResources().getColor(R.color.blue519AF4));
                        ds.setColor(ContextCompat.getColor(PhoneAct3.this, R.color.blue519AF4));
                        ds.setUnderlineText(false);
                    }
                })
                .append(getApplication().getResources().getString(R.string.applogin8))
                .append(getApplication().getResources().getString(R.string.applogin9))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
//                Uri url = Uri.parse("http://blog.51cto.com/liangxiao");
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(url);
//                startActivity(intent);
//                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, MmkvUtils.getInstance().get_common(CommonUtils.MMKV_privacyPolicy));
                        HiosHelperNew.resolveAd(PhoneAct3.this, PhoneAct3.this, fconfigBean.getPrivacy());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(ContextCompat.getColor(PhoneAct3.this, R.color.blue519AF4));
                        ds.setUnderlineText(false);
                    }
                })
                .create());
        tv_tips12.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        YanzhengUtil.timer_des();
        if (fconfig1Presenter != null) {
            fconfig1Presenter.onDestory();
        }
        if (ftipsPresenter != null) {
            ftipsPresenter.onDestory();
        }
        super.onDestroy();

    }

    /**
     * --------------------------------业务逻辑分割线----------------------------------
     */

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
    }

    @Override
    public void OntipsNodata(String bean) {
        ToastUtils.showLong(bean);
    }

    @Override
    public void OntipsFail(String msg) {
        ToastUtils.showLong(msg);
    }
}
