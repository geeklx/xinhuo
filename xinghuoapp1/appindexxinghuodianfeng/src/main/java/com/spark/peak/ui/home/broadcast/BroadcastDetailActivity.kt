package com.spark.peak.ui.home.broadcast

import android.annotation.SuppressLint
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.AnnouncementDetail
import com.spark.peak.ui.home.HomePresenter
import kotlinx.android.synthetic.main.activity_broadcast_detaildf.*

class BroadcastDetailActivity : LifeActivity<HomePresenter>() {
    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_broadcast_detaildf

    private val key by lazy { intent.getStringExtra(KEY)?:"" }

    class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            url?.let { view?.loadUrl(it) }
            return true
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            handler?.proceed()
        }
    }

//    class MyWebChromeClient : WebChromeClient() {
//        override fun onProgressChanged(view: WebView?, newProgress: Int) {
//
//        }
//    }

    companion object {
        val KEY = "key"
    }

    override fun configView() {
        setSupportActionBar(tb_webview)
        tb_webview.setNavigationOnClickListener { finish() }
        title?.let {
            tv_title.text=title
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initData() {
        web_view.webViewClient= MyWebViewClient()
        web_view.settings.javaScriptEnabled = true
        web_view.settings.useWideViewPort = true

        presenter.getAnnouncementDetail(key){
            showData(it)
        }
    }

    private fun showData(detail: AnnouncementDetail){
        tv_title.text = detail.title
        tv_date.text = detail.time
        detail.content?.let {
            if (it.startsWith("http://")||it.startsWith("https://"))
                web_view.loadUrl(it)
            else
                web_view.loadDataWithBaseURL(null, format(it), "text/html", "utf-8", null)
        }
    }
    fun format(data : String)= "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\" />\n" +
            "<style>\n" +
            "*{margin:5; padding:5;}\n" + "img {\n" +
            "            max-width: 100% !important;\n" +
            "</style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            data +
            "</body>\n" +
            "</html>"
}
