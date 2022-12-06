package tuoyan.com.xinghuo_dayingindex.base

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import tuoyan.com.xinghuo_dayingindex.bean.LoginResponse
import tuoyan.com.xinghuo_dayingindex.bean.UserInfo
import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.login.LoginActivity
import tuoyan.com.xinghuo_dayingindex.utlis.*

/**
 * 创建者：
 * 时间：
 */
abstract class BaseV4Fragment : Fragment() {
    abstract val layoutResId: Int
    private var isFirstInit: Boolean = true
    private var isGoToLogin = true
    private var networkWrong: NetworkWrong? = null
    private val rootView by lazy {
        if (layoutResId != 0) View.inflate(context, layoutResId, null)
        else initView()
    }

    open fun initView(): View? {
        return null
    }

    private val dialog by lazy {
        with(Dialog(this.requireContext())) {
            val bar = ProgressBar(context)
            setContentView(
                bar,
                ViewGroup.LayoutParams(
                    DeviceUtil.dp2px(activity, 50F).toInt(),
                    DeviceUtil.dp2px(activity, 50F).toInt()
                )
            )
            setCancelable(true)
            setCanceledOnTouchOutside(false)
            window?.setDimAmount(0.4f)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            this
        }
    }
    private var isFirstVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("ClassName====", javaClass.simpleName)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        MPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isFirstInit) {
            isFirstInit = false
            configView(rootView)
            handleEvent()
            initData()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    /**
     * 初始化布局组件
     */
    open fun configView(view: View?) {

    }

    /**
     * 触控事件
     */
    open fun handleEvent() {
    }

    /*数据初始化*/
    open fun initData() {

    }

    open fun refresh() {
        initData()
    }

    /**
     * 首次可见
     */
    open fun onFirstVisible() {
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false
                onFirstVisible()
            }
        }
    }

    protected fun hideDialog() {
        dialog.hide()
    }

    protected fun showDialog() {
        dialog.show()
    }

    protected fun hideNetDialog() {
        networkWrong?.dismiss()

    }

    protected fun showNetDialog(topMargin: Int, bottomMargin: Int) {
        if (networkWrong == null) {
            networkWrong = NetworkWrong.make(this, topMargin, bottomMargin) {
                if (!NetWorkUtils.isNetWorkReachable()) return@make
                hideNetDialog()
                refresh()
            }
        }
        networkWrong?.show()
    }

    override fun onDestroy() {
        dialog.dismiss()
        super.onDestroy()
    }

    fun mToast(message: String) {
        val list = message.split(",")
        var msg = list[0]
        if (list.size > 1) {
            val code = list[1]
            if (code == "401") {
                if (!SpUtil.isLogin) msg = "忽略"
                if (activity !is MainActivity) activity?.onBackPressed() //图书详情这里的登陆异常不处理，回来后直接刷新数据
                startActivity(Intent(activity, LoginActivity::class.java))
                SpUtil.isLogin = false
                SpUtil.userInfo = UserInfo()
                SpUtil.user = LoginResponse()
                SpUtil.loginInfo = ""
            }
            if (isGoToLogin) {
                isGoToLogin = false
            } else msg = "忽略"
        }
        if (msg == "忽略") return
        if (activity == null || activity!!.isDestroyed || activity!!.isFinishing) return
        //OPPO真垃圾 弹个吐司也能蹦
        if (Build.BRAND.contains("OPPO|oppo".toRegex())) return
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
    }

    fun checkLogin(next: () -> Unit) {
        if (SpUtil.isLogin) {
            next()
        } else {
            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }

    /**
     * 判断当前网络状态
     */
    fun netCheck(content: String?, action: () -> Unit) {
        if (NetWorkUtils.isNetWorkReachable()) {
            if (NetWorkUtils.isWifiConnected()) {
                action()
            } else {
                AlertDialog.Builder(context)
                    .setMessage("您正在使用非wifi网络，将产生" + if (content != null) content + "M" else "" + "流量消耗")
                    .setPositiveButton("继续") { _, _ -> action() }
                    .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }.show()
            }
        } else {
            mToast("请检查网络")
        }
    }
}