package com.geek.appindex.index.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.geek.appindex.R;
import com.geek.appindex.index.ShouyeInterface;
import com.geek.libutils.app.MyLogUtil;
import com.just.agentweb.IWebLayout;
import com.just.agentweb.geek.fragment.BounceWebFragment;
import com.just.agentweb.geek.widget.SmartRefreshWebLayout;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

public class ShouyeF2 extends BounceWebFragment {

    private String tablayoutId;
    private SmartRefreshWebLayout mSmartRefreshWebLayout = null;
    private SmartRefreshLayout mSmartRefreshLayout;
    private WebView mWebView;

    public static ShouyeF2 getInstance(Bundle bundle) {
        ShouyeF2 mSmartRefreshWebFragment = new ShouyeF2();
        if (mSmartRefreshWebFragment != null) {
            mSmartRefreshWebFragment.setArguments(bundle);
        }
        return mSmartRefreshWebFragment;
    }

    @Override
    protected int getIndicatorColor() {
        return Color.parseColor("#E60000");
    }

    @Override
    public String getUrl() {
        Bundle bundle = this.getArguments();
        String target = bundle.getString("url_key");
        LogUtils.e("targetaaaaaaa=" + target);
        if (TextUtils.isEmpty(target)) {
            target = "https://www.baidu.com";
        }
        return target;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agentweb_smart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mAgentWeb != null) {
            //注入对象
            mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new ShouyeInterface(mAgentWeb, this.getActivity()));
        }
        mSmartRefreshLayout = (SmartRefreshLayout) this.mSmartRefreshWebLayout.getLayout();
        mWebView = this.mSmartRefreshWebLayout.getWebView();
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mAgentWeb.getUrlLoader().reload();

                mSmartRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSmartRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });
        mSmartRefreshLayout.autoRefresh();


    }


    @Override
    protected IWebLayout getWebLayout() {
        return this.mSmartRefreshWebLayout = new SmartRefreshWebLayout(this.getActivity());
    }


    @Override
    protected void addBGChild(FrameLayout frameLayout) {

        frameLayout.setBackgroundColor(Color.TRANSPARENT);

    }

    /**
     * 底部点击bufen
     *
     * @param cateId
     * @param isrefresh
     */
    public void getCate(String cateId, boolean isrefresh) {

        if (!isrefresh) {
            // 从缓存中拿出头像bufen

            return;
        }
        mSmartRefreshLayout.autoRefresh();
    }


    /**
     * 当切换底部的时候通知每个fragment切换的id是哪个bufen
     *
     * @param cateId
     */
    public void give_id(String cateId) {
//        ToastUtils.showLong("下拉刷新啦");
        MyLogUtil.e("tablayoutId->", "give_id->" + cateId);
    }

}