package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.bokecc.sdk.mobile.live.DWLive
import com.bokecc.sdk.mobile.live.DWLiveLoginListener
import com.bokecc.sdk.mobile.live.Exception.DWLiveException
import com.bokecc.sdk.mobile.live.pojo.*
import com.bokecc.sdk.mobile.live.replay.DWLiveReplay
import com.bokecc.sdk.mobile.live.replay.DWLiveReplayLoginListener
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayLoginInfo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_play_back.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.startActivity
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui._public.ImageActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.LiveCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.LocalReplayCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.ReplayCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.StateDescriptionDialog
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.LiveHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.ReplayHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.adapter.LessonItemAdapter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.composition.CompositionDetailWebActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File
import java.io.Serializable

/**
 * 回访列表
 */
class PlayBackFragment() : LifeV4Fragment<NetLessonsPresenter>() {
    override val presenter = NetLessonsPresenter(this)
    override val layoutResId = 0

    /**
     * 滑动经过两次，首先smoothScrollToPosition，将置顶项显示出来，
     * 然后计算距离顶部的距离用scrollBy完成最后距离的滚动。
     * nextPos 要定位到当前学习的位置
     * mShouldScroll用在滑动监听时 第一次滑动
     */
    private var nextPos = 0
    private var mShouldScroll = false
    val key by lazy { arguments?.getString(KEY) ?: "" }
    val parent by lazy { activity as MineLessonActivity }
    private lateinit var dataList: List<CatalogBean>
    private var resourceList = ArrayList<ResourceListBean>()
    val stateDescriptionDialog by lazy {
        context?.let { StateDescriptionDialog(it, "视频尚未更新，请耐心等待") }
    }
    private val adapter by lazy {
        PlayBackAdapter({
            toLesson(it)
        }, {
            //评论
            goComment(it)
        }, {
            //课后作业
            goWork(it.id)
        }, {
            //预习
            goWorkPre(parent.key, it.id)
        })
    }
    private val secAdapter by lazy {
        PlayBackSecAdapter({
            toLesson(it)
        }, {
            //评论
            goComment(it)
        }, {
            //课后作业
            goWork(it.id)
        }, {
            //预习
            goWorkPre(parent.key, it.id)
        })
    }
//    private val threeAdapter by lazy {
//        PlayBackThreeAdapter({ item, pos, oldItem ->
//            toLesson(item)
//        }, {
//            //评论
//            goComment(it)
//        }, {
//            //课后作业
//            goWork(it)
//        }, {
//            //预习
//            goWorkPre(parent.key, it.id)
//        })
//    }

    private val lessonAdapter by lazy {
        LessonItemAdapter { type, item, position ->
            when (type) {
                "1" -> {
                    toLesson(item)
                }
                "2" -> {
                    goWorkPre(parent.key, item.id)
                }
                "3" -> {
                    //评论
                    goComment(item)
                }
                "4" -> {
                    //课后作业
                    goWork(item.id)
                }
            }
        }
    }

    private fun goComment(item: ResourceListBean) {
        parent.saClick("点击评论")
        parent.isLivePlayBackRefresh = true
        startActivity<LessonComActivity>(
            LessonComActivity.P_KEY to parent.key,
            LessonComActivity.IS_COMMENT to "0",
            LessonComActivity.KEY to item.id,
            LessonComActivity.TYPE to "6"
        )
    }

    private fun goWork(id: String) {
        parent.saClick("点击作业")
        parent.isLivePlayBackRefresh = true
        startActivity<LessonWorksActivity>(
            LessonWorksActivity.COURSE_KEY to parent.key, LessonWorksActivity.VIDEO_KEY to id
        )
    }

    private fun goWorkPre(courseKey: String, videoKey: String) {
        parent.saClick("点击预习")
        presenter.getPrepareList(courseKey, videoKey) {
            parent.isLivePlayBackRefresh = true
            if (it.size == 1) {
                goDetail(it[0], courseKey, videoKey)
            } else {
                val intent = Intent(this.requireActivity(), WorkPreListActivity::class.java)
                intent.putExtra(WorkPreListActivity.COURSE_KEY, courseKey)
                intent.putExtra(WorkPreListActivity.VIDEO_KEY, videoKey)
                intent.putExtra(WorkPreListActivity.DATA_LIST, it as Serializable)
                startActivity(intent)
            }
        }
    }

    /**
     *记录网课预习接口 get ，参数courseKey网课主键，vidoKey网课小节主键，courseResourceKey预习主键
     */
    private fun getLearnedPrepare(
        courseKey: String, videoKey: String, courseResourceKey: String, success: () -> Unit
    ) {
        presenter.getLearnedPrepare(courseKey, videoKey, courseResourceKey) {
            success()
        }
    }

    /**
     * //配套联系type:1:试卷,2:试卷解析,3:视频,4:图片,5:图文,6:文档,7:音频,8:链接,10:测评,11:资讯
     * 预习 数据 传递
     */
    private fun goDetail(item: BookRes, courseKey: String, videoKey: String) {
        if (item.field5 == "2" && (item.field3 == "0" || item.field3 == "2")) {
            //主观题批改中跳转到正在批改中
            startActivity<CompositionDetailWebActivity>(
                CompositionDetailWebActivity.PRACTISE_KEY to item.id,
                CompositionDetailWebActivity.EVAL_KEY to item.field1,
                CompositionDetailWebActivity.TITLE to item.name
            )
        } else {
            PermissionUtlis.checkPermissions(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) {
                when (item.type) {
                    "1" -> {
                        if ("1" != item.isFinish) {
                            val intent =
                                Intent(this.requireActivity(), ExerciseDetailKActivity::class.java)
                            intent.putExtra(ExerciseDetailKActivity.KEY, item.id)
                            intent.putExtra(ExerciseDetailKActivity.NAME, item.name)
                            intent.putExtra(ExerciseDetailKActivity.CAT_KEY, item.parentKey)
                            intent.putExtra(ExerciseDetailKActivity.NET_COURSE_KEY, courseKey)
                            intent.putExtra(ExerciseDetailKActivity.NET_COURSE_VIDEO_KEY, videoKey)
                            intent.putExtra(
                                ExerciseDetailKActivity.SOURCE, "5"
                            )//预习列表中的 试卷、测评，调用对应的提交接口是传source=5 课后作业source=6 其他不传
                            intent.putExtra(
                                ExerciseDetailKActivity.NET_COURSE_RESOURCE_KEY, item.field6
                            )//预习列表中的 试卷、测评，field6 为预习key netCourseResourceKey
                            intent.putExtra(ExerciseDetailKActivity.TYPE, item.type)
                            intent.putExtra(
                                ExerciseDetailKActivity.USER_PRACTISE_KEY, item.userPracticeKey
                            )
                            startActivity(intent)
                        } else {
                            presenter.getReport(item.id, item.userPracticeKey) {
                                startActivity<ReportActivity>(
                                    ReportActivity.DATA to it,
                                    ReportActivity.TIME to "",
                                    ReportActivity.KEY to item.id,
                                    ReportActivity.CAT_KEY to item.parentKey,
                                    ReportActivity.NAME to item.name,
                                    ReportActivity.TYPE to item.type,
                                    ReportActivity.EVAL_STATE to "1"
                                )
                            }
                        }
                    }
                    "3" -> {
                        val videoParam = VideoParam()
                        videoParam.key = item.id
                        videoParam.type = "1.3"
                        startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam)
                    }
                    "4" -> {
                        getLearnedPrepare(courseKey, videoKey, item.field6) {
                            startActivity<ImageActivity>(
                                ImageActivity.URL to item.link, ImageActivity.NAME to item.name
                            )
                        }
                    }
                    "5" -> {
                        getLearnedPrepare(courseKey, videoKey, item.field6) {
                            startActivity<WebViewActivity>(
                                WebViewActivity.URL to item.content,
                                WebViewActivity.TITLE to item.name
                            )
                        }
                    }
                    "6" -> {
                        presenter.getResourceInfo(item.id, "1.3") {
                            val p = DownloadManager.getFilePathWithKey(item.id, item.type)
                            if (p.isNotEmpty() && File(p).exists()) {
                                startActivity<PDFActivity>(
                                    PDFActivity.URL to p,
                                    PDFActivity.TITLE to item.name,
                                    PDFActivity.RES_ID to item.id,
                                    PDFActivity.SOURCE_TYPE to "1.3"
                                )
                            } else {
                                netCheck(null) {
                                    startActivity<PDFActivity>(
                                        PDFActivity.URL to it.url,
                                        PDFActivity.TITLE to item.name,
                                        PDFActivity.RES_ID to item.id,
                                        PDFActivity.SOURCE_TYPE to "1.3"
                                    )
                                }
                            }
                        }
                    }
                    "7" -> {
//                            presenter.getResourceInfo(item.id, "1.3") {
//                                item.playUrl = it.url
//                                item.downUrl = it.downloadUrl
                        val resList = ArrayList<BookRes>()
                        resList.add(item)
//                                val p = DownloadManager.getFilePathWithKey(item.id, item.type)
//                                if (p.isNotEmpty() && File(p).exists()) {
//                                    MyApp.instance.bookres = resList
//                                    startActivity<AudioActivity>()
//                                } else {
//                                    netCheck(null) {
//                        MyApp.instance.bookres = resList
                        // 存
                        val list1: List<BookRes> = ArrayList()
                        val gson1 = Gson()
                        val data1 = gson1.toJson(resList)
                        SPUtils.getInstance().put("BookRes", data1)
                        startActivity<AudioActivity>(AudioActivity.RESOURCE_TYPE to "1.3")
//                                    }
//                                }
//                            }
                    }
                    "8" -> {
                        getLearnedPrepare(courseKey, videoKey, item.field6) {
                            startActivity<WebViewActivity>(
                                WebViewActivity.URL to item.link, WebViewActivity.TITLE to item.name
                            )
                        }
                    }
                    "10" -> {
                        if ("1" != item.isFinish) {
                            startActivity<ExerciseDetailKActivity>(
                                ExerciseDetailKActivity.KEY to item.id,
                                ExerciseDetailKActivity.CAT_KEY to item.field1,
                                ExerciseDetailKActivity.NAME to item.name,
                                ExerciseDetailKActivity.EX_TYPE to ExerciseDetailKActivity.EX_TYPE_PG,
                                ExerciseDetailKActivity.SOURCE to "5",
                                ExerciseDetailKActivity.NET_COURSE_KEY to courseKey,
                                ExerciseDetailKActivity.NET_COURSE_VIDEO_KEY to videoKey,
                                ExerciseDetailKActivity.NET_COURSE_RESOURCE_KEY to item.field6,
                                ExerciseDetailKActivity.TYPE to item.type,
                                ExerciseDetailKActivity.USER_PRACTISE_KEY to item.userPracticeKey
                            )
                        } else {
                            if (item.field3 == "0" || item.field3 == "2") {
                                presenter.getReport(item.id, item.userPracticeKey) { report ->
                                    startActivity<ReportActivity>(
                                        ReportActivity.DATA to report,
                                        ReportActivity.TIME to "",
                                        ReportActivity.KEY to item.id,
                                        ReportActivity.NAME to item.name,
                                        ReportActivity.EX_TYPE to 2,
                                        ReportActivity.TYPE to item.type,
                                        ReportActivity.CAT_KEY to item.field1,
                                        ReportActivity.PRA_KEY to "",
                                        ReportActivity.SP_Q_KEY to "",
                                        ReportActivity.SP_G_NAME to "",
                                        ReportActivity.NEED_UP_LOAD to true,
                                        ReportActivity.EVAL_STATE to "0"
                                    )
                                }
                            } else {
                                startActivity<BookReportActivity>(
                                    BookReportActivity.EVALKEY to item.field1,
                                    BookReportActivity.PAPERKEY to item.id,//试卷key
                                    BookReportActivity.PAPER_NAME to item.name,//试卷名曾
                                    BookReportActivity.USERPRACTISEKEY to item.userPracticeKey,
                                    BookReportActivity.NEED_UP_LOAD to false,
                                    BookReportActivity.ANSWER_TYPE to "0"
                                )
                            }
                        }
                    }
                    "11" -> {
//              field1:  测评时，测评key；资讯时，资讯类型 1文字内容 2外链内容
//               field2	:测评时，阅卷方式 0是自判 1是人工 2可选；资讯时，外链
//               field3 测评时，试卷批改状态 0未批改 1已批改 2批改中；资讯时，文字内容
                        getLearnedPrepare(courseKey, videoKey, item.field6) {
                            when (item.field1) {
                                "1" -> startActivity<NewsAndAudioActivity>(
                                    NewsAndAudioActivity.KEY to item.id,
                                    NewsAndAudioActivity.TITLE to item.name
                                )
                                "2" -> startActivity<WebViewActivity>(
                                    WebViewActivity.URL to item.field2,
                                    WebViewActivity.TITLE to item.name
                                )
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    //liveType： 0 录播 1 直播 4 音频课item 3:测评
    //liveState： "0" -> "<font color='#c7cad2'>未开课</font>"
    //            "5" -> "<font color='#c7cad2'>未上课</font>"
    //            "2" -> "<font color='#ffaf30'>回放生成中</font>"
    //            "1" -> "<font color='#00ca0d'>直播中</font>"
    //            "3" -> "<font color='#00ca0d'>已转录</font>"
    //            "4" -> "<font color='#4c84ff'>观看回放</font>"
    //            "99" -> "<font color='#5467ff'>今日开课</font>"
    private fun toLesson(item: ResourceListBean) {
        parent.isLivePlayBackRefresh = true
        parent.saClick("点击看课")
        PermissionUtlis.checkPermissions(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) {
            when (item.liveType) {
                "0" -> { //TODO 0 录播 1 直播 4 音频课   //w为0并且downUrl为空为待更新状态
                    if (TextUtils.isEmpty(item.downUrl)) {
                        stateDescriptionDialog?.show()
                    } else {
                        toVideo(item)
                    }
                }
                "3" -> {
                    toExercise(item)
                }
                else -> {
                    presenter.getMTCloudUrl(item.id) { netLesson ->
                        val data = getSensorsData(item, netLesson)
                        //直播回放
                        if (item.liveState == "4" && "1" == netLesson.liveSource) {
                            toPlayBackHT(item, netLesson)
                        } else if ("2" == netLesson.liveSource && item.liveState == "4") {
                            //如果本地有资源就本地播放
                            toPlayBackCC(item, netLesson, data)
                        } else if ("1" == netLesson.liveSource && item.liveState == "1") {//正在直播中的课程
                            toLiveHT(item, netLesson)
                        } else if ("2" == netLesson.liveSource && item.liveState == "1") {
                            toLiveCC(item, netLesson, data)
                        }
                    }
                }
            }
        }
    }

    private fun getSensorsData(item: ResourceListBean, netLesson: NetLesson): SensorsData {
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
        data.period_id = item.id
        data.live_platform_id =
            if ("1" == netLesson.liveSource) netLesson.liveKey else if ("1" == netLesson.liveState) netLesson.roomid else netLesson.recordid
        data.period_type = if (item.liveState == "4") "回放小节" else "直播小节"
        data.period_name = item.name
        data.video_service_provider = if ("1" == netLesson.liveSource) "欢拓" else "cc"
        return data
    }

    private fun toLiveCC(item: ResourceListBean, netLesson: NetLesson, data: SensorsData) {
        val loginInfo = LoginInfo()
        loginInfo.roomId = netLesson.roomid
        loginInfo.userId = netLesson.userid
        loginInfo.viewerName = netLesson.viewername
        loginInfo.viewerToken = netLesson.viewertoken
        DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
            override fun onLogin(
                templateInfo: TemplateInfo?,
                viewer: Viewer?,
                roomInfo: RoomInfo?,
                publishInfo: PublishInfo?
            ) {
                val intent =
                    Intent(this@PlayBackFragment.requireContext(), LiveCCActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("marquee", viewer?.getMarquee())
                bundle.putString("title", item.name)
                bundle.putSerializable("lesson", netLesson)
                bundle.putParcelable("SensorsData", data)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onException(e: DWLiveException?) {
                runOnUiThread {
                    Toast.makeText(
                        this@PlayBackFragment.requireContext(),
                        e?.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }, loginInfo)
        // 执行登录操作
        DWLive.getInstance().startLogin()
    }

    private fun toLiveHT(item: ResourceListBean, netLesson: NetLesson) {
        netLesson.chargeType = item.chargeType ?: ""
        netLesson.periodId = item.id
        netLesson.teacher = item.teacher ?: ""
        startActivity<LiveHTActivity>(
            "token" to netLesson.liveToken,
            "Lesson" to Gson().toJson(netLesson),
            "title" to item.name,
            "activityLesson" to Gson().toJson(parent.lessonDetail)
        )
    }

    private fun toPlayBackCC(item: ResourceListBean, netLesson: NetLesson, data: SensorsData) {
        val path = DownloadManager.getFilePathWithKey(item)
        if (File(path).exists()) {
            val intent = Intent(this.requireContext(), LocalReplayCCActivity::class.java)
            intent.putExtra("filePath", path)
            intent.putExtra("title", item.name)
            intent.putExtra("lesson", netLesson)
            intent.putExtra("SensorsData", data)
            intent.putExtra("isShowDialog", true)//在课程详情页面进入到本地播放，需要弹窗
            startActivity(intent)
        } else {
            val replayInfo = ReplayLoginInfo()
            replayInfo.roomId = netLesson.roomid
            replayInfo.userId = netLesson.userid
            replayInfo.liveId = netLesson.liveid
            replayInfo.recordId = netLesson.recordid
            replayInfo.viewerName = netLesson.viewername
            replayInfo.viewerToken = netLesson.viewertoken
            DWLiveReplay.getInstance().setLoginParams(object : DWLiveReplayLoginListener() {
                override fun onException(e: DWLiveException?) {
                    runOnUiThread {
                        Toast.makeText(
                            this@PlayBackFragment.requireContext(),
                            e?.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onLogin(info: TemplateInfo?, marquee: Marquee?) {
                    val bundle = Bundle()
                    bundle.putSerializable("marquee", marquee)
                    bundle.putString("recordId", replayInfo.getRecordId())
                    bundle.putString("title", item.name)
                    bundle.putSerializable("lesson", netLesson)
                    bundle.putParcelable("SensorsData", data)
                    val intent =
                        Intent(this@PlayBackFragment.requireContext(), ReplayCCActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, replayInfo)
            DWLiveReplay.getInstance().startLogin()
        }
    }

    private fun toPlayBackHT(item: ResourceListBean, netLesson: NetLesson) {
        netLesson.chargeType = item.chargeType ?: ""
        netLesson.periodId = item.id
        netLesson.teacher = item.teacher ?: ""
        startActivity<ReplayHTActivity>(
            "token" to netLesson.liveToken,
            "Lesson" to Gson().toJson(netLesson),
            "id" to item.liveKey,
            "title" to item.name,
            "activityLesson" to Gson().toJson(parent.lessonDetail)
        )
    }

    private fun toExercise(item: ResourceListBean) {
        when {
            "1" != item.isFinish -> {
                //测评
                val intent = Intent(this.requireActivity(), ExerciseDetailKActivity::class.java)
                intent.putExtra(ExerciseDetailKActivity.KEY, item.id)
                intent.putExtra(ExerciseDetailKActivity.CAT_KEY, item.field1)
                intent.putExtra(ExerciseDetailKActivity.NAME, item.name)
                intent.putExtra(ExerciseDetailKActivity.NET_COURSE_KEY, parent.key)
                intent.putExtra(ExerciseDetailKActivity.NET_COURSE_VIDEO_KEY, item.field6)
                intent.putExtra(ExerciseDetailKActivity.SOURCE, "7")
                intent.putExtra(ExerciseDetailKActivity.EX_TYPE, ExerciseDetailKActivity.EX_TYPE_PG)
                intent.putExtra(ExerciseDetailKActivity.TYPE, "10")
                intent.putExtra(ExerciseDetailKActivity.USER_PRACTISE_KEY, item.userPracticeKey)
                startActivity(intent)
            }
            "1" == item.field3 -> {
                //测评报告
                val intent = Intent(this.requireActivity(), BookReportActivity::class.java)
                intent.putExtra(BookReportActivity.EVALKEY, item.field1)
                intent.putExtra(BookReportActivity.USERPRACTISEKEY, item.userPracticeKey)
                intent.putExtra(BookReportActivity.PAPERKEY, item.id)
                intent.putExtra(BookReportActivity.PAPER_NAME, item.name)
                startActivity(intent)
            }
            else -> {
                presenter.getReport(item.id, item.userPracticeKey) { report ->
                    report.userpractisekey = item.userPracticeKey
                    //成绩报告
                    startActivity<ReportActivity>(
                        ReportActivity.KEY to item.id,
                        ReportActivity.NAME to item.name,
                        ReportActivity.NEED_UP_LOAD to true,
                        ReportActivity.CAT_KEY to item.field1,
                        ReportActivity.EVAL_STATE to item.field3,
                        ReportActivity.EX_TYPE to ReportActivity.EX_TYPE_PG,
                        ReportActivity.TYPE to "10",
                        ReportActivity.DATA to report
                    )
                }
            }
        }
    }

    private fun toVideo(item: ResourceListBean) {
        val videoParam = VideoParam()
        videoParam.key = item.id
        videoParam.name = item.name
        videoParam.type = "1.2"
        videoParam.courseKey = parent.key
        val netLesson = NetLesson()
        netLesson.teacher = item.teacher ?: ""
        netLesson.periodId = item.id
        netLesson.chargeType = item.chargeType ?: ""
        startActivity<VideoActivity>(
            VideoActivity.VIDEO_PARAM to videoParam,
            VideoActivity.NET_LESSON to netLesson,
            VideoActivity.ACTIVITY_LESSON to parent.lessonDetail
        )
    }

    override fun initView(): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_play_back, null)
    }

    override fun configView(view: View?) {
        rlv_lessons.setHasFixedSize(true)
        rlv_lessons.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_lessons.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (mShouldScroll && newState === RecyclerView.SCROLL_STATE_IDLE) {
                    mShouldScroll = false;
                    val layoutManager = rlv_lessons.layoutManager as LinearLayoutManager
                    var n = nextPos - layoutManager.findFirstVisibleItemPosition()
                    if (n >= 0 && n < rlv_lessons.childCount) {
                        rlv_lessons.smoothScrollBy(0, rlv_lessons.getChildAt(n).top)
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun handleEvent() {
        super.handleEvent()
        rg_play_back.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                R.id.rb_package -> {
                    getListPackage()
                }
                R.id.rb_time -> {
                    getListByTime()
                }
            }
        }
    }

    private fun scrollToNextPos() {
        val layoutManager = rlv_lessons.layoutManager as LinearLayoutManager
        ////获取当前RecycleView屏幕可见的第一项和最后一项的Position
        var firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        var lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        if (nextPos < firstVisibleItemPosition) {
            //要置顶的项在当前显示的第一项之前
            rlv_lessons.smoothScrollToPosition(nextPos)
        } else if (nextPos < lastVisibleItemPosition) {
            //要置顶的项已经在屏幕上显示，计算它离屏幕原点的距离
            rlv_lessons.smoothScrollBy(
                0, rlv_lessons.getChildAt(nextPos - firstVisibleItemPosition).top
            )
        } else {
            //要置顶的项在当前显示的最后一项之后
            mShouldScroll = true;
            rlv_lessons.smoothScrollToPosition(nextPos)
        }
    }

    private fun initRlv() {
        val params = rlv_lessons.layoutParams as ConstraintLayout.LayoutParams
        dataList.let {
            if (it[0].catalogList.size > 0) {
                params.setMargins(0, DeviceUtil.dp2px(this.requireActivity(), 5f).toInt(), 0, 0)
                rlv_lessons.layoutParams = params
                rlv_lessons.adapter = adapter
                adapter.setFooter(true)
                adapter.setData(dataList)
            } else {
                params.setMargins(
                    DeviceUtil.dp2px(this.requireActivity(), 20f).toInt(),
                    DeviceUtil.dp2px(this.requireActivity(), 15f).toInt(),
                    DeviceUtil.dp2px(this.requireActivity(), 20f).toInt(),
                    0
                )
                rlv_lessons.layoutParams = params
                rlv_lessons.adapter = secAdapter
                secAdapter.isSecond(true)
                secAdapter.setFooter(true)
                secAdapter.setData(dataList)
            }
        }
    }

    fun getListPackage() {
        presenter.getPlaybackListByPackage(key) { backBean ->
            dataList = backBean.catalogkList
            if (dataList.isNullOrEmpty() || "XUNIMULU_AAAAA_BBBBB" == dataList[0].catalogName) {
                //XUNIMULU_AAAAA_BBBBB 虚拟目录单层
                rg_play_back.visibility = View.GONE
                getListByTime()
            } else {
                if ("0" == backBean.isHasLastLearn) {
                    if (dataList[0].catalogList.size > 0) {
                        dataList[0].isLastLook = "1"
                        dataList[0].catalogList[0].isLastLook = "1"
                        if (dataList[0].catalogList[0].resourceList.isNotEmpty()) {
                            dataList[0].catalogList[0].resourceList[0].isLastLook = "1"
                        }
                    } else {
                        dataList[0].isLastLook = "1"
                        if (dataList[0].resourceList.isNotEmpty()) {
                            dataList[0].resourceList[0].isLastLook = "1"
                        }
                    }
                }
                initRlv()
//                Handler().postDelayed({
//                    for ((index, item) in dataList.withIndex()) {
//                        if ("1" == item.isLastLook) {
//                            nextPos = index
//                            runOnUiThread {
//                                scrollToNextPos()
//                            }
//                        }
//                    }
//                }, 500)
            }
        }
    }

    fun getListByTime() {
        rlv_lessons?.let {
            val params = rlv_lessons.layoutParams as ConstraintLayout.LayoutParams
            params.setMargins(
                0, DeviceUtil.dp2px(this.requireActivity(), 5f).toInt(), 0, 0
            )
            rlv_lessons.layoutParams = params
            rlv_lessons.adapter = lessonAdapter
//            threeAdapter.setSortType("1")
//            threeAdapter.setEmpty(true)
//            threeAdapter.setFooter(true)
            lessonAdapter.setSortType("1")
            lessonAdapter.setEmpty(true)
            lessonAdapter.setFooter(true)
            presenter.getPlaybackListByTime(key) { list ->
                resourceList = list.playBackList
                if ("0" == list.isHasLastLearn && resourceList.isNotEmpty()) {
                    resourceList[0].isLastLook = "1"
                }
                lessonAdapter.setData(resourceList)
            }
        }
    }

    fun refreshData() {
        if (rg_play_back != null && rg_play_back.visibility == View.VISIBLE) {
            if (rb_package.isChecked) {
                getListPackage()
            } else {
                getListByTime()
            }
        } else {
            getListByTime()
        }
    }

    override fun initData() {
        super.initData()
        presenter.getPlaybackListByPackage(key) { backBean ->
            dataList = backBean.catalogkList
            if (dataList.isNullOrEmpty() || "XUNIMULU_AAAAA_BBBBB" == dataList[0].catalogName) {
                //XUNIMULU_AAAAA_BBBBB 虚拟目录单层
                rg_play_back.visibility = View.GONE
                presenter.getPlaybackListByTime(key) { list ->
                    resourceList = list.playBackList
                    if ("0" == list.isHasLastLearn && resourceList.isNotEmpty()) {
                        resourceList[0].isLastLook = "1"
                    }
                    rlv_lessons.adapter = lessonAdapter
                    lessonAdapter.setSortType("1")
                    lessonAdapter.setEmpty(true)
                    lessonAdapter.setFooter(true)
                    lessonAdapter.setData(resourceList)
                }
            } else {
                if ("0" == backBean.isHasLastLearn) {
                    if (dataList[0].catalogList.size > 0) {
                        dataList[0].isLastLook = "1"
                        dataList[0].catalogList[0].isLastLook = "1"
                        if (dataList[0].catalogList[0].resourceList.isNotEmpty()) {
                            dataList[0].catalogList[0].resourceList[0].isLastLook = "1"
                        }
                    } else {
                        dataList[0].isLastLook = "1"
                        if (dataList[0].resourceList.isNotEmpty()) {
                            dataList[0].resourceList[0].isLastLook = "1"
                        }
                    }
                }
                initRlv()
//                Handler().postDelayed({
//                    for ((index, item) in dataList.withIndex()) {
//                        if ("1" == item.isLastLook) {
//                            nextPos = index
//                            runOnUiThread {
//                                scrollToNextPos()
//                            }
//                        }
//                    }
//                }, 500)
            }
        }
    }

    companion object {
        fun instance(key: String) = PlayBackFragment().apply {
            arguments = Bundle().apply {
                putString(KEY, key)
            }
        }

        const val DATA = "data"
        const val KEY = "key"
    }

}