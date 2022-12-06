package tuoyan.com.xinghuo_dayingindex.ui.common.register
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
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.CodeDialog
import tuoyan.com.xinghuo_dayingindex.utlis.MD5Util
import tuoyan.com.xinghuo_dayingindex.utlis.RegularUtils
import java.util.concurrent.TimeUnit

/**
 * 创建者：
 * 时间：
 */
class RegisterActivity(override val layoutResId: Int = R.layout.activity_register)
    : LifeActivity<RegisterPresenter>() {
    override val presenter by lazy { RegisterPresenter(this) }
    var disposable: Disposable? = null
    var phone: String = ""
    var code: String = ""
    var pwd: String = ""
    override fun configView() {
        var warmingStr = SpannableStringBuilder()
        warmingStr.append("我已阅读并同意《用户服务协议》和《隐私政策》")
        var service = object : ClickableSpan() {
            override fun onClick(p0: View) {
                //1用户服务协议
                startActivity<WebViewActivity>(WebViewActivity.URL to WEB_BASE_URL + "login/agreement?isApp=1", WebViewActivity.TITLE to "用户服务协议")
            }
        }
        var personal = object : ClickableSpan() {
            override fun onClick(p0: View) {
                //2隐私政策
                startActivity<WebViewActivity>(WebViewActivity.URL to WEB_BASE_URL + "login/privacy?isApp=1", WebViewActivity.TITLE to "隐私政策")
            }
        }
        warmingStr.setSpan(service, 7, 15, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(personal, 16, 22, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(ForegroundColorSpan(Color.parseColor("#4c84ff")), 7, 15, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(ForegroundColorSpan(Color.parseColor("#4c84ff")), 16, 22, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        tv_register_agree_warming.text = warmingStr
        tv_register_agree_warming.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun handleEvent() {
        et_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                phone = p0.toString().trim()
                bt_register.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank() && RegularUtils.isPWD(pwd))
                iv_close.visibility = if (phone.isNotBlank()) View.VISIBLE else View.GONE
            }
        })
        et_code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                code = p0.toString().trim()
                bt_register.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank() && RegularUtils.isPWD(pwd))
            }
        })
        et_pwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                pwd = p0.toString().trim()
                bt_register.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank() && RegularUtils.isPWD(pwd))
            }
        })

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
    fun code(v: View) {
        v as TextView
        phone = et_phone.text.toString().trim()
        if (RegularUtils.isMobileSimple(phone)) {
            saVerify()
            CodeDialog(this, phone, "0", presenter) {
                v.isEnabled = false
                disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .map { 60 - it }
                        .take(61)
                        .doOnComplete {
                            v.isEnabled = true
                            v.textColor = resources.getColor(R.color.color_1482ff)
                            v.backgroundResource = R.drawable.bg_shape_25_1482ff
                            v.text = "获取验证码"
                        }
                        .doOnDispose {
                            v.isEnabled = true
                            v.textColor = resources.getColor(R.color.color_1482ff)
                            v.backgroundResource = R.drawable.bg_shape_25_1482ff
                            v.text = "获取验证码"
                        }
                        .subscribe {
                            v.text = "${it}s"
                        }
//            presenter.sendCode(SMSCode(phone, "0"), {
////                disposable?.dispose()
//            }) {
//                disposable?.dispose()
//            }
                v.textColor = resources.getColor(R.color.color_ffffff)
                v.backgroundResource = R.drawable.bg_shape_25_abaeb0
            }.show()
        } else mToast("请输入正确的手机号码")
    }

    //获取图片验证码,神策自定义事件
    private fun saVerify() {
        try {
            SensorsDataAPI.sharedInstance().track("verify_picture_captcha")
        } catch (e: Exception) {
        }
    }
    //    fun read(v: View) {
//        v.isSelected = !v.isSelected
//    }

    @SensorsDataTrackViewOnClick
    fun register(v: View) {
//        val code = et_code.text.toString().trim()
//        val pwd = et_pwd.text.toString().trim()
        if (code.isBlank()) {
            mToast("验证码为空")
            return
        }
        if (!RegularUtils.isPWD(pwd)) {
            mToast("密码格式不正确")
            return
        }
        if (!ckb_agree.isChecked) {
            mToast("请同意用户服务协议和隐私政策")
            return
        }
//        if (!tv_register.isSelected) {
//            mToast("请阅读下方提示并勾选")
//            return
//        }
        v.isEnabled = false
        presenter.register(
            mutableMapOf(
                "phone" to phone,
                "code" to code,
                "password" to MD5Util.encrypt(pwd),
//                "equipmentId" to MyApp.instance.equipmentId
                "equipmentId" to SPUtils.getInstance().getString("equipmentId","")
            ),
            {
                mToast("注册成功")
                onBackPressed()
                startActivity<MainActivity>()
//                    startActivity<GradeActivity>()
//                    EventBus.getDefault().post(EventMsg("login", -1))
                v.isEnabled = true
            }) {
            v.isEnabled = true
        }
    }

    @SensorsDataTrackViewOnClick
    fun agreement(v: View) {
        startActivity<PostActivity>(
                PostActivity.URL to "login/agreement")
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}