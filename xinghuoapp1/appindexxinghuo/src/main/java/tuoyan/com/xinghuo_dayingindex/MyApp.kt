package tuoyan.com.xinghuo_dayingindex

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import cn.jiguang.verifysdk.api.JVerificationInterface
import cn.udesk.UdeskSDKManager
import com.bokecc.livemodule.LiveSDKHelper
import com.liulishuo.filedownloader.FileDownloader
import com.sensorsdata.analytics.android.sdk.SAConfigOptions
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.sf.core.SFConfigOptions
import com.sensorsdata.sf.core.SensorsFocusAPI
import com.sensorsdata.sf.ui.listener.PopupListener
import com.sensorsdata.sf.ui.view.SensorsFocusActionModel
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.utils.UMUtils
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareConfig
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.bean.WordsByCatalogkey
import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.report.RecommedNewsActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
import tuoyan.com.xinghuo_dayingindex.ui.main.ReceiverToViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.main.coupon.MainCouponActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.detail.MessageDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.coupon.CouponsActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.umengpush.PushHelper
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
import tuoyan.com.xinghuo_dayingindex.utlis.log.L
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager
import java.util.*
import kotlin.properties.Delegates

class MyApp : Application() {
    private val store by lazy { Stack<Activity>() }
    var data: List<WordsByCatalogkey>? = null
    var bookres: List<BookRes>? = null
    var resList: List<ResourceListBean>? = null

    var loginComplete = false

    var channal = "" //TODO 当前程序的渠道名称，用于网络请求时添加header

    companion object {
        var instance by Delegates.notNull<MyApp>()
    }

    var equipmentId = "" //极光设备id

    override fun onCreate() {
        super.onCreate()
        instance = this
//        CrashHandler.getInstance(this.this)
//        Log.e("BRAND", Build.BRAND.toLowerCase())
//        if (Build.BRAND.toLowerCase() == "oppo") {
//            fix()
//            Log.e("BRAND", "oppo is found,fix it!!")
//        }
        if (SpUtil.agreement) {
            initApp()
        }
    }

    fun initApp() {
        LiveSDKHelper.initSDK(this)
        // 设置开发设备，默认为false，上传补丁如果下发范围指定为“开发设备”，需要调用此接口来标识开发设备
//        Bugly.setIsDevelopmentDevice(this, true)
//        val strategy = CrashReport.UserStrategy(this)
        CrashReport.initCrashReport(this, "51dd7ab1b2", false)
//        Bugly.init(this, "51dd7ab1b2", false)
//        try {
//            strategy.appChannel = UMUtils.getChannelByXML(this)//TODO 设置bugly渠道名
//        } catch (e: Exception) {
//        }
        Realm.init(this)
        /** 初始化 realm **/
//        if (SpUtil.uuid.isBlank()) {
//            SpUtil.uuid = UUID.randomUUID().toString().replace("-", "")
//        }
        L.init("日志").logLevel(L.LogLevel.NONE)
            .methodCount(2)
            .methodOffset(0)
            .bulid()
        FileDownloader.setup(this)
        FileDownloader.setGlobalPost2UIInterval(150)
        registerActivityLifecycleCallbacks(SwitchBackgroundCallbacks())
        umengConfig()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
        initJPush()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
//        initPlaybackDownLoader()
//        TalkFunLogger.setLogLevel(TalkFunLogger.LogLevel.ALL)
        UdeskSDKManager.getInstance().initApiKey(this, UDESK_DOMAIN, UDESK_KEY, UDESK_ID)
//        requestHttpNDS()
        initSaData()
    }

    /**
     * 初始化common库
     * 参数1:上下文，不能为空
     * 参数2:【友盟+】 AppKey
     * 参数3:【友盟+】 Channel
     * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
     * 参数5:Push推送业务的secret
     */
    private fun umengConfig() {
        UMConfigure.setLogEnabled(true)// 友盟日志
        PushHelper.preInit(this)
        if (!SpUtil.agreement) {
            return
        }
        val isMainProcess = UMUtils.isMainProgress(this)
        if (isMainProcess) {
            //启动优化：建议在子线程中执行初始化
            Thread { PushHelper.init(this) }.start()
        } else {
            //若不是主进程（":channel"结尾的进程），直接初始化sdk，不可在子线程中执行
            PushHelper.init(this)
        }

        val config = UMShareConfig()
        config.isNeedAuthOnGetUserInfo(true)
        UMShareAPI.get(this).setShareConfig(config)

        PlatformConfig.setWeixin("wx35bdc73a79e2a430", "79e8f9668072517e643a860c458ac169")
        PlatformConfig.setWXFileProvider("tuoyan.com.xinghuo_daying.fileprovider")
        PlatformConfig.setQQZone("1105474912", "c7394704798a158208a74ab60104f0ba")
        PlatformConfig.setQQFileProvider("tuoyan.com.xinghuo_daying.fileprovider")
        PlatformConfig.setSinaWeibo("3240568197", "7d0161ac53ca6082d22d85726c5f881e", "http://m.sparke.cn/download.html")
        PlatformConfig.setSinaFileProvider("tuoyan.com.xinghuo_daying.fileprovider")


    }

    private fun initJPush() {
//        JPushInterface.init(this)
//        JPushInterface.resumePush(this)
//        JPushInterface.getRegistrationID(this)
        JVerificationInterface.init(this, 3000) { codeJ, messageJ ->
            JVerificationInterface.preLogin(this, 3000) { code, content ->
                Log.d("JVerificationInterface", "[$code]message=$content");
            }
        }
        JVerificationInterface.setDebugMode(false)
    }

    //神策
    private fun initSaData() {
        val saConfigOptions = SAConfigOptions(SA_SERVER_URL)
        saConfigOptions.setAutoTrackEventType(
            SensorsAnalyticsAutoTrackEventType.APP_CLICK or
                    SensorsAnalyticsAutoTrackEventType.APP_START or
                    SensorsAnalyticsAutoTrackEventType.APP_END or
                    SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN
        ).enableLog(false).enableVisualizedAutoTrack(true)
            .enableJavaScriptBridge(true).enableHeatMap(true)
//            .enableTrackAppCrash()
        SensorsDataAPI.startWithConfigOptions(this, saConfigOptions)
        // 初始化 SDK 之后，开启自动采集 Fragment 页面浏览事件
//        SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen()
        //设置公共属性公共属性会保存在 App 本地缓存中。
        // 可以通过 unregisterSuperProperty() 删除一个公共属性，
        // 或使用 clearSuperProperties() 删除所有已设置的事件公共属性。
        try {
            val saProperties = JSONObject()
            saProperties.put("platform_type", "Android")
            saProperties.put("product_name", "星火英语")
            saProperties.put("app_key", appId)
            SensorsDataAPI.sharedInstance().registerSuperProperties(saProperties)
        } catch (e: Exception) {
        }
        val userId = SpUtil.user.userId
        if (userId != null && userId.isNotEmpty()) {
            SensorsDataAPI.sharedInstance().login(SpUtil.user.userId);
            try {
                val properties = JSONObject()
                properties.put("phone_number", SpUtil.userInfo.phone)
                properties.put("user_nickname", SpUtil.userInfo.name)
                SensorsDataAPI.sharedInstance().profileSet(properties)
            } catch (e: Exception) {
            }
        }
        SensorsFocusAPI.startWithConfigOptions(this, SFConfigOptions(SA_FOCUS_URL).setPopupListener(object : PopupListener {
            override fun onPopupLoadSuccess(planId: String?) {
            }

            override fun onPopupLoadFailed(planId: String?, errorCode: Int, errorMessage: String?) {
            }

            override fun onPopupClick(planId: String?, actionModel: SensorsFocusActionModel?) {
                when (actionModel) {
                    SensorsFocusActionModel.OPEN_LINK -> {
                        val url = actionModel.getValue();
                        val intent = Intent(instance, PostActivity::class.java)
                        intent.putExtra(PostActivity.URL, url)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    SensorsFocusActionModel.COPY -> {
                        val copyText = actionModel.value
                        //获取剪贴板管理器：
                        val cm = instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        // 创建普通字符型ClipData
                        val mClipData = ClipData.newPlainText("Label", copyText)
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData)
                        Toast.makeText(instance, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                    }
                    SensorsFocusActionModel.CLOSE -> {

                    }
                    SensorsFocusActionModel.CUSTOMIZE -> {
                        val customizeJson = actionModel.extra
                        val type = if (customizeJson.has("type")) customizeJson.getString("type") else ""
                        val key = if (customizeJson.has("bizInfoKey")) customizeJson.getString("bizInfoKey") else ""
                        val title = if (customizeJson.has("title")) customizeJson.getString("title") else "星火英语"
                        when (type) {
                            "1" -> {
                                val intent0 = Intent()
                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent0.setClass(instance, LessonDetailActivity::class.java)
                                intent0.putExtra(LessonDetailActivity.KEY, key)
                                startActivity(intent0)
                            }
                            "2" -> {
                                val intent0 = Intent()
                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent0.setClass(instance, MessageDetailActivity::class.java)
                                intent0.putExtra(MessageDetailActivity.KEY, key)
                                startActivity(intent0)
                            }
                            "3" -> {
                                val intent0 = Intent()
                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent0.setClass(instance, PostActivity::class.java)
                                intent0.putExtra(PostActivity.URL, "bookdetail?id=$key")
                                startActivity(intent0)
                            }
                            "4" -> {
                                val intent0 = Intent()
                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent0.setClass(instance, LessonListActivity::class.java)
                                intent0.putExtra(LessonListActivity.KEY, key)
                                intent0.putExtra(LessonListActivity.TITLE, title)
                                startActivity(intent0)
                            }
                            "5" -> {
                                val intent0 = Intent()
                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent0.setClass(instance, MainActivity::class.java)
                                intent0.putExtra(MainActivity.POS, "3")
                                startActivity(intent0)
                                EventBus.getDefault().post("book")
                            }
                            "7" -> {
                                val intent0 = Intent()
                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent0.setClass(instance, RecommedNewsActivity::class.java)
                                intent0.putExtra(RecommedNewsActivity.TITLE, "备考干货")
                                intent0.putExtra(RecommedNewsActivity.GRADE_KEY, key)
                                startActivity(intent0)
                            }
                            "8" -> {
                                WxMini.goWxMini(instance, key)
                            }
                            "9" -> {
                                val intent0 = Intent()
                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent0.setClass(instance, PostActivity::class.java)
                                intent0.putExtra(PostActivity.URL, key)
                                startActivity(intent0)
                            }
                            "10" -> {
                                val intent0 = Intent()
                                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent0.setClass(instance, ReceiverToViewActivity::class.java)
                                intent0.putExtra(ReceiverToViewActivity.ID, key)
                                startActivity(intent0)
                            }
                            "11" -> {
                                val intent = Intent(instance, MainCouponActivity::class.java)
                                intent.putExtra(MainCouponActivity.KEY, key)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                            "12" -> {
                                val intent = Intent(instance, CouponsActivity::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }

                    }
                    else -> {}
                }
            }

            override fun onPopupClose(planId: String?) {
            }
        }))
    }

    inner class SwitchBackgroundCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            store.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            store.add(activity)
        }
    }

    fun getCurActivity(): Activity = store.lastElement()


    /**
     * 直播回放下载相关配置
     */
//    fun initPlaybackDownLoader() {
//        PlaybackDownloader.getInstance().init(this)
//        PlaybackDownloader.getInstance().setRootFolder(Environment.getExternalStorageDirectory().path + "/" + DOWNLOAD_PATH)
//        PlaybackDownloader.getInstance().setDownLoadThreadSize(3)
//    }
}