package com.spark.peak

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import cn.jiguang.verifysdk.api.JVerificationInterface
import cn.jpush.android.api.JPushInterface
import com.sensorsdata.analytics.android.sdk.SAConfigOptions
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.bean.AudioRes
import com.spark.peak.utlis.SpUtil
import com.spark.peak.utlis.log.L
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareConfig
import org.json.JSONObject
import java.util.*
import kotlin.properties.Delegates

class MyApp : Application() {
    private val store by lazy { Stack<Activity>() }

    companion object {
        var instance by Delegates.notNull<MyApp>()
    }

    val equipmentId by lazy { JPushInterface.getRegistrationID(this) } //极光设备id

    var bookres: List<AudioRes>? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        if (SpUtil.agreement) {
            initApp()
        }
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
            .enableJavaScriptBridge(true)
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
            saProperties.put("product_name", "巅峰训练")
            saProperties.put("app_key", appId)
            saProperties.put("anonymousId", SensorsDataAPI.sharedInstance().anonymousId)
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
    }

    private fun initJPush() {
        JPushInterface.setDebugMode(false)
        JPushInterface.init(this)
        //极光推送一键登录
        JVerificationInterface.setDebugMode(false)
        JVerificationInterface.init(this, 3000) { codeJ, result ->
            JVerificationInterface.preLogin(this, 3 * 1000) { code, content ->
//                    Log.i("JVerificationInterface", "" + code + "=======" + content)
            }
        }
    }

    private fun initUMeng() {
        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:【友盟+】 AppKey
         * 参数3:【友盟+】 Channel
         * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数5:Push推送业务的secret
         */
        val config = UMShareConfig()
        config.isNeedAuthOnGetUserInfo(true)
        UMShareAPI.get(this).setShareConfig(config)
        UMConfigure.init(
            this,
            "58df0cd4f29d98137100112f",
            "channel",
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
        UMConfigure.setLogEnabled(true)// 友盟日志
        PlatformConfig.setWeixin("wx91bde5fbce777507", "a18c522c35ee7efbce899a64f50f8300")
        PlatformConfig.setQQZone("1106013843", "vky3mxfys9cA4Ysn")
        PlatformConfig.setSinaWeibo(
            "4130745373",
            "04f881f3e1d1cbce914449da0de56f77",
            "http://www.sparke.cn"
        )
    }

    fun initApp() {
        L.init("日志").logLevel(L.LogLevel.FULL)
            .methodCount(2)
            .methodOffset(0)
            .bulid()
        initJPush()
        registerActivityLifecycleCallbacks(SwitchBackgroundCallbacks())
        initUMeng()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        initSaData()
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
}