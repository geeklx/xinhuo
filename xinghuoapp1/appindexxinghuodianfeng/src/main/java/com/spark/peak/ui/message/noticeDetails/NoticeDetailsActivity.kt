package com.spark.peak.ui.message.noticeDetails

import android.webkit.WebView
import android.webkit.WebViewClient
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import kotlinx.android.synthetic.main.activity_notice_detailsdf.*

/**
 * 创建者：
 * 时间：
 */
class NoticeDetailsActivity(override val layoutResId: Int = R.layout.activity_notice_detailsdf)
    : LifeActivity<NoticeDetailsPresenter>() {
    override val presenter by lazy { NoticeDetailsPresenter(this) }
    private val url by lazy { intent.getStringExtra(URL) }
    override fun configView() {
        web_notice_details.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        url?.let { web_notice_details.loadUrl(it) }
    }

    companion object {
        const val URL = "url"
    }
}