package tuoyan.com.xinghuo_dayingindex.base

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.jiguang.verifysdk.api.JVerificationInterface
import cn.jiguang.verifysdk.api.JVerifyUIConfig
import cn.jiguang.verifysdk.api.LoginSettings
import com.blankj.utilcode.util.SPUtils
import io.reactivex.internal.disposables.ListCompositeDisposable
import org.jetbrains.anko.textColor
import org.jetbrains.anko.startActivity
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.ui.common.login.LoginActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil


abstract class LifeV4Fragment<out P : BasePresenter> : BaseV4Fragment(), OnProgress {
    abstract val presenter: P
    override val disposables by lazy { ListCompositeDisposable() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val className = javaClass.name.substring(javaClass.name.lastIndexOf(".") + 1)
        if (className == "HomeFragment" || className == "StudyFragment" || className == "MineFragment" || className == "BookFragment") {
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
        presenter.isLogin {
            if (it) {
                navigation()
            } else {
                if (JVerificationInterface.checkVerifyEnable(this.requireContext()) && "1" == SpUtil.isFreeLogin) {
                    initAutoLogin()
                } else {
                    this.requireContext().startActivity<LoginActivity>()
                }
            }
        }
    }

    fun isLogin(navigation: () -> Unit, onError: () -> Unit) {
        presenter.isLogin {
            if (it) {
                navigation()
            } else {
                onError()
                if (JVerificationInterface.checkVerifyEnable(this.requireContext()) && "1" == SpUtil.isFreeLogin) {
                    initAutoLogin()
                } else {
                    this.requireContext().startActivity<LoginActivity>()
                }
            }
        }
    }

    private val handlerLogin = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0x11) {
//                presenter.freeLogin(mutableMapOf("loginToken" to msg.data.getString("data")!!, "equipmentId" to MyApp.instance.equipmentId)) {
                presenter.freeLogin(mutableMapOf("loginToken" to msg.data.getString("data")!!, "equipmentId" to SPUtils.getInstance().getString("equipmentId",""))) {
                    JVerificationInterface.dismissLoginAuthActivity()
//                    if (activity is MainActivity) {
//                        (activity as MainActivity).onResume()
//                    }
                }
            }
        }
    }

    fun initAutoLogin() {
        val text = TextView(this.requireContext())
        text.text = "账号登录"
        text.textColor = ContextCompat.getColor(this.requireContext(),R.color.color_4882ff)
        text.textSize = 15f
        text.setPadding(
            DeviceUtil.dp2px(this.requireContext(), 15f).toInt(),
            DeviceUtil.dp2px(this.requireContext(), 15f).toInt(),
            DeviceUtil.dp2px(this.requireContext(), 15f).toInt(),
            DeviceUtil.dp2px(this.requireContext(), 15f).toInt()
        )
        val topView = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        topView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        text.layoutParams = topView
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val imageView = ImageView(this.requireContext())
        imageView.setImageResource(R.drawable.icon_back)
        params.topMargin = DeviceUtil.dp2px(this.requireContext(), 14f).toInt()
        params.leftMargin = DeviceUtil.dp2px(this.requireContext(), 12f).toInt()
        imageView.layoutParams = params
        val config = JVerifyUIConfig.Builder().setNavColor(0xffffff)
            .setNavText("").setNavReturnImgPath("icon_back").setLogoImgPath("logo").setNumberColor(0xff333333.toInt())
            .setLogBtnHeight(44).setPrivacyNavColor(0xffffff).setPrivacyNavTitleTextColor(0xff333333.toInt()).setPrivacyNavReturnBtn(imageView)
            .setPrivacyNavTitleTextSize(18).setPrivacyNavTitleTextBold(true).setPrivacyTextSize(12).setPrivacyCheckboxSize(22)
            .setAppPrivacyOne("用户服务协议", "${WEB_BASE_URL}login/agreement?isApp=1").setAppPrivacyTwo("隐私协议", "${WEB_BASE_URL}login/privacy?isApp=1")
            .setCheckedImgPath("agree_checked").setUncheckedImgPath("agree_normal").setAppPrivacyColor(0xff999999.toInt(), 0xff4882ff.toInt()).setPrivacyOffsetX(30)
            .addNavControlView(text) { context, view ->
                this.requireContext().startActivity(Intent(this.requireContext(), LoginActivity::class.java))
                JVerificationInterface.dismissLoginAuthActivity()
            }
            .build()
        JVerificationInterface.setCustomUIWithConfig(config, config)
        val settings = LoginSettings()
        settings.isAutoFinish = false
        settings.timeout = 15 * 1000
        JVerificationInterface.loginAuth(this.requireContext(), settings) { code: Int, content: String?, operator: String? ->
            if (code == 6000) {
                //获取loginToken成功
                val bundle = Bundle()
                bundle.putString("data", content)
                val msg = Message.obtain()
                msg.data = bundle
                msg.what = 0x11
                handlerLogin.sendMessage(msg)
            } else if (code != 6002) {
                //6002:用户取消获取loginToken
                this.requireContext().startActivity<LoginActivity>()
            }
        }
    }
}