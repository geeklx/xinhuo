package tuoyan.com.xinghuo_dayingindex.ui.message.detail

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_message_detail.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.Message

class MessageDetailActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId = R.layout.activity_message_detail
    private val message by lazy { intent.getSerializableExtra(DATA) as? Message }
    private val key by lazy { intent.getStringExtra(KEY)  }

    override fun configView() {
        web_view.webViewClient=object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url!!.startsWith("http://")||url.startsWith("https://"))
                    view?.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }
    }
    override fun initData() {
        key?.let {
            presenter.informationDetail(it){
                tv_name.text= it["title"]
                tv_time_label.text= it["time"]
                var data = it["content"]
                web_view.loadDataWithBaseURL(null, format(data ?: ""), "text/html", "utf-8", null)
            }
        }

    }
    companion object {
        const val DATA="data"
        const val KEY="key"
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

