package com.spark.peak.ui.community.post


import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import com.blankj.utilcode.util.SPUtils
import com.geek.libutils.app.BaseApp
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.google.gson.Gson
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.appId
import com.spark.peak.base.EventMsg
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.AudioRes
import com.spark.peak.net.Re
import com.spark.peak.sonic.SonicRuntimeImpl
import com.spark.peak.sonic.SonicSessionClientImpl
import com.spark.peak.ui._public.PDFActivity
import com.spark.peak.ui.common.login.LoginActivity
import com.spark.peak.ui.dialog.ShareDialog
import com.spark.peak.ui.exercise.detail.ExerciseDetailActivity2
import com.spark.peak.ui.exercise.parsing.ExerciseParsingActivity
import com.spark.peak.ui.study.book.BookDetailActivity
import com.spark.peak.ui.video.AudioActivity
import com.spark.peak.ui.video.SparkVideoActivity
import com.spark.peak.utlis.ShareUtils
import com.spark.peak.utlis.SpUtil
import com.spark.peak.utlis.WebCameraHelper
import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicEngine
import com.tencent.sonic.sdk.SonicSession
import com.tencent.sonic.sdk.SonicSessionConfig
import kotlinx.android.synthetic.main.activity_postdf.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/**
 * 创建者：
 * 时间：
 */
class PostActivity(override val layoutResId: Int = R.layout.activity_postdf) :
    LifeActivity<PostPresenter>() {
    override val presenter by lazy { PostPresenter(this) }
    private val url by lazy { intent.getStringExtra(URL) ?: "" }
    private val title by lazy { intent.getStringExtra(TITLE) ?: "" }
    private val isShare by lazy { intent.getBooleanExtra(IS_SHARE, false) }
    private val dialog by lazy {
        ShareDialog(this) {
            ShareUtils.share(
                this,
                it,
                title,
                "",
                if (isShare) "${WEB_BASE_URL}scan/dfxldownload?isApp=1" else url
            )
        }
    }

    var sonicSession: SonicSession? = null
    var sonicSessionClient: SonicSessionClientImpl? = null

    override fun configView() {
        img_share.visibility = if (isShare) View.VISIBLE else View.GONE
        setSupportActionBar(toolbar)
        EventBus.getDefault().register(this)
        web_view.settings.domStorageEnabled = true
        web_view.settings.javaScriptEnabled = true
        web_view.settings.javaScriptCanOpenWindowsAutomatically = true
        web_view.settings.useWideViewPort = true//关键点

        web_view.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        web_view.settings.displayZoomControls = false
        web_view.settings.allowFileAccess = true // 允许访问文件
        web_view.settings.builtInZoomControls = true // 设置显示缩放按钮
        web_view.settings.setSupportZoom(true) // 支持缩放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }//支持http和https混合请求


        web_view.settings.loadWithOverviewMode = true
        web_view.settings.javaScriptCanOpenWindowsAutomatically = true

        web_view.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (web_view != null && web_view.canGoBack()) {
                    web_view.goBack()
                    return@OnKeyListener true
                }
            }
            false
        })

        web_view.registerHandler("skip") { data, function ->
            try {
                var json = JSONObject(data)
                when {
                    json.getString("type") == "1" ->
                        startActivity<BookDetailActivity>(
                            BookDetailActivity.KEY to json.getString("key"),
                            BookDetailActivity.TYPE to "1"
                        ) //图书详情
                    json.getString("type") == "2" -> { //试卷
                        startActivity<ExerciseDetailActivity2>(
                            ExerciseDetailActivity2.PARENT_KEY to json.getString("catalogkey"),
                            ExerciseDetailActivity2.NAME to json.getString("papername"),
                            ExerciseDetailActivity2.KEY to json.getString("paperkey"),
                            ExerciseDetailActivity2.BOOK_KEY to json.getString("bookkey"),
                            ExerciseDetailActivity2.TYPE to ExerciseDetailActivity2.TYPE_QYT
                        )
                    }

                    json.getString("type") == "3" -> { //解析
                        startActivity<ExerciseParsingActivity>(
                            ExerciseParsingActivity.KEY to json.getString("paperkey"),
                            ExerciseParsingActivity.NAME to json.getString("papername")
                        )
                    }
                }
            } catch (e: Exception) {
            }
        }

        web_view.registerHandler("getHeaders") { data, function ->
            function?.onCallBack(Re.getHeard())
        }
        web_view.registerHandler("getToken") { data, function ->
            function?.onCallBack("${SpUtil.user.token},${SpUtil.user.userId},$appId,${SpUtil.userInfo.grade},1")
        }
        web_view.registerHandler("share") { data, function ->
            dialog.show()
        }
        web_view.registerHandler("showDialog") { data, _ ->
            val list = data.split(',')
            startActivity<PostActivity>(
                PostActivity.URL to if (list.size > 1) list[1] else "",
                PostActivity.TITLE to if (list.isNotEmpty()) list[0] else ""
            )
        }

        web_view.registerHandler("pdf") { data, _ ->
            val list = data.split(',')
            startActivity<PDFActivity>(PDFActivity.KEY to list[0], PDFActivity.NAME to list[1])
        }
        web_view.registerHandler("voiceDetail") { data, _ ->
//            startActivity<PostActivity>(URL to data, TITLE to "音标跟读")
            val uri = Uri.parse(data)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        web_view.registerHandler("toFaq") { data, _ ->
            web_view.loadUrl("${WEB_BASE_URL}community/faq/faq/")
        }
        web_view.registerHandler("toMusic") { data, _ ->
            try {
                var position = 0
                val res = mutableListOf<AudioRes>()
                val jsonObject = JSONObject(data)
                presenter.supportingResourceList(jsonObject.getString("catalogKey") ?: "") {
                    res.addAll(it.list)
                    res.forEach {
                        val string = jsonObject.getString("id")
                        if (TextUtils.equals(it.id, string)) {
                            position = res.indexOf(it)
                        }
                    }
//                    MyApp.instance.bookres = res
                    // 存
                    val list1: List<AudioRes> = ArrayList()
                    val gson1 = Gson()
                    val data1 = gson1.toJson(res)
                    SPUtils.getInstance().put("AudioRes", data1)
                    startActivity<AudioActivity>(
                        AudioActivity.DATA to res,
                        AudioActivity.POSITION to position,
                        AudioActivity.TYPE to "pt",
                        AudioActivity.SUPPORT_KEY to (jsonObject.getString("bookKey") ?: ""),
                        AudioActivity.NAME to (jsonObject.getString("bookName") ?: "")
                    )
                }
            } catch (e: Exception) {
            }
        }

        web_view.registerHandler("login") { data, _ ->
            startActivity<LoginActivity>()
        }
        web_view.registerHandler("goBack") { data, _ ->
            onBackPressed()
        }
        web_view.registerHandler("playVideo") { data, _ ->
            startActivity<SparkVideoActivity>(
                SparkVideoActivity.URL to data,
                SparkVideoActivity.TITLE to ""
            )
        }
        web_view.registerHandler("voicePay") { data, _ ->
            //goodKey  支付方式 userid token
            val uri = Uri.parse(data)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        web_view.webChromeClient = object : WebChromeClient() {
            fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                WebCameraHelper.instance.mUploadMessage = uploadMsg
                WebCameraHelper.instance.showOptions(this@PostActivity)
            }

            // For Android > 4.1.1
            fun openFileChooser(
                uploadMsg: ValueCallback<Uri>,
                acceptType: String, capture: String
            ) {
                WebCameraHelper.instance.mUploadMessage = uploadMsg
                WebCameraHelper.instance.showOptions(this@PostActivity)
            }

            // For Android > 5.0支持多张上传
            override fun onShowFileChooser(
                webView: WebView,
                uploadMsg: ValueCallback<Array<Uri>>,
                fileChooserParams: WebChromeClient.FileChooserParams
            ): Boolean {
                WebCameraHelper.instance.mUploadCallbackAboveL = uploadMsg
                WebCameraHelper.instance.showOptions(this@PostActivity)
                return true
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                tv_title.text = title
            }
        }
        web_view.webViewClient = object : BridgeWebViewClient(web_view) {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (sonicSession != null) {
                    sonicSession!!.sessionClient.pageFinish(url)
                }
            }

            @TargetApi(21)
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                return shouldInterceptRequest(view, request?.url.toString())
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                url: String?
            ): WebResourceResponse? {
                if (sonicSession != null) {
                    val requestResponse = sonicSessionClient?.requestResource(url)
                    if (requestResponse is WebResourceResponse) {
                        return requestResponse
                    }
                }
                return null

            }
        }
        toolbar.visibility = if (url.contains("OnlineQA")) View.GONE else View.VISIBLE
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        img_share.setOnClickListener {
            dialog.show()
        }
    }

    override fun initData() {
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(
//                SonicRuntimeImpl(MyApp.instance),
                SonicRuntimeImpl(BaseApp.get()),
                SonicConfig.Builder().build()
            )
        }
        var webUrl = ""
        if (url.startsWith("http://") || url.startsWith("https://")) {
            if (url.contains("?")) {
                webUrl = "${url}&isApp=1"
            } else {
                webUrl = "${url}?isApp=1"
            }
            sonicSession = SonicEngine.getInstance()
                .createSession(webUrl, SonicSessionConfig.Builder().build())
            sonicLoad()
            //                web_view.loadUrl(webUrl)
        } else {
            web_view.loadDataWithBaseURL(null, format(url), "text/html", "utf-8", null)
        }
    }

    private fun sonicLoad() {
        if (null != sonicSession) {
            sonicSessionClient = SonicSessionClientImpl()
            sonicSession!!.bindClient(sonicSessionClient)
        }

        // step 5: webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient!!.bindWebView(web_view)
            sonicSessionClient!!.clientReady()
        } else { // default mode
            if (url.startsWith("http")) {
                web_view?.loadUrl("$url${if (url.contains("?")) "&" else "?"}isApp=1")
            } else {
                web_view?.loadUrl("$WEB_BASE_URL$url${if (url.contains("?")) "&" else "?"}isApp=1")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        WebCameraHelper.instance.onActivityResult(requestCode, resultCode, data)
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun update(msg: EventMsg) {
        if (msg.action == "login")
            initData()
    }

    override fun onDestroy() {
        if (null != sonicSession) {
            sonicSession!!.destroy()
            sonicSession = null
        }
        EventBus.getDefault().unregister(this)
        web_view.loadUrl("about:blank")
        web_view.destroy()
        super.onDestroy()
    }

    companion object {
        const val URL = "url"
        const val TITLE = "title"
        const val IS_SHARE = "IS_SHARE"
    }

    fun format(data: String) = "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">\n" +
            "<title>${title}</title>\n" +
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

    override fun onBackPressed() {
        super.onBackPressed()
    }
}