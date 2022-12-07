package com.spark.peak.ui.mine.setting.changePhone

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.MainActivity
import com.spark.peak.ui.dialog.CodeDialog
import com.spark.peak.utlis.RegularUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_change_phonedf.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 * 创建者：
 * 时间：
 */
class ChangePhoneActivity(override val layoutResId: Int = R.layout.activity_change_phonedf) :
    LifeActivity<ChangePhonePresenter>() {
    override val presenter by lazy { ChangePhonePresenter(this) }
    var disposable: Disposable? = null
    var phone: String = ""
    var code: String = ""

    //    var pwd: String = ""
    override fun configView() {

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
                bt_complete.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank())
            }
        })
        et_code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                code = p0.toString().trim()
                bt_complete.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank())
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
//                bt_complete.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank() && RegularUtils.isPWD(pwd))
//            }
//        })
    }


    fun complete(v: View) {
        val phone = et_phone.text.toString().trim()
//        val pwd = et_pwd.text.toString().trim()
        if (!RegularUtils.isMobileSimple(phone)) {
            mToast("请输入正确的手机号码")
            return
        }
//        if (!RegularUtils.isPWD(pwd)) {
//            mToast("密码格式不正确")
//            return
//        }

        v.isEnabled = false
        presenter.bindPhone(mutableMapOf(
            "phone" to phone,
//            "equipmentId" to MyApp.instance.equipmentId,
            "equipmentId" to SPUtils.getInstance().getString("JPushInterfacegetRegistrationID",""),
            "smsCode" to code
        ), {
            if (it == 10101) {
                startActivity<ChangePwdActivity>(
                    ChangePwdActivity.CODE to code,
                    ChangePwdActivity.PHONE to phone
                )
                return@bindPhone
            }
            startActivity<MainActivity>()
            refreshHomeData()
            finish()
            v.isEnabled = true
        }) {
            v.isEnabled = true
        }
    }

    fun code(v: View) {
        v as TextView
        phone = et_phone.text.toString().trim()
        if (RegularUtils.isMobileSimple(phone)) {
            CodeDialog(this, phone, "2", presenter) {
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
//            presenter.sendCode(SMSCode(phone, "2"), {
//            }) {
//                disposable?.dispose()
//            }
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