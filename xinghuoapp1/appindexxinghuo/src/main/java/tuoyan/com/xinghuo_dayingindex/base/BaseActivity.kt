package tuoyan.com.xinghuo_dayingindex.base


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.UMShareAPI
import org.jetbrains.anko.ctx
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.bean.LoginResponse
import tuoyan.com.xinghuo_dayingindex.bean.UserInfo
import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.detail.BookDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.LiveCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.login.LoginActivity
import tuoyan.com.xinghuo_dayingindex.utlis.MPermissionUtils
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils
import tuoyan.com.xinghuo_dayingindex.utlis.NetworkWrong
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil


abstract class BaseActivity : AppCompatActivity() {
    var fullScreen = false
    abstract val layoutResId: Int
    open val title: String = ""
    private val dialog by lazy {
        with(Dialog(this)) {
            val bar = ProgressBar(context)
            setContentView(bar, ViewGroup.LayoutParams(dip(50), dip(50)))
            setCancelable(true)
            setCanceledOnTouchOutside(false)
            window?.setDimAmount(0.4f)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            this
        }
    }

    private var networkWrong: NetworkWrong? = null
    private val im by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        if (this is MainActivity)
            window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            )
        else if (this !is LiveCCActivity)
            window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            )
        if (!fullScreen) statusColor()
        if (layoutResId != 0) setContentView(layoutResId)
        else setContentView()
        configView()
        handleEvent()
        initData()
        Log.i("ClassName====", javaClass.simpleName)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    /**
     * 7.0以后，广播静态注册失效，改用此种方式监听网络状态变化
     */
    fun initNetStateListener() {
        var cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= 24) {
            cm.requestNetwork(NetworkRequest.Builder().build(), object : ConnectivityManager.NetworkCallback() {
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

    protected fun getSystemUiVisibility(isFull: Boolean): Int {
        return if (isFull) {
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        } else {
            View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    open fun onNetChange() {}

    open fun statusColor() {
        ImmersionBar.with(this).fitsSystemWindows(true)
            .statusBarColor(R.color.colorPrimary)
            .statusBarDarkFont(true)
            .navigationBarColor(android.R.color.white)
            .keyboardEnable(true).init()
    }

//    abstract @LayoutRes
//    fun getLayoutResId(): Int

    open fun setContentView() {}

    open fun configView() {}

    open fun handleEvent() {}

    open fun initData() {}
    open fun refresh() {
        initData()
    }

    protected fun hideDialog() {
        dialog.hide()
    }

    protected fun showDialog() {
        dialog.show()
    }

    protected fun hideNetDialog() {
//        netDialog.hide()
        networkWrong?.dismiss()
    }

    protected fun showNetDialog() {
//        if (netDialog.isShowing) return
//        netDialog.show()
        if (networkWrong == null) {
            val resId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            val bottom = resources.getDimensionPixelSize(resId)
            val top = if (this is PostActivity) dip(20) else dip(64)
            networkWrong = NetworkWrong.make(this, top, 0) {
                if (!NetWorkUtils.isNetWorkReachable()) return@make
                hideNetDialog()
                refresh()
            }
        }
        networkWrong?.show()
    }

    open fun mToast(message: String) {
        ConnectivityManager.CONNECTIVITY_ACTION
        val list = message.split(",")
        var msg = list[0]
        if (list.size > 1) {
            val code = list[1]
            if (code == "401") {
                if (!SpUtil.isLogin) msg = "忽略"
                if (this !is MainActivity && this !is BookDetailActivity) finish() //图书详情这里的登陆异常不处理，回来后直接刷新数据
                SpUtil.isLogin = false
                SpUtil.userInfo = UserInfo()
                SpUtil.user = LoginResponse()
                SpUtil.loginInfo = ""
                startActivity<LoginActivity>()
            }
        }
        //OPPO真垃圾 弹个吐司也能蹦
        if (msg == "忽略" || (this is MainActivity && Build.BRAND.toLowerCase().contains("oppo"))) return
        toast(msg)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        dialog.dismiss()
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onDestroy() {
//        netDialog.dismiss()
        hideNetDialog()
        super.onDestroy()
    }

    /**
     * 友盟回掉
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
        // : 2018/5/17 10:20    友盟回调
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        MPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                val v = currentFocus
                if (isShouldHideKeyboard(v, ev)) {
                    im.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    v?.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
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

    //    fun refreshHomeData() {
//        var intent = Intent(RECEIVER_FLAG)
//        intent.putExtra("refresh", true)
//        sendBroadcast(intent)
//    }
//
//    fun checkLogin(next: () -> Unit) {
//        if (SpUtil.isLogin) {
//            next()
//        } else {
//            startActivity<LoginActivity>()
//        }
//    }
    @SensorsDataTrackViewOnClick
    fun onBack(v: View) {
        onBackPressed()
    }

    /**
     * 判断当前网络状态
     */
    fun netCheck(content: String?, action: () -> Unit) {
        if (NetWorkUtils.isNetWorkReachable()) {
            if (NetWorkUtils.isWifiConnected()) {
                action()
            } else {
                AlertDialog.Builder(ctx).setMessage("您正在使用非wifi网络，将产生" + if (content != null) content + "M" else "" + "流量消耗").setPositiveButton("继续") { _, _ -> action() }.setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }.show()
            }
        } else {
            mToast("请检查网络")
        }
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

    // 请求全屏特性
    fun requestFullScreenFeature() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        hideActionBar()
    }

    // 隐藏ActionBar
    fun hideActionBar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }
    }

    // 判断当前屏幕朝向是否为竖屏
    fun isPortrait(): Boolean {
        val mOrientation = applicationContext.resources.configuration.orientation
        return mOrientation != Configuration.ORIENTATION_LANDSCAPE
    }
}
