package com.geek.appindex.index.beifen;//package com.geek.appindex.index;
//
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.webkit.WebView;
//import android.widget.LinearLayout;
//
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//
//import com.blankj.utilcode.util.LogUtils;
//import com.geek.appindex.R;
//import com.geek.libbase.base.SlbBaseLazyFragmentNew;
//import com.geek.libutils.app.MyLogUtil;
//import com.just.agentweb.AgentWeb;
//import com.just.agentweb.WebChromeClient;
//import com.just.agentweb.WebViewClient;
//import com.scwang.smart.refresh.layout.SmartRefreshLayout;
//import com.scwang.smart.refresh.layout.api.RefreshLayout;
//import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
//import com.scwang.smart.refresh.layout.listener.ScrollBoundaryDecider;
//
//
//public class ShouyeF2beifen extends SlbBaseLazyFragmentNew {
//
//    private AgentWeb mAgentWeb;
//    private LinearLayout ll_base_container2;
//    private SmartRefreshLayout smarkLayout;
//    private boolean is_first;
//    private String tablayoutId;
//
//    private String getUrl() {
//        Bundle bundle = this.getArguments();
//        String target = bundle.getString("url_key");
//        if (TextUtils.isEmpty(target)) {
//            target = "http://www.jd.com/";
//        }
//        LogUtils.e("targetaaaaaaa=" + target);
//        return target;
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_shouyef2;
//    }
//
//    @Override
//    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
//        super.setup(rootView, savedInstanceState);
//        smarkLayout = rootView.findViewById(R.id.smarkLayout);
//        ll_base_container2 = rootView.findViewById(R.id.ll_base_container2);
//        //
//        mAgentWeb = AgentWeb.with(this)
//                .setAgentWebParent(ll_base_container2, new LinearLayout.LayoutParams(-1, -1))
//                .useDefaultIndicator(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
//                .setWebChromeClient(mWebChromeClient)
//                .setWebViewClient(mWebViewClient)
//                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
//                .createAgentWeb()
//                .ready()
//                .go(getUrl());
//        //????????????
//        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new ShouyeInterface(mAgentWeb, getActivity()));
//        //
//        smarkLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                smarkLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        smarkLayout.finishRefresh();
//                        if (!is_first) {
//                            is_first = true;
//                            return;
//                        }
//                        mAgentWeb.getUrlLoader().reload();
//                    }
//                }, 0);
//            }
//        });
//        //?????????????????????????????????
//        smarkLayout.setEnableAutoLoadMore(false);
//        //?????????????????????1.0.4???????????????
//        smarkLayout.setEnableOverScrollDrag(false);
//        //????????????????????????
//        smarkLayout.setEnableOverScrollBounce(false);
//        smarkLayout.setScrollBoundaryDecider(new ScrollBoundaryDecider() {
//            @Override
//            public boolean canRefresh(View content) {
//                //webview????????????????????????????????????
//                MyLogUtil.e("ssssss", "" + mAgentWeb.getWebCreator().getWebView().getScrollY());
//                return false;
////                return mAgentWeb.getWebCreator().getWebView().getScrollY() == 0;
//            }
//
//            @Override
//            public boolean canLoadMore(View content) {
//                return false;
//            }
//        });
//        smarkLayout.autoRefresh();
//    }
//
//    private WebViewClient mWebViewClient = new WebViewClient() {
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            //do you  work
//        }
//    };
//
//    private WebChromeClient mWebChromeClient = new WebChromeClient() {
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//            //do you work
//        }
//    };
//
////    @Override
////    public boolean onKeyDown(int keyCode, KeyEvent event) {
////
////        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
////            return true;
////        }
////        return super.onKeyDown(keyCode, event);
////    }
//
//    @Override
//    public void onPause() {
//        mAgentWeb.getWebLifeCycle().onPause();
//        super.onPause();
//
//    }
//
//    @Override
//    public void onResume() {
//        mAgentWeb.getWebLifeCycle().onResume();
//        super.onResume();
//    }
//
//    @Override
//    public void onDestroyView() {
//        mAgentWeb.getWebLifeCycle().onDestroy();
//        super.onDestroyView();
//    }
//
//    /**
//     * ????????????bufen
//     *
//     * @param cateId
//     * @param isrefresh
//     */
//    public void getCate(String cateId, boolean isrefresh) {
//
//        if (!isrefresh) {
//            // ????????????????????????bufen
//
//            return;
//        }
//        smarkLayout.autoRefresh();
//    }
//
//    /**
//     * ????????????????????????????????????fragment?????????id?????????bufen
//     *
//     * @param cateId
//     */
//    public void give_id(String cateId) {
////        ToastUtils.showLong("???????????????");
//        MyLogUtil.e("tablayoutId->", "give_id->" + cateId);
//    }
//
//}