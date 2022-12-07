package com.spark.peak.ui.mine.setting

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.blankj.utilcode.util.AppUtils
import com.spark.peak.BuildConfig
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.community.post.PostActivity
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.startActivity

class AboutActivity(override val layoutResId: Int = R.layout.activity_about) : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)

    //    override fun statusColor() {
//        ImmersionBar.with(this)
//                .transparentStatusBar()
//                .fitsSystemWindows(false)
//                .statusBarDarkFont(true)
//                .statusBarColor(R.color.color_ffffff)
//                .init()
//    }
    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        tv_version.text = "v${AppUtils.getAppVersionName()}"
    }

    override fun configView() {
        super.configView()
        var warmingStr = SpannableStringBuilder()
        warmingStr.append("您还可以了解我们的《用户服务协议》和《隐私政策》")
        var service = object : ClickableSpan() {
            override fun onClick(p0: View) {
                //1用户服务协议
                startActivity<PostActivity>(PostActivity.TITLE to "用户服务协议",
                        PostActivity.URL to "${WEB_BASE_URL}login/agreement?isApp=1")
            }
        }
        var personal = object : ClickableSpan() {
            override fun onClick(p0: View) {
                //2隐私政策
                startActivity<PostActivity>(PostActivity.TITLE to "隐私政策",
                        PostActivity.URL to "${WEB_BASE_URL}login/privacy?isApp=1")
            }
        }
        warmingStr.setSpan(service, 9, 17, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(personal, 18, 24, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(ForegroundColorSpan(Color.parseColor("#1482ff")), 9, 17, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(ForegroundColorSpan(Color.parseColor("#1482ff")), 18, 24, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        tv_about_warming.text = warmingStr
        tv_about_warming.movementMethod = LinkMovementMethod.getInstance()
    }
}
