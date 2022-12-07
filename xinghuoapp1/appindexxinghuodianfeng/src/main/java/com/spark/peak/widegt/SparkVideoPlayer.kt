package com.spark.peak.widegt

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.Debuger

import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.spark.peak.R

class SparkVideoPlayer : NormalGSYVideoPlayer {
    private var speed: TextView? = null
    private var speedF = 1f

    private var ou: OrientationUtils? = null

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag!!) {}

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun init(context: Context) {
        super.init(context)
        speed = findViewById(R.id.speed)

        speed!!.setOnClickListener { changeSpeed() }

        mProgressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val duration = duration
                var currentTime = duration * p1 / 100
                mCurrentTimeTextView.text = CommonUtil.stringForTime(currentTime)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mVideoAllCallBack != null && isCurrentMediaListener) {
                    if (isIfCurrentIsFullscreen) {
                        Debuger.printfLog("onClickSeekbarFullscreen")
                        mVideoAllCallBack.onClickSeekbarFullscreen(mOriginUrl, mTitle, this)
                    } else {
                        Debuger.printfLog("onClickSeekbar")
                        mVideoAllCallBack.onClickSeekbar(mOriginUrl, mTitle, this)
                    }
                }
                if (gsyVideoManager != null && mHadPlay) {
                    try {
                        val time = seekBar!!.progress * duration / 100
                        gsyVideoManager.seekTo(time.toLong())
                    } catch (e: Exception) {
                        Debuger.printfWarning(e.toString())
                    }

                }
            }

        })
    }

    private fun changeSpeed() {
        if (speedF == 1f) {
            speedF = 1.2f
            speed!!.text = "1.2"
        } else if (speedF == 1.2f) {
            speedF = 1.5f
            speed!!.text = "1.5"
        } else if (speedF == 1.5f) {
            speedF = 2f
            speed!!.text = "2.0"
        } else if (speedF == 2f) {
            speedF = 1f
            speed!!.text = "1.0"
        }
        setSpeed(speedF)
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_video_sparkdf
    }

    override fun changeUiToError() {
        super.changeUiToError()
        setViewShowState(mTopContainer, VISIBLE)
    }

    var lastProgress = 0
    override fun onPrepared() {
        super.onPrepared()
        if (lastProgress > 0) {
            seekTo(lastProgress.toLong())
        }
    }

    fun startWithLand(ou: OrientationUtils, click: () -> Unit) {
        startPlayLogic()
        ou.resolveByClick()
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            click()
        }
        fullscreenButton.visibility = View.GONE
    }

    override fun resolveFullVideoShow(context: Context, gsyVideoPlayer: GSYBaseVideoPlayer, frameLayout: FrameLayout) {
        super.resolveFullVideoShow(context, gsyVideoPlayer, frameLayout)
        ou = OrientationUtils(context as Activity, gsyVideoPlayer)
        ou!!.resolveByClick()//设置全屏时 强制设为横屏
    }

    override fun resolveNormalVideoShow(oldF: View?, vp: ViewGroup, gsyVideoPlayer: GSYVideoPlayer?) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
        ou!!.resolveByClick()//退出全屏时 强制设为竖屏
    }

    /**
     * ondestory时调用
     */
    public fun releaseOU(){
        ou?.let {
            it.releaseListener()
        }
    }
}
