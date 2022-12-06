//// MyApp.java
//package tuoyan.com.xinghuo_dayingindex;
//
//import android.app.Activity;
//import android.app.Application;
//import android.content.Context;
//import android.os.Build.VERSION;
//import android.os.Bundle;
//import android.os.StrictMode;
//import android.os.StrictMode.VmPolicy.Builder;
//
//import androidx.appcompat.app.AppCompatDelegate;
//
//import com.bokecc.livemodule.LiveSDKHelper;
//import com.liulishuo.filedownloader.FileDownloader;
//import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
//import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
//import com.sensorsdata.sf.core.SFConfigOptions;
//import com.sensorsdata.sf.core.SensorsFocusAPI;
//import com.sensorsdata.sf.ui.listener.PopupListener;
//import com.sensorsdata.sf.ui.view.SensorsFocusActionModel;
//import com.shuyu.gsyvideoplayer.cache.CacheFactory;
//import com.shuyu.gsyvideoplayer.player.PlayerFactory;
//import com.tencent.bugly.crashreport.CrashReport;
//import com.umeng.commonsdk.UMConfigure;
//import com.umeng.commonsdk.utils.UMUtils;
//import com.umeng.socialize.PlatformConfig;
//import com.umeng.socialize.UMShareAPI;
//import com.umeng.socialize.UMShareConfig;
//
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import org.json.JSONObject;
//
//import java.util.List;
//import java.util.Stack;
//
//import cn.jiguang.verifysdk.api.JVerificationInterface;
//import cn.jiguang.verifysdk.api.RequestCallback;
//import cn.udesk.UdeskSDKManager;
//import io.realm.Realm;
//import kotlin.Lazy;
//import kotlin.LazyKt;
//import kotlin.jvm.internal.DefaultConstructorMarker;
//import kotlin.jvm.internal.MutablePropertyReference1Impl;
//import kotlin.jvm.internal.Reflection;
//import kotlin.properties.ReadWriteProperty;
//import kotlin.reflect.KProperty;
//import tuoyan.com.xinghuo_dayingindex.bean.BookRes;
//import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean;
//import tuoyan.com.xinghuo_dayingindex.bean.WordsByCatalogkey;
//import tuoyan.com.xinghuo_dayingindex.umengpush.PushHelper;
//import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil;
//import tuoyan.com.xinghuo_dayingindex.utlis.log.L;
//import tuoyan.com.xinghuo_dayingindex.utlis.log.L.LogLevel;
//import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
//import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;
//
//
//public final class MyApp extends Application {
//    private final Lazy store$delegate;
//    private List<WordsByCatalogkey> data;
//    private List<BookRes> bookres;
//    private List<ResourceListBean> resList;
//    private boolean loginComplete;
//    private String channal;
//    private String equipmentId;
//    private static final ReadWriteProperty instance$delegate = null;
//    public static final MyApp.Companion Companion = new MyApp.Companion((DefaultConstructorMarker) null);
//
//    private final Stack getStore() {
//        Lazy var1 = this.store$delegate;
//        Object var3 = null;
//        return (Stack) var1.getValue();
//    }
//
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Companion.setInstance((MyApp) this);
//        if (SpUtil.INSTANCE.getAgreement()) {
//            this.initApp();
//        }
//
//    }
//
//    public final void initApp() {
//        LiveSDKHelper.initSDK((Application) this);
//        CrashReport.initCrashReport((Context) this, "51dd7ab1b2", false);
//        Realm.init((Context) this);
//        L.INSTANCE.init("日志").logLevel(LogLevel.NONE).methodCount(2).methodOffset(0).bulid();
//        FileDownloader.setup((Context) this);
//        FileDownloader.setGlobalPost2UIInterval(150);
//        this.registerActivityLifecycleCallbacks((ActivityLifecycleCallbacks) (new MyApp.SwitchBackgroundCallbacks()));
//        this.umengConfig();
//        if (VERSION.SDK_INT >= 24) {
//            Builder builder = new Builder();
//            StrictMode.setVmPolicy(builder.build());
//        }
//
//        this.initJPush();
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
//        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
//        UdeskSDKManager.getInstance().initApiKey((Context) this, "sparke.udesk.cn", "2463f483be5af71206ebfd95709e8760", "b4a65189d090bb62");
//        this.initSaData();
//    }
//
//    private void umengConfig() {
//        UMConfigure.setLogEnabled(true);
//        PushHelper.Companion.preInit((Context) this);
//        if (SpUtil.INSTANCE.getAgreement()) {
//            boolean isMainProcess = UMUtils.isMainProgress((Context) this);
//            if (isMainProcess) {
//                (new Thread((Runnable) (new Runnable() {
//                    public final void run() {
//                        PushHelper.Companion.init((Context) MyApp.this);
//                    }
//                }))).start();
//            } else {
//                PushHelper.Companion.init((Context) this);
//            }
//
//            UMShareConfig config = new UMShareConfig();
//            config.isNeedAuthOnGetUserInfo(true);
//            UMShareAPI.get((Context) this).setShareConfig(config);
//            PlatformConfig.setWeixin("wx35bdc73a79e2a430", "79e8f9668072517e643a860c458ac169");
//            PlatformConfig.setWXFileProvider("tuoyan.com.xinghuo_daying.fileprovider");
//            PlatformConfig.setQQZone("1105474912", "c7394704798a158208a74ab60104f0ba");
//            PlatformConfig.setQQFileProvider("tuoyan.com.xinghuo_daying.fileprovider");
//            PlatformConfig.setSinaWeibo("3240568197", "7d0161ac53ca6082d22d85726c5f881e", "http://m.sparke.cn/download.html");
//            PlatformConfig.setSinaFileProvider("tuoyan.com.xinghuo_daying.fileprovider");
//        }
//    }
//
//    private void initJPush() {
//        JVerificationInterface.init((Context) this, 3000, (RequestCallback) (new RequestCallback() {
//            // $FF: synthetic method
//            // $FF: bridge method
//            @Override
//            public void onResult(int var1, Object var2) {
//                this.onResult(var1, (String) var2);
//            }
//
//            public final void onResult(int codeJ, String messageJ) {
//                JVerificationInterface.preLogin((Context) MyApp.this, 3000, null);
//            }
//        }));
//        JVerificationInterface.setDebugMode(false);
//    }
//
//    private void initSaData() {
//        SAConfigOptions saConfigOptions = new SAConfigOptions("https://datasink.sparke.cn/sa?project=production");
//        saConfigOptions.setAutoTrackEventType(15).enableLog(false).enableVisualizedAutoTrack(true).enableJavaScriptBridge(true).enableHeatMap(true);
//        SensorsDataAPI.startWithConfigOptions((Context) this, saConfigOptions);
//
//        try {
//            JSONObject saProperties = new JSONObject();
//            saProperties.put("platform_type", "Android");
//            saProperties.put("product_name", "星火英语");
//            saProperties.put("app_key", "210951669544977408");
//            SensorsDataAPI.sharedInstance().registerSuperProperties(saProperties);
//        } catch (Exception var5) {
//        }
//
//        String userId = SpUtil.INSTANCE.getUser().getUserId();
//        if (userId != null) {
//            CharSequence var3 = (CharSequence) userId;
//            if (var3.length() > 0) {
//                SensorsDataAPI.sharedInstance().login(SpUtil.INSTANCE.getUser().getUserId());
//
//                try {
//                    JSONObject properties = new JSONObject();
//                    properties.put("phone_number", SpUtil.INSTANCE.getUserInfo().getPhone());
//                    properties.put("user_nickname", SpUtil.INSTANCE.getUserInfo().getName());
//                    SensorsDataAPI.sharedInstance().profileSet(properties);
//                } catch (Exception var4) {
//                }
//            }
//        }
//
//        SensorsFocusAPI.startWithConfigOptions((Context) this, (new SFConfigOptions("https://sparke.sfn-tx-shanghai-01.saas.sensorsdata.cn/api/v2")).setPopupListener((PopupListener) (new PopupListener() {
//            @Override
//            public void onPopupLoadSuccess(@Nullable String planId) {
//            }
//
//            @Override
//            public void onPopupLoadFailed(@Nullable String planId, int errorCode, @Nullable String errorMessage) {
//            }
//
//            @Override
//            public void onPopupClick(@Nullable String planId, @Nullable SensorsFocusActionModel actionModel) {
//                // $FF: Couldn't be decompiled
//            }
//
//            @Override
//            public void onPopupClose(@Nullable String planId) {
//            }
//        })));
//    }
//
//    @NotNull
//    public final Activity getCurActivity() {
//        Object var10000 = this.getStore().lastElement();
//        return (Activity) var10000;
//    }
//
//    public MyApp() {
//        this.store$delegate = LazyKt.lazy(null);
//        this.channal = "";
//        this.equipmentId = "";
//    }
//
//
//    public final class SwitchBackgroundCallbacks implements ActivityLifecycleCallbacks {
//        @Override
//        public void onActivityPaused(@NotNull Activity activity) {
//
//        }
//
//        @Override
//        public void onActivityResumed(@NotNull Activity activity) {
//
//        }
//
//        @Override
//        public void onActivityStarted(@NotNull Activity activity) {
//
//        }
//
//        @Override
//        public void onActivityDestroyed(@NotNull Activity activity) {
//            MyApp.this.getStore().remove(activity);
//        }
//
//        @Override
//        public void onActivitySaveInstanceState(@NotNull Activity activity, @NotNull Bundle outState) {
//
//        }
//
//        @Override
//        public void onActivityStopped(@NotNull Activity activity) {
//
//        }
//
//        @Override
//        public void onActivityCreated(@NotNull Activity activity, @Nullable Bundle savedInstanceState) {
//            MyApp.this.getStore().add(activity);
//        }
//    }
//
//    public static final class Companion {
//        // $FF: synthetic field
//        static final KProperty[] $$delegatedProperties = new KProperty[]{(KProperty) Reflection.mutableProperty1(new MutablePropertyReference1Impl(MyApp.Companion.class, "instance", "getInstance()Ltuoyan/com/xinghuo_dayingindex/MyApp;", 0))};
//
//        @NotNull
//        public final MyApp getInstance() {
//            return (MyApp) MyApp.instance$delegate.getValue(MyApp.Companion, $$delegatedProperties[0]);
//        }
//
//        public final void setInstance(@NotNull MyApp var1) {
//            MyApp.instance$delegate.setValue(MyApp.Companion, $$delegatedProperties[0], var1);
//        }
//
//        private Companion() {
//        }
//
//        // $FF: synthetic method
//        public Companion(DefaultConstructorMarker $constructor_marker) {
//            this();
//        }
//    }
//}
