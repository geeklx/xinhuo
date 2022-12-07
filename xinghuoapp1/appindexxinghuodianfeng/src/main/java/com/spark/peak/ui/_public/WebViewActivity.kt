package com.spark.peak.ui._public

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import com.spark.peak.R
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.LifeActivity
import kotlinx.android.synthetic.main.activity_progress_web_view.*

class WebViewActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_progress_web_view

    private val title by lazy { intent.getStringExtra(TITLE) }
    private val url by lazy { intent.getStringExtra(URL) }

    class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url!!.startsWith("http://") || url.startsWith("https://"))
                view?.loadUrl(url)
            return true
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.proceed()
        }
    }

//    class MyWebChromeClient : WebChromeClient() {
//        override fun onProgressChanged(view: WebView?, newProgress: Int) {
//
//        }
//    }

    companion object {
        val TITLE = "title"
        val URL = "url"
    }

    override fun configView() {
        setSupportActionBar(tb_webview)
        tb_webview.setNavigationOnClickListener { finish() }
        title?.let {
            tv_title.text = title
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initData() {
        web_view.settings.useWideViewPort = true
        web_view.settings.domStorageEnabled = true
        web_view.settings.javaScriptEnabled = true
        web_view.settings.javaScriptCanOpenWindowsAutomatically = true

        web_view.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        web_view.settings.displayZoomControls = false
        web_view.settings.allowFileAccess = true // 允许访问文件
        web_view.settings.builtInZoomControls = true // 设置显示缩放按钮
        web_view.settings.setSupportZoom(true) // 支持缩放
        web_view.settings.loadWithOverviewMode = true
        web_view.settings.javaScriptCanOpenWindowsAutomatically = true
        web_view.webViewClient = MyWebViewClient()
        web_view.webChromeClient = object : WebChromeClient() {

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }//支持http和https混合请求
        url?.let {
            if (it.startsWith("http://") || it.startsWith("https://"))
                web_view.loadUrl(it)
            else
                web_view.loadDataWithBaseURL(null, format(it), "text/html", "utf-8", null)

        }
    }

    override fun onPause() {
        super.onPause()
        web_view.onPause()
    }

    override fun onResume() {
        super.onResume()
        web_view.onResume()
    }

    override fun onDestroy() {
        web_view.destroy()
        super.onDestroy()
    }

    fun format(data: String) = "<!doctype html>\n" +
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
