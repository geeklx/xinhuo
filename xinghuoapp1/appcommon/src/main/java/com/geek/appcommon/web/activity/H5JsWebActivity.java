package com.geek.appcommon.web.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.web.AndroidInterface;
import com.geek.common.R;
import com.geek.libutils.app.MyLogUtil;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.just.agentweb.geek.base.BaseAgentWebActivityJs2;
import com.just.agentweb.geek.fragment.AgentWebFragment;

public class H5JsWebActivity extends BaseAgentWebActivityJs2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtysweb3);
        LinearLayout mLinearLayout = (LinearLayout) this.findViewById(R.id.container);
        mTitleTextView.setText("灯塔有声");
    }

    @Override
    protected void onclickX(View v) {
        finish();
    }

    @Override
    protected void onclickMore(View v) {
        showPoPup(v);
    }

    @Override
    protected void javainterface() {
        if (mAgentWeb != null) {
            //注入对象
            mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb, H5JsWebActivity.this));
        }
        mBridgeWebView.registerHandler("back", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
//                function.onCallBack("submitFromWeb exe, response data 中文 from Java");
//                onBackPressed();
                MyLogUtil.e("ssssssssss", "退出了");
                ToastUtils.showLong("退出了");
            }

        });
    }


    @NonNull
    @Override
    protected ViewGroup getAgentWebParent() {
        return (ViewGroup) this.findViewById(R.id.container);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int getIndicatorColor() {
        return Color.parseColor("#ff0000");
    }

    @Override
    protected void setTitle(WebView view, String title) {
        super.setTitle(view, title);
        if (!TextUtils.isEmpty(title)) {
            if (title.length() > 10) {
                title = title.substring(0, 10).concat("...");
            }
        }
        mTitleTextView.setText(title);
    }

    @Override
    protected void addBGChild(FrameLayout frameLayout) {

        TextView mTextView = new TextView(frameLayout.getContext());
        mTextView.setText("技术由 福生佳信 提供");
        mTextView.setTextSize(16);
        mTextView.setTextColor(Color.parseColor("#727779"));
        frameLayout.setBackgroundColor(Color.parseColor("#272b2d"));
        FrameLayout.LayoutParams mFlp = new FrameLayout.LayoutParams(-2, -2);
        mFlp.gravity = Gravity.CENTER_HORIZONTAL;
        final float scale = frameLayout.getContext().getResources().getDisplayMetrics().density;
        mFlp.topMargin = (int) (15 * scale + 0.5f);
        frameLayout.addView(mTextView, 0, mFlp);
    }

    @Override
    protected int getIndicatorHeight() {
        return 3;
    }

//    @Nullable
//    @Override
//    protected String getUrl() {
//        return "http://v.dtdjzx.gov.cn/voice/";
////        return "https://m.mogujie.com/?f=mgjlm&ptp=_qd._cps______3069826.152.1.0";
//    }

    @Override
    public String getUrl() {
        String target = "";
        Intent appLinkIntent = getIntent();
        if (appLinkIntent != null) {
//            String appLinkAction = appLinkIntent.getAction();
//                if (appLinkAction != null) {
            Uri appLinkData = appLinkIntent.getData();
            if (appLinkData != null) {
                target = appLinkData.getQueryParameter(AgentWebFragment.URL_KEY);
            } else {
                //
                if (TextUtils.isEmpty(appLinkIntent.getStringExtra(AgentWebFragment.URL_KEY))) {
                    target = "http://www.jd.com/";
                } else {
                    target = appLinkIntent.getStringExtra(AgentWebFragment.URL_KEY);
                }
            }
        }
        return target;
    }

}
