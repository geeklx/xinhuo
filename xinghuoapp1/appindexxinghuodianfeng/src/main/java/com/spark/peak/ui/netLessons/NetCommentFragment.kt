package com.spark.peak.ui.netLessons

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewManager
import android.webkit.*
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.appId
import com.spark.peak.base.LifeFragment
import com.spark.peak.ui.common.login.LoginActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.utlis.SpUtil
import com.spark.peak.utlis.WebCameraHelper
import kotlinx.android.synthetic.main.activity_image.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.support.v4.UI
import kotlin.properties.Delegates

/**
 * 创建者：
 * 时间：
 */
class NetCommentFragment : LifeFragment<NetLessonsPresenter>() {
    override val presenter = NetLessonsPresenter(this)
    override val layoutResId = 0
    private var webView by Delegates.notNull<BridgeWebView>()
    private val key by lazy { arguments?.getString(KEY) ?: "" }

    override fun initView() = UI {
        frameLayout {
            webView = mWebView {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                registerHandler("getToken") { data, function ->
                    function?.onCallBack("${SpUtil.user.token},${SpUtil.user.userId},$appId,${SpUtil.userInfo.grade}")
                }
                registerHandler("showDialog") { data, _ ->
                    val list = data.split(',')
                    startActivity<PostActivity>(PostActivity.URL to if (list.size > 1) list[1] else "",
                            PostActivity.TITLE to if (list.isNotEmpty()) list[0] else "")
                }
                registerHandler("login") { data, _ ->
                    startActivity<LoginActivity>()
                }
                webChromeClient = object : WebChromeClient() {
                    // For Android < 3.0
                    fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                        WebCameraHelper.instance.mUploadMessage = uploadMsg
                        WebCameraHelper.instance.showOptions(this@NetCommentFragment)
                    }

                    // For Android > 4.1.1
                    fun openFileChooser(uploadMsg: ValueCallback<Uri>,
                                        acceptType: String, capture: String) {
                        WebCameraHelper.instance.mUploadMessage = uploadMsg
                        WebCameraHelper.instance.showOptions(this@NetCommentFragment)
                    }

                    // For Android > 5.0支持多张上传
                    override fun onShowFileChooser(webView: WebView,
                                                   uploadMsg: ValueCallback<Array<Uri>>,
                                                   fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
                        WebCameraHelper.instance.mUploadCallbackAboveL = uploadMsg
                        WebCameraHelper.instance.showOptions(this@NetCommentFragment)
                        return true
                    }
                }
                webViewClient = object : BridgeWebViewClient(this) {
                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        super.onReceivedError(view, request, error)
                        visibility = View.GONE
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        visibility = View.VISIBLE
                    }
                }
                setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (canGoBack()) {
                            goBack()
                            return@OnKeyListener true
                        }
                    }
                    false
                })
            }.lparams(matchParent, matchParent)
        }

    }.view

    @SuppressLint("SetTextI18n")
    override fun initData() {
        webView.loadUrl("${WEB_BASE_URL}network/netCom?key=$key")
    }


    companion object {
        fun instance(key: String) = NetCommentFragment().apply {
            arguments = Bundle().apply {
                putString(KEY, key)
            }
        }

        const val KEY = "key"
    }

}

inline fun ViewManager.mWebView(): BridgeWebView = mWebView() {}
inline fun ViewManager.mWebView(init: (BridgeWebView).() -> Unit): BridgeWebView {
    return ankoView({ BridgeWebView(it) }, theme = 0) { init() }
}