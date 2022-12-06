package tuoyan.com.xinghuo_dayingindex.ui.video

import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Typeface
import android.net.wifi.WifiManager
import android.os.*
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_audio.*
import kotlinx.android.synthetic.main.layout_en_ch_header.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_CALLBACK
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_COMPLETE
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_ERROR
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_NET_ERROR
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_RES_INFO
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_START
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_STATUS
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_STOP
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.LrcRow
import tuoyan.com.xinghuo_dayingindex.bean.SensorBook
import tuoyan.com.xinghuo_dayingindex.ui.book.SentenceListenActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.SpeedDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.TextSizeDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.TimerDialog
import tuoyan.com.xinghuo_dayingindex.ui.mine.offline.OfflineActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.NetLessonsPresenter
import tuoyan.com.xinghuo_dayingindex.ui.video.adapter.AudioEnChAdapter
import tuoyan.com.xinghuo_dayingindex.utlis.*
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import tuoyan.com.xinghuo_dayingindex.widegt.LinearTopSmoothScroller
import java.io.File
import java.lang.reflect.Type
import java.util.*


class AudioActivity : LifeActivity<NetLessonsPresenter>(), ServiceConnection {
    override val presenter = NetLessonsPresenter(this)
    override val layoutResId = R.layout.activity_audio

    private val enChAdapter by lazy {
        AudioEnChAdapter {
            service?.setProgress(it.currentRowTime)
        }
    }

    private val position by lazy { intent.getIntExtra(POSITION, 0) }
    val supportName by lazy { intent.getStringExtra(SUPPORT_NAME) ?: "" }
    val supportKey by lazy { intent.getStringExtra(SUPPORT_KEY) ?: "" }
    val canDownload by lazy { intent.getBooleanExtra(CAN_DOWNLOAD, true) }
    val resourceType by lazy { intent.getStringExtra(RESOURCE_TYPE) ?: "2" }
    val bookDetail by lazy { intent.getParcelableExtra(SENSORBOOK) as? SensorBook }

    private var bookres: List<BookRes>? = null
    private var service: AudioService? = null
    private var currentRes: BookRes? = null
    private var countDownTimer: CountDownTimer? = null
    private var timer: Timer? = null
    private var isTouch = false//中英文字母是否在触摸状态，是 不滚动，否 滚动
    private var onScreen = true//isTouch 之后是否出现在屏幕中，手动触发的时候只有文本高亮出现在屏幕中时，字幕可自动滚动

    private val wifiLock by lazy {
        (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).createWifiLock(
            WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock"
        )
    }
    private val wakeLock by lazy {
        (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelocktag"
        )
    }

    private val timerDialog by lazy {
        TimerDialog(this@AudioActivity) { item ->
            setTimer(item)
        }
    }

    private val listDialog by lazy {
        ListDialog(this, bookres) {
            service?.toPosition(it)
        }
    }
    private var listener = object : FileDownloadListener() {
        override fun warn(task: BaseDownloadTask?) {
            toast("已经在下载列表...")
        }

        override fun completed(task: BaseDownloadTask?) {
            try {
                task?.let {
                    saveDownload(it)
                }
                toast("下载成功")
            } catch (e: Exception) {
            }
        }

        override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            toast("正在下载...")
        }

        override fun error(task: BaseDownloadTask?, e: Throwable?) {
            if (task?.tag == currentRes?.id) {
                img_download.isSelected = false
            }
            try {
                AlertDialog.Builder(this@AudioActivity)
                    .setMessage("下载出错，请检查资源或网络连接设置，${e?.message}")
                    .setPositiveButton("确定") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }.create().show()
            } catch (e: Exception) {
            }
        }

        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

        }

        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            toast("暂停...")
        }

    }
    private val speedDialog by lazy {
        SpeedDialog(this@AudioActivity) { item ->
            setAudioSpeed(item)
        }
    }

    private val shareDialog by lazy {
        ShareDialog(this@AudioActivity) {
            ShareUtils.share(
                this@AudioActivity,
                it,
                currentRes?.name ?: "",
                "",
                "${WEB_BASE_URL}/share?" + "type=${1}" + "&title=${currentRes?.name}"
            )
        }
    }

    private val textSizeDialog by lazy {
        TextSizeDialog(this@AudioActivity) { size ->
            tv_size.isSelected = size != "常规"
            img_more.isSelected =
                tv_sort.isSelected || tv_speed.isSelected || tv_timer.isSelected || tv_size.isSelected
            setLrcSize(size)
        }
    }

    /**
     * 存储下载信息到realm
     */
    private fun saveDownload(task: BaseDownloadTask) {
        if (task.tag == currentRes?.id) {
            currentRes?.let {
                it.lrcUrls =
                    "${if (it.lrcurl.isEmpty()) "none" else it.lrcurl},${if (it.lrcurl2.isEmpty()) "none" else it.lrcurl2},${if (it.lrcurl3.isEmpty()) "none" else it.lrcurl3}"
                DownloadManager.saveDownloadInfo(
                    supportName ?: "",
                    supportKey ?: "",
                    if (supportName.isNotBlank()) "网课" else "图书",
                    it
                )
            }
        } else {
            kotlin.run breaking@{
                bookres?.forEach { item ->
                    if (item.id == task.tag) {
                        item.lrcUrls =
                            "${if (item.lrcurl.isEmpty()) "none" else item.lrcurl},${if (item.lrcurl2.isEmpty()) "none" else item.lrcurl2},${if (item.lrcurl3.isEmpty()) "none" else item.lrcurl3}"
                        DownloadManager.saveDownloadInfo(
                            supportName ?: "",
                            supportKey ?: "",
                            if (supportName.isNotBlank()) "网课" else "图书",
                            item
                        )
                        return@breaking
                    }
                }
            }
        }
    }

    companion object {
        val DATA = "data"
        val POSITION = "position"
        val SUPPORT_NAME = "supportName"
        val SUPPORT_KEY = "supportKey"
        val CAN_DOWNLOAD = "canDownload"
        val RESOURCE_TYPE = "resource_type"
        val SENSORBOOK = "SensorBook"
    }


    override fun configView() {
        // 取 bookres = MyApp.instance.bookres
        val data2: String = SPUtils.getInstance().getString("BookRes", "")
        val gson2 = Gson()
        val listType2: Type = object : TypeToken<List<BookRes?>?>() {}.getType()
        bookres = gson2.fromJson<List<BookRes>>(data2, listType2)
        EventBus.getDefault().register(this)
        PermissionUtlis.checkPermissions(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) {
            val intent = Intent(this, AudioService::class.java)
            bindService(intent, this, BIND_AUTO_CREATE)
        }
        wifiLock.acquire()
        wakeLock.acquire()

        rlv_ch_en.layoutManager = LinearLayoutManager(this)
        rlv_ch_en.adapter = enChAdapter
    }

    override fun handleEvent() {
        val handler = Handler()
        val runnable = Runnable {
            isTouch = false
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        rlv_ch_en.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        isTouch = true
                        onScreen = false
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

        img_en2ch.setOnClickListener {
            img_en2ch.isSelected = !img_en2ch.isSelected
            enChAdapter.noShowCH = img_en2ch.isSelected
        }
        img_download.setOnClickListener { view ->
            download(view)
        }
        img_collect.setOnClickListener { view ->
            collection(view)
        }
        img_share.setOnClickListener { view ->
            share(view)
        }
        img_more.setOnClickListener {
            ctl_setting.visibility =
                if (ctl_setting.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        img_pre.setOnClickListener { view ->
            previous(view)
        }
        img_play.setOnClickListener { view ->
            play(view)
        }
        img_next.setOnClickListener { view ->
            next(view)
        }
        img_list.setOnClickListener { view ->
            list(view)
        }
        tv_sort.setOnClickListener { view ->
            service?.setLoop()
            tv_sort.isSelected = service?.loop ?: false
            sort_txt.text = if (tv_sort.isSelected) "单曲循环" else "顺序播放"
            img_more.isSelected =
                tv_sort.isSelected || tv_speed.isSelected || tv_timer.isSelected || tv_size.isSelected
        }
        tv_speed.setOnClickListener { view ->
            showSpeed(view)
        }
        tv_timer.setOnClickListener { view ->
            showTimer(view)
        }
        tv_sentence.setOnClickListener {
            service?.pause()
            startActivity<SentenceListenActivity>(
                SentenceListenActivity.AUDIO to currentRes,
                SentenceListenActivity.SOURCE_TYPE to resourceType
            )
        }
        tv_size.setOnClickListener {
            textSizeDialog.show()
        }
    }

    override fun initData() {
    }

    private fun setLrcSize(size: String) {
        enChAdapter.textSize = size
    }

    private fun collection(v: View) {
// : 2018/10/27 9:49  收藏
        isLogin {
            when {
                v.isSelected -> {
                    currentRes?.id?.let {
                        currentRes?.isCollection = "0"
                        v.isSelected = false
                        presenter.deleteCollection(it) {}
                    }
                }
                currentRes?.id.isNullOrEmpty() -> {
                    toast("离线音频暂不支持收藏")
                }
                else -> {
                    currentRes?.id?.let {
                        currentRes?.isCollection = "1"
                        v.isSelected = true
                        presenter.addCollection(
                            mutableMapOf(
                                "targetkey" to it,
                                "type" to "5",
                                "source" to if (supportName == null) "1" else "2",
                                "sourceKey" to supportKey
                            )
                        ) {}
                    }
                }
            }
        }
    }

    private fun download(v: View) {
        isLogin {
            if (!v.isEnabled) {
                mToast("暂不支持下载")
                return@isLogin
            }
            if (v.isSelected) {
                mToast("已下载")
                return@isLogin
            }
            // : 2018/10/27 9:49  开始下载
            currentRes?.downUrl?.let {
                PermissionUtlis.checkPermissions(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) {
                    FileDownloader.setup(this@AudioActivity)
                    val task = FileDownloader.getImpl().create(it).setPath(
                        DownloadManager.getFilePathWithKey(
                            currentRes?.id ?: "", currentRes?.type ?: ""
                        )
                    ).setListener(listener)
                    task.tag = currentRes?.id
                    DownloadManager.taskList.add(task)
                    task.start()
                    v.isSelected = true
                    //TODO 同步下载不同的字幕文件
                    if (!currentRes?.lrcurl.isNullOrEmpty()) {
                        FileDownloader.getImpl().create(currentRes?.lrcurl)
                            .setPath(DownloadManager.getFilePath(currentRes?.lrcurl ?: "")).start()
                    }
                    if (!currentRes?.lrcurl2.isNullOrEmpty()) {
                        FileDownloader.getImpl().create(currentRes?.lrcurl2)
                            .setPath(DownloadManager.getFilePath(currentRes?.lrcurl2 ?: "")).start()
                    }
                    if (!currentRes?.lrcurl3.isNullOrEmpty()) {
                        FileDownloader.getImpl().create(currentRes?.lrcurl3)
                            .setPath(DownloadManager.getFilePath(currentRes?.lrcurl3 ?: "")).start()
                    }
                }
            }
        }
    }

    private fun showTimer(v: View) {
        timerDialog.show()
    }

    private fun showSpeed(v: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            speedDialog.show()
        } else {
            mToast("手机版本不支持倍速播放")
        }
    }

    private fun setTimer(item: Int) {
        tv_timer.isSelected = -2 != item
        img_more.isSelected =
            tv_sort.isSelected || tv_speed.isSelected || tv_timer.isSelected || tv_size.isSelected
        cancelTimer()
        when (item) {
            -2 -> {
                service?.isClose = false
            }
            -1 -> {
                initTimer()
            }
            else -> {
                initCountDownTimer(item * 60 * 1000L)
            }
        }
    }

    private fun setAudioSpeed(speed: Float) {
        service?.speed(speed)
        speed_img.setImageResource(
            when (speed) {
                0.5f -> R.mipmap.icon_speed_05
                0.8f -> R.mipmap.icon_speed_08
                1.0f -> R.mipmap.icon_speed_10
                1.2f -> R.mipmap.icon_speed_12
                1.5f -> R.mipmap.icon_speed_15
                1.8f -> R.mipmap.icon_speed_18
                2.0f -> R.mipmap.icon_speed_20
                else -> R.mipmap.icon_speed_10
            }
        )
        tv_speed.isSelected = speed != 1.0f
        img_more.isSelected =
            tv_sort.isSelected || tv_speed.isSelected || tv_timer.isSelected || tv_size.isSelected
    }

    private fun cancelTimer() {
        timer_txt?.text = "定时关闭"
        timer_txt?.typeface = Typeface.DEFAULT
        countDownTimer?.cancel()
        countDownTimer = null
        timer?.cancel()
        timer = null
    }

    private fun initCountDownTimer(item: Long) {
        timer_txt.typeface = Typeface.DEFAULT_BOLD
        service?.isClose = false
        countDownTimer = object : CountDownTimer(item, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer_txt?.text = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                timer_txt?.typeface = Typeface.DEFAULT
                timer_txt?.text = "定时关闭"
                tv_timer?.isSelected = false
                service?.pause()
            }
        }.start()
    }

    private fun initTimer() {
        service?.isClose = true
        timer = Timer(true)
        timer_txt.typeface = Typeface.DEFAULT_BOLD
        timer?.schedule(object : TimerTask() {
            override fun run() {
                val duration = seek_bar?.max ?: 0
                val currentTime = seek_bar?.progress ?: 0
                runOnUiThread {
                    timer_txt?.text = formatTime((duration - currentTime).toLong())
                    if (duration <= currentTime + 1000) {
                        cancelTimer()
                    }
                }
            }
        }, 500, 1000)
    }

    private fun previous(v: View) {
        service?.previous()
    }

    private fun play(v: View) {
        service?.play()
    }

    private fun next(v: View) {
        service?.next()
    }

    private fun list(v: View) {
        // : 2018/10/27 9:51  列表
        listDialog.setPosition(bookres?.indexOf(currentRes) ?: 0)
        listDialog.show()
    }

    private fun share(v: View) {
        shareDialog.show()
    }

    override fun onBackPressed() {
        when {
            speedDialog.isShowing -> {
                speedDialog.dismiss()
            }
            timerDialog.isShowing -> {
                timerDialog.dismiss()
            }
            shareDialog.isShowing -> {
                shareDialog.dismiss()
            }
            textSizeDialog.isShowing -> {
                textSizeDialog.dismiss()
            }
            else -> {
                currentRes?.let {
                    saFinishVideo(
                        it.id, "${seek_bar.progress.toFloat() / seek_bar.max.toFloat()}"
                    )
                }
                super.onBackPressed()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun callBacks(event: AudioCallBack) {
        this.service ?: return
        when (event.type) {
            TYPE_COMPLETE -> {
                currentRes?.let {
                    saFinishVideo(it.id, "1")
                }
                if (this.service?.isClose == true) onBackPressed()
                tv_time.text = "00:00"
                tv_duration.text = "00:00"
                seek_bar.progress = 0
                seek_bar.secondaryProgress = 0
                seek_bar.max = 0
            }
            TYPE_ERROR -> {
                mToast(event.msg)
            }
            TYPE_START -> {
                if (listDialog.isShowing) {
                    listDialog.setPosition(event.position)
                }
                start(event.time, event.position)
            }
            TYPE_STOP -> {
                tv_time.text = "00:00"
                seek_bar.progress = 0
            }
            TYPE_STATUS -> {
                img_play.isSelected = event.flag
            }
            TYPE_CALLBACK -> {
                seek_bar.progress = event.time.toInt()
                tv_time.text = formatTime(event.time)
                kotlin.run breaking@{
                    val lrcDatas = enChAdapter.getData()
                    lrcDatas.forEachIndexed { index, lrcRow ->
                        val isTrue =
                            lrcDatas.size - 1 == index && lrcRow.currentRowTime <= event.time || (lrcDatas.size - 1 > index && lrcRow.currentRowTime <= event.time && lrcDatas[index + 1].currentRowTime > event.time)
                        if (isTrue) {
                            enChAdapter.currentPos = index + 1
                            val layoutManager = rlv_ch_en.layoutManager as? LinearLayoutManager
                            val firstPos =
                                layoutManager?.findFirstCompletelyVisibleItemPosition() ?: 0
                            val lastPos =
                                layoutManager?.findLastCompletelyVisibleItemPosition() ?: 0
                            onScreen = firstPos <= index + 1 && lastPos >= index || onScreen
                            if ((firstPos > index + 1 || lastPos < index + 1) && !isTouch && onScreen) {
                                val smoothScroller = LinearTopSmoothScroller(this@AudioActivity)
                                smoothScroller.targetPosition = index + 1
                                layoutManager?.startSmoothScroll(smoothScroller)
                            }
                            return@breaking
                        }
                    }
                }
            }
            TYPE_RES_INFO -> {
                presenter.getResourceInfo(event.key, resourceType) {
                    event.function(it)
                }
            }
            TYPE_NET_ERROR -> {
                AlertDialog.Builder(this@AudioActivity).setCancelable(false).setTitle("网络异常")
                    .setMessage("暂无网络 ，快去离线中心查看相关缓存").setPositiveButton("确定") { _, _ ->
                        startActivity<OfflineActivity>(OfflineActivity.POSITION to 1)
                        finish()
                    }.setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }.create().show()
            }
        }
    }

    /**
     * 处理回调
     */
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is AudioService.MBinder) {
            this.service = service.getService()
            if (bookres == null) return
            if (position >= bookres!!.size) return
            this.service?.setData(bookres!!, position)
        }
    }

    /**
     * 音频播放开始
     */
    private fun start(time: Long, position: Int) {
        tv_time.text = "00:00"
        tv_duration.text = formatTime(time)
        seek_bar.progress = 0
        seek_bar.secondaryProgress = 0
        seek_bar.max = time.toInt()
        currentRes = bookres?.get(position)
        enChAdapter.title = currentRes?.name ?: ""
        lrc_empty.tv_title.text = currentRes?.name ?: ""
        img_download.visibility =
            if (currentRes?.downloadFlag == "1" && canDownload) View.VISIBLE else View.GONE
        img_download.isSelected = currentRes?.isDownload() ?: false
        img_collect.visibility = if (supportKey.isNotBlank()) View.VISIBLE else View.GONE
        img_collect.isSelected = currentRes?.isCollection == "1"
        currentRes?.let { cRes ->
            if (!NetWorkUtils.isNetWorkReachable()) {
                val res = DownloadManager.getDownloadedRes(cRes.id)
                val lrcList = res?.lrcUrls?.split(",") ?: mutableListOf()
                if (lrcList.size >= 3) {
                    cRes.lrcurl = if (lrcList[0] == "none") "" else lrcList[0]
                    cRes.lrcurl2 = if (lrcList[1] == "none") "" else lrcList[1]
                    cRes.lrcurl3 = if (lrcList[2] == "none") "" else lrcList[2]
                }
            }
            saStartVideo(cRes.id)
        }
        initLrcView()
    }

    private fun initLrcView() {
        rlv_ch_en.scrollToPosition(0)
        enChAdapter.currentPos = 0
        currentRes?.let { res ->
            img_en2ch.visibility = if (res.lrcurl2?.isNotEmpty()!!) View.VISIBLE else View.GONE
            tv_sentence.visibility = if (res.lrcurl2?.isNotEmpty()!!) View.VISIBLE else View.GONE
            lrc_empty.visibility = View.GONE
            tv_size.visibility = View.VISIBLE
            when {
                res.lrcurl2?.isNotEmpty()!! -> {
                    enChAdapter.noShowCH = img_en2ch.isSelected
                    PermissionUtlis.checkPermissions(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) {
                        if (currentRes?.islrcurl2() == true) {
                            val lrcRowsEn = MyLrcDataBuilder().Build(
                                FileUtils.getFileByPath(
                                    DownloadManager.getFilePath(currentRes?.lrcurl2 ?: "")
                                ), parser
                            )
                            enChAdapter.setData(lrcRowsEn)
                            if (lrcRowsEn.isNullOrEmpty()) {
                                lrc_empty.visibility = View.VISIBLE
                                tv_size.visibility = View.GONE
                                img_en2ch.visibility = View.GONE
                            }
                        } else {
                            presenter.getLrc(currentRes?.lrcurl2 ?: "", {
                                val lrcRowsEn = MyLrcDataBuilder().Build(it, parser)
                                enChAdapter.setData(lrcRowsEn)
                                if (lrcRowsEn.isNullOrEmpty()) {
                                    lrc_empty.visibility = View.VISIBLE
                                    tv_size.visibility = View.GONE
                                    img_en2ch.visibility = View.GONE
                                }
                            }) {
                                enChAdapter.setData(null)
                                lrc_empty.visibility = View.VISIBLE
                                tv_size.visibility = View.GONE
                                img_en2ch.visibility = View.GONE
                            }
                        }
                    }
                }
                res.lrcurl?.isNotEmpty()!! -> {
                    enChAdapter.noShowCH = true
                    PermissionUtlis.checkPermissions(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) {
                        when {
                            currentRes?.islrcurl() == true -> {
                                val lrcRowsEn = MyLrcDataBuilder().Build(
                                    FileUtils.getFileByPath(
                                        DownloadManager.getFilePath(currentRes?.lrcurl ?: "")
                                    ), parser
                                )
                                enChAdapter.setData(lrcRowsEn)
                                if (lrcRowsEn.isNullOrEmpty()) {
                                    lrc_empty.visibility = View.VISIBLE
                                    tv_size.visibility = View.GONE
                                }
                            }
                            else -> {
                                presenter.getLrc(currentRes?.lrcurl ?: "", {
                                    val lrcRowsEn = MyLrcDataBuilder().Build(it, parser)
                                    enChAdapter.setData(lrcRowsEn)
                                    if (lrcRowsEn.isNullOrEmpty()) {
                                        lrc_empty.visibility = View.VISIBLE
                                        tv_size.visibility = View.GONE
                                    }
                                }) {
                                    enChAdapter.setData(null)
                                    lrc_empty.visibility = View.VISIBLE
                                    tv_size.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
                else -> {
                    enChAdapter.setData(null)
                    lrc_empty.visibility = View.VISIBLE
                    tv_size.visibility = View.GONE
                }
            }
        }
    }

    private val parser by lazy {
        object : IRowsParser {
            override fun parse(lrcRowDada: String): MutableList<LrcRow> {

                try {
                    val lastIndexOfRightBracket = lrcRowDada.indexOf("]")
                    val content =
                        lrcRowDada.substring(lastIndexOfRightBracket + 1, lrcRowDada.length)
                    val times =
                        lrcRowDada.substring(0, lastIndexOfRightBracket + 1).replace("[", "-")
                            .replace("]", "-")
                    val arrTimes =
                        times.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val listTimes = mutableListOf<LrcRow>()
                    val var10 = arrTimes.size
                    for (var11 in 0 until var10) {
                        val temp = arrTimes[var11]
                        if (temp.trim { it <= ' ' }.isNotEmpty()) {
                            val lrcRow = LrcRow(content, temp, this.timeConvert(temp))
                            listTimes.add(lrcRow)
                        }
                    }
                    return listTimes
                } catch (var14: Exception) {
                    return mutableListOf()
                }

            }

            private fun timeConvert(timeString: String): Long {
                var timeStr = timeString
                timeStr = timeStr.replace('.', ':')
                val times =
                    timeStr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return when {
                    times.size == 4 -> (Integer.valueOf(times[0]) * 60 * 60 * 1000 + Integer.valueOf(
                        times[1]
                    ) * 60 * 1000 + Integer.valueOf(times[2]) * 1000 + Integer.valueOf(times[3])).toLong()
                    times.size == 3 -> (Integer.valueOf(times[0]) * 60 * 1000 + Integer.valueOf(
                        times[1]
                    ) * 1000 + Integer.valueOf(times[2])).toLong()
                    times.size == 2 -> (Integer.valueOf(times[0]) * 60 * 1000 + Integer.valueOf(
                        times[1]
                    ) * 1000).toLong()
                    times.size == 1 -> (Integer.valueOf(times[0]) * 1000).toLong()
                    else -> 0
                }
            }

        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service?.unCallBack()
    }

    override fun onDestroy() {
        if (this.service != null) {
            unbindService(this)
        }
        wifiLock.release()
        wakeLock.release()
        bookres = null
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    fun formatTime(time: Long): String {
        val i = time / 1000
        val s = i % 60
        val m = i / 60
        return "${if (m < 10) "0$m" else "$m"}:${if (s < 10) "0$s" else "$s"}"
    }

    private fun saStartVideo(id: String) {
        try {
            val file = File(DownloadManager.getFilePathWithKey(id, "7"))
            var property = JSONObject()
            bookDetail?.let {
                property = JSONObject(Gson().toJson(it))
            }
            property.put("resource_id", id)
            property.put("is_playing_offline", file.exists())
            SensorsDataAPI.sharedInstance().track("play_audio", property)
            SensorsDataAPI.sharedInstance().trackTimerStart("audio_playback_complete")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saFinishVideo(id: String, percent: String) {
        try {
            val file = File(DownloadManager.getFilePathWithKey(id, "7"))
            var property = JSONObject()
            bookDetail?.let {
                property = JSONObject(Gson().toJson(it))
            }
            property.put("resource_id", id)
            property.put("is_playing_offline", file.exists())
            property.put("playback_progress", percent)
            SensorsDataAPI.sharedInstance().trackTimerEnd("audio_playback_complete", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}
