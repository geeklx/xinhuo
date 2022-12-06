package com.geek.appcommon.wechat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.sharepop.FSharePopupView;
import com.geek.biz1.bean.FShareBean;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.bean.FinitBean;
import com.geek.common.R;
import com.geek.libbase.utils.ApkDownloadUtils;
import com.geek.libfileprovdider.AndroidFileUtil;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.haier.cellarette.baselibrary.toasts3.MProgressBarDialog;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.just.agentweb.geek.base.BaseAgentWebActivityJs2;
import com.just.agentweb.geek.fragment.AgentWebFragment;
import com.just.agentweb.geek.hois3.HiosHelperNew;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.lxj.xpopup.XPopup;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class WeChatH5JsWebActivity extends BaseAgentWebActivityJs2 {

    private PopupMenu mPopupMenu;
    private String title = "网页";
    private String roomNumber = "";
    private CountDownTimer mCuntDownTimer;
    private TextView tv1;
    private TextView tv11;

    @Override
    protected void onDestroy() {
        if (mCuntDownTimer != null) {
            mCuntDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isHideBar = getIntent().getBooleanExtra("isHideBar", false);
        setContentView(R.layout.activity_dt_common_web);
        RelativeLayout mLinearLayout = this.findViewById(R.id.container);
        RelativeLayout toolBars = this.findViewById(R.id.rl_head);
        tv1 = findViewById(R.id.tv1);
        tv11 = findViewById(R.id.tv11);
        mLinearLayout.removeView(tv1);
        mLinearLayout.removeView(tv11);
        mLinearLayout.removeView(toolBars);
        mLinearLayout.addView(tv1);
        mLinearLayout.addView(tv11);
        mLinearLayout.addView(toolBars);

//        toolBars.setVisibility(View.VISIBLE);
        if (isHideBar) {
            // 倒计时
            tv1.setVisibility(View.VISIBLE);
            tv11.setVisibility(View.VISIBLE);
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye"));// ShouyeActivity
//                    finish();
                    FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
                    HiosHelperNew.resolveAd(WeChatH5JsWebActivity.this, WeChatH5JsWebActivity.this, fconfigBean.getAdvertlinkurl());
                    finish();
                }
            });
//            tv11.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye"));// ShouyeActivity
////                    finish();
//                    FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
//                    HiosHelperNew.resolveAd(WeChatH5JsWebActivity.this, WeChatH5JsWebActivity.this, fconfigBean.getAdvertlinkurl());
//                }
//            });
            mCuntDownTimer = new CountDownTimer(5 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String value = String.valueOf((int) (millisUntilFinished / 1000));
                    tv1.setText(String.format(getResources().getString(R.string.appsplash_djs), value));
                }

                @Override
                public void onFinish() {
                    FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
                    HiosHelperNew.resolveAd(WeChatH5JsWebActivity.this, WeChatH5JsWebActivity.this, fconfigBean.getAdvertlinkurl());
                    finish();
                }
            };
            mCuntDownTimer.start();
        } else {
            // 正常
            tv1.setVisibility(View.GONE);
            tv11.setVisibility(View.GONE);
        }
        toolBars.setVisibility(isHideBar ? View.GONE : View.VISIBLE);
//        toolBars.setVisibility(View.GONE);
//        mTitleTextView.setText("灯塔有声");
    }

    @Override
    protected void onclickX(View v) {
        finish();
    }

    @Override
    protected void onclickMore(View v) {
        if (mAgentWeb != null) {
            FShareBean shareBean = new FShareBean();
            shareBean.title = title;
//                    shareBean.content = "我是内容";
            shareBean.url = mAgentWeb.getWebCreator().getWebView().getUrl();
            shareBean.isH5 = true;
//                    shareBean.imageUrl = "https://www.dtdjzx.gov.cn/u/cms/dtdjzx/202111/15155729f9n7.jpg";

            new XPopup.Builder(WeChatH5JsWebActivity.this)
                    //半透明阴影背
                    .hasShadowBg(true)
                    .asCustom(new FSharePopupView(WeChatH5JsWebActivity.this, shareBean))
                    .show();

        }
    }

    @Override
    protected void javainterface() {
        if (mAgentWeb != null) {
            //注入对象
            mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface2(mAgentWeb, WeChatH5JsWebActivity.this));
        }
        //用户信息
        mBridgeWebView.registerHandler("getUserInfo", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
//                FgrxxBean1 fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean1.class);
//                function.onCallBack(GsonUtils.toJson(fgrxxBean));
////                JSONObject jsonObject = new JSONObject();
////                if (fgrxxBean != null) {
////                    try {
////                        jsonObject.put("id", fgrxxBean.getId());
////                        jsonObject.put("token", fgrxxBean.getToken());
////                        function.onCallBack(jsonObject.toString());
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                } else {
////                    function.onCallBack("{\"id\":\"RuCBHvyYhIpFJ+bAGfW5GKEBN5g=\",\"token\":\"00cba951bcfeb263a5c561830e766d29c15e8ae6b6849108d4452c140fc2e1a3ef6383aecd0331409c2ffe038e0d8af8\"}");
////                }
            }
        });
        //退出
        mBridgeWebView.registerHandler("back", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (mBridgeWebView.canGoBack()) {
                    mBridgeWebView.goBack();
                } else {
                    onBackPressed();
                }
            }
        });
        //分享
        mBridgeWebView.registerHandler("share", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
//                new XPopup.Builder(getContext())
//                        .asCustom(new ShareDialog(getContext()))
//                        .show();
            }
        });
        // 下载
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
                            mProgressBarDialog = new MProgressBarDialog.Builder(WeChatH5JsWebActivity.this)
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
                            getWindow().getDecorView().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mProgressBarDialog != null) {
                                        mProgressBarDialog.dismiss();
                                    }
                                    // test
                                    File saveFile = new File(savepath);
//                                    Intent i = new Intent(Intent.ACTION_VIEW);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    FileProvider7.setIntentDataAndType(WeChatH5JsWebActivity.this, i,
//                                            "applicationnd.android.package-archive", saveFile, true);
//                                    startActivity(i);
                                    AndroidFileUtil.openFile(savepath);
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

        //加入会议
        mBridgeWebView.registerHandler("joinMeeting", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                XXPermissions.setScopedStorage(true);
                XXPermissions.with(WeChatH5JsWebActivity.this)
                        .permission(Permission.READ_EXTERNAL_STORAGE)
                        .permission(Permission.WRITE_EXTERNAL_STORAGE)
                        .permission(Permission.CALL_PHONE)
                        .permission(Permission.CAMERA)
                        .permission(Permission.RECORD_AUDIO)
                        .request(new OnPermissionCallback() {
                            @Override
                            public void onGranted(List<String> permissions, boolean all) {
                                try {
                                    FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
                                    JSONObject jsonObject = new JSONObject(data);
                                    roomNumber = jsonObject.getString("roomNumber");
//                                    JoinMeetingUtil.getInstance()
//                                            .joinMetting(WeChatH5JsWebActivity.this,
//                                                    roomNumber,
//                                                    fgrxxBean.getUserId(),
//                                                    "51omlbpXnYU5V+gPCKpSHGfC4Lc=");//
//                                  loginMeeting("10000", "test001");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    com.hjq.toast.ToastUtils.show("参数异常");
                                }
//                              startActivity(new Intent(getActivity(), HSTact.class));

                            }

                            @Override
                            public void onDenied(List<String> permissions, boolean never) {
//                                DoubleButtonDialog permissionDialog = new DoubleButtonDialog(WeChatH5JsWebActivity.this,
//                                        R.string.login_tip, R.string.permission_fail,
//                                        R.string.cancel, R.string.to_android_setting);
//                                permissionDialog.setOnClickListener(new DoubleButtonDialog.IOnClickListener() {
//                                    @Override
//                                    public void leftButtonClick(Dialog dialog) {
//                                        dialog.cancel();
//                                    }
//
//                                    @Override
//                                    public void rightButtonClick(Dialog dialog) {
//                                        PermissionsPageUtils pageUtils = new PermissionsPageUtils();
//                                        pageUtils.jumpPermissionPage();
//                                    }
//                                });
                            }
                        });
            }
        });
        //APPLINK
        mBridgeWebView.registerHandler("hioslink", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
//                StartUtil.INSTANCE.startWebAct(getActivity(), data, true);
//                Intent intent = new Intent(WeChatH5JsWebActivity.this, H5WebAct.class);
//              HiosHelperNew.resolveAd(getActivity(), getActivity(), "dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp.H5WebAct{act}?url=" + data);
                HiosHelperNew.resolveAd(WeChatH5JsWebActivity.this, WeChatH5JsWebActivity.this, data);
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
                    IWXAPI api = WXAPIFactory.createWXAPI(WeChatH5JsWebActivity.this, appId);
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

        //跳转第三方app
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

        //刷新
        mBridgeWebView.registerHandler("refreshapp", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                if (mAgentWeb != null) {
                    mAgentWeb.getUrlLoader().reload(); // 刷新
                }
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
        return Color.parseColor("#E40E02");
    }

    @Override
    protected void setTitle(WebView view, String title) {
        super.setTitle(view, title);
        this.title = title;
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
//        target = "https://v.youku.com/v_show/id_XNTgwMTY2NDkzMg==.html?spm=a2ha1.14919748_WEBGAME_JINGXUAN.drawer4.d_zj1_2&scm=20140719.manual.4471.video_XNTgwMTY2NDkzMg%3D%3D";
//        target = "http://119.188.115.245:7107/branch-features";
        return target;
    }

}
