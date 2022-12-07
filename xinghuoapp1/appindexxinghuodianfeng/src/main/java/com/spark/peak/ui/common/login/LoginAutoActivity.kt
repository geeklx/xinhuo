package com.spark.peak.ui.common.login

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import cn.jiguang.verifysdk.api.JVerificationInterface
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.community.post.PostActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginAutoActivity : LifeActivity<LoginPresenter>() {

    override val presenter: LoginPresenter
        get() = LoginPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_login_auto

    override fun configView() {
        super.configView()
        initAgreement()
    }

    override fun handleEvent() {
        super.handleEvent()
    }

    override fun initData() {
        super.initData()
    }

    fun initAgreement() {
        val warmingStr = SpannableStringBuilder()
        warmingStr.append("我已阅读并同意《用户服务协议》和《隐私政策》")
        val service = object : ClickableSpan() {
            override fun onClick(p0: View) {
                //1用户服务协议
                startActivity<PostActivity>(PostActivity.TITLE to "用户服务协议",
                        PostActivity.URL to "${WEB_BASE_URL}login/agreement?isApp=1")
            }
        }
        val personal = object : ClickableSpan() {
            override fun onClick(p0: View) {
                //2隐私政策
                startActivity<PostActivity>(PostActivity.TITLE to "隐私政策",
                        PostActivity.URL to "${WEB_BASE_URL}login/privacy?isApp=1")
            }
        }
        warmingStr.setSpan(service, 7, 15, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(personal, 16, 22, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_2ca7ff)), 7, 15, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_2ca7ff)), 16, 22, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        tv_login_warming.text = warmingStr
        tv_login_warming.movementMethod = LinkMovementMethod.getInstance()
    }
}