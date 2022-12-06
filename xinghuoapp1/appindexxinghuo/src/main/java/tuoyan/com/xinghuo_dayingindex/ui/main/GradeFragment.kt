package tuoyan.com.xinghuo_dayingindex.ui.main
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bokecc.sdk.mobile.live.DWLive
import com.bokecc.sdk.mobile.live.DWLiveLoginListener
import com.bokecc.sdk.mobile.live.Exception.DWLiveException
import com.bokecc.sdk.mobile.live.pojo.*
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.fragment_grade.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONObject
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.list.BookListActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.report.RecommedNewsActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.GoodsLiveActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.PopDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.HomePresenter
import tuoyan.com.xinghuo_dayingindex.ui.main.adapter.GradeMenuAdapter
import tuoyan.com.xinghuo_dayingindex.ui.main.adapter.PointAdapter
import tuoyan.com.xinghuo_dayingindex.ui.main.adapter.TeacherAdapter
import tuoyan.com.xinghuo_dayingindex.ui.main.coupon.MainCouponActivity
import tuoyan.com.xinghuo_dayingindex.ui.main.list.LessonRecListActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.spoken.SpokenListActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.real.RealCETActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
import java.io.Serializable

class GradeFragment : LifeV4Fragment<HomePresenter>() {
    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_grade
    private var checkLogin = false;
    private var isFirstShow = false;
    val adapter by lazy {
        Adapter {
            saRecommendClick(it)
            //1:网课文件夹；0：网课 2:图书
            when (it.dataType) {
                "0" -> {
                    val intent = Intent(activity, LessonDetailActivity::class.java)
                    intent.putExtra(LessonDetailActivity.KEY, it.key)
                    startActivity(intent)
                }
                "1" -> {
                    val intent = Intent(activity, LessonListActivity::class.java)
                    intent.putExtra(LessonListActivity.KEY, it.key)
                    intent.putExtra(LessonListActivity.TITLE, it.title)
                    startActivity(intent)
                }
                "2" -> {
                    startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${it.key}")
                }
            }
        }
    }
    val menuAdapter by lazy {
        GradeMenuAdapter { item, pos ->
            saClick(item.name, pos)
            goMenuType(item)
        }
    }
    val gradeItem: GradeBean? by lazy { arguments?.getParcelable<GradeBean>(DATA) }
    private lateinit var lav_play: LottieAnimationView
    private lateinit var liveFlag: LiveFlag
    override fun configView(view: View?) {
        super.configView(view)
        rlv_class.setHasFixedSize(true)
        rlv_class.layoutManager = LinearLayoutManager(activity)
        rlv_class.adapter = adapter
        srfl.setColorSchemeResources(R.color.color_1482ff)
        lav_play = drag_float.findViewById<LottieAnimationView>(R.id.lav_play)
        drag_float.setOnClickListener({})
        drag_float.findViewById<View>(R.id.live_goods).setOnClickListener({
            //点击进入到直播带货
            liveFlag?.let {
                checkLogin = false;
                isLogin {
                    checkLogin = true;
                    goLiveGoodsLiving(liveFlag)
                }
            }
        })

        drag_float.findViewById<View>(R.id.iv_close).setOnClickListener({
            if (drag_float.visibility == View.VISIBLE) {
                drag_float.visibility = View.GONE
            }
        })
    }

    private fun goLiveGoodsLiving(liveFlag: LiveFlag) {
        val loginInfo = LoginInfo()
        loginInfo.roomId = liveFlag.roomId
        loginInfo.userId = liveFlag.userId
        loginInfo.viewerName = SpUtil.userInfo.name
        loginInfo.viewerToken = liveFlag.viewertoken
        DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
            override fun onLogin(
                templateInfo: TemplateInfo?, viewer: Viewer?, roomInfo: RoomInfo?, publishInfo: PublishInfo?
            ) {
                val intent = Intent(this@GradeFragment.requireContext(), GoodsLiveActivity::class.java)
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
                    Toast.makeText(
                        this@GradeFragment.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG
                    ).show()
                }
            }
        }, loginInfo)
        // 执行登录操作
        DWLive.getInstance().startLogin()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d("zhao", "setUserVisibleHint: " + (gradeItem?.key ?: '9'))
        if (isVisibleToUser) {
            if (!isFirstShow) {
                gradeItem?.let { it -> getDialog(it.key) }
                isFirstShow = true
            }
        } else {
        }

    }

    override fun onResume() {
        super.onResume()
        if ((!checkLogin) && SpUtil.isLogin) {
            getLiveFlag()
        }

    }

    override fun handleEvent() {
        super.handleEvent()
//        abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//            srfl.isEnabled = verticalOffset >= 0
//        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            slv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                v_c_t.visibility = if (scrollY >= v_c.top) View.VISIBLE else View.GONE
                v_c.visibility = if (scrollY >= v_c.top) View.GONE else View.VISIBLE
            }
        }
        srfl.setOnRefreshListener {
            presenter.getHomeData(gradeItem?.key ?: "", {

                srfl.isRefreshing = false
                initBanner(it.sybnList, it.syzwList)
                initLessons(it.netList)
                rlv_menu.layoutManager = GridLayoutManager(this.requireContext(), it.menuList.size)
                rlv_menu.adapter = menuAdapter
                menuAdapter.setData(it.menuList)
                if (it.examList != null && it.examList.size > 0) {
                    mq_text.clearState()
                    if (it.examList.size == 1) {
                        var list = ArrayList<Spanned>()
                        for (item in it.examList) {
                            list.add(Html.fromHtml("距" + item.name + "还有<font color=\"#008AFF\">" + item.days + "</font>" + "天"))
                        }
                        mq_text.startWithListOne(list as List<Nothing>?)
                        ll_exam.visibility = View.VISIBLE
                    } else {
                        var list = ArrayList<Spanned>()
                        for (item in it.examList) {
                            list.add(Html.fromHtml("距" + item.name + "还有<font color=\"#008AFF\">" + item.days + "</font>" + "天"))
                        }
                        mq_text.startWithList(list as List<Nothing>?)
                        ll_exam.visibility = View.VISIBLE
                    }
                } else {
                    ll_exam.visibility = View.GONE
                }
            }) {
                srfl.isRefreshing = false
                ll_exam.visibility = View.GONE
            }

            getLiveFlag()
        }
        tv_id.setOnClickListener {
//            val bookParam = EBookParam()
//            bookParam.bookKey = "1553662010876340032"
//            bookParam.catalogKey = "1576084592019792576"
//            bookParam.resourceKey = "1576169400444020160"
//            bookParam.userpractisekey = "1668104867632015808"
//            startActivity<EBookWordExerciseAnalysisActivity>(EBookLifeActivity.EBOOK_PARAM to bookParam)
        }
    }

    private fun getLiveFlag() {
        presenter.getLiveFlag(gradeItem?.key ?: "", {
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

    override fun initData() {
        super.initData()
        presenter.getHomeData(gradeItem?.key ?: "", {
            initBanner(it.sybnList, it.syzwList)
            rlv_menu.layoutManager = GridLayoutManager(this.requireContext(), it.menuList.size)
            rlv_menu.adapter = menuAdapter
            menuAdapter.setData(it.menuList)
            if (it.netList.isEmpty()) {
                tv_id.visibility = View.GONE
                rlv_class.visibility = View.GONE
            } else {
                tv_id.visibility = View.VISIBLE
                rlv_class.visibility = View.VISIBLE
                initLessons(it.netList)
            }
            if (it.examList != null && it.examList.size > 0) {
                if (it.examList.size == 1) {
                    var list = ArrayList<Spanned>()
                    for (item in it.examList) {
                        list.add(Html.fromHtml("距" + item.name + "还有<font color=\"#008AFF\">" + item.days + "</font>" + "天"))
                    }
                    mq_text.startWithListOne(list as List<Nothing>?)
                    ll_exam.visibility = View.VISIBLE
                } else {
                    var list = ArrayList<Spanned>()
                    for (item in it.examList) {
                        list.add(Html.fromHtml("距" + item.name + "还有<font color=\"#008AFF\">" + item.days + "</font>" + "天"))
                    }
                    mq_text.startWithList(list as List<Nothing>?)
                    ll_exam.visibility = View.VISIBLE
                }
            } else {
                ll_exam.visibility = View.GONE
            }
        }) {}

        presenter.getLiveFlag(gradeItem?.key ?: "", {
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

    companion object {
        const val DATA = "data"
        fun newInstance(item: GradeBean): GradeFragment {
            val f = GradeFragment()
            val arg = Bundle()
            arg.putParcelable(DATA, item)
            f.arguments = arg
            return f
        }
    }

    private fun initBanner(dataList: List<Advert>, syzwList: List<Advert>) {
        banner_home.setImagesUrl(dataList)
        banner_home.setOnItemClickListener {
            val item = dataList[it]
            presenter.advertisingPv(item.key)
            if (item.type == "link" && item.link != "") {
                saBanner(item, "首页上方banner", it, "外链")
                startActivity<WebViewActivity>(
                    WebViewActivity.URL to item.link, WebViewActivity.TITLE to item.title
                )
            } else if (item.goodtype == "net" && item.goodkey != "") {
                saBanner(item, "首页上方banner", it, "网课")
                startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to item.goodkey)
            } else if (item.goodtype == "book" && item.goodkey != "") {
                saBanner(item, "首页上方banner", it, "图书")
                Log.e("BOOK_DETAIL", WEB_BASE_URL + "bookdetail?id=${item.goodkey}")
                startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${item.goodkey}")
            } else if ("app" == item.goodtype && !item.key.isNullOrEmpty()) {
                saBanner(item, "首页上方banner", it, "小程序")
//                小程序
                WxMini.goWxMini(this.requireActivity(), item.key)
            } else if ("smartBook" == item.goodtype) {
                saBanner(item, "首页上方banner", it, "点读书")
                startActivity<EBookListActivity>()
            } else if ("netSpecial" == item.goodtype) {
                saBanner(item, "首页上方banner", it, "网课专题")
                startActivity<LessonListActivity>(
                    LessonListActivity.KEY to item.goodkey, LessonListActivity.TITLE to item.title
                )
            }else if("spoken" == item.goodtype){
                startActivity<SpokenListActivity>( SpokenListActivity.TITLE to item.title)

            }
        }
        if (syzwList.isEmpty()) {
            banner_recommed.visibility = View.GONE
        } else {
            banner_recommed.visibility = View.VISIBLE
            banner_recommed.setImagesUrl(syzwList)
            banner_recommed.stopAutoPlay()
            banner_recommed.setPointsIsVisible(false)
            banner_recommed.setOnItemClickListener {
                val item = syzwList[it]
                presenter.advertisingPv(item.key)
                if (item.type == "link" && item.link != "") {
                    saBanner(item, "首页中间banner", it, "外链")
                    startActivity<WebViewActivity>(
                        WebViewActivity.URL to item.link, WebViewActivity.TITLE to item.title
                    )
                } else if (item.goodtype == "net" && item.goodkey != "") {
                    saBanner(item, "首页中间banner", it, "网课")
                    startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to item.goodkey)
                } else if (item.goodtype == "book" && item.goodkey != "") {
                    saBanner(item, "首页中间banner", it, "图书")
                    Log.e("BOOK_DETAIL", WEB_BASE_URL + "bookdetail?id=${item.goodkey}")
                    startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${item.goodkey}")
                } else if ("app" == item.goodtype && !item.key.isNullOrEmpty()) {
                    saBanner(item, "首页中间banner", it, "小程序")
                    WxMini.goWxMini(this.requireActivity(), item.key)
                } else if ("smartBook" == item.goodtype) {
                    saBanner(item, "首页中间banner", it, "点读书")
                    startActivity<EBookListActivity>()
                } else if ("netSpecial" == item.goodtype) {
                    saBanner(item, "首页中间banner", it, "网课专题")
                    startActivity<LessonListActivity>(
                        LessonListActivity.KEY to item.goodkey, LessonListActivity.TITLE to item.title
                    )
                }else if("spoken" == item.goodtype){
                    startActivity<SpokenListActivity>( SpokenListActivity.TITLE to item.title)

                }
            }
        }
    }

    private fun initLessons(lessons: List<Lesson>) {
        val str =
            ("Product-" + Build.PRODUCT + "|VERSION_CODES.BASE-" + Build.VERSION_CODES.BASE + "|system-" + Build.DISPLAY + "|MODEL-" + Build.MODEL + "|BRAND-" + Build.BRAND + "|CPU_ABI-" + Build.CPU_ABI + "|SDK-" + Build.VERSION.SDK + "|VERSION.RELEASE-" + Build.VERSION.RELEASE + "|MANUFACTURER-" + Build.MANUFACTURER)
        Log.e("AAAAA", str)
        adapter.setData(lessons)
    }

    /**
     * 99338629033601428     口袋题库书列表（智能书列表）
     * 10016003909571888011  分学段网课
     * 10016003909571888007  敬请期待
     * 10016003909571888006  优惠专区
     * 10016003909571888005  智能模考(全部)
     * 10016003909571888004  备考干货(全部)
     * 10016003909571888003  微信小程序
     * 10016003909571888002  活动页
     * 10016003909571888001  网课
     * 10016003909571888000  图书配套(全部)
     */
    private fun goMenuType(item: GradeMenu) {
        when (item.menuType) {
            //口语模考
            "10016003874571888000"->{
                startActivity<SpokenListActivity>( SpokenListActivity.TITLE to "四六级口语模考")

            }
            "10016003909571888007" -> {
            }
            "10016003909571888006" -> {
                val intent = Intent(this.requireActivity(), MainCouponActivity::class.java)
                intent.putExtra(MainCouponActivity.KEY, item.key)
                startActivity(intent)
            }
            "10016003909571888005" -> {
                val intent = Intent(activity, RealCETActivity::class.java)
                intent.putExtra(RealCETActivity.GRAD_KEY, gradeItem?.key)
                startActivity(intent)
            }
            "10016003909571888004" -> {
                startActivity<RecommedNewsActivity>(
                    RecommedNewsActivity.TITLE to "备考干货", RecommedNewsActivity.GRADE_KEY to gradeItem?.key
                )
            }
            "10016003909571888003" -> {
                presenter.getHomeApplets(item.key) {
                    WxMini.goWxMini(this.requireContext(), it[0].id)
                }
            }
            "10016003909571888002" -> {
                startActivity<PostActivity>(PostActivity.URL to item.content)
            }
            "10016003909571888001" -> {
                //不分学段网课
                presenter.getHomeNets(item.key) {
                    if (1 == it.size) {
                        if ("0" == it[0].dataType) {
                            startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to it[0].key)
                        } else if ("1" == it[0].dataType) {
                            val intent = Intent(activity, LessonListActivity::class.java)
                            intent.putExtra(LessonListActivity.KEY, it[0].key)
                            intent.putExtra(LessonListActivity.TITLE, it[0].title)
                            startActivity(intent)
                        }
                    } else {
                        val intent = Intent(this.requireContext(), LessonListActivity::class.java)
                        intent.putExtra(LessonListActivity.DATA_LIST, it as Serializable)
                        intent.putExtra(LessonListActivity.TITLE, item.name)
                        startActivity(intent)
                    }
                }
            }
            "10016003909571888000" -> {
                val intent = Intent(activity, BookListActivity::class.java)
                intent.putExtra(BookListActivity.GRADE_KEY, gradeItem?.key)
                startActivity(intent)
            }
            "10016003909571888011" -> {
                //分学段网课 item.key
                val intent = Intent(activity, LessonRecListActivity::class.java)
                intent.putExtra(LessonRecListActivity.KEY, item.key)
                intent.putExtra(LessonRecListActivity.GRADE_KEY, gradeItem?.key)
                intent.putExtra(LessonRecListActivity.TITLE, item.name)
                startActivity(intent)
            }
            "99338629033601428" -> {
                //口袋题库
                val intent = Intent(activity, EBookListActivity::class.java)
                startActivity(intent)
            }
        }
    }

    //网课操作
    private fun saClick(type: String, pos: Int) {
        val grade = when (gradeItem?.key) {
            "0" -> "精选"
            "1" -> "四六级"
            "2" -> "考研"
            "3" -> "专四专八"
            else -> ""
        }
        try {
            val property = JSONObject()
            property.put("section", grade)
            property.put("types_of_function_entry", type)
            property.put("location_of_function_entry", "${pos + 1}号位")
            SensorsDataAPI.sharedInstance().track("click_function_entry", property)
        } catch (e: Exception) {
        }
    }

    private fun saRecommendClick(item: Lesson) {
        val grade = when (gradeItem?.key) {
            "0" -> "精选"
            "1" -> "四六级"
            "2" -> "考研"
            "3" -> "专四专八"
            else -> ""
        }
        try {
            val property = JSONObject()
            property.put("section", grade)
            property.put("commodity_type", "网课")
            property.put("commodity_id", item.key)
            property.put("commodity_name", item.title)
            property.put("internal_name_online_course", item.privateName)
            SensorsDataAPI.sharedInstance().track("click_home_page_recommendation", property)
        } catch (e: Exception) {
        }
    }

    fun saBanner(item: Advert, location: String, pos: Int, type: String) {
        try {
            val grade = when (gradeItem?.key) {
                "0" -> "精选"
                "1" -> "四六级"
                "2" -> "考研"
                "3" -> "专四专八"
                else -> ""
            }
            val property = JSONObject()
            property.put("advertisement_id", item.key)
            property.put("advertisement_name", item.title)
            property.put("location_of_advertisement", location)
            property.put("section", grade)
            property.put("advertising_sequence", "${pos + 1}号")
            property.put("types_of_advertisement", type)
            SensorsDataAPI.sharedInstance().track("click_advertisement", property)
        } catch (e: Exception) {
        }
    }

    fun getDialog(key: String) {
        presenter.getDialog(key) { list ->
            if (list.isNotEmpty()) {
                val advert = list[0]
                PopDialog(requireContext(), advert.img, { dialog ->
                    dialog.dismiss()
                    presenter.advertisingPv(advert.key)
                    if (advert.type == "link") {
                        if (advert.link != "") {
                            saBanner(advert, "广告弹窗" + key, 0, "外链")
                            startActivity<WebViewActivity>(
                                WebViewActivity.URL to advert.link, WebViewActivity.TITLE to advert.title
                            )
                        }
                    } else if (advert.goodtype == "net" && !advert.goodkey.isNullOrEmpty()) {
                        saBanner(advert, "广告弹窗" + key, 0, "网课")
                        startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to advert.goodkey)
                    } else if (advert.goodtype == "book" && !advert.goodkey.isNullOrEmpty()) {
                        //跳转点读
                        saBanner(advert, "广告弹窗" + key, 0, "图书")
                        startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${advert.goodkey}")
                    } else if ("app" == advert.goodtype && !advert.key.isNullOrEmpty()) {
                        saBanner(advert, "广告弹窗" + key, 0, "小程序")
                        WxMini.goWxMini(requireContext(), advert.key)
                    } else if ("smartBook" == advert.goodtype) {
                        saBanner(advert, "广告弹窗" + key, 0, "智能书")
                        startActivity<EBookListActivity>()
                    } else if ("netSpecial" == advert.goodtype) {
                        saBanner(advert, "广告弹窗" + key, 0, "网课专题")
                        startActivity<LessonListActivity>(
                            LessonListActivity.KEY to advert.goodkey, LessonListActivity.TITLE to advert.title
                        )
                    }else if("spoken" == advert.goodtype){
                        startActivity<SpokenListActivity>( SpokenListActivity.TITLE to advert.title)

                    }
                }) {

                }.show()
            }

        }
    }

    override fun onStart() {
        super.onStart()
        mq_text.startFlipping();
    }

    override fun onStop() {
        super.onStop()
        mq_text.stopFlipping();
    }
}

class Adapter(val onItemClick: (id: Lesson) -> Unit) : BaseAdapter<Lesson>(isEmpty = true, isFooter = true) {
    override fun convert(holder: ViewHolder, item: Lesson) {
        (holder.getView(R.id.tv_old_price) as TextView).paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        Glide.with(holder.itemView.context).load(item.img).into(holder.getView(R.id.img_cover) as ImageView)
        Glide.with(holder.itemView.context).load(item.sellingPointImg).into(holder.getView(R.id.img_hot) as ImageView)
        holder.setText(R.id.tv_title, item.title).setText(
            R.id.tv_old_price, if (item.isown == "1" || item.chargeType == "0" || item.isAppointment == "1") {
                ""
            } else if (!item.price.isNullOrEmpty()) {
                "¥" + item.price
            } else {
                ""
            }
        ).setText(R.id.tv_cover_tag, item.netSubjectName).setVisible(
            R.id.tv_cover_tag, if (item.netSubjectName.isEmpty()) View.GONE else View.VISIBLE
        )
//            .setVisible(
//                R.id.img_tag_down,
//                if (item.netSubjectName.isEmpty()) View.GONE else View.VISIBLE
//            )
        setText(holder, item)

        val pointAdapter = PointAdapter(item.sellingPointColor)
        val pManager = LinearLayoutManager(holder.itemView.context)
        pManager.orientation = LinearLayoutManager.HORIZONTAL
        val rlv_p = holder.getView(R.id.rlv_p) as RecyclerView
        rlv_p.layoutManager = pManager
        rlv_p.adapter = pointAdapter
        pointAdapter.setData(item.sellingPoint.split(","))

        val adapter = TeacherAdapter()
        val rlv_teachers = holder.getView(R.id.rlv_teachers) as RecyclerView
        val manager = LinearLayoutManager(holder.itemView.context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        rlv_teachers.layoutManager = manager
        rlv_teachers.adapter = adapter
        val list = mutableListOf<GradeMenu>()
        val teacherNames = item.teacher.split(",")
        val teacherImgs = item.teacherImg.split(",")
        teacherNames.forEachIndexed { index, name ->
            val temp = GradeMenu()
            temp.name = name
            if (teacherImgs.size > index) {
                temp.imgUrl = teacherImgs[index]
            }
            list.add(temp)
        }
        adapter.setData(list)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
        setParams(holder)
    }

    private fun setParams(holder: ViewHolder) {
        val context = holder.itemView.context
        val img = holder.getView(R.id.img_cover) as ImageView
        val tv_title = holder.getView(R.id.tv_title) as TextView
        val tv_time = holder.getView(R.id.tv_time) as TextView
        val rlv_teachers = holder.getView(R.id.rlv_teachers) as RecyclerView
        rlv_teachers.post {//post中获取高度
            img.measuredHeight
            tv_title.measuredHeight
            tv_time.measuredHeight
            rlv_teachers.measuredHeight
            val tvParams = tv_time.layoutParams as ConstraintLayout.LayoutParams
            val teacherParams = rlv_teachers.layoutParams as ConstraintLayout.LayoutParams
            val imgHeight = img.height
            var tHeight = tv_title.height + tv_time.height + rlv_teachers.height + teacherParams.topMargin
            if (tv_time.height > 0) {
                tHeight += tvParams.topMargin
            }
            context.runOnUiThread {
                val params = ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                params.leftToRight = R.id.cv_cover
                if (imgHeight < tHeight) {
                    params.topToBottom = R.id.tv_time
                    params.topMargin = DeviceUtil.dp2px(context, 12f).toInt()
                } else {
                    params.bottomToBottom = R.id.cv_cover
                }
                rlv_teachers.layoutParams = params
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = LayoutInflater.from(context).inflate(R.layout.layout_home_class_item, parent, false)

    private fun setText(holder: ViewHolder, item: Lesson) {
        if ("2" == item.dataType) {
            holder.setVisible(R.id.tv_time, View.GONE).setVisible(R.id.v_line, View.GONE).setVisible(R.id.tv_num, View.GONE)
        } else if (item.liveTime.isNullOrEmpty()) {
            holder.setText(R.id.tv_time, "${item.period}课时").setVisible(R.id.v_line, View.GONE).setText(R.id.tv_num, "")
        } else {
            holder.setText(R.id.tv_time, item.liveTime).setText(R.id.tv_num, "${item.period}课时")
        }

        if (item.isown != "1" && item.isAppointment == "1") {
            //没有购买，预约
            holder.setText(R.id.tv_buy_num, "将于<font color='#15D25F'>${item.upStartTime}</font>开售").setVisible(R.id.v_line_b, View.GONE).setText(R.id.tv_limit, "")
        } else if (!item.limitBuyers.isNullOrEmpty() && item.limitBuyers.toInt() > 0) {
            //网课限购人数大于0 展示
            holder.setText(R.id.tv_buy_num, "已购${item.buyers}").setVisible(R.id.v_line_b, View.VISIBLE).setText(R.id.tv_limit, "限购${item.limitBuyers}")
        } else {
            holder.setText(R.id.tv_buy_num, "已购${item.buyers}").setVisible(R.id.v_line_b, View.GONE).setText(R.id.tv_limit, "")
        }
        if (item.isown == "1" && item.chargeType == "0") {
            holder.setText(R.id.tv_new_price, "<font color='#222222'>已领取</font>")
        } else if (item.isown == "1" && item.chargeType == "1") {
            holder.setText(R.id.tv_new_price, "<font color='#222222'>已购买</font>")
        } else if ("1" == item.saleOut) {
            holder.setText(R.id.tv_new_price, "<font color='#222222'>已售罄</font>")
        } else if (!item.assembleKey.isNullOrEmpty()) {
            holder.setText(R.id.tv_new_price, "<small>￥</small>${item.assemblePrice} 拼团")
        } else if (item.isAppointedNet == "1" && item.isAppointment == "1") {
            holder.setText(R.id.tv_new_price, "<font color='#222222'>已预约</font>")
        } else if (item.isAppointment == "1") {
            holder.setText(R.id.tv_new_price, "<font color='#15D25F'>正在预约</font>")
        } else if (item.chargeType == "0") {
            holder.setText(R.id.tv_new_price, "免费")
        } else if (item.isLimitFree == "1") {
            holder.setText(R.id.tv_new_price, "限时免费")
        } else if (!item.dpKey.isNullOrBlank()) {
            holder.setText(
                R.id.tv_new_price, "<font color='#FFC800'>定金${item.dpPrice}抵${item.dpSweelPrice}</font>"
            )
        } else {
            holder.setText(R.id.tv_new_price, "<small>￥</small>${item.disprice}")
        }
    }

    override fun emptyImageRes(): Int {
        return R.drawable.empty_study
    }

    override fun emptyText(): String {
        return "暂无网课"
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt()
        )
        view.layoutParams = params
        return view
    }
}