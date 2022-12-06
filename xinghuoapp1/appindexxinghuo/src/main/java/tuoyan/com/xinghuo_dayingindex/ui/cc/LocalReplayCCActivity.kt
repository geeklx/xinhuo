package tuoyan.com.xinghuo_dayingindex.ui.cc
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bokecc.livemodule.base.BaseReplayRoomLayout
import com.bokecc.livemodule.localplay.DWLocalReplayCoreHandler
import com.bokecc.livemodule.localplay.DWLocalReplayCoreHandler.LocalTemplateUpdateListener
import com.bokecc.livemodule.localplay.chat.LocalReplayChatComponent
import com.bokecc.livemodule.localplay.doc.DWLocalReplayDocPageInfoListComponent
import com.bokecc.livemodule.localplay.doc.LocalReplayDocComponent
import com.bokecc.livemodule.localplay.qa.LocalReplayQAComponent
import com.bokecc.livemodule.localplay.video.LocalReplayVideoView
import com.bokecc.livemodule.view.BaseRelativeLayout
import com.bokecc.sdk.mobile.live.player.DWBasePlayer
import com.bokecc.sdk.mobile.live.replay.DWLiveReplay
import kotlinx.android.synthetic.main.activity_local_replay_play.*
import kotlinx.android.synthetic.main.replay_pc_portrait_msg_layout.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.LiveBackPop
import tuoyan.com.xinghuo_dayingindex.bean.NetLesson
import tuoyan.com.xinghuo_dayingindex.bean.SensorsData
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.dialog.AlertDialogFactory
import tuoyan.com.xinghuo_dayingindex.ui.cc.dialog.LessonJumpDialog
import tuoyan.com.xinghuo_dayingindex.ui.cc.popup.FloatingPopupWindow
import tuoyan.com.xinghuo_dayingindex.ui.cc.util.NotificationUtil
import tuoyan.com.xinghuo_dayingindex.ui.dialog.LiveCCCouponHorizontalDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.LiveCCCouponVerticalDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.LiveCCPopDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListActivity
import tuoyan.com.xinghuo_dayingindex.ui.main.Adapter
import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.NetLessonsPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils
import tuoyan.com.xinghuo_dayingindex.utlis.UnZiper
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
import java.io.File
import java.util.*

class LocalReplayCCActivity : LifeActivity<NetLessonsPresenter>(), LocalTemplateUpdateListener {
    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_local_replay_play

    private val lessonAdapter by lazy {
        Adapter {
            val intent = Intent(this@LocalReplayCCActivity, LessonDetailActivity::class.java)
            intent.putExtra("key", it.key)
            startActivity(intent)
        }
    }
    private val lesson by lazy { intent.getSerializableExtra("lesson") as? NetLesson }

    private val sensorsData by lazy { intent.getParcelableExtra("SensorsData") as? SensorsData }

    private val mTitle by lazy { intent.getStringExtra("title") ?: "" }
    private val isShowDialog by lazy { intent.getBooleanExtra("isShowDialog", false) ?: false }
    private val lessonJumpDialog by lazy { LessonJumpDialog(this@LocalReplayCCActivity, mTitle) }

    //这里进行解压播放
    private val mPlayPath by lazy { intent.getStringExtra("filePath") ?: "" }
    private val handler by lazy { DWLocalReplayCoreHandler.getInstance() }
    private val dwLocalReplayCoreHandler by lazy { DWLocalReplayCoreHandler.getInstance() }

    private val mLocalReplayFloatingView by lazy { FloatingPopupWindow(this) }
    private val mChatLayout by lazy { LocalReplayChatComponent(this) }
    private val mQaLayout by lazy { LocalReplayQAComponent(this) }
    private val mReplayVideoView by lazy { LocalReplayVideoView(this) }

    //文档列表
    private val mDocPageInfoListComponent by lazy { DWLocalReplayDocPageInfoListComponent(this) }
    private val mDocLayout by lazy { LocalReplayDocComponent(this) }
    private var mRoot: View? = null
    private val cCPlayer by lazy { dwLocalReplayCoreHandler.player }

    // 广播接收者
    private var broadcastReceiver: BroadcastReceiver? = null
    private var mSpeed = "1"//倍速 默认1

//    private val floatDismissListener by lazy {
//        FloatingPopupWindow.FloatDismissListener {
//            replay_room_layout.setSwitchText(true)
//        }
//    }

    /**************************************  Room 状态回调监听  */
    private val roomStatusListener by lazy {
        object : BaseReplayRoomLayout.ReplayRoomStatusListener() {
            override fun switchVideoDoc(isVideoMain: Boolean) {
                sensorsData!!.let {
                    SaUtil.saOption(it, "切换双屏")
                }
                switchView(isVideoMain)
            }

            override fun openVideoDoc() {
                if (replay_room_layout.isVideoMain) {
                    mLocalReplayFloatingView.show(mRoot)
                    if (mDocLayout.parent != null) (mDocLayout.parent as ViewGroup).removeView(mDocLayout)
                    mLocalReplayFloatingView.addView(mDocLayout)
                } else {
                    mLocalReplayFloatingView.show(mRoot)
                    if (mReplayVideoView.parent != null) (mReplayVideoView.getParent() as ViewGroup).removeView(mReplayVideoView)
                    mLocalReplayFloatingView.addView(mReplayVideoView)
                }
            }

            override fun closeRoom() {
                runOnUiThread { // 如果当前状态是竖屏，则弹出退出确认框，否则切换为竖屏
                    if (isPortrait()) {
                        showExitTips()
                    } else {
                        quitFullScreen()
                    }
                }
            }

            override fun fullScreen() {
                sensorsData!!.let {
                    SaUtil.saOption(it, "点击全屏")
                }
                runOnUiThread {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    ll_pc_replay_msg_layout.setVisibility(View.GONE)
                    window.decorView.systemUiVisibility = getSystemUiVisibility(true)
                }
            }

            override fun onClickDocScaleType(scaleType: Int) {
                if (mDocLayout != null) {
                    mDocLayout.setScaleType(scaleType)
                }
            }

            override fun onDismissFloatView() {
                mLocalReplayFloatingView.dismiss()
            }
        }
    }

    fun switchView(isVideoMain: Boolean) {
        if (mReplayVideoView.parent != null) (mReplayVideoView.parent as ViewGroup).removeView(mReplayVideoView)
        if (mDocLayout.parent != null) (mDocLayout.parent as ViewGroup).removeView(mDocLayout)
        if (isVideoMain) {
            // 缓存视频的切换前的画面
            mLocalReplayFloatingView.addView(mDocLayout)
            rl_video_container.addView(mReplayVideoView)
            mDocLayout.setDocScrollable(false) //webview不可切换
        } else {
            // 缓存视频的切换前的画面
            mLocalReplayFloatingView.addView(mReplayVideoView)
            rl_video_container.addView(mDocLayout)
            mDocLayout.setDocScrollable(true) //webview不可切换
        }
    }

    private val adapter by lazy {
        object : PagerAdapter() {
            override fun getCount(): Int {
                return mLiveInfoList.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                container.addView(mLiveInfoList[position])
                return mLiveInfoList[position]
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(mLiveInfoList[position])
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }
        }
    }

    /********************************* 重要组件相关  */
//因为息屏会重新触发一次onLocalTemplateUpdate回调
    private var isComponentsInit = false

    /*************************************** 下方布局  */
    private val mLiveInfoList by lazy { ArrayList<View>() }
    private val mIdList by lazy { ArrayList<Int>() }
    private val mTagList by lazy { ArrayList<RadioButton>() }

    private var playBackTime = ArrayList<LiveBackPop>()
    private var oldPlayTime = -1L;
    private val horizontalDialog by lazy {
        LiveCCCouponHorizontalDialog(this@LocalReplayCCActivity) { item, dialog ->
            presenter.exchangeCoupon(mutableMapOf("pKey" to item.key)) {
                item.isOwn = it.isOwn
                dialog.dataRefresh()
                Toast.makeText(this, "优惠券领取成功", Toast.LENGTH_LONG).show()
            }
        }
    }
    private val verticalDialog by lazy {
        LiveCCCouponVerticalDialog(this@LocalReplayCCActivity) { item, dialog ->
            presenter.exchangeCoupon(mutableMapOf("pKey" to item.key)) {
                item.isOwn = it.isOwn
                dialog.dataRefresh()
                Toast.makeText(MainCouponActivity@ this, "优惠券领取成功", Toast.LENGTH_LONG).show()
            }
        }
    }
    private val imgDialog by lazy {
        LiveCCPopDialog(this, { dialog, it ->
            dialog.dismiss()
            if (it.shoppingType == "link") {
                if (it.link != "") {
                    startActivity<WebViewActivity>(
                        WebViewActivity.URL to it.link,
                        WebViewActivity.TITLE to it.name
                    )
                }
            } else if (it.shoppingType == "net" && !it.shoppingKey.isNullOrEmpty()) {
//                                    saBanner(advert, "网课")
                startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to it.shoppingKey)
            } else if (it.shoppingType == "book" && !it.shoppingKey.isNullOrEmpty()) {
                //跳转点读
//                                    saBanner(advert, "图书")
                startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${it.shoppingKey}")
            } else if ("app" == it.shoppingType && !it.shoppingKey.isNullOrEmpty()) {
//                                    saBanner(advert, "小程序")
                WxMini.goWxMini(this, it.shoppingKey)
            } else if ("smartBook" == it.shoppingType) {
//                                    saBanner(advert, "智能书")
                startActivity<EBookListActivity>()
            } else if ("netSpecial" == it.shoppingType) {
//                                    saBanner(advert, "网课专题")
                startActivity<LessonListActivity>(
                    LessonListActivity.KEY to it.shoppingKey,
                    LessonListActivity.TITLE to it.name
                )
            }
        }) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestFullScreenFeature()
        fullScreen = true
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        lessonJumpDialog.show()
    }

    override fun configView() {
        super.configView()
        if (isShowDialog) {

            presenter.getLiveBackPopList(sensorsData?.period_id ?: "") {
                playBackTime.clear()
                playBackTime.addAll(it)
            }
        }
        handler.setLocalTemplateUpdateListener(this)
        tv_local_sel.isSelected = true
        mRoot = window.decorView.findViewById(android.R.id.content)
        replay_room_layout.mTitle.text = mTitle
//        val lastPosition = SPUtil.getInstance().getLong(mPlayPath, 0L)
//        if (lastPosition > 0) {
//            replay_room_layout.showJump(lastPosition, mPlayPath)
//        } else {
//            replay_room_layout.setRecordId(mPlayPath)
//        }
        dwLocalReplayCoreHandler.setRecordTag(mPlayPath)
        startUnzip(mPlayPath) { path ->
            path.let {
                runOnUiThread {
                    mReplayVideoView.setPlayPath(it)
                    mReplayVideoView.start()
                }
            }
        }
        replay_room_layout.init(false)
        replay_room_layout.setActivity(this)
        initReceiver()
        initLessons()
//        replay_video_view.setPlayPath("/storage/emulated/0/星火英语/4F8C5AC3122912B1")
    }

    private fun initLessons() {
        rlc_lessons.layoutManager = LinearLayoutManager(this@LocalReplayCCActivity)
        rlc_lessons.adapter = lessonAdapter
        val params = rlc_lessons.layoutParams
        if (lesson?.netList is List<*>) {
            if (lesson!!.netList?.isEmpty() == true) {
                v_local.visibility = View.GONE
                tv_chat_top.visibility = View.GONE
                rl_recommend.visibility = View.GONE
                rlc_lessons.visibility = View.GONE
                v_line.setBackground(ContextCompat.getDrawable(this@LocalReplayCCActivity, R.color.color_f5))
            } else if (lesson!!.netList?.size == 1) {
                v_local.visibility = View.VISIBLE
                tv_chat_top.visibility = View.VISIBLE
                rl_recommend.visibility = View.VISIBLE
                rlc_lessons.visibility = View.VISIBLE
                v_line.setBackground(ContextCompat.getDrawable(this@LocalReplayCCActivity, R.drawable.chat_msg_shadow))
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                v_local.visibility = View.VISIBLE
                tv_chat_top.visibility = View.VISIBLE
                rl_recommend.visibility = View.VISIBLE
                rlc_lessons.visibility = View.VISIBLE
                v_line.setBackground(ContextCompat.getDrawable(this@LocalReplayCCActivity, R.drawable.chat_msg_shadow))
                params.height = 610
            }
            rlc_lessons.layoutParams = params
            lessonAdapter.setData(lesson!!.netList)
        } else {
            v_local.visibility = View.GONE
            rl_recommend.visibility = View.GONE
            rlc_lessons.visibility = View.GONE
            v_line.setBackground(ContextCompat.getDrawable(this@LocalReplayCCActivity, R.color.color_f5))
            tv_chat_top.visibility = View.GONE
        }
    }

    // 注册广播接收者
    private fun initReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                when {
                    NotificationUtil.ACTION_PLAY_PAUSE == action -> {
                        replay_room_layout.changePlayerStatus()
                        NotificationUtil.sendLocalReplayNotification(this@LocalReplayCCActivity)
                    }
                    NotificationUtil.ACTION_DESTROY == action -> {
                        finish()
                    }
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(NotificationUtil.ACTION_PLAY_PAUSE)
        intentFilter.addAction(NotificationUtil.ACTION_DESTROY)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    /**
     * 开始解压,
     * cc
     */

    private fun startUnzip(path: String, onUnZipFinish: (path: String?) -> Unit) {
        val ccrFile = File(path)
        val unZiper = UnZiper(object : UnZiper.UnZipListener {
            override fun onError(errorCode: Int, message: String?) {
                //解压失败 怎么操作
                runOnUiThread {
                    Toast.makeText(this@LocalReplayCCActivity, message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onUnZipFinish() {
                //
                onUnZipFinish(FileUtils.getUnzipDir(ccrFile))
            }
        }, ccrFile, FileUtils.getUnzipDir(ccrFile))
        unZiper.unZipFile()
    }

    override fun onResume() {
        super.onResume()
        sensorsData!!.let {
            SaUtil.saStartView(it)
        }
        NotificationUtil.cancelNotification(this)
    }

    override fun onPause() {
        super.onPause()
        cancelTask()
        mReplayVideoView.pause()
        sensorsData!!.let {
            SaUtil.saFinishView(it)
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishing) {
            NotificationUtil.sendLocalReplayNotification(this)
        }
    }

    override fun initData() {
        super.initData()
    }

    override fun handleEvent() {
        super.handleEvent()
        mLocalReplayFloatingView.setFloatClick {
            replay_room_layout.mVideoDocSwitch.performClick()
        }
        tv_local_sel.setOnClickListener {
            tv_local_sel.isSelected = !tv_local_sel.isSelected
            if (tv_local_sel.isSelected) {
                sensorsData!!.let {
                    SaUtil.saOption(it, "展开推荐网课")
                }
                tv_local_sel.text = "收起"
                rlc_lessons.visibility = View.VISIBLE
                v_line.background = ContextCompat.getDrawable(this@LocalReplayCCActivity, R.drawable.chat_msg_shadow)
                tv_chat_top.visibility = View.VISIBLE
            } else {
                sensorsData!!.let {
                    SaUtil.saOption(it, "收起推荐网课")
                }
                tv_local_sel.text = "展开"
                rlc_lessons.visibility = View.GONE
                v_line.background = ContextCompat.getDrawable(this@LocalReplayCCActivity, R.color.color_f5)
                tv_chat_top.visibility = View.GONE
            }
        }
        tv_chat_top.setOnClickListener {
            sensorsData!!.let {
                SaUtil.saOption(it, "收起推荐网课")
            }
            tv_local_sel.isSelected = false
            tv_local_sel.text = "展开"
            rlc_lessons.visibility = View.GONE
            v_line.background = ContextCompat.getDrawable(this@LocalReplayCCActivity, R.color.color_f5)
            tv_chat_top.visibility = View.GONE
        }
        live_portrait_container_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                mTagList[position].isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        rg_infos_tag.setOnCheckedChangeListener { radioGroup, i ->
            sensorsData!!.let {
                when (i) {
                    R.id.live_portrait_info_chat -> SaUtil.saOption(it, "聊天")
                    R.id.live_portrait_info_qa -> SaUtil.saOption(it, "提问")
                    R.id.live_portrait_info_intro -> SaUtil.saOption(it, "选择章节")
                }
            }
            live_portrait_container_viewpager.setCurrentItem(mIdList.indexOf(i), true)
        }
        replay_room_layout.setReplayRoomStatusListener(roomStatusListener)
//        mLocalReplayFloatingView.setFloatDismissListener(floatDismissListener)
        if (isShowDialog) {
            replay_room_layout.setPlayTimeListener() {
                if (oldPlayTime != it) {
                    if (DWLiveReplay.getInstance().getSpeed() <= 1f) {
                        oldPlayTime = it
                        for (element in playBackTime) {
                            if (element.use && element.showTime.toLong() == it) {
                                element.use = false
                                showTimeDialog(element);
                                break
                            }
                        }
                    } else {
                        for (element in playBackTime) {
                            if (element.use && element.showTime.toLong() in oldPlayTime until it) {
                                element.use = false
                                showTimeDialog(element);
                                break
                            }
                        }
                        oldPlayTime = it
                    }
                }

            }
        }
        try {
            val time = lesson?.recommendTime?.toFloat()?.toInt() ?: 0
            replay_room_layout.setRecommendBack(time) { type ->
                if ("1" == type) {
                    sensorsData?.let { SaUtil.saRecommend(it, "$time") }
                }
            }
        } catch (e: Exception) {
        }
        mReplayVideoView.setPlayStateChange { state ->
            when (state) {
                DWBasePlayer.State.PLAYING -> {
                    initTask()
                }
                else -> {
                    cancelTask()
                }
            }
        }
        replay_room_layout.setEvent(object : BaseRelativeLayout.SaEvent {
            override fun changeSpeed(speed: Float) {
                sensorsData!!.let {
                    SaUtil.saOption(it, "倍速选择(${speed})")
                }
            }

            override fun changeLine(line: Int) {
            }

            override fun changeProgress() {
                sensorsData!!.let {
                    SaUtil.saOption(it, "拖动进度")
                }
            }

            override fun changePlayState() {
                sensorsData!!.let {
                    SaUtil.saOption(it, "暂停/播放")
                }
            }
        })
    }

    private fun showTimeDialog(it: LiveBackPop) {
        horizontalDialog.dismiss()
        verticalDialog.dismiss()
        imgDialog.dismiss()
//                        内容有  网课:net  图书:book 外链:link 小程序:app
        //                        智能书:smartBook 网课专题：netSpecial 优惠券：promotional
        if ("promotional".equals(it.shoppingType)) {
            if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                //横屏
                it.promotionalList?.let { it1 ->
                    horizontalDialog.setData(it1)
                    horizontalDialog.show()
                }

            } else {
                //竖屏
                it.promotionalList?.let { it1 ->
                    verticalDialog.setData(it1)
                    verticalDialog.show()
                }

            }
        } else {
            imgDialog.show()
            imgDialog.setData(it, requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }

    }

    /**
     * 提交看课时长，学习进度
     * type:1 提交60s时长，进度条未达到95%;
     *      2 提交60s时长，进度条达到95%;
     *      3 不提交60s时长，进度条达到95%
     */
    private fun recordPlayLog(type: String) {
        if (NetWorkUtils.isNetWorkReachable()) {
            val map = mutableMapOf<String, String>()
            map["courseKey"] = "${sensorsData?.course_id ?: ""}"
            map["videoKey"] = "${sensorsData?.period_id ?: ""}"
            when (type) {
                "1" -> {
                    map["total"] = "60"
                }
                "2" -> {
                    map["total"] = "60"
                    map["schedule"] = "1"//不为空就行
                }
                "3" -> {
                    map["schedule"] = "1"//不为空就行
                }
            }
            presenter.recordPlayLog(map)
        }
    }

    override fun onBackPressed() {
        if (!isPortrait()) {
            quitFullScreen()
            return
        }
        showExitTips()
    }

    override fun onDestroy() {
        Thread {
            FileUtils.delete(File(FileUtils.getUnzipDir(File(mPlayPath))))
        }.start()
        if (mRoot != null && mRoot!!.handler != null) {
            mRoot!!.handler.removeCallbacksAndMessages(null)
        }
        super.onDestroy()
        mLocalReplayFloatingView.dismiss()
        mReplayVideoView.destroy()
        NotificationUtil.cancelNotification(this)
        unregisterReceiver(broadcastReceiver)
    }

    fun getUnzipDir(oriFile: File): String {
        val fileName = oriFile.name
        val sb = StringBuilder()
        sb.append(oriFile.parent)
        sb.append("/")
        val index = fileName.indexOf(".")
        if (index == -1) {
            sb.append(fileName)
        } else {
            sb.append(fileName.substring(0, index))
        }
        return sb.toString()
    }

    /**
     * 模板信息获取监听器，当本地回放模板信获取成功后该方法将会回调
     */
    override fun onLocalTemplateUpdate() {
        runOnUiThread { initComponents() }
    }

    /**
     * 根据直播间模版初始化相关组件
     */
    private fun initComponents() {
        if (isComponentsInit) {
            return
        }
        isComponentsInit = true
        mLiveInfoList.clear()
        mIdList.clear()
        // 初始化视频控件
        initVideoLayout()
        // 判断当前直播间模版是否有"聊天"功能
        if (dwLocalReplayCoreHandler.hasChatView()) {
            initChatLayout()
        }
        // 判断当前直播间模版是否有"问答"功能
        if (dwLocalReplayCoreHandler.hasQaView()) {
            initQaLayout()
        }
        // 判断当前直播间模版是否有"文档"功能
        if (dwLocalReplayCoreHandler.hasDocView()) {
            initDocLayout()
            initDocPageInfoListLayout()
        }
        initViewPager()
    }

    private fun initVideoLayout() {
        if (replay_room_layout.isVideoMain) {
            replay_room_layout.addView(mReplayVideoView)
        } else {
            if (DWLocalReplayCoreHandler.getInstance().hasDocView()) {
                mLocalReplayFloatingView.addView(mReplayVideoView)
            } else {
                replay_room_layout.addView(mReplayVideoView)
            }
        }
    }

    // 展示文档悬浮窗布局
    private fun showFloatingDocLayout() {
        // 判断当前直播间模版是否有"文档"功能，如果没文档，则小窗功能也不应该有
        if (dwLocalReplayCoreHandler.hasDocView()) {
            mLocalReplayFloatingView.show(mRoot)
        }
    }

    // 初始化文档布局区域
    private fun initDocLayout() {
        if (replay_room_layout.isVideoMain) {
            mLocalReplayFloatingView.addView(mDocLayout)
        } else {
            if (DWLocalReplayCoreHandler.getInstance().hasDocView()) {
                rl_video_container.addView(mDocLayout)
            }
        }
        // 显示小窗
        showFloatingDocLayout()
    }

    // 初始化聊天布局区域
    private fun initChatLayout() {
        mIdList.add(R.id.live_portrait_info_chat)
        mTagList.add(live_portrait_info_chat)
        live_portrait_info_chat.visibility = View.VISIBLE
        mLiveInfoList.add(mChatLayout)
    }

    // 初始化问答布局区域
    private fun initQaLayout() {
        mIdList.add(R.id.live_portrait_info_qa)
        mTagList.add(live_portrait_info_qa)
        live_portrait_info_qa.visibility = View.VISIBLE
        mLiveInfoList.add(mQaLayout)
    }

    // 初始化文档
    private fun initDocPageInfoListLayout() {
        mIdList.add(R.id.live_portrait_info_intro)
        mTagList.add(live_portrait_info_intro)
        live_portrait_info_intro.visibility = View.VISIBLE
        mLiveInfoList.add(mDocPageInfoListComponent)
    }

    /**
     * 初始化ViewPager
     */
    private fun initViewPager() {
        live_portrait_container_viewpager.offscreenPageLimit = 3
        live_portrait_container_viewpager.adapter = adapter
        if (mTagList != null && mTagList.size > 0) {
            mTagList[0].performClick()
        }
    }

    private fun showExitTips() {
        AlertDialogFactory.showAlertDialog(
            this.supportFragmentManager, resources.getString(R.string.tips), resources.getString(R.string.exit),
            resources.getString(R.string.determine), resources.getString(R.string.cancel)
        ) {
            finish()
        }
    }

    // 退出全屏
    private fun quitFullScreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ll_pc_replay_msg_layout.visibility = View.VISIBLE
        window.decorView.systemUiVisibility = getSystemUiVisibility(false)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 横屏隐藏状态栏
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.decorView.systemUiVisibility = getSystemUiVisibility(true)
        } else {
            window.decorView.systemUiVisibility = getSystemUiVisibility(false)
        }
        //调整窗口的位置
        if (mLocalReplayFloatingView != null) {
            mLocalReplayFloatingView.onConfigurationChanged(newConfig.orientation)
        }
    }

    private var timer: Timer? = null
    private var stayTime = 0f
    private var oldTimeM = 0
    private var progressUp = false
    private fun initTask() {
        cancelTask()
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                stayTime += 4.0f * cCPlayer.getSpeed(1.0f)
                val timeM = (stayTime / 60).toInt()
                if (oldTimeM != timeM) {
                    oldTimeM = timeM
                    sensorsData?.let { data -> SaUtil.saDotMinute(data, timeM, (cCPlayer.currentPosition / 1000).toInt(), mSpeed) }
                    recordPlayLog(if (replay_room_layout.isFinish) "2" else "1")
                }
                if (!progressUp) {
                    progressUp = replay_room_layout.isFinish
                    if (progressUp) {
                        //提交看完数据，提交看课时长  不做判断， 前端判断进度条
                        recordPlayLog("3")
                    }
                }
            }
        }, 4000, 4000)
    }

    private fun cancelTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }
}