package com.geek.appcommon.web.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.web.AndroidInterface;
import com.geek.appcommon.web.activity.H5WebAct;
import com.geek.appcommon.web.bean.DTFgrxxBean;
import com.geek.appcommon.web.bean.DTRegionBean;
import com.geek.appcommon.web.share.ShareDialog;
import com.geek.common.R;
import com.geek.libbase.utils.ApkDownloadUtils;
import com.geek.libutils.SlbLoginUtil;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.haier.cellarette.baselibrary.toasts3.MProgressBarDialog;
import com.just.agentweb.WebViewClient;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.lxj.xpopup.XPopup;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * @author fosung
 */
public class H5WebFragment extends H5AgentWebFragment {

    public static final String IS_SHOW_TOOLBAR = "isShowtoolbar";

    private ViewGroup mViewGroup;

    public static H5WebFragment getInstance(Bundle bundle) {
        H5WebFragment mEasyWebFragment = new H5WebFragment();
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
    protected void initView(View view) {
        super.initView(view);
        if (getArguments().getBoolean(IS_SHOW_TOOLBAR, false)) {
            view.findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.toolbar).setVisibility(View.GONE);
        }
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

    @Nullable
    @Override
    protected WebView getWebView() {
        return mBridgeWebView;
    }

    @Nullable
    @Override
    protected WebViewClient getWebViewClient() {
        return new WebViewClient() {
            BridgeWebViewClient mBridgeWebViewClient = new BridgeWebViewClient(mBridgeWebView);

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return mBridgeWebViewClient.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (mBridgeWebViewClient.shouldOverrideUrlLoading(view, request.getUrl().toString())) {
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mBridgeWebViewClient.onPageFinished(view, url);
            }

        };
    }

    @Override
    protected void javainterface() {
        //用户信息
        mBridgeWebView.registerHandler("getUserInfo", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                DTFgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo_dt, DTFgrxxBean.class);
                JSONObject jsonObject = new JSONObject();
                if (fgrxxBean != null) {
                    try {
                        jsonObject.put("id", fgrxxBean.getResult().getUserId());
                        jsonObject.put("userId", fgrxxBean.getResult().getHash());
                        jsonObject.put("idCardHash", fgrxxBean.getResult().getIdCardHash());
                        jsonObject.put("name", fgrxxBean.getResult().getName());
                        jsonObject.put("orgName", fgrxxBean.getResult().getOrgName());
                        jsonObject.put("orgId", fgrxxBean.getResult().getOrgId());
                        jsonObject.put("orgCode", fgrxxBean.getResult().getOrgCode());
                        jsonObject.put("avatar", fgrxxBean.getResult().getLogo());
                        jsonObject.put("sourceClient", "android");
                        jsonObject.put("phone", fgrxxBean.getResult().getTelephone());
                        jsonObject.put("publishMonth", "");
                        jsonObject.put("examType", "");
//                      jsonObject.put("latitude", UserMgr.getLatitude());
//                      jsonObject.put("longitude", UserMgr.getLongitude());
                        jsonObject.put("identity", fgrxxBean.getResult().getIdentity());   //人员身份
                        function.onCallBack(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    function.onCallBack("{\"userId\":\"RuCBHvyYhIpFJ+bAGfW5GKEBN5g=\",\"idCardHash\":\"ksN9Zr66oapuQ\\/UUmQnEI5zwcHI=\",\"name\":\"韩*萍\",\"orgName\":\"中国共产党山东省委组织部党员教育中心支部委员会\",\"orgId\":\"dab02b2c-e59f-4740-ab02-b2a4077a6132\",\"orgCode\":\"000002000008000018000003000001\",\"avatar\":\"http:\\/\\/119.188.115.252:8090\\/resource-handle\\/uploads\\/image\\/2022-01-11\\/3525916993909695386.jpg\",\"sourceClient\":\"android\",\"phone\":\"15552553199\",\"publishMonth\":\"\",\"examType\":\"\",\"identity\":\"PARTY_MEMBER,CADRE\"}");
                }


            }
        });

        //定位坐标
        mBridgeWebView.registerHandler("userLnglats", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                function.onCallBack("117.129698,36.67261");
//                if (!PhoneUtils.isSimCardReady()) {
//                    // 如果没有sim卡就开启GPS，否则拿不到定位
//                    if (!LocationUtils.isGpsEnabled()) {
//                        LocationUtils.openGpsSettings();
//                    }
//                }
//                if (!LocationUtils.isGpsEnabled()) {
//                    LocationUtils.openGpsSettings();
//                } else {
//                    LocUtil.getLocation(requireContext(), new LocListener() {
//                        @Override
//                        public void success(LocationBean model) {
//                            function.onCallBack(model.getLongitude() + "," + model.getLatitude());
//                        }
//
//                        @Override
//                        public void fail(int msg) {
//                            ToastUtils.showLong("定位失败");
//                        }
//                    });
//                }
            }
        });

        //地域信息
        mBridgeWebView.registerHandler("getCityInfo", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    DTRegionBean.TreeBean region =  MmkvUtils.getInstance().get_common_json(AppCommonUtils.region, DTRegionBean.TreeBean.class);
                    if (region != null) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionName", region.admName);
                        jsonObject.put("regionCode", region.admCode);

                        jsonObject.put("admId", region.admId);
                        jsonObject.put("admCode", region.admCode);
                        jsonObject.put("admName", region.admName);
                        jsonObject.put("orgId", region.orgId);
                        jsonObject.put("orgCode", region.orgCode);
                        jsonObject.put("orgName", region.orgName);
                        function.onCallBack(jsonObject.toString());
                    } else {
                        function.onCallBack("{\"regionName\":\"济南市\",\"regionCode\":\"000002000008000001\",\"admId\":\"8d3a3fa7-3ae4-4dbe-abb6-5b282bd0db65\",\"admCode\":\"000002000008000001\",\"admName\":\"济南市\",\"orgId\":\"6cbe66a6-5b15-11e7-aa8a-0050569a68e4\",\"orgCode\":\"000002000008000001\",\"orgName\":\"中国共产党济南市委员会\"}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //token
        mBridgeWebView.registerHandler("getToken", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                String token = SPUtils.getInstance().getString("token_dt");
                function.onCallBack(token);
            }
        });

        //跳转小程序
        mBridgeWebView.registerHandler("startWeChatApp", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    JSONObject obj = new JSONObject(data);
                    String appId = obj.getString("appId");
                    String userName = obj.getString("originalId");
                    String path = obj.getString("path");
                    // 调用
                    IWXAPI api = WXAPIFactory.createWXAPI(getActivity(), appId);
                    WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                    req.userName = userName;          // 填小程序原始id
                    req.path = path;                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
                    req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
                    api.sendReq(req);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        //跳转app
        mBridgeWebView.registerHandler("startThirdApp", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                if (data.startsWith("com") || data.startsWith("cn")) {
                    if (!AppUtils.isAppInstalled(data)) {
                        ToastUtils.showLong("未安装此应用服务");
                        return;
                    }
                    AppUtils.launchApp(data);
                }
            }
        });

        //退出
        mBridgeWebView.registerHandler("exitApp", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                getActivity().onBackPressed();
                MyLogUtil.e("H5WebNomalFragment", "退出了");
            }
        });

        //退出
        mBridgeWebView.registerHandler("popWebView", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                getActivity().onBackPressed();
                MyLogUtil.e("H5WebNomalFragment", "退出了");
            }
        });

        //退出
        mBridgeWebView.registerHandler("back", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (mBridgeWebView.canGoBack()) {
                    mBridgeWebView.goBack();
                } else {
                    getActivity().onBackPressed();
                }
            }
        });

        //分享
        mBridgeWebView.registerHandler("share", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                new XPopup.Builder(getContext())
                        .asCustom(new ShareDialog(getContext()))
                        .show();
            }
        });

        mBridgeWebView.registerHandler("downloadFile", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    String filename = data.substring(data.lastIndexOf('/') + 1);
                    String savepath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + filename;
                    ApkDownloadUtils.get().zujian_loading(data, savepath, "pkgname", "pkgname_jump", new ApkDownloadUtils.OnLoadingStatus() {
                        public MProgressBarDialog mProgressBarDialog;

                        @Override
                        public void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                            //新建一个Dialog
                            mProgressBarDialog = new MProgressBarDialog.Builder(getActivity())
                                    .setStyle(MProgressBarDialog.MProgressBarDialogStyle_Circle)
                                    .build();
                        }

                        @Override
                        public void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                            MyLogUtil.e("ssssssss", Integer.parseInt((100 * soFarBytes / totalBytes) + "") + "");
                            if (mProgressBarDialog != null) {
                                mProgressBarDialog.showProgress(Integer.parseInt((100 * soFarBytes / totalBytes) + ""), "下载中：" + (Integer.parseInt((100 * soFarBytes / totalBytes) + "") + 1) + "%", true);
                            }
                        }

                        @Override
                        public void completed(BaseDownloadTask task) {
                            //
                            if (mProgressBarDialog != null) {
                                mProgressBarDialog.showProgress(100, "完成", true);
                            }
                            getActivity().getWindow().getDecorView().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mProgressBarDialog != null) {
                                        mProgressBarDialog.dismiss();
                                    }
                                    // test
//                                File saveFile = new File(savepath);
//                                Intent i = new Intent(Intent.ACTION_VIEW);
//                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                FileProvider7.setIntentDataAndType(getActivity(), i, "applicationnd.android.package-archive", saveFile, true);
//                                startActivity(i);
                                }
                            }, 100);
                        }

                        @Override
                        public void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                        }

                        @Override
                        public void error(BaseDownloadTask task, Throwable e) {
                            //
                            mProgressBarDialog.showProgress(100, "完成", true);
                            mProgressBarDialog.dismiss();
                        }
                    });
                } catch (Exception e) {

                }

            }
        });


        mBridgeWebView.registerHandler("startLink", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
//                StartUtil.INSTANCE.startWebAct(getActivity(), data, true);
                SlbLoginUtil.get().loginTowhere(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), H5WebAct.class);
                        intent.putExtra("url", data);
                        startActivity(intent);
                    }
                });
//              HiosHelperNew.resolveAd(getActivity(), getActivity(), "dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp.H5WebAct{act}?url=" + data);
            }
        });

        if (mAgentWeb != null) {
            mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb, getActivity()));
        }

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

    public void reload() {
        mBridgeWebView.reload();
    }

    public boolean onBackPressed() {
        return mAgentWeb.back();
    }

    @Nullable
    @Override
    protected String getUrl() {
        Bundle bundle = this.getArguments();
        String target = bundle.getString("tablayoutId");
        String url = bundle.getString("url");
        if (TextUtils.isEmpty(url)) {
            url = "https://www.baidu.com";
        }
        return url;
    }

}