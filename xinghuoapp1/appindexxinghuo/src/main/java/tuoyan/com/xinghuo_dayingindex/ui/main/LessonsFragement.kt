package tuoyan.com.xinghuo_dayingindex.ui.main
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bokecc.sdk.mobile.live.DWLive
import com.bokecc.sdk.mobile.live.DWLiveLoginListener
import com.bokecc.sdk.mobile.live.Exception.DWLiveException
import com.bokecc.sdk.mobile.live.pojo.*
import com.bokecc.sdk.mobile.live.replay.DWLiveReplay
import com.bokecc.sdk.mobile.live.replay.DWLiveReplayLoginListener
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayLoginInfo
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.sensorsdata.analytics.android.sdk.SensorsDataFragmentTitle
import kotlinx.android.synthetic.main.fragment_lessons.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EventMsg
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.LessonDetail
import tuoyan.com.xinghuo_dayingindex.bean.NetLesson
import tuoyan.com.xinghuo_dayingindex.bean.SensorsData
import tuoyan.com.xinghuo_dayingindex.bean.TodyLesson
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.LiveCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.ReplayCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.LiveHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.ReplayHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.MineLessonActivity
import tuoyan.com.xinghuo_dayingindex.ui.study.StudyPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

@SensorsDataFragmentTitle(title = "网课")
class LessonsFragement : LifeV4Fragment<StudyPresenter>() {
    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_lessons

    private val todayAdapter by lazy {
        TodayLessonAdapter { todayLesson ->
            isLogin {
                PermissionUtlis.checkPermissions(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) {
                    presenter.getMTCloudUrl(todayLesson.id) { netLesson ->
                        presenter.getNetVideoInfo(todayLesson.id, { videoData ->
                            val lessonDetail = LessonDetail()
                            lessonDetail.netcoursekey = videoData.courseKey
                            lessonDetail.title = videoData.title
                            lessonDetail.form = videoData.form
                            lessonDetail.endTime = videoData.endTime
                            lessonDetail.disprice = videoData.disprice ?: "0"
                            lessonDetail.price = videoData.price ?: "0"
                            lessonDetail.privateName = videoData.privateName
                            netLesson.teacher = videoData.teacher
                            netLesson.chargeType = videoData.chargeType
                            netLesson.periodId = todayLesson.id
                            goVideo(todayLesson, netLesson, lessonDetail)
                        }) {
                            goVideo(todayLesson, netLesson)
                        }
                    }
                }
            }
        }
    }

    private fun goVideo(todayLesson: TodyLesson, lesson: NetLesson) {
        goVideo(todayLesson, lesson, null)
    }

    private fun goVideo(todayLesson: TodyLesson, lesson: NetLesson, lessonDetail: LessonDetail?) {
        lesson.chargeType = todayLesson.chargeType ?: ""
        lesson.periodId = todayLesson.id
        lesson.teacher = todayLesson.teacher ?: ""

        val data = SensorsData()
        data.course_id = lessonDetail?.netcoursekey ?: ""
        data.course_name = lessonDetail?.title ?: ""
        data.is_live = "1" == lessonDetail?.form
        data.course_validity = lessonDetail?.endTime ?: ""
        data.original_price = lessonDetail?.price ?: "0"
        data.current_price = lessonDetail?.disprice ?: "0"
        data.internal_name_online_course = lessonDetail?.privateName ?: ""
        data.teacher_name = todayLesson.teacher ?: ""
        data.charge_type = if ("0" == todayLesson.chargeType) "免费" else "付费"
        data.period_id = todayLesson.id
        data.live_platform_id = if ("1" == lesson.liveSource) lesson.liveKey else if ("1" == lesson.liveState) lesson.roomid else lesson.recordid
        data.period_type = if (lesson.liveState == "4") "回放小节" else "直播小节"
        data.period_name = todayLesson.name
        data.video_service_provider = if ("1" == lesson.liveSource) "欢拓" else "cc"

        if (todayLesson.liveState == "4" && "1" == lesson.liveSource) {
//            startActivity<PlaybackNativeActivity>(
//                "token" to lesson.liveToken,
//                "Lesson" to Gson().toJson(lesson),
//                "id" to todayLesson.liveKey,
//                "title" to todayLesson.name,
//                "activityLesson" to Gson().toJson(lessonDetail)
//            )
            val intent = Intent(this.requireContext(), ReplayHTActivity::class.java)
            intent.putExtra("token", lesson.liveToken)
            intent.putExtra("Lesson", Gson().toJson(lesson))
            intent.putExtra("id", todayLesson.liveKey)
            intent.putExtra("title", todayLesson.name)
            if (lessonDetail != null) {
                intent.putExtra("activityLesson", Gson().toJson(lessonDetail))
            }
            startActivity(intent)
        } else if (todayLesson.liveState == "4" && "2" == lesson.liveSource) {
            playBackToCC(todayLesson.name, lesson, data)
        } else if (todayLesson.liveState == "1" && "1" == lesson.liveSource) {
//            startActivity<LiveNativeActivity>(
//                "token" to lesson.liveToken,
//                "Lesson" to Gson().toJson(lesson),
//                "title" to todayLesson.name,
//                "activityLesson" to Gson().toJson(lessonDetail)
//            )
            val intent = Intent(this.requireContext(), LiveHTActivity::class.java)
            intent.putExtra("token", lesson.liveToken)
            intent.putExtra("Lesson", Gson().toJson(lesson))
            intent.putExtra("title", todayLesson.name)
            if (lessonDetail != null) {
                intent.putExtra("activityLesson", Gson().toJson(lessonDetail))
            }
            startActivity(intent)
        } else if (todayLesson.liveState == "1" && "2" == lesson.liveSource) {
            liveToCC(todayLesson.name, lesson, data)
        }
    }

    private fun playBackToCC(name: String, lesson: NetLesson, data: SensorsData) {
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
                    Toast.makeText(this@LessonsFragement.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onLogin(info: TemplateInfo?, marquee: Marquee?) {
                val bundle = Bundle()
                bundle.putSerializable("marquee", marquee)
                bundle.putString("recordId", replayInfo.getRecordId())
                bundle.putString("title", name)
                bundle.putSerializable("lesson", lesson)
                bundle.putParcelable("SensorsData", data)
                val intent = Intent(this@LessonsFragement.requireContext(), ReplayCCActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }, replayInfo)
        DWLiveReplay.getInstance().startLogin()
    }

    private fun liveToCC(name: String, lesson: NetLesson, data: SensorsData) {
        val loginInfo = LoginInfo()
        loginInfo.roomId = lesson.roomid
        loginInfo.userId = lesson.userid
        loginInfo.viewerName = lesson.viewername
        loginInfo.viewerToken = lesson.viewertoken
        DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
            override fun onLogin(templateInfo: TemplateInfo?, viewer: Viewer?, roomInfo: RoomInfo?, publishInfo: PublishInfo?) {
                val intent = Intent(this@LessonsFragement.requireContext(), LiveCCActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("marquee", viewer?.getMarquee())
                bundle.putString("title", name)
                bundle.putSerializable("lesson", lesson)
                bundle.putParcelable("SensorsData", data)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onException(e: DWLiveException?) {
                runOnUiThread {
                    Toast.makeText(this@LessonsFragement.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }, loginInfo)
        // 执行登录操作
        DWLive.getInstance().startLogin()
    }

    private val netClassAdapter by lazy {
        NetClassAdapter({ lesson, type ->
            isLogin {
                if (1 == type) {
                    startActivity<PostActivity>(PostActivity.URL to "preferential/proClassScore?id=${lesson.key}")
                } else if (lesson.iseffect != "1" && type == 0) {
                    startActivity<MineLessonActivity>(MineLessonActivity.KEY to lesson.key)
                } else {
                    toast("该网课已过期")
                }
            }
        }, { key, position ->
            isLogin {
                AlertDialog.Builder(context).setMessage("确定要删除该网课吗？").setPositiveButton("确定") { _, _ ->
                    postDele(key, position)
                }.setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
            }
        })
    }

    override fun configView(view: View?) {
        super.configView(view)
        srfl.setColorSchemeResources(R.color.color_1482ff)
        var manager = LinearLayoutManager(activity)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        rlv_today_class.layoutManager = manager
        rlv_today_class.adapter = todayAdapter

        rlv_class.layoutManager = LinearLayoutManager(activity)
        rlv_class.adapter = netClassAdapter
    }

    override fun handleEvent() {
        super.handleEvent()
        srfl.setOnRefreshListener {
            srfl.isRefreshing = false
            isLogin({
                refreshData()
                EventBus.getDefault().post(EventMsg("onRefresh", 0))
            }) {
                srfl.isRefreshing = false
            }
        }
        abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            srfl.isEnabled = verticalOffset >= 0
        })
        tv_sub.setOnClickListener {
            isLogin {
                val code = et_code.text.toString().trim()
                if (code.isEmpty()) {
                    mToast("请输入兑换码")
                } else {
                    val map = HashMap<String, String>()
                    map["code"] = code
                    map["type"] = "0"
                    presenter.activedGoods(map) {
                        toast("兑换成功")
                        et_code.setText("")
                        refreshData()
                    }
                }
            }
        }
        tv_edit.setOnClickListener {
            netClassAdapter.showDelete = !netClassAdapter.showDelete
            netClassAdapter.notifyDataSetChanged()
            if (netClassAdapter.showDelete) {
                tv_edit.text = "完成"
            } else {
                tv_edit.text = "管理"
            }
            tv_edit.isSelected = netClassAdapter.showDelete
        }
    }

    override fun onResume() {
        super.onResume()
        if (SpUtil.isLogin) {
            refreshData()
        }
    }

    override fun initData() {
        super.initData()
    }

    private fun refreshData() {
        presenter.getMyLessons({
            srfl.isRefreshing = false
        }) {
            netClassAdapter.setData(it)
            tv_all_class.text = "（" + it.size + "）"
            srfl.isRefreshing = false
        }
        presenter.getTodayLesson {
            todayAdapter.setData(it)
        }
    }

    fun postDele(key: String, position: Int) {
        var map = HashMap<String, String>()
        map.put("key", key)
        presenter.deleteMyNetClass(map) {
            toast(it)
            netClassAdapter.remove(position)
        }
    }
}