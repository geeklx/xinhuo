package tuoyan.com.xinghuo_dayingindex.utlis

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.geek.libutils.app.BaseApp
import tuoyan.com.xinghuo_dayingindex.MyApp

/**
 * 创建者： huoshulei
 * 时间：  2017/5/26.
 */
class NetWorkUtils private constructor() {
    companion object {
        @SuppressLint("MissingPermission")
        fun isNetWorkReachable(): Boolean {
            val manager = BaseApp.get().getSystemService(Context.CONNECTIVITY_SERVICE)
            if (manager != null) {
                if (manager is ConnectivityManager)
                    return manager.activeNetworkInfo != null
            }
            return false
        }

        @SuppressLint("MissingPermission")
                /**
                 * wifi是否已连接
                 *
                 * @param context
                 * @return
                 */
        fun isWifiConnected(): Boolean {
            val connManager = BaseApp.get().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

            return mWifi?.isConnected ?: true
        }
    }
}