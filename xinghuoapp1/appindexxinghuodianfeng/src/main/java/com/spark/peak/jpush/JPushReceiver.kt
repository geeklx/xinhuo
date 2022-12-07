package com.spark.peak.jpush

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.jpush.android.api.JPushInterface
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.netLessons.NetLessonsActivity
import org.json.JSONObject


/**
 * Created by 李昊 on 2018/1/13.
 */
class JPushReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
//        context.toast(intent.action)
        if (JPushInterface.ACTION_NOTIFICATION_OPENED == intent.action) {//用户点击通知的时候执行
            try {
                val bundle = intent.extras
                val extras = bundle?.getString(JPushInterface.EXTRA_EXTRA)
                if (extras.isNullOrEmpty()) return
                val jsonObject = JSONObject(extras)
                var key = jsonObject.getString("bizInfoKey")
                var type = jsonObject.getString("type")

                //辣鸡oppo 小米，跳转要求这么麻烦
                var intent0 = Intent()
                intent0.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                //1  网课  2 咨询
                if (type == "1") {
                    intent0.setClass(context, NetLessonsActivity::class.java)
                    intent0.putExtra(NetLessonsActivity.KEY, key)
//                    App.instance.startActivity<NetLessonsActivity>(NetLessonsActivity.KEY to key)

                } else if (type == "2") {
                    intent0.setClass(context, PostActivity::class.java)
                    intent0.putExtra(PostActivity.TITLE, "")
                    intent0.putExtra(
                        PostActivity.URL,
                        WEB_BASE_URL + "information/infoDetails?key=" + key
                    )
//                    App.instance.startActivity<PostActivity>(PostActivity.TITLE to "", PostActivity.URL to WEB_BASE_URL +"information/infoDetails?key="+ key)
                }
                context.startActivity(intent0)
//                val id = jsonObject.getString("msgid")
//                id?.let {
////                    val i = Intent(context, MessageDetailActivity::class.java)
////                    i.putExtra(MessageDetailActivity.ID, it)
////                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////                    context.startActivity(i)
////                    App.instance.startActivity<MessageDetailActivity>(MessageDetailActivity.ID to it)
//                }
            } catch (e: Exception) {
                Log.e("JPUSH_EX", e.message.toString())
            }
        }

    }
}