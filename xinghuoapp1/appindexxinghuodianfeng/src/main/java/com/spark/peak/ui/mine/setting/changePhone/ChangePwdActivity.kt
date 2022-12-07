package com.spark.peak.ui.mine.setting.changePhone

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.blankj.utilcode.util.SPUtils
import com.spark.peak.MyApp
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.MainActivity
import com.spark.peak.utlis.MD5Util
import com.spark.peak.utlis.RegularUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_change_pwd.*
import org.jetbrains.anko.startActivity

/**
 * 创建者：
 * 时间：
 */
class ChangePwdActivity(override val layoutResId: Int = R.layout.activity_change_pwd) :
    LifeActivity<ChangePhonePresenter>() {
    override val presenter by lazy { ChangePhonePresenter(this) }
    var disposable: Disposable? = null
    private val phone by lazy { intent.getStringExtra(PHONE) ?:""}
    private val code by lazy { intent.getStringExtra(CODE)?:"" }
    var pwd: String = ""
    override fun configView() {
        et_phone.text = phone
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        et_pwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                pwd = p0.toString().trim()
                bt_complete.isEnabled = (RegularUtils.isPWD(pwd))
            }
        })
    }


    fun complete(v: View) {

        v.isEnabled = false
        presenter.bindPhone(mutableMapOf(
            "phone" to phone,
            "smsCode" to code,
//            "equipmentId" to MyApp.instance.equipmentId,
            "equipmentId" to SPUtils.getInstance().getString("JPushInterfacegetRegistrationID",""),
            "password" to MD5Util.encrypt(pwd)
        ), {
            v.isEnabled = true
            refreshHomeData()
            startActivity<MainActivity>()
            finish()
        }) {
            v.isEnabled = true
        }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    companion object {
        const val CODE = "code"
        const val PHONE = "phone"
    }
}