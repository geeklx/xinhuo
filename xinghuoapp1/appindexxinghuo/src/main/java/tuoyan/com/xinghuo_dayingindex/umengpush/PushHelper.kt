package tuoyan.com.xinghuo_dayingindex.umengpush

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.api.UPushRegisterCallback
import com.umeng.message.entity.UMessage
import org.greenrobot.eventbus.EventBus
import tuoyan.com.xinghuo_dayingindex.UMENG_APP_KEY
import tuoyan.com.xinghuo_dayingindex.UMENG_MESSAGE_SECRET
import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.report.RecommedNewsActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
import tuoyan.com.xinghuo_dayingindex.ui.main.ReceiverToViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.main.coupon.MainCouponActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.detail.MessageDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.coupon.CouponsActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini

/**
 * PushSDK集成帮助类
 */
class PushHelper {
    companion object {

        const val TAG = "PushHelper"

        /**
         * 预初始化，已添加子进程中初始化sdk。
         * 使用场景：用户未同意隐私政策协议授权时，延迟初始化
         *
         * @param context 应用上下文
         */
        fun preInit(context: Context) {
            PushAgent.setup(context, UMENG_APP_KEY, UMENG_MESSAGE_SECRET)
            UMConfigure.preInit(context, UMENG_APP_KEY, "")
        }

        /**
         * 初始化。
         * 场景：用户已同意隐私政策协议授权时
         *
         * @param context 应用上下文
         */
        fun init(context: Context) {
            // 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
            // 参数一：当前上下文context；
            // 参数二：应用申请的Appkey；
            // 参数三：渠道名称；
            // 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
            // 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息
            UMConfigure.init(context, UMENG_APP_KEY, "", UMConfigure.DEVICE_TYPE_PHONE, UMENG_MESSAGE_SECRET)

            //获取消息推送实例
            val pushAgent = PushAgent.getInstance(context)

            //推送消息拦截处理
            pushAdvancedFunction(context)

            //注册推送服务，每次调用register方法都会回调该接口
            pushAgent.register(object : UPushRegisterCallback {
                override fun onSuccess(deviceToken: String?) {
                    //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                    Log.i(TAG, "deviceToken --> $deviceToken")
                    //获取deviceToken可通过接口：PushAgent.getInstance(context).getRegistrationId();
                    //可设置别名，推送时使用别名推送
//                    val alias = "123456";
//                    val type = "aa";
//                    pushAgent.setAlias(alias, type) { success, message -> Log.i(TAG, "setAlias " + success + " msg:" + message); }
                }

                override fun onFailure(errCode: String?, errDesc: String?) {
                    Log.e(TAG, "register failure：--> code:$errCode,desc:$errDesc")
                }

            })

        }

        //推送高级功能集成说明
        private fun pushAdvancedFunction(context: Context) {
            val pushAgent = PushAgent.getInstance(context)
            //设置通知栏显示通知的最大个数（0～10），0：不限制个数
            pushAgent.displayNotificationNumber = 0

//            //如果修改了AndroidManifest.xml中package属性值，需要修改调用如下接口，否则可能会出现不显示通知的情况
//            pushAgent.resourcePackageName = "tuoyan.com.xinghuo_daying"

            //推送消息处理,没有特别要求不处理
            val msgHandler = object : UmengMessageHandler() {
                //处理通知栏消息
                override fun dealWithNotificationMessage(context: Context?, msg: UMessage?) {
                    super.dealWithNotificationMessage(context, msg)
                }

                //自定义通知样式，此方法可以修改通知样式等
                override fun getNotification(context: Context?, msg: UMessage?): Notification {
                    return super.getNotification(context, msg)
                }

                //处理透传消息,自定义消息不是通知，默认不会被SDK展示到通知栏上
                override fun dealWithCustomMessage(context: Context?, msg: UMessage?) {
                    super.dealWithCustomMessage(context, msg)
                    dealWithMessage(context, msg)
                }
            }
            //pushAgent.messageHandler = msgHandler

            //推送消息点击处理
            val notificationClickHandler = object : UmengNotificationClickHandler() {
                override fun openActivity(context: Context?, msg: UMessage?) {
                    super.openActivity(context, msg)
                }

                override fun launchApp(context: Context?, msg: UMessage?) {
                    super.launchApp(context, msg)
                    dealWithAction(context, msg)
                }

                override fun dismissNotification(context: Context?, msg: UMessage?) {
                    super.dismissNotification(context, msg)
                }

                override fun dealWithCustomAction(context: Context?, msg: UMessage?) {
                    super.dealWithCustomAction(context, msg)
                    dealWithAction(context, msg)
                }
            }
            pushAgent.notificationClickHandler = notificationClickHandler
        }

        private fun dealWithMessage(context: Context?, msg: UMessage?) {
        }

        private fun dealWithAction(context: Context?, msg: UMessage?) {
            //自定义行为放在了 custom 参数中
            //请在自定义Application中调用此接口，如果在Activity中调用，当应用进程关闭情况下，设置无效；
            // UmengNotificationClickHandler是在BroadcastReceiver中被调用，因此若需启动Activity，
            // 需为Intent添加Flag：Intent.FLAG_ACTIVITY_NEW_TASK，否则无法启动Activity。
            saClick()
            msg?.let { mMsg ->
                val sfData = mMsg.extra["sf_data"] ?: ""
                var key = mMsg.extra["bizInfoKey"] ?: ""
                var type = mMsg.extra["type"] ?: ""
                var title = mMsg.extra["title"] ?: ""
                var sfType = "UPush"//神策返回：LINK  CUSTOMIZED；默认友盟返回
                var sfLink = ""
                if (!sfData.isNullOrEmpty()) {
                    val sfMap = Gson().fromJson(sfData, Map::class.java)
                    sfType = (sfMap["sf_landing_type"] ?: "") as String
                    sfLink = (sfMap["sf_link_url"] ?: "") as String//LINK 返回
                    if ("CUSTOMIZED" == sfType) {
                        val customizedMap = sfMap["customized"] as LinkedTreeMap<*, *>
                        key = (customizedMap["bizInfoKey"] ?: "") as String//CUSTOMIZED 返回
                        type = (customizedMap["type"] ?: "") as String//CUSTOMIZED 返回
                        title = (customizedMap["title"] ?: "") as String//CUSTOMIZED 返回
                    }
                }
                context?.let { ctx ->
                    if ("LINK" == sfType) {
                        openUrl(ctx, sfLink)
                    } else if ("CUSTOMIZED" == sfType || "UPush" == sfType) {
                        when (type) {
                            "1" -> {
                                val intent = Intent()
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.setClass(ctx, LessonDetailActivity::class.java)
                                intent.putExtra(LessonDetailActivity.KEY, key)
                                ctx.startActivity(intent)
                            }
                            "2" -> {
                                val intent = Intent()
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.setClass(ctx, MessageDetailActivity::class.java)
                                intent.putExtra(MessageDetailActivity.KEY, key)
                                ctx.startActivity(intent)
                            }
                            "3" -> {
                                val intent = Intent()
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.setClass(ctx, PostActivity::class.java)
                                intent.putExtra(PostActivity.URL, "bookdetail?id=$key")
                                ctx.startActivity(intent)
                            }
                            "4" -> {
                                val intent = Intent()
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.setClass(ctx, LessonListActivity::class.java)
                                intent.putExtra(LessonListActivity.KEY, key)
                                intent.putExtra(LessonListActivity.TITLE, title)
                                ctx.startActivity(intent)
                            }
                            "5" -> {
                                val intent = Intent()
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.setClass(ctx, MainActivity::class.java)
                                intent.putExtra(MainActivity.POS, "3")
                                ctx.startActivity(intent)
                                EventBus.getDefault().post("book")
                            }
                            "7" -> {
                                val intent = Intent()
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.setClass(ctx, RecommedNewsActivity::class.java)
                                intent.putExtra(RecommedNewsActivity.TITLE, "备考干货")
                                intent.putExtra(RecommedNewsActivity.GRADE_KEY, key)
                                ctx.startActivity(intent)
                            }
                            "8" -> {
                                WxMini.goWxMini(ctx, key)
                            }
                            "9" -> {
                                val intent = Intent()
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.setClass(ctx, PostActivity::class.java)
                                intent.putExtra(PostActivity.URL, key)
                                ctx.startActivity(intent)
                            }
                            "10" -> {
                                val intent = Intent()
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.setClass(ctx, ReceiverToViewActivity::class.java)
                                intent.putExtra(ReceiverToViewActivity.ID, key)
                                ctx.startActivity(intent)
                            }
                            "11" -> {
                                val intent = Intent(ctx, MainCouponActivity::class.java)
                                intent.putExtra(MainCouponActivity.KEY, key)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                ctx.startActivity(intent)
                            }
                            "12" -> {
                                val intent = Intent(ctx, CouponsActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                ctx.startActivity(intent)
                            }
                        }
                    }
                }
            }
        }

        /**
         * 注册设备推送通道（小米、华为等设备的推送）
         *
         * @param context 应用上下文
         */
        fun registerDeviceChannel(context: Context) {
//            //小米通道，填写您在小米后台APP对应的xiaomi id和key
//            MiPushRegistar.register(context, PushConstants.MI_ID, PushConstants.MI_KEY);
//            //华为，注意华为通道的初始化参数在minifest中配置
//            HuaWeiRegister.register((Application) context.getApplicationContext());
//            //魅族，填写您在魅族后台APP对应的app id和key
//            MeizuRegister.register(context, PushConstants.MEI_ZU_ID, PushConstants.MEI_ZU_KEY);
//            //OPPO，填写您在OPPO后台APP对应的app key和secret
//            OppoRegister.register(context, PushConstants.OPPO_KEY, PushConstants.OPPO_SECRET);
//            //vivo，注意vivo通道的初始化参数在minifest中配置
//            VivoRegister.register(context);
        }

        //推送点击事件
        fun saClick() {
            try {
                SensorsDataAPI.sharedInstance().track("click_push")
            } catch (e: Exception) {
            }
        }

        fun openUrl(context: Context, url: String) {
            try {
                val intent = Intent("android.intent.action.VIEW", Uri.parse(url));
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent);
            } catch (e: java.lang.Exception) {
            }
        }
    }
}