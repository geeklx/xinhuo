package tuoyan.com.xinghuo_dayingindex.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.bumptech.glide.Glide
import com.geek.libutils.data.MmkvUtils
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.analytics.android.sdk.SensorsDataIgnoreTrackAppViewScreen
import kotlinx.android.synthetic.main.activity_start.*
import okhttp3.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.Advert
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.AgreementDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.HomePresenter
import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.spoken.SpokenListActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.UuidUtil
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
import java.io.IOException

@SensorsDataIgnoreTrackAppViewScreen
class StartActivity : LifeActivity<HomePresenter>() {
    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_start

    private var advert: Advert? = null
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var runnable: Runnable? = null
    private var dialog: AgreementDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getUuid()
        ImmersionBar.with(this).fullScreen(true).statusBarColor(R.color.transparent)
            .statusBarDarkFont(true).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init()
        fullScreen = true
        super.onCreate(savedInstanceState)
    }

    override fun configView() {
        super.configView()
    }

    private fun agreeDialog() {
        if (!SpUtil.agreement) {
            if (dialog == null) {
                dialog = AgreementDialog(this, { type ->
                    if (type == 1) {
                        //1用户服务协议
                        startActivity<WebViewActivity>(
                            WebViewActivity.URL to WEB_BASE_URL + "login/agreement?isApp=1",
                            WebViewActivity.TITLE to "用户服务协议"
                        )
                    } else {
                        //2隐私政策
                        startActivity<WebViewActivity>(
                            WebViewActivity.URL to WEB_BASE_URL + "login/privacy?isApp=1",
                            WebViewActivity.TITLE to "隐私政策"
                        )
                    }
                }) {
                    getIpStr()
                    getBanner()
                    SpUtil.agreement = true
                    initGoBtn()
//                    MyApp.instance.initApp()//此写法无法解耦换了

                }
                dialog?.show()
            } else if (!dialog!!.isShowing) {
                dialog?.show()
            }
        } else {
            getIpStr()
            getBanner()
            countDown()
        }
    }

    private fun countDown() {
        runnable = Runnable {
            goOn(null)
        }
        handler.postDelayed(runnable!!, 2000)
    }

    private fun getBanner() {
        presenter.getBanner("spgg", 1, 1) {
            if (it.isNotEmpty()) {
                try {
                    initGoBtn()
                    advert = it[0]
                    Glide.with(this).load(advert!!.alternateImg).into(iv_advert)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        iv_advert.setOnClickListener {
            advert?.let { item ->
                presenter.advertisingPv(item.key)
                if (item.type == "link") {
                    if (item.link != "") {
                        saBanner(item, "外链")
                        startActivity<WebViewActivity>(
                            WebViewActivity.URL to item.link, WebViewActivity.TITLE to item.title
                        )
                    }
                } else if (item.goodtype == "net" && !item.goodkey.isNullOrEmpty()) {
                    saBanner(item, "网课")
                    startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to item.goodkey)
                } else if (item.goodtype == "book" && !item.goodkey.isNullOrEmpty()) {
                    saBanner(item, "图书")
                    startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${item.goodkey}")
                } else if ("app" == item.goodtype && !item.key.isNullOrEmpty()) {
                    saBanner(item, "小程序")
                    WxMini.goWxMini(this, item.key)
                } else if ("smartBook" == item.goodtype) {
                    saBanner(item, "智能书")
                    startActivity<EBookListActivity>()
                } else if ("netSpecial" == item.goodtype) {
                    saBanner(item, "网课专题")
                    startActivity<LessonListActivity>(
                        LessonListActivity.KEY to item.goodkey,
                        LessonListActivity.TITLE to item.title
                    )
                } else if ("spoken" == item.goodtype) {
                    saBanner(item, "口语模考")
                    startActivity<SpokenListActivity>(SpokenListActivity.TITLE to item.title)
                }
                clicked = true
            }
        }
    }

    var clicked = false
    override fun onResume() {
        super.onResume()
        if (clicked) {
            goOn(null)
        } else {
            agreeDialog()
        }
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
        val channal = com.umeng.commonsdk.utils.UMUtils.getChannelByXML(this)
        MmkvUtils.getInstance().set_common("channal", channal)
//        MyApp.instance.channal = channal
        runnable?.let { handler.removeCallbacks(it) }
        if (SpUtil.first) {
            presenter.postFirstInstall()
            SpUtil.first = false
            startActivity<MainActivity>()
        } else {
            startActivity<MainActivity>()
        }
        finish()
    }

    /**
     *获取本机ip地址等信息，在请求header中添加 Re.kt中
     */
    fun getIpStr() {
        val url = "http://pv.sohu.com/cityjson?ie=utf-8"
        val httpClient = OkHttpClient()
        val request = Request.Builder().url(url).get().build()
        val call = httpClient.newCall(request);
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                SpUtil.ipStr = response.body()?.string().toString()
                var aaa = ""
            }
        })
    }

    /**
     * 用户唯一标识，读取app缓存和本地缓存是否相同，以app缓存为主更新本地缓存
     */

    fun getUuid() {
        Thread {
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
                if (SpUtil.uuid.isNullOrEmpty()) {
                    SpUtil.uuid = UuidUtil.getUuid()
                }
            }
        }.start()
    }

    fun saBanner(item: Advert, type: String) {
        try {
            val property = JSONObject()
            property.put("advertisement_id", item.key)
            property.put("advertisement_name", item.title)
            property.put("location_of_advertisement", "闪屏广告")
            property.put("advertising_sequence", "1号")
            property.put("types_of_advertisement", type)
            SensorsDataAPI.sharedInstance().track("click_advertisement", property)
        } catch (e: Exception) {
        }
    }
}
