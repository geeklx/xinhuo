package tuoyan.com.xinghuo_daying;

import static tuoyan.com.xinghuo_dayingindex.ConfigKt.SA_FOCUS_URL;
import static tuoyan.com.xinghuo_dayingindex.ConfigKt.SA_SERVER_URL;
import static tuoyan.com.xinghuo_dayingindex.ConfigKt.UDESK_DOMAIN;
import static tuoyan.com.xinghuo_dayingindex.ConfigKt.UDESK_ID;
import static tuoyan.com.xinghuo_dayingindex.ConfigKt.UDESK_KEY;
import static tuoyan.com.xinghuo_dayingindex.ConfigKt.appId;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bokecc.livemodule.LiveSDKHelper;
import com.geek.appcommon.service.MOBIDservices;
import com.geek.libbase.AndroidApplication;
import com.geek.libutils.app.AppUtils;
import com.geek.libutils.app.BaseApp;
import com.geek.libutils.app.BaseAppManager;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.mob.MobSDK;
import com.mob.OperationCallback;
import com.mob.pushsdk.MobPush;
import com.mob.pushsdk.MobPushCustomMessage;
import com.mob.pushsdk.MobPushNotifyMessage;
import com.mob.pushsdk.MobPushReceiver;
import com.pgyer.pgyersdk.PgyerSDKManager;
import com.pgyer.pgyersdk.pgyerenum.Features;
import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.sf.core.SFConfigOptions;
import com.sensorsdata.sf.core.SensorsFocusAPI;
import com.sensorsdata.sf.ui.listener.PopupListener;
import com.sensorsdata.sf.ui.view.SensorsFocusActionModel;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.Proxy;
import java.util.Locale;

import cn.jiguang.verifysdk.api.JVerificationInterface;
import cn.jiguang.verifysdk.api.PreLoginListener;
import cn.jiguang.verifysdk.api.RequestCallback;
import cn.jpush.android.api.JPushInterface;
import cn.udesk.UdeskSDKManager;
import io.realm.Realm;
import tuoyan.com.xinghuo_dayingindex.umengpush.PushHelper;
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil;
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini;
import tuoyan.com.xinghuo_dayingindex.utlis.log.L;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

public class YewuApplicationAndroid extends AndroidApplication {

    private static final String TAG = YewuApplicationAndroid.class.getSimpleName();
    private static YewuApplicationAndroid instance;

    public static YewuApplicationAndroid instance() {
        return instance;
    }

    // ??????????????????????????????????????????????????????????????????????????????????????????
    private String versionname = "", brand = "", model = "", serial_no = "", os_ver = "";
    //?????????????????????
    private int screen_width = 0, screen_height, version = 0;
    public static String sysSpecial = "00";
    public static String blueTooth = "00";
    public static String apkFeature = "00";
    public static String goldFish = "00";
    public static String debugger = "00";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!AppUtils.isProcessAs(getPackageName(), this)) {
            return;
        }
        //TODO commonbufen
        configBugly(BuildConfigyewu.versionNameConfig, "b6ee75e0d4", "b6ee75e0d4", "b6ee75e0d4");
        configHios();
//        HiosHelperNew.config(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".web.page3.js2");
        configmmkv();
        configShipei();
        configRetrofitNet();
        RetrofitNetNew2.config();
        RetrofitNetNew3.config();
        //TODO ??????bufen
        //?????????mob
        configMob();
        /*????????????*/
//        mobPush();
        // ndk
        configNDK();
        // pgyer
        initpgyer();
        // ??????????????????bufen
        MmkvUtils.getInstance().set_xiancheng("App.isLogined", false);
        others();
        initFileDownLoader();
        // ?????????????????????bufen
        xinghuoyingyuinitdata();

    }

    private void xinghuoyingyuinitdata() {
        if (SpUtil.INSTANCE.getAgreement()) {

        }
//        SpUtil.INSTANCE.setAgreement(true);
        LiveSDKHelper.initSDK(this);
//        CrashReport.initCrashReport(this, "51dd7ab1b2", false);
        Realm.init(this);
        L.INSTANCE.init("??????").logLevel(L.LogLevel.NONE).methodCount(2).methodOffset(0).bulid();
        FileDownloader.setup(this);
        FileDownloader.setGlobalPost2UIInterval(150);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                BaseAppManager.getInstance().add(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                BaseAppManager.getInstance().remove(activity);
            }
        });
        umengConfig();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        initJPush();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        UdeskSDKManager.getInstance().initApiKey(this, UDESK_DOMAIN, UDESK_KEY, UDESK_ID);
        initSaData();
    }

    /**
     * ?????????common???
     * ??????1:????????????????????????
     * ??????2:?????????+??? AppKey
     * ??????3:?????????+??? Channel
     * ??????4:???????????????UMConfigure.DEVICE_TYPE_PHONE????????????UMConfigure.DEVICE_TYPE_BOX???????????????????????????
     * ??????5:Push???????????????secret
     */
    private void umengConfig() {
        UMConfigure.setLogEnabled(true);// ????????????
        PushHelper.Companion.preInit(this);
        if (!SpUtil.INSTANCE.getAgreement()) {
            return;
        }
        boolean isMainProcess = UMUtils.isMainProgress(this);
        if (isMainProcess) {
            //???????????????????????????????????????????????????
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushHelper.Companion.init(getApplicationContext());
                }
            }).start();
        } else {
            //?????????????????????":channel"????????????????????????????????????sdk??????????????????????????????
            PushHelper.Companion.init(this);
        }
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(this).setShareConfig(config);
        PlatformConfig.setWeixin("wx35bdc73a79e2a430", "79e8f9668072517e643a860c458ac169");
        PlatformConfig.setWXFileProvider("tuoyan.com.xinghuo_daying.fileprovider");
        PlatformConfig.setQQZone("1105474912", "c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setQQFileProvider("tuoyan.com.xinghuo_daying.fileprovider");
        PlatformConfig.setSinaWeibo("3240568197", "7d0161ac53ca6082d22d85726c5f881e", "http://m.sparke.cn/download.html");
        PlatformConfig.setSinaFileProvider("tuoyan.com.xinghuo_daying.fileprovider");
    }

    private void initJPush() {
        JVerificationInterface.init(this, 3000, new RequestCallback<String>() {
            @Override
            public void onResult(int i, String s) {

            }
        });
        JVerificationInterface.preLogin(getApplicationContext(), 3000, new PreLoginListener() {
            @Override
            public void onResult(int i, String s) {
                Log.d("JVerificationInterface", "[$code]message=$content");
            }
        });
        JVerificationInterface.setDebugMode(false);
        String aaa = JPushInterface.getRegistrationID(this);
        SPUtils.getInstance().put("JPushInterfacegetRegistrationID",aaa);
    }

    //??????
    private void initSaData() {
        SAConfigOptions saConfigOptions = new SAConfigOptions(SA_SERVER_URL);
        saConfigOptions.setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_CLICK | SensorsAnalyticsAutoTrackEventType.APP_START | SensorsAnalyticsAutoTrackEventType.APP_END | SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN).enableLog(false).enableVisualizedAutoTrack(true).enableJavaScriptBridge(true).enableHeatMap(true);
//            .enableTrackAppCrash()
        SensorsDataAPI.startWithConfigOptions(this, saConfigOptions);
        try {
            JSONObject saProperties = new JSONObject();
            saProperties.put("platform_type", "Android");
            saProperties.put("product_name", "????????????");
            saProperties.put("app_key", appId);
            SensorsDataAPI.sharedInstance().registerSuperProperties(saProperties);
        } catch (JSONException e) {
        }
        String userId = SpUtil.INSTANCE.getUser().getUserId();
        if (userId != null && !userId.isEmpty()) {
            SensorsDataAPI.sharedInstance().login(SpUtil.INSTANCE.getUser().getUserId());
            try {
                JSONObject properties = new JSONObject();
                properties.put("phone_number", SpUtil.INSTANCE.getUserInfo().getPhone());
                properties.put("user_nickname", SpUtil.INSTANCE.getUserInfo().getName());
                SensorsDataAPI.sharedInstance().profileSet(properties);
            } catch (JSONException e) {
            }
        }
        SensorsFocusAPI.startWithConfigOptions(this, new SFConfigOptions(SA_FOCUS_URL).setPopupListener(new PopupListener() {
            @Override
            public void onPopupLoadSuccess(String planId) {

            }

            @Override
            public void onPopupLoadFailed(String planId, int errorCode, String errorMessage) {

            }

            @Override
            public void onPopupClick(String planId, SensorsFocusActionModel actionModel) {
                switch (actionModel) {
                    case OPEN_LINK:
                        String url = actionModel.getValue();
                        Intent intent = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.PostActivity");
                        intent.putExtra("url", url);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    case COPY:
                        ClipboardManager mClipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, actionModel.getValue()));
                        ToastUtils.showLong("????????????");
                        break;
                    case CLOSE:

                        break;
                    case CUSTOMIZE:
                        JSONObject customizeJson = actionModel.getExtra();
                        String type = "";
                        if (customizeJson.has("type")) {
                            try {
                                type = customizeJson.getString("type");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        String key = "";
                        if (customizeJson.has("bizInfoKey")) {
                            try {
                                key = customizeJson.getString("bizInfoKey");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        String title = "????????????";
                        if (customizeJson.has("title")) {
                            try {
                                title = customizeJson.getString("title");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (TextUtils.equals("1", type)) {
                            Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.LessonDetailActivity");
                            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent0.putExtra("key", key);
                            startActivity(intent0);
                        } else if (TextUtils.equals("2", type)) {
                            Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.MessageDetailActivity");
                            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent0.putExtra("key", key);
                            startActivity(intent0);
                        } else if (TextUtils.equals("3", type)) {
                            Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.PostActivity");
                            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent0.putExtra("key", "bookdetail?id=$key");
                            startActivity(intent0);
                        } else if (TextUtils.equals("4", type)) {
                            Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.LessonListActivity");
                            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent0.putExtra("key", key);
                            intent0.putExtra("title", title);
                            startActivity(intent0);
                        } else if (TextUtils.equals("5", type)) {
                            Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.MainActivityXH");
                            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent0.putExtra("position", "3");
                            startActivity(intent0);
                            EventBus.getDefault().post("book");
                        } else if (TextUtils.equals("7", type)) {
                            Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.RecommedNewsActivity");
                            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent0.putExtra("title", "????????????");
                            intent0.putExtra("grade_key", key);
                            startActivity(intent0);
                        } else if (TextUtils.equals("8", type)) {
                            WxMini.Companion.goWxMini(getApplicationContext(), key);
                        } else if (TextUtils.equals("9", type)) {
                            Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.PostActivity");
                            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent0.putExtra("key", key);
                            startActivity(intent0);
                        } else if (TextUtils.equals("10", type)) {
                            Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.ReceiverToViewActivity");
                            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent0.putExtra("id", key);
                            startActivity(intent0);
                        } else if (TextUtils.equals("11", type)) {
                            Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.MainCouponActivity");
                            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent0.putExtra("key", key);
                            startActivity(intent0);
                        } else if (TextUtils.equals("12", type)) {
                            Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.CouponsActivity");
                            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent0);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPopupClose(String planId) {

            }
        }));
    }

    private void initForgetOrBack() {
        com.blankj.utilcode.util.AppUtils.registerAppStatusChangedListener(new Utils.OnAppStatusChangedListener() {
            @Override
            public void onForeground(Activity activity) {
                if (!ServiceUtils.isServiceRunning(MOBIDservices.class)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(BaseApp.get(), MOBIDservices.class));
                    } else {
                        startService(new Intent(BaseApp.get(), MOBIDservices.class));
                    }
                }
            }

            @Override
            public void onBackground(Activity activity) {

            }
        });
    }

    private void mobPush() {

        MobPush.setClickNotificationToLaunchMainActivity(false);
        MobPush.addPushReceiver(new MobPushReceiver() {
            @Override
            public void onCustomMessageReceive(Context context, MobPushCustomMessage message) {
                //?????????????????????(??????)
                System.out.println("MobPush onCustomMessageReceive:" + message.toString());
                Log.e("aaaaaa", "MobPush onCustomMessageReceive: " + message.toString());
            }

            @Override
            public void onNotifyMessageReceive(Context context, MobPushNotifyMessage message) {
                //???????????????
                System.out.println("MobPush onNotifyMessageReceive:" + message.toString());
                Log.e("aaaaaa", "MobPush onNotifyMessageReceive: " + message.toString());
            }

            @Override
            public void onNotifyMessageOpenedReceive(Context context, MobPushNotifyMessage message) {
                //?????????????????????????????????
                Log.e("aaaaaa", message.getContent() + "onNotifyMessageOpenedReceive: " + message.toString());
//                System.out.println("MobPush onNotifyMessageOpenedReceive:" + message.toString());
//                System.out.println("MobPush onNotifyMessageOpenedReceive:" + message.getContent());
                Message msg = new Message();
                msg.what = 2;
                msg.obj = message.getTitle();
                handler.sendMessage(msg);
            }

            @Override
            public void onTagsCallback(Context context, String[] tags, int operation, int errorCode) {
                //??????tags?????????????????????
                System.out.println("MobPush onTagsCallback:" + operation + "  " + errorCode);
                Log.e("aaaaaa", "MobPush onTagsCallback: " + "  " + operation + "  " + errorCode);
            }

            @Override
            public void onAliasCallback(Context context, String alias, int operation, int errorCode) {
                //??????alias?????????????????????
                System.out.println("MobPush onAliasCallback:" + alias + "  " + operation + "  " + errorCode);
                Log.e("aaaaaa", "MobPush onAliasCallback: " + alias + "  " + operation + "  " + errorCode);
            }
        });
        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.obj.toString() != null && !"".equals(msg.obj.toString())) {
                    if (msg.obj.toString().equals("????????????")) {
                        Log.e("aaaaaa", "????????????: " + msg.obj.toString());
                        Intent intent = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.slbapp.AdNaticeActivity");
                        intent.putExtra("url1", "https://www.baidu.com/");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("content", "???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                        startActivity(intent);
                    } else if (msg.obj.toString().equals("????????????")) {
                        Log.e("aaaaaa", "????????????: " + msg.obj.toString());
                        Intent intent = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.slbapp.AdPushNaticeActivity");
                        intent.putExtra("url1", "https://www.baidu.com/");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("content", "?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                        startActivity(intent);
                    } else if (msg.obj.toString().equals("????????????")) {
                        Log.e("aaaaaa", "????????????: " + msg.obj.toString());
                        Intent intent = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.slbapp.AdBusinessNaticeActivity");
                        intent.putExtra("url1", "https://www.baidu.com/");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("content", "???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void initFileDownLoader() {
        FileDownloader.setupOnApplicationOnCreate(this).connectionCreator(new FileDownloadUrlConnection.Creator(new FileDownloadUrlConnection.Configuration().connectTimeout(60_000) // set connection timeout.
                .readTimeout(60_000) // set read timeout.
                .proxy(Proxy.NO_PROXY) // set proxy
        )).commit();
        FileDownloadUtils.setDefaultSaveRootPath(getExternalFilesDir(null).getAbsolutePath() + File.separator + "lighthouse/download");
//      FileDownloadUtils.setDefaultSaveRootPath(get_file_url(this) + File.separator + "ota/download");
    }

    private void initpgyer() {
        new PgyerSDKManager.Init().setContext(this) //?????????????????????
                .setApiKey("8bd21fed5150eab87567cbcfb6ed8a46").setFrontJSToken("c23db605166d72dbe63c1dce1ee98e38").enable(Features.CHECK_UPDATE).start();
        // ????????????bufen
//        try {
//
//        } catch (Exception e) {
//            PgyerSDKManager.reportException(e);
//        }
        // ????????????
//        PgyerSDKManager.checkVersionUpdate((Activity) getApplicationContext(), new CheckoutCallBack() {
//            @Override
//            public void onNewVersionExist(CheckSoftModel model) {
//                //????????????????????????????????????
//
//                /**
//                 *   CheckSoftModel ????????????
//                 *
//                 *    private int buildBuildVersion;//?????????????????????????????????????????????build???
//                 *     private String forceUpdateVersion;//????????????????????????????????????????????????????????????
//                 *     private String forceUpdateVersionNo;//???????????????????????????
//                 *     private boolean needForceUpdate;//	??????????????????
//                 *     private boolean buildHaveNewVersion;//??????????????????
//                 *     private String downloadURL;//??????????????????
//                 *     private String buildVersionNo;//????????????????????????????????????1 (??????????????????????????????????????????????????????
//                 *    ???????????????????????????, ??? Android ?????? Version Code????????? iOS ???????????????????????????????????? Android ???
//                 *    ??????????????????????????????1001???28??????)
//                 *     private String buildVersion;//?????????, ?????????1.0 (?????????????????????????????????????????????????????????1.1???8.2.1??????)
//                 *     private String buildShortcutUrl;//	???????????????
//                 *     private String buildUpdateDescription;//	??????????????????
//                 */
//
//            }
//
//            @Override
//            public void onNonentityVersionExist(String string) {
//                //????????????
//            }
//
//            @Override
//            public void onFail(String error) {
//                //????????????
//            }
//        });
        // ?????????????????????(?????????????????????JSON??????????????? ??????{"userId":"ceshi_001","userName":"ceshi"})
//        PgyerSDKManager.setUserData("{\"userId\":\"ceshi_001\",\"userName\":\"ceshi\"}");
    }

    private void configNDK() {
//        JNIUtils jniUtils = new JNIUtils();
//        MyLogUtil.e("--JNIUtils--", jniUtils.stringFromJNI());
    }

    private Handler handler;

    private void configMob() {
        MobSDK.init(this);
        //????????????
        MobSDK.submitPolicyGrantResult(true);
        //
//        MobPush.getRegistrationId(new MobPushCallback<String>() {
//
//            @Override
//            public void onCallback(String rid) {
//                MyLogUtil.e("RegistrationId", "RegistrationId:" + rid);
//                SPUtils.getInstance().put("MOBID", rid);
//
//            }
//        });
//        MobPush.getDeviceToken(new MobPushCallback<String>() {
//            @Override
//            public void onCallback(String s) {
//                MyLogUtil.e("MobPush----getDeviceToken2--", s);
//                SPUtils.getInstance().put("MOBDeviceToken", s);
//                if (!TextUtils.isEmpty(s)) {
//                    ThirdPushTokenMgr.getInstance().setThirdPushToken(s);
//                    ECDevice.reportHuaWeiToken(s);
//                }
//            }
//        });
//        mob_privacy();
//        MobPush.setDeviceTokenByUser(DeviceUtils.getAndroidID());
        //???????????????????????????  ?????????MainActivity????????????????????????MobPushReceiver
//        MobPush.getDeviceToken(new MobPushCallback<String>() {
//            @Override
//            public void onCallback(String s) {
//                MyLogUtil.e("MobPush----getDeviceToken1--", s);
//                SPUtils.getInstance().put("MOBDeviceToken", s);
////                // ??????im
////                ThirdPushTokenMgr.getInstance().setThirdPushToken(s);
////                ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
////                // ??????im
////                initrlytx();
////                ECDevice.reportHuaWeiToken(s);
//            }
//        });
//        MobSDK.init(this);
//        //???????????????????????????  ?????????MainActivity????????????????????????MobPushReceiver
//        String processName = getProcessName(this);
//        MobPush.getRegistrationId(new MobPushCallback<String>() {
//
//            @Override
//            public void onCallback(String rid) {
//                System.out.println("MobPush->RegistrationId:" + rid);
//                SPUtils.getInstance().put("MOBID", rid);
//            }
//        });
//        if (getPackageName().equals(processName)) {
//            MobPush.addPushReceiver(new MobPushReceiver() {
//                @Override
//                public void onCustomMessageReceive(Context context, MobPushCustomMessage message) {
//                    //?????????????????????(??????)
//                    System.out.println("MobPush onCustomMessageReceive:" + message.toString());
//                }
//
//                @Override
//                public void onNotifyMessageReceive(Context context, MobPushNotifyMessage message) {
//                    //???????????????
//                    System.out.println("MobPush onNotifyMessageReceive:" + message.toString());
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = "Message Receive:" + message.toString();
//                    handler.sendMessage(msg);
//
//                }
//
//                @Override
//                public void onNotifyMessageOpenedReceive(Context context, MobPushNotifyMessage message) {
//                    //?????????????????????????????????
//                    System.out.println("MobPush onNotifyMessageOpenedReceive:" + message.toString());
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = "Click Message:" + message.toString();
//                    handler.sendMessage(msg);
//                }
//
//                @Override
//                public void onTagsCallback(Context context, String[] tags, int operation, int errorCode) {
//                    //??????tags?????????????????????
//                    System.out.println("MobPush onTagsCallback:" + operation + "  " + errorCode);
//                }
//
//                @Override
//                public void onAliasCallback(Context context, String alias, int operation, int errorCode) {
//                    //??????alias?????????????????????
//                    System.out.println("MobPush onAliasCallback:" + alias + "  " + operation + "  " + errorCode);
//                }
//            });
//
//            handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
//                @Override
//                public boolean handleMessage(@NonNull Message msg) {
//                    if (msg.what == 1) {
//                        System.out.println("MobPush Callback Data:" + msg.obj);
//                    }
//                    return false;
//                }
//            });
//        }
        // activity????????????
//        Intent intent = getIntent();
//        Uri uri = intent.getData();
//        if (intent != null) {
//            System.out.println("MobPush linkone intent---" + intent.toUri(Intent.URI_INTENT_SCHEME));
//        }
//        StringBuilder sb = new StringBuilder();
//        if (uri != null) {
//            sb.append(" scheme:" + uri.getScheme() + "\n");
//            sb.append(" host:" + uri.getHost() + "\n");
//            sb.append(" port:" + uri.getPort() + "\n");
//            sb.append(" query:" + uri.getQuery() + "\n");
//        }
//
//        //??????link?????????????????????
//        JSONArray jsonArray = MobPushUtils.parseSchemePluginPushIntent(intent);
//        if (jsonArray != null){
//            sb.append("\n" + "bundle toString :" + jsonArray.toString());
//        }
//        //??????scheme????????????????????????????????????
//        JSONArray var = new JSONArray();
//        var =  MobPushUtils.parseSchemePluginPushIntent(intent2);
//        //??????????????????????????????
//        JSONArray var2 = new JSONArray();
//        var2 = MobPushUtils.parseMainPluginPushIntent(intent2);
    }

    public void mob_privacy() {
        // ????????????Locale
// Locale locale = Locale.CHINA;
// ??????????????????locale
        Locale locale = null;
        if (Build.VERSION.SDK_INT >= 24) {
            LocaleList localeList = getApplicationContext().getResources().getConfiguration().getLocales();
            if (localeList != null && !localeList.isEmpty()) {
                locale = localeList.get(0);
            }
        } else {
            locale = getApplicationContext().getResources().getConfiguration().locale;
        }

// ????????????????????????,locale?????????null?????????????????????????????????????????????
//        PrivacyPolicy policyUrl = MobSDK.getPrivacyPolicy(MobSDK.POLICY_TYPE_URL, locale);
//        String url = policyUrl.getContent();

// ????????????????????????,locale?????????null?????????????????????????????????????????????
//        MobSDK.getPrivacyPolicyAsync(MobSDK.POLICY_TYPE_URL, new PrivacyPolicy.OnPolicyListener() {
//            @Override
//            public void onComplete(PrivacyPolicy data) {
//                if (data != null) {
//                    // ???????????????
//                    String text = data.getContent();
//                    MyLogUtil.e("MobPush", "??????????????????->" + text);
//                    MobSDK.submitPolicyGrantResult(!TextUtils.isEmpty(text), new OperationCallback<Void>() {
//                        @Override
//                        public void onComplete(Void data) {
//                            MyLogUtil.e("MobPush", "???????????????????????????????????????");
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) {
//                            MyLogUtil.e("MobPush", "???????????????????????????????????????");
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                // ????????????
//            }
//        });
        MobSDK.submitPolicyGrantResult(true, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void data) {
                MyLogUtil.e("MobPush", "???????????????????????????????????????");
            }

            @Override
            public void onFailure(Throwable t) {
                MyLogUtil.e("MobPush", "???????????????????????????????????????");
            }
        });
    }

    @Override
    public void onHomePressed() {
        super.onHomePressed();
//        AddressSaver.addr = null;
    }
}
