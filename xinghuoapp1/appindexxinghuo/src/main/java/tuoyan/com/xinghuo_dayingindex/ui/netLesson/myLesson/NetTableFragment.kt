package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
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
import com.geek.libutils.data.MmkvUtils
import com.google.gson.Gson
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.runOnUiThread
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui._public.ImageActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.LiveCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.LocalReplayCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.ReplayCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.LiveHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.ReplayHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.adapter.LessonItemAdapter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.composition.CompositionDetailWebActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioLessonActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File
import java.io.Serializable
import kotlin.collections.ArrayList

/**
 */
class NetTableFragment() : LifeV4Fragment<NetLessonsPresenter>() {
    override val presenter = NetLessonsPresenter(this)
    override val layoutResId = 0
    private lateinit var rlv: RecyclerView
    val dataList by lazy { requireArguments().getSerializable(DATA) as ArrayList<ResourceListBean> }

    val parent by lazy { activity as MineLessonActivity }

    //    private val liveAdapter by lazy {
//        LessonLivingAdapter({ item, pos ->
//            parent.isLiveRefresh = true
//            goLessonVideo(item, pos)
//        }, {//评论
//            parent.saClick("点击评论")
//            parent.isLiveRefresh = true
//            startActivity<LessonComActivity>(
//                LessonComActivity.P_KEY to parent.key,
//                LessonComActivity.IS_COMMENT to "0",
//                LessonComActivity.KEY to it.id,
//                LessonComActivity.TYPE to "6"
//            )
//        }, {//课后作业
//            parent.saClick("点击作业")
//            parent.isLiveRefresh = true
//            presenter.getWorks(parent.key, it, 1) {
//                startActivity<LessonWorksActivity>(LessonWorksActivity.DATA to (it as ArrayList))
//            }
//        }) {
//            parent.saClick("点击预习")
//            //预习
//            goWorkPre(parent.key, it.id)
//        }
//    }
//1:看课；2:预习；3:评论；4：课后作业
    private val adapter by lazy {
        LessonItemAdapter { type, item, position ->
            when (type) {
                "1" -> {
                    parent.isLiveRefresh = true
                    goLessonVideo(item, position)
                }
                "2" -> {
                    parent.saClick("点击预习")
                    //预习
                    goWorkPre(parent.key, item.id)
                }
                "3" -> {
                    parent.saClick("点击评论")
                    parent.isLiveRefresh = true
                    startActivity<LessonComActivity>(
                        LessonComActivity.P_KEY to parent.key,
                        LessonComActivity.IS_COMMENT to "0",
                        LessonComActivity.KEY to item.id,
                        LessonComActivity.TYPE to "6"
                    )
                }
                "4" -> {
                    parent.saClick("点击作业")
                    parent.isLiveRefresh = true
                    startActivity<LessonWorksActivity>(
                        LessonWorksActivity.COURSE_KEY to parent.key,
                        LessonWorksActivity.VIDEO_KEY to item.id
                    )
                }
            }
        }
    }

    private fun goLessonVideo(item: ResourceListBean, pos: Int) {
        PermissionUtlis.checkPermissions(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) {
            //liveType： 0 录播 1 直播 4 音频课item
            //liveState： "0" -> "<font color='#c7cad2'>未开课</font>"
            //            "5" -> "<font color='#c7cad2'>未上课</font>"
            //            "2" -> "<font color='#ffaf30'>回放生成中</font>"
            //            "1" -> "<font color='#00ca0d'>直播中</font>"
            //            "3" -> "<font color='#00ca0d'>已转录</font>"
            //            "4" -> "<font color='#4c84ff'>观看回放</font>"
            //            "99" -> "<font color='#5467ff'>今日开课</font>"
            parent.saClick("点击看课")
            if ("0" == item.liveType) { //TODO 0 录播 1 直播 4 音频课
                goVideo(item)
            } else if ("4" == item.liveType) {
                goAudio(item, pos)
            } else if ("3" == item.liveType) {
                goExercise(item)
            } else {
                //直播回放
                presenter.getMTCloudUrl(item.id) { netLesson ->
                    val data = getSensData(netLesson, item)
                    if (item.liveState == "4" && "1" == netLesson.liveSource) {
                        goHTPlayBack(netLesson, item)
                    } else if ("2" == netLesson.liveSource && item.liveState == "4") {
                        goCCPlayBack(netLesson, item, data)
                    } else if (item.liveState == "1" && "1" == netLesson.liveSource) {//正在直播中的课程
                        goHTLiving(netLesson, item)
                    } else if ("2" == netLesson.liveSource && item.liveState == "1") {
                        goCCLiving(netLesson, data)
                    }
                }
            }
        }
    }

    private fun getSensData(netLesson: NetLesson, item: ResourceListBean): SensorsData {
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
        data.live_platform_id = if ("1" == netLesson.liveSource) netLesson.liveKey else if ("1" == netLesson.liveState) netLesson.roomid else netLesson.recordid
        data.period_type = if (item.liveState == "4") "回放小节" else "直播小节"
        data.period_name = item.name
        data.video_service_provider = if ("1" == netLesson.liveSource) "欢拓" else "cc"
        return data
    }

    private fun goVideo(item: ResourceListBean) {
        val map = mutableMapOf<String, String>()
        map["courseKey"] = parent.key
        map["videoKey"] = item.id
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

    private fun goAudio(item: ResourceListBean, pos: Int) {
        val map = mutableMapOf<String, String>()
        map.put("courseKey", parent.key)
        map.put("videoKey", item.id)
        //todo 判断是否已经下载，若下载用本地文件,当前播放位置会判断失误
        val title = (activity as MineLessonActivity).lessonDetail?.title
        val netcoursekey = (activity as MineLessonActivity).lessonDetail?.netcoursekey
        val resList = DownloadManager.getDownloadedListByGroupName(title ?: "NO TITLE", netcoursekey ?: "")
        resList.forEach { resource ->
            dataList.forEach { bean ->
                if (bean.id == resource.key || (bean.name == resource.name && bean.type == resource.type)) {
                    val p = DownloadManager.getFilePathWithKey(bean.id, bean.type)
                    val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
                    val info = Gson().fromJson(preferences.getString(p, ""), ResourceInfo::class.java)
                    if (p.isNotEmpty() && File(p).exists() && info != null && info.textContext.isNotBlank()) {
                        //本地存在文件的 标记下载完成状态
                        bean.id = ""
                        bean.playUrl = p
                    }
                }
            }
        }
//        MyApp.instance.resList = dataList
//        MmkvUtils.getInstance().set_common_json2("ResourceListBean",dataList)
        val list1: List<ResourceListBean> = ArrayList()
        val gson1 = Gson()
        val data1 = gson1.toJson(dataList)
        SPUtils.getInstance().put("ResourceListBean", data1)
        val intent = Intent(activity, AudioLessonActivity::class.java)
        intent.putExtra("POSITION", pos)
        intent.putExtra("ClASSKEY", parent.key)
        intent.putExtra("TITLE", title)
        intent.putExtra("FREETYPE", 2)
        startActivity(intent)
    }

    private fun goExercise(item: ResourceListBean) {
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

    private fun goCCLiving(netLesson: NetLesson, data: SensorsData) {
        val loginInfo = LoginInfo()
        loginInfo.roomId = netLesson.roomid
        loginInfo.userId = netLesson.userid
        loginInfo.viewerName = netLesson.viewername
        loginInfo.viewerToken = netLesson.viewertoken
        DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
            override fun onLogin(templateInfo: TemplateInfo?, viewer: Viewer?, roomInfo: RoomInfo?, publishInfo: PublishInfo?) {
                val intent = Intent(this@NetTableFragment.requireContext(), LiveCCActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("marquee", viewer?.getMarquee())
                bundle.putString("title", data.period_name)
                bundle.putSerializable("lesson", netLesson)
                bundle.putParcelable("SensorsData", data)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onException(e: DWLiveException?) {
                runOnUiThread {
                    Toast.makeText(this@NetTableFragment.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }, loginInfo)
        // 执行登录操作
        DWLive.getInstance().startLogin()
    }

    private fun goHTLiving(netLesson: NetLesson, item: ResourceListBean) {
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

    private fun goCCPlayBack(netLesson: NetLesson, item: ResourceListBean, data: SensorsData) {
        //如果本地有资源就本地播放
        val path = DownloadManager.getFilePathWithKey(item)
        if (File(path).exists()) {
            val intent = Intent(this.requireContext(), LocalReplayCCActivity::class.java)
            intent.putExtra("filePath", path)
            intent.putExtra("title", item.name)
            intent.putExtra("lesson", netLesson)
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
                        Toast.makeText(this@NetTableFragment.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onLogin(info: TemplateInfo?, marquee: Marquee?) {
                    val bundle = Bundle()
                    bundle.putSerializable("marquee", marquee)
                    bundle.putString("recordId", replayInfo.getRecordId())
                    bundle.putString("title", item.name)
                    bundle.putSerializable("lesson", netLesson)
                    bundle.putParcelable("SensorsData", data)
                    val intent = Intent(this@NetTableFragment.requireContext(), ReplayCCActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, replayInfo)
            DWLiveReplay.getInstance().startLogin()
        }
    }

    private fun goHTPlayBack(netLesson: NetLesson, item: ResourceListBean) {
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

    private fun goWorkPre(courseKey: String, videoKey: String) {
        presenter.getPrepareList(courseKey, videoKey) { list ->
            parent.isLiveRefresh = true
            if (list.size == 1) {
                goDetail(list[0], courseKey, videoKey)
            } else {
                val intent = Intent(this.requireActivity(), WorkPreListActivity::class.java)
                intent.putExtra(WorkPreListActivity.COURSE_KEY, courseKey)
                intent.putExtra(WorkPreListActivity.VIDEO_KEY, videoKey)
                intent.putExtra(WorkPreListActivity.DATA_LIST, list as Serializable)
                startActivity(intent)
            }
        }
    }

    /**
     *记录网课预习接口 get ，参数courseKey网课主键，vidoKey网课小节主键，courseResourceKey预习主键
     */
    private fun getLearnedPrepare(courseKey: String, videoKey: String, courseResourceKey: String, success: () -> Unit) {
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
            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
                when (item.type) {
                    "1" -> {
                        if ("1" != item.isFinish) {
                            val intent = Intent(this.requireActivity(), ExerciseDetailKActivity::class.java)
                            intent.putExtra(ExerciseDetailKActivity.KEY, item.id)
                            intent.putExtra(ExerciseDetailKActivity.NAME, item.name)
                            intent.putExtra(ExerciseDetailKActivity.CAT_KEY, item.parentKey)
                            intent.putExtra(ExerciseDetailKActivity.NET_COURSE_KEY, courseKey)
                            intent.putExtra(ExerciseDetailKActivity.NET_COURSE_VIDEO_KEY, videoKey)
                            intent.putExtra(ExerciseDetailKActivity.SOURCE, "5")//预习列表中的 试卷、测评，调用对应的提交接口是传source=5 课后作业source=6 其他不传
                            intent.putExtra(ExerciseDetailKActivity.NET_COURSE_RESOURCE_KEY, item.field6)//预习列表中的 试卷、测评，field6 为预习key netCourseResourceKey
                            intent.putExtra(ExerciseDetailKActivity.USER_PRACTISE_KEY, item.userPracticeKey)
                            startActivity(intent)
                        } else {
                            presenter.getReport(item.id, item.userPracticeKey) {
                                startActivity<ReportActivity>(
                                    ReportActivity.DATA to it,
                                    ReportActivity.TIME to "",
                                    ReportActivity.KEY to item.id,
                                    ReportActivity.CAT_KEY to item.parentKey,
                                    ReportActivity.NAME to item.name,
                                    ReportActivity.TYPE to "1",
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
                            startActivity<ImageActivity>(ImageActivity.URL to item.link, ImageActivity.NAME to item.name)
                        }
                    }
                    "5" -> {
                        getLearnedPrepare(courseKey, videoKey, item.field6) {
                            startActivity<WebViewActivity>(WebViewActivity.URL to item.content, WebViewActivity.TITLE to item.name)
                        }
                    }
                    "6" -> {
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
                                presenter.getResourceInfo(item.id, "1.3") { info ->
                                    startActivity<PDFActivity>(
                                        PDFActivity.URL to info.url,
                                        PDFActivity.TITLE to item.name,
                                        PDFActivity.RES_ID to item.id,
                                        PDFActivity.SOURCE_TYPE to "1.3"
                                    )
                                }
                            }
                        }
                    }
                    "7" -> {
//                        presenter.getResourceInfo(item.id, "1.3") { info ->
//                            item.playUrl = info.url
//                            item.downUrl = info.downloadUrl
                        val resList = ArrayList<BookRes>()
                        resList.add(item)
//                            val p = DownloadManager.getFilePathWithKey(item.id, item.type)
//                            if (p.isNotEmpty() && File(p).exists()) {
//                        MyApp.instance.bookres = resList
                        startActivity<AudioActivity>(AudioActivity.RESOURCE_TYPE to "1.3")
//                            } else {
//                                netCheck(null) {
//                                    MyApp.instance.bookres = resList
                        // 存
                        val list1: List<BookRes> = ArrayList()
                        val gson1 = Gson()
                        val data1 = gson1.toJson(resList)
                        SPUtils.getInstance().put("BookRes", data1)
//                                    startActivity<AudioActivity>()
//                                }
//                            }
//                        }
                    }
                    "8" -> {
                        getLearnedPrepare(courseKey, videoKey, item.field6) {
                            startActivity<WebViewActivity>(WebViewActivity.URL to item.link, WebViewActivity.TITLE to item.name)
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
                                "1" -> startActivity<NewsAndAudioActivity>(NewsAndAudioActivity.KEY to item.id, NewsAndAudioActivity.TITLE to item.name)
                                "2" -> startActivity<WebViewActivity>(WebViewActivity.URL to item.field2, WebViewActivity.TITLE to item.name)
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun initView() = UI {
        rlv = recyclerView {
            layoutManager = LinearLayoutManager(context)
            adapter = this@NetTableFragment.adapter
        }
    }.view

    override fun initData() {
        adapter.setHeader(true)
        adapter.setEmpty(true)
        adapter.setFooter(true)
        adapter.setSortType("1")
        adapter.setData(dataList)
    }

    /**
     * 外部刷新网课小节数据
     */
    fun refreshData(list: List<ResourceListBean>) {
        adapter.setData(list)
    }

    companion object {
        fun instance(list: ArrayList<ResourceListBean>) = NetTableFragment().apply {
            arguments = Bundle().apply {
                putSerializable(DATA, list)
            }
        }

        const val DATA = "data"
    }

}