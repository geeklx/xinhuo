package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video

import android.os.Bundle
import android.os.Handler
import com.geek.libutils.app.BaseApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import kotlinx.android.synthetic.main.activity_video.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.LessonDetail
import tuoyan.com.xinghuo_dayingindex.bean.NetLesson
import tuoyan.com.xinghuo_dayingindex.bean.SensorBook
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.encrypt.FileEnDecryptManager
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.NetLessonsPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.widegt.SparkVideoPlayer
import java.io.File
import java.util.*

open class VideoActivity : EBookLifeActivity<NetLessonsPresenter>() {
    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_video

    private val videoParam by lazy { intent.getSerializableExtra(VIDEO_PARAM) as? VideoParam }
    private var tempFilePath: String? = null

    //    private var startTime = 0L//统计播放进入时间戳
    private val netLesson by lazy { intent.getSerializableExtra(NET_LESSON) as? NetLesson }
    private val activityDetail by lazy { intent.getSerializableExtra(ACTIVITY_LESSON) as? LessonDetail }
    private val sensorBook by lazy { intent.getParcelableExtra(SENSORSBOOK) as? SensorBook }
    private var timer: Timer? = null
    private var hasRecommend = false
    private var speed = 1.0f//当前倍速
    private var stayTime = 1.0f//停留时长
//    private var progressUp = false//进度条是否达标95%以上

    //    private var timeUp = false//看课时长是否达标90%以上
//    private var isUpdata = false//是否提交看完数据
    private var recommendTime = 0//推荐神策时间

    private var progressMap = mutableMapOf<String, String>()
    private var isPrepare = false

    override fun onCreate(savedInstanceState: Bundle?) {
//        ImmersionBar.with(this)
//            .fullScreen(true)
//            .statusBarColor(R.color.transparent)
//            .statusBarDarkFont(true)
//            .hideBar(BarHide.FLAG_HIDE_BAR)
//            .init()
        fullScreen = true
        super.onCreate(savedInstanceState)
    }

    companion object {
        val NET_LESSON = "netLesson"
        val ACTIVITY_LESSON = "activityLesson"
        val SENSORSBOOK = "SensorsBook"
        val VIDEO_PARAM = "VideoParam"
    }


    override fun configView() {
        window.decorView.systemUiVisibility = getSystemUiVisibility(true)
        try {
            val p = SpUtil.videoProgress
            if (p.isNotEmpty()) {
                progressMap = Gson().fromJson(p, object : TypeToken<Map<String, String>>() {}.type)
            }
        } catch (e: Exception) {
        }
        videoParam?.let { video ->
            if (video.offline) {
                startVideo(video.path)
            } else {
                presenter.getResourceInfo(video.key, video.type) {
                    try {
                        recommendTime = it.recommendTime?.toFloat()?.toInt() ?: 0
                    } catch (e: Exception) {
                    }
                    val p = DownloadManager.getFilePathWithKey(video.key, "3")
                    startVideo(
                        if (p.isNotEmpty() && File(p).exists()) {
                            p
                        } else {
                            it.url
                        }
                    )
                }
            }
        }
    }

    private fun startVideo(mUrl: String) {
        if (mUrl.startsWith("http")) {
            initVideo(mUrl)
        } else {
            Thread {
                run {
                    BaseApp.get().runOnUiThread {
                        toast("视频准备中...")
                    }
                    if (FileEnDecryptManager.getInstance().isEncrypt(mUrl)) {
                        tempFilePath = FileEnDecryptManager.getInstance().decryptFile(mUrl)
                    } else {
                        tempFilePath = mUrl
                    }
                    handle.sendEmptyMessage(1)
                }
            }.start()
        }
    }

    var handle = Handler {
        initVideo(tempFilePath ?: "")
        true
    }

    private fun initVideo(videoUrl0: String) {
        val option = GSYVideoOptionBuilder().setDismissControlTime(10000)
        option.setVideoAllCallBack(object : GSYSampleCallBack() {
            override fun onPrepared(url: String?, vararg objects: Any?) {
                super.onPrepared(url, *objects)
                //开始播放
                isPrepare = true
                initTask()
            }

            override fun onAutoComplete(url: String?, vararg objects: Any?) {
                super.onAutoComplete(url, *objects)
                cancelTask()
            }

            override fun onPlayError(url: String?, vararg objects: Any?) {
                super.onPlayError(url, *objects)
                cancelTask()
            }
        }).build(video_player)
        videoParam?.let { video ->
            val progress = progressMap[video.key]?.toInt()
            video_player.lastProgress = progress ?: 0//添加上次观看的进度
        }

        when {
            videoUrl0.startsWith("http") -> {
                video_player.setUp(videoUrl0, true, videoParam?.name ?: "")
            }
            tempFilePath != null -> {
                video_player.setUp(tempFilePath, true, videoParam?.name ?: "")
            }
            else -> {
                toast("未知错误")
                onBackPressed()
            }
        }
        video_player.setIsTouchWiget(true)
        playVideo()
    }

    open fun playVideo() {
        val ou = OrientationUtils(this, video_player)
        video_player.startWithLand(ou) {
            onBackPressed()
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        video_player.setCallback(object : SparkVideoPlayer.OptionCallback {
            override fun callBack(type: Int, cSpeed: Float) {
                when (type) {
                    1 -> {
                        saOption("拖动进度")
                    }
                    2 -> {
                        speed = cSpeed
                        saOption("倍速选择(${cSpeed}x)")
                    }
                    3 -> {
                        saOption("播放/暂停")
                        when (video_player.currentState) {
                            GSYVideoView.CURRENT_STATE_PLAYING -> {
                                initTask()
                            }
                            GSYVideoView.CURRENT_STATE_PAUSE -> {
                                cancelTask()
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        video_player.onVideoResume()
        saStartView()
        if (isPrepare) {
            initTask()
        }
    }

    override fun onPause() {
        super.onPause()
        video_player.onVideoPause()
        saFinishView()
        cancelTask()
        videoParam?.let { video ->
            val p = video_player.currentPositionWhenPlaying
            progressMap[video.key] = p.toString()
            val s = Gson().toJson(progressMap)
            SpUtil.videoProgress = s
        }
    }

    override fun onDestroy() {
        GSYVideoManager.releaseAllVideos()
        video_player.releaseOU()
        video_player.setVideoAllCallBack(null)
        if (tempFilePath != null) {
            Thread {
                FileUtils.deleteFile(tempFilePath!!)
            }.start()
        }
        super.onDestroy()
    }

    private fun saProperty(detail: LessonDetail?, lesson: NetLesson?, title: String?, liveType: String): JSONObject {
        val property = JSONObject()
        detail?.let {
            property.put("course_id", it.netcoursekey)
            property.put("course_name", it.title)
            property.put("is_live", "1" == it.form)
            property.put("course_validity", it.endTime)
            property.put("original_price", it.price ?: "0")
            property.put("current_price", it.disprice ?: "0")
            property.put("internal_name_online_course", it.privateName ?: "")
        }
        lesson?.let {
            property.put("teacher_name", it.teacher)
            property.put("charge_type", if ("0" == it.chargeType) "免费" else "付费")
            property.put("period_id", it.periodId)
        }
        if (detail != null || lesson != null) {
            property.put("source_video_courses", "网课")
        }
        sensorBook?.let {
            property.put("source_video_courses", "图书配套")
            property.put("book_matching_id", it.book_matching_id)
            property.put("book_matching_name", it.book_matching_name)
        }
        property.put("period_type", liveType)
        property.put("period_name", title)
        return property
    }

    private fun saStartView() {
        try {
            val property = saProperty(activityDetail, netLesson, videoParam?.name ?: "", "录播小节")
            SensorsDataAPI.sharedInstance().track("start_watch_course_video", property)
            SensorsDataAPI.sharedInstance().trackTimerStart("finish_watch_course_video")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saFinishView() {
        try {
            val property = saProperty(activityDetail, netLesson, videoParam?.name ?: "", "录播小节")
            SensorsDataAPI.sharedInstance().trackTimerEnd("finish_watch_course_video", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saOption(btnName: String) {
        try {
            val property = saProperty(activityDetail, netLesson, videoParam?.name ?: "", "录播小节")
            property.put("button_name", btnName)
            property.put("operation_type", "播放调节")
            SensorsDataAPI.sharedInstance().trackTimerEnd("watch_click", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saRecommend(time: Int) {
        try {
            val property = saProperty(activityDetail, netLesson, videoParam?.name ?: "", "录播小节")
            property.put("recommend_time", "$time")
            SensorsDataAPI.sharedInstance().track("course_video_recommend", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saDotMinute(time: Int, seekTime: Int, schedule: String) {
        try {
            val property = saProperty(activityDetail, netLesson, videoParam?.name ?: "", "录播小节")
            property.put("dot_minute", "$time")
            property.put("seek_time", "$seekTime")
            property.put("course_speed", "$speed")
            SensorsDataAPI.sharedInstance().track("minute_course_video", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        recordPlayLog(schedule)
    }

    /**
     * 提交看课时长，学习进度
     * type:1 提交60s时长，进度条未达到95%;
     *      2 提交60s时长，进度条达到95%;
     *      3 不提交60s时长，进度条达到95%
     */
    private fun recordPlayLog(type: String) {
        videoParam?.let { video ->
            if (!video.courseKey.isNullOrEmpty() && !video.key.isNullOrEmpty()) {
                val map = mutableMapOf<String, String>()
                map["courseKey"] = video.courseKey
                map["videoKey"] = video.key
                when (type) {
                    "1" -> {
                        map["total"] = "60"
                    }
                    "2" -> {
                        map["total"] = "60"
                        map["schedule"] = "1"//不为空就行
                    }
                    "3" -> {
                        map["schedule"] = "1"//不为空就行
                    }
                }
                presenter.recordPlayLog(map)
            }
        }
    }

    private var oldTimeM = 0
    private var progressUp = false

    private fun initTask() {
        cancelTask()
        if (netLesson != null || activityDetail != null || sensorBook != null) {
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        stayTime += 4.0f * speed
                        val time = video_player.currentPositionWhenPlaying ?: 0
                        val duration = video_player.mDuration//进度>95% 看课时长>90%
                        if (recommendTime > 0 && recommendTime <= time / 1000 && recommendTime + 5 > time / 1000 && !hasRecommend) {
                            hasRecommend = true
                            saRecommend(recommendTime)
                        }
                        val timeM = (stayTime / 60).toInt()
                        if (oldTimeM != timeM) {
                            oldTimeM = timeM
                            saDotMinute(timeM, time / 1000, if (time * 100 / duration > 95) "2" else "1")
                        }
                        if (!progressUp) {
                            progressUp = time * 100 / duration > 95
                            if (progressUp) {
                                //提交看完数据，提交看课时长  不做判断， 前端判断进度条
                                recordPlayLog("3")
                            }
                        }
                    }
                }
            }, 4000, 4000)
        }
    }

    private fun cancelTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }
}
