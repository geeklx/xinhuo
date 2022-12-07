package com.spark.peak.base

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.jiguang.verifysdk.api.JVerificationInterface
import cn.jiguang.verifysdk.api.JVerifyUIConfig
import cn.jiguang.verifysdk.api.LoginSettings
import com.blankj.utilcode.util.SPUtils
import com.spark.peak.MyApp
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.ui.MainActivity
import com.spark.peak.ui.common.grade.GradeActivity
import com.spark.peak.ui.common.login.LoginActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.netLessons.NetLessonsActivity
import com.spark.peak.ui.study.book.BookDetailActivity
import com.spark.peak.utlis.DeviceUtil
import com.spark.peak.utlis.SpUtil
import io.reactivex.internal.disposables.ListCompositeDisposable
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColor


abstract class LifeFragment<out P : BasePresenter> : BaseFragment(), OnProgress {
    abstract val presenter: P
    override val disposables by lazy { ListCompositeDisposable() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var className = javaClass.name.substring(javaClass.name.lastIndexOf(".") + 1)
        if (className == "HomeFragment" || className == "StudyFragment" || className == "MineFragment" || className == "CommunityFragment") {
            Log.d("postPV", className)
            presenter.postPV(className)
        }
    }

    override fun showProgress() {
        showDialog()
    }

    override fun dismissProgress() {
        hideDialog()
    }

    override fun onError(message: String) {
        mToast(message)
    }

    override fun onDestroy() {
        clear()
        super.onDestroy()
    }

    fun isLogin(navigation: () -> Unit) {
        checkLogin {
            navigation
        }
    }

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0x11) {
                runOnUiThread {
                    presenter.freeLogin(
                        mutableMapOf(
                            "loginToken" to msg.data.getString("data")!!,
//                            "equipmentId" to MyApp.instance.equipmentId
                            "equipmentId" to SPUtils.getInstance().getString("JPushInterfacegetRegistrationID","")
                        )
                    ) { it, isLogin ->
                        if (!isLogin) {
                            startActivity<GradeActivity>("type" to "grade")
                        } else {
                            if (activity is MainActivity) {
                                (activity as MainActivity).onResume()
                            }
                        }
                    }
                }
            }
        }
    }

    fun checkLogin(next: () -> Unit) {
        if (SpUtil.isLogin) {
            next()
        } else {
            if (JVerificationInterface.checkVerifyEnable(this.requireContext())) {
//                JVerificationInterface.getToken(this.requireContext()) { code: Int, content: String, operator: String ->
//                    Log.d("TAG", "token=" + content + ", operator=" + operator)
//                }85047867cad475bbd91b062f43e2fc8c
                initAutoLogin()
            } else {
                startActivity<LoginActivity>()
            }
        }
    }

    private fun initAutoLogin() {
        val text = TextView(this.requireContext())
        text.text = "账号登录"
        text.textColor = R.color.color_333333
        text.textSize = 16f
        text.setPadding(
            DeviceUtil.dp2px(this.requireContext(), 15f).toInt(),
            DeviceUtil.dp2px(this.requireContext(), 15f).toInt(),
            DeviceUtil.dp2px(this.requireContext(), 15f).toInt(),
            DeviceUtil.dp2px(this.requireContext(), 15f).toInt()
        )
        val topView = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        topView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        text.layoutParams = topView
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        val imageView = ImageView(this.requireContext())
        imageView.setImageResource(R.drawable.icon_login_back)
        params.topMargin = DeviceUtil.dp2px(this.requireContext(), 14f).toInt()
        params.leftMargin = DeviceUtil.dp2px(this.requireContext(), 12f).toInt()
        imageView.layoutParams = params
        val config = JVerifyUIConfig.Builder().setNavColor(0xffffff)
            .setNavText("").setNavReturnImgPath("icon_login_back").setLogoImgPath("logo")
            .setNumberColor(0xff333333.toInt())
            .setLogBtnHeight(44).setPrivacyNavColor(0xffffff)
            .setPrivacyNavTitleTextColor(0xff333333.toInt()).setPrivacyNavReturnBtn(imageView)
            .setPrivacyNavTitleTextSize(18).setPrivacyNavTitleTextBold(true).setPrivacyTextSize(12)
            .setPrivacyCheckboxSize(22)
            .setAppPrivacyOne("用户服务协议", "${WEB_BASE_URL}login/agreement?isApp=1")
            .setAppPrivacyTwo("隐私协议", "${WEB_BASE_URL}login/privacy?isApp=1")
            .setCheckedImgPath("agree_checked").setUncheckedImgPath("agree_normal")
            .setAppPrivacyColor(0xff999999.toInt(), 0xff2ca7ff.toInt())
            .addNavControlView(text) { context, view ->
                this.requireContext()
                    .startActivity(Intent(this.requireContext(), LoginActivity::class.java))
                JVerificationInterface.dismissLoginAuthActivity()
            }
            .build()
        JVerificationInterface.setCustomUIWithConfig(config, config)
        val settings = LoginSettings()
        settings.isAutoFinish = true
        settings.timeout = 15 * 1000
        JVerificationInterface.loginAuth(
            this.requireContext(),
            settings
        ) { code: Int, content: String?, operator: String? ->
            if (code == 6000) {
                val bundle = Bundle()
                bundle.putString("data", content)
                val msg = Message.obtain()
                msg.data = bundle
                msg.what = 0x11
                handler.sendMessage(msg)
            } else {
                startActivity<LoginActivity>()
            }
            JVerificationInterface.dismissLoginAuthActivity()
        }
    }

    fun scanTo(data: Map<String, String>) {
        presenter.getDataByScan(data) {
            when {
                it.type == "1" -> it.key?.let {
                    startActivity<PostActivity>(PostActivity.TITLE to "", PostActivity.URL to it)
                }
                it.type == "2" -> it.key?.let {
                    startActivity<BookDetailActivity>(
                        BookDetailActivity.KEY to it,
                        BookDetailActivity.TYPE to "1"
                    )
                }
                it.type == "3" -> it.key?.let {
                    startActivity<NetLessonsActivity>(NetLessonsActivity.KEY to it)
                }
//                it.type == "6" -> it.key?.let {
//                    checkLogin {
//                        startActivity<PostActivity>(PostActivity.TITLE to "", PostActivity.URL to WEB_BASE_URL + "community/faq/faq/")
//                    }
//                }
                it.type == "7" -> it.key?.let {
                    startActivity<PostActivity>(PostActivity.TITLE to "下载", PostActivity.URL to it)
                }
                it.type == "8" -> it.key?.let {
                    startActivity<PostActivity>(PostActivity.TITLE to "资源", PostActivity.URL to it)
                }
            }
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun update(msg: EventMsg) {
//        when (msg.action) {
//            "freeLogin" -> {
//
//            }
//        }
//    }
}