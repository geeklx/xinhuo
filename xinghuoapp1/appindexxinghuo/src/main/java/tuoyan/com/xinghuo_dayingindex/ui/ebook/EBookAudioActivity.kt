package tuoyan.com.xinghuo_dayingindex.ui.ebook

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_ebook_audio.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.LrcRow
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.NetLessonsPresenter
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioService
import tuoyan.com.xinghuo_dayingindex.ui.video.adapter.EbookAudioAdapter
import tuoyan.com.xinghuo_dayingindex.utlis.IRowsParser
import tuoyan.com.xinghuo_dayingindex.utlis.MyLrcDataBuilder
import tuoyan.com.xinghuo_dayingindex.widegt.LinearTopSmoothScroller

class EBookAudioActivity : EBookLifeActivity<NetLessonsPresenter>(), ServiceConnection {
    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_audio

    private var service: AudioService? = null
    private var speed = 1.0
    private val wifiLock by lazy { (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock") }
    private val wakeLock by lazy { (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelocktag") }
    private var currentRes: BookRes? = null
    private val audioDatas by lazy { intent.getSerializableExtra(AUDIO_DATAS) as? List<BookRes> }
    private val lrcRows by lazy { mutableListOf<LrcRow>() }
    private var duration = 0L
    private var startTime = 0L//单句循环开始时间
    private var nextTime = 0L//单句循环结束时间

    private var isTouch = false//中英文字母是否在触摸状态，是 不滚动，否 滚动
    private val enChAdapter by lazy {
        EbookAudioAdapter {
            service?.setProgress(it.currentRowTime)
            if (lrcRows.isNotEmpty()) {
                val currentLrcRowPos = lrcRows.indexOf(it)
                startTime = it.currentRowTime
                nextTime = if (currentLrcRowPos + 1 < lrcRows.size) {
                    lrcRows[currentLrcRowPos + 1].currentRowTime
                } else {
                    duration
                }
            }
        }
    }
    private val parser by lazy {
        object : IRowsParser {
            override fun parse(lrcRowDada: String): MutableList<LrcRow> {
                try {
                    val lastIndexOfRightBracket = lrcRowDada.indexOf("]")
                    val content = lrcRowDada.substring(lastIndexOfRightBracket + 1, lrcRowDada.length)
                    val times = lrcRowDada.substring(0, lastIndexOfRightBracket + 1).replace("[", "-").replace("]", "-")
                    val arrTimes = times.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val listTimes = mutableListOf<LrcRow>()
                    val var10 = arrTimes.size
                    for (var11 in 0 until var10) {
                        val temp = arrTimes[var11]
                        if (temp.trim { it <= ' ' }.isNotEmpty()) {
                            val pre = temp.split(",")
                            val lrcRow = LrcRow(content, pre[0], this.timeConvert(pre[0]))
                            listTimes.add(lrcRow)
                        }
                    }
                    return listTimes
                } catch (e: Exception) {
                    return mutableListOf()
                }
            }

            private fun timeConvert(timeString: String): Long {
                var timeStr = timeString
                timeStr = timeStr.replace('.', ':')
                val times = timeStr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return when (times.size) {
                    4 -> (Integer.valueOf(times[0]) * 60 * 60 * 1000 + Integer.valueOf(times[1]) * 60 * 1000 + Integer.valueOf(times[2]) * 1000 + Integer.valueOf(times[3])).toLong()
                    3 -> (Integer.valueOf(times[0]) * 60 * 1000 + Integer.valueOf(times[1]) * 1000 + Integer.valueOf(times[2])).toLong()
                    2 -> (Integer.valueOf(times[0]) * 60 * 1000 + Integer.valueOf(times[1]) * 1000).toLong()
                    1 -> (Integer.valueOf(times[0]) * 1000).toLong()
                    else -> 0
                }
            }

        }
    }

    override fun configView() {
        super.configView()
        EventBus.getDefault().register(this)
        val intent = Intent(this, AudioService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        wifiLock.acquire()
        wakeLock.acquire()
        rlv_ch_en.layoutManager = LinearLayoutManager(this)
        rlv_ch_en.adapter = enChAdapter
    }

    override fun initData() {
        super.initData()
    }

    override fun handleEvent() {
        super.handleEvent()
        val handler = Handler()
        val runnable = Runnable {
            isTouch = false
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
        img_model.setOnClickListener {
            if (lrcRows.isNotEmpty()) {
                img_model.isSelected = !img_model.isSelected
                val currentLrcRowPos = if (enChAdapter.currentPos < 1) {
                    0
                } else {
                    enChAdapter.currentPos - 1
                }
                startTime = lrcRows[currentLrcRowPos].currentRowTime
                nextTime = if (currentLrcRowPos + 1 < lrcRows.size) {
                    lrcRows[currentLrcRowPos + 1].currentRowTime
                } else {
                    duration
                }
            }
        }
        img_play.setOnClickListener {
            service?.play()
        }
        img_speed.setOnClickListener {
            when (speed) {
                1.0 -> {
                    img_speed.setImageResource(R.mipmap.icon_speed_12)
                    speed = 1.2
                }
                1.2 -> {
                    img_speed.setImageResource(R.mipmap.icon_speed_15)
                    speed = 1.5
                }
                1.5 -> {
                    img_speed.setImageResource(R.mipmap.icon_speed_20)
                    speed = 2.0
                }
                else -> {
                    img_speed.setImageResource(R.mipmap.icon_speed_10)
                    speed = 1.0
                }
            }
            speed(speed.toFloat())
        }
        rlv_ch_en.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        isTouch = true
                        handler.removeCallbacks(runnable)
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        handler.postDelayed(runnable, 3000)
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    service?.setProgress(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is AudioService.MBinder) {
            if (audioDatas == null || audioDatas!!.isEmpty()) return
            this.service = service.getService()
            this.service?.setData(audioDatas!!, 0)
        }
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        service?.unCallBack()
    }

    fun speed(speed: Float) {
        service?.speed(speed)
    }

    fun play(v: View) {
        service?.play()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun callBacks(event: AudioCallBack) {
        this.service ?: return
        when (event.type) {
            AudioCallBack.TYPE_COMPLETE -> {
                if (this.service?.isClose == true) onBackPressed()
            }
            AudioCallBack.TYPE_ERROR -> {
                mToast(event.msg)
            }
            AudioCallBack.TYPE_START -> {
                start(event.time, event.position)
            }
            AudioCallBack.TYPE_STOP -> {
            }
            AudioCallBack.TYPE_STATUS -> {
                img_play.isSelected = event.flag
            }
            AudioCallBack.TYPE_CALLBACK -> {
                if (img_model.isSelected && event.time + 500 >= nextTime) {
                    service?.setProgress(startTime)
                } else {
                    seek_bar.progress = event.time.toInt()
                    tv_time.text = time(event.time)
                    kotlin.run breaking@{
                        lrcRows.forEachIndexed { index, lrcRow ->
                            val isTrue = lrcRows.size - 1 == index && lrcRow.currentRowTime <= event.time || (lrcRows.size - 1 > index && lrcRow.currentRowTime <= event.time && lrcRows[index + 1].currentRowTime > event.time)
                            if (isTrue) {
                                enChAdapter.currentPos = index + 1
                                val layoutManager = rlv_ch_en.layoutManager as? LinearLayoutManager
                                val firstPos = layoutManager?.findFirstCompletelyVisibleItemPosition() ?: 0
                                val lastPos = layoutManager?.findLastCompletelyVisibleItemPosition() ?: 0
                                if ((firstPos > index + 1 || lastPos < index + 1) && !isTouch) {
                                    val smoothScroller = LinearTopSmoothScroller(this@EBookAudioActivity)
                                    smoothScroller.targetPosition = index + 1
                                    layoutManager?.startSmoothScroll(smoothScroller)
                                }
                                return@breaking
                            }
                        }
                    }
                }
            }
            AudioCallBack.TYPE_RES_INFO -> {
                presenter.getResourceInfo(event.key, "3") {
                    event.function(it)
                }
            }
            AudioCallBack.TYPE_NET_ERROR -> {
            }
        }
    }

    fun start(time: Long, position: Int) {
        duration = time
        tv_duration.text = time(time)
        tv_time.text = "00:00"
        seek_bar.progress = 0
        seek_bar.secondaryProgress = 0
        seek_bar.max = time.toInt()
        currentRes = audioDatas?.get(position)
        currentRes?.let { bookRes ->
            if (bookRes.ebookLrc.isNullOrEmpty()) {

            } else {
                lrcRows.clear()
                presenter.getLrc(bookRes.ebookLrc ?: "", { data ->
                    lrcRows.addAll(MyLrcDataBuilder().Build(data, parser))
                    enChAdapter.setData(lrcRows)
                }) {

                }
            }
        }
    }

    /**
     * 格式化音频 格式00：00
     */
    private fun time(time: Long): String {
        val i = time / 1000
        val s = i % 60
        val m = i / 60
        return "${if (m < 10) "0$m" else "$m"}:${if (s < 10) "0$s" else "$s"}"
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
        wifiLock.release()
        wakeLock.release()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        val AUDIO_DATAS = "AudioDatas"
    }
}