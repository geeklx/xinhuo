package tuoyan.com.xinghuo_dayingindex.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.bokecc.sdk.mobile.live.DWLive
import com.bokecc.sdk.mobile.live.DWLiveLoginListener
import com.bokecc.sdk.mobile.live.Exception.DWLiveException
import com.bokecc.sdk.mobile.live.pojo.*
import com.google.android.material.appbar.AppBarLayout
import com.gyf.immersionbar.ImmersionBar
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.analytics.android.sdk.SensorsDataFragmentTitle
import kotlinx.android.synthetic.main.fragment_book_store.*
import kotlinx.android.synthetic.main.fragment_book_store.drag_float
import kotlinx.android.synthetic.main.fragment_book_store.top
import kotlinx.android.synthetic.main.fragment_study_main.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.startActivity
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.Advert
import tuoyan.com.xinghuo_dayingindex.bean.Level
import tuoyan.com.xinghuo_dayingindex.bean.LiveFlag
import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.GoodsLiveActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.PopDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
import tuoyan.com.xinghuo_dayingindex.widegt.onLoadMoreListener

@SensorsDataFragmentTitle(title = "书城页")
class BookStoreFragment : LifeV4Fragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_book_store
    private var isFirstShow = false;
    var gradekey = ""
    var contentType = ""
    var page = 1
    val step = 10
    var sortingmode = "1"
    var priceClick = 0

    val bookAdapter by lazy {
        BookAdapter() {
            startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${it.key}")
        }
    }
    private lateinit var lav_play: LottieAnimationView
    private lateinit var liveFlag: LiveFlag
    private var checkLogin=false;
    override fun configView(view: View?) {
        initStatusBar()
        super.configView(view)
        rlv_books.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_books.adapter = bookAdapter

        lav_play = drag_float.findViewById<LottieAnimationView>(R.id.lav_play)
        drag_float.setOnClickListener({})
        drag_float.findViewById<View>(R.id.live_goods).setOnClickListener({
            checkLogin=false;
          isLogin {  //点击进入到直播带货
              checkLogin=true
              liveFlag?.let {
                  goLiveGoodsLiving(liveFlag)
              } }


        })
        drag_float.findViewById<View>(R.id.iv_close).setOnClickListener({
            if (drag_float.visibility==View.VISIBLE) {
                drag_float.visibility=View.GONE
            }
        })
    }
    override fun onResume() {
        super.onResume()
        if ((!checkLogin)&&SpUtil.isLogin) {
            getLiveFlag()
        }
        if (!isFirstShow) {
           context?.let { it1 -> getDialog("4", it1)  }
            isFirstShow=true
        }
    }
    override fun handleEvent() {
        super.handleEvent()
        srfl.setOnRefreshListener {
            getData()
            getBookBanner()
            getLiveFlag()
        }
        abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            srfl.isEnabled = verticalOffset >= 0
        })
        rlv_books.addOnScrollListener(object : onLoadMoreListener() {
            override fun onLoading(countItem: Int, lastItem: Int) {
                loadMore()
            }
        })
        rg_book.setOnCheckedChangeListener { group, checkedId ->
            when {
                checkedId == R.id.rb_one -> {
                    priceClick = 0
                    sortingmode = "1"
                    rb_three.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.mipmap.icon_price_normal,
                        0
                    )
                    getData()
                }
                checkedId == R.id.rb_two -> {
                    priceClick = 0
                    sortingmode = "2"
                    rb_three.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.mipmap.icon_price_normal,
                        0
                    )
                    getData()
                }
            }
        }

        rb_three.setOnClickListener {
            priceClick++
            if (priceClick % 2 == 0) {
                //降序
                rb_three.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_price_down, 0)
                sortingmode = "4"
            } else {
                sortingmode = "3"
                rb_three.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_price_up, 0)
            }
            getData()
        }
        img_good.setOnClickListener {
            isLogin {
                startActivity<PostActivity>(PostActivity.URL to "trolley?tro=0")
            }
        }
        tv_search.setOnClickListener {
            startActivity<PostActivity>(PostActivity.URL to "booksearch?bs=0")
        }

        tv_sel_grade.setOnClickListener {
            (activity as MainActivity).showBookStoreGrade()
        }
    }

    private fun getBookBanner() {
        presenter.getBookBanner { dataList ->
            banner_books.setImagesUrl(dataList)
            banner_books.setOnItemClickListener {
                var item = dataList[it]
                presenter.advertisingPv(item.key)
                if (item.type == "link" && item.link != "") {
                    saBanner(item, it, "外链")
                    startActivity<WebViewActivity>(
                        WebViewActivity.URL to item.link,
                        WebViewActivity.TITLE to item.title
                    )
                } else if (item.goodtype == "net" && item.goodkey != "") {
                    saBanner(item, it, "网课")
                    startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to item.goodkey)
                } else if (item.goodtype == "book" && item.goodkey != "") {
                    saBanner(item, it, "图书")
                    startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${item.goodkey}")
                } else if ("app" == item.goodtype && !item.key.isNullOrEmpty()) {
                    saBanner(item, it, "小程序")
                    WxMini.goWxMini(this.requireActivity(), item.key)
                } else if ("smartBook" == item.goodtype) {
                    saBanner(item, it, "点读书")
                    startActivity<EBookListActivity>()
                } else if ("netSpecial" == item.goodtype) {
                    saBanner(item, it, "网课专题")
                    startActivity<LessonListActivity>(LessonListActivity.KEY to item.goodkey, LessonListActivity.TITLE to item.title)
                }
            }
        }
    }
    fun getDialog(key :String,context: Context){
        presenter.getDialog(key) { list ->
            if (list.isNotEmpty()) {
                val advert = list[0]
                PopDialog(context, advert.img, { dialog ->
                    dialog.dismiss()
                    presenter.advertisingPv(advert.key)
                    if (advert.type == "link") {
                        if (advert.link != "") {
                            saBanner(advert, 0,"书城广告外链")
                            startActivity<WebViewActivity>(
                                WebViewActivity.URL to advert.link,
                                WebViewActivity.TITLE to advert.title
                            )
                        }
                    } else if (advert.goodtype == "net" && !advert.goodkey.isNullOrEmpty()) {
                        saBanner(advert, 0,"书城广告网课")
                        startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to advert.goodkey)
                    } else if (advert.goodtype == "book" && !advert.goodkey.isNullOrEmpty()) {
                        //跳转点读
                        saBanner(advert, 0,"书城广告图书")
                        startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${advert.goodkey}")
                    } else if ("app" == advert.goodtype && !advert.key.isNullOrEmpty()) {
                        saBanner(advert, 0,"书城广告小程序")
                        WxMini.goWxMini(context, advert.key)
                    } else if ("smartBook" == advert.goodtype) {
                        saBanner(advert, 0, "书城广告智能书")
                        startActivity<EBookListActivity>()
                    } else if ("netSpecial" == advert.goodtype) {
                        saBanner(advert, 0, "书城广告网课专题")
                        startActivity<LessonListActivity>(LessonListActivity.KEY to advert.goodkey, LessonListActivity.TITLE to advert.title)
                    }
                }) {

                }.show()
            }
        }
    }
    override fun initData() {
        super.initData()
        getData()
        getBookBanner()
        getLiveFlag()
    }

    private fun getLiveFlag() {
        presenter.getLiveFlag("h2", {
            liveFlag = it
            if (it.isShow == 0) {
                drag_float.visibility = View.GONE
                lav_play.cancelAnimation()
                lav_play.progress = 0f
            } else {
                drag_float.visibility = View.VISIBLE
                lav_play.playAnimation()
            }
        })
    }

    fun selByGrade(gradeBean: Level, content: Level) {
        if (gradeBean.isAdd) {
            gradekey = gradeBean.key
            if (content.isAdd) {
                contentType = content.key
            } else {
                contentType = ""
            }
            tv_sel_grade.isSelected = true
        } else {
            tv_sel_grade.isSelected = false
            gradekey = ""
            contentType = ""
        }
        getData()
    }

    fun getData() {
        page = 1
        presenter.getBookList("", sortingmode, gradekey, contentType, page, step) {
            srfl.isRefreshing = false
            bookAdapter.setData(it)
        }
    }

    fun loadMore() {
        page++
        presenter.getBookList("", sortingmode, gradekey, contentType, page, step) {
            srfl.isRefreshing = false
            bookAdapter.addData(it)
        }
    }

    fun saBanner(item: Advert, pos: Int, type: String) {
        try {
            val property = JSONObject()
            property.put("advertisement_id", item.key)
            property.put("advertisement_name", item.title)
            property.put("location_of_advertisement", "书城banner")
            property.put("advertising_sequence", "${pos + 1}号")
            property.put("types_of_advertisement", type)
            SensorsDataAPI.sharedInstance().track("click_advertisement", property)
        } catch (e: Exception) {
        }
    }

    private fun initStatusBar() {
        val params = top.layoutParams as ConstraintLayout.LayoutParams
        params.height = ImmersionBar.getStatusBarHeight(this.requireActivity())
    }
    private fun goLiveGoodsLiving(liveFlag: LiveFlag) {
        val loginInfo = LoginInfo()
        loginInfo.roomId = liveFlag.roomId
        loginInfo.userId = liveFlag.userId
        loginInfo.viewerName = SpUtil.userInfo.name
        loginInfo.viewerToken = liveFlag.viewertoken
        DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
            override fun onLogin(templateInfo: TemplateInfo?, viewer: Viewer?, roomInfo: RoomInfo?, publishInfo: PublishInfo?) {
                val intent = Intent(this@BookStoreFragment.requireContext(), GoodsLiveActivity::class.java)
                val bundle = Bundle()
//                bundle.putSerializable("marquee", viewer?.getMarquee())
                bundle.putString("title", getString(R.string.live_goods_name))
//                bundle.putSerializable("lesson", netLesson)
//                bundle.putParcelable("SensorsData", data)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onException(e: DWLiveException?) {
                runOnUiThread {
                    Toast.makeText(this@BookStoreFragment.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }, loginInfo)
        // 执行登录操作
        DWLive.getInstance().startLogin()
    }
}