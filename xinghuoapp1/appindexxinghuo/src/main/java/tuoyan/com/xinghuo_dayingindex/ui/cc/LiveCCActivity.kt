package tuoyan.com.xinghuo_dayingindex.ui.cc
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bokecc.livemodule.live.DWLiveCoreHandler
import com.bokecc.livemodule.live.chat.KeyboardHeightProvider
import com.bokecc.livemodule.live.chat.LiveChatComponent
import com.bokecc.livemodule.live.chat.OnChatComponentClickListener
import com.bokecc.livemodule.live.chat.adapter.LivePublicChatAdapter
import com.bokecc.livemodule.live.doc.LiveDocComponent
import com.bokecc.livemodule.live.function.FunctionCallBack
import com.bokecc.livemodule.live.function.FunctionHandler
import com.bokecc.livemodule.live.morefunction.announce.AnnounceLayout
import com.bokecc.livemodule.live.qa.LiveQAComponent
import com.bokecc.livemodule.live.room.LiveRoomLayout.*
import com.bokecc.livemodule.live.video.LiveVideoView
import com.bokecc.livemodule.utils.AppPhoneStateListener
import com.bokecc.livemodule.view.BaseRelativeLayout
import com.bokecc.sdk.mobile.live.DWLive
import kotlinx.android.synthetic.main.activity_live_play.*
import kotlinx.android.synthetic.main.live_pc_portrait_msg_layout.*
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.NetLesson
import tuoyan.com.xinghuo_dayingindex.bean.SensorsData
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.dialog.AlertDialogFactory
import tuoyan.com.xinghuo_dayingindex.ui.cc.dialog.LessonJumpDialog
import tuoyan.com.xinghuo_dayingindex.ui.cc.popup.FloatingPopupWindow
import tuoyan.com.xinghuo_dayingindex.ui.cc.util.NotificationUtil
import tuoyan.com.xinghuo_dayingindex.ui.dialog.AppraiseDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.LiveCCCouponHorizontalDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.LiveCCCouponVerticalDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.LiveCCPopDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListActivity
import tuoyan.com.xinghuo_dayingindex.ui.main.Adapter
import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.NetLessonsPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
import java.util.*

class LiveCCActivity : LifeActivity<NetLessonsPresenter>() {
    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_live_play

    private val mTitle by lazy { intent.getStringExtra("title") ?: "" }
    private val lessonJumpDialog by lazy { LessonJumpDialog(this@LiveCCActivity, mTitle) }
    private var isVote = false//答题卡收起监听
    private var mRoot: View? = null
    private val sensorsData by lazy { intent.getParcelableExtra("SensorsData") as? SensorsData }

    // 广播接收者
    private var broadcastReceiver: BroadcastReceiver? = null
    private val lessonAdapter by lazy {
        Adapter {
            sensorsData!!.let {
                SaUtil.saOption(it, "点击推荐课程")
            }
            val intent = Intent(this@LiveCCActivity, LessonDetailActivity::class.java)
            intent.putExtra("key", it.key)
            startActivity(intent)
        }
    }
    private val lesson by lazy { intent.getSerializableExtra("lesson") as NetLesson }

    // 视频组件
    private val mLiveVideoView by lazy { LiveVideoView(this) }
    private val dwLiveCoreHandler by lazy { DWLiveCoreHandler.getInstance() }

    // 直播功能处理机制（签到、答题卡/投票、问卷、抽奖）
    private val mFunctionHandler by lazy { FunctionHandler() }

    //添加答题卡收起监听
    private val functionCallBack: FunctionCallBack by lazy {
        object : FunctionCallBack() {
            override fun onMinimize(isVote: Boolean) {
                super.onMinimize(isVote)
                this@LiveCCActivity.isVote = isVote
            }

            override fun onClose() {
                super.onClose()
            }
        }
    }

    // 文档组件
    private val mDocLayout: LiveDocComponent by lazy { LiveDocComponent(this) }

    // 聊天组件
    private val mChatLayout: LiveChatComponent by lazy { LiveChatComponent(this) }

    // 问答组件
    private val mQaLayout: LiveQAComponent by lazy { LiveQAComponent(this) }

    // 公告组件
    private val mAnnounceLayout: AnnounceLayout by lazy { AnnounceLayout(this) }

    // 悬浮弹窗（用于展示文档和视频）
    private val mLiveFloatingView: FloatingPopupWindow by lazy { FloatingPopupWindow(this) }

    //    private val floatDismissListener: FloatingPopupWindow.FloatDismissListener = FloatingPopupWindow.FloatDismissListener {
//        if (live_room_layout != null) {
//            live_room_layout.setSwitchText(true)
//        }
//    }
    fun closeappraiseDialog() {
        appraiseDialog.dismiss()
    }

    private val appraiseDialog by lazy {
        AppraiseDialog(this@LiveCCActivity) { a, b, c ->
            val map = mutableMapOf<String, String>()
            map["netcourseKey"] = "${sensorsData?.course_id ?: ""}"//网课key
            map["videoKey"] = "${sensorsData?.period_id ?: ""}"//小节key
            map["contentScore"] = a.toString()
            map["methodScore"] = b.toString()
            map["effectScore"] = c.toString()
            presenter.recordLiveScore(map) {
                toast("评价成功")
                closeappraiseDialog()
            }
        }
    }
    private val horizontalDialog by lazy {
        LiveCCCouponHorizontalDialog(this@LiveCCActivity) { item, dialog ->
            presenter.exchangeCoupon(mutableMapOf("pKey" to item.key)) {
                item.isOwn = it.isOwn
                dialog.dataRefresh()
                Toast.makeText(this, "优惠券领取成功", Toast.LENGTH_LONG).show()
            }
        }
    }
    private val verticalDialog by lazy {
        LiveCCCouponVerticalDialog(this@LiveCCActivity) { item, dialog ->
            presenter.exchangeCoupon(mutableMapOf("pKey" to item.key)) {
                item.isOwn = it.isOwn
                dialog.dataRefresh()
                Toast.makeText(this, "优惠券领取成功", Toast.LENGTH_LONG).show()
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

    //*************************************** 下方布局 ***************************************/
    private val mLiveInfoList by lazy { ArrayList<View>() }
    private val mIdList by lazy { ArrayList<Int>() }
    private val mTagList by lazy { ArrayList<RadioButton>() }
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

    private fun initReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (NotificationUtil.ACTION_PLAY_PAUSE.equals(action)) {
                    NotificationUtil.sendLiveNotification(this@LiveCCActivity)
                } else if (NotificationUtil.ACTION_DESTROY.equals(action)) {
                    finish()
                } else if (AppPhoneStateListener.ACTION_RESUME == action) {
                    // 重新播放video
                    if (mLiveVideoView != null) {
                        mLiveVideoView.exitRtcMode()
                    }
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(NotificationUtil.ACTION_PLAY_PAUSE)
        intentFilter.addAction(NotificationUtil.ACTION_DESTROY)
        intentFilter.addAction(AppPhoneStateListener.ACTION_PAUSE)
        intentFilter.addAction(AppPhoneStateListener.ACTION_RESUME)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    //---------------------------------- 直播间状态监听 --------------------------------------------/
    private val roomStatusListener by lazy {
        object : LiveRoomStatusListener {
            // 文档/视频布局区域 回调事件 #Called From LiveRoomLayout
            @Synchronized
            override fun switchVideoDoc(isVideoMain: Boolean) {
                sensorsData!!.let {
                    SaUtil.saOption(it, "切换双屏")
                }
                switchView(isVideoMain) // 切换视频
            }

            override fun openVideoDoc() {
                if (mLiveFloatingView == null) return
                if (live_room_layout.isVideoMain) {
                    mLiveFloatingView.show(mRoot)
                    if (mDocLayout.parent != null) (mDocLayout.parent as ViewGroup).removeView(
                        mDocLayout
                    )
                    mLiveFloatingView.addView(mDocLayout)
                } else {
                    mLiveFloatingView.show(mRoot)
                    if (mLiveVideoView.parent != null) (mLiveVideoView.parent as ViewGroup).removeView(
                        mLiveVideoView
                    )
                    mLiveFloatingView.addView(mLiveVideoView)
                }
            }

            // 退出直播间
            override fun closeRoom() {
                runOnUiThread { // 如果当前状态是竖屏，则弹出退出确认框，否则切换为竖屏
                    if (isPortrait()) {
                        showExitTips()
                    } else {
                        quitFullScreen()
                    }
                }
            }

            // 全屏
            override fun fullScreen() {
                sensorsData!!.let {
                    SaUtil.saOption(it, "点击全屏")
                }
                runOnUiThread {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    ll_pc_live_msg_layout.visibility = View.GONE
                    if (requestedOrientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        live_room_layout.findViewById<ImageView>(R.id.iv_barrage_control).visibility= GONE
                    }else{
                        live_room_layout.findViewById<ImageView>(R.id.iv_barrage_control).visibility= VISIBLE
                    }
                }
            }

            // 踢出直播间
            override fun kickOut() {
                runOnUiThread {
                    Toast.makeText(this@LiveCCActivity, "您已经被踢出直播间", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onClickDocScaleType(scaleType: Int) {
                if (mDocLayout != null) {
                    mDocLayout.setScaleType(scaleType)
                }
            }

            override fun onStreamEnd() {
                //初始化视频界面并隐藏悬浮框
                if (mLiveFloatingView != null) {
                    mLiveFloatingView.dismiss()
                }
            }

            override fun onStreamEnd(isNormal: Boolean, reason: String?) {
                if (isNormal && reason == "直播已结束") {
                    if (lesson.isShowScore == "1") {

//                        String netcourseKey = object.getString("netcourseKey");//网课key
//                        String videoKey = object.getString("videoKey");//小节key
//                        String contentScore = object.getString("contentScore");//课程内容
//                        String methodScore = object.getString("methodScore");//课程方法
//                        String effectScore = object.getString("effectScore");//课程效果

                        appraiseDialog.show()
                    }
                }

            }

            override fun onStreamStart() {
                if (!mLiveFloatingView.isShowing) {
                    //显示小窗
                    showFloatingLayout()
                    switchView(live_room_layout.isVideoMain()) // 开课回调
                }
                //尝试获取一下当前正在进行的随堂测，需要考虑用户重新登录及Home桌面的问题
                DWLive.getInstance().getPracticeInformation()
            }

            //            override fun refresh() {
//                sensorsData!!.let {
//                    SaUtil.saOption(it, "直播刷新")
//                }
//            }
            override fun switchDanMu() {
                sensorsData!!.let {
                    SaUtil.saOption(it, "开启关闭弹幕")
                }
            }

            override fun sendChat() {
                sensorsData!!.let {
                    SaUtil.saOption(it, "发送聊天")
                }
            }

            override fun onDismissFloatView() {
                mLiveFloatingView.dismiss()
            }
        }
    }

    private fun switchView(isVideoMain: Boolean) {
        if (mLiveVideoView.parent != null) {
            (mLiveVideoView.parent as ViewGroup).removeView(mLiveVideoView);
        }
        if (DWLiveCoreHandler.getInstance().hasPdfView()) {
            if (mDocLayout.parent != null) {
                (mDocLayout.parent as ViewGroup).removeView(mDocLayout);
            }
        }
        //---------------------------------------------player拉流切换窗口
        if (isVideoMain) {
            if (DWLiveCoreHandler.getInstance().hasPdfView()) {
                mLiveFloatingView.addView(mDocLayout);
                mDocLayout.setDocScrollable(false);
            }
            rl_video_container.addView(mLiveVideoView);
        } else {
            mLiveFloatingView.addView(mLiveVideoView);
            if (DWLiveCoreHandler.getInstance().hasPdfView()) {
                rl_video_container.addView(mDocLayout, 0);
                mDocLayout.setDocScrollable(true);
            }
        }
    }

    private val keyboardHeightProvider by lazy { KeyboardHeightProvider(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestFullScreenFeature()
        fullScreen = true
        // 屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        lessonJumpDialog.show()
    }

    //    private var startTime = 0L
    override fun onResume() {
        super.onResume()
        keyboardHeightProvider.addKeyboardHeightObserver(mChatLayout)
        keyboardHeightProvider.addKeyboardHeightObserver(live_room_layout)
        keyboardHeightProvider.addKeyboardHeightObserver(mQaLayout)
        mFunctionHandler.setRootView(mRoot)
        // 开始播放
        mLiveVideoView.start()
        // 开启弹幕
        live_room_layout.openLiveBarrage()
        if (requestedOrientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            live_room_layout.findViewById<ImageView>(R.id.iv_barrage_control).visibility= GONE
        }else{
            live_room_layout.findViewById<ImageView>(R.id.iv_barrage_control).visibility= VISIBLE
        }
        // 取消通知
        NotificationUtil.cancelNotification(this)
        sensorsData!!.let { SaUtil.saStartView(it) };
        initTask()
    }

    override fun configView() {
        super.configView()
        tv_re_sel.isSelected = true
        mRoot = window.decorView.findViewById(android.R.id.content)
        // 初始化控制布局，需登录成功之后调用
        live_room_layout.init(false)
        live_room_layout.setActivity(this)
        initViewPager()
        initLessons()
//        switchView(false)
        live_room_layout.mLiveTitle.text = mTitle
        // 初始化广播接收器
        initReceiver()
        // 设置防录屏
//        mLiveVideoView.setAntiRecordScreen(this)
    }

    private fun initLessons() {
        rlc_lessons.layoutManager = LinearLayoutManager(this@LiveCCActivity)
        rlc_lessons.adapter = lessonAdapter
        val params = rlc_lessons.layoutParams
        if (lesson.netList is List<*>) {
            if (lesson.netList?.isEmpty() == true) {
                v1.visibility = View.GONE
                tv_chat_top.visibility = View.GONE
                rl_recommend.visibility = View.GONE
                rlc_lessons.visibility = View.GONE
                v_line.setBackground(
                    ContextCompat.getDrawable(
                        this@LiveCCActivity,
                        R.color.color_f5
                    )
                )
            } else if (lesson.netList?.size == 1) {
                v1.visibility = View.VISIBLE
                tv_chat_top.visibility = View.VISIBLE
                rl_recommend.visibility = View.VISIBLE
                rlc_lessons.visibility = View.VISIBLE
                v_line.setBackground(
                    ContextCompat.getDrawable(
                        this@LiveCCActivity,
                        R.drawable.chat_msg_shadow
                    )
                )
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                v1.visibility = View.VISIBLE
                tv_chat_top.visibility = View.VISIBLE
                rl_recommend.visibility = View.VISIBLE
                rlc_lessons.visibility = View.VISIBLE
                v_line.setBackground(
                    ContextCompat.getDrawable(
                        this@LiveCCActivity,
                        R.drawable.chat_msg_shadow
                    )
                )
                params.height = 610
            }
            rlc_lessons.layoutParams = params
            lessonAdapter.setData(lesson.netList)
        } else {
            v1.visibility = View.GONE
            rl_recommend.visibility = View.GONE
            rlc_lessons.visibility = View.GONE
            v_line.setBackground(ContextCompat.getDrawable(this@LiveCCActivity, R.color.color_f5))
            tv_chat_top.visibility = View.GONE
        }
    }

    override fun initData() {
        super.initData()
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_re_sel.setOnClickListener {
            tv_re_sel.isSelected = !tv_re_sel.isSelected
            if (tv_re_sel.isSelected) {
                sensorsData!!.let {
                    SaUtil.saOption(it, "展开推荐课程")
                }
                tv_re_sel.text = "收起"
                rlc_lessons.visibility = View.VISIBLE
                v_line.background =
                    ContextCompat.getDrawable(this@LiveCCActivity, R.drawable.chat_msg_shadow)
                tv_chat_top.visibility = View.VISIBLE
            } else {
                sensorsData!!.let {
                    SaUtil.saOption(it, "收起推荐课程")
                }
                tv_re_sel.text = "展开"
                rlc_lessons.visibility = View.GONE
                v_line.background = ContextCompat.getDrawable(this@LiveCCActivity, R.color.color_f5)
                tv_chat_top.visibility = View.GONE
            }
        }
        tv_chat_top.setOnClickListener {
            sensorsData!!.let {
                SaUtil.saOption(it, "展开推荐课程")
            }
            tv_re_sel.isSelected = false
            tv_re_sel.text = "展开"
            rlc_lessons.visibility = View.GONE
            v_line.background = ContextCompat.getDrawable(this@LiveCCActivity, R.color.color_f5)
            tv_chat_top.visibility = View.GONE
        }
        live_portrait_container_viewpager.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mTagList[position].isChecked = true
                hideKeyboard()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        rg_infos_tag.setOnCheckedChangeListener { radioGroup, i ->
            sensorsData!!.let {
                when (i) {
                    R.id.live_portrait_info_chat -> SaUtil.saOption(it, "聊天")
                    R.id.live_portrait_info_qa -> SaUtil.saOption(it, "提问")
                    R.id.live_announce -> SaUtil.saOption(it, "公告")
                }
            }
            live_portrait_container_viewpager.setCurrentItem(mIdList.indexOf(i), true)
            if (i == R.id.live_announce) {
                announce_point.visibility = View.GONE
            }
        }
        if (mTagList != null && mTagList.size > 0) {
            mTagList[0].performClick()
        }
        initRoomStatusListener()
        mFunctionHandler.initFunctionHandler(this, functionCallBack)
        mRoot?.viewTreeObserver?.addOnGlobalLayoutListener {
            keyboardHeightProvider.start()
        }
        mLiveFloatingView.setFloatClick {
            live_room_layout.mLiveVideoDocSwitch.performClick()
        }
        live_room_layout.setEvent(object : BaseRelativeLayout.SaEvent {
            override fun changeSpeed(speed: Float) {
            }

            override fun changeLine(line: Int) {
                sensorsData!!.let {
                    SaUtil.saOption(it, "切换线路")
                }
            }

            override fun changeProgress() {
            }

            override fun changePlayState() {
            }
        })
    }

    override fun onBackPressed() {
        if (!isPortrait()) {
            if (!live_room_layout.onBackPressed()) {
                quitFullScreen()
            }
            return
        } else {
            if (mChatLayout != null && mChatLayout.onBackPressed()) {
                return
            }
        }
        // 弹出退出提示
        showExitTips()
    }

    private fun showExitTips() {
        AlertDialogFactory.showAlertDialog(
            this.supportFragmentManager,
            resources.getString(R.string.tips),
            resources.getString(R.string.exit),
            resources.getString(R.string.determine),
            resources.getString(R.string.cancel)
        ) {
            finish()
        }
    }

    // 退出全屏
    private fun quitFullScreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ll_pc_live_msg_layout.visibility = View.VISIBLE
        if (requestedOrientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            live_room_layout.findViewById<ImageView>(R.id.iv_barrage_control).visibility= GONE
        }else{
            live_room_layout.findViewById<ImageView>(R.id.iv_barrage_control).visibility= VISIBLE
        }
    }

    /**
     * 初始化ViewPager
     */
    private fun initViewPager() {
//        mLiveFloatingView.setFloatDismissListener(floatDismissListener)
        //首次进入默认竖屏 所以需要关闭弹幕
//        live_barrage.stop()
        initComponents()
        live_portrait_container_viewpager.adapter = adapter
//        if (roomInfo != null) {
        // 获取是否显示弹幕
//            isBarrageOn = roomInfo.barrage == 1
//            live_room_layout.controlBarrageControl(isBarrageOn)
//        }
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider.clearObserver()
        mFunctionHandler.removeRootView()
        // 停止弹幕
        live_room_layout.closeLiveBarrage()
        mLiveVideoView.stop()
        sensorsData!!.let { SaUtil.saFinishView(it) }
        cancelTask()
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishing) {
            NotificationUtil.sendLiveNotification(this)
        }
    }

    /**
     * 每分钟提交一次
     */
    fun recordPlayLog() {
        val map = mutableMapOf<String, String>()
        map["courseKey"] = "${sensorsData?.course_id ?: ""}"
        map["videoKey"] = "${sensorsData?.period_id ?: ""}"
        map["total"] = "60"
        presenter.recordPlayLog(map)
    }

    override fun onDestroy() {
        keyboardHeightProvider.close()
        mLiveFloatingView.dismiss()
        mFunctionHandler.onDestroy(this)
        mLiveVideoView.stop()
        live_room_layout.destroy()
        mLiveVideoView.destroy()
        // 取消通知显示
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    //根据直播间模版初始化相关组件
    private fun initComponents() {
        initVideoLayout()
        // 判断当前直播间模版是否有"文档"功能
        if (dwLiveCoreHandler.hasPdfView()) {
            initDocLayout()
        }
        // 显示视频
        // 显示悬浮窗
        showFloatingLayout()
        // 判断当前直播间模版是否有"聊天"功能
        if (dwLiveCoreHandler.hasChatView()) {
            initChatLayout()
        }
        // 判断当前直播间模版是否有"问答"功能
        if (dwLiveCoreHandler.hasQaView()) {
            initQaLayout()
        }
        // 判断当前直播间模版是否是视频大屏模式，如果是，隐藏更多功能
//        if (dwLiveCoreHandler.isOnlyVideoTemplate) {
//            more_function_layout.setVisibility(View.GONE)
//        }
        // 直播间简介
        initIntroLayout()
        // 设置弹幕状态监听
        dwLiveCoreHandler.setNoticeCallback { content ->
            sensorsData?.let { SaUtil.saNotice(it, content) }
        }
    }

    private fun initVideoLayout() {
        if (mLiveVideoView.parent != null) {
            (mLiveVideoView.parent as ViewGroup).removeView(mLiveVideoView);
        }
        if (live_room_layout.isVideoMain) {
            rl_video_container.addView(mLiveVideoView);
        } else {
            if (dwLiveCoreHandler != null && dwLiveCoreHandler.hasPdfView()) {
                mLiveFloatingView.addView(mLiveVideoView);
            } else {
                rl_video_container.addView(mLiveVideoView);
            }

        }
    }

    // 初始化文档布局区域
    private fun initDocLayout() {
        if (live_room_layout.isVideoMain) {
            mLiveFloatingView.removeNowView()
            mLiveFloatingView.addView(mDocLayout)
        } else {
            rl_video_container.addView(mDocLayout)
            mDocLayout.setDocScrollable(true)
        }
    }

    // 初始化聊天布局区域
    private fun initChatLayout() {
        mIdList.add(R.id.live_portrait_info_chat)
        mTagList.add(live_portrait_info_chat)
        live_portrait_info_chat.setVisibility(View.VISIBLE)
        mChatLayout.setPopView(mRoot)
        //initChatView();
        mLiveInfoList.add(mChatLayout)
        mChatLayout.setBarrageLayout(live_room_layout.liveBarrageLayout)
        mChatLayout.setOnChatComponentClickListener(OnChatComponentClickListener { bundle ->
            if (bundle == null) return@OnChatComponentClickListener
            val type = bundle.getString("type")
            if (LivePublicChatAdapter.CONTENT_IMAGE_COMPONENT == type) {
//                val intent = Intent(this@LivePlayActivity, ImageDetailsActivity::class.java)
//                intent.putExtra("imageUrl", bundle.getString("url"))
//                startActivity(intent)
            } else if (LivePublicChatAdapter.CONTENT_ULR_COMPONET == type) {
                val uri = Uri.parse(bundle.getString("url"))
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        })
        mChatLayout.setOnBroadcastClickListener(LiveChatComponent.OnBroadcastClickListener {
            it.content?.let { content ->
                if (content.startsWith("直播弹窗")) {
                    val substring = content.substring(4)
                    var split = substring.split(",");
                    if (split.size == 2) {
//                        var it:LivePop= LivePop();
                        presenter.getLivePop(split[0], split[1]) {
////                            返回name、coverUrl、link、shoppingKey、list(优惠券列表)
                            it.shoppingType = split[1]
                            horizontalDialog.dismiss()
                            verticalDialog.dismiss()
                            imgDialog.dismiss()
//                        内容有  网课:net  图书:book 外链:link 小程序:app
                            //                        智能书:smartBook 网课专题：netSpecial 优惠券：promotional
                            if ("promotional".equals(it.shoppingType)) {
                                if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                                    //横屏
                                    it.list?.let { it1 ->
                                        horizontalDialog.setData(it1)
                                        horizontalDialog.show()
                                    }
                                } else {
                                    //竖屏
                                    it.list?.let { it1 ->
                                        verticalDialog.setData(it1)
                                        verticalDialog.show()
                                    }
                                }
                            } else {
                                imgDialog.show()
                                imgDialog.setData(it, requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                            }
                        }
                    }
                }
            }
        })
    }

    // 初始化问答布局区域
    private fun initQaLayout() {
        mIdList.add(R.id.live_portrait_info_qa)
        mTagList.add(live_portrait_info_qa)
        live_portrait_info_qa.setVisibility(View.VISIBLE)
        mLiveInfoList.add(mQaLayout)
    }

    // 初始化简介布局区域
    private fun initIntroLayout() {
        mIdList.add(R.id.live_announce)
        mTagList.add(live_announce)
        live_announce.setVisibility(View.VISIBLE)
        mLiveInfoList.add(mAnnounceLayout)
        mAnnounceLayout.setNewAnnounce {
            announce_point.visibility = View.VISIBLE
        }
    }

    // 初始化房间状态监听
    private fun initRoomStatusListener() {
        if (live_room_layout == null) {
            return
        }
        live_room_layout.setLiveRoomStatusListener(roomStatusListener)
    }

    // 展示文档悬浮窗布局
    private fun showFloatingLayout() {
        // 判断当前直播间模版是否有"文档"功能，如果没文档，则小窗功能也不应该有
        if (dwLiveCoreHandler.hasPdfView()) {
            mLiveFloatingView.show(mRoot)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 横屏隐藏状态栏
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.decorView.systemUiVisibility = getSystemUiVisibility(true)
        } else {
            window.decorView.systemUiVisibility = getSystemUiVisibility(false)
        }
        // 调整窗口的位置
        if (mLiveFloatingView != null) {
            mLiveFloatingView.onConfigurationChanged(newConfig.orientation)
        }
        // 开启或者关闭弹幕
        live_room_layout.showInput(newConfig.orientation)
    }

    //********************************** 工具方法 *******************************************/
    // 隐藏输入法
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rl_pc_live_top_layout.windowToken, 0)
    }

    private var timer: Timer? = null
    private var stayTime = 0
    private fun initTask() {
        cancelTask()
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                stayTime++
                sensorsData?.let { data -> SaUtil.saDotMinute(data, stayTime, 0, "1") }
                recordPlayLog()
            }
        }, 60000, 60000)
    }

    private fun cancelTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }
}