package tuoyan.com.xinghuo_dayingindex.ui.video

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.preference.PreferenceManager
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SPUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_audio_lesson.*
import kotlinx.android.synthetic.main.activity_audio_lesson.tv_title
import kotlinx.android.synthetic.main.activity_news_and_audio.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.AudioTime
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.ResourceInfo
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.ui.mine.offline.OfflineActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.NetLessonsPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.util.*


class AudioLessonActivity : LifeActivity<NetLessonsPresenter>(), ServiceConnection {
    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_audio_lesson

    private var service: AudioService? = null
    private var bgColors = mutableListOf<Int>(
        R.color.color_3a3e4d,
        R.color.color_455261,
        R.color.color_3a4b4a,
        R.color.color_475359,
        R.color.color_3a362a
    )
    private var colorPos = -1
    private var timer: CountDownTimer? = null
    private var currentPos = 0

    //1:试听状态进来并且未购买//3离线播放
    private val freeType by lazy { intent.getIntExtra("FREETYPE", 2) }
    private val audioTitle by lazy { intent.getStringExtra("TITLE") ?: "" }
    private val classKey by lazy { intent.getStringExtra("ClASSKEY") ?: "" }
    private var currentRes: ResourceListBean? = null

    //-----用于播放完关闭- duration- currentTime----
    private var duration = 0L
    private var currentTime = 0L

    private var serviceList = mutableListOf<BookRes>()
    private var resList: List<ResourceListBean>? = null
    private var currentInfo: ResourceInfo? = null
    private var htmlStr: StringBuilder = StringBuilder()
    private var startTime = 0L//统计播放进入时间戳


    private val timeDialog by lazy {
        AudioTimeCloseDialog(this@AudioLessonActivity) {
            initCountDownTimer(it)
        }
    }
    private val speedDialog by lazy {
        AudioSpeedDialog(this@AudioLessonActivity) {
            //倍速播放
            tv_speed.text = it.speed.toString()
            service?.speed(it.speed)
        }
    }
    private val audioListDialog by lazy {
        AudioListDialog(this@AudioLessonActivity, resList!!) {
            //播放列表
            service?.toPosition(it)
        }
    }


    override fun onServiceDisconnected(name: ComponentName?) {
        service?.unCallBack()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is AudioService.MBinder) {
            this.service = service.getService()
            if (currentPos >= serviceList.size) return
            this.service?.defSpeed = SpUtil.audioSpeed
            this.service?.loop = SpUtil.audioModel
            this.service?.setData(serviceList, currentPos)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).fullScreen(true).statusBarColor(android.R.color.transparent)
            .statusBarDarkFont(false).init()
        fullScreen = true
        super.onCreate(savedInstanceState)
        startTime = Date().time
    }

    override fun configView() {
        currentPos = intent.getIntExtra("POSITION", 0)
        super.configView()
        web_view.webChromeClient = object : WebChromeClient() {}
        web_view.webViewClient = object : WebViewClient() {}
        initHtmlText()
        tv_toolbar_title.text = audioTitle
        changeData()
        setSupportActionBar(tb_news_audio)
        tb_news_audio.setNavigationOnClickListener { onBackPressed() }
        EventBus.getDefault().register(this)
        initBgColor()
        tv_speed.text = SpUtil.audioSpeed.toString()
        img_play_model.isSelected = SpUtil.audioModel
        val intent = Intent(this@AudioLessonActivity, AudioService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
    }

    //    随机生成背景颜色
    private fun initBgColor() {
        var tempPos = (0..4).random()
        if (colorPos == tempPos) {
            initBgColor()
        } else {
            colorPos = tempPos
            rl_audio.setBackgroundColor(ContextCompat.getColor(this, bgColors[colorPos]))
        }

    }

    private fun changeData() {
//        resList = MyApp.instance.resList
        // 取
        val data2: String = SPUtils.getInstance().getString("ResourceListBean", "")
        val gson2 = Gson()
        val listType2: Type = object : TypeToken<List<ResourceListBean?>?>() {}.getType()
        resList = gson2.fromJson<List<ResourceListBean>>(data2, listType2)
//        // 存
//        val list1: List<ResourceListBean> = ArrayList()
//        val gson1 = Gson()
//        val data1 = gson1.toJson(resList)
//        SPUtils.getInstance().put("ResourceListBean", data1)
//        // 取
//        val data2: String = SPUtils.getInstance().getString("ResourceListBean", "")
//        val gson2 = Gson()
//        val listType2: Type = object : TypeToken<List<String?>?>() {}.getType()
//        val list2 = gson2.fromJson<List<String>>(data2, listType2)
        //
        resList?.forEachIndexed { index, resourceListBean ->
            var item = BookRes()
            //id为空的时候进行本地播放在这查询，playUrl 本地地址，不等于null,id 查询
            item.id = resourceListBean.id
            item.name = resourceListBean.name
            item.playUrl = resourceListBean.playUrl.toString()
            resourceListBean.isChecked = currentPos == index
            serviceList.add(item)
        }
        currentRes = resList?.get(currentPos)
    }

    override fun handleEvent() {
        super.handleEvent()
        img_back.setOnClickListener {
            onBackPressed()
        }
        tv_audio_time.setOnClickListener {
            //定时关闭弹窗展示
            timeDialog.show()
        }
        tv_audio_text.setOnClickListener {
            if (currentInfo!!.textContext.isNotEmpty()) {
                //文稿页面展示
                ImmersionBar.with(this).fullScreen(false).statusBarColor(R.color.colorPrimary)
                    .statusBarDarkFont(true).init()
                initHtml(currentInfo!!)
                layout_audio_text.visibility = View.VISIBLE
            } else {
                toast("暂无文稿")
            }
        }
        ll_speed.setOnClickListener {
            //倍速弹窗展示
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                speedDialog.show()
            } else {
                toast("手机版本不支持倍速播放")
            }
        }
        img_play_model.setOnClickListener {
            if (freeType == 1) {
                //免费试听
                toast("请购买后尝试")
            } else if (freeType == 3) {
                //免费试听
                toast("该功能不支持离线使用")
            } else {
                img_play_model.isSelected = !img_play_model.isSelected
                //默认false
                SpUtil.audioModel = img_play_model.isSelected
                //默认false
                service?.setLoop()
                if (img_play_model.isSelected) {
                    toast("单曲循环")
                } else {
                    toast("顺序播放")
                }
            }
        }
        img_play_state.setOnClickListener {
            service?.play()
        }
        img_audio_pre.setOnClickListener {
            if (freeType == 1) {
                //免费试听
                toast("请购买后尝试")
            } else if (freeType == 3) {
                //免费试听
                toast("该功能不支持离线使用")
            } else {
                //上一曲
                service?.previous()
            }
        }
        img_audio_next.setOnClickListener {
            if (freeType == 1) {
                //免费试听
                toast("请购买后尝试")
            } else if (freeType == 3) {
                //免费试听
                toast("该功能不支持离线使用")
            } else {
                //下一曲
                service?.next()
            }
        }
        img_audio_list.setOnClickListener {
            //播放列表展示
            if (freeType == 1) {
                //免费试听
                toast("请购买后尝试")
            } else if (freeType == 3) {
                //免费试听
                toast("该功能不支持离线使用")
            } else {
                audioListDialog.moveToPosAndShow(currentPos)
            }
        }
        sk_audio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

    override fun onDestroy() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
        unbindService(this)
//        MyApp.instance.resList = null
        SPUtils.getInstance().put("ResourceListBean", "")
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun initCountDownTimer(item: AudioTime) {
        service?.isClose = false
        if (timer != null) {
            timer?.cancel()
            timer = null
            tv_audio_time.text = "定时关闭"
        }
        //audioTimes.add(AudioTime(1, "不开启", 0, true))
        //AudioTime(2, "播完当前音频", 0, false)
        //id ==2时，倒计时=音频duration时间-音频当前播放时间
        if (item.id > 2) {
            timer = object : CountDownTimer(item.time * 60 * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    tv_audio_time.text = formatTime(millisUntilFinished)
                }

                override fun onFinish() {
                    tv_audio_time.text = "定时关闭"
                    //Todo 暂停音乐
                    service?.pause()
                    timeDialog.resetTimes()
                }
            }.start()
        } else if (item.id == 2) {
            //倍速最低0.5，因此时间最多是duration*2，CountDownTimer(duration*2,1000)；
            // onTick时当前播放时间》=duration，timer.cancel,tv_audio_time.text = "定时关闭" 暂停播放
            service?.isClose = true
            timer = object : CountDownTimer(duration * 2L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (duration > currentTime) {
                        var time = duration - currentTime
                        tv_audio_time.text = formatTime(time)
                    } else {
                        tv_audio_time.text = "定时关闭"
                        cancel()
                        timeDialog.resetTimes()
                        currentTime = 0
                    }
                }

                override fun onFinish() {
                    tv_audio_time.text = "定时关闭"
                    timeDialog.resetTimes()
                }
            }.start()
        }
    }

    private fun formatTime(millisUntilFinished: Long): String {
        var minute = 0L
        var second = 0L
        minute = millisUntilFinished / 1000 / 60
        second = millisUntilFinished / 1000 % 60
        if (minute < 10) {
            if (second < 10) {
                return "0${minute}:0${second}"
            } else {
                return "0${minute}:${second}"
            }
        } else {
            if (second < 10) {
                return "${minute}:0${second}"
            } else {
                return "${minute}:${second}"
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun callBacks(event: AudioCallBack) {
        this.service ?: return
        when (event.type) {
            AudioCallBack.TYPE_COMPLETE -> {
                currentTime = duration
                tv_start.text = "00:00"
                tv_start.text = "00:00"
                sk_audio.progress = 0
                sk_audio.secondaryProgress = 0
                sk_audio.max = 0
                postData()
            }
            AudioCallBack.TYPE_ERROR -> {
                tv_start.text = "00:00"
                tv_start.text = "00:00"
                sk_audio.progress = 0
                sk_audio.secondaryProgress = 0
                sk_audio.max = 0
                mToast(event.msg)
            }
            AudioCallBack.TYPE_START -> {
                initBgColor()
                duration = event.time
                start(event.time, event.position)
                if (freeType == 1 || freeType == 3) {
                    this.service?.isClose = true
                }
            }
            AudioCallBack.TYPE_STOP -> {
                tv_start.text = "00:00"
                sk_audio.progress = 0
            }
            AudioCallBack.TYPE_STATUS -> {
                img_play_state.isSelected = event.flag
            }
            AudioCallBack.TYPE_CALLBACK -> {
                currentTime = event.time
                sk_audio.progress = event.time.toInt()
                tv_start.text = formatTime(event.time)
            }
            AudioCallBack.TYPE_RES_INFO -> {
                val temp = resList?.get(event.position)
                tv_teacher_name.text = temp?.teacher ?: ""
                tv_title.text = temp?.name ?: ""
                presenter.getResourceInfo(event.key, "1.2") {
                    currentInfo = it
                    event.function(it)
                    initResInfo(it)
                    var p = DownloadManager.getFilePathWithKey(currentRes!!.id, currentRes!!.type)
                    if (p.isNotEmpty() && File(p).exists()) {
                        var preferences =
                            PreferenceManager.getDefaultSharedPreferences(this@AudioLessonActivity)
                        preferences.edit().putString(p, Gson().toJson(it)).commit()
                    }
                }
            }
            AudioCallBack.TYPE_NET_ERROR -> {
                AlertDialog.Builder(this).setCancelable(false).setTitle("网络异常")
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

    private fun postData() {
        val endTime = Date().time
        val map = mutableMapOf<String, String>()
        map["courseKey"] = classKey
        map["videoKey"] = currentRes?.id ?: ""
        map["startTime"] = startTime.toString()
        map["endTime"] = endTime.toString()
        presenter.recordPlayLog(map)
        startTime = endTime
    }

    override fun onPause() {
        super.onPause()
        postData()
    }

    /**
     * 音频播放开始
     */
    private var totalTime = 0L

    private fun start(time: Long, position: Int) {
        totalTime = time
        tv_start.text = "00:00"
        tv_end.text = formatTime(time)
        sk_audio.progress = 0
        sk_audio.secondaryProgress = 0
        sk_audio.max = time.toInt()
        currentPos = position
        currentRes = resList?.get(position)
        tv_teacher_name.text = currentRes?.teacher ?: ""
        tv_title.text = currentRes?.name ?: ""
        if (freeType == 3 || currentRes?.id.isNullOrBlank()) {
            var preferences =
                PreferenceManager.getDefaultSharedPreferences(this@AudioLessonActivity)
            var info = Gson().fromJson(
                preferences.getString(currentRes?.playUrl, ""), ResourceInfo::class.java
            )
            tv_teacher_name.text = info.teacherName
            currentInfo = info
            initResInfo(info)
        }
    }

    override fun onBackPressed() {
        if (layout_audio_text.visibility == View.VISIBLE) {
            ImmersionBar.with(this).fullScreen(true).statusBarColor(android.R.color.transparent)
                .statusBarDarkFont(false).init()
            layout_audio_text.visibility = View.GONE
        } else {

            super.onBackPressed()
        }
    }

    private fun initHtmlText() {
        var ips: InputStream = resources.assets.open("audio_text.html")
        var reder = BufferedReader(InputStreamReader(ips))
        var str = reder.readLine()

        while (str != null) {
            htmlStr.append(str + "\n")
            str = reder.readLine()
        }
    }

    private fun initResInfo(info: ResourceInfo) {
        Glide.with(this).clear(img_header)
        Glide.with(this).load(info.teacherImg).placeholder(R.mipmap.icon_audio_teacher_header)
            .error(R.mipmap.icon_audio_teacher_header).into(img_header)
        Glide.with(this).clear(img_cover)
        Glide.with(this).load(info.coverImg).into(img_cover)
    }

    private fun initHtml(info: ResourceInfo) {
        var htmlEle = htmlStr.toString()
        htmlEle = htmlEle.replace("\${res_title}", info.name)
        htmlEle = htmlEle.replace("\${res_data}", info.textContext)
        web_view.loadDataWithBaseURL(null, htmlEle, "text/html", "utf-8", null)
    }
}