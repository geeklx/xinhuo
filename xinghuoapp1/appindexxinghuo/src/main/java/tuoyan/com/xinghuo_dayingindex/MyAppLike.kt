//package tuoyan.com.xinghuo_daying
//
//import android.annotation.TargetApi
//import android.app.Activity
//import android.app.Application
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.os.StrictMode
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatDelegate
//import cn.jiguang.verifysdk.api.JVerificationInterface
//import cn.udesk.UdeskSDKManager
//import com.bokecc.livemodule.LiveSDKHelper
//import com.liulishuo.filedownloader.FileDownloader
//import com.sensorsdata.analytics.android.sdk.SAConfigOptions
//import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType
//import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
//import com.sensorsdata.sf.core.SFConfigOptions
//import com.sensorsdata.sf.core.SensorsFocusAPI
//import com.sensorsdata.sf.ui.listener.PopupListener
//import com.sensorsdata.sf.ui.view.SensorsFocusActionModel
//import com.talkfun.sdk.offline.PlaybackDownloader
//import com.tencent.bugly.Bugly
//import com.tencent.bugly.beta.Beta
//import com.tencent.bugly.crashreport.CrashReport
//import com.tencent.tinker.anno.DefaultLifeCycle
//import com.tencent.tinker.entry.DefaultApplicationLike
//import com.tencent.tinker.loader.shareutil.ShareConstants
//import com.umeng.commonsdk.UMConfigure
//import com.umeng.commonsdk.utils.UMUtils
//import com.umeng.socialize.PlatformConfig
//import com.umeng.socialize.UMShareAPI
//import com.umeng.socialize.UMShareConfig
//import io.realm.Realm
//import org.greenrobot.eventbus.EventBus
//import org.json.JSONObject
//import tuoyan.com.xinghuo_dayingindex.bean.BookRes
//import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
//import tuoyan.com.xinghuo_dayingindex.bean.WordsByCatalogkey
//import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
//import tuoyan.com.xinghuo_dayingindex.ui.books.report.RecommedNewsActivity
//import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
//import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
//import tuoyan.com.xinghuo_dayingindex.ui.main.ReceiverToViewActivity
//import tuoyan.com.xinghuo_dayingindex.ui.main.coupon.MainCouponActivity
//import tuoyan.com.xinghuo_dayingindex.ui.message.detail.MessageDetailActivity
//import tuoyan.com.xinghuo_dayingindex.ui.mine.coupon.CouponsActivity
//import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
//import tuoyan.com.xinghuo_dayingindex.umengpush.PushHelper
//import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
//import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
//import tuoyan.com.xinghuo_dayingindex.utlis.log.L
//import java.util.*
//import kotlin.properties.Delegates
//
//
///**
// * 自定义ApplicationLike类.
// *
// * 注意：这个类是Application的代理类，以前所有在Application的实现必须要全部拷贝到这里<br/>
// * 创建者：
// * 时间：  2019/1/22.
// */
//@SuppressWarnings("unused")
//@DefaultLifeCycle(
//    application = "tuoyan.com.xinghuo_daying.MyApp",
//    flags = ShareConstants.TINKER_ENABLE_ALL,
//    loadVerifyFlag = false
//)
//class MyAppLike(
//    application: Application?,
//    tinkerFlags: Int,
//    tinkerLoadVerifyFlag: Boolean,
//    applicationStartElapsedTime: Long,
//    applicationStartMillisTime: Long,
//    tinkerResultIntent: Intent?
//) : DefaultApplicationLike(
//    application,
//    tinkerFlags,
//    tinkerLoadVerifyFlag,
//    applicationStartElapsedTime,
//    applicationStartMillisTime,
//    tinkerResultIntent
//) {
//    private val store by lazy { Stack<Activity>() }
//    var data: List<WordsByCatalogkey>? = null
//    var bookres: List<BookRes>? = null
//    var resList: List<ResourceListBean>? = null
//
//    var loginComplete = false
//
//    var channal = "" //TODO 当前程序的渠道名称，用于网络请求时添加header
//
//    companion object {
//        var instance by Delegates.notNull<MyAppLike>()
//    }
//
//    var equipmentId = "" //极光设备id
//
//    override fun onCreate() {
//        super.onCreate()
//        instance = this
////        CrashHandler.getInstance(this.application)
////        Log.e("BRAND", Build.BRAND.toLowerCase())
////        if (Build.BRAND.toLowerCase() == "oppo") {
////            fix()
////            Log.e("BRAND", "oppo is found,fix it!!")
////        }
//        LiveSDKHelper.initSDK(application)
//        if (SpUtil.agreement) {
//            initApp()
//        }
//    }
//
//    fun initApp() {
//        // 设置开发设备，默认为false，上传补丁如果下发范围指定为“开发设备”，需要调用此接口来标识开发设备
////        Bugly.setIsDevelopmentDevice(application, true)
////        var strategy = CrashReport.UserStrategy(application)
////        CrashReport.initCrashReport(application, "51dd7ab1b2", true)
////        Bugly.init(application, "51dd7ab1b2", false)
////        try {
////            strategy.appChannel = UMUtils.getChannelByXML(application)//TODO 设置bugly渠道名
////        } catch (e: Exception) {
////        }
//        Realm.init(application)
//        /** 初始化 realm **/
////        if (SpUtil.uuid.isBlank()) {
////            SpUtil.uuid = UUID.randomUUID().toString().replace("-", "")
////        }
//        L.init("日志").logLevel(L.LogLevel.NONE)
//            .methodCount(2)
//            .methodOffset(0)
//            .bulid()
//        FileDownloader.setup(application)
//        FileDownloader.setGlobalPost2UIInterval(50)
//        registerActivityLifecycleCallbacks(SwitchBackgroundCallbacks())
//        umengConfig()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            val builder = StrictMode.VmPolicy.Builder()
//            StrictMode.setVmPolicy(builder.build())
//        }
//        initJPush()
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
////        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
////        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
//        initPlaybackDownLoader()
////        TalkFunLogger.setLogLevel(TalkFunLogger.LogLevel.ALL)
//        UdeskSDKManager.getInstance().initApiKey(application, UDESK_DOMAIN, UDESK_KEY, UDESK_ID)
////        requestHttpNDS()
//        initSaData()
//    }
//
//    /**
//     * 初始化common库
//     * 参数1:上下文，不能为空
//     * 参数2:【友盟+】 AppKey
//     * 参数3:【友盟+】 Channel
//     * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
//     * 参数5:Push推送业务的secret
//     */
//    private fun umengConfig() {
//        UMConfigure.setLogEnabled(true)// 友盟日志
//        PushHelper.preInit(application)
//        if (!SpUtil.agreement) {
//            return
//        }
//        val isMainProcess = UMUtils.isMainProgress(application)
//        if (isMainProcess) {
//            //启动优化：建议在子线程中执行初始化
//            Thread { PushHelper.init(application) }.start()
//        } else {
//            //若不是主进程（":channel"结尾的进程），直接初始化sdk，不可在子线程中执行
//            PushHelper.init(application)
//        }
//
//        val config = UMShareConfig()
//        config.isNeedAuthOnGetUserInfo(true)
//        UMShareAPI.get(application).setShareConfig(config)
//
//        PlatformConfig.setWeixin("wx35bdc73a79e2a430", "79e8f9668072517e643a860c458ac169")
//        PlatformConfig.setWXFileProvider("tuoyan.com.xinghuo_daying.fileprovider")
//        PlatformConfig.setQQZone("1105474912", "c7394704798a158208a74ab60104f0ba")
//        PlatformConfig.setQQFileProvider("tuoyan.com.xinghuo_daying.fileprovider")
//        PlatformConfig.setSinaWeibo("3240568197", "7d0161ac53ca6082d22d85726c5f881e", "http://m.sparke.cn/download.html")
//        PlatformConfig.setSinaFileProvider("tuoyan.com.xinghuo_daying.fileprovider")
//
//
//    }
//
//    private fun initJPush() {
////        JPushInterface.init(application)
////        JPushInterface.resumePush(application)
////        JPushInterface.getRegistrationID(application)
//        JVerificationInterface.init(application, 3000) { codeJ, messageJ ->
//            JVerificationInterface.preLogin(application, 3000) { code, content ->
//                Log.d("JVerificationInterface", "[$code]message=$content");
//            }
//        }
//        JVerificationInterface.setDebugMode(false)
//    }
//
//    //神策
//    private fun initSaData() {
//        val saConfigOptions = SAConfigOptions(SA_SERVER_URL)
//        saConfigOptions.setAutoTrackEventType(
//            SensorsAnalyticsAutoTrackEventType.APP_CLICK or
//                    SensorsAnalyticsAutoTrackEventType.APP_START or
//                    SensorsAnalyticsAutoTrackEventType.APP_END or
//                    SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN
//        ).enableLog(false).enableVisualizedAutoTrack(true)
//            .enableJavaScriptBridge(true)
////            .enableTrackAppCrash()
//        SensorsDataAPI.startWithConfigOptions(this.application, saConfigOptions)
//        // 初始化 SDK 之后，开启自动采集 Fragment 页面浏览事件
////        SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen()
//        //设置公共属性公共属性会保存在 App 本地缓存中。
//        // 可以通过 unregisterSuperProperty() 删除一个公共属性，
//        // 或使用 clearSuperProperties() 删除所有已设置的事件公共属性。
//        try {
//            val saProperties = JSONObject()
//            saProperties.put("platform_type", "Android")
//            saProperties.put("product_name", "星火英语")
//            saProperties.put("app_key", appId)
//            SensorsDataAPI.sharedInstance().registerSuperProperties(saProperties)
//        } catch (e: Exception) {
//        }
//        val userId = SpUtil.user.userId
//        if (userId != null && userId.isNotEmpty()) {
//            SensorsDataAPI.sharedInstance().login(SpUtil.user.userId);
//            try {
//                val properties = JSONObject()
//                properties.put("phone_number", SpUtil.userInfo.phone)
//                properties.put("user_nickname", SpUtil.userInfo.name)
//                SensorsDataAPI.sharedInstance().profileSet(properties)
//            } catch (e: Exception) {
//            }
//        }
//        SensorsFocusAPI.startWithConfigOptions(application, SFConfigOptions(SA_FOCUS_URL).setPopupListener(object : PopupListener {
//            override fun onPopupLoadSuccess(planId: String?) {
//            }
//
//            override fun onPopupLoadFailed(planId: String?, errorCode: Int, errorMessage: String?) {
//            }
//
//            override fun onPopupClick(planId: String?, actionModel: SensorsFocusActionModel?) {
//                when (actionModel) {
//                    SensorsFocusActionModel.OPEN_LINK -> {
//                        val url = actionModel.getValue();
//                        val intent = Intent(application, PostActivity::class.java)
//                        intent.putExtra(PostActivity.URL, url)
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        application.startActivity(intent)
//                    }
//                    SensorsFocusActionModel.COPY -> {
//                        val copyText = actionModel.value
//                        //获取剪贴板管理器：
//                        val cm = application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                        // 创建普通字符型ClipData
//                        val mClipData = ClipData.newPlainText("Label", copyText)
//                        // 将ClipData内容放到系统剪贴板里。
//                        cm.setPrimaryClip(mClipData)
//                        Toast.makeText(application, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
//                    }
//                    SensorsFocusActionModel.CLOSE -> {
//
//                    }
//                    SensorsFocusActionModel.CUSTOMIZE -> {
//                        val customizeJson = actionModel.extra
//                        val type = if (customizeJson.has("type")) customizeJson.getString("type") else ""
//                        val key = if (customizeJson.has("bizInfoKey")) customizeJson.getString("bizInfoKey") else ""
//                        val title = if (customizeJson.has("title")) customizeJson.getString("title") else "星火英语"
//                        when (type) {
//                            "1" -> {
//                                val intent0 = Intent()
//                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                intent0.setClass(application, LessonDetailActivity::class.java)
//                                intent0.putExtra(LessonDetailActivity.KEY, key)
//                                application.startActivity(intent0)
//                            }
//                            "2" -> {
//                                val intent0 = Intent()
//                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                intent0.setClass(application, MessageDetailActivity::class.java)
//                                intent0.putExtra(MessageDetailActivity.KEY, key)
//                                application.startActivity(intent0)
//                            }
//                            "3" -> {
//                                val intent0 = Intent()
//                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                intent0.setClass(application, PostActivity::class.java)
//                                intent0.putExtra(PostActivity.URL, "bookdetail?id=$key")
//                                application.startActivity(intent0)
//                            }
//                            "4" -> {
//                                val intent0 = Intent()
//                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                intent0.setClass(application, LessonListActivity::class.java)
//                                intent0.putExtra(LessonListActivity.KEY, key)
//                                intent0.putExtra(LessonListActivity.TITLE, title)
//                                application.startActivity(intent0)
//                            }
//                            "5" -> {
//                                val intent0 = Intent()
//                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                intent0.setClass(application, MainActivity::class.java)
//                                intent0.putExtra(MainActivity.POS, "3")
//                                application.startActivity(intent0)
//                                EventBus.getDefault().post("book")
//                            }
//                            "7" -> {
//                                val intent0 = Intent()
//                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                intent0.setClass(application, RecommedNewsActivity::class.java)
//                                intent0.putExtra(RecommedNewsActivity.TITLE, "备考干货")
//                                intent0.putExtra(RecommedNewsActivity.GRADE_KEY, key)
//                                application.startActivity(intent0)
//                            }
//                            "8" -> {
//                                WxMini.goWxMini(application, key)
//                            }
//                            "9" -> {
//                                val intent0 = Intent()
//                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                intent0.setClass(application, PostActivity::class.java)
//                                intent0.putExtra(PostActivity.URL, key)
//                                application.startActivity(intent0)
//                            }
//                            "10" -> {
//                                val intent0 = Intent()
//                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                intent0.setClass(application, ReceiverToViewActivity::class.java)
//                                intent0.putExtra(ReceiverToViewActivity.ID, key)
//                                application.startActivity(intent0)
//                            }
//                            "11" -> {
//                                val intent = Intent(application, MainCouponActivity::class.java)
//                                intent.putExtra(MainCouponActivity.KEY, key)
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                application.startActivity(intent)
//                            }
//                            "12" -> {
//                                val intent = Intent(application, CouponsActivity::class.java)
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                application.startActivity(intent)
//                            }
//                        }
//
//                    }
//                }
//            }
//
//            override fun onPopupClose(planId: String?) {
//            }
//        }))
//    }
//
////    fun initUmeng() {
////        UMConfigure.init(application, "5785f5ae67e58eb942000341", null, UMConfigure.DEVICE_TYPE_PHONE, "")
////    }
//
//    /**
//     * 尝试修复oppo R9 中 异常崩溃的问题
//     */
//    private fun fix() {
//        try {
//            val clazz = Class.forName("java.lang.Daemons\$FinalizerWatchdogDaemon")
//            val method = clazz.superclass?.getDeclaredMethod("stop")
//            method?.isAccessible = true
//            val field = clazz.getDeclaredField("INSTANCE")
//            field.isAccessible = true
//            method?.invoke(field.get(null))
//        } catch (e: Exception) {
//        }
//
//    }
//
//    inner class SwitchBackgroundCallbacks : Application.ActivityLifecycleCallbacks {
//        override fun onActivityPaused(activity: Activity?) {
//        }
//
//        override fun onActivityResumed(activity: Activity?) {
//        }
//
//        override fun onActivityStarted(activity: Activity?) {
//        }
//
//        override fun onActivityDestroyed(activity: Activity?) {
//            store.remove(activity)
//        }
//
//        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
//        }
//
//        override fun onActivityStopped(activity: Activity?) {
//        }
//
//        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
//            store.add(activity)
//        }
//    }
//
//    fun getCurActivity(): Activity = store.lastElement()
//
//
//    /**
//     * 直播回放下载相关配置
//     */
//    fun initPlaybackDownLoader() {
//        PlaybackDownloader.getInstance().init(application)
//        PlaybackDownloader.getInstance().setRootFolder(Environment.getExternalStorageDirectory().path + "/" + DOWNLOAD_PATH)
//        PlaybackDownloader.getInstance().setDownLoadThreadSize(3)
//    }
////    //-------------------------
//    /**
//     * install multiDex before install tinker
//     * so we don't need to put the tinker lib classes in the main dex
//     *
//     * @param base
//     */
//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    override fun onBaseContextAttached(base: Context) {
//        super.onBaseContextAttached(base)
//        //you must install multiDex whatever tinker is installed!
////        MultiDex.install(base)
////        MultiDex.install(base)
//
////        SampleApplicationContext.application = application
////        SampleApplicationContext.context = application
////        TinkerManager.setTinkerApplicationLike(this)
////
////        TinkerManager.initFastCrashProtect()
////        //should set before tinker is installed
////        TinkerManager.setUpgradeRetryEnable(true)
////
////        //optional set logIml, or you can use default debug log
////        TinkerInstaller.setLogIml(MyLog())
////
////        //installTinker after load multiDex
////        //or you can put com.tencent.tinker.** to main dex
////        TinkerManager.installTinker(this)
////        val tinker = Tinker.with(application)
//        // TODO: 安装tinker
//        Beta.installTinker(this)
//    }
//
//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    fun registerActivityLifecycleCallbacks(callback: Application.ActivityLifecycleCallbacks) {
//        application.registerActivityLifecycleCallbacks(callback)
//    }
//
//    override fun onTerminate() {
//        super.onTerminate()
//        Beta.unInit()
//    }
//}