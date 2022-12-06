package tuoyan.com.xinghuo_dayingindex.ui.main
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.bokecc.sdk.mobile.live.DWLive
import com.bokecc.sdk.mobile.live.DWLiveLoginListener
import com.bokecc.sdk.mobile.live.Exception.DWLiveException
import com.bokecc.sdk.mobile.live.pojo.*
import com.bokecc.sdk.mobile.live.replay.DWLiveReplay
import com.bokecc.sdk.mobile.live.replay.DWLiveReplayLoginListener
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayLoginInfo
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.LessonDetail
import tuoyan.com.xinghuo_dayingindex.bean.NetLesson
import tuoyan.com.xinghuo_dayingindex.bean.SensorsData
import tuoyan.com.xinghuo_dayingindex.ui.cc.LiveCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.ReplayCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.login.LoginActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.LiveHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.ReplayHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

/**
 * 接收到直播课广播时的过渡页面
 */
class ReceiverToViewActivity : LifeActivity<BasePresenter>() {

    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_lesson_jump

    val id by lazy { intent.getStringExtra(ID) ?: "" }
    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .fullScreen(true)
            .statusBarColor(R.color.transparent)
            .statusBarDarkFont(false)
            .init()
        fullScreen = true
        super.onCreate(savedInstanceState)
    }

    override fun initData() {
        super.initData()
        if (SpUtil.isLogin) {
            //请求接口
            getLesson()
        } else {
            startActivityForResult(Intent(this, LoginActivity::class.java), 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (100 == requestCode && RESULT_OK == resultCode) {
            getLesson()
        }
    }

    companion object {
        const val ID = "id"
    }

    private fun getLesson() {
        presenter.getNetVideoInfo(id, { videoData ->
            if ("1" == videoData.isOwn) {
                presenter.getMTCloudUrl(id) { netLesson ->
                    val lessonDetail = LessonDetail()
                    lessonDetail.netcoursekey = videoData.courseKey
                    lessonDetail.title = videoData.title
                    lessonDetail.form = videoData.form
                    lessonDetail.endTime = videoData.endTime
                    lessonDetail.disprice = videoData.disprice ?: "0"
                    lessonDetail.price = videoData.price ?: "0"
                    netLesson.teacher = videoData.teacher
                    netLesson.chargeType = videoData.chargeType
                    netLesson.periodId = id
                    val data = SensorsData()
                    data.course_id = videoData.courseKey
                    data.course_name = videoData.title
                    data.is_live = "1" == videoData.form
                    data.course_validity = videoData.endTime
                    data.original_price = videoData.price ?: "0"
                    data.current_price = videoData.disprice ?: "0"
                    data.internal_name_online_course = videoData.privateName
                    data.teacher_name = videoData.teacher
                    data.charge_type = if ("0" == videoData.chargeType) "免费" else "付费"
                    data.period_id = id
                    data.live_platform_id = if ("1" == netLesson.liveSource) netLesson.liveKey else if ("1" == netLesson.liveState) netLesson.roomid else netLesson.recordid
                    data.period_type = if (netLesson.liveState == "4") "回放小节" else "直播小节"
                    data.period_name = netLesson.name
                    data.video_service_provider = if ("1" == netLesson.liveSource) "欢拓" else "cc"
                    if (netLesson.liveState == "4" && "1" == netLesson.liveSource) {
                        //欢拓回放
                        playBackToHT(netLesson, lessonDetail)
                    } else if ("2" == netLesson.liveSource && netLesson.liveState == "4") {
                        //cc回放
                        playBackToCC(netLesson, data)
                    } else if (netLesson.liveSource == "2") {
                        //cc直播
                        liveToCC(netLesson, data)
                    } else if (netLesson.liveSource == "1") {
                        //欢拓直播
                        liveToHT(netLesson, lessonDetail)
                    }
                    onBackPressed()
                }
            } else {
                startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to videoData.courseKey)
                onBackPressed()
            }
        }) {
            onBackPressed()
        }
    }

    private fun liveToCC(lesson: NetLesson, data: SensorsData) {
        val replayInfo = ReplayLoginInfo()
        replayInfo.roomId = lesson.roomid
        replayInfo.userId = lesson.userid
        replayInfo.liveId = lesson.liveid
        replayInfo.recordId = lesson.recordid
        replayInfo.viewerName = lesson.viewername
        replayInfo.viewerToken = lesson.viewertoken
        DWLiveReplay.getInstance().setLoginParams(object : DWLiveReplayLoginListener() {
            override fun onException(e: DWLiveException?) {
                runOnUiThread {
                    Toast.makeText(this@ReceiverToViewActivity, e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onLogin(info: TemplateInfo?, marquee: Marquee?) {
                val bundle = Bundle()
                bundle.putSerializable("marquee", marquee)
                bundle.putString("recordId", replayInfo.getRecordId())
                bundle.putString("title", lesson.name)
                bundle.putSerializable("lesson", lesson)
                bundle.putParcelable("SensorsData", data)
                val intent = Intent(this@ReceiverToViewActivity, ReplayCCActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }, replayInfo)
        DWLiveReplay.getInstance().startLogin()
    }

    private fun playBackToCC(lesson: NetLesson, data: SensorsData) {
        val loginInfo = LoginInfo()
        loginInfo.roomId = lesson.roomid
        loginInfo.userId = lesson.userid
        loginInfo.viewerName = lesson.viewername
        loginInfo.viewerToken = lesson.viewertoken
        DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
            override fun onLogin(templateInfo: TemplateInfo?, viewer: Viewer?, roomInfo: RoomInfo?, publishInfo: PublishInfo?) {
                val intent = Intent(this@ReceiverToViewActivity, LiveCCActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("marquee", viewer?.getMarquee())
                bundle.putString("title", lesson.name)
                bundle.putSerializable("lesson", lesson)
                bundle.putParcelable("SensorsData", data)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onException(e: DWLiveException?) {
                runOnUiThread {
                    Toast.makeText(this@ReceiverToViewActivity, e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }, loginInfo)
        // 执行登录操作
        DWLive.getInstance().startLogin()
    }

    private fun liveToHT(lesson: NetLesson, lessonDetail: LessonDetail?) {
        val intent = Intent(this@ReceiverToViewActivity, LiveHTActivity::class.java)
        intent.putExtra("token", lesson.liveToken)
        intent.putExtra("Lesson", Gson().toJson(lesson))
        intent.putExtra("title", lesson.name)
        if (lessonDetail != null) {
            intent.putExtra("activityLesson", Gson().toJson(lessonDetail))
        }
        startActivity(intent)
    }

    private fun playBackToHT(lesson: NetLesson, lessonDetail: LessonDetail?) {
        val intent = Intent(this@ReceiverToViewActivity, ReplayHTActivity::class.java)
        intent.putExtra("token", lesson.liveToken)
        intent.putExtra("Lesson", Gson().toJson(lesson))
        intent.putExtra("id", lesson.liveKey)
        intent.putExtra("title", lesson.name)
        if (lessonDetail != null) {
            intent.putExtra("activityLesson", Gson().toJson(lessonDetail))
        }
        startActivity(intent)
    }
}