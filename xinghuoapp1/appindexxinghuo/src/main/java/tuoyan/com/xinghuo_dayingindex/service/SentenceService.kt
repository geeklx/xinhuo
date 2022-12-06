package tuoyan.com.xinghuo_dayingindex.service

import android.app.*
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.runOnUiThread
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_SENTENCE_CALLBACK
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_SENTENCE_COMPLETE
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_SENTENCE_DURATION
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_SENTENCE_ERROR
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_SENTENCE_INFO
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_SENTENCE_NET_ERROR
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_SENTENCE_START
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_SENTENCE_STATUS
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack.Companion.TYPE_SENTENCE_STOP
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.LrcDetail
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import java.io.File
import java.util.*

class SentenceService : Service() {
    private var mData: BookRes? = null
    private var lrcDatas: List<LrcDetail>? = null

    private var mediaPlayer: IjkExo2MediaPlayer? = null
    private var isPrepared = false

    private val handler by lazy { Handler() }
    var isPlaying = false
        set(value) {
            field = value
        }

    private var SPEED_SELECT = 1.0f//选择倍速播放
    private var TIME_SELECT = 0//选择的时间间隔
    private var PLAY_NUM_SELECT = 1//播放次数
    private var AUTO_PLAY_NEXT = false//是否自动播放下一句
    private var IS_PLAY_WARMING = true//是否播放提示音

    private var mSoundPool: SoundPool? = null
    private var mStreamId = 0

    var currentLrcPos = 0
        //当前句子播放位置
        set(value) {
            field = value
        }
    private var currentPlayNum = 1//当前句子播放遍数

    public fun setSetting(speedSelect: Float, timeSelect: Int, playNum: Int, autoPlayNext: Boolean, isPlayWarming: Boolean) {
        this.SPEED_SELECT = speedSelect
        this.TIME_SELECT = timeSelect
        this.PLAY_NUM_SELECT = playNum
        this.AUTO_PLAY_NEXT = autoPlayNext
        this.IS_PLAY_WARMING = isPlayWarming
    }

    fun playRes(res: BookRes) {
        if (mediaPlayer == null) {
            mediaPlayer = IjkExo2MediaPlayer(this)
        } else {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            mediaPlayer = null
            mediaPlayer = IjkExo2MediaPlayer(this)
        }
        isPrepared = false
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setOnCompletionListener { mp ->
            EventBus.getDefault().post(AudioCallBack(TYPE_SENTENCE_COMPLETE))
            updateNotification(0, 0)
        }
        mediaPlayer!!.setOnErrorListener { player, what, extra ->
            isPrepared = false
            updateNotification(0, 0)
            EventBus.getDefault().post(AudioCallBack(TYPE_SENTENCE_ERROR, msg = "错误类型=$what"))
            player.stop()
            player.reset()
            player.release()
            true
        }
        mediaPlayer!!.setOnPreparedListener { player ->
            EventBus.getDefault().post(AudioCallBack(TYPE_SENTENCE_DURATION, time = player.duration))
            isPrepared = true
            initNotification()//TODO 开始播放时，发送notification
        }

//        if (res.id.isNullOrBlank()) {
//            //TODO 离线中心进入
//            mediaPlayer?.dataSource = res.playUrl ?: ""
//            mediaPlayer?.prepareAsync()
//        } else
        if (!NetWorkUtils.isNetWorkReachable()) {
            EventBus.getDefault().post(AudioCallBack(TYPE_SENTENCE_NET_ERROR))
        } else {
            EventBus.getDefault()
                .post(AudioCallBack(TYPE_SENTENCE_INFO, key = res.id, getLrc = { list ->
                    lrcDatas = list
                }))
            val file = File(DownloadManager.getFilePathWithKey(res.id ?: "", res.type ?: ""))
            if (file.exists()) {
                mediaPlayer?.dataSource = file.path
                mediaPlayer?.prepareAsync()
            } else {
                mediaPlayer?.dataSource = res.playUrl ?: ""
                mediaPlayer?.prepareAsync()
            }
        }
    }

    private var timer: Timer? = null
    private var times = 1

    /**
     * 刷新进度定时器
     */
    fun initTask() {
        cancelTask()
        timer = Timer(true)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    try {
                        val currentPosition = mediaPlayer?.currentPosition ?: 0
                        val duration = mediaPlayer?.duration ?: 0
                        val totalSize = lrcDatas?.size ?: 0
                        val currentLrcRow = lrcDatas?.get(currentLrcPos)
                        currentLrcRow?.let { lrc ->
                            val nextTime = if (totalSize - 1 > currentLrcPos) {
                                lrcDatas?.get(currentLrcPos + 1)?.startTime ?: 0
                            } else {
                                //最后一个
                                duration
                            }
                            /**
                             * 如播放次数为无限(-1)，播放到下一个时间点，暂停，开始计时间隔，再次播放 次数+1
                             *当前句子播放到下一个时间点，播放次数是否到达，如果次数没有到达，暂停，开始计时间隔，再次播放 次数+1
                             * 如果到达，次数归1，查看是否自动播放下一句，若是继续播放，否则暂停
                             */
                            if (-1 == PLAY_NUM_SELECT && currentPosition + 150 >= nextTime || currentPlayNum < PLAY_NUM_SELECT && currentPosition + 150 >= nextTime) {
                                pause()
                                seekTo(lrc.startTime)
                                pause()//防止有的设备 seekTo 之后自动播放
                                runOnUiThread {
                                    handler.postDelayed({
                                        currentPlayNum++
                                        play()
                                    }, TIME_SELECT * 1000L)
                                }

                                if (IS_PLAY_WARMING) {
                                    //播放提示音
                                    playSound()
                                }
                            } else if (AUTO_PLAY_NEXT && currentPosition + 150 >= nextTime && currentPosition + 150 < duration) {
                                pause()
                                currentPlayNum = 1
                                currentLrcPos++
                                runOnUiThread {
                                    handler.postDelayed({
                                        play()
                                    }, TIME_SELECT * 1000L)
                                }
                                if (IS_PLAY_WARMING) {
                                    //播放提示音
                                    playSound()
                                }
                            } else if (currentPosition + 150 >= nextTime) {
                                currentPlayNum = 1
                                isPlaying = false
                                pause()
                                if (IS_PLAY_WARMING) {
                                    //播放提示音
                                    playSound()
                                }
                            }
                        }
                        EventBus.getDefault().post(AudioCallBack(TYPE_SENTENCE_STATUS, flag = isPlaying))
                        EventBus.getDefault().post(AudioCallBack(TYPE_SENTENCE_CALLBACK, position = currentLrcPos))
                        if (mediaPlayer?.isPlaying == true && times % 4 == 0) {
                            times = 1
                            updateNotification(currentPosition, 1)
                        } else if (mediaPlayer?.isPlaying == true) {
                            times = times.inc()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }, 0, 150)
    }

    fun cancelTask() {
        timer?.cancel()
        timer = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data = intent?.getSerializableExtra("BookRes") as? BookRes
        data?.let { setData(it) }
        return super.onStartCommand(intent, flags, startId)
    }

    fun setData(data: BookRes) {
        //TODO 注册广播
//        val intentFilter = IntentFilter()
//        intentFilter.addAction(MusicReceiver.PLAYER_TAG)
//        receiver.callBacks = object : MusicReceiver.CallBacks {
//            override fun open() {
//                SystemHelper.setTopApp(this@SentenceService)
//                SystemHelper.collapseStatusBar(this@SentenceService)
//            }
//
//            override fun playOrPause() {
//                playOrPause()
//            }
//
//            override fun previous() {
//            }
//
//            override fun next() {
//            }
//
//            override fun close() {
//                stop()
//                Handler().postDelayed({
//                    notificationManager.cancel(111)
//                    stopForeground(true)
//                }, 300)
//            }
//        }
//        try {
//            registerReceiver(receiver, intentFilter)
//        } catch (e: Exception) {
//        }
        this.mData = data
        playRes(data)
    }

    fun play() {
        if (isPrepared) {
            isPlaying = true
            speed(this.SPEED_SELECT)
            initTask()
            mediaPlayer?.start()
            EventBus.getDefault().post(AudioCallBack(TYPE_SENTENCE_START))
        }
    }

    private fun pause() {
        if (isPrepared) {
            cancelTask()
            mediaPlayer?.pause()
            updateNotification(-1, 0)
        }
    }

    fun allPause() {
        cancelTask()
        mediaPlayer?.pause()
        handler.removeCallbacksAndMessages(null)
        isPlaying = false
        updateNotification(-1, 0)
    }

    fun playOrPause() {
        if (isPrepared && true == mediaPlayer?.isPlaying) {
            cancelTask()
            mediaPlayer?.pause()
        } else if (isPrepared) {
            initTask()
            mediaPlayer?.start()
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        EventBus.getDefault().post(AudioCallBack(TYPE_SENTENCE_STOP))
    }

    /**
     * 倍速
     */
    fun speed(speed: Float) {
        mediaPlayer?.setSpeed(speed, 1f)
    }


    fun isPrepared() = isPrepared

    fun seekTo(progress: Long) {
        mediaPlayer?.seekTo(progress)
    }

    fun getDuration(): Long {
        return mediaPlayer?.duration ?: 0L
    }

    private fun reset() {
        allPause()
        SPEED_SELECT = 1.0f//选择倍速播放
        TIME_SELECT = 0//选择的时间间隔
        PLAY_NUM_SELECT = 1//播放次数
        AUTO_PLAY_NEXT = false//是否自动播放下一句
        IS_PLAY_WARMING = true//是否播放提示音

        currentLrcPos = 0
        currentPlayNum = 1

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        releaseSoundPool()
        //        try {
//            unregisterReceiver(receiver)
//        } catch (e: Exception) {
//        }
//        notificationManager.cancel(111)
    }


    override fun onDestroy() {
        reset()
    }


    override fun onBind(intent: Intent): IBinder {
        return MBinder()
    }

    inner class MBinder : Binder() {
        fun getService() = this@SentenceService
    }

    fun formatTime(time: Long): String {
        val i = time / 1000
        val s = i % 60
        val m = i / 60
        return "${if (m < 10) "0$m" else "$m"}:${if (s < 10) "0$s" else "$s"}"
    }

    override fun onCreate() {
        super.onCreate()
        createSoundPool()
    }

    private fun createSoundPool() {
        if (mSoundPool == null) {
            mSoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
                SoundPool.Builder()
                    .setMaxStreams(16)
                    .setAudioAttributes(audioAttributes)
                    .build()
            } else {
                SoundPool(16, AudioManager.STREAM_MUSIC, 0)
            }
            mStreamId = mSoundPool?.load(this.applicationContext, R.raw.sentence_sound, 1) ?: 0
        }
    }

    private fun playSound() {
        mSoundPool?.play(mStreamId, 1f, 1f, 16, 0, 1f)
    }

    private fun releaseSoundPool() {
        mSoundPool?.let { pool ->
            pool.autoPause()
            pool.unload(mStreamId)
            mStreamId = 0
            pool.release()
        }
        mSoundPool = null
    }

//    private val receiver by lazy { MusicReceiver() }

    val CHANNAL_ID = "100"

    //    val notificationManager: NotificationManager by lazy {
//        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    }
    var notification: Notification? = null
    private fun initNotification() {
//        mData?.let { data ->
//            val contentView = RemoteViews(packageName, R.layout.view_player_notification)
//            contentView.setTextViewText(R.id.tv_music_name, data.name)
//            contentView.setTextViewText(R.id.tv_total, formatTime(mediaPlayer?.duration ?: 0L))
//            contentView.setProgressBar(R.id.progress_bar, (mediaPlayer?.duration ?: 0L).toInt(), 0, false)
//
//            val intentPause = Intent(MusicReceiver.PLAYER_TAG)
//            intentPause.putExtra("status", "pause")
//            val pIntentPause = PendingIntent.getBroadcast(this, 2, intentPause, PendingIntent.FLAG_UPDATE_CURRENT)
//            contentView.setOnClickPendingIntent(R.id.btn_play, pIntentPause)//TODO 设置点击事件
//
//            val intentPrevious = Intent(MusicReceiver.PLAYER_TAG)
//            intentPrevious.putExtra("status", "previous")
//            val pIntentPrevious = PendingIntent.getBroadcast(this, 3, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT)
//            contentView.setOnClickPendingIntent(R.id.previous, pIntentPrevious)//TODO 设置点击事件
//
//            val intentNext = Intent(MusicReceiver.PLAYER_TAG)
//            intentNext.putExtra("status", "next")
//            val pIntentNext = PendingIntent.getBroadcast(this, 4, intentNext, PendingIntent.FLAG_UPDATE_CURRENT)
//            contentView.setOnClickPendingIntent(R.id.next, pIntentNext)//TODO 设置点击事件
//
//            val intentClose = Intent(MusicReceiver.PLAYER_TAG)
//            intentClose.putExtra("status", "close")
//            val pIntentClose = PendingIntent.getBroadcast(this, 5, intentClose, PendingIntent.FLAG_UPDATE_CURRENT)
//            contentView.setOnClickPendingIntent(R.id.btn_close, pIntentClose)//TODO 设置点击事件
//
//            val intentOpen = Intent(MusicReceiver.PLAYER_TAG)
//            intentOpen.putExtra("status", "open")
//            val pIntentOpen = PendingIntent.getBroadcast(this, 6, intentOpen, PendingIntent.FLAG_UPDATE_CURRENT)
//            contentView.setOnClickPendingIntent(R.id.rl_root, pIntentOpen)//TODO 设置点击事件
//
//            val notificationIntent = Intent(this, SentenceListenActivity.javaClass)
//            val pIntentNotification = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//            val builder = NotificationCompat.Builder(this, CHANNAL_ID)
//
//            val smallContentView = RemoteViews(packageName, R.layout.view_player_notification_small)
//            smallContentView.setTextViewText(R.id.tv_music_name, data.name)
//            smallContentView.setTextViewText(R.id.tv_total, formatTime(mediaPlayer?.duration ?: 0L))
//            smallContentView.setOnClickPendingIntent(R.id.btn_play, pIntentPause)//TODO 设置点击事件
////            smallContentView.setOnClickPendingIntent(R.id.previous, pIntentPrevious)//TODO 设置点击事件
////            smallContentView.setOnClickPendingIntent(R.id.next, pIntentNext)//TODO 设置点击事件
//            smallContentView.setOnClickPendingIntent(R.id.btn_close, pIntentClose)//TODO 设置点击事件
//            smallContentView.setOnClickPendingIntent(R.id.rl_root, pIntentOpen)//TODO 设置点击事件
//
//            builder.setTicker("sparke")
//                .setContentTitle("sparke")
//                .setContentText("sparke")
//                .setCustomContentView(smallContentView)
//                .setCustomBigContentView(contentView)
//                .setSmallIcon(R.drawable.logo_corner)
//                .setContentIntent(pIntentNotification)
//                .setChannelId(packageName)
//                .setOngoing(true)
//            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            notification = builder.build()
//            notification!!.flags = Notification.FLAG_NO_CLEAR
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(packageName, packageName, NotificationManager.IMPORTANCE_LOW)
//                notificationManager.createNotificationChannel(channel)
//            }
//
//            notificationManager.notify(111, notification)
//            // 启动前台服务
//            // 参数一：唯一的通知标识；参数二：通知消息。
//            startForeground(111, notification)// 开始前台服务
//        }
    }

    private fun updateNotification(current: Long = -1L, playing: Int = -1) {
//        try {
//            notification?.let {
//                if (current != -1L) {
//                    it.bigContentView.setTextViewText(R.id.tv_current, formatTime(current))
//                    it.bigContentView.setProgressBar(R.id.progress_bar, (mediaPlayer?.duration ?: 0L).toInt(), current.toInt(), false)
//                }
//                if (playing == 1) {
//                    it.contentView.setImageViewResource(R.id.btn_play, R.drawable.notify_pause)
//                    it.bigContentView.setImageViewResource(R.id.btn_play, R.drawable.notify_pause)
//                } else if (playing == 0) {
//                    it.contentView.setImageViewResource(R.id.btn_play, R.drawable.notify_play)
//                    it.bigContentView.setImageViewResource(R.id.btn_play, R.drawable.notify_play)
//                }
//                notificationManager.notify(111, notification)
//            }
//        } catch (e: Exception) {
//        }
    }
}
