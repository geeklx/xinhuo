package com.geek.applogin.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.util.CountdownUtil;
import com.geek.appcommon.util.XZUtil;
import com.geek.applogin.R;
import com.geek.biz1.bean.FconfigBean;
import com.geek.biz1.bean.FinitBean;
import com.geek.biz1.presenter.Fconfig1Presenter;
import com.geek.biz1.presenter.FtipsPresenter;
import com.geek.biz1.view.Fconfig1View;
import com.geek.biz1.view.FtipsView;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libutils.app.BaseApp;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.haier.cellarette.baselibrary.toasts3.MProgressDialog;
import com.haier.cellarette.baselibrary.zothers.SpannableStringUtils;
import com.just.agentweb.geek.hois3.HiosHelperNew;

public class RegisterFragment extends SlbBaseLazyFragmentNew implements View.OnClickListener, Fconfig1View, FtipsView {

    private String tablayoutId;
    private MessageReceiverIndex mMessageReceiver;
    private ImageView iv1;

    private EditText edt1;
    private EditText edt2;
    private EditText edt3;
    private EditText edt4;
    private EditText edt5;
    private Button im_look;
    private Button tv_hqyzm;
    private Button tv1;
    private TextView tv_tips12;
    private CheckBox radAgreement;

    private Fconfig1Presenter fconfig1Presenter;
    private FtipsPresenter ftipsPresenter;
    private String url;
    private boolean isShwoAsPossword = true;
    private String requestType;

    private OnRegisterListener onRegisterListener;

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

    public void setOnRegisterListener(OnRegisterListener onRegisterListener) {
        this.onRegisterListener = onRegisterListener;
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
        return R.layout.fragment_register;
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
        edt1 = rootView.findViewById(R.id.edt1);
        edt2 = rootView.findViewById(R.id.edt2);
        edt3 = rootView.findViewById(R.id.edt3);
        edt4 = rootView.findViewById(R.id.edt4);
        edt5 = rootView.findViewById(R.id.edt5);

        tv1 = rootView.findViewById(R.id.tv1);
        tv1.setEnabled(false);
        tv1.setBackgroundResource(R.drawable.btn_denglu2_enpressed);
        tv_tips12 = rootView.findViewById(R.id.tv_tips12);
        im_look = rootView.findViewById(R.id.im_look);

        tv_hqyzm = rootView.findViewById(R.id.tv_hqyzm);
        tv_tips12 = rootView.findViewById(R.id.tv_tips12);
        radAgreement = rootView.findViewById(R.id.rad_agreement);
        tv1 = rootView.findViewById(R.id.tv1);
        tv1.setEnabled(false);
        tv1.setBackgroundResource(R.drawable.btn_denglu2_enpressed);
        FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
        tv_tips12.setText(SpannableStringUtils.getInstance(getActivity().getApplicationContext()).getBuilder("我已阅读并同意").append(getActivity().getApplication().getResources().getString(R.string.applogin7)).setClickSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, MmkvUtils.getInstance().get_common(CommonUtils.MMKV_serviceProtocol));
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
//                Uri url = Uri.parse("http://blog.51cto.com/liangxiao");
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(url);
//                startActivity(intent);
//                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, MmkvUtils.getInstance().get_common(CommonUtils.MMKV_privacyPolicy));
                HiosHelperNew.resolveAd(getActivity(), getActivity(), fconfigBean.getPrivacy());
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(getActivity(), R.color.defaultred));
                ds.setUnderlineText(false);
            }
        }).create());
        tv_tips12.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void onclick() {

        tv_hqyzm.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 获取验证码bufen
                String aaa = edt1.getText().toString().trim();
                if (TextUtils.isEmpty(aaa)) {
                    ToastUtils.showLong(getResources().getString(R.string.yhzc_tip4));
                    return;
                }

                CountdownUtil.startTime(60 * 1000, tv_hqyzm);
                ftipsPresenter.gettips(url, edt1.getText().toString().trim());
                requestType = "code";
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
                String eee = edt5.getText().toString().trim();
                if (TextUtils.isEmpty(aaa)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin10));
                    return;
                }
                if (!RegexUtils.isMobileSimple(aaa)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin34));
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

                if (!XZUtil.validatePassword(ccc)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin33));
                    return;
                }

                if (TextUtils.isEmpty(ddd)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin31));
                    return;
                }
                if (TextUtils.isEmpty(eee)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin32));
                    return;
                }

                if (!RegexUtils.isIDCard18(eee)) {
                    ToastUtils.showLong(getResources().getString(R.string.applogin35));
                    return;
                }
                if (!radAgreement.isChecked()) {
                    com.hjq.toast.ToastUtils.show(getResources().getString(R.string.applogin29));
                    return;
                }

                MProgressDialog.showProgress(getActivity(), "请稍等...");
                ftipsPresenter.getRegistertips(url + "/api/registered", ccc, aaa, bbb, eee, ddd);
                requestType = "register";
            }
        });

        im_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShwoAsPossword) {
                    isShwoAsPossword = false;
                    edt3.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    im_look.setSelected(true);
                    Editable etable = edt3.getText();
                    Selection.setSelection(etable, etable.length());

                } else {
                    isShwoAsPossword = true;
                    edt3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    im_look.setSelected(false);
                    Editable etable = edt3.getText();
                    Selection.setSelection(etable, etable.length());

                }
            }
        });

        edt1.addTextChangedListener(textWatcher);
        edt2.addTextChangedListener(textWatcher);
        edt3.addTextChangedListener(textWatcher);
        edt4.addTextChangedListener(textWatcher);
        edt5.addTextChangedListener(textWatcher);
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
            String e = edt5.getText().toString().trim();

//            if (TextUtils.isEmpty(a)) {
//                iv_xx.setVisibility(View.GONE);
//            } else {
//                iv_xx.setVisibility(View.VISIBLE);
//            }
            if (!TextUtils.isEmpty(a) && !TextUtils.isEmpty(b) && !TextUtils.isEmpty(c) && !TextUtils.isEmpty(d) && !TextUtils.isEmpty(e) /*&& radAgreement.isChecked() */ && charSequence.length() > 0) {
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
    }

    @Override
    public void onDestroyView() {
        CountdownUtil.timer_des();
        if (fconfig1Presenter != null) {
            fconfig1Presenter.onDestory();
        }
        if (ftipsPresenter != null) {
            ftipsPresenter.onDestory();
        }
        super.onDestroyView();
    }

    /**
     * 第一次进来加载bufen
     */
    private void donetwork() {
        //
        fconfig1Presenter = new Fconfig1Presenter();
        fconfig1Presenter.onCreate(this);
        fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");

        ftipsPresenter = new FtipsPresenter();
        ftipsPresenter.onCreate(this);

    }

    public interface OnRegisterListener {
        void onUpdate();
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
        MProgressDialog.dismissProgress();
        ToastUtils.showLong(bean);
        if ("register".equals(requestType)) {
            edt1.setText("");
            edt2.setText("");
            edt3.setText("");
            edt4.setText("");
            edt5.setText("");
            CountdownUtil.timer_des();
            tv_hqyzm.setEnabled(true);
            tv_hqyzm.setBackgroundResource(R.drawable.bg_hqyzm_dt);
            tv_hqyzm.setText(BaseApp.get().getResources().getString(R.string.yhzc_tip1));
            tv_hqyzm.setTextColor(ContextCompat.getColor(BaseApp.get(), R.color.defaultred));
            onRegisterListener.onUpdate();
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

}
