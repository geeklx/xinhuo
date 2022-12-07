package com.spark.peak.base

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.spark.peak.bean.LoginResponse
import com.spark.peak.bean.UserInfo
import com.spark.peak.ui.MainActivity
import com.spark.peak.ui.common.login.LoginActivity
import com.spark.peak.utlis.MPermissionUtils
import com.spark.peak.utlis.SpUtil
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.toast

/**
 * 创建者：
 * 时间：
 */
abstract class BaseFragment : Fragment() {
    abstract val layoutResId: Int
    private var isFirstInit: Boolean = true
    //    private var rootView by Delegates.notNull<View>()
    private var isGoToLogin = true

    private val rootView by lazy {
        if (layoutResId != 0) View.inflate(this.requireContext(), layoutResId, null)
        else initView()
    }

    open fun initView(): View? {
        return null
    }

    private val dialog by lazy {
        with(Dialog(this.requireContext())) {
            setContentView(View(context), ViewGroup.LayoutParams(0, 0))
            setCancelable(true)
            setCanceledOnTouchOutside(false)
            this
        }
    }
    private var isFirstVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("ClassName====", javaClass.simpleName)
        try {
//            GrowingIO.getInstance().setPageName(this, javaClass.simpleName)
        } catch (e: Exception) {
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        MPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        rootView = inflater.inflate(layoutResId, null, false)
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
//                if (!SpUtil.isLogin) msg = "忽略"
                if (activity !is MainActivity) activity?.onBackPressed() //图书详情这里的登陆异常不处理，回来后直接刷新数据
                startActivity<LoginActivity>()
                SpUtil.isLogin = false
                SpUtil.userInfo = UserInfo()
                SpUtil.user = LoginResponse()
            }
            if (isGoToLogin) {
                isGoToLogin = false
            } else msg = "忽略"
        }
//        var msg = message
//        if (msg.contains("重新登录")) {
//            if (!SpUtil.isLogin) msg = "忽略"
//            if (activity !is MainActivity) activity?.onBackPressed()
//            this@BaseFragment?.startActivity<LoginActivity>()
//            SpUtil.isLogin = false
//        }
        if (msg == "忽略") return
//        if (activity == null || activity.isDestroyed || activity.isFinishing) return
        //OPPO真垃圾 弹个吐司也能蹦
//        if (this is BackWordFragment && Build.BRAND.contains("OPPO|oppo".toRegex())) return
        activity?.toast(msg)
    }
}