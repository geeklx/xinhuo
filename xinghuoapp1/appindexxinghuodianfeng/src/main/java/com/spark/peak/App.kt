//package com.spark.peak
//
//import android.annotation.TargetApi
//import android.app.Activity
//import android.app.Application
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.os.StrictMode
//import androidx.appcompat.app.AppCompatDelegate
//import androidx.multidex.MultiDex
//import cn.jiguang.verifysdk.api.JVerificationInterface
//import cn.jpush.android.api.JPushInterface
//import com.spark.peak.bean.AudioRes
//import com.spark.peak.tinker.MyLog
//import com.spark.peak.tinker.SampleApplicationContext
//import com.spark.peak.tinker.TinkerManager
//import com.spark.peak.utlis.SpUtil
//import com.spark.peak.utlis.log.L
//import com.tencent.bugly.crashreport.CrashReport
//import com.tencent.tinker.anno.DefaultLifeCycle
//import com.tencent.tinker.entry.DefaultApplicationLike
//import com.tencent.tinker.lib.tinker.Tinker
//import com.tencent.tinker.lib.tinker.TinkerInstaller
//import com.tencent.tinker.loader.shareutil.ShareConstants
//import com.umeng.commonsdk.UMConfigure
//import com.umeng.socialize.PlatformConfig
//import com.umeng.socialize.UMShareAPI
//import com.umeng.socialize.UMShareConfig
//import java.util.*
//import kotlin.properties.Delegates
//
///**
// * 创建者： huoshulei
// * 时间：  2017/4/20.
// */
//@SuppressWarnings("unused")
//@DefaultLifeCycle(application = "com.spark.peak.MyApp",
//        flags = ShareConstants.TINKER_ENABLE_ALL,
//        loadVerifyFlag = false)
//class App (
//        application: Application?,
//        tinkerFlags: Int,
//        tinkerLoadVerifyFlag: Boolean,
//        applicationStartElapsedTime: Long,
//        applicationStartMillisTime: Long,
//        tinkerResultIntent: Intent?
//) : DefaultApplicationLike(
//        application,
//        tinkerFlags,
//        tinkerLoadVerifyFlag,
//        applicationStartElapsedTime,
//        applicationStartMillisTime,
//        tinkerResultIntent
//) {
//    private val store by lazy { Stack<Activity>() }
//
//    companion object {
//        var instance by Delegates.notNull<App>()
//    }
//
//    val equipmentId by lazy { JPushInterface.getRegistrationID(application) } //极光设备id
//
//    var bookres: List<AudioRes>? = null
//    override fun onCreate() {
//        super.onCreate()
//        instance = this
////        if (SpUtil.uuid.isBlank()){
////            SpUtil.uuid=UUID.randomUUID().toString().replace("-","")
////        }
//        if (SpUtil.agreement) {
//            initApp()
//        }
//    }
//
//    fun initApp() {
//        L.init("日志").logLevel(L.LogLevel.FULL)
//            .methodCount(2)
//            .methodOffset(0)
//            .bulid()
//        CrashReport.initCrashReport(application, "dcb9b69195", true)
//        JPushInterface.setDebugMode(false)
//        JPushInterface.init(application)
//        //极光推送一键登录
//        JVerificationInterface.setDebugMode(false)
//        JVerificationInterface.init(application, 3000) { codeJ, result ->
//            JVerificationInterface.preLogin(application, 3 * 1000) { code, content ->
////                    Log.i("JVerificationInterface", "" + code + "=======" + content)
//            }
//        }
//
//        registerActivityLifecycleCallbacks(SwitchBackgroundCallbacks())
////        try {
////            GrowingIO.startWithConfiguration(this, Configuration()
////                    .useID()
////                    .trackAllFragments()
////                    //                .setChannel(AnalyticsConfig.getChannel(this)))
////                    .setChannel("huaweiapp"))
////        } catch (e: Exception) {
////        }
////        UMShareAPI.get(this)
//
//        /**
//         * 初始化common库
//         * 参数1:上下文，不能为空
//         * 参数2:【友盟+】 AppKey
//         * 参数3:【友盟+】 Channel
//         * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
//         * 参数5:Push推送业务的secret
//         */
//        val config = UMShareConfig()
//        config.isNeedAuthOnGetUserInfo(true)
//        UMShareAPI.get(application).setShareConfig(config)
////        if (SpUtil.agreement) {
//        UMConfigure.init(
//            application,
//            "58df0cd4f29d98137100112f",
//            "channel",
//            UMConfigure.DEVICE_TYPE_PHONE,
//            ""
//        )
////        } else {
////            UMConfigure.preInit(application, "58df0cd4f29d98137100112f", null)
////        }
//        UMConfigure.setLogEnabled(true)// 友盟日志
//        PlatformConfig.setWeixin("wx91bde5fbce777507", "a18c522c35ee7efbce899a64f50f8300")
//        PlatformConfig.setQQZone("1106013843", "vky3mxfys9cA4Ysn")
//        PlatformConfig.setSinaWeibo(
//            "4130745373",
//            "04f881f3e1d1cbce914449da0de56f77",
//            "http://www.sparke.cn"
//        )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            val builder = StrictMode.VmPolicy.Builder()
//            StrictMode.setVmPolicy(builder.build())
//        }
//
////        if (SpUtil.isPush) {
////            JPushInterface.resumePush(App.instance)
////        } else {
////            JPushInterface.stopPush(App.instance)
////        }
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//    }
//
////    fun initUmeng() {
////        UMConfigure.init(
////            application,
////            "58df0cd4f29d98137100112f",
////            "channel",
////            UMConfigure.DEVICE_TYPE_PHONE,
////            ""
////        )
////    }
//
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
//        MultiDex.install(base)
//
//        SampleApplicationContext.application = application
//        SampleApplicationContext.context = application
//        TinkerManager.setTinkerApplicationLike(this)
//
//        TinkerManager.initFastCrashProtect()
//        //should set before tinker is installed
//        TinkerManager.setUpgradeRetryEnable(true)
//
//        //optional set logIml, or you can use default debug log
//        TinkerInstaller.setLogIml(MyLog())
//
//        //installTinker after load multiDex
//        //or you can put com.tencent.tinker.** to main dex
//        TinkerManager.installTinker(this)
//        val tinker = Tinker.with(application)
//
//    }
//
//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    fun registerActivityLifecycleCallbacks(callback: Application.ActivityLifecycleCallbacks) {
//        application.registerActivityLifecycleCallbacks(callback)
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
//}