package tuoyan.com.xinghuo_dayingindex.ui.message

import android.media.AudioManager
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.SeekBar
import com.shuyu.gsyvideoplayer.GSYVideoManager
import kotlinx.android.synthetic.main.activity_news_and_audio.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class NewsAndAudioActivity : LifeActivity<BasePresenter>() {
    private var mediaPlayer: IjkExo2MediaPlayer? = null
    private var isPerpared = false
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_news_and_audio
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private val newsTitle by lazy { intent.getStringExtra(TITLE) ?: "" }

    private var isPlaying = false
    override fun configView() {
        super.configView()
        setSupportActionBar(tb_news_audio)
        tb_news_audio.setNavigationOnClickListener { onBackPressed() }
        initAudioView()
        initHtmlStr()
    }

    private fun initAudioView() {
        playRes()
        img_play_pause.isSelected = true
        tv_current_time.text = "00:00:00"
        tv_total_time.text = "00:00:00"
        seek_bar.progress = 0
    }

    override fun handleEvent() {
        super.handleEvent()
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_current_time.text = time(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isPlaying = mediaPlayer!!.isPlaying
                mediaPlayer?.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mediaPlayer?.seekTo(it.progress.toLong())
                }
                if (isPlaying) {
                    mediaPlayer?.start()
                } else {
                    mediaPlayer?.pause()
                }
            }
        })
        img_play_pause.setOnClickListener {
            playAndPause()
        }
        video.fullscreenButton.setOnClickListener {
            video.startWindowFullscreen(this, false, false)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nslv_news.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                Log.d("scrollX", scrollY.toString() + "==" + oldScrollY.toString())
                if (scrollY > 88) {
                    tv_title.visibility = View.VISIBLE
                } else {
                    tv_title.visibility = View.GONE
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        //点击数量
        presenter.informationPv(key)
        var htmlEle = htmlStr.toString()
        web_view.webChromeClient = object : WebChromeClient() {}
        web_view.webViewClient = object : WebViewClient() {}
        presenter.informationDetail(key) {
            tv_wb_title.text = it["title"].toString()
            tv_title.text = it["title"].toString()
            tv_num.text = "${it["pv"]}人已学习"
            htmlEle = htmlEle.replace("\${res_data}", it["content"].toString())
            web_view.loadDataWithBaseURL(null, htmlEle, "text/html", "utf-8", null)
//            resourceKey
            if (it["resourceKey"].isNullOrEmpty()) {
                rl_bottom.visibility = View.GONE
            } else {
                rl_bottom.visibility = View.VISIBLE
                presenter.getResourceInfo(it["resourceKey"]!!, "") {
                    mediaPlayer?.dataSource = it.url
                    mediaPlayer?.prepareAsync()
                }
            }
            if (it["videoKey"].isNullOrEmpty()) {
                stl_video.visibility = View.GONE
            } else {
                stl_video.visibility = View.VISIBLE//1107797564596605376
                presenter.getResourceInfo(it["videoKey"].toString(), "") { info ->
                    initVideo(info.url, it["title"].toString())
                }
            }

        }
    }

    private fun initVideo(url: String, title: String) {
        video.backButton.visibility = View.GONE
        video.setUp(url, true, title)
        video.setIsTouchWiget(true)
        video.startClick {
            mediaPlayer?.pause()
            img_play_pause.isSelected = true
        }
    }


    override fun onResume() {
        super.onResume()
        video.onVideoResume()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        video.onVideoPause()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        cancelTask()
        GSYVideoManager.releaseAllVideos()
        video.setVideoAllCallBack(null)
    }

    fun time(time: Long): String {
        val i = time / 1000
        val s = i % 60
        val m = i / 60 % 60
        val h = i / 60 / 60 % 24
        return "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}:${if (s < 10) "0$s" else "$s"}"
    }

    fun playRes() {
        if (mediaPlayer == null) {
            mediaPlayer = IjkExo2MediaPlayer(ctx)
        } else {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
            mediaPlayer = IjkExo2MediaPlayer(ctx)
        }
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.isLooping = false
        mediaPlayer!!.setOnCompletionListener { mp ->
//            mp.stop()
//            isPerpared = false
            img_play_pause.isSelected = true
            tv_current_time.text = "00:00:00"
            seek_bar.progress = 0
            mp.seekTo(0)
            mp.pause()
        }

        mediaPlayer!!.setOnErrorListener { player, what, extra ->
            player.stop()
            player.reset()
            isPerpared = false
            true
        }

        mediaPlayer!!.setOnPreparedListener {
            isPerpared = true
            tv_current_time.text = "00:00:00"
            tv_total_time.text = time(it.duration)
            seek_bar.progress = 0
            seek_bar.secondaryProgress = 0
            seek_bar.max = it.duration.toInt()
            initTask()
        }
    }

    fun playAndPause() {
        video.onVideoPause()
        if (isPerpared) {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                img_play_pause.isSelected = true
            } else {
                mediaPlayer?.start()
                img_play_pause.isSelected = false
            }
        } else {
            toast("正在加载，请稍等")
        }
    }

    private val timer by lazy { Timer(true) }

    /**
     * 刷新进度定时器
     */
    fun initTask() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    try {
                        if (mediaPlayer?.isPlaying == true) {
                            val index = mediaPlayer?.currentPosition ?: 0
                            seek_bar.progress = index.toInt()
                            tv_current_time.text = time(index)
                        } else {
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }, 1000, 500)
    }

    fun cancelTask() {
        timer.cancel()
    }

    companion object {
        val KEY = "key"
        val TITLE = "title"
    }

    var htmlStr: StringBuilder = StringBuilder()
    private fun initHtmlStr() {
        val ips: InputStream = resources.assets.open("news_detail.html")
        val reder = BufferedReader(InputStreamReader(ips))
        var str = reder.readLine()

        while (str != null) {
            htmlStr.append(str + "\n")
            str = reder.readLine()
        }
    }
}
