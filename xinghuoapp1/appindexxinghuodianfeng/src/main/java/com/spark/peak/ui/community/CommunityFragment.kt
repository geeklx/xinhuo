package com.spark.peak.ui.community

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.google.android.material.tabs.TabLayout
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.appId
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.EventMsg
import com.spark.peak.base.LifeFragment
import com.spark.peak.ui._public.NetErrorFragment
import com.spark.peak.ui.common.login.LoginActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.message.MessageNoticeActivity
import com.spark.peak.utlis.SpUtil
import com.spark.peak.utlis.WebCameraHelper
import kotlinx.android.synthetic.main.fragment_community.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.startActivity


/**
 * Created by 李昊 on 2018/3/30.
 */
class CommunityFragment : LifeFragment<BasePresenter>() {
    override val presenter: BasePresenter = BasePresenter(this)
    override val layoutResId: Int = R.layout.fragment_community
    override fun configView(view: View?) {
        EventBus.getDefault().register(this)
        web_view.settings.javaScriptEnabled = true
        web_view.settings.domStorageEnabled = true
        web_view.settings.javaScriptCanOpenWindowsAutomatically = true
        web_view.registerHandler("getToken") { data, function ->
            function?.onCallBack("${SpUtil.user.token},${SpUtil.user.userId},$appId,${SpUtil.userInfo.grade}")
        }
        web_view.registerHandler("showDialog") { data, _ ->
            val list = data.split(',')
            startActivity<PostActivity>(PostActivity.URL to if (list.size > 1) list[1] else "", PostActivity.TITLE to if (list.isNotEmpty()) list[0] else "")
        }

        web_view.registerHandler("toFaq") { data, _ ->
            hasInfo = false
            web_view.loadUrl("${WEB_BASE_URL}community/faq/faq/")
        }

        web_view.registerHandler("login") { data, _ ->
            startActivity<LoginActivity>()
        }
        web_view.webChromeClient = object : WebChromeClient() {

            // For Android < 3.0
            fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                WebCameraHelper.instance.mUploadMessage = uploadMsg
                WebCameraHelper.instance.showOptions(this@CommunityFragment)
            }

            // For Android > 4.1.1
            fun openFileChooser(uploadMsg: ValueCallback<Uri>,
                                acceptType: String, capture: String) {
                WebCameraHelper.instance.mUploadMessage = uploadMsg
                WebCameraHelper.instance.showOptions(this@CommunityFragment)
            }

            // For Android > 5.0支持多张上传
            override fun onShowFileChooser(webView: WebView,
                                           uploadMsg: ValueCallback<Array<Uri>>,
                                           fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
                WebCameraHelper.instance.mUploadCallbackAboveL = uploadMsg
                WebCameraHelper.instance.showOptions(this@CommunityFragment)
                return true
            }
        }
        web_view.webViewClient = object : BridgeWebViewClient(web_view) {

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                web_view?.visibility = View.GONE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                web_view?.visibility = View.VISIBLE
            }
        }
        web_view.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (web_view != null && web_view.canGoBack()) {
                    web_view.goBack()
                    return@OnKeyListener true
                }
            }
            false
        })
    }

    override fun handleEvent() {
//        tv_ask.setOnClickListener { startActivity<PostActivity>() }
        iv_message.setOnClickListener { startActivity<MessageNoticeActivity>() }
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab?.position == 0)
                    web_view.loadUrl("${WEB_BASE_URL}community/faq/faq/")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0)
//                    web_view.loadUrl("http://10.100.10.2:8080/community/faq/faq/")
                    web_view.loadUrl("${WEB_BASE_URL}community/faq/faq/")
                else
//                    web_view.loadUrl("http://10.100.10.2:8080/community/circle/circle/")
                    web_view.loadUrl("${WEB_BASE_URL}community/circle/circle/")
            }

        })
        val fragment = fragment_error as NetErrorFragment
        fragment.listener = object : NetErrorFragment.Listener {
            override fun reTry() {
//                tab_layout.getTabAt(0)?.select()
                web_view.url?.let { web_view.loadUrl(it) }

            }

        }

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun update(msg: EventMsg) {
        if (msg.action == "login")
            tab_layout.getTabAt(0)?.select()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        WebCameraHelper.instance.onActivityResult(requestCode, resultCode, data)
    }

    override fun initData() {
//        web_view.loadUrl("http://10.100.10.2:8080/community/faq/faq/")
        if (hasInfo) {
            web_view.loadUrl("${WEB_BASE_URL}community/faq/faq?ask=1")
        } else {
            web_view.loadUrl("${WEB_BASE_URL}community/faq/faq/")
        }
    }

    var hasInfo = false
    fun initDataWithInfo() {
        if (web_view == null) {
            hasInfo = true
        } else {
            web_view.loadUrl("${WEB_BASE_URL}community/faq/faq?ask=1")
        }
    }

    override fun onResume() {
        super.onResume()
        upmsg()
    }

    fun upmsg() {
        try {
            iv_msg_red_dot?.visibility = View.GONE
            if (!SpUtil.isLogin) return
            presenter.ifNewFB {
                if (it == 1)
                    iv_msg_red_dot?.visibility = View.VISIBLE
            }
            presenter.ifNewNotice {
                if (it == 1)
                    iv_msg_red_dot?.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    fun showNetError() {
        runOnUiThread {
            web_view.visibility = View.GONE
        }
    }
}