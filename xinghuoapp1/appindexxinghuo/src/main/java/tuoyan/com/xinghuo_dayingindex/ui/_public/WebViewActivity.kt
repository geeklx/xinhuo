package tuoyan.com.xinghuo_dayingindex.ui._public

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Build
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sensorsdata.analytics.android.sdk.SensorsDataIgnoreTrackAppViewScreen
import kotlinx.android.synthetic.main.activity_progress_web_view.*
import org.jetbrains.anko.ctx
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils

@SensorsDataIgnoreTrackAppViewScreen
class WebViewActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_progress_web_view

    override val title by lazy { intent.getStringExtra(TITLE)?:"" }
    private val url by lazy { intent.getStringExtra(URL)?:"" }
    private val needRequest by lazy { intent.getBooleanExtra(NEED_REQUEST,false) }
    private val share by lazy { intent.getBooleanExtra(SHARE,false) }

    class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url!!.startsWith("http://")||url.startsWith("https://"))
                view?.loadUrl(url)
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
        val TITLE = "title"
        val URL = "url"
        val NEED_REQUEST = "needRequest"
        val SHARE = "share"
    }

    override fun configView() {
        setSupportActionBar(tb_webview)
        tb_webview.setNavigationOnClickListener { finish() }
        title?.let {
            tv_title.text=title
        }
        if (share) ic_share.visibility = View.VISIBLE else View.GONE
        ic_share.setOnClickListener {
            ShareDialog(ctx) {
                ShareUtils.share(this, it, title, "专业.让学习更简单",
                        WEB_BASE_URL +"download.html")
            }.show()
        }
    }

    override fun onDestroy() {
        web_view.destroy()
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initData() {
        web_view.webViewClient= MyWebViewClient()
        web_view.settings.javaScriptEnabled = true
        web_view.settings.useWideViewPort = true
        web_view.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }//支持http和https混合请求

        if (needRequest){
            presenter.informationDetail(url){

            }
        }else{
            url?.let {
                if (it.startsWith("http://")||it.startsWith("https://"))
                    web_view.loadUrl(it)
                else
                    web_view.loadDataWithBaseURL(null, format(it), "text/html", "utf-8", null)

            }
        }

    }

    fun format(data : String)= "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\" />\n" +
            "<style>\n" +
            "*{margin:5; padding:25;}\n" + "img {\n" +
            "            max-width: 100% !important;\n" +
            "</style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            data +
            "</body>\n" +
            "</html>"
}
