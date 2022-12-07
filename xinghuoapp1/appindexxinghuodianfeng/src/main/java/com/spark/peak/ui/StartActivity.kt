package com.spark.peak.ui

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.bumptech.glide.Glide
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.Advert
import com.spark.peak.bean.LoginResponse
import com.spark.peak.bean.UserInfo
import com.spark.peak.ui._public.WebViewActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.dialog.AgreementDialog
import com.spark.peak.ui.home.HomePresenter
import com.spark.peak.ui.netLessons.NetLessonsActivity
import com.spark.peak.utlis.SpUtil
import com.spark.peak.utlis.UuidUtil
import kotlinx.android.synthetic.main.activity_startdf.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class StartActivity : LifeActivity<HomePresenter>() {
    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_startdf

    private var advert: Advert? = null
    private val handler by lazy { Handler() }
    private var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getUuid()
        ImmersionBar.with(this)
            .fullScreen(true)
            .hideBar(BarHide.FLAG_HIDE_BAR)
            .statusBarColor(android.R.color.transparent)
            .statusBarDarkFont(true)
            .init()
        fullScreen = true
        super.onCreate(savedInstanceState)
        if (!SpUtil.isLogin) {
            SpUtil.userInfo = UserInfo()
            SpUtil.user = LoginResponse()
        }
    }

    private fun agreeDialog() {
        if (!SpUtil.agreement) {
            AgreementDialog(this, { type ->
                if (type == 1) {
                    //1用户服务协议
                    startActivity<PostActivity>(
                        PostActivity.TITLE to "用户服务协议",
                        PostActivity.URL to "${WEB_BASE_URL}login/agreement?isApp=1"
                    )
                } else {
                    //2隐私政策
                    startActivity<PostActivity>(
                        PostActivity.TITLE to "隐私政策",
                        PostActivity.URL to "${WEB_BASE_URL}login/privacy?isApp=1"
                    )
                }
            }) {
                SpUtil.agreement = true
                initGoBtn()
//                MyApp.instance.initApp()
            }.show()
        } else {
            countDown()
        }
    }

    private fun countDown() {
        runnable = Runnable {
            goOn(null)
        }
        handler.postDelayed(runnable!!, 2000)
    }

    override fun initData() {
        presenter.getBanner("spgg", 1, 1) {
            if (it.isNotEmpty()) {
                initGoBtn()
                advert = it[0]
                Glide.with(ctx).load(advert!!.img).into(iv_advert)
            }
        }

        //先无脑调用一次安装记录
//        presenter.postFirstInstall()
    }

    override fun handleEvent() {
        super.handleEvent()
        iv_advert.setOnClickListener {
            advert?.let {
                if (it.type == "link") {
                    if (it.link != "") {
                        startActivity<WebViewActivity>(
                            WebViewActivity.URL to it.link,
                            WebViewActivity.TITLE to it.title
                        )
                    }
                } else if (it.type == "goods") {
                    if (it.goodtype == "net") {
                        startActivity<NetLessonsActivity>(NetLessonsActivity.KEY to it.goodkey)
                    } else if (it.goodtype == "book") {
                        //跳转点读
                    }
                }

                presenter.addAdPv(it.key) {

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        agreeDialog()
    }

    override fun onPause() {
        super.onPause()
        runnable?.let { handler.removeCallbacks(it) }
    }

    /**
     * 初始化跳过按钮
     * 倒计时
     */
    var aLong = 3

    private fun initGoBtn() {
        if (SpUtil.agreement) {
            //同意当前协议
            runnable?.let { handler.removeCallbacks(it) }
            runnable = Runnable {
                tv_go_on.text = "跳过 " + (--aLong)
                if (aLong != 0) {
                    initGoBtn()
                } else {
                    goOn(null)
                }
            }
            tv_go_on.visibility = View.VISIBLE
            handler.postDelayed(runnable!!, 1000)
        }
    }

    fun goOn(view: View?) {
        runnable?.let { handler.removeCallbacks(it) }
        if (!SpUtil.noFirst) { //
//            startActivity<GuideActivity>()
            presenter.postFirstInstall()
        } else {
        }
        startActivity<MainActivity>()
        finish()
    }

    /**
     * 用户唯一标识，读取app缓存和本地缓存是否相同，以app缓存为主更新本地缓存
     */

    fun getUuid() {
        Thread(object : Runnable {
            override fun run() {
                try {
                    var localUuid = UuidUtil.readUuid(this@StartActivity)
                    var appUuid = SpUtil.uuid
                    if (appUuid.isNullOrEmpty()) {
                        if (localUuid.isNullOrEmpty()) {
                            appUuid = UuidUtil.getUuid()
                            localUuid = appUuid
                            SpUtil.uuid = appUuid
                            UuidUtil.saveUuid(localUuid, this@StartActivity)
                        } else {
                            appUuid = localUuid
                            SpUtil.uuid = appUuid
                        }
                    } else {
                        if (appUuid != localUuid) {
                            localUuid = appUuid
                            UuidUtil.saveUuid(localUuid, this@StartActivity)
                        }
                    }
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }).start()
    }
}
