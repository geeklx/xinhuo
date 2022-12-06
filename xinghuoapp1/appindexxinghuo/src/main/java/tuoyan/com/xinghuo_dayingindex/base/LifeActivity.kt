package tuoyan.com.xinghuo_dayingindex.base

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import cn.jiguang.verifysdk.api.JVerificationInterface
import cn.jiguang.verifysdk.api.JVerifyUIConfig
import cn.jiguang.verifysdk.api.LoginSettings
import com.blankj.utilcode.util.SPUtils
import io.reactivex.internal.disposables.ListCompositeDisposable
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import org.jetbrains.anko.startActivity
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.bean.LoginResponse
import tuoyan.com.xinghuo_dayingindex.bean.UserInfo
import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
import tuoyan.com.xinghuo_dayingindex.ui.ScannerActivity
import tuoyan.com.xinghuo_dayingindex.ui.StartActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.detail.BookDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.list.BookListActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.LocalReplayCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.login.LoginActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordImgActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.setting.SettingActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioLessonActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil


abstract class LifeActivity<out P : BasePresenter> : BaseActivity(), OnProgress {
    var freeLoginTo = false//是否从一键登录跳转到登录页面
    abstract val presenter: P
    override val disposables by lazy { ListCompositeDisposable() }
    override fun showProgress() {
        showDialog()
    }

    override fun dismissProgress() {
        hideDialog()
    }

    override fun onError(message: String) {
        if (this is ExerciseDetailKActivity) {
            mToast(message)
        } else if (this is MainActivity || this is VideoActivity || this is SettingActivity || this is StartActivity || this is AudioActivity || this is BookListActivity || this is AudioLessonActivity || this is ScannerActivity || this is LocalReplayCCActivity || this is EBookWordImgActivity) {

        } else if (message == "网络异常，请稍后再试" || message == "无法连接到服务器" || message == "连接超时") {
            showNetDialog()
        } else {
            mToast(message)
        }
    }

    override fun refresh() {
        presenter.postPV(javaClass.name.substring(javaClass.name.lastIndexOf(".") + 1))
        super.refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("postPV", javaClass.name.substring(javaClass.name.lastIndexOf(".") + 1))
        presenter.postPV(javaClass.name.substring(javaClass.name.lastIndexOf(".") + 1))
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
                if (JVerificationInterface.checkVerifyEnable(this) && "1" == SpUtil.isFreeLogin) {
                    initAutoLogin()
                } else {
                    startActivity<LoginActivity>()
                }
            }
        }
    }

    override fun mToast(message: String) {
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
                isLogin { }
            }
        }
        //OPPO真垃圾 弹个吐司也能蹦
        if (msg == "忽略" || (this is MainActivity && Build.BRAND.toLowerCase().contains("oppo"))) return
        toast(msg)
    }

    private val handlerLogin = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0x11) {
//                presenter.freeLogin(mutableMapOf("loginToken" to msg.data.getString("data")!!, "equipmentId" to MyApp.instance.equipmentId)) {
                presenter.freeLogin(mutableMapOf("loginToken" to msg.data.getString("data")!!, "equipmentId" to SPUtils.getInstance().getString("equipmentId",""))) {
                    JVerificationInterface.dismissLoginAuthActivity()
                }
            }
        }
    }

    fun initAutoLogin() {
        val text = TextView(this)
        text.text = "账号登录"
        text.textColor = ContextCompat.getColor(this, R.color.color_4882ff)
        text.textSize = 15f
        text.setPadding(
            DeviceUtil.dp2px(this, 15f).toInt(), DeviceUtil.dp2px(this, 15f).toInt(), DeviceUtil.dp2px(this, 15f).toInt(), DeviceUtil.dp2px(this, 15f).toInt()
        )
        val topView = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        topView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        text.layoutParams = topView
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.icon_back)
        params.topMargin = DeviceUtil.dp2px(this, 14f).toInt()
        params.leftMargin = DeviceUtil.dp2px(this, 12f).toInt()
        imageView.layoutParams = params
        val config = JVerifyUIConfig.Builder().setNavColor(0xffffff).setNavText("").setNavReturnImgPath("icon_back").setLogoImgPath("logo").setNumberColor(0xff333333.toInt()).setLogBtnHeight(44).setPrivacyNavColor(0xffffff)
            .setPrivacyNavTitleTextColor(0xff333333.toInt()).setPrivacyNavReturnBtn(imageView).setPrivacyNavTitleTextSize(18).setPrivacyNavTitleTextBold(true).setPrivacyTextSize(12).setPrivacyCheckboxSize(22)
            .setAppPrivacyOne("用户服务协议", "${WEB_BASE_URL}login/agreement?isApp=1").setAppPrivacyTwo("隐私协议", "${WEB_BASE_URL}login/privacy?isApp=1").setCheckedImgPath("agree_checked").setUncheckedImgPath("agree_normal")
            .setAppPrivacyColor(0xff999999.toInt(), 0xff4882ff.toInt()).setPrivacyOffsetX(30).addNavControlView(text) { context, view ->
                startActivity(Intent(context, LoginActivity::class.java))
                JVerificationInterface.dismissLoginAuthActivity()
            }.build()
        JVerificationInterface.setCustomUIWithConfig(config, config)
        val settings = LoginSettings()
        settings.isAutoFinish = false
        settings.timeout = 15 * 1000
        JVerificationInterface.loginAuth(this, settings) { code: Int, content: String?, operator: String? ->
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
                freeLoginTo = true
                startActivity<LoginActivity>()
            }
        }
    }

    fun addIntegral(text: String) {
        presenter.tastIntegral("7", text) {
            if (!it["integralRemark"].isNullOrEmpty()) {
                Toast.makeText(this, it["integralRemark"], Toast.LENGTH_LONG).show()
            }
        }
    }
}
