package com.geek.appcommon.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.SlbBase;
import com.geek.biz1.bean.FshengjiBean;
import com.geek.biz1.bean.HMobid2Bean;
import com.geek.biz1.bean.SbbdBean;
import com.geek.biz1.presenter.FshengjiPresenter;
import com.geek.biz1.presenter.HMobID2Presenter;
import com.geek.biz1.presenter.HMobIDPresenter;
import com.geek.biz1.presenter.SbbdPresenter;
import com.geek.biz1.view.FshengjiView;
import com.geek.biz1.view.HMobID2View;
import com.geek.biz1.view.HMobIDView;
import com.geek.biz1.view.SbbdView;
import com.geek.common.R;
import com.geek.libupdateapp.util.UpdateAppUtils;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.jiami.Md5Utils;
import com.google.gson.Gson;
import com.lxj.xpopup.core.BasePopupView;
import com.mob.pushsdk.MobPush;
import com.mob.pushsdk.MobPushCustomMessage;
import com.mob.pushsdk.MobPushNotifyMessage;
import com.mob.pushsdk.MobPushReceiver;

import java.util.List;

public class MOBIDservices extends Service implements HMobIDView, HMobID2View, SbbdView, FshengjiView {

    // test
    private HMobIDPresenter presenter_mobid;
    private HMobID2Presenter presenter_mobid2;
    private SbbdPresenter presenter3;
    private FshengjiPresenter shengjiPresenter;
    private Handler handler;
    private String apkPath = "";
    private int serverVersionCode = 0;
    private String serverVersionName = "";
    private String updateInfoTitle = "";
    private String updateInfo = "";
    private String md5 = "";
    private String appPackageName = "";
    public static final String TAG_UPGRADE = "upgrade";//更新

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }

    @Override
    public void OnMobIDSuccess(String bean) {
        MyLogUtil.e("MobPush", "后台接口初始化推送成功");

    }

    @Override
    public void OnMobIDNodata(String bean) {

    }

    @Override
    public void OnMobIDFail(String msg) {

    }

    @Override
    public void OnMobID2Success(HMobid2Bean bean) {
        MyLogUtil.e("MobPush推送成功2");
        //TODO 发送广播bufen
        Intent msgIntent = new Intent();
        msgIntent.setAction("ShouyeActivity");
        msgIntent.putExtra("mobid", bean.totalCount);
        LocalBroadcastManagers.getInstance(this).sendBroadcast(msgIntent);
    }

    @Override
    public void OnMobID2Nodata(String bean) {

    }

    @Override
    public void OnMobID2Fail(String msg) {

    }

    @Override
    public String getIdentifier() {
        return System.currentTimeMillis() + "";
    }

    @Override
    public void OnSbbdSuccess(SbbdBean versionInfoBean) {
        SPUtils.getInstance().put("accessSecret", versionInfoBean.getAccessSecret());
        SPUtils.getInstance().put("accessKey", versionInfoBean.getAccessKey());
        SPUtils.getInstance().put("version", versionInfoBean.getSignVersion());

    }

    @Override
    public void OnSbbdNodata(String bean) {

    }

    @Override
    public void OnSbbdFail(String msg) {

    }

    @Override
    public void OnFshengjiSuccess(FshengjiBean bean) {
        apkPath = bean.getApkPath();
        serverVersionCode = Integer.valueOf(bean.getServerVersionCode());
        serverVersionName = bean.getServerVersionName();
        updateInfoTitle = bean.getUpdateInfoTitle();
        updateInfo = bean.getUpdateInfo();
        if (TextUtils.isEmpty(apkPath)) {
            return;
        }
        SlbBase topActivity = (SlbBase) ActivityUtils.getTopActivity();

        if (topActivity != null && (topActivity.getClass().getName() != "com.geek.appsplash.WelComeActivity")) {
            //必须是slbBase的activity才进行更新操作
            if (ActivityUtils.isActivityAlive(topActivity)) {
                UpdateAppUtils.from(topActivity)
                        .serverVersionCode(serverVersionCode)
                        .serverVersionName(serverVersionName)
                        .downloadPath("apks/" + getPackageName() + ".apk")
                        .showProgress(true)
                        .isForce(bean.getIsForce())
                        .apkPath(apkPath)
                        .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)    //default
                        .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //default
                        .updateInfoTitle(updateInfoTitle)
                        .updateInfo(updateInfo.replace("|", "\n"))
//                    .showNotification(true)
//                    .needFitAndroidN(true)
                        .update();
            }

        }

    }

    @Override
    public void OnFshengjiNodata(String bean) {

    }

    @Override
    public void OnFshengjiFail(String msg) {

    }


    public class MsgBinder extends Binder {
        public MOBIDservices getService() {
            return MOBIDservices.this;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent it = new Intent(this, MOBIDServicesBg.class);
        it.putExtra(MOBIDServicesBg.EXTRA_NOTIFICATION_ID, HUYAN_MANAGE_NOTIFICATION_ID);
        startService(it);
        // test
        presenter_mobid = new HMobIDPresenter();
        presenter_mobid.onCreate(this);
        presenter_mobid2 = new HMobID2Presenter();
        presenter_mobid2.onCreate(this);
        presenter3 = new SbbdPresenter();
        presenter3.onCreate(this);
        shengjiPresenter = new FshengjiPresenter();
        shengjiPresenter.onCreate(this);
        apkPath = "";
        updateInfoTitle = "";
        updateInfo = "";
        serverVersionCode = AppUtils.getAppVersionCode();
        serverVersionName = AppUtils.getAppVersionName();
        appPackageName = AppUtils.getAppPackageName();
        md5 = Md5Utils.get32Md5LowerCase(appPackageName);
//        MobSDK.init(this);
//        //防止多进程注册多次  可以在MainActivity或者其他页面注册MobPushReceiver
//        String processName = getProcessName(this);
//        MobPush.getRegistrationId(new MobPushCallback<String>() {
//
//            @Override
//            public void onCallback(String rid) {
//                System.out.println("MobPush->RegistrationId:" + rid);
//                SPUtils.getInstance().put("MOBID", rid);
//                // test
//                presenter_mobid.get_mob_id();
//            }
//        });
        MobPush.setClickNotificationToLaunchMainActivity(false);
        MobPush.addPushReceiver(new MobPushReceiver() {
            @Override
            public void onCustomMessageReceive(Context context, MobPushCustomMessage message) {
                //接收自定义消息(透传)
                System.out.println("MobPush onCustomMessageReceive:" + message.toString());
            }

            @Override
            public void onNotifyMessageReceive(Context context, MobPushNotifyMessage message) {
                //接收通知消
                System.out.println("MobPush onNotifyMessageReceive:" + message.toString());
//                try {
//                    String schemeData = message.getExtrasMap().get("schemeData");
//                    if (message.getTitle() != null && !"".equals(message.getTitle())) {
//                        if (message.getTitle().equals("公告推送")) {
//                            Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.AdNaticeActivity");
//                            intent.putExtra("url1", "https://www.baidu.com/");
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("content", "公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
//                            startActivity(intent);
//                        } else if (message.getTitle().equals("推送通知")) {
//                            Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.AdPushNaticeActivity");
//                            intent.putExtra("url1", "https://www.baidu.com/");
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("content", "公告摘要描述公告摘要描述公告，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
//                            startActivity(intent);
//                        } else if (message.getTitle().equals("业务推送")) {
//                            Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.AdBusinessNaticeActivity");
//                            intent.putExtra("url1", "https://www.baidu.com/");
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("content", "公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
//                            startActivity(intent);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e("schemeData", "推送消息处理异常");
//                }

//                Message msg = new Message();
//                msg.what = 1;
//                msg.obj = "Message Receive:" + message.toString();
//                handler.sendMessage(msg);


//                String str = "{\n" +
//                        "    \"content\": \"透传推送测试\n" +
//                        "1.添加灯塔2.0用户中心...\",\n" +
//                        "    \"extrasMap\": {\n" +
//                        "        \"channel\": \"mobpush\",\n" +
//                        "        \"id\": \"4btjdv04to6zgbxp1c\",\n" +
//                        "        \"mobpush_link_k\": \"default_schemeUrl?msgId=3535346917787581613&type=upgrade\",\n" +
//                        "        \"mobpush_link_v\": \"msgId=3535346917787581613&type=upgrade\",\n" +
//                        "        \"pushData\": \"{\"mobpush_link_k\":\"default_schemeUrl?msgId=3535346917787581613&type=upgrade\",\"mobpush_link_v\":\"msgId=3535346917787581613&type=upgrade\"}\",\n" +
//                        "        \"schemeData\": \"{\"msgId\":\"3535346917787581613\",\"type\":\"upgrade\"}\"\n" +
//                        "    },\n" +
//                        "    \"messageId\": \"4btjdv04to6zgbxp1c\",\n" +
//                        "    \"offlineFlag\": 0,\n" +
//                        "    \"timestamp\": 1646274195391\n" +
//                        "}";

//                try {
//                    String str = "{\"msgId\":\"3535354693825869254\",\"type\":\"upgrade\"}";
//                    PushMessageBean pushMessageBean = new Gson().fromJson(str, PushMessageBean.class);
//
//                    String type = pushMessageBean.getType();
//                    if (pushMessageBean != null && !TextUtils.isEmpty(type)) {
//                        switch (type) {
//                            case "upgrade":
//                                shengjiPresenter.getshengji(AppCommonUtils.auth_url,
//                                        serverVersionCode + "", serverVersionName, appPackageName, md5);
//                                break;
//                        }
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e("schemeData", "推送消息处理异常");
//                }
                try {
                    String schemeData = message.getExtrasMap().get("schemeData");
                    if (!TextUtils.isEmpty(schemeData)) {
                        PushMessageBean pushMessageBean = new Gson().fromJson(schemeData, PushMessageBean.class);
                        String type = pushMessageBean.getType();
                        if (pushMessageBean != null && !TextUtils.isEmpty(type)) {
                            switch (type) {
                                case TAG_UPGRADE:
                                    shengjiPresenter.getshengji(AppCommonUtils.auth_url,
                                            serverVersionCode + "", serverVersionName, appPackageName, md5);
                                    break;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("schemeData", "推送消息处理异常");
                }

            }

            @Override
            public void onNotifyMessageOpenedReceive(Context context, MobPushNotifyMessage message) {
                //接收通知消息被点击事件
                System.out.println("MobPush onNotifyMessageOpenedReceive:" + message.toString());
                System.out.println("MobPush onNotifyMessageOpenedReceive:" + message.getContent());
                Message msg = new Message();
                msg.what = 2;
                msg.obj = message.getTitle();
                handler.sendMessage(msg);
            }

            @Override
            public void onTagsCallback(Context context, String[] tags, int operation, int errorCode) {
                //接收tags的增改删查操作
                System.out.println("MobPush onTagsCallback:" + operation + "  " + errorCode);
            }

            @Override
            public void onAliasCallback(Context context, String alias, int operation, int errorCode) {
                //接收alias的增改删查操作
                System.out.println("MobPush onAliasCallback:" + alias + "  " + operation + "  " + errorCode);
            }
        });

        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.obj.toString() != null && !"".equals(msg.obj.toString())) {
                    if (msg.obj.toString().equals("公告推送")) {
                        Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.AdNaticeActivity");
                        intent.putExtra("url1", "https://www.baidu.com/");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("content", "公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                        startActivity(intent);
                    } else if (msg.obj.toString().equals("推送通知")) {
                        Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.AdPushNaticeActivity");
                        intent.putExtra("url1", "https://www.baidu.com/");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("content", "公告摘要描述公告摘要描述公告，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                        startActivity(intent);
                    } else if (msg.obj.toString().equals("业务推送")) {
                        Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.AdBusinessNaticeActivity");
                        intent.putExtra("url1", "https://www.baidu.com/");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("content", "公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                        startActivity(intent);
                    }
                }

//                if (msg.what == 1) {
//                    System.out.println("MobPush Callback Data1:" + msg.obj.toString());
//                    //
//                    presenter_mobid2.get_mob_id2(1, 1, "1", "APP_MOB");
//                }
//                if (msg.what == 2) {
//                    System.out.println("MobPush Callback Data2:" + msg.obj.toString());
//                    Intent intent2 = ActivityUtils.getTopActivity().getIntent();
//                    JSONArray jsonArray =  MobPushUtils.parseMainPluginPushIntent(intent2);
//                    System.out.println("-------MobPush------JsonPushData打印查看："+jsonArray);
//                    try {
//                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
////                        GsonUtils.fromJson();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }


                // activity获取信息
//                Intent intent2 = ActivityUtils.getTopActivity().getIntent();
//                Uri uri = intent2.getData();
//                if (intent2 != null) {
//                    System.out.println("MobPush linkone intent---" + intent2.toUri(Intent.URI_INTENT_SCHEME));
//                }
//                StringBuilder sb = new StringBuilder();
//                if (uri != null) {
//                    sb.append(" scheme:" + uri.getScheme() + "\n");
//                    sb.append(" host:" + uri.getHost() + "\n");
//                    sb.append(" port:" + uri.getPort() + "\n");
//                    sb.append(" query:" + uri.getQuery() + "\n");
//                }
//
//                //获取link界面传输的数据
//                JSONArray jsonArray = MobPushUtils.parseSchemePluginPushIntent(intent2);
//                if (jsonArray != null) {
//                    sb.append("\n" + "bundle toString :" + jsonArray.toString());
//                }
                //通过scheme跳转详情页面可选择此方式
//                    JSONArray var = new JSONArray();
//                    var = MobPushUtils.parseSchemePluginPushIntent(intent2);
//                    MyLogUtil.e("MobPushsssssss", var.toString());
//                //跳转首页可选择此方式
//                JSONArray var2 = new JSONArray();
//                var2 = MobPushUtils.parseMainPluginPushIntent(intent2);
                return false;
            }
        });
//        presenter3.getSbbdPresenter();
        presenter_mobid.get_mob_id();
    }

    private static BasePopupView loadingPopup1 = null;//公告弹框


    private String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }


    public static final int HUYAN_MANAGE_NOTIFICATION_ID = 1231611;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Intent it = new Intent(this, MOBIDServicesBg.class);
//        it.putExtra(MOBIDServicesBg.EXTRA_NOTIFICATION_ID, HUYAN_MANAGE_NOTIFICATION_ID);
//        startService(it);
//        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
//            String action = intent.getAction();
//            if (action.equals(HUIBEN_READINGTIME_ACTION)) {
//                String id2 = intent.getStringExtra(id_zong);
//                String id1 = intent.getStringExtra(id);
//                String type1 = intent.getStringExtra(type);
//                String sourceType1 = intent.getStringExtra(sourceType);
//                String time1 = intent.getStringExtra(time);
//                set_timerTo(id1, id2, type1, sourceType1, time1);
//            } else if (action.equals(LATEST_MEDAL_ACTION)) {
//                if (BaseAppManager.getInstance().top() != null) {
//                    if (TextUtils.equals("com.example.slbappindex.IndexMainActivity", BaseAppManager.getInstance().top().getClass().getName())) {
////                        getLatestMedalPresenter.getMyMedalDetailCommData();
//                        sIndexAdvertisingPresenter.getIndexAdvertisingData();
//                    }
//                }
//            }
//        }
        set_services2();
        return START_STICKY;
    }

    public static final String CHANNEL_ID = "DTXZ_ForegroundServiceChannel";

    private void set_services2() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "DTXZ Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
        Intent notificationIntent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye");
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 123, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 123, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(AppUtils.getAppName())
                .setSmallIcon(R.drawable.icon11)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (presenter_mobid != null) {
            presenter_mobid.onDestory();
        }
        if (shengjiPresenter != null) {
            shengjiPresenter.onDestory();
        }
        super.onDestroy();

    }

}
