package com.spark.peak.ui.video

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.SPUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hw.lrcviewlib.ILrcViewSeekListener
import com.hw.lrcviewlib.IRowsParser
import com.hw.lrcviewlib.LrcRow
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.R
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.AudioRes
import com.spark.peak.bean.LrcInfo
import com.spark.peak.bean.Lyric
import com.spark.peak.bean.ResourceInfo
import com.spark.peak.service.AudioService
import com.spark.peak.ui.dialog.ListDialog
import com.spark.peak.ui.video.adapter.LyricAdapter
import com.spark.peak.utlis.*
import kotlinx.android.synthetic.main.activity_audiodf.*
import org.jetbrains.anko.sp
import org.jetbrains.anko.toast
import org.jetbrains.anko.wrapContent
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat


class AudioActivity : LifeActivity<BasePresenter>(), ServiceConnection {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId = R.layout.activity_audiodf

    private var currentRes: ResourceInfo? = null
    private var position = 0
    private val data by lazy { intent.getSerializableExtra(DATA) as? MutableList<AudioRes> }

    //pt(配套) wk（网课） jctb(教材同步)，如果是教材同步，无收藏 无下载，service中直接赋值播放链接，无需请求
    private val type by lazy { intent.getStringExtra(TYPE) ?: "" }
    private val bookName by lazy { intent.getStringExtra(NAME) ?: "" }
    private val supportKey by lazy { intent.getStringExtra(SUPPORT_KEY) ?: "" }
    private val section by lazy { intent.getStringExtra(SECTION) ?: "" }

    //胶片动画
    private val animatorSet = AnimatorSet()
    private val rotation by lazy { ObjectAnimator.ofFloat(img_film, "rotation", 0f, 360f) }

    //    private var player: IjkExo2MediaPlayer? = null
    private var service: AudioService? = null
    private val sdf by lazy {
        SimpleDateFormat("mm:ss")
    }
    private var audioRes: AudioRes? = null
    private var currentSpeed: TextView? = null
    private var width = 0
    private var height = 0
    private val wifiLock by lazy {
        (getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager).createWifiLock(
            WifiManager.WIFI_MODE_FULL_HIGH_PERF,
            "mylock"
        )
    }
    private val wakeLock by lazy {
        (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "myapp:mywakelocktag"
        )
    }

    //    PowerManager.PARTIAL_WAKE_LOCK
    override fun onServiceDisconnected(name: ComponentName?) {
        service?.unCallBack()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is AudioService.MBinder) {
            this.service = service.getService()
            this.service?.setCallBack(
                onCompletion = {
                    currentRes = null
                    audioRes = null
                    saEnd()
//                    if (this.service?.isClose == true) onBackPressed()
//                    tv_index.text = "00:00"
//                    tv_total.text = "00:00"
//                    sb_progress.progress = 0
//                    sb_progress.secondaryProgress = 0
//                    sb_progress.max = 0
                    if (!iv_sort.isSelected) {
                        next()
                    } else {
                        this.service?.play()
                    }
                },
                onError = {
                    mToast(it)
                    onBackPressed()
                },
                onStart = { position, time ->
                    start(time, position)
                },
                status = {
                    if (iv_play.isSelected != it) {
                        iv_play.isSelected = it
                        animator(it)
                    }
                },
                callBack = {
                    // 回调函数
                    sb_progress.progress = it.toInt()
                    tv_index.text = sdf.format(it)
                    lrc_view.seekLrcToTime(it)
                    if (haveLyc) {
                        lycUtils.notifyTime(it.toLong())
                    }
                },
                resInfo = { key, function ->
                    if (type == "jctb") {
                        presenter.getJCTBRes(key) {
                            currentRes = it
                            function(it)
                        }
                    } else {
                        presenter.getResInfo(key, "2", "0") {
                            currentRes = it
                            function(it)
                        }
                    }
                }
            )

            if (type == "jctb") {
                data?.let { this.service?.setData(it, position, type, bookName) }
            } else {
                var list1: List<AudioRes>? = null
//                MyApp.instance.bookres?.let {
//                    this.service?.setData(it, position, type, bookName)
//                }
//                MyApp.instance.bookres ?: mToast("数据为空")
                list1?.let {
                    this.service?.setData(it, position, type, bookName)
                }
                list1 ?: mToast("数据为空")
                // 存
//                val list1: List<AudioRes> = ArrayList()
                val gson1 = Gson()
                val data1 = gson1.toJson(list1)
                SPUtils.getInstance().put("AudioRes", data1)
            }
        }
    }

    private fun saPlay() {
        try {
            SensorsDataAPI.sharedInstance().track("df_play_audio", saProperty())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saEnd() {
        try {
            val property = saProperty()
            property.put("playback_progress", tv_index.text.toString())
            SensorsDataAPI.sharedInstance().track("df_audio_playback_complete", property)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saProperty(): JSONObject {
        val property = JSONObject()
        property.put("book_matching_id", supportKey)
        property.put("book_matching_name", bookName)
        property.put(
            "is_playing_offline", File(
                DownloadManager.getFilePath(
                    type,
                    bookName,
                    audioRes?.name ?: "",
                    audioRes?.downUrl ?: ""
                )
            ).exists()
        )
        property.put("resource_id", audioRes?.id ?: "")
        property.put("section", section)
        property.put("synchronous_teaching_name", typeName())
        return property
    }

    /**
     * pt(配套) wk（网课） jctb(教材同步)
     */
    private fun typeName(): String {
        return when (type) {
            "pt" -> "配套"
            "wk" -> "网课"
            "jctb" -> "教材同步"
            else -> ""
        }
    }

    /**
     * 音频播放开始
     */
    private fun start(time: Long, position: Int) {
        sb_progress.max = time.toInt()
        sb_progress.progress = 0
        tv_index.text = "00:00"
        if (time > 1000 * 60 * 60) {
            sdf.applyPattern("HH:mm:ss")
        }
        tv_total.text = sdf.format(time)
        prepareMusic(data!![position])
        saPlay()
    }

    private fun animator(isPlaying: Boolean) {
        if (isPlaying) {
            val shadowAni = ObjectAnimator.ofFloat(img_film_shadow, "translationX", 50f)
            val filmAni = ObjectAnimator.ofFloat(img_film, "translationX", 50f)
            animatorSet.duration = 1000
            animatorSet.playTogether(shadowAni, filmAni)
            animatorSet.start()
            rotation.startDelay = 1050
            rotation.duration = 2000
            rotation.repeatCount = ObjectAnimator.INFINITE
            rotation.interpolator = LinearInterpolator()
            rotation.start()
        } else {
            rotation.cancel()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                animatorSet.reverse()
            }
        }
    }


    private val popupWindow by lazy {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_pop_speeddf, null)
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        width = view.measuredWidth
        height = view.measuredHeight
        val rg_speed = view.findViewById(R.id.rg_speed) as RadioGroup
        (rg_speed.getChildAt(2) as RadioButton).isChecked = true
        rg_speed.setOnCheckedChangeListener { radioGroup, i ->
            when (radioGroup.indexOfChild(radioGroup.findViewById(i)) / 2) {
                0 -> speed(0.75f)
                1 -> speed(1f)
                2 -> speed(1.25f)
                3 -> speed(1.5f)
            }
        }
        PopupWindow(view, wrapContent, wrapContent).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isOutsideTouchable = true
            setOnDismissListener {
            }
        }
    }
    private val listDialog by lazy {
        ListDialog(this, data) {
            position = it
            tv_index.text = "00:00"
            tv_total.text = "00:00"
            sb_progress.progress = 0
            sb_progress.secondaryProgress = 0
            sb_progress.max = 0
            service?.toPosition(position)
            lrc_view.setNoDataMessage("字幕加载中")
            lrc_view.setLrcData(null)
        }
    }

    private fun collection(v: View) {
        checkLogin {
            if (type == "jctb") {
                mToast("暂不支持收藏")
                return@checkLogin
            }
            if (!NetWorkUtils.isNetWorkReachable()) {
                mToast("暂无网络")
                return@checkLogin
            }
            if (!v.isEnabled || audioRes?.id.isNullOrEmpty()) {
                mToast("离线音频暂不支持收藏")
                return@checkLogin
            }
            if (v.isSelected) {
                audioRes?.id?.let {
                    audioRes?.isCollection = "0"
                    v.isSelected = false
                    presenter.deleteCollection(it) {}
                }
                Toast.makeText(this@AudioActivity, "已取消收藏", Toast.LENGTH_LONG).show()
            } else if (audioRes?.id.isNullOrEmpty()) {
                toast("离线音频暂不支持收藏")
            } else {
                audioRes?.id?.let {
                    audioRes?.isCollection = "1"
                    v.isSelected = true
                    presenter.addCollection(
                        mutableMapOf(
                            "targetkey" to it,
                            "type" to "5", "source" to "1", "sourceKey" to supportKey
                        )
                    ) {}
                }
                Toast.makeText(this@AudioActivity, "已收藏", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun download(v: View) {
        checkLogin {
            if (v.isSelected) {
                mToast("下载已完成")
                return@checkLogin
            }
            if (type == "jctb" || currentRes?.downloadFlag != "1") {
                mToast("无法下载该音频")
                return@checkLogin
            }
//            if (!v.isEnabled) {
//                mToast("无法下载该音频")
//                return@checkLogin
//            }
            // : 2018/10/27 9:49 霍述雷 开始下载
            audioRes?.downUrl?.let {
                PermissionUtlis.checkPermissions(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) {
                    Toast.makeText(this@AudioActivity, "开始下载", Toast.LENGTH_LONG).show()
                    FileDownloader.setup(this@AudioActivity)
                    val task = FileDownloader.getImpl()
                        .create(it)
                        .setPath(
                            DownloadManager.getFilePath(
                                type,
                                bookName,
                                audioRes?.name ?: "",
                                audioRes?.downUrl ?: ""
                            )
                        )
                        .setListener(object : FileDownloadListener() {
                            override fun warn(task: BaseDownloadTask?) {}

                            override fun completed(task: BaseDownloadTask?) {
                                iv_download.setImageResource(R.mipmap.audio_down_ed)
                                Toast.makeText(this@AudioActivity, "下载已完成", Toast.LENGTH_LONG)
                                    .show()
                            }

                            override fun pending(
                                task: BaseDownloadTask?,
                                soFarBytes: Int,
                                totalBytes: Int
                            ) {
                            }

                            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                                toast("下载失败")
                            }

                            override fun progress(
                                task: BaseDownloadTask?,
                                soFarBytes: Int,
                                totalBytes: Int
                            ) {
                            }

                            override fun paused(
                                task: BaseDownloadTask?,
                                soFarBytes: Int,
                                totalBytes: Int
                            ) {
                            }
                        })

                    DownloadManager.taskList.add(task)
                    task.start()
                    v.isSelected = true

                    //TODO 关联文件名及字幕url
                    val lrcInfoList = if (SpUtil.lrcInfo.isEmpty()) {
                        mutableListOf<LrcInfo>()
                    } else {
                        Gson().fromJson(SpUtil.lrcInfo, object : TypeToken<List<LrcInfo>>() {}.type)
                    }
                    var havaSame = false
                    kotlin.run breaking@{
                        lrcInfoList.forEach {
                            if (it.resName == task.path) {
                                it.lrcUrl = audioRes?.lrcurl ?: ""
                                it.id = audioRes?.id ?: ""
                                it.duration = audioRes?.duration ?: ""
                                havaSame = true
                                return@breaking
                            }
                        }
                    }
                    if (!havaSame) lrcInfoList.add(
                        LrcInfo(
                            task.path,
                            audioRes?.lrcurl ?: "",
                            audioRes?.id ?: "",
                            audioRes?.duration ?: ""
                        )
                    )//TODO 再添加
                    SpUtil.lrcInfo = Gson().toJson(lrcInfoList)//TODO 再存储
                }
            }
        }
    }

    fun next() {
        position++
        if (data!!.size <= position) {
            position = 0
        }
        tv_index.text = "00:00"
        tv_total.text = "00:00"
        sb_progress.progress = 0
        sb_progress.secondaryProgress = 0
        sb_progress.max = 0
        service?.toPosition(position)
        lrc_view.setNoDataMessage("字幕加载中")
        lrc_view.setLrcData(null)
        if (listDialog.isShowing) {
            listDialog.setPos(position)
        }
    }

//    fun previous(v: View) {
//        position--
//        if (position >= 0) {
//            tv_index.text = "00:00"
//            tv_total.text = "00:00"
//            sb_progress.progress = 0
//            sb_progress.secondaryProgress = 0
//            sb_progress.max = 0
//            service?.previous()
//            lrc_view.setNoDataMessage("字幕加载中")
//            lrc_view.setLrcData(null)
//        } else {
//            position = 0
//            toast("没有上一首")
////            position = data!!.size - 1
////            prepareMusic(data!![position])
//        }
////        position--
////        if (position >= 0) {
////            prepareMusic(data!![position])
////        } else {
////            position = data!!.size - 1
////            prepareMusic(data!![position])
////        }
//    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            saEnd()
        }
    }

    fun speed(speed: Float) {
        service?.speed(speed)
        popupWindow.dismiss()
        var resId = R.mipmap.audio_speed_100
        when (speed) {
            0.75f -> {
                resId = R.mipmap.audio_speed_075
            }
            1f -> {
                resId = R.mipmap.audio_speed_100
            }
            1.25f -> {
                resId = R.mipmap.audio_speed_125
            }
            1.5f -> {
                resId = R.mipmap.audio_speed_150
            }
        }
        iv_speed.setImageResource(resId)
        Toast.makeText(this@AudioActivity, "已切换为${speed}倍速", Toast.LENGTH_LONG).show()
    }

    private val downloadListener by lazy {
        object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask?) {}

            override fun completed(task: BaseDownloadTask?) {
                try {
                    val file = File(task?.path ?: "")
                    lrc_view.setLrcData(MyLrcDataBuilder().Build(file, parser))
                } catch (e: Exception) {
                    iv_lrc.visibility = View.GONE
                    ctl_book_film.visibility = View.VISIBLE
                }
            }

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {}

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                //下载报错temp文件内容正常，取巧
                try {
                    val tempFile = File(task?.path + ".temp")
                    val file = File(task?.path)
                    tempFile.renameTo(file)
                    lrc_view.setLrcData(MyLrcDataBuilder().Build(file, parser))
                } catch (e: Exception) {
                    iv_lrc.visibility = View.GONE
                    ctl_book_film.visibility = View.VISIBLE
                }
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {}

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {}
        }
    }

    companion object {
        val URL = "url"
        val TITLE = "title"
        val DATA = "data"
        val POSITION = "position"
        val TYPE = "type"
        val SUPPORT_KEY = "supportKey"
        val SECTION = "SECTION"

        val NAME = "name"
        val LYC_URL = "lyc_url"
    }

    override fun configView() {
        setSupportActionBar(tb_audio)
        tb_audio.setNavigationOnClickListener { finish() }
        title?.let { tv_title.text = title }
        val intent = Intent(this, AudioService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        initNotification()
        wifiLock.acquire()
        wakeLock.acquire()
    }

    fun initNotification() {
        if (!NotificationUtils.checkNotifySetting(this)) {
            var builder = AlertDialog.Builder(this)
                .setTitle("通知权限")
                .setMessage("尚未开启通知权限，点击去开启")
                .setPositiveButton("确认") { dialog, which ->
                    var intent = Intent()
                    //下面这种方案是直接跳转到当前应用的设置界面。
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    var uri = Uri.fromParts("package", getPackageName(), null)
                    intent.setData(uri)
                    startActivity(intent)
                }
                .setNegativeButton("取消") { dialog, which ->

                }
            builder.show()
        }
    }

    override fun initData() {
        position = intent.getIntExtra(POSITION, 0)
        if (data == null || data!!.isEmpty()) {
            toast("数据出错啦，请重试~~~")
            onBackPressed()
            return
        }
        initLrcView()
    }

    private fun initAudio(res: AudioRes) {
        audioRes = res
        iv_collection.isEnabled = !res.id.isNullOrBlank()
        iv_collection.isSelected = res.isCollection == "1"
        if (!res.playUrl.startsWith("http") || File(
                DownloadManager.getFilePath(type, bookName, res.name, res.downUrl ?: "")
            ).exists()
        ) {
            iv_download.setImageResource(R.mipmap.audio_down_ed)
        } else if (currentRes?.downloadFlag != "1" || type == "jctb") {
            iv_download.setImageResource(R.mipmap.audio_down_no)
        } else {
            iv_download.setImageResource(R.mipmap.audio_down_normal)
        }
        iv_download.isSelected = !res.playUrl.startsWith("http") || File(
            DownloadManager.getFilePath(type, bookName, res.name, res.downUrl ?: "")
        ).exists()
        iv_lrc.visibility = if (res.lrcurl.isNullOrBlank()) View.GONE else View.VISIBLE
        iv_lrc.isSelected = res.showLrc == "1"
        ctl_book_film.visibility =
            if (!iv_lrc.isShown || !iv_lrc.isSelected) View.VISIBLE else View.GONE
        Glide.with(this@AudioActivity).load(res.bookCover)
            .error(R.mipmap.audio_book_cover)
            .placeholder(R.mipmap.audio_book_cover)
            .into(img_book)
    }

    private fun prepareMusic(res: AudioRes) {
        res.playUrl = currentRes?.url ?: res.playUrl
        res.lrcurl = currentRes?.lrcurl1 ?: res.lrcurl
//        res.lrcurl2 = currentRes?.lrcurl2 ?: res.lrcurl2
//        res.lrcurl3 = currentRes?.lrcurl3 ?: res.lrcurl3
        res.downUrl = currentRes?.downloadUrl ?: res.downUrl
        res.showLrc = currentRes?.showLrc ?: res.showLrc
        res.bookCover = currentRes?.bookCover ?: res.bookCover
        tv_title.text = res.name.replace("/AA/", "  ")
        playRes(res)
    }

    private fun playRes(res: AudioRes) {
        initAudio(res)
        oAdapter.setLyric(mutableListOf<Lyric>())
        oAdapter.notifyDataSetChanged()
        if (!res.lrcurl.isNullOrBlank()) {
            val path = DownloadManager.getFilePath(res.lrcurl)
            val lycFile = File(path)
            if (lycFile.exists()) {
                lrc_view.setLrcData(MyLrcDataBuilder().Build(lycFile, parser))
            } else {
                PermissionUtlis.checkPermissions(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) {
                    val tempPath = DownloadManager.getFilePath(res.lrcurl)
                    val tempFile = File("$tempPath.temp")
                    if (tempFile.exists()) {
                        tempFile.delete()
                    }
                    FileDownloader.setup(this)
                    FileDownloader
                        .getImpl()
                        .create(res.lrcurl)
                        .setPath(DownloadManager.getFilePath(res.lrcurl))
                        .setListener(downloadListener)
                        .start()
                }
            }
        }
    }

    override fun handleEvent() {
        tb_audio.setNavigationOnClickListener { onBackPressed() }
        sb_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    service?.setProgress(progress.toLong())
                }
            }
        })
        iv_lrc.setOnClickListener {
            iv_lrc.isSelected = !iv_lrc.isSelected
            ctl_book_film.visibility = if (iv_lrc.isSelected) View.GONE else View.VISIBLE
            Toast.makeText(
                this@AudioActivity,
                if (iv_lrc.isSelected) "已开启字幕" else "已关闭字幕",
                Toast.LENGTH_LONG
            ).show()
        }
        iv_list.setOnClickListener {
            listDialog.show()
            listDialog.setPos(position)
        }
        iv_speed.setOnClickListener { v ->
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            } else {
                val location = intArrayOf(0, 0)
                v.getLocationOnScreen(location)
                popupWindow.showAtLocation(
                    v, Gravity.NO_GRAVITY, (location[0] + v.width / 2) - width / 2,
                    location[1] - height
                )
            }
        }
        iv_play.setOnClickListener {
            service?.play()
        }
        iv_collection.setOnClickListener {
            collection(it)
        }
        iv_download.setOnClickListener {
            download(it)
        }
        iv_sort.setOnClickListener {
            iv_sort.isSelected = !iv_sort.isSelected
            Toast.makeText(
                this@AudioActivity,
                if (iv_sort.isSelected) "已开启单曲循环播放" else "已开启列表循环播放",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * 初始化歌词显示相关
     */
    var lycUtils = LyricLoadUtils()
    val oAdapter by lazy { LyricAdapter(this@AudioActivity) }
    var haveLyc = false

    /**
     * 2019/6/17 新增 --- 修改字幕显示方式
     */
    private var lrcListener = ILrcViewSeekListener { _, time ->
        service?.setProgress(time)
    }

    private val parser by lazy {
        object : IRowsParser {
            override fun parse(lrcRowDada: String): MutableList<LrcRow>? {
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
                    return null
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

    private fun initLrcView() {
        lrc_view.lrcSetting
            .setTimeTextSize(40)//时间字体大小
            .setSelectLineColor(Color.parseColor("#4c84ff"))//选中线颜色
            .setSelectLineTextSize(25)//选中线大小
            .setHeightRowColor(Color.parseColor("#4c84ff"))//高亮字体颜色
            .setNormalRowTextSize(this.sp(17))//正常行字体大小
            .setHeightLightRowTextSize(this.sp(17))//高亮行字体大小
            .setTrySelectRowTextSize(this.sp(17))//尝试选中行字体大小
            .setTimeTextColor(Color.parseColor("#4c84ff"))//时间字体颜色
            .setTrySelectRowColor(Color.parseColor("#aa4c84ff"))//尝试选中字体颜色
            .setNormalRowColor(Color.parseColor("#c3c7cb"))
            .setMessageColor(Color.parseColor("#c3c7cb"))
            .setMessagePaintTextSize(this.sp(15))

        lrc_view.commitLrcSettings()
        lrc_view.setNoDataMessage("字幕加载中")
        lrc_view.setLrcViewSeekListener(lrcListener)
    }

    override fun onDestroy() {
        super.onDestroy()
//        MyApp.instance.bookres = null
        SPUtils.getInstance().put("AudioRes", "")
        unbindService(this)
        wifiLock.release()
        wakeLock.release()
    }
}
