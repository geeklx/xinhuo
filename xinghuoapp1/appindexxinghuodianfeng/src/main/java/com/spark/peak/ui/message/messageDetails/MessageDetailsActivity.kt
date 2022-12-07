package com.spark.peak.ui.message.messageDetails

import android.webkit.WebView
import android.webkit.WebViewClient
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import kotlinx.android.synthetic.main.activity_message_details.*

/**
 * 创建者：
 * 时间：
 */
class MessageDetailsActivity(override val layoutResId: Int = R.layout.activity_message_details)
    : LifeActivity<MessageDetailsPresenter>() {
    override val presenter by lazy { MessageDetailsPresenter(this) }
    private val url by lazy { intent.getStringExtra(URL)?:"" }
    override fun configView() {
        web_message_details.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { view?.loadUrl(it) }
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        web_message_details.loadUrl(url)

    }

    companion object {
        const val URL = "url"
    }
}