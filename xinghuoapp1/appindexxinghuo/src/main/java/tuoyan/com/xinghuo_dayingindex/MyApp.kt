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

    var channal = "" //TODO ?????????????????????????????????????????????????????????header

    companion object {
        var instance by Delegates.notNull<MyApp>()
    }

    var equipmentId = "" //????????????id

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
        // ??????????????????????????????false?????????????????????????????????????????????????????????????????????????????????????????????????????????
//        Bugly.setIsDevelopmentDevice(this, true)
//        val strategy = CrashReport.UserStrategy(this)
        CrashReport.initCrashReport(this, "51dd7ab1b2", false)
//        Bugly.init(this, "51dd7ab1b2", false)
//        try {
//            strategy.appChannel = UMUtils.getChannelByXML(this)//TODO ??????bugly?????????
//        } catch (e: Exception) {
//        }
        Realm.init(this)
        /** ????????? realm **/
//        if (SpUtil.uuid.isBlank()) {
//            SpUtil.uuid = UUID.randomUUID().toString().replace("-", "")
//        }
        L.init("??????").logLevel(L.LogLevel.NONE)
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
     * ?????????common???
     * ??????1:????????????????????????
     * ??????2:?????????+??? AppKey
     * ??????3:?????????+??? Channel
     * ??????4:???????????????UMConfigure.DEVICE_TYPE_PHONE????????????UMConfigure.DEVICE_TYPE_BOX???????????????????????????
     * ??????5:Push???????????????secret
     */
    private fun umengConfig() {
        UMConfigure.setLogEnabled(true)// ????????????
        PushHelper.preInit(this)
        if (!SpUtil.agreement) {
            return
        }
        val isMainProcess = UMUtils.isMainProgress(this)
        if (isMainProcess) {
            //???????????????????????????????????????????????????
            Thread { PushHelper.init(this) }.start()
        } else {
            //?????????????????????":channel"????????????????????????????????????sdk??????????????????????????????
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

    //??????
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
        // ????????? SDK ??????????????????????????? Fragment ??????????????????
//        SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen()
        //?????????????????????????????????????????? App ??????????????????
        // ???????????? unregisterSuperProperty() ???????????????????????????
        // ????????? clearSuperProperties() ?????????????????????????????????????????????
        try {
            val saProperties = JSONObject()
            saProperties.put("platform_type", "Android")
            saProperties.put("product_name", "????????????")
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
                        //???????????????????????????
                        val cm = instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        // ?????????????????????ClipData
                        val mClipData = ClipData.newPlainText("Label", copyText)
                        // ???ClipData?????????????????????????????????
                        cm.setPrimaryClip(mClipData)
                        Toast.makeText(instance, "?????????????????????", Toast.LENGTH_SHORT).show()
                    }
                    SensorsFocusActionModel.CLOSE -> {

                    }
                    SensorsFocusActionModel.CUSTOMIZE -> {
                        val customizeJson = actionModel.extra
                        val type = if (customizeJson.has("type")) customizeJson.getString("type") else ""
                        val key = if (customizeJson.has("bizInfoKey")) customizeJson.getString("bizInfoKey") else ""
                        val title = if (customizeJson.has("title")) customizeJson.getString("title") else "????????????"
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
                                intent0.putExtra(RecommedNewsActivity.TITLE, "????????????")
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
     * ??????????????????????????????
     */
//    fun initPlaybackDownLoader() {
//        PlaybackDownloader.getInstance().init(this)
//        PlaybackDownloader.getInstance().setRootFolder(Environment.getExternalStorageDirectory().path + "/" + DOWNLOAD_PATH)
//        PlaybackDownloader.getInstance().setDownLoadThreadSize(3)
//    }
}