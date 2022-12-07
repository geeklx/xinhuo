package com.spark.peak.ui.common.code_login

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.spark.peak.MyApp
import com.spark.peak.R
import com.spark.peak.base.EventMsg
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.MainActivity
import com.spark.peak.ui.common.grade.GradeActivity
import com.spark.peak.ui.dialog.CodeDialog
import com.spark.peak.utlis.RegularUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_code_login.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 * 创建者：
 * 时间：
 */
class CodeLoginActivity(override val layoutResId: Int = R.layout.activity_code_login) :
    LifeActivity<CodeLoginPresenter>() {
    override val presenter by lazy { CodeLoginPresenter(this) }
    var disposable: Disposable? = null
    val phone by lazy { intent.getStringExtra(PHONE)?:"" }
    var code: String = ""

    //    var pwd: String = ""
    override fun configView() {
        tv_phone.text = phone
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
//        et_phone.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                phone = p0.toString().trim()
//                bt_register.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank() /*&& RegularUtils.isPWD(pwd)*/)
//                iv_close.visibility = if (phone.isNotBlank()) View.VISIBLE else View.GONE
////                disposable?.dispose()// TODO: 2018/8/7 15:32 霍述雷
//            }
//        })
        et_code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                code = p0.toString().trim()
                bt_register.isEnabled =
                    (/*RegularUtils.isMobileSimple(phone) &&*/ code.isNotBlank() /*&& RegularUtils.isPWD(pwd)*/)
            }
        })
//        et_pwd.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                pwd = p0.toString().trim()
//                bt_register.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank() && RegularUtils.isPWD(pwd))
//            }
//        })

    }

//    fun close(v: View) {
//        phone = ""
//        et_phone.text = SpannableStringBuilder.valueOf(phone)
//    }

//    fun hide(v: View) {
//        v.isSelected = !v.isSelected
////        et_pwd.inputType = if (!v.isSelected) InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//        et_pwd.transformationMethod = if (!v.isSelected) PasswordTransformationMethod.getInstance() else HideReturnsTransformationMethod.getInstance()
//        et_pwd.setSelection(pwd.length)
//    }

    fun code(v: View) {
        v as TextView
//        phone = et_phone.text.toString().trim()
        if (RegularUtils.isMobileSimple(phone)) {
            CodeDialog(this, phone, "3", presenter) {
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
//                presenter.sendCode(SMSCode(phone, "0"), {
//                    //                disposable?.dispose()
//                }) {
//                    disposable?.dispose()
//                }
                v.textColor = resources.getColor(R.color.color_ffffff)
                v.backgroundResource = R.drawable.bg_shape_25_abaeb0
            }.show()
        }

    }

    fun register(v: View) {
//        val code = et_code.text.toString().trim()
//        val pwd = et_pwd.text.toString().trim()
        if (code.isBlank()) {
            mToast("验证码为空")
            return
        }
//        if (!RegularUtils.isPWD(pwd)) {
//            mToast("密码格式不正确")
//            return
//        }
        v.isEnabled = false
        presenter.login(
            mutableMapOf(
                "username" to phone,
                "thirdToken" to code,
                "loginType" to "5",
//                "equipmentId" to MyApp.instance.equipmentId
                "equipmentId" to SPUtils.getInstance().getString("JPushInterfacegetRegistrationID","")
            ),
            { it, isLogin ->
                if (isLogin) {
                    EventBus.getDefault().post(EventMsg("login", -1))
                    startActivity<MainActivity>()
                    refreshHomeData()
                } else {
                    startActivity<GradeActivity>("type" to "grade")
                }
                finish()
                v.isEnabled = true
            }) {
            v.isEnabled = true
        }
    }

//    fun agreement(v: View) {
//        startActivity<PostActivity>(PostActivity.TITLE to "星火用户注册协议",
//                PostActivity.URL to "${WEB_BASE_URL}login/Agreement")
////                PostActivity.URL to "http://10.20.0.106:3000/login/agreement")
//    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    companion object {
        const val PHONE = "phone"
    }
}