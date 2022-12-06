package tuoyan.com.xinghuo_dayingindex.ui.bookstore.post
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.ViewManager
import android.webkit.*
import com.geek.libutils.app.BaseApp
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sensorsdata.analytics.android.sdk.SensorsDataIgnoreTrackAppViewScreen
import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicEngine
import com.tencent.sonic.sdk.SonicSession
import com.tencent.sonic.sdk.SonicSessionConfig
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.appId
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.ProductBean
import tuoyan.com.xinghuo_dayingindex.net.Re
import tuoyan.com.xinghuo_dayingindex.sonic.SonicRuntimeImpl
import tuoyan.com.xinghuo_dayingindex.sonic.SonicSessionClientImpl
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.mine.order.PaymentOrderActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.UdeskUtils
import tuoyan.com.xinghuo_dayingindex.utlis.WebCameraHelper
import tuoyan.com.xinghuo_dayingindex.utlis.log.L
import udesk.core.model.Product

/**
 * 创建者：
 * 时间：
 */
@SensorsDataIgnoreTrackAppViewScreen
class PostActivity(override val layoutResId: Int = 0) : LifeActivity<PostPresenter>() {
    override val presenter by lazy { PostPresenter(this) }
    private val url by lazy { intent.getStringExtra(URL) ?: "" }
    private val assembleKey by lazy { intent.getStringExtra(ASSEMBLE_KEY) ?: "" }
    private val lessonKey by lazy { intent.getStringExtra(LESSON_KEY) ?: "" }
    private var bridgeWebView: BridgeWebView? = null
    private var oldLoginState = false


    override fun setContentView() {
        linearLayout {
            lparams(matchParent, matchParent)
            bridgeWebView = bridgeWebView {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.databaseEnabled = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.blockNetworkImage = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
//                settings.allowContentAccess = true
//                settings.setAppCacheEnabled(true)
//                settings.savePassword = false
//                settings.saveFormData = false
//                settings.useWideViewPort = true
//                settings.loadWithOverviewMode = true

                registerHandler("getToken") { _, function ->
                    function?.onCallBack(
                        "${SpUtil.user.token ?: ""},${
                            SpUtil.user.userId ?: ""
                        },$appId,${SpUtil.userInfo.grade ?: ""}"
                    )
                    function?.let {
                        Log.e(
                            "BRIDGE-TOKEN", "${SpUtil.user.token ?: ""},${
                                SpUtil.user.userId ?: ""
                            },$appId,${SpUtil.userInfo.grade ?: ""}"
                        )
                    }
                }


                registerHandler("toShare") { data, _ ->
                    // TODO: 2018/10/29 15:36  分享
                    val ss: Map<String, String> = Gson().fromJson(data, object : TypeToken<Map<String, String>>() {}.type)
                    ShareDialog(this@PostActivity) { media ->
                        val url = ss["url"] ?: ""
                        if (url.contains("preferential")) {//在当前目录下的 preferential活动需要添加积分
                            ShareUtils.setCallback(object : ShareUtils.Callback {
                                override fun onSure() {
                                    addIntegral("活动")
                                }
                            })
                        }
                        ShareUtils.share(this@PostActivity, media, ss["name"] ?: "", "", url)
                    }.show()
                }
                registerHandler("toPay") { data, _ ->
                    val map: Map<String, String> = Gson().fromJson(data, object : TypeToken<Map<String, String>>() {}.type)
                    startActivity<PaymentOrderActivity>(
                        PaymentOrderActivity.KEY to map["key"],
                        PaymentOrderActivity.TITLE to map["title"],
                        PaymentOrderActivity.PRICE to map["price"],
                        PaymentOrderActivity.MAIN_ORDER_NUMBER to map["mainOrderNumber"],
                        PaymentOrderActivity.DP_ORDER_NUMBER to map["dpOrderNumber"],
                        PaymentOrderActivity.ASSEMBLE_KEY to assembleKey,
                        PaymentOrderActivity.LESSON_KEY to lessonKey
                    )
                }
                registerHandler("outLine") { url, _ ->
                    // : 2018/10/29 16:01  外部链接
                    startActivity<WebViewActivity>(WebViewActivity.URL to url)
                    // : 2018/10/29 15:45
                }
                registerHandler("changeUrl") { path, _ ->
                    startActivity<PostActivity>(URL to path)
                }
                registerHandler("toNetwork") { key, _ ->
                    startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to key)
                    // : 2018/10/29 15:45
                }
                registerHandler("toUdesk") { data, _ ->
                    isLogin {
                        try {
                            val product = Gson().fromJson(data, ProductBean::class.java)
                            val temp = Product()
                            temp.name = product.name
                            temp.imgUrl = product.imgUrl
                            temp.url = product.url
                            temp.params = product.params
                            UdeskUtils.openChatView(this@PostActivity, temp)
                        } catch (e: Exception) {
                            UdeskUtils.openChatView(this@PostActivity)
                        }
                    }
                }
                registerHandler("goBack") { _, _ ->
                    // : 2018/10/29 15:45  回主页
//                    startActivity<MainActivity>()
                    onBackPressed()
                }
                registerHandler("toLogin") { _, _ ->
//                    startActivity<LoginActivity>()
                    SpUtil.isLogin = false
                    refreshFlag = true
                    isLogin { }
                }

                registerHandler("orderConfirm") { _, _ ->
                    toast("该课程已达购买上限")
                    onBackPressed()
                }
                registerHandler("getHeaders") { _, function ->
                    function?.onCallBack(Re.getHeard())
                }
                registerHandler("deleteUser") { _, function ->
                    setResult(RESULT_OK)
                    onBackPressed()
                }
                webChromeClient = object : WebChromeClient() {
                    // For Android < 3.0
                    fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                        WebCameraHelper.instance.mUploadMessage = uploadMsg
                        WebCameraHelper.instance.showOptions(this@PostActivity)
                    }

                    // For Android > 4.1.1
                    fun openFileChooser(
                        uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String
                    ) {
                        WebCameraHelper.instance.mUploadMessage = uploadMsg
                        WebCameraHelper.instance.showOptions(this@PostActivity)
                    }

                    // For Android > 5.0支持多张上传
                    override fun onShowFileChooser(
                        webView: WebView, uploadMsg: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams
                    ): Boolean {
                        WebCameraHelper.instance.mUploadCallbackAboveL = uploadMsg
                        WebCameraHelper.instance.showOptions(this@PostActivity)
                        return true
                    }
                }
                webViewClient = object : BridgeWebViewClient(this) {
                    override fun onReceivedError(
                        view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        // TODO: 2018/10/29 16:01  暂不处理
//                        visibility = View.GONE
//                        toast("error")
                        L.d("DB需要的日志: " + error.toString() + Gson().toJson(error))
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        // TODO: 2018/10/29 16:01  暂不处理
//                        visibility = View.VISIBLE
                        L.d("DB需要的日志:+1 " + url.toString())
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (sonicSession != null) {
                            sonicSession!!.sessionClient.pageFinish(url)
                        }
                    }

                    @TargetApi(21)
                    override fun shouldInterceptRequest(
                        view: WebView?, request: WebResourceRequest?
                    ): WebResourceResponse? {
                        return shouldInterceptRequest(view, request?.url.toString())
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?, url: String?
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
            }.lparams(matchParent, matchParent)
        }
    }

    var sonicSession: SonicSession? = null
    var sonicSessionClient: SonicSessionClientImpl? = null

    override fun initData() {
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(
                SonicRuntimeImpl(BaseApp.get()), SonicConfig.Builder().build()
            )
        }
        val temUrl = if (url.startsWith("http")) "$url${if (url.contains("?")) "&" else "?"}isApp=1" else "$WEB_BASE_URL$url${if (url.contains("?")) "&" else "?"}isApp=1"
        sonicSession = SonicEngine.getInstance().createSession(temUrl, SonicSessionConfig.Builder().build())
        sonicLoad()
    }

    override fun refresh() {
        sonicLoad()
    }

    /**
     * 使用VasSonic 加载url
     */
    private fun sonicLoad() {
        if (null != sonicSession) {
            sonicSessionClient = SonicSessionClientImpl()
            sonicSession!!.bindClient(sonicSessionClient)
        }

        // step 5: webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient!!.bindWebView(bridgeWebView)
            sonicSessionClient!!.clientReady()
        } else { // default mode
            if (url.startsWith("http")) {
                bridgeWebView?.loadUrl("$url${if (url.contains("?")) "&" else "?"}isApp=1")
            } else {
                bridgeWebView?.loadUrl("$WEB_BASE_URL$url${if (url.contains("?")) "&" else "?"}isApp=1")
            }
        }
    }

    var refreshFlag = false
    override fun onResume() {
        super.onResume()
        if (refreshFlag && SpUtil.isLogin) {
            bridgeWebView?.reload()
        }
        refreshFlag = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && bridgeWebView?.canGoBack() == true) {
            bridgeWebView?.goBack()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        WebCameraHelper.instance.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        if (null != sonicSession) {
            sonicSession!!.destroy()
            sonicSession = null
        }
        bridgeWebView = null
        super.onDestroy()
    }

    companion object {
        const val URL = "url"
        const val ASSEMBLE_KEY = "assembleKey"
        const val LESSON_KEY = "lessonKey"
    }
}

inline fun ViewManager.bridgeWebView(): BridgeWebView = bridgeWebView() {}
inline fun ViewManager.bridgeWebView(init: (BridgeWebView).() -> Unit): BridgeWebView {
    return ankoView({ BridgeWebView(it) }, theme = 0) { init() }
}
