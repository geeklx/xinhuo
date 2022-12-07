package com.spark.peak.ui.common.recover

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.TextView
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.bean.Register
import com.spark.peak.ui.dialog.CodeDialog
import com.spark.peak.utlis.MD5Util
import com.spark.peak.utlis.RegularUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_recover_passeorddf.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 * 创建者：
 * 时间：
 */
class RecoverPasswordActivity(override val layoutResId: Int = R.layout.activity_recover_passeorddf)
    : LifeActivity<RecoverPasswordPresenter>() {
    override val presenter by lazy { RecoverPasswordPresenter(this) }
    var disposable: Disposable? = null
    var phone: String = ""
    var code: String = ""
    var pwd: String = ""
    override fun configView() {
//        et_phone.hint = phone
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        et_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                phone = p0.toString().trim()
                bt_complete.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank() && RegularUtils.isPWD(pwd))
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
                bt_complete.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank() && RegularUtils.isPWD(pwd))
            }
        })
        et_pwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                pwd = p0.toString().trim()
                bt_complete.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank() && RegularUtils.isPWD(pwd))
            }
        })
    }

    fun complete(v: View) {
        if (!RegularUtils.isPWD(pwd)) {
            mToast("密码格式不正确")
            return
        }
        v.isEnabled = false

        presenter.findPwd(Register(phone, code, MD5Util.encrypt(pwd)), {
            mToast("修改成功")
            finish()
            v.isEnabled = true

        }) {
            v.isEnabled = true
        }
    }

    fun close(v: View) {
        phone = ""
        et_phone.text = SpannableStringBuilder.valueOf(phone)
    }

    fun hide(v: View) {
        v.isSelected = !v.isSelected
//        et_pwd.inputType = if (!v.isSelected) InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        et_pwd.transformationMethod = if (!v.isSelected) PasswordTransformationMethod.getInstance() else HideReturnsTransformationMethod.getInstance()
        et_pwd.setSelection(pwd.length)
    }

    fun code(v: View) {
        v as TextView
        if (RegularUtils.isMobileSimple(phone)) {
            CodeDialog(this, phone, "1", presenter) {
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
//                presenter.sendCode(SMSCode(phone, "1"), {
//                }) {
//                    disposable?.dispose()
//                }
                v.textColor = resources.getColor(R.color.color_ffffff)
                v.backgroundResource = R.drawable.bg_shape_25_abaeb0
            }.show()
        } else mToast("请输入正确的手机号码")
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

}