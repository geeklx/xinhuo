package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
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
import com.google.gson.Gson
import org.jetbrains.anko.support.v4.runOnUiThread
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.LiveCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.ReplayCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.StateDescriptionDialog
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.LiveHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.ReplayHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.LessonsPresenter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioLessonActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import kotlin.properties.Delegates

/**
 */
class LessonCatalogFragment : LifeV4Fragment<BasePresenter>() {
    private lateinit var catalogBean: Catalogue
    private val mAdapter by lazy {
        CatalogAdapter {
            goDetail(it)
        }
    }
    private val threeAdapter by lazy {
        CatalogThreeAdapter {
            goDetail(it)
        }
    }
    private val secAdapter by lazy {
        CatalogSecAdapter {
            goDetail(it)
        }
    }
    private val stateDescriptionDialog by lazy {
        context?.let { StateDescriptionDialog(it, "视频尚未更新，请耐心等待") }
    }
    private fun goDetail(res: ResourceListBean) {
        activity.saClick("试看")
        when (res.liveType) {
            "0" -> {//0 录播  1 直播 4音频课
                goVideo(res)
            }
            "4" -> {
                goAudioLesson(res)
            }
            "3" -> {
                goExercise(res)
            }
            else -> {
                presenter.getMTCloudUrl(res.id) { netLesson ->
                    val data = getSensorData(res, netLesson)
                    if (res.liveState == "4" && "1" == netLesson.liveSource) {
                        goReplayHT(res, netLesson)
                    } else if ("2" == netLesson.liveSource && res.liveState == "4") {
                        goReplayCC(netLesson, res, data)
                    } else if (res.liveState == "1" && "1" == netLesson.liveSource) {
                        goLivingHT(res, netLesson)
                    } else if (res.liveState == "1" && "2" == netLesson.liveSource) {
                        goLivingCC(res, netLesson, data)
                    }
                }
            }
        }
    }

    private fun getSensorData(res: ResourceListBean, netLesson: NetLesson): SensorsData {
        val data = SensorsData()
        activity.detail?.let { lessonDetail ->
            data.course_id = lessonDetail.netcoursekey ?: ""
            data.course_name = lessonDetail.title ?: ""
            data.is_live = "1" == lessonDetail.form
            data.course_validity = lessonDetail.endTime
            data.original_price = lessonDetail.price ?: "0"
            data.current_price = lessonDetail.disprice ?: "0"
            data.internal_name_online_course = lessonDetail.privateName
        }
        data.teacher_name = res.teacher ?: ""
        data.charge_type = if ("0" == res.chargeType) "免费" else "付费"
        data.period_id = res.id
        data.live_platform_id = if ("1" == netLesson.liveSource) netLesson.liveKey else if ("1" == netLesson.liveState) netLesson.roomid else netLesson.recordid
        data.period_type = if (res.liveState == "4") "回放小节" else "直播小节"
        data.period_name = res.name
        data.video_service_provider = if ("1" == netLesson.liveSource) "欢拓" else "cc"
        return data
    }

    private fun goVideo(res: ResourceListBean) {
        if (TextUtils.isEmpty(res.downUrl)) {
            stateDescriptionDialog?.show()
            return
     }
        val netLesson = NetLesson()
        netLesson.teacher = res.teacher ?: ""
        netLesson.periodId = res.id
        netLesson.chargeType = res.chargeType ?: ""
        val videoParam = VideoParam()
        videoParam.key = res.id
        videoParam.name = res.name
        videoParam.type = "1.2"
        startActivity<VideoActivity>(
            VideoActivity.VIDEO_PARAM to videoParam,
            VideoActivity.NET_LESSON to netLesson,
            VideoActivity.ACTIVITY_LESSON to activity.detail
        )
    }

    private fun goAudioLesson(res: ResourceListBean) {
        //ResourceListBean,BookRes service中进行转化
        //已购买 未过期 随便进
        //未购买 试听
        if (activity.detail?.isown == "1" && activity.detail?.iseffect == "0") {
//            MyApp.instance.resList = catalogBean.resouceList
            val list1: List<ResourceListBean> = ArrayList()
            val gson1 = Gson()
            val data1 = gson1.toJson(catalogBean.resouceList)
            SPUtils.getInstance().put("ResourceListBean", data1)
            val position = catalogBean.resouceList.indexOf(res)
            val intent = Intent(activity, AudioLessonActivity::class.java)
            intent.putExtra("POSITION", position)
            intent.putExtra("ClASSKEY", "")
            intent.putExtra("TITLE", activity.detail?.title)
            intent.putExtra("FREETYPE", 2)
            startActivity(intent)
        } else if (res.chargeType == "0") {
            //免费试听
//            MyApp.instance.resList = catalogBean.resouceList
            val list1: List<ResourceListBean> = ArrayList()
            val gson1 = Gson()
            val data1 = gson1.toJson(catalogBean.resouceList)
            SPUtils.getInstance().put("ResourceListBean", data1)
            val position = catalogBean.resouceList.indexOf(res)
            val intent = Intent(activity, AudioLessonActivity::class.java)
            intent.putExtra("POSITION", position)
            intent.putExtra("ClASSKEY", "")
            intent.putExtra("TITLE", activity.detail?.title)
            intent.putExtra("FREETYPE", 1)
            startActivity(intent)
        }
    }

    private fun goReplayHT(res: ResourceListBean, netLesson: NetLesson) {
        netLesson.chargeType = res.chargeType ?: ""
        netLesson.periodId = res.id
        netLesson.teacher = res.teacher ?: ""
        startActivity<ReplayHTActivity>(
            "token" to netLesson.liveToken,
            "id" to res.liveKey,
            "Lesson" to Gson().toJson(netLesson),
            "title" to res.name,
            "activityLesson" to Gson().toJson(activity.detail)
        )
    }

    private fun goReplayCC(netLesson: NetLesson, res: ResourceListBean, data: SensorsData) {
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
                    Toast.makeText(this@LessonCatalogFragment.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onLogin(info: TemplateInfo?, marquee: Marquee?) {
                val bundle = Bundle()
                bundle.putSerializable("marquee", marquee)
                bundle.putString("recordId", replayInfo.getRecordId())
                bundle.putString("title", res.name)
                bundle.putSerializable("lesson", netLesson)
                bundle.putParcelable("SensorsData", data)
                val intent = Intent(this@LessonCatalogFragment.requireContext(), ReplayCCActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }, replayInfo)
        DWLiveReplay.getInstance().startLogin()
    }

    private fun goLivingHT(res: ResourceListBean, netLesson: NetLesson) {
        netLesson.chargeType = res.chargeType ?: ""
        netLesson.periodId = res.id
        netLesson.teacher = res.teacher ?: ""
        startActivity<LiveHTActivity>(
            "token" to netLesson.liveToken,
            "Lesson" to Gson().toJson(netLesson),
            "title" to res.name,
            "activityLesson" to Gson().toJson(activity.detail)
        )
    }

    private fun goLivingCC(res: ResourceListBean, netLesson: NetLesson, data: SensorsData) {
        val loginInfo = LoginInfo()
        loginInfo.roomId = netLesson.roomid
        loginInfo.userId = netLesson.userid
        loginInfo.viewerName = netLesson.viewername
        loginInfo.viewerToken = netLesson.viewertoken
        DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
            override fun onLogin(templateInfo: TemplateInfo?, viewer: Viewer?, roomInfo: RoomInfo?, publishInfo: PublishInfo?) {
                val intent = Intent(this@LessonCatalogFragment.requireContext(), LiveCCActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("marquee", viewer?.getMarquee())
                bundle.putString("title", res.name)
                bundle.putSerializable("lesson", netLesson)
                bundle.putParcelable("SensorsData", data)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onException(e: DWLiveException?) {
                runOnUiThread {
                    Toast.makeText(this@LessonCatalogFragment.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }, loginInfo)
        // 执行登录操作
        DWLive.getInstance().startLogin()
    }

    private fun goExercise(res: ResourceListBean) {
        activity.updateCatalog = true
        when {
            "1" != res.isFinish -> {
                //测评,点击了ExerciseDetailKActivity，onResume中更新catalogFragment
                val intent = Intent(this.requireActivity(), ExerciseDetailKActivity::class.java)
                intent.putExtra(ExerciseDetailKActivity.KEY, res.id)
                intent.putExtra(ExerciseDetailKActivity.CAT_KEY, res.field1)
                intent.putExtra(ExerciseDetailKActivity.NAME, res.name)
                intent.putExtra(ExerciseDetailKActivity.NET_COURSE_KEY, activity.key)
                intent.putExtra(ExerciseDetailKActivity.NET_COURSE_VIDEO_KEY, res.field6)
                intent.putExtra(ExerciseDetailKActivity.EX_TYPE, ExerciseDetailKActivity.EX_TYPE_PG)
                intent.putExtra(ExerciseDetailKActivity.TYPE, "10")
                intent.putExtra(ExerciseDetailKActivity.USER_PRACTISE_KEY, res.userPracticeKey)
                startActivity(intent)
            }
            "1" == res.field3 -> {
                //测评报告
                val intent = Intent(this.requireActivity(), BookReportActivity::class.java)
                intent.putExtra(BookReportActivity.EVALKEY, res.field1)
                intent.putExtra(BookReportActivity.USERPRACTISEKEY, res.userPracticeKey)
                intent.putExtra(BookReportActivity.PAPERKEY, res.id)
                intent.putExtra(BookReportActivity.PAPER_NAME, res.name)
                startActivity(intent)
            }
            else -> {
                presenter.getReport(
                    res.id,
                    res.userPracticeKey
                ) { report ->
                    report.userpractisekey = res.userPracticeKey
                    //成绩报告
                    startActivity<ReportActivity>(
                        ReportActivity.KEY to res.id,
                        ReportActivity.NAME to res.name,
                        ReportActivity.NEED_UP_LOAD to true,
                        ReportActivity.CAT_KEY to res.field1,
                        ReportActivity.EVAL_STATE to res.field3,
                        ReportActivity.EX_TYPE to ReportActivity.EX_TYPE_PG,
                        ReportActivity.TYPE to "10",
                        ReportActivity.DATA to report
                    )
                }
            }
        }
    }

    override val presenter: LessonsPresenter
        get() = LessonsPresenter(this)
    override val layoutResId: Int
        get() = 0

    private var rvCatalog by Delegates.notNull<RecyclerView>()

    //    val dataList by lazy { arguments!!.getSerializable(LIST) as ArrayList<ResourceListBean> }
    private val key by lazy { arguments?.getString(KEY) ?: "" }
    private val activity by lazy { this.requireActivity() as LessonDetailActivity }

    companion object {
        //        const val LIST = "list"
        const val KEY = "key"
        fun newInstance(key: String): LessonCatalogFragment {
            var f = LessonCatalogFragment()
            var arg = Bundle()
//            arg.putSerializable(LIST, content)
            arg.putString(KEY, key)
            f.arguments = arg
            return f
        }
    }

    override fun initView(): View? {
        val frameLayout = FrameLayout(this.requireActivity())
        rvCatalog = RecyclerView(this.requireActivity())
        frameLayout.addView(rvCatalog)
        return frameLayout
    }


    override fun configView(view: View?) {
        super.configView(view)
        rvCatalog.layoutManager = LinearLayoutManager(activity)
        rvCatalog.setHasFixedSize(true)
    }

    override fun initData() {
        presenter.getCatalogue(key) { catalogue ->
            catalogBean = catalogue
            initRlv()
        }
    }

    private fun initRlv() {
        when {
            catalogBean.catalogList.isNullOrEmpty() || "XUNIMULU_AAAAA_BBBBB" == catalogBean.catalogList[0].catalogName -> {
                //单层 XUNIMULU_AAAAA_BBBBB 虚拟目录
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(
                    DeviceUtil.dp2px(this.requireActivity(), 15f).toInt(),
                    0,
                    DeviceUtil.dp2px(this.requireActivity(), 20f).toInt(),
                    0
                )
                rvCatalog.layoutParams = params
                rvCatalog.adapter = threeAdapter
                threeAdapter.setEmpty(true)
                threeAdapter.isOne(true)
                threeAdapter.setFooter(true)
                threeAdapter.setData(catalogBean.resouceList)
            }
            catalogBean.catalogList[0].catalogList.isNullOrEmpty() -> {
                //两层
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(
                    DeviceUtil.dp2px(this.requireActivity(), 20f).toInt(),
                    DeviceUtil.dp2px(this.requireActivity(), 2f).toInt(),
                    DeviceUtil.dp2px(this.requireActivity(), 20f).toInt(),
                    0
                )
                rvCatalog.layoutParams = params
                rvCatalog.adapter = secAdapter
                secAdapter.setEmpty(true)
                secAdapter.setFooter(true)
                secAdapter.setData(catalogBean.catalogList)
            }
            else -> {
                //三层
                rvCatalog.adapter = mAdapter
                mAdapter.setEmpty(true)
                mAdapter.setFooter(true)
                mAdapter.setData(catalogBean.catalogList)
            }
        }
    }
}
