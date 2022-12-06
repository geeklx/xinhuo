package tuoyan.com.xinghuo_dayingindex.ui.video

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.*
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.ctx
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_CALLBACK
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_COMPLETE
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_ERROR
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_NET_ERROR
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_RES_INFO
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_START
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_STATUS
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_STOP
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.ResourceInfo
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SystemHelper
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import java.io.File
import java.util.*

class AudioService : Service() {
    private var callBack: (Long) -> Unit = {}
    private var onCompletion: (Int) -> Unit = {}
    private var onError: (String) -> Unit = {}
    private var start: (Int, Long) -> Unit = { _, _ -> }
    private var onStop: () -> Unit = {}
    private var status: (Boolean) -> Unit = {}
    private val mData by lazy { mutableListOf<BookRes>() }
    private var mediaPlayer: IjkExo2MediaPlayer? = null
    private var resInfo: (String, function: (ResourceInfo?) -> Unit) -> Unit = { _, _ -> }

    private var error = false
    private var errorDuration = 0L
    var defSpeed = 1f
    private var position = 0
    private var isPerpared = false
    var loop = false //单曲循环
    var isClose = false
    fun playRes(res: BookRes) {
        if (mediaPlayer == null) {
            mediaPlayer = IjkExo2MediaPlayer(ctx)
            speed(defSpeed)
        } else {
            var speed = 1f
            try {
                speed = mediaPlayer!!.speed
            } catch (e: java.lang.Exception) {
                speed = 1f
                e.printStackTrace()
            }
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null
            mediaPlayer = IjkExo2MediaPlayer(ctx)
            speed(speed)
        }
        isPerpared = false
        error = false
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setOnCompletionListener { mp ->
            onCompletion(0)
            EventBus.getDefault().post(AudioCallBack(TYPE_COMPLETE, position = 0))
            updateNotification(0, 0)
            if (isClose) {
                isClose = false
                mp.stop()
                mp.reset()
                mp.release()
                mediaPlayer = null
                return@setOnCompletionListener
            }
            if (!loop && mData.isNotEmpty()) { //列表循环
                position++
                if (position >= mData.size) position = 0
                playRes(mData[position])
            } else if (loop && mData.isNotEmpty()) {
                playRes(mData[position])
            }
        }
        mediaPlayer!!.setOnErrorListener { player, what, extra ->
            updateNotification(0, 0)
            onError("错误类型=$what")
            EventBus.getDefault().post(AudioCallBack(TYPE_ERROR, msg = "错误类型=$what"))
            error = true
            errorDuration = player.currentPosition
            player.stop()
            player.reset()
            player.release()
            isPerpared = false
            mediaPlayer = null
            true
        }
        mediaPlayer!!.setOnPreparedListener {
            isPerpared = true
            start(position, it.duration)
            EventBus.getDefault()
                .post(AudioCallBack(TYPE_START, position = position, time = it.duration))
            initNotification()//TODO 开始播放时，发送notification
            if (!it.isPlaying) {
                it.start()
                if (errorDuration != 0L) {
                    Handler().postDelayed({
                        it.seekTo(errorDuration)
                        errorDuration = 0L
                    }, 300)
                }
            }
        }

        if (res.isDownload() && !NetWorkUtils.isNetWorkReachable()) {
            //TODO 离线中心进入
            val filePath = DownloadManager.getFilePathWithKey(res.id, res.type)
            mediaPlayer?.dataSource = filePath
            mediaPlayer?.prepareAsync()
        } else if (!NetWorkUtils.isNetWorkReachable()) {
            EventBus.getDefault().post(AudioCallBack(TYPE_NET_ERROR))
        } else {
            EventBus.getDefault()
                .post(AudioCallBack(TYPE_RES_INFO, key = res.id, position = position, function = {
                    res.playUrl = it?.url ?: ""
                    res.lrcurl = it?.lrcurl1 ?: ""
                    res.lrcurl2 = it?.lrcurl2 ?: ""
                    res.lrcurl3 = it?.lrcurl3 ?: ""
                    res.downUrl = it?.downloadUrl ?: ""
                    res.lrcKey2 = it?.lrcKey2 ?: ""
                    val file = File(DownloadManager.getFilePathWithKey(res.id ?: "", res.type ?: ""))
                    if (file.exists()) {
                        mediaPlayer?.dataSource = file.path
                        mediaPlayer?.prepareAsync()
                    } else {
                        mediaPlayer?.dataSource = res.playUrl ?: ""
                        mediaPlayer?.prepareAsync()
                    }
                }))
        }

    }

    private var timer: Timer? = null

    /**
     * 刷新进度定时器
     */
    fun initTask() {
        cancelTask()
        timer = Timer(true)
        var times = 1
        timer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    try {
                        status(mediaPlayer?.isPlaying == true)
                        EventBus.getDefault()
                            .post(AudioCallBack(TYPE_STATUS, flag = mediaPlayer?.isPlaying == true))
                        if (mediaPlayer?.isPlaying == true) {
                            val index = mediaPlayer?.currentPosition ?: 0
                            callBack(index)
                            EventBus.getDefault().post(AudioCallBack(TYPE_CALLBACK, time = index))
                            if (isPerpared) {
                                if (times % 4 == 0) {
                                    times = 1
                                    updateNotification(index, 1)
                                } else {
                                    times = times.inc()
                                }
                            }
                        } else {
                            if (isPerpared) {
                                if (times % 4 == 0) {
                                    times = 1
                                    updateNotification(-1, 0)
                                } else {
                                    times = times.inc()
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }, 1000, 500)
    }

    fun cancelTask() {
        timer?.cancel()
        timer = null
    }

    fun setCallBack(
        onCompletion: (Int) -> Unit,
        onError: (String) -> Unit,
        onStart: (Int, Long) -> Unit,
        onStop: () -> Unit,
        status: (Boolean) -> Unit,
        callBack: (Long) -> Unit = {},
        resInfo: (String, function: (ResourceInfo?) -> Unit) -> Unit
    ) {
        this.onCompletion = onCompletion
        this.onError = onError
        this.start = onStart
        this.onStop = onStop
        this.status = status
        this.callBack = callBack
        this.resInfo = resInfo
        initTask()
    }

    fun unCallBack() {
        onCompletion = {}
        onError = {}
        start = { _, _ -> }
        callBack = {}
        onStop = {}
        cancelTask()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data = intent?.getSerializableExtra("BookRes") as List<BookRes>
        val pos = intent.getIntExtra("Pos", 0)
        setData(data, pos)
        return super.onStartCommand(intent, flags, startId)
    }

    fun setData(data: List<BookRes>, position: Int = 0) {
        initTask()//TODO 开启状态轮询
        //TODO 注册广播
        var intentFilter = IntentFilter()
        intentFilter.addAction(MusicReceiver.PLAYER_TAG)
        receiver.callBacks = object : MusicReceiver.CallBacks {
            override fun open() {
                SystemHelper.setTopApp(ctx)
                SystemHelper.collapseStatusBar(ctx)
            }

            override fun playOrPause() {
                play()
            }

            override fun previous() {
                this@AudioService.previous()
            }

            override fun next() {
                this@AudioService.next()
            }

            override fun close() {
                stop()
                Handler().postDelayed({
                    notificationManager.cancel(111)
                    stopForeground(true)
                }, 300)
            }
        }
        try {
            registerReceiver(receiver, intentFilter)
        } catch (e: Exception) {
        }

        this.position = if (position >= data.size) 0 else position
        if (data.isEmpty()) return
        mData.addAll(data)
        playRes(mData[this.position])
    }

    fun play() {
        if (mData.isNotEmpty()) {
            if (error) {
                playRes(mData[position])
                error = false
            } else if (isPerpared) {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.start()
                }
            } else {
                playRes(mData[position])
            }
        } else {
            toast("请重新加载该页面")
        }
    }

    fun isPerpared() = isPerpared

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }


    /**
     * 上一首
     */
    fun previous() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        if (mData.isEmpty()) return
        position--
        if (position < 0) {
            position++
            toast("没有上一首")
            return
        }
        playRes(mData[position])
    }

    /**
     * 下一首
     */
    fun next() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        if (mData.isEmpty()) return
        position++
        if (position >= mData.size) {
            position--
            toast("没有下一首")
            return
        }
        playRes(mData[position])
    }

    /**
     * 指定位置
     */
    fun toPosition(position: Int) {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        if (mData.isEmpty()) return
        this.position = position
        if (this.position >= mData.size) {
            this.position = 0
            return
        }
        playRes(mData[position])
    }

    /**
     * 倍速
     */
    fun speed(speed: Float) {
        mediaPlayer?.setSpeed(speed, 1f)
    }

    fun setProgress(progress: Long) {
        mediaPlayer?.seekTo(progress)

    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        isPerpared = false
        onStop()
        EventBus.getDefault().post(AudioCallBack(TYPE_STOP))
    }

    fun setLoop() {
        loop = !loop
    }

    override fun onDestroy() {
        cancelTask()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {
        }
        notificationManager.cancel(111)
    }


    override fun onBind(intent: Intent): IBinder {
        return MBinder()
    }

    inner class MBinder : Binder() {
        fun getService() = this@AudioService
    }

    //TODO ------------------------------------------------------------------------------------------
    fun time(time: Long): String {
        val i = time / 1000
        val s = i % 60
        val m = i / 60 % 60
        val h = i / 60 / 60 % 24
        return "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}:${if (s < 10) "0$s" else "$s"}"
    }

    private val receiver by lazy { MusicReceiver() }

    val CHANNAL_ID = "100"
    val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    var notification: Notification? = null
    private fun initNotification() {
        if (mData.isEmpty() || position >= mData.size) return
        var contentView = RemoteViews(packageName, R.layout.view_player_notification)
        contentView.setTextViewText(R.id.tv_music_name, mData[position].name)
        contentView.setTextViewText(R.id.tv_total, time(mediaPlayer?.duration ?: 0L))
        contentView.setProgressBar(
            R.id.progress_bar, (mediaPlayer?.duration
                ?: 0L).toInt(), 0, false
        )

        var intentPause = Intent(MusicReceiver.PLAYER_TAG)
        intentPause.putExtra("status", "pause")
        var pIntentPause =
            PendingIntent.getBroadcast(this, 2, intentPause, PendingIntent.FLAG_UPDATE_CURRENT)
        contentView.setOnClickPendingIntent(R.id.btn_play, pIntentPause)//TODO 设置点击事件

        var intentPrevious = Intent(MusicReceiver.PLAYER_TAG)
        intentPrevious.putExtra("status", "previous")
        var pIntentPrevious =
            PendingIntent.getBroadcast(this, 3, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT)
        contentView.setOnClickPendingIntent(R.id.previous, pIntentPrevious)//TODO 设置点击事件

        var intentNext = Intent(MusicReceiver.PLAYER_TAG)
        intentNext.putExtra("status", "next")
        var pIntentNext =
            PendingIntent.getBroadcast(this, 4, intentNext, PendingIntent.FLAG_UPDATE_CURRENT)
        contentView.setOnClickPendingIntent(R.id.next, pIntentNext)//TODO 设置点击事件

        var intentClose = Intent(MusicReceiver.PLAYER_TAG)
        intentClose.putExtra("status", "close")
        var pIntentClose =
            PendingIntent.getBroadcast(this, 5, intentClose, PendingIntent.FLAG_UPDATE_CURRENT)
        contentView.setOnClickPendingIntent(R.id.btn_close, pIntentClose)//TODO 设置点击事件

        var intentOpen = Intent(MusicReceiver.PLAYER_TAG)
        intentOpen.putExtra("status", "open")
        var pIntentOpen =
            PendingIntent.getBroadcast(this, 6, intentOpen, PendingIntent.FLAG_UPDATE_CURRENT)
        contentView.setOnClickPendingIntent(R.id.rl_root, pIntentOpen)//TODO 设置点击事件

        var notificationIntent = Intent(this, AudioActivity.javaClass)
        var pIntentNotification = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        var builder = NotificationCompat.Builder(this, CHANNAL_ID)

        var smallContentView = RemoteViews(packageName, R.layout.view_player_notification_small)
        smallContentView.setTextViewText(R.id.tv_music_name, mData[position].name)
        smallContentView.setTextViewText(R.id.tv_total, time(mediaPlayer?.duration ?: 0L))
        smallContentView.setOnClickPendingIntent(R.id.btn_play, pIntentPause)//TODO 设置点击事件
        smallContentView.setOnClickPendingIntent(R.id.previous, pIntentPrevious)//TODO 设置点击事件
        smallContentView.setOnClickPendingIntent(R.id.next, pIntentNext)//TODO 设置点击事件
        smallContentView.setOnClickPendingIntent(R.id.btn_close, pIntentClose)//TODO 设置点击事件
        smallContentView.setOnClickPendingIntent(R.id.rl_root, pIntentOpen)//TODO 设置点击事件


        builder.setTicker("sparke")
            .setContentTitle("sparke")
            .setContentText("sparke")
            .setCustomContentView(smallContentView)
            .setCustomBigContentView(contentView)
            .setSmallIcon(R.drawable.logo_corner)
            .setContentIntent(pIntentNotification)
            .setChannelId(packageName)
            .setOngoing(true)
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        notification = builder.build()
        notification!!.flags = Notification.FLAG_NO_CLEAR


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                packageName,
                packageName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(111, notification)
        // 启动前台服务
        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(111, notification)// 开始前台服务
    }

    private fun updateNotification(current: Long = -1L, playing: Int = -1) {
//        initNotification()
        try {
            notification?.let {
                if (current != -1L) {
                    it.bigContentView.setTextViewText(R.id.tv_current, time(current))
                    it.bigContentView.setProgressBar(
                        R.id.progress_bar, (mediaPlayer?.duration
                            ?: 0L).toInt(), current.toInt(), false
                    )
                }
                if (playing == 1) {
                    it.contentView.setImageViewResource(R.id.btn_play, R.drawable.notify_pause)
                    it.bigContentView.setImageViewResource(R.id.btn_play, R.drawable.notify_pause)
                } else if (playing == 0) {
                    it.contentView.setImageViewResource(R.id.btn_play, R.drawable.notify_play)
                    it.bigContentView.setImageViewResource(R.id.btn_play, R.drawable.notify_play)
                }
                notificationManager.notify(111, notification)
            }
        } catch (e: Exception) {
        }
    }
}
