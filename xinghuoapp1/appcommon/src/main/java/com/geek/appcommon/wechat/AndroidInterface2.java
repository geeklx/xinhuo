package com.geek.appcommon.wechat;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.just.agentweb.AgentWeb;

public class AndroidInterface2 {

    private Handler deliver = new Handler(Looper.getMainLooper());
    private AgentWeb agent;
    private Context context;

    public AndroidInterface2(AgentWeb agent, Context context) {
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
    public void exitApp() {

        deliver.post(new Runnable() {
            @Override
            public void run() {
                ((WeChatH5JsWebActivity) context).finish();
            }
        });


        Log.i("Info", "Thread:" + Thread.currentThread());

    }

    @JavascriptInterface
    public String get_feicheng_usertoken() {
//        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        ToastUtils.showLong("进来了~");
        return SPUtils.getInstance().getString("token_fc");
    }

    @JavascriptInterface
    public String get_platform() {
//        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);

        return AppUtils.getAppPackageName();
    }


}