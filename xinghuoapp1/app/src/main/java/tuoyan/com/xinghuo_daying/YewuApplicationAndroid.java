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

    // 客户端版本号，品牌，型号，客户端唯一标识、操作系统名称。版本
    private String versionname = "", brand = "", model = "", serial_no = "", os_ver = "";
    //屏幕宽度、高度
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
        //TODO 业务bufen
        //初始化mob
        configMob();
        /*消息通知*/
//        mobPush();
        // ndk
        configNDK();
        // pgyer
        initpgyer();
        // 组件快捷方式bufen
        MmkvUtils.getInstance().set_xiancheng("App.isLogined", false);
        others();
        initFileDownLoader();
        // 星火英语初始化bufen
        xinghuoyingyuinitdata();

    }

    private void xinghuoyingyuinitdata() {
        if (SpUtil.INSTANCE.getAgreement()) {

        }
//        SpUtil.INSTANCE.setAgreement(true);
        LiveSDKHelper.initSDK(this);
//        CrashReport.initCrashReport(this, "51dd7ab1b2", false);
        Realm.init(this);
        L.INSTANCE.init("日志").logLevel(L.LogLevel.NONE).methodCount(2).methodOffset(0).bulid();
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
     * 初始化common库
     * 参数1:上下文，不能为空
     * 参数2:【友盟+】 AppKey
     * 参数3:【友盟+】 Channel
     * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
     * 参数5:Push推送业务的secret
     */
    private void umengConfig() {
        UMConfigure.setLogEnabled(true);// 友盟日志
        PushHelper.Companion.preInit(this);
        if (!SpUtil.INSTANCE.getAgreement()) {
            return;
        }
        boolean isMainProcess = UMUtils.isMainProgress(this);
        if (isMainProcess) {
            //启动优化：建议在子线程中执行初始化
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushHelper.Companion.init(getApplicationContext());
                }
            }).start();
        } else {
            //若不是主进程（":channel"结尾的进程），直接初始化sdk，不可在子线程中执行
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
                JVerificationInterface.preLogin(getApplicationContext(), 3000, new PreLoginListener() {
                    @Override
                    public void onResult(int i, String s) {
                        Log.d("JVerificationInterface", "[$code]message=$content");
                    }
                });
            }
        });
        JVerificationInterface.setDebugMode(false);
    }

    //神策
    private void initSaData() {
        SAConfigOptions saConfigOptions = new SAConfigOptions(SA_SERVER_URL);
        saConfigOptions.setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_CLICK | SensorsAnalyticsAutoTrackEventType.APP_START | SensorsAnalyticsAutoTrackEventType.APP_END | SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN).enableLog(false).enableVisualizedAutoTrack(true).enableJavaScriptBridge(true).enableHeatMap(true);
//            .enableTrackAppCrash()
        SensorsDataAPI.startWithConfigOptions(this, saConfigOptions);
        try {
            JSONObject saProperties = new JSONObject();
            saProperties.put("platform_type", "Android");
            saProperties.put("product_name", "星火英语");
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
                        ToastUtils.showLong("复制成功");
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
                        String title = "星火英语";
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
                            intent0.putExtra("title", "备考干货");
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
                //接收自定义消息(透传)
                System.out.println("MobPush onCustomMessageReceive:" + message.toString());
                Log.e("aaaaaa", "MobPush onCustomMessageReceive: " + message.toString());
            }

            @Override
            public void onNotifyMessageReceive(Context context, MobPushNotifyMessage message) {
                //接收通知消
                System.out.println("MobPush onNotifyMessageReceive:" + message.toString());
                Log.e("aaaaaa", "MobPush onNotifyMessageReceive: " + message.toString());
            }

            @Override
            public void onNotifyMessageOpenedReceive(Context context, MobPushNotifyMessage message) {
                //接收通知消息被点击事件
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
                //接收tags的增改删查操作
                System.out.println("MobPush onTagsCallback:" + operation + "  " + errorCode);
                Log.e("aaaaaa", "MobPush onTagsCallback: " + "  " + operation + "  " + errorCode);
            }

            @Override
            public void onAliasCallback(Context context, String alias, int operation, int errorCode) {
                //接收alias的增改删查操作
                System.out.println("MobPush onAliasCallback:" + alias + "  " + operation + "  " + errorCode);
                Log.e("aaaaaa", "MobPush onAliasCallback: " + alias + "  " + operation + "  " + errorCode);
            }
        });
        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.obj.toString() != null && !"".equals(msg.obj.toString())) {
                    if (msg.obj.toString().equals("公告推送")) {
                        Log.e("aaaaaa", "公告推送: " + msg.obj.toString());
                        Intent intent = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.slbapp.AdNaticeActivity");
                        intent.putExtra("url1", "https://www.baidu.com/");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("content", "公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                        startActivity(intent);
                    } else if (msg.obj.toString().equals("推送通知")) {
                        Log.e("aaaaaa", "推送通知: " + msg.obj.toString());
                        Intent intent = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.slbapp.AdPushNaticeActivity");
                        intent.putExtra("url1", "https://www.baidu.com/");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("content", "公告摘要描述公告摘要描述公告，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                        startActivity(intent);
                    } else if (msg.obj.toString().equals("业务推送")) {
                        Log.e("aaaaaa", "业务推送: " + msg.obj.toString());
                        Intent intent = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.slbapp.AdBusinessNaticeActivity");
                        intent.putExtra("url1", "https://www.baidu.com/");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("content", "公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 初始化文件下载
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
        new PgyerSDKManager.Init().setContext(this) //设置上下问对象
                .setApiKey("8bd21fed5150eab87567cbcfb6ed8a46").setFrontJSToken("d68155b81756a82d2324fe46bff24c45").enable(Features.CHECK_UPDATE).start();
        // 上报异常bufen
//        try {
//
//        } catch (Exception e) {
//            PgyerSDKManager.reportException(e);
//        }
        // 手动更新
//        PgyerSDKManager.checkVersionUpdate((Activity) getApplicationContext(), new CheckoutCallBack() {
//            @Override
//            public void onNewVersionExist(CheckSoftModel model) {
//                //检查版本成功（有新版本）
//
//                /**
//                 *   CheckSoftModel 参数介绍
//                 *
//                 *    private int buildBuildVersion;//蒲公英生成的用于区分历史版本的build号
//                 *     private String forceUpdateVersion;//强制更新版本号（未设置强置更新默认为空）
//                 *     private String forceUpdateVersionNo;//强制更新的版本编号
//                 *     private boolean needForceUpdate;//	是否强制更新
//                 *     private boolean buildHaveNewVersion;//是否有新版本
//                 *     private String downloadURL;//应用安装地址
//                 *     private String buildVersionNo;//上传包的版本编号，默认为1 (即编译的版本号，一般来说，编译一次会
//                 *    变动一次这个版本号, 在 Android 上叫 Version Code。对于 iOS 来说，是字符串类型；对于 Android 来
//                 *    说是一个整数。例如：1001，28等。)
//                 *     private String buildVersion;//版本号, 默认为1.0 (是应用向用户宣传时候用到的标识，例如：1.1、8.2.1等。)
//                 *     private String buildShortcutUrl;//	应用短链接
//                 *     private String buildUpdateDescription;//	应用更新说明
//                 */
//
//            }
//
//            @Override
//            public void onNonentityVersionExist(String string) {
//                //无新版本
//            }
//
//            @Override
//            public void onFail(String error) {
//                //请求异常
//            }
//        });
        // 用户自定义数据(上传数据必须是JSON字符串格式 如：{"userId":"ceshi_001","userName":"ceshi"})
//        PgyerSDKManager.setUserData("{\"userId\":\"ceshi_001\",\"userName\":\"ceshi\"}");
    }

    private void configNDK() {
//        JNIUtils jniUtils = new JNIUtils();
//        MyLogUtil.e("--JNIUtils--", jniUtils.stringFromJNI());
    }

    private Handler handler;

    private void configMob() {
        MobSDK.init(this);
        //隐私协议
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
        //防止多进程注册多次  可以在MainActivity或者其他页面注册MobPushReceiver
//        MobPush.getDeviceToken(new MobPushCallback<String>() {
//            @Override
//            public void onCallback(String s) {
//                MyLogUtil.e("MobPush----getDeviceToken1--", s);
//                SPUtils.getInstance().put("MOBDeviceToken", s);
////                // 腾讯im
////                ThirdPushTokenMgr.getInstance().setThirdPushToken(s);
////                ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
////                // 容联im
////                initrlytx();
////                ECDevice.reportHuaWeiToken(s);
//            }
//        });
//        MobSDK.init(this);
//        //防止多进程注册多次  可以在MainActivity或者其他页面注册MobPushReceiver
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
//                    //接收自定义消息(透传)
//                    System.out.println("MobPush onCustomMessageReceive:" + message.toString());
//                }
//
//                @Override
//                public void onNotifyMessageReceive(Context context, MobPushNotifyMessage message) {
//                    //接收通知消
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
//                    //接收通知消息被点击事件
//                    System.out.println("MobPush onNotifyMessageOpenedReceive:" + message.toString());
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = "Click Message:" + message.toString();
//                    handler.sendMessage(msg);
//                }
//
//                @Override
//                public void onTagsCallback(Context context, String[] tags, int operation, int errorCode) {
//                    //接收tags的增改删查操作
//                    System.out.println("MobPush onTagsCallback:" + operation + "  " + errorCode);
//                }
//
//                @Override
//                public void onAliasCallback(Context context, String alias, int operation, int errorCode) {
//                    //接收alias的增改删查操作
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
        // activity获取信息
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
//        //获取link界面传输的数据
//        JSONArray jsonArray = MobPushUtils.parseSchemePluginPushIntent(intent);
//        if (jsonArray != null){
//            sb.append("\n" + "bundle toString :" + jsonArray.toString());
//        }
//        //通过scheme跳转详情页面可选择此方式
//        JSONArray var = new JSONArray();
//        var =  MobPushUtils.parseSchemePluginPushIntent(intent2);
//        //跳转首页可选择此方式
//        JSONArray var2 = new JSONArray();
//        var2 = MobPushUtils.parseMainPluginPushIntent(intent2);
    }

    public void mob_privacy() {
        // 指定固定Locale
// Locale locale = Locale.CHINA;
// 指定当前系统locale
        Locale locale = null;
        if (Build.VERSION.SDK_INT >= 24) {
            LocaleList localeList = getApplicationContext().getResources().getConfiguration().getLocales();
            if (localeList != null && !localeList.isEmpty()) {
                locale = localeList.get(0);
            }
        } else {
            locale = getApplicationContext().getResources().getConfiguration().locale;
        }

// 同步方法查询隐私,locale可以为null或不设置，默认使用当前系统语言
//        PrivacyPolicy policyUrl = MobSDK.getPrivacyPolicy(MobSDK.POLICY_TYPE_URL, locale);
//        String url = policyUrl.getContent();

// 异步方法查询隐私,locale可以为null或不设置，默认使用当前系统语言
//        MobSDK.getPrivacyPolicyAsync(MobSDK.POLICY_TYPE_URL, new PrivacyPolicy.OnPolicyListener() {
//            @Override
//            public void onComplete(PrivacyPolicy data) {
//                if (data != null) {
//                    // 富文本内容
//                    String text = data.getContent();
//                    MyLogUtil.e("MobPush", "隐私协议地址->" + text);
//                    MobSDK.submitPolicyGrantResult(!TextUtils.isEmpty(text), new OperationCallback<Void>() {
//                        @Override
//                        public void onComplete(Void data) {
//                            MyLogUtil.e("MobPush", "隐私协议授权结果提交：成功");
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) {
//                            MyLogUtil.e("MobPush", "隐私协议授权结果提交：失败");
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                // 请求失败
//            }
//        });
        MobSDK.submitPolicyGrantResult(true, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void data) {
                MyLogUtil.e("MobPush", "隐私协议授权结果提交：成功");
            }

            @Override
            public void onFailure(Throwable t) {
                MyLogUtil.e("MobPush", "隐私协议授权结果提交：失败");
            }
        });
    }

    @Override
    public void onHomePressed() {
        super.onHomePressed();
//        AddressSaver.addr = null;
    }
}
