package tuoyan.com.xinghuo_dayingindex.ui.book

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_ebook_exercise.*
import kotlinx.android.synthetic.main.activity_sentence_listen.*
import kotlinx.android.synthetic.main.activity_sentence_listen.toolbar
import kotlinx.android.synthetic.main.fragment_sentence.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.AudioCallBack
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.LrcData
import tuoyan.com.xinghuo_dayingindex.bean.LrcDetail
import tuoyan.com.xinghuo_dayingindex.service.SentenceService
import tuoyan.com.xinghuo_dayingindex.ui.dialog.SentenceListDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.SentenceSettingDialog
import tuoyan.com.xinghuo_dayingindex.ui.main.PagerV4Adapter
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

class SentenceListenActivity : LifeActivity<BasePresenter>(), ServiceConnection {
    override val layoutResId: Int
        get() = R.layout.activity_sentence_listen
    override val presenter: BasePresenter
        get() = BasePresenter(this)

    private val audioData by lazy { intent.getSerializableExtra(AUDIO) as? BookRes }
    private val sourceType by lazy { intent.getStringExtra(SOURCE_TYPE) ?: "2" }

    var lrcData: LrcData? = null//字幕列表文件
    private val sentenceFragments by lazy { mutableListOf<Fragment>() }
    private var currentPos = 0
    private var isTouchVp = false

    private var service: SentenceService? = null

    private val wifiLock by lazy { (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock") }
    private val wakeLock by lazy { (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelocktag") }

    private val settingDialog by lazy {
        SentenceSettingDialog(this@SentenceListenActivity) { speed, time, loop, isSort, isWarming ->
            img_setting.isSelected = speed != 1.0f || time != 0 || loop != 1 || isSort || !isWarming
            service?.setSetting(speed, time, loop, isSort, isWarming)
        }
    }

    private val sentenceDialog by lazy {
        SentenceListDialog(this@SentenceListenActivity) {
            val pos = lrcData?.list?.indexOf(it) ?: 0
            service?.currentLrcPos = pos
            vp_content.currentItem = pos
            setSentencePlayState(pos, true == service?.isPlaying)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .fullScreen(true)
            .statusBarColor(R.color.transparent)
            .statusBarDarkFont(true)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()
        fullScreen = true
        super.onCreate(savedInstanceState)
    }

    override fun configView() {
        initStatusBar()
        super.configView()
        EventBus.getDefault().register(this)
        val intent = Intent(this, SentenceService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        wifiLock.acquire()
        wakeLock.acquire()
        vp_content.pageMargin = DeviceUtil.dp2px(this@SentenceListenActivity, 15f).toInt()
        vp_content.offscreenPageLimit = 1000
    }

    override fun initData() {
        super.initData()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        when {
            settingDialog.isShowing -> settingDialog.dismiss()
            else -> super.onBackPressed()
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        img_setting.setOnClickListener {
            settingDialog.show()
        }
        img_ch_en.setOnClickListener {
            img_ch_en.isSelected = !img_ch_en.isSelected
            showCh2En(vp_content.currentItem)
        }
        img_list.setOnClickListener {
            sentenceDialog.show()
            sentenceDialog.notifyDataSetChanged(lrcData?.signNum ?: 0 <= 0)
        }
        vp_content.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if(sentenceDialog.isShowing){
                    val list = lrcData?.list
                    list?.let { lrcDetails ->
                        sentenceDialog.setPlayId(lrcDetails[position].lrcDetailKey, position)
                    }
                }
                //前一个始终暂停状态，当前位置根据是否正在播放来确认
                setSentenceState(currentPos, false)
                currentPos = position
                if (isTouchVp) {
                    service?.currentLrcPos = position
                    setSentencePlayState(position, true == service?.isPlaying)
                } else {
                    setSentenceState(position, true == service?.isPlaying)
                }
                showCh2En(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_DRAGGING -> isTouchVp = true
//                    ViewPager.SCROLL_STATE_SETTLING ->
                    ViewPager.SCROLL_STATE_IDLE -> {
                        if (isTouchVp) {
                            Handler().postDelayed({
                                isTouchVp = false
                            }, 1000)
                        }
                    }
                }
            }
        })

        sentenceDialog.setOnDismissListener {
            ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                .init()
        }
        settingDialog.setOnDismissListener {
            ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                .init()
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        unbindService(this)
        wifiLock.release()
        wakeLock.release()
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun callBacks(event: AudioCallBack) {
        this.service ?: return
        when (event.type) {
            AudioCallBack.TYPE_SENTENCE_COMPLETE -> {
            }
            AudioCallBack.TYPE_SENTENCE_ERROR -> {
            }
            AudioCallBack.TYPE_SENTENCE_DURATION -> {
                sentenceDialog.setDuration(event.time)
            }
            AudioCallBack.TYPE_SENTENCE_START -> {
                lrcData?.let { data ->
                    val lrcDetail = data.list[vp_content.currentItem]
                    lrcDetail.listenNum += 1
                    val map = mutableMapOf<String, String>()
                    map["lrcKey"] = lrcDetail.lrcKey ?: ""
                    map["lrcDetailKey"] = lrcDetail.lrcDetailKey ?: ""
                    map["listenNum"] = "${lrcDetail.listenNum}"
                    presenter.recordLrc(map) {}
                }
            }
            AudioCallBack.TYPE_SENTENCE_STOP -> {
            }
            AudioCallBack.TYPE_SENTENCE_STATUS -> {
                setSentenceState(vp_content.currentItem, event.flag)
            }
            AudioCallBack.TYPE_SENTENCE_CALLBACK -> {
                if (vp_content.currentItem != event.position && !isTouchVp) {
                    vp_content.currentItem = event.position
                }
            }
            AudioCallBack.TYPE_SENTENCE_INFO -> {
                audioData?.let { info ->
                    if (!info.lrcKey2.isNullOrEmpty()) {
                        presenter.getLrcDetail(info.lrcKey2) { data ->
                            lrcData = data
                            tv_collect.text = "共标记${data.signNum}"
                            sentenceFragments.clear()
                            data.list.forEach { lrcDetail ->
                                lrcDetail.lrcKey = info.lrcKey2
                                sentenceFragments.add(SentenceFragment.newInstance(lrcDetail))
                            }
                            event.getLrc(data.list)
                            vp_content.adapter = PagerV4Adapter(supportFragmentManager, sentenceFragments as ArrayList<Fragment>)
                            sentenceDialog.setData(data.list, lrcData?.signNum ?: 0 <= 0)
                        }
                    }
                }
            }
            AudioCallBack.TYPE_SENTENCE_NET_ERROR -> {
            }
        }
    }

    private fun initStatusBar() {
        val params = top.layoutParams as ConstraintLayout.LayoutParams
        params.height = ImmersionBar.getStatusBarHeight(this)
    }

    fun setSignNum(disNum: Int) {
        lrcData?.let { data ->
            data.signNum = data.signNum + disNum
            tv_collect.text = "共标记${data.signNum}"
        }
    }

    fun playCurrentPage(lrcRow: LrcDetail) {
        if (service?.isPlaying == true) {
            service?.allPause()
            setSentenceState(vp_content.currentItem, false)
        } else {
            seekToCurrentPage(lrcRow)
        }
    }

    fun seekToCurrentPage(lrcRow: LrcDetail) {
        service?.seekTo(lrcRow.startTime)
        service?.play()
    }

    fun setSentencePlayState(pos: Int, state: Boolean) {
        service?.allPause()
        when (val fragment = sentenceFragments[pos]) {
            is SentenceFragment -> {
                fragment.setImgStateAndPlay(state)
            }
        }
    }

    fun setSentenceState(pos: Int, state: Boolean) {
        when (val fragment = sentenceFragments[pos]) {
            is SentenceFragment -> {
                fragment.setImgState(state)
            }
        }
    }

    fun showCh2En(pos: Int) {
        when (val fragment = sentenceFragments[pos]) {
            is SentenceFragment -> {
                fragment.showChAndEn(img_ch_en.isSelected)
            }
        }
    }

    companion object {
        const val SOURCE_TYPE = "sourceType"
        const val AUDIO = "AUDIO"
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is SentenceService.MBinder) {
            this.service = service.getService()
            audioData?.let { data ->
                this.service?.setData(data)
            }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }
}