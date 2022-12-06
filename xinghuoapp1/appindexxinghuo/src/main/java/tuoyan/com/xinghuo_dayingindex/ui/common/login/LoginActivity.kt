package tuoyan.com.xinghuo_dayingindex.ui.common.login
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import cn.jiguang.verifysdk.api.JVerificationInterface
import com.blankj.utilcode.util.SPUtils
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_login.*
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.changePassword.ChangePasswordActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.code_login.CodeLoginActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.recover.RecoverPasswordActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.register.RegisterActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.PromptDialog
import tuoyan.com.xinghuo_dayingindex.utlis.MD5Util
import tuoyan.com.xinghuo_dayingindex.utlis.RegularUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.log.L


/**
 * 创建者：
 * 时间：
 */
class LoginActivity(override val layoutResId: Int = R.layout.activity_login)
    : LifeActivity<LoginPresenter>() {
    override val presenter by lazy { LoginPresenter(this) }
    var phone = ""
    var pwd = ""
    override fun configView() {
        if (JVerificationInterface.checkVerifyEnable(this) && "1" == SpUtil.isFreeLogin) {
            tv_auto_login.visibility = View.VISIBLE
            img_back.visibility = View.GONE
        } else {
            tv_auto_login.visibility = View.GONE
            img_back.visibility = View.VISIBLE
        }
        val warmingStr = SpannableStringBuilder()
        warmingStr.append("我已阅读并同意《用户服务协议》和《隐私政策》")
        val service = object : ClickableSpan() {
            override fun onClick(p0: View) {
                //1用户服务协议
                startActivity<WebViewActivity>(WebViewActivity.URL to WEB_BASE_URL + "login/agreement?isApp=1", WebViewActivity.TITLE to "用户服务协议")
            }
        }
        val personal = object : ClickableSpan() {
            override fun onClick(p0: View) {
                //2隐私政策
                startActivity<WebViewActivity>(WebViewActivity.URL to WEB_BASE_URL + "login/privacy?isApp=1", WebViewActivity.TITLE to "隐私政策")
            }
        }
        warmingStr.setSpan(service, 7, 15, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(personal, 16, 22, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(ForegroundColorSpan(Color.parseColor("#4c84ff")), 7, 15, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(ForegroundColorSpan(Color.parseColor("#4c84ff")), 16, 22, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        tv_login_agree_warming.text = warmingStr
        tv_login_agree_warming.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun handleEvent() {
        et_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                phone = p0.toString().trim()
                bt_login.isEnabled = (RegularUtils.isMobileSimple(phone) && RegularUtils.isPWD(pwd))
                iv_close.visibility = if (phone.isNotBlank()) View.VISIBLE else View.GONE
            }
        })
        et_pwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                pwd = p0.toString().trim()
                bt_login.isEnabled = (RegularUtils.isMobileSimple(phone) && RegularUtils.isPWD(pwd))
            }
        })

        tv_auto_login.setOnClickListener {
            if (JVerificationInterface.checkVerifyEnable(this) && "1" == SpUtil.isFreeLogin) {
                initAutoLogin()
            }
        }
    }

    @SensorsDataTrackViewOnClick
    fun register(v: View) {
        startActivity<RegisterActivity>()
//        startActivity<CodeLoginActivity>()
    }

    @SensorsDataTrackViewOnClick
    fun close(v: View) {
        phone = ""
        et_phone.setText(phone)
    }

    @SensorsDataTrackViewOnClick
    fun hide(v: View) {
        v.isSelected = !v.isSelected
//        et_pwd.inputType = if (!v.isSelected) InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        et_pwd.transformationMethod = if (!v.isSelected) PasswordTransformationMethod.getInstance() else HideReturnsTransformationMethod.getInstance()
        et_pwd.setSelection(pwd.length)
    }

    @SensorsDataTrackViewOnClick
    fun login(v: View) {
        if (!ckb_agree.isChecked) {
            mToast("请同意用户服务协议和隐私政策")
            return
        }
        val phone = et_phone.text.toString().trim()
        val pwd = et_pwd.text.toString().trim()
        if (!RegularUtils.isMobileSimple(phone)) {
            mToast("请输入正确的手机号码")
            return
        }
        if (!RegularUtils.isPWD(pwd)) {
            mToast("密码格式不正确")
            return
        }
        v.isEnabled = false
        isLoggingIn = true
        presenter.login(
            mutableMapOf(
                "username" to phone,
//                "equipmentId" to MyApp.instance.equipmentId,
                "equipmentId" to SPUtils.getInstance().getString("equipmentId",""),
                "password" to MD5Util.encrypt(pwd)
            ), {
                //                startActivity<GradeActivity>()
                SpUtil.loginInfo = "$phone,${MD5Util.encrypt(pwd)}"
                v.isEnabled = true
                isLoggingIn = false
                if (it == 10102) {
                    PromptDialog(this@LoginActivity, "验证码登录", {
                        et_pwd.setText("")
                        startActivity<ChangePasswordActivity>(
                            ChangePasswordActivity.PHONE to phone,
                            ChangePasswordActivity.TITLE to "重置密码"
                        )
                    }) {
                        startActivity<CodeLoginActivity>(CodeLoginActivity.PHONE to phone)
                    }.show()
                    return@login
                }

//            refreshHomeData()
                setResult(RESULT_OK)
                finish()
            }) {
            v.isEnabled = true
            isLoggingIn = false
        }
    }

    @SensorsDataTrackViewOnClick
    fun forgetPassword(v: View) {
//        if (!RegularUtils.isMobileSimple(phone)) {
//            mToast("请输入正确的手机号码")
//            return
//        }
        startActivity<RecoverPasswordActivity>()
    }

    var isLoggingIn = false

    @SensorsDataTrackViewOnClick
    fun qqLogin(v: View) {
        if (!isLoggingIn) {
            isLoggingIn = true
            UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.QQ, authListener)
        }
    }

    @SensorsDataTrackViewOnClick
    fun weChatLogin(v: View) {
        if (!isLoggingIn) {
            isLoggingIn = true
            UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, authListener)
        }
    }

    @SensorsDataTrackViewOnClick
    fun sinaLogin(v: View) {
        if (!isLoggingIn) {
            isLoggingIn = true
            UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.SINA, authListener)
        }
    }

    var authListener: UMAuthListener = object : UMAuthListener {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        override fun onStart(platform: SHARE_MEDIA) {

        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        override fun onComplete(platform: SHARE_MEDIA, action: Int, data: Map<String, String>) {
            L.d(data.toString())
//            val id = data["openid"] ?: ""
//            val name = data["name"]
//            val gender = data["gender"]
//            val iconurl = data["iconurl"]
//            isLoggingIn = false
            presenter.login_3(mutableMapOf(
//                    "equipmentId" to App.instance.equipmentId,
                    "username" to when (platform) {
                        SHARE_MEDIA.QQ -> data["openid"] ?: ""
                        SHARE_MEDIA.WEIXIN -> data["openid"] ?: ""
                        SHARE_MEDIA.SINA -> data["uid"] ?: ""
                        else -> ""
                    },
                    "thirdToken" to (data["access_token"] ?: ""),
                    "unionId" to when (platform) {
                        SHARE_MEDIA.QQ -> (data["unionid"] ?: "")
                        SHARE_MEDIA.WEIXIN -> (data["unionid"] ?: "")
                        SHARE_MEDIA.SINA -> (data["uid"] ?: "")
                        else -> ""
                    },
                    "loginType" to when (platform) {
                        SHARE_MEDIA.QQ -> "3"
                        SHARE_MEDIA.WEIXIN -> "4"
                        SHARE_MEDIA.SINA -> "2"
                        else -> "1"
                    },
                    "thirdKey" to when (platform) {
                        SHARE_MEDIA.QQ -> "1106013843"
                        SHARE_MEDIA.WEIXIN -> "wx91bde5fbce777507"
                        SHARE_MEDIA.SINA -> "4130745373"
                        else -> ""
                    }), {
                //                startActivity<GradeActivity>()
                bt_login.isEnabled = true
                isLoggingIn = false
//                if (!RegularUtils.isMobileSimple(SpUtil.userInfo.phone
//                                ?: ""))
                if (it == 10101) {
//                    startActivity<ChangePhoneActivity>()
                    return@login_3
                }
//                refreshHomeData()
                finish()
            }) {
                bt_login.isEnabled = true
                isLoggingIn = false
            }
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        override fun onError(platform: SHARE_MEDIA, action: Int, t: Throwable) {
            isLoggingIn = false
            Toast.makeText(mContext, "失败：" + t.message, Toast.LENGTH_LONG).show()
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        override fun onCancel(platform: SHARE_MEDIA, action: Int) {
            isLoggingIn = false
            Toast.makeText(mContext, "取消了", Toast.LENGTH_LONG).show()
        }
    }
}