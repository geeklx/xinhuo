package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Intent
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_net_lesson_detail.*
import org.jetbrains.anko.longToast
import org.json.JSONObject
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WX_PROGRAM_ID_SPARKE
import tuoyan.com.xinghuo_dayingindex.base.LifeActivityStateAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeFullActivity
import tuoyan.com.xinghuo_dayingindex.bean.LessonDetail
import tuoyan.com.xinghuo_dayingindex.bean.LiveListBean
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.LearningProgressPromptDialog
import tuoyan.com.xinghuo_dayingindex.ui.mine.user.YanMessageActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.download.DownloadActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
import kotlin.math.abs

/**
 * 创建者：
 * 时间：
 * 已购买的网课详情页面
 */
class MineLessonActivity : LifeFullActivity<NetLessonsPresenter>() {
    override val presenter = NetLessonsPresenter(this)
    override val layoutResId: Int = R.layout.activity_net_lesson_detail

    val key by lazy { intent.getStringExtra(KEY) ?: "" }
    lateinit var lessonDetail: LessonDetail//课程详情
    private lateinit var liveList: LiveListBean//老版直播列表和回放列表数据用于录播课和音频课，新版回放数据newPlaybackFragment中请求
    var isLiveRefresh = false//直播列表或者录播课回放列表更新
    var isTest = false//是否从测评返回更新配套资源
    var isLivePlayBackRefresh = false//直播回放列表 是否更新
    var isDayWorkRefresh = false//每日任务刷新

    private val pagerAdapter by lazy { LifeActivityStateAdapter(this) }

    //每日任务
    private val dayWorkFragment by lazy {
        DayWorkFragment.instance()
    }

    //    直播列表
    private val liveFragment by lazy {
        NetTableFragment.instance(liveList.liveList)
    }

    //音频课，录播课回放列表
    private val playbackFragment by lazy {
        NetTableFragment.instance(liveList.playBackList)
    }

    //改版之后回放列表，调用新的接口
    private val newPlaybackFragment by lazy {
        PlayBackFragment.instance(key)
    }
    private val resFragment by lazy {
        LessonResFragment.newInstance(
            lessonDetail.classList,
            "沟通学习获取学霸学习技巧",
            lessonDetail.qqNum,
            lessonDetail.qqContent,
            lessonDetail.qqKey,
            lessonDetail.isApplet,
            lessonDetail.isDocDown,
            lessonDetail.docDownUrl,
            lessonDetail.title,
            lessonDetail.isAppletListen
        )
    }
    private val learningDialog by lazy { LearningProgressPromptDialog(this@MineLessonActivity) }

    override fun configView() {
        setSupportActionBar(toolbar)
        view_pager.offscreenPageLimit = 10
        view_pager.adapter = pagerAdapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        img_toolbar_download.setOnClickListener {
            saClick("点击下载网课")
            presenter.getCatalogue(key) {
                startActivity<DownloadActivity>(
                    DownloadActivity.LESSON_LIST to it,
                    DownloadActivity.RES_LIST to lessonDetail.classList,
                    DownloadActivity.LESSON_INFO to lessonDetail
                )
            }
        }
        ll_all_data.setOnClickListener {
            saClick("查看学习数据")
            val intent = Intent(this, StudyDataActivity::class.java)
            intent.putExtra(StudyDataActivity.COURSE_KEY, key)
            startActivity(intent)
        }
        clt_data.setOnClickListener {
            ll_all_data.performClick()
        }
        rg_net_class.setOnCheckedChangeListener { group, checkedId ->
            view_pager.currentItem = group.indexOfChild(group.findViewById(checkedId))
        }
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                for (index in 0 until rg_net_class.childCount) {
                    val temp = rg_net_class.getChildAt(index) as RadioButton
                    temp.textSize = 15f
                }
                val rb = rg_net_class.getChildAt(position) as RadioButton
                rb.textSize = 18f
                if (!rb.isChecked) rb.isChecked = true
            }
        })
        abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            when {
                verticalOffset == 0 -> {
                    //展开状态
                    toolbar_title.visibility = View.GONE
                    img_shadow.visibility = View.VISIBLE
                }
                abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                    //折叠状态
                    toolbar_title.visibility = View.VISIBLE
                    img_shadow.visibility = View.GONE
                }
                else -> {
                    if (abs(verticalOffset) > tv_title.height) {
                        toolbar_title.visibility = View.VISIBLE
                        img_shadow.visibility = View.GONE
                    } else {
                        toolbar_title.visibility = View.GONE
                        img_shadow.visibility = View.VISIBLE
                    }
                }
            }
        })

        cd_yan.setOnClickListener {
            if ("true" == lessonDetail.allowAddAdmissionCard) {
                startActivity<PostActivity>(PostActivity.URL to "preferential/BackUpCertification")
            }
        }
        cd_wx.setOnClickListener {
            WxMini.goWxMini(this, WX_PROGRAM_ID_SPARKE, lessonDetail.qrCode)
        }
    }

    override fun initData() {
        presenter.getMyLessonDetail(key) { it ->
            if (it.isown == "1") {
                lessonDetail = it
                saDetail(lessonDetail)
                //直播列表 未改版录播列表都在此接口
                presenter.getLiveCatalogue(key, lessonDetail.form) { listBean ->
                    liveList = listBean
                    bindData()
                    if (lessonDetail?.rateLearningShow == "1") {
                        learningDialog?.show()
                        learningDialog?.setType(lessonDetail.videoLearnedRateState, lessonDetail.videoLearnedNum, 3)
                    }
                }
            } else {
                longToast("您未购买此课程")
                onBackPressed()
            }
        }
    }

    private fun refreshDetail(type: String = "0") {
        presenter.getMyLessonDetail(key) { it ->
            lessonDetail = it
            updateProgress()
            if (type == "1") {
                refreshRes()
            }
        }
    }

    private fun updateProgress() {
        try {
            if (("0" == lessonDetail.workNum || lessonDetail.workNum.isNullOrEmpty())
                && ("0" == lessonDetail.videoNum || lessonDetail.videoNum.isNullOrEmpty())
                && ("0" == lessonDetail.prepareNum || lessonDetail.prepareNum.isNullOrEmpty())
            ) {
                ll_progress.visibility = View.GONE
                ctl_pro.visibility = View.GONE
            } else if ("true" == lessonDetail.necessary
                && ("0" != lessonDetail.workNum && lessonDetail.workNum.isNotEmpty())
                && ("0" != lessonDetail.videoNum && lessonDetail.videoNum.isNotEmpty())
                && ("0" != lessonDetail.prepareNum && lessonDetail.prepareNum.isNotEmpty())
            ) {
                ctl_pro.visibility = View.VISIBLE
                ll_progress.visibility = View.GONE
                tv_class_num.text = Html.fromHtml("<font color='#5467ff'><big>${lessonDetail.videoLearnedNum}</big></font>/<small>${lessonDetail.videoNum}</small>")
                tv_pre_num.text = Html.fromHtml("<font color='#5467ff'><big>${lessonDetail.prepareLearnedNum}</big></font>/<small>${lessonDetail.prepareNum}</small>")
                tv_work_num.text = Html.fromHtml("<font color='#5467ff'><big>${lessonDetail.workLearnedNum}</big></font>/<small>${lessonDetail.workNum}</small>")
            } else if (("0" == lessonDetail.workNum || lessonDetail.workNum.isNullOrEmpty())
                && ("0" == lessonDetail.prepareNum || lessonDetail.prepareNum.isNullOrEmpty())
            ) {
                ctl_pro.visibility = View.GONE
                ll_progress.visibility = View.VISIBLE
                ctl_test.visibility = View.GONE
                val ctlListenParams = ctl_listen.layoutParams as LinearLayout.LayoutParams
                ctlListenParams.weight = 1.6f
                ctl_listen.layoutParams = ctlListenParams
                val llAllDataParams = ll_all_data.layoutParams as LinearLayout.LayoutParams
                llAllDataParams.marginEnd = DeviceUtil.dp2px(this, 20f).toInt()
                ll_all_data.layoutParams = llAllDataParams
                ll_all_data.setPadding(0, 17, 0, 0)
                tv_all_data.setEms(4)
                tv_all_data.compoundDrawablePadding = DeviceUtil.dp2px(this, 6f).toInt()
                tv_all_data.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_all_data, 0, 0)
                img_lesson.setImageResource(R.mipmap.icon_lesson)
                val lessonParams = img_lesson.layoutParams as ConstraintLayout.LayoutParams
                lessonParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                img_lesson.layoutParams = lessonParams
            } else {
                ctl_pro.visibility = View.GONE
                ll_progress.visibility = View.VISIBLE
                ctl_test.visibility = View.VISIBLE
                val ctlListenParams = ctl_listen.layoutParams as LinearLayout.LayoutParams
                ctlListenParams.weight = 4.2f
                ctl_listen.layoutParams = ctlListenParams
                val llAllDataParams = ll_all_data.layoutParams as LinearLayout.LayoutParams
                llAllDataParams.marginEnd = 0
                ll_all_data.layoutParams = llAllDataParams
                tv_all_data.setEms(1)
                tv_all_data.compoundDrawablePadding = 0
                tv_all_data.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                if ("0" == lessonDetail.workNum || lessonDetail.workNum.isNullOrEmpty()) {
                    pb_test.progress = lessonDetail.prepareLearnedNum.toInt() * 100 / lessonDetail.prepareNum.toInt()
                    tv_test_num.text = Html.fromHtml("<font color='#008aff'><big>${lessonDetail.prepareLearnedNum}</big></font>/${lessonDetail.prepareNum}")
                    tv_test_title.text = if ("true" == lessonDetail.necessary) "必修预习" else "完成练习"
                } else if ("0" == lessonDetail.prepareNum || lessonDetail.prepareNum.isNullOrEmpty()) {
                    pb_test.progress = lessonDetail.workLearnedNum.toInt() * 100 / lessonDetail.workNum.toInt()
                    tv_test_num.text = Html.fromHtml("<font color='#008aff'><big>${lessonDetail.workLearnedNum}</big></font>/${lessonDetail.workNum}")
                    tv_test_title.text = if ("true" == lessonDetail.necessary) "必修课后作业" else "完成练习"
                }
            }
            tv_lesson_title.text = if ("true" == lessonDetail.necessary) "必修课程" else "完成听课"
            if ("0" != lessonDetail.videoNum && !lessonDetail.videoNum.isNullOrEmpty()) {
                pb_listen.progress = lessonDetail.videoLearnedNum.toInt() * 100 / lessonDetail.videoNum.toInt()
                tv_listen_num.text = Html.fromHtml("<font color='#5467ff'><big>${lessonDetail.videoLearnedNum}</big></font>/${lessonDetail.videoNum}")
            }


        } catch (e: Exception) {
        }
    }

    private fun refreshRes() {
        if (resFragment.isAdded) {
            resFragment.initResourceList(lessonDetail.classList)
        }
    }

    private var fragmentList = ArrayList<Fragment>()
    private fun bindData() {
        updateProgress()
        cd_yan.visibility = if ("true" == lessonDetail.isAdmissionTicket) View.VISIBLE else View.GONE
        cd_wx.visibility = if (lessonDetail.qrCode.isNullOrBlank()) View.GONE else View.VISIBLE
        tv_title.text = lessonDetail.title
        toolbar_title.text = lessonDetail.title
        Glide.with(this).load(lessonDetail.admissionTicketImg).into(img_yan)
        tv_validity_period?.text = lessonDetail.endTime?.replace(":", " ")?.replace("-", ".")
        if ("1" == lessonDetail.IsEverydayTask) {
            fragmentList.add(dayWorkFragment)
            //1直播2录播3过级包4音频课,目前过级包归到直播下面
            if (lessonDetail.form == "1" || lessonDetail.form == "3") {
                fragmentList.add(liveFragment)
                fragmentList.add(newPlaybackFragment)
            } else if (lessonDetail.form == "2" || lessonDetail.form == "4") {
                rb_two.text = "课程列表"
                rb_three.text = "课程服务"
                rb_four.visibility = View.GONE
                fragmentList.add(playbackFragment)
            }
        } else {
            if (lessonDetail.form == "1" || lessonDetail.form == "3") {
                fragmentList.add(liveFragment)
                rb_one.text = "直播列表"
                rb_two.text = "回放列表"
                rb_three.text = "课程服务"
                rb_four.visibility = View.GONE
                fragmentList.add(newPlaybackFragment)
            } else if (lessonDetail.form == "2" || lessonDetail.form == "4") {
                rb_one.text = "课程列表"
                rb_two.text = "课程服务"
                rb_three.visibility = View.GONE
                rb_four.visibility = View.GONE
                fragmentList.add(playbackFragment)
            }
        }
        fragmentList.add(resFragment) //课程资料
        pagerAdapter.fragmentList = fragmentList
        if (lessonDetail.postgraduateRecordKey.isNullOrEmpty()) {
            startActivity(Intent(this, YanMessageActivity::class.java))
        }
    }

    companion object {
        const val KEY = "key"
    }

    override fun onResume() {
        super.onResume()
        if (isLiveRefresh) {
            refreshDetail()
            presenter.getLiveCatalogue(key, lessonDetail.form) {
                if (it.liveList.size > 0 && liveFragment.isAdded) {
                    liveFragment.refreshData(it.liveList)
                }
                if (it.playBackList.size > 0 && playbackFragment.isAdded) {
                    playbackFragment.refreshData(it.playBackList)
                }
            }
            isLiveRefresh = false
        }
        if (isTest) {
            refreshDetail("1")
            isTest = false
        }
        if (isLivePlayBackRefresh) {
            refreshDetail()
            newPlaybackFragment.refreshData()
            isLivePlayBackRefresh = false
        }
        if (isDayWorkRefresh) {
            refreshDetail()
            dayWorkFragment.initData()
            isDayWorkRefresh = false
        }
    }

    //神策收集网课详情
    private fun saDetail(detail: LessonDetail) {
        try {
            val property = setProperty(detail)
            SensorsDataAPI.sharedInstance().track("view_learning_page_click", property)
        } catch (e: Exception) {
        }
    }

    private fun setProperty(detail: LessonDetail): JSONObject {
        val property = JSONObject()
        property.put("course_id", key)
        property.put("course_name", detail.title)
        property.put("is_live", "1" == detail.form)
        property.put("course_validity", detail.endTime)
        property.put("original_price", detail.price ?: "0")
        property.put("current_price", detail.disprice ?: "0")
        property.put("teacher_name", detail.teacher)
        property.put("internal_name_online_course", detail.privateName)
        return property
    }

    //网课操作
    fun saClick(operationName: String) {
        try {
            val property = setProperty(lessonDetail)
            property.put("operation_type", operationName)
            SensorsDataAPI.sharedInstance().track("online_learning_page_click", property)
        } catch (e: Exception) {
        }
    }
}