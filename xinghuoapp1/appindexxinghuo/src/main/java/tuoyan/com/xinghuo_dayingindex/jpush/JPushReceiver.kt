//package tuoyan.com.xinghuo_daying.jpush
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//import cn.jpush.android.api.JPushInterface
//import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
//import org.greenrobot.eventbus.EventBus
//import org.json.JSONObject
//import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
//import tuoyan.com.xinghuo_dayingindex.ui.books.report.RecommedNewsActivity
//import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
//import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
//import tuoyan.com.xinghuo_dayingindex.ui.main.ReceiverToViewActivity
//import tuoyan.com.xinghuo_dayingindex.ui.main.coupon.MainCouponActivity
//import tuoyan.com.xinghuo_dayingindex.ui.message.detail.MessageDetailActivity
//import tuoyan.com.xinghuo_dayingindex.ui.mine.coupon.CouponsActivity
//import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
//import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
//
//
///**
// * Created by  on 2018/1/13.
// */
//class JPushReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        if (JPushInterface.ACTION_NOTIFICATION_OPENED == intent.action) {//用户点击通知的时候执行
//            saClick()
//            try {
//                val bundle = intent.extras
//                val extras = bundle?.getString(JPushInterface.EXTRA_EXTRA)
//                if (extras.isNullOrEmpty()) return
//                Log.e("JPUSH_EX", extras)
//                val jsonObject = JSONObject(extras)
//                val key = if (jsonObject.has("bizInfoKey")) jsonObject.getString("bizInfoKey") else ""
//                val type = if (jsonObject.has("type")) jsonObject.getString("type") else ""
//                val title = if (jsonObject.has("title")) jsonObject.getString("title") else "星火英语"
//                //辣鸡oppo 小米，跳转要求这么麻烦
//                //1  网课  2 咨询 3图书 4网课专题 5图书商城 6备考干活 7小程序 8外链 9直播小节
//                when (type) {
//                    "1" -> {
//                        val intent0 = Intent()
//                        intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent0.setClass(context, LessonDetailActivity::class.java)
//                        intent0.putExtra(LessonDetailActivity.KEY, key)
//                        context.startActivity(intent0)
//                    }
//                    "2" -> {
//                        val intent0 = Intent()
//                        intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent0.setClass(context, MessageDetailActivity::class.java)
//                        intent0.putExtra(MessageDetailActivity.KEY, key)
//                        context.startActivity(intent0)
//                    }
//                    "3" -> {
//                        val intent0 = Intent()
//                        intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent0.setClass(context, PostActivity::class.java)
//                        intent0.putExtra(PostActivity.URL, "bookdetail?id=$key")
//                        context.startActivity(intent0)
//                    }
//                    "4" -> {
//                        val intent0 = Intent()
//                        intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent0.setClass(context, LessonListActivity::class.java)
//                        intent0.putExtra(LessonListActivity.KEY, key)
//                        intent0.putExtra(LessonListActivity.TITLE, title)
//                        context.startActivity(intent0)
//                    }
//                    "5" -> {
//                        val intent0 = Intent()
//                        intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent0.setClass(context, MainActivity::class.java)
//                        intent0.putExtra(MainActivity.POS, "3")
//                        context.startActivity(intent0)
//                        EventBus.getDefault().post("book")
//                    }
//                    "7" -> {
//                        val intent0 = Intent()
//                        intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent0.setClass(context, RecommedNewsActivity::class.java)
//                        intent0.putExtra(RecommedNewsActivity.TITLE, "备考干货")
//                        intent0.putExtra(RecommedNewsActivity.GRADE_KEY, key)
//                        context.startActivity(intent0)
//                    }
//                    "8" -> {
//                        WxMini.goWxMini(context, key)
//                    }
//                    "9" -> {
//                        val intent0 = Intent()
//                        intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent0.setClass(context, PostActivity::class.java)
//                        intent0.putExtra(PostActivity.URL, key)
//                        context.startActivity(intent0)
//                    }
//                    "10" -> {
//                        val intent0 = Intent()
//                        intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent0.setClass(context, ReceiverToViewActivity::class.java)
//                        intent0.putExtra(ReceiverToViewActivity.ID, key)
//                        context.startActivity(intent0)
//                    }
//                    "11" -> {
//                        val intent0 = Intent(context, MainCouponActivity::class.java)
//                        intent0.putExtra(MainCouponActivity.KEY, key)
//                        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        context.startActivity(intent0)
//                    }
//                    "12" -> {
//                        val intent0 = Intent(context, CouponsActivity::class.java)
//                        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        context.startActivity(intent0)
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("JPUSH_EX", e.message)
//            }
//        }
//    }
//
//    //推送点击事件
//    fun saClick() {
//        try {
//            SensorsDataAPI.sharedInstance().track("click_push")
//        } catch (e: Exception) {
//        }
//    }
//}