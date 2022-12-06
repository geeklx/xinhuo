package com.geek.appcommon.wechat.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.geek.common.R;
import com.just.agentweb.geek.fragment.EasyWebFragment;

/**
 * Created by cenxiaozhong on 2017/7/22.
 */

public class H5WebFragment extends H5AgentWebFragment {

    private ViewGroup mViewGroup;

    public static EasyWebFragment getInstance(Bundle bundle) {
        EasyWebFragment mEasyWebFragment = new EasyWebFragment();
        if (bundle != null) {
            mEasyWebFragment.setArguments(bundle);
        }
        return mEasyWebFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mViewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_agentweb, container, false);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @NonNull
    @Override
    protected ViewGroup getAgentWebParent() {
        return (ViewGroup) this.mViewGroup.findViewById(R.id.linearLayout);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected void javainterface() {

    }

    @Override
    protected void onclickX(View v) {
        getActivity().finish();
    }

    @Override
    protected void onclickMore(View v) {
        showPoPup(v);
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

    @Nullable
    @Override
    protected String getUrl() {
        Bundle bundle = this.getArguments();
        String target = bundle.getString("tablayoutId");
        LogUtils.e("targetaaaaaaa=" + target);
        if (TextUtils.isEmpty(target)) {
            target = "https://www.baidu.com";
        }
        return target;
//        return "https://m.v.qq.com/index.html";
    }
}
