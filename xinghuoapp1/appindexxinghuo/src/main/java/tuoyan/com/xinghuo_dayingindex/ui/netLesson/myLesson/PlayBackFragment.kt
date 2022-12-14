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
 * ????????????
 */
class PlayBackFragment() : LifeV4Fragment<NetLessonsPresenter>() {
    override val presenter = NetLessonsPresenter(this)
    override val layoutResId = 0

    /**
     * ???????????????????????????smoothScrollToPosition??????????????????????????????
     * ????????????????????????????????????scrollBy??????????????????????????????
     * nextPos ?????????????????????????????????
     * mShouldScroll????????????????????? ???????????????
     */
    private var nextPos = 0
    private var mShouldScroll = false
    val key by lazy { arguments?.getString(KEY) ?: "" }
    val parent by lazy { activity as MineLessonActivity }
    private lateinit var dataList: List<CatalogBean>
    private var resourceList = ArrayList<ResourceListBean>()
    val stateDescriptionDialog by lazy {
        context?.let { StateDescriptionDialog(it, "????????????????????????????????????") }
    }
    private val adapter by lazy {
        PlayBackAdapter({
            toLesson(it)
        }, {
            //??????
            goComment(it)
        }, {
            //????????????
            goWork(it.id)
        }, {
            //??????
            goWorkPre(parent.key, it.id)
        })
    }
    private val secAdapter by lazy {
        PlayBackSecAdapter({
            toLesson(it)
        }, {
            //??????
            goComment(it)
        }, {
            //????????????
            goWork(it.id)
        }, {
            //??????
            goWorkPre(parent.key, it.id)
        })
    }
//    private val threeAdapter by lazy {
//        PlayBackThreeAdapter({ item, pos, oldItem ->
//            toLesson(item)
//        }, {
//            //??????
//            goComment(it)
//        }, {
//            //????????????
//            goWork(it)
//        }, {
//            //??????
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
                    //??????
                    goComment(item)
                }
                "4" -> {
                    //????????????
                    goWork(item.id)
                }
            }
        }
    }

    private fun goComment(item: ResourceListBean) {
        parent.saClick("????????????")
        parent.isLivePlayBackRefresh = true
        startActivity<LessonComActivity>(
            LessonComActivity.P_KEY to parent.key,
            LessonComActivity.IS_COMMENT to "0",
            LessonComActivity.KEY to item.id,
            LessonComActivity.TYPE to "6"
        )
    }

    private fun goWork(id: String) {
        parent.saClick("????????????")
        parent.isLivePlayBackRefresh = true
        startActivity<LessonWorksActivity>(
            LessonWorksActivity.COURSE_KEY to parent.key, LessonWorksActivity.VIDEO_KEY to id
        )
    }

    private fun goWorkPre(courseKey: String, videoKey: String) {
        parent.saClick("????????????")
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
     *???????????????????????? get ?????????courseKey???????????????vidoKey?????????????????????courseResourceKey????????????
     */
    private fun getLearnedPrepare(
        courseKey: String, videoKey: String, courseResourceKey: String, success: () -> Unit
    ) {
        presenter.getLearnedPrepare(courseKey, videoKey, courseResourceKey) {
            success()
        }
    }

    /**
     * //????????????type:1:??????,2:????????????,3:??????,4:??????,5:??????,6:??????,7:??????,8:??????,10:??????,11:??????
     * ?????? ?????? ??????
     */
    private fun goDetail(item: BookRes, courseKey: String, videoKey: String) {
        if (item.field5 == "2" && (item.field3 == "0" || item.field3 == "2")) {
            //??????????????????????????????????????????
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
                            )//?????????????????? ???????????????????????????????????????????????????source=5 ????????????source=6 ????????????
                            intent.putExtra(
                                ExerciseDetailKActivity.NET_COURSE_RESOURCE_KEY, item.field6
                            )//?????????????????? ??????????????????field6 ?????????key netCourseResourceKey
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
                        // ???
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
                                    BookReportActivity.PAPERKEY to item.id,//??????key
                                    BookReportActivity.PAPER_NAME to item.name,//????????????
                                    BookReportActivity.USERPRACTISEKEY to item.userPracticeKey,
                                    BookReportActivity.NEED_UP_LOAD to false,
                                    BookReportActivity.ANSWER_TYPE to "0"
                                )
                            }
                        }
                    }
                    "11" -> {
//              field1:  ??????????????????key??????????????????????????? 1???????????? 2????????????
//               field2	:???????????????????????? 0????????? 1????????? 2???????????????????????????
//               field3 ?????????????????????????????? 0????????? 1????????? 2????????????????????????????????????
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

    //liveType??? 0 ?????? 1 ?????? 4 ?????????item 3:??????
    //liveState??? "0" -> "<font color='#c7cad2'>?????????</font>"
    //            "5" -> "<font color='#c7cad2'>?????????</font>"
    //            "2" -> "<font color='#ffaf30'>???????????????</font>"
    //            "1" -> "<font color='#00ca0d'>?????????</font>"
    //            "3" -> "<font color='#00ca0d'>?????????</font>"
    //            "4" -> "<font color='#4c84ff'>????????????</font>"
    //            "99" -> "<font color='#5467ff'>????????????</font>"
    private fun toLesson(item: ResourceListBean) {
        parent.isLivePlayBackRefresh = true
        parent.saClick("????????????")
        PermissionUtlis.checkPermissions(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) {
            when (item.liveType) {
                "0" -> { //TODO 0 ?????? 1 ?????? 4 ?????????   //w???0??????downUrl????????????????????????
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
                        //????????????
                        if (item.liveState == "4" && "1" == netLesson.liveSource) {
                            toPlayBackHT(item, netLesson)
                        } else if ("2" == netLesson.liveSource && item.liveState == "4") {
                            //????????????????????????????????????
                            toPlayBackCC(item, netLesson, data)
                        } else if ("1" == netLesson.liveSource && item.liveState == "1") {//????????????????????????
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
        data.charge_type = if ("0" == item.chargeType) "??????" else "??????"
        data.period_id = item.id
        data.live_platform_id =
            if ("1" == netLesson.liveSource) netLesson.liveKey else if ("1" == netLesson.liveState) netLesson.roomid else netLesson.recordid
        data.period_type = if (item.liveState == "4") "????????????" else "????????????"
        data.period_name = item.name
        data.video_service_provider = if ("1" == netLesson.liveSource) "??????" else "cc"
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
        // ??????????????????
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
            intent.putExtra("isShowDialog", true)//?????????????????????????????????????????????????????????
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
                //??????
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
                //????????????
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
                    //????????????
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
        ////????????????RecycleView??????????????????????????????????????????Position
        var firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        var lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        if (nextPos < firstVisibleItemPosition) {
            //????????????????????????????????????????????????
            rlv_lessons.smoothScrollToPosition(nextPos)
        } else if (nextPos < lastVisibleItemPosition) {
            //???????????????????????????????????????????????????????????????????????????
            rlv_lessons.smoothScrollBy(
                0, rlv_lessons.getChildAt(nextPos - firstVisibleItemPosition).top
            )
        } else {
            //???????????????????????????????????????????????????
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
                //XUNIMULU_AAAAA_BBBBB ??????????????????
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
                //XUNIMULU_AAAAA_BBBBB ??????????????????
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