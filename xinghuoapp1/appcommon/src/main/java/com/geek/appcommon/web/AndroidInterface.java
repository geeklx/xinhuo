package com.geek.appcommon.web;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.libbase.utils.ApkDownloadUtils;
import com.just.agentweb.AgentWeb;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;

public class AndroidInterface {

    private Handler deliver = new Handler(Looper.getMainLooper());
    private AgentWeb agent;
    private Context context;

    public AndroidInterface(AgentWeb agent, Context context) {
        this.agent = agent;
        this.context = context;
    }


    @JavascriptInterface
    public void callAndroid(final String msg) {

        deliver.post(new Runnable() {
            @Override
            public void run() {

                Log.i("Info", "main Thread:" + Thread.currentThread());
                Toast.makeText(context.getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
            }
        });


        Log.i("Info", "Thread:" + Thread.currentThread());

    }


    @JavascriptInterface
    public String getUserToken() {
//        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        return SPUtils.getInstance().getString("token_fc");
    }

    @JavascriptInterface
    public String get_feicheng_usertoken() {
//        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        return SPUtils.getInstance().getString("token");
    }

    @JavascriptInterface
    public String getUserInfo() {
//        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        return GsonUtils.toJson("");
    }

    @JavascriptInterface
    public void exitApp() {

        deliver.post(new Runnable() {
            @Override
            public void run() {
                ((Activity) context).finish();
            }
        });


        Log.i("Info", "Thread:" + Thread.currentThread());

    }

    //跳转小程序
    @JavascriptInterface
    public void startWeChatApp(String url) {

        deliver.post(new Runnable() {
            @Override
            public void run() {

            }
        });


        Log.i("Info", "Thread:" + Thread.currentThread());

    }

    //跳转三方应用
    @JavascriptInterface
    public void startThirdApp(String url) {

        deliver.post(new Runnable() {
            @Override
            public void run() {
                if (url.startsWith("com") || url.startsWith("cn")) {
                    if (!AppUtils.isAppInstalled(url)) {
                        ToastUtils.showLong("未安装此应用服务");
                        return;
                    }
                    AppUtils.launchApp(url);
                }
            }
        });


        Log.i("Info", "Thread:" + Thread.currentThread());

    }


    @JavascriptInterface
    public void downloadFile(String url) {

        ApkDownloadUtils.get().initFileDownLoader();
        String savepath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "Cellarette" + ".apk";
        String pkgname = "com.fosung.lighthouse.ezhibu";
        String pkgname2 = "com.fosung.lighthouse.ezhibu.EzhibuActApk";
        String pkgname_jump = "dataability:/.znclass.com/" +
                "com.fosung.lighthouse.ezhibu.hs.act.slbapp.EzhibuActApk?query3=aaaa&query2=45464&query1=pc";
//        if (!AppUtils.isAppInstalled(pkgname)) {
//            ApkDownloadUtils.get().zujian_loading(url, savepath, pkgname, pkgname_jump, new ApkDownloadUtils.OnLoadingStatus() {
//                public MProgressBarDialog mProgressBarDialog;
//
//                @Override
//                public void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//                    //新建一个Dialog
//                    mProgressBarDialog = new MProgressBarDialog.Builder(getActivity())
//                            .setStyle(MProgressBarDialog.MProgressBarDialogStyle_Circle)
//                            .build();
//                }
//
//                @Override
//                public void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//                    MyLogUtil.e("ssssssss", Integer.parseInt((100 * soFarBytes / totalBytes) + "") + "");
//                    if (mProgressBarDialog != null) {
//                        mProgressBarDialog.showProgress(Integer.parseInt((100 * soFarBytes / totalBytes) + ""), "加载组件：" + (Integer.parseInt((100 * soFarBytes / totalBytes) + "") + 1) + "%", true);
//                    }
//                }
//
//                @Override
//                public void completed(BaseDownloadTask task) {
//                    //
//                    if (mProgressBarDialog != null) {
//                        mProgressBarDialog.showProgress(100, "完成", true);
//                    }
//                    getActivity().getWindow().getDecorView().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mProgressBarDialog != null) {
//                                mProgressBarDialog.dismiss();
//                            }
//                            // test
//                            File saveFile = new File(savepath);
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            FileProvider7.setIntentDataAndType(getActivity(), i, "applicationnd.android.package-archive", saveFile, true);
//                            startActivity(i);
//                        }
//                    }, 100);
//                }
//
//                @Override
//                public void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//
//                }
//
//                @Override
//                public void error(BaseDownloadTask task, Throwable e) {
//                    //
//                    mProgressBarDialog.showProgress(100, "完成", true);
//                    mProgressBarDialog.dismiss();
//                }
//            });
//        }


    }

}