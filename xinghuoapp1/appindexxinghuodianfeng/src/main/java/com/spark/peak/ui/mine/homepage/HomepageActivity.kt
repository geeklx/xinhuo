package com.spark.peak.ui.mine.homepage

import android.graphics.Bitmap
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.spark.peak.R
import com.spark.peak.appId
import com.spark.peak.base.EventMsg
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui._public.NetErrorFragment
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.dialog.ShareDialog
import com.spark.peak.ui.mine.user.UserActivity
import com.spark.peak.utlis.ShareUtils
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_homepage.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

/**
 * 创建者：
 * 时间：
 */
class HomepageActivity(override val layoutResId: Int = R.layout.activity_homepage)
    : LifeActivity<HomepagePresenter>() {
    override val presenter by lazy { HomepagePresenter(this) }
    private val url by lazy { intent.getStringExtra(URL) ?:""}
    private val dialog by lazy {
        ShareDialog(this) {
            ShareUtils.share(this, it, "个人主页", "",
                    url)
        }
    }

    override fun configView() {
        setSupportActionBar(toolbar)
        EventBus.getDefault().register(this)
        web_view.settings.javaScriptEnabled = true
        web_view.settings.domStorageEnabled = true
        web_view.settings.javaScriptCanOpenWindowsAutomatically = true
        web_view.webChromeClient = WebChromeClient()
        web_view.registerHandler("getToken") { data, function ->
            //            function?.onCallBack("${SpUtil.user.userId}")
            function?.onCallBack("${SpUtil.user.token},${SpUtil.user.userId},$appId")
        }
        web_view.registerHandler("showDialog") { data, _ ->
            val split = data.split(',')
            if (split.isNotEmpty()) {
                if (split[0].contains("编辑资料")) {
                    startActivity<UserActivity>()
                } else {
                    startActivity<PostActivity>(PostActivity.URL to if (split.size > 1) split[1] else "",
                            PostActivity.TITLE to if (split.isNotEmpty()) split[0] else "")
                }

            }
        }
        web_view.webViewClient = object : BridgeWebViewClient(web_view) {

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                web_view.visibility = View.GONE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                web_view.visibility = View.VISIBLE
            }
        }
        val netErrorFragment by lazy { NetErrorFragment() }
        netErrorFragment.listener = object : NetErrorFragment.Listener{
            override fun reTry() {
                initData()
            }
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.post_error, netErrorFragment).commit()

    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        web_view.loadUrl(url)
    }
//    override fun statusColor() {
//        ImmersionBar.with(this)
//                .transparentStatusBar()
////                .fitsSystemWindows(true)
////                .statusBarDarkFont(true)
////                .statusBarColor(R.color.color_1482ff)
//                .init()
//    }哭器

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_share, menu)
//        val item = menu?.getItem(0)
//        item?.setIcon(R.drawable.ic_share)
//        item?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_share -> {
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun update(msg: EventMsg) {
        if (msg.action == "home") initData()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    companion object {
        const val URL = "url"
    }
}