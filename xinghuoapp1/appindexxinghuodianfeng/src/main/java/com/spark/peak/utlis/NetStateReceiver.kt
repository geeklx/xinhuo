package com.spark.peak.utlis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.geek.libutils.app.BaseAppManager
import com.spark.peak.MyApp
import com.spark.peak.base.BaseActivity

/**
 * Created by 李昊 on 2018/7/18.
 */
class NetStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            try {
//                var currentActivity = MyApp.instance.getCurActivity()
                var currentActivity = BaseAppManager.getInstance().top()
                if (currentActivity is BaseActivity) {
                    currentActivity.onNetChange()
                }
            } catch (e: Exception) {
            }
        }
    }
}