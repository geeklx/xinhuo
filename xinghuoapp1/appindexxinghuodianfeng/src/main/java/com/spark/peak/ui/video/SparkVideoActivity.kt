package com.spark.peak.ui.video

import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.spark.peak.R
import com.spark.peak.base.BaseActivity
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_spark_video.*
import org.json.JSONObject

class SparkVideoActivity : BaseActivity() {

    override val layoutResId: Int
        get() = R.layout.activity_spark_video

    private val videoUrl by lazy { intent.getStringExtra(URL) ?: "" }
    private val title: String by lazy { intent.getStringExtra(TITLE) ?: "" }

    var progressMap = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        fullScreen = true
        ImmersionBar.with(this).fullScreen(true).hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
            .statusBarColor(android.R.color.transparent).statusBarDarkFont(true).init()
        super.onCreate(savedInstanceState)
    }

    companion object {
        val URL = "video_url"
        val TITLE = "title"
    }


    override fun configView() {
        try {
            var p = SpUtil.videoProgress
            if (p.isNotEmpty()) {
                progressMap = Gson().fromJson(p, object : TypeToken<Map<String, String>>() {}.type)
            }
        } catch (e: Exception) {
            Log.e("videoProgress_catch", e.message.toString())
        }

        initVideo(videoUrl)
    }


    private fun initVideo(videoUrl0: String) {
        var progress = progressMap[videoUrl]?.toInt()
        video_player.lastProgress = progress ?: 0//添加上次观看的进度
        video_player.setUp(videoUrl0, true, title ?: "")

        video_player.setIsTouchWiget(true)
        var ou = OrientationUtils(this, video_player)
        video_player.startWithLand(ou) {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        video_player.onVideoResume()
    }

    override fun onPause() {
        super.onPause()
        video_player.onVideoPause()
        if (isFinishing) {
            saEnd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        var p = video_player.currentPositionWhenPlaying
        progressMap[videoUrl] = p.toString()
        var s = Gson().toJson(progressMap)
        SpUtil.videoProgress = s

        GSYVideoManager.releaseAllVideos()
        video_player.releaseOU()
        video_player.setVideoAllCallBack(null)
    }

    private fun saEnd() {
        try {
            val property = JSONObject(SpUtil.SenorData)
            SensorsDataAPI.sharedInstance().track("df_finish_watch_course_video", property)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
