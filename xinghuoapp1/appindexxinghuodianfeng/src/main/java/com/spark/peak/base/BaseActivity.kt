package com.spark.peak.base


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.spark.peak.R
import com.spark.peak.bean.LoginResponse
import com.spark.peak.bean.UserInfo
import com.spark.peak.ui.MainActivity
import com.spark.peak.ui.common.login.LoginActivity
import com.spark.peak.ui.home.FRESH_FLAG
import com.spark.peak.ui.study.book.BookDetailActivity
import com.spark.peak.utlis.MPermissionUtils
import com.spark.peak.utlis.SpUtil
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.UMShareAPI
import org.jetbrains.anko.ctx
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


abstract class BaseActivity : AppCompatActivity() {
    var fullScreen = false
    var landScreen = false

    abstract val layoutResId: Int
    private var isGoToLogin = true
    private val dialog by lazy {
        with(Dialog(this)) {
            val bar = ProgressBar(context)
            setContentView(bar, ViewGroup.LayoutParams(dip(50), dip(50)))
            setCancelable(true)
            setCanceledOnTouchOutside(false)
            window?.setBackgroundDrawable(null)
            this
        }
    }
    private val im by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("ClassName====", javaClass.simpleName)
        if (this is MainActivity)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        else
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        if (!fullScreen) statusColor()
        if (layoutResId != 0) setContentView(layoutResId)
        else setContentView()
        if (!landScreen) requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //屏幕方向
        configView()
        handleEvent()
        initData()
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    /**
     * 7.0以后，广播静态注册失效，改用此种方式监听网络状态变化
     */
    fun initNetStateListener() {
        var cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= 24) {
            cm.requestNetwork(
                NetworkRequest.Builder().build(),
                object : ConnectivityManager.NetworkCallback() {
                    override fun onLost(network: Network) {
                        super.onLost(network)
                        onNetChange()
                    }

                    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                        super.onCapabilitiesChanged(network, networkCapabilities)
                        onNetChange()
                    }
                })
        }
    }

    open fun onNetChange() {}

    open fun statusColor() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.colorPrimary)
            .statusBarDarkFont(true).keyboardEnable(true).init()
    }

    open fun setContentView() {}

    open fun configView() {}

    open fun handleEvent() {}

    open fun initData() {}


    protected fun hideDialog() {
        dialog.hide()
    }

    protected fun showDialog() {
        dialog.show()
    }

    fun mToast(message: String) {
        ConnectivityManager.CONNECTIVITY_ACTION
        val list = message.split(",")
        var msg = list[0]
        if (list.size > 1) {

            val code = list[1]
            if (code == "401") {
                if (this !is MainActivity && this !is BookDetailActivity) finish() //图书详情这里的登陆异常不处理，回来后直接刷新数据
                startActivity<LoginActivity>()
                SpUtil.isLogin = false
                SpUtil.userInfo = UserInfo()
                SpUtil.user = LoginResponse()
            }
            if (isGoToLogin) {
                isGoToLogin = false
            } else msg = "忽略"
        }
        if (msg == "忽略") return
        toast(msg)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(ctx)
    }

    override fun onPause() {
        dialog.dismiss()
        super.onPause()
        MobclickAgent.onPause(ctx)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * 友盟回掉
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
        // TODO: 2018/5/17 10:20 霍述雷   友盟回调
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        MPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                if (v != null) {
                    im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    v.clearFocus()
                }
            }
        }

        return try {
            super.dispatchTouchEvent(ev)
        } catch (e: Exception) {
            false
        }
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

    fun refreshHomeData() {
        val intent = Intent(FRESH_FLAG)
        intent.putExtra("refresh", true)
        sendBroadcast(intent)
    }

    fun hideSoftKeybord(v: View) {
        im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        v.clearFocus()
    }
}
