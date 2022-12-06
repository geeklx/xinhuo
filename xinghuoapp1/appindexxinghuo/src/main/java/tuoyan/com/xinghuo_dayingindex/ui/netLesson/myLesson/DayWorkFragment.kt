package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bokecc.sdk.mobile.live.DWLive
import com.bokecc.sdk.mobile.live.DWLiveLoginListener
import com.bokecc.sdk.mobile.live.Exception.DWLiveException
import com.bokecc.sdk.mobile.live.pojo.*
import com.bokecc.sdk.mobile.live.replay.DWLiveReplay
import com.bokecc.sdk.mobile.live.replay.DWLiveReplayLoginListener
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayLoginInfo
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.fragment_day_work.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.LiveCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.ReplayCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.LiveHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.ReplayHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
import tuoyan.com.xinghuo_dayingindex.utlis.img.GlideImageLoader

/**
 */
class DayWorkFragment() : LifeV4Fragment<NetLessonsPresenter>() {
    override val presenter = NetLessonsPresenter(this)
    override val layoutResId = R.layout.fragment_day_work
    val parent by lazy { activity as MineLessonActivity }
    private var dayWork: DayWork? = null
    private val workAdapter by lazy {
        WorkAdapter() { item ->
            parent.isDayWorkRefresh = true
            if ("1" == item.type) {
                //state 0未开课,1直播中,2 维护中,3已转录,4 可回放,5 未进行直播但已到直播结束时间 6已完成
                parent.saClick("点击看课")
                presenter.getMTCloudUrl(item.videoKey) {
                    val data = SensorsData()
                    data.course_id = parent.lessonDetail.netcoursekey ?: ""
                    data.course_name = parent.lessonDetail.title ?: ""
                    data.is_live = "1" == parent.lessonDetail.form
                    data.course_validity = parent.lessonDetail.endTime
                    data.original_price = parent.lessonDetail.price ?: "0"
                    data.current_price = parent.lessonDetail.disprice ?: "0"
                    data.internal_name_online_course = parent.lessonDetail.privateName
                    data.teacher_name = item.teacher ?: ""
                    data.charge_type = if ("0" == item.chargeType) "免费" else "付费"
                    data.period_id = item.videoKey
                    data.live_platform_id = if ("1" == it.liveSource) it.liveKey else if ("1" == it.liveState) it.roomid else it.recordid
                    data.period_type = if (item.state == "4") "回放小节" else "直播小节"
                    data.period_name = item.name
                    data.video_service_provider = if ("1" == it.liveSource) "欢拓" else "cc"
                    when (item.state) {
                        "1" -> { //直播页面
                            if ("1" == it.liveSource) {
                                it.chargeType = item.chargeType ?: ""
                                it.periodId = item.videoKey
                                it.teacher = item.teacher ?: ""
                                startActivity<LiveHTActivity>(
                                    "token" to it.liveToken,
                                    "Lesson" to Gson().toJson(it),
                                    "title" to item.name,
                                    "activityLesson" to Gson().toJson(parent.lessonDetail)
                                )
                            } else if ("2" == it.liveSource) {
                                val loginInfo = LoginInfo()
                                loginInfo.roomId = it.roomid
                                loginInfo.userId = it.userid
                                loginInfo.viewerName = it.viewername
                                loginInfo.viewerToken = it.viewertoken
                                DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
                                    override fun onLogin(templateInfo: TemplateInfo?, viewer: Viewer?, roomInfo: RoomInfo?, publishInfo: PublishInfo?) {
                                        val intent = Intent(this@DayWorkFragment.requireContext(), LiveCCActivity::class.java)
                                        val bundle = Bundle()
                                        bundle.putSerializable("marquee", viewer?.getMarquee())
                                        bundle.putString("title", item.name)
                                        bundle.putSerializable("lesson", it)
                                        bundle.putParcelable("SensorsData", data)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    }

                                    override fun onException(e: DWLiveException?) {
                                        runOnUiThread {
                                            Toast.makeText(this@DayWorkFragment.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }, loginInfo)
                                // 执行登录操作
                                DWLive.getInstance().startLogin()
                            }
                        }
                        "4", "6" -> { //回放页面
                            if ("1" == it.liveSource) {
                                it.chargeType = item.chargeType ?: ""
                                it.periodId = item.videoKey
                                it.teacher = item.teacher ?: ""
                                startActivity<ReplayHTActivity>(
                                    "token" to it.liveToken,
                                    "Lesson" to Gson().toJson(it),
                                    "id" to item.videoKey,
                                    "title" to item.name,
                                    "activityLesson" to Gson().toJson(parent.lessonDetail)
                                )
                            } else if ("2" == it.liveSource) {
                                val replayInfo = ReplayLoginInfo()
                                replayInfo.roomId = it.roomid
                                replayInfo.userId = it.userid
                                replayInfo.liveId = it.liveid
                                replayInfo.recordId = it.recordid
                                replayInfo.viewerName = it.viewername
                                replayInfo.viewerToken = it.viewertoken
                                DWLiveReplay.getInstance().setLoginParams(object : DWLiveReplayLoginListener() {
                                    override fun onException(e: DWLiveException?) {
                                        runOnUiThread {
                                            Toast.makeText(this@DayWorkFragment.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun onLogin(info: TemplateInfo?, marquee: Marquee?) {
                                        val bundle = Bundle()
                                        bundle.putSerializable("marquee", marquee)
                                        bundle.putString("recordId", replayInfo.getRecordId())
                                        bundle.putString("title", item.name)
                                        bundle.putSerializable("lesson", it)
                                        bundle.putParcelable("SensorsData", data)
                                        val intent = Intent(this@DayWorkFragment.requireContext(), ReplayCCActivity::class.java)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    }
                                }, replayInfo)
                                DWLiveReplay.getInstance().startLogin()
                            }
                        }
                        else -> { //没有操作
                            Toast.makeText(this.requireActivity(), "请等待直播开始", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                //0直播未开始不能做 ， 1有作业 ，2已完成作业
                when (item.state) {
                    "0" -> { //没有操作
                        Toast.makeText(this.requireActivity(), "请等待直播开始", Toast.LENGTH_LONG).show()
                    }
                    else -> {//做题列表
                        presenter.getWorks(parent.key, item.videoKey, 1) { list ->
                            if (list.size == 1 && "XUNIMULU_AAAAA_BBBBB" == list[0].catalogName && list[0].resourceList.size == 1) {
                                val itemResource = list[0].resourceList[0]
                                if ("1" != itemResource.isFinish) {
                                    startActivity<ExerciseDetailKActivity>(
                                        ExerciseDetailKActivity.KEY to itemResource.paperKey,
                                        ExerciseDetailKActivity.NAME to itemResource.paperName,
                                        ExerciseDetailKActivity.CAT_KEY to itemResource.videoKey,
                                        ExerciseDetailKActivity.SOURCE to "6",
                                        ExerciseDetailKActivity.USER_PRACTISE_KEY to itemResource.userPracticeKey,
                                    )
                                } else {
                                    presenter.getReport(itemResource.paperKey, itemResource.userPracticeKey) {
                                        startActivity<ReportActivity>(
                                            ReportActivity.DATA to it,
                                            ReportActivity.TIME to "",
                                            ReportActivity.KEY to itemResource.paperKey,
                                            ReportActivity.CAT_KEY to itemResource.videoKey,
                                            ReportActivity.NAME to item.name,
                                            ReportActivity.TYPE to "1",
                                            ReportActivity.EVAL_STATE to "1"
                                        )
                                    }
                                }
                            } else {
                                startActivity<LessonWorksActivity>(
                                    LessonWorksActivity.COURSE_KEY to parent.key,
                                    LessonWorksActivity.VIDEO_KEY to item.videoKey
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    private val recommendAdapter by lazy {
        RecommendAdapter() { pushItem ->
            parent.isDayWorkRefresh = true
            ////推送类型：测评eval 试卷paper 小程序applet
            when (pushItem.targetType) {
                "eval" -> {
                    when {
                        "1" != pushItem.isFinish -> {
                            //测评
                            val intent = Intent(this.requireActivity(), ExerciseDetailKActivity::class.java)
                            intent.putExtra(ExerciseDetailKActivity.KEY, pushItem.paperKey)
                            intent.putExtra(ExerciseDetailKActivity.CAT_KEY, pushItem.targetKey)
                            intent.putExtra(ExerciseDetailKActivity.NAME, pushItem.name)
                            intent.putExtra(ExerciseDetailKActivity.EX_TYPE, ExerciseDetailKActivity.EX_TYPE_PG)
                            intent.putExtra(ExerciseDetailKActivity.TYPE, "10")
                            intent.putExtra(ExerciseDetailKActivity.USER_PRACTISE_KEY, pushItem.userPracticeKey)
                            startActivity(intent)
                        }
                        "1" == pushItem.state -> {
                            //测评报告
                            val intent = Intent(this.requireActivity(), BookReportActivity::class.java)
                            intent.putExtra(BookReportActivity.EVALKEY, pushItem.targetKey)
                            intent.putExtra(BookReportActivity.USERPRACTISEKEY, pushItem.userPracticeKey)
                            intent.putExtra(BookReportActivity.PAPERKEY, pushItem.paperKey)
                            intent.putExtra(BookReportActivity.PAPER_NAME, pushItem.name)
                            startActivity(intent)
                        }
                        else -> {
                            presenter.getReport(pushItem.paperKey, pushItem.userPracticeKey) { report ->
                                //成绩报告
                                startActivity<ReportActivity>(
                                    ReportActivity.KEY to pushItem.paperKey,
                                    ReportActivity.NAME to pushItem.name,
                                    ReportActivity.NEED_UP_LOAD to true,
                                    ReportActivity.CAT_KEY to pushItem.targetKey,
                                    ReportActivity.EVAL_STATE to pushItem.state,
                                    ReportActivity.EX_TYPE to ReportActivity.EX_TYPE_PG,
                                    ReportActivity.TYPE to "10",
                                    ReportActivity.DATA to pushItem
                                )
                            }
                        }
                    }
                }
                "paper" -> {
                    if ("1" != pushItem.isFinish) {
                        val intent = Intent(this.requireActivity(), ExerciseDetailKActivity::class.java)
                        intent.putExtra(ExerciseDetailKActivity.KEY, pushItem.paperKey)
                        intent.putExtra(ExerciseDetailKActivity.CAT_KEY, pushItem.targetKey)
                        intent.putExtra(ExerciseDetailKActivity.NAME, pushItem.name)
                        intent.putExtra(ExerciseDetailKActivity.USER_PRACTISE_KEY, pushItem.userPracticeKey)
                        startActivity(intent)
                    } else {
                        presenter.getReport(pushItem.paperKey, pushItem.userPracticeKey) {
                            startActivity<ReportActivity>(
                                ReportActivity.DATA to it,
                                ReportActivity.TIME to "",
                                ReportActivity.KEY to pushItem.paperKey,
                                ReportActivity.CAT_KEY to pushItem.targetKey,
                                ReportActivity.NAME to pushItem.name,
                                ReportActivity.TYPE to "1",
                                ReportActivity.EVAL_STATE to "1"
                            )
                        }
                    }
                }
                "applet" -> {
                    WxMini.goWxMini(this.requireActivity(), pushItem.targetKey)
                }
                else -> {

                }
            }
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_work.isNestedScrollingEnabled = false
        rlv_recommend.isNestedScrollingEnabled = false
        rlv_work.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_recommend.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_work.adapter = workAdapter
        rlv_recommend.adapter = recommendAdapter
    }

    override fun initData() {
        presenter.getEverydayTask(parent.key) { work ->
            dayWork = work
            workAdapter.setData(work.dayList)
            if (work.pushList.isEmpty()) {
                img_recommend.visibility = View.GONE
                tv_learn.visibility = View.GONE
            } else {
                img_recommend.visibility = View.VISIBLE
                tv_learn.visibility = View.VISIBLE
                recommendAdapter.setData(work.pushList)
            }
            if (work.dayList.isEmpty()) {
                tv_work_num.text = ""
            } else {
                tv_work_num.text = "（${work.finishNum}/${work.dayList.size}）"
            }
            if (work.adTargetImg.isNullOrEmpty()) {
                cdv_banner.visibility = View.GONE
            } else {
                cdv_banner.visibility = View.VISIBLE
                val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                GlideImageLoader.create(img_banner).load(work.adTargetImg, requestOptions)
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        img_banner.setOnClickListener {
            dayWork?.let {
                if ("1" == it.adTargetType && !it.adTargetKey.isNullOrEmpty()) {//广告类型：1网课,2文章
                    saBanner(it, "网课")
                    val intent = Intent(this.requireActivity(), LessonDetailActivity::class.java)
                    intent.putExtra(LessonDetailActivity.KEY, it.adTargetKey)
                    startActivity(intent)
                } else if ("2" == it.adTargetType && "1" == it.adInfType && !it.adTargetKey.isNullOrEmpty()) {
                    saBanner(it, "文章")
                    startActivity<NewsAndAudioActivity>(NewsAndAudioActivity.KEY to it.adTargetKey, NewsAndAudioActivity.TITLE to it.name)
                } else if ("2" == it.adTargetType && "2" == it.adInfType && !it.adInfLink.isNullOrEmpty()) {
                    saBanner(it, "文章")
                    startActivity<WebViewActivity>(WebViewActivity.URL to it.adInfLink, WebViewActivity.TITLE to it.name)
                }
            }
        }
    }

    fun saBanner(item: DayWork, type: String) {
        try {
            val property = JSONObject()
            property.put("advertisement_id", item.adTargetKey)
            property.put("advertisement_name", item.name)
            property.put("location_of_advertisement", "每日任务banner")
            property.put("advertising_sequence", "1号")
            property.put("types_of_advertisement", type)
            SensorsDataAPI.sharedInstance().track("click_advertisement", property)
        } catch (e: Exception) {
        }
    }

    companion object {
        fun instance() = DayWorkFragment().apply {
//            arguments = Bundle().apply {
//                putSerializable(DATA, list)
//            }
        }

        const val DATA = "data"
    }
}

class WorkAdapter(val OnClick: (item: DayWorkItem) -> Unit) : BaseAdapter<DayWorkItem>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: DayWorkItem) {
        holder.setText(R.id.tv_title, item.name)
            .setText(R.id.tv_work_state, if ("1" == item.type) "直播" else "课后作业")
            .setText(R.id.tv_content, if ("1" == item.type) "开课时间  ${item.liveStart}" else "${item.practiceCount}人已完成")
            .setText(R.id.tv_go, getGoStr(item, holder.getView(R.id.tv_go) as TextView))
        holder.setOnClickListener(R.id.tv_go) {
            OnClick(item)
        }
    }

    private fun getGoStr(item: DayWorkItem, tv: TextView): String {
        return if ("1" == item.type) {
            //state 0未开课,1直播中,2 维护中,3已转录,4 可回放,5 未进行直播但已到直播结束时间 6已完成
            when (item.state) {
                "0" -> {
                    tv.isSelected = false
                    "未开课"
                }
                "6" -> {
                    tv.isSelected = true
                    "已完成"
                }
                else -> {
                    tv.isSelected = true
                    "去完成"
                }
            }
        } else {
            //0直播未开始不能做 ， 1有作业 ，2已完成作业
            when (item.state) {
                "1" -> {
                    tv.isSelected = true
                    "去完成"
                }
                "2" -> {
                    tv.isSelected = true
                    "已完成"
                }
                else -> {
                    tv.isSelected = false
                    "未解锁"
                }
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_work_item, parent, false)
    }

    override fun emptyView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_empty_work, parent, false)
    }
}

class RecommendAdapter(val OnClick: (PushItem) -> Unit) : BaseAdapter<PushItem>() {
    override fun convert(holder: ViewHolder, item: PushItem) {
        holder.setText(R.id.tv_title, item.name)
            .setText(R.id.tv_content, if ("applet" == item.targetType) "微信小程序" else "${item.practiseCount}人已完成")
        holder.setOnClickListener(R.id.rl_go) {
            OnClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_recommend_item, parent, false)
    }
}