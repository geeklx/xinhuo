package com.spark.peak.service


import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.spark.peak.R
import com.spark.peak.bean.AudioRes
import com.spark.peak.bean.ResourceInfo
import com.spark.peak.receiver.MusicReceiver
import com.spark.peak.ui.video.AudioActivity
import com.spark.peak.utlis.SystemHelper
import org.jetbrains.anko.ctx
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import java.io.File
import java.util.*

class AudioService : Service() {
    private var callBack: (Long) -> Unit = {}
    private var onCompletion: (Int) -> Unit = {}
    private var onError: (String) -> Unit = {}
    private var start: (Int, Long) -> Unit = { _, _ -> }
    private var status: (Boolean) -> Unit = {}
    private val mData by lazy { mutableListOf<AudioRes>() }
    private var type: String = ""
    private var bookName: String = ""
    private var mediaPlayer: IjkExo2MediaPlayer? = null
    private var resInfo: (String, function: (ResourceInfo?) -> Unit) -> Unit = { _, _ -> }

    private var position = 0
    private var isPerpared = false
    private var isCompletion = false
//    var loop = false //单曲循环
//    var isClose = false

    private val receiver by lazy { MusicReceiver() }
    val CHANNAL_ID = "999"
    var notification: Notification? = null
    var speed = 1f

    fun playRes(res: AudioRes) {
        isCompletion = false
        if (mediaPlayer == null) {
            mediaPlayer = IjkExo2MediaPlayer(ctx)
        } else {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
            mediaPlayer = IjkExo2MediaPlayer(ctx)
        }
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
//        mediaPlayer!!.isLooping = loop
        mediaPlayer!!.setOnCompletionListener { mp ->
            isCompletion = true
            updateNotification(0, 0)
            mp.stop()
//            if (isClose) {
//                isClose = false
//                return@setOnCompletionListener
//                //            mp.reset()
//            }
//            if (!loop && mData.isNotEmpty()) { //列表循环
////                if (position >= mData.size-1) position = 0 //todo 循环播放
//                if (position >= mData.size - 1) return@setOnCompletionListener //todo 顺序播放
//                position++
//                playRes(mData[position])
//            }
            onCompletion(0)
        }
        mediaPlayer!!.setOnErrorListener { player, what, extra ->
            updateNotification(0, 0)
            isCompletion = true
            onError("错误类型=$what")
//            else
//                onError("忽略")
            player.stop()
            player.reset()
            true
        }
        mediaPlayer!!.setOnPreparedListener {
            isPerpared = true
            start(position, it.duration)
            initNotification()
            if (!it.isPlaying) {
                it.start()
                speed(speed)
            }
        }
        val file = File(res.playUrl)
        if (file.exists()) {
            mediaPlayer?.dataSource = file.path
            mediaPlayer?.prepareAsync()
        } else {
            resInfo(res.id!!) {
                res.playUrl = it?.url ?: ""
//                res.name = it?.name ?: ""
                res.lrcurl = it?.lrcurl1 ?: ""
//                res.lrcurl2 = it?.lrcurl2 ?: ""
//                res.lrcurl3 = it?.lrcurl3 ?: ""
                res.downUrl = it?.downloadUrl ?: ""
                mediaPlayer?.dataSource = res.playUrl ?: ""
                mediaPlayer?.prepareAsync()
            }
        }
    }

    private val timer by lazy { Timer(true) }

    /**
     * 刷新进度定时器
     */
    private fun initTask() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    try {
                        status(mediaPlayer?.isPlaying == true)
//                        if (mediaPlayer?.isPlaying == true) {
                        val index = mediaPlayer?.currentPosition ?: 0
                        callBack(index)
                        if (isPerpared) updateNotification(index, 1)
//                        } else {
//                            if (isPerpared) updateNotification(-1, 0)
//                        }
                    } catch (e: Exception) {
                    }

                }
            }
        }, 250, 250)
    }

    fun setCallBack(
        onCompletion: (Int) -> Unit,
        onError: (String) -> Unit,
        onStart: (Int, Long) -> Unit,
        status: (Boolean) -> Unit,
        callBack: (Long) -> Unit = {},
        resInfo: (String, function: (ResourceInfo?) -> Unit) -> Unit
    ) {
        this.onCompletion = onCompletion
        this.onError = onError
        this.start = onStart
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
        timer.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    fun setData(data: List<AudioRes>, position: Int = 0, type: String, bookName: String) {
        this.type = type
        this.bookName = bookName
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
                    notificationManager.cancel(999)
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
        if (isPerpared)
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            } else {
                if (isCompletion) playRes(mData[this.position])
                else mediaPlayer?.start()
            }

    }

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
        this.speed = speed
        mediaPlayer?.setSpeed(speed, 1f)
    }


    fun setProgress(progress: Long) {
        mediaPlayer?.seekTo(progress)
    }

    fun stop() {
        mediaPlayer?.stop()
//        mediaPlayer?.reset()
    }

//    fun setLoop() {
//        loop = !loop
//        mediaPlayer?.isLooping = loop
//    }

    override fun onDestroy() {
        timer.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {
        }
        notificationManager.cancel(999)
    }


    override fun onBind(intent: Intent): IBinder {
        return MBinder()
    }

    inner class MBinder : Binder() {
        fun getService() = this@AudioService
    }

    val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

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
        var pIntentNotification =
            PendingIntent.getActivity(
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
            .setSmallIcon(R.mipmap.logo)
            .setContentIntent(pIntentNotification)
            .setChannelId(packageName)
            .setOngoing(true)
//                .setStyle(NotificationCompat.MediaStyle()
//                        .setMediaSession(MediaSession.Token))
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

        notificationManager.notify(999, notification)
        // 启动前台服务
        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(999, notification)// 开始前台服务
    }

    private fun updateNotification(current: Long = -1L, playing: Int = -1) {
        try {
            notification?.let {
                if (current != -1L) {
                    it.bigContentView.setTextViewText(R.id.tv_current, time(current))
                    it.bigContentView.setProgressBar(
                        R.id.progress_bar,
                        (mediaPlayer?.duration ?: 0L).toInt(),
                        current.toInt(),
                        false
                    )
                }
                if (playing == 1) {
                    it.contentView.setImageViewResource(R.id.btn_play, R.mipmap.notify_pause)
                    it.bigContentView.setImageViewResource(R.id.btn_play, R.mipmap.notify_pause)
                } else if (playing == 0) {
                    it.contentView.setImageViewResource(R.id.btn_play, R.mipmap.notify_play)
                    it.bigContentView.setImageViewResource(R.id.btn_play, R.mipmap.notify_play)
                }
                notificationManager.notify(999, notification)
            }
        } catch (e: Exception) {
        }
    }

    fun time(time: Long): String {
        val i = time / 1000
        val s = i % 60
        val m = i / 60 % 60
        val h = i / 60 / 60 % 24
        return "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}:${if (s < 10) "0$s" else "$s"}"
    }
}
