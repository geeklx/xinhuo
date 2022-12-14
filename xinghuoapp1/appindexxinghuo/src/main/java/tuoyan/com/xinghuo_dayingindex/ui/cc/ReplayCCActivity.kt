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
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bokecc.livemodule.base.BaseReplayRoomLayout
import com.bokecc.livemodule.live.chat.OnChatComponentClickListener
import com.bokecc.livemodule.live.chat.adapter.LivePublicChatAdapter
import com.bokecc.livemodule.replay.DWReplayCoreHandler
import com.bokecc.livemodule.replay.chat.ReplayChatComponent
import com.bokecc.livemodule.replay.doc.ReplayDocComponent
import com.bokecc.livemodule.replay.doc.ReplayDocPageInfoListComponent
import com.bokecc.livemodule.replay.qa.ReplayQAComponent
import com.bokecc.livemodule.replay.video.ReplayVideoView
import com.bokecc.livemodule.utils.AppPhoneStateListener
import com.bokecc.livemodule.view.BaseRelativeLayout
import com.bokecc.sdk.mobile.live.player.DWBasePlayer
import com.bokecc.sdk.mobile.live.replay.DWLiveReplay
import kotlinx.android.synthetic.main.activity_replay_play.*
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
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
import java.util.*

class ReplayCCActivity : LifeActivity<NetLessonsPresenter>() {
    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_replay_play

    private val mTitle by lazy { intent.getStringExtra("title") ?: "" }
    private val lessonJumpDialog by lazy { LessonJumpDialog(this@ReplayCCActivity, mTitle) }
    private val recordId by lazy { intent.getStringExtra("recordId") ?: "" }

    private var mRoot: View? = null
    private val mReplayFloatingView by lazy { FloatingPopupWindow(this) }

    // ???????????????
    private var broadcastReceiver: BroadcastReceiver? = null

    // ????????????View
    private val mReplayVideoView by lazy { ReplayVideoView(this) }

    private val sensorsData by lazy { intent.getParcelableExtra("SensorsData") as? SensorsData }
    private val lessonAdapter by lazy {
        Adapter {
            val intent = Intent(this@ReplayCCActivity, LessonDetailActivity::class.java)
            intent.putExtra("key", it.key)
            startActivity(intent)
        }
    }
    private val lesson by lazy { intent.getSerializableExtra("lesson") as NetLesson }

    /********************************* ?????????????????? ***************************************/
    // ????????????
    private val mDocLayout by lazy { ReplayDocComponent(this) }

    //????????????
    private val mDocPageInfoListComponent by lazy { ReplayDocPageInfoListComponent(this) }

    // ????????????
    private val mQaLayout by lazy { ReplayQAComponent(this) }

    // ????????????
    private val mChatLayout by lazy { ReplayChatComponent(this) }

    private val CHANNEL_ID by lazy { "HD_SDK_CHANNEL_ID" }

    private val cCPlayer by lazy { DWReplayCoreHandler.getInstance().player }
    private val dwReplayCoreHandler by lazy { DWReplayCoreHandler.getInstance() }
    private var playBackTime = ArrayList<LiveBackPop>()
    private var oldPlayTime = -1L;

    /*************************************** ????????????  */
    var mLiveInfoList = ArrayList<View>()
    var mIdList = ArrayList<Int>()
    var mTagList = ArrayList<RadioButton>()
    private val horizontalDialog by lazy {
        LiveCCCouponHorizontalDialog(this@ReplayCCActivity) { item, dialog ->
            presenter.exchangeCoupon(mutableMapOf("pKey" to item.key)) {
                item.isOwn = it.isOwn
                dialog.dataRefresh()
                Toast.makeText(this, "?????????????????????", Toast.LENGTH_LONG).show()
            }
        }
    }
    private val verticalDialog by lazy {
        LiveCCCouponVerticalDialog(this@ReplayCCActivity) { item, dialog ->
            presenter.exchangeCoupon(mutableMapOf("pKey" to item.key)) {
                item.isOwn = it.isOwn
                dialog.dataRefresh()
                Toast.makeText(this, "?????????????????????", Toast.LENGTH_LONG).show()
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
//                                    saBanner(advert, "??????")
                startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to it.shoppingKey)
            } else if (it.shoppingType == "book" && !it.shoppingKey.isNullOrEmpty()) {
                //????????????
//                                    saBanner(advert, "??????")
                startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${it.shoppingKey}")
            } else if ("app" == it.shoppingType && !it.shoppingKey.isNullOrEmpty()) {
//                                    saBanner(advert, "?????????")
                WxMini.goWxMini(this, it.shoppingKey)
            } else if ("smartBook" == it.shoppingType) {
//                                    saBanner(advert, "?????????")
                startActivity<EBookListActivity>()
            } else if ("netSpecial" == it.shoppingType) {
//                                    saBanner(advert, "????????????")
                startActivity<LessonListActivity>(
                    LessonListActivity.KEY to it.shoppingKey,
                    LessonListActivity.TITLE to it.name
                )
            }
        }) {
        }
    }
    private var mSpeed = "1"//?????? ??????1
    private val adapter by lazy {
        object : PagerAdapter() {
            override fun getCount(): Int {
                return mLiveInfoList.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                container.addView(mLiveInfoList.get(position))
                return mLiveInfoList.get(position)
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(mLiveInfoList.get(position))
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestFullScreenFeature()
        fullScreen = true
        // ????????????
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        lessonJumpDialog.show()
    }

    override fun configView() {
        super.configView()
        initViews()
//        //???????????????????????????
//        val lastPosition = SPUtil.getInstance().getLong(recordId)
//        //??????????????????
//        if (lastPosition > 0) {
//            replay_room_layout.showJump(lastPosition, recordId)
//        } else {
//            replay_room_layout.setRecordId(recordId)
//        }
        initViewPager()
        initReceiver()
        presenter.getLiveBackPopList(sensorsData?.period_id ?: "") {
            playBackTime.clear()
            playBackTime.addAll(it)
            dwReplayCoreHandler.setRecordTag(recordId)
            mReplayVideoView.start()
            // ???????????????
//        mReplayVideoView.setAntiRecordScreen(this)
            initLessons()
        }

    }

    private fun initLessons() {
        rlc_lessons_replay.layoutManager = LinearLayoutManager(this@ReplayCCActivity)
        rlc_lessons_replay.adapter = lessonAdapter
        val params = rlc_lessons_replay.layoutParams
        if (lesson.netList is List<*>) {
            if (lesson.netList?.isEmpty() == true) {
                v_replay.visibility = View.GONE
                tv_chat_top.visibility = View.GONE
                rl_recommend.visibility = View.GONE
                rlc_lessons_replay.visibility = View.GONE
                v_line.setBackground(
                    ContextCompat.getDrawable(
                        this@ReplayCCActivity,
                        R.color.color_f5
                    )
                )
            } else if (lesson.netList?.size == 1) {
                v_replay.visibility = View.VISIBLE
                rl_recommend.visibility = View.VISIBLE
                tv_chat_top.visibility = View.VISIBLE
                rlc_lessons_replay.visibility = View.VISIBLE
                v_line.setBackground(
                    ContextCompat.getDrawable(
                        this@ReplayCCActivity,
                        R.drawable.chat_msg_shadow
                    )
                )
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                v_replay.visibility = View.VISIBLE
                tv_chat_top.visibility = View.VISIBLE
                rl_recommend.visibility = View.VISIBLE
                rlc_lessons_replay.visibility = View.VISIBLE
                v_line.setBackground(
                    ContextCompat.getDrawable(
                        this@ReplayCCActivity,
                        R.drawable.chat_msg_shadow
                    )
                )
                params.height = 610
            }
            rlc_lessons_replay.layoutParams = params
            lessonAdapter.setData(lesson.netList)
        } else {
            v_replay.visibility = View.GONE
            rl_recommend.visibility = View.GONE
            rlc_lessons_replay.visibility = View.GONE
            v_line.setBackground(ContextCompat.getDrawable(this@ReplayCCActivity, R.color.color_f5))
            tv_chat_top.visibility = View.GONE
        }
    }

    override fun initData() {
        super.initData()
    }

    // ?????????????????????
    private fun initReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                when {
                    NotificationUtil.ACTION_PLAY_PAUSE == action -> {
                        if (replay_room_layout != null) {
                            replay_room_layout.changePlayerStatus()
                        }
                        NotificationUtil.sendReplayNotification(this@ReplayCCActivity)
                    }
                    NotificationUtil.ACTION_DESTROY.equals(action) -> {
                        finish()
                    }
                    AppPhoneStateListener.ACTION_PAUSE == action -> {
                        DWLiveReplay.getInstance().pause()
                    }
                    AppPhoneStateListener.ACTION_RESUME == action -> {
                        DWLiveReplay.getInstance().resume()
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

    override fun onStop() {
        super.onStop()
        // ????????????
        if (!isFinishing) {
            NotificationUtil.sendReplayNotification(this)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        mReplayFloatingView.setFloatClick {
            replay_room_layout.mVideoDocSwitch.performClick()
        }
        tv_replay_sel.setOnClickListener {
            tv_replay_sel.isSelected = !tv_replay_sel.isSelected
            if (tv_replay_sel.isSelected) {
                sensorsData!!.let {
                    SaUtil.saOption(it, "??????????????????")
                }
                tv_replay_sel.text = "??????"
                rlc_lessons_replay.visibility = View.VISIBLE
                v_line.background =
                    ContextCompat.getDrawable(this@ReplayCCActivity, R.drawable.chat_msg_shadow)
                tv_chat_top.visibility = View.VISIBLE
            } else {
                sensorsData!!.let {
                    SaUtil.saOption(it, "??????????????????")
                }
                tv_replay_sel.text = "??????"
                rlc_lessons_replay.visibility = View.GONE
                v_line.background =
                    ContextCompat.getDrawable(this@ReplayCCActivity, R.color.color_f5)
                tv_chat_top.visibility = View.GONE
            }
        }
        tv_chat_top.setOnClickListener {
            sensorsData!!.let {
                SaUtil.saOption(it, "??????????????????")
            }
            tv_replay_sel.isSelected = false
            tv_replay_sel.text = "??????"
            rlc_lessons_replay.visibility = View.GONE
            v_line.background = ContextCompat.getDrawable(this@ReplayCCActivity, R.color.color_f5)
            tv_chat_top.visibility = View.GONE
        }
//        mReplayFloatingView.setFloatDismissListener(floatDismissListener)
        replay_room_layout.setReplayRoomStatusListener(roomStatusListener)
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
//                            Log.d("", "handleEvent: "+element.showTime.toLong())
                            break
                        }
                    }
                    oldPlayTime = it
                }
            }
        }
        with(mChatLayout) {
            setOnChatComponentClickListener(OnChatComponentClickListener { bundle ->
                if (bundle == null) return@OnChatComponentClickListener
                val type = bundle.getString("type")
                if (LivePublicChatAdapter.CONTENT_IMAGE_COMPONENT == type) {
                    //                val intent = Intent(this@ReplayPlayActivity, ImageDetailsActivity::class.java)
                    //                intent.putExtra("imageUrl", bundle.getString("url"))
                    //                startActivity(intent)
                } else if (LivePublicChatAdapter.CONTENT_ULR_COMPONET == type) {
                    val uri = Uri.parse(bundle.getString("url"))
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            })
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
                mTagList.get(position).setChecked(true)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        rg_infos_tag.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            sensorsData!!.let {
                when (i) {
                    R.id.live_portrait_info_chat -> SaUtil.saOption(it, "??????")
                    R.id.live_portrait_info_qa -> SaUtil.saOption(it, "??????")
                    R.id.live_portrait_info_intro -> SaUtil.saOption(it, "????????????")
                }
            }
            live_portrait_container_viewpager.setCurrentItem(mIdList.indexOf(i), true)
        })
        try {
            val time = lesson.recommendTime.toFloat().toInt()
            replay_room_layout.setRecommendBack(time) { type ->
                if ("1" == type) {
                    sensorsData?.let { data -> SaUtil.saRecommend(data, "$time") }
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
                sensorsData?.let {
                    SaUtil.saOption(it, "????????????(${speed})")
                }
            }

            override fun changeLine(line: Int) {
                sensorsData!!.let {
                    SaUtil.saOption(it, "????????????")
                }
            }

            override fun changeProgress() {
                sensorsData!!.let {
                    SaUtil.saOption(it, "????????????")
                }
            }

            override fun changePlayState() {
                sensorsData!!.let {
                    SaUtil.saOption(it, "??????/??????")
                }
            }
        })
    }

    private fun showTimeDialog(it: LiveBackPop) {
        horizontalDialog.dismiss()
        verticalDialog.dismiss()
        imgDialog.dismiss()
//                        ?????????  ??????:net  ??????:book ??????:link ?????????:app
        //                        ?????????:smartBook ???????????????netSpecial ????????????promotional
        if ("promotional".equals(it.shoppingType)) {
            if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                //??????
                it.promotionalList?.let { it1 ->
                    horizontalDialog.setData(it1)
                    horizontalDialog.show()
                }

            } else {
                //??????
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

    override fun onDestroy() {
        if (lessonJumpDialog.isShowing) {
            lessonJumpDialog.dismiss()
        }
        NotificationUtil.cancelNotification(this)
        unregisterReceiver(broadcastReceiver)
        mReplayFloatingView.dismiss()
        mReplayVideoView.destroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!isPortrait()) {
            quitFullScreen()
            return
        }
        showExitTips()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // ?????????????????????
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.decorView.systemUiVisibility = getSystemUiVisibility(true)
        } else {
            window.decorView.systemUiVisibility = getSystemUiVisibility(false)
        }
        //?????????????????????
        if (mReplayFloatingView != null) {
            mReplayFloatingView.onConfigurationChanged(newConfig.orientation)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorsData!!.let {
            SaUtil.saStartView(it)
        }
        // ????????????
        NotificationUtil.cancelNotification(this)
    }

    /**
     * ?????????????????????????????????
     * type:1 ??????60s???????????????????????????95%;
     *      2 ??????60s????????????????????????95%;
     *      3 ?????????60s????????????????????????95%
     */
    private fun recordPlayLog(type: String) {
        val map = mutableMapOf<String, String>()
        map["courseKey"] = "${sensorsData?.course_id ?: ""}"
        map["videoKey"] = "${sensorsData?.period_id ?: ""}"
        when (type) {
            "1" -> {
                map["total"] = "60"
            }
            "2" -> {
                map["total"] = "60"
                map["schedule"] = "1"//???????????????
            }
            "3" -> {
                map["schedule"] = "1"//???????????????
            }
        }
        presenter.recordPlayLog(map)
    }

    override fun onPause() {
        super.onPause()
        mReplayVideoView.pause()
        cancelTask()
        sensorsData!!.let {
            SaUtil.saFinishView(it)
        }
        //???????????????????????????
//        SPUtil.getInstance().put(recordId, cCPlayer.currentPosition)
    }

    // ???????????????????????????
    private fun showFloatingDocLayout() {
        // ????????????????????????????????????"??????"?????????????????????????????????????????????????????????
        if (dwReplayCoreHandler.hasDocView()) {
            mReplayFloatingView.show(mRoot)
        }
    }

    private fun initViews() {
        replay_room_layout.mTitle.text = mTitle
        tv_replay_sel.isSelected = true
        mRoot = window.decorView.findViewById(android.R.id.content)
        replay_room_layout.init(false)
        replay_room_layout.setActivity(this)
        showFloatingDocLayout()
    }

    /**************************************  Room ??????????????????  */
    private val roomStatusListener = object : BaseReplayRoomLayout.ReplayRoomStatusListener() {
        override fun switchVideoDoc(isVideoMain: Boolean) {
            sensorsData!!.let {
                SaUtil.saOption(it, "????????????")
            }
            switchView(isVideoMain)
        }

        override fun openVideoDoc() {
            if (replay_room_layout.isVideoMain) {
                mReplayFloatingView.show(mRoot)
                if (mDocLayout.parent != null) (mDocLayout.parent as ViewGroup).removeView(
                    mDocLayout
                )
                mReplayFloatingView.addView(mDocLayout)
            } else {
                mReplayFloatingView.show(mRoot)
                if (mReplayVideoView.parent != null) (mReplayVideoView.parent as ViewGroup).removeView(
                    mReplayVideoView
                )
                mReplayFloatingView.addView(mReplayVideoView)
            }
        }

        override fun closeRoom() {
            runOnUiThread { // ??????????????????????????????????????????????????????????????????????????????
                if (isPortrait()) {
                    showExitTips()
                } else {
                    quitFullScreen()
                }
            }
        }

        override fun fullScreen() {
            sensorsData!!.let {
                SaUtil.saOption(it, "????????????")
            }
            runOnUiThread {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                ll_pc_replay_msg_layout.visibility = View.GONE
                window.decorView.systemUiVisibility = getSystemUiVisibility(true)
            }
        }

        override fun onClickDocScaleType(type: Int) {
            if (mDocLayout != null) {
                mDocLayout.setScaleType(type)
            }
        }

        override fun onChangePlayMode(playMode: DWLiveReplay.PlayMode?) {
            mReplayVideoView.changeModeLayout(playMode)
        }

        override fun onDismissFloatView() {
            mReplayFloatingView.dismiss()
        }
    }

    fun switchView(isVideoMain: Boolean) {
        if (mReplayVideoView.parent != null) (mReplayVideoView.parent as ViewGroup).removeView(
            mReplayVideoView
        )
        if (mDocLayout.parent != null) (mDocLayout.parent as ViewGroup).removeView(mDocLayout)
        if (isVideoMain) {
            // ?????????????????????????????????
            mReplayFloatingView.addView(mDocLayout)
            rl_video_container.addView(mReplayVideoView)
            mDocLayout.setDocScrollable(false) //webview????????????
        } else {
            // ?????????????????????????????????
            mReplayFloatingView.addView(mReplayVideoView)
            rl_video_container.addView(mDocLayout)
            mDocLayout.setDocScrollable(true) //webview?????????
        }
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

    //---------------------------------- ?????????????????? --------------------------------------------/
    // ????????????
    private fun quitFullScreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ll_pc_replay_msg_layout.setVisibility(View.VISIBLE)

        window.decorView.systemUiVisibility = getSystemUiVisibility(false)
    }

    /**
     * ?????????ViewPager
     */
    private fun initViewPager() {
        initComponents()
        live_portrait_container_viewpager.offscreenPageLimit = 3
        live_portrait_container_viewpager.adapter = adapter
        if (mTagList != null && mTagList.size > 0) {
            mTagList.get(0).performClick()
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    private fun initComponents() {
        initVideoLayout()
        // ????????????????????????????????????"??????"??????
        if (dwReplayCoreHandler.hasDocView()) {
            initDocLayout()
        }
        // ????????????????????????????????????"??????"??????
        if (dwReplayCoreHandler.hasChatView()) {
            initChatLayout()
        }
        // ????????????????????????????????????"??????"??????
        if (dwReplayCoreHandler.hasQaView()) {
            initQaLayout()
        }
        // ???????????????
        initIntroLayout()
    }

    // ???????????????????????????
    private fun initChatLayout() {
        mIdList.add(R.id.live_portrait_info_chat)
        mTagList.add(live_portrait_info_chat)
        live_portrait_info_chat.visibility = View.VISIBLE
        mLiveInfoList.add(mChatLayout)
        if (mChatLayout != null) {
            replay_room_layout.setSeekListener(mChatLayout)
        }
    }

    // ???????????????????????????
    private fun initQaLayout() {
        mIdList.add(R.id.live_portrait_info_qa)
        mTagList.add(live_portrait_info_qa)
        live_portrait_info_qa.visibility = View.VISIBLE
        mLiveInfoList.add(mQaLayout)
    }

    // ???????????????????????????
    private fun initIntroLayout() {
        mIdList.add(R.id.live_portrait_info_intro)
        mTagList.add(live_portrait_info_intro)
        live_portrait_info_intro.visibility = View.VISIBLE
        mLiveInfoList.add(mDocPageInfoListComponent)
    }

    // ???????????????????????????
    private fun initVideoLayout() {
        if (replay_room_layout.isVideoMain) {
            rl_video_container.addView(mReplayVideoView)
        } else {
            if (DWReplayCoreHandler.getInstance().hasDocView()) {
                mReplayFloatingView.addView(mReplayVideoView)
            } else {
                rl_video_container.addView(mReplayVideoView)
            }
        }
    }

    // ???????????????????????????
    private fun initDocLayout() {
        if (replay_room_layout.isVideoMain) {
            mReplayFloatingView.addView(mDocLayout)
        } else {
            if (dwReplayCoreHandler.hasDocView()) {
                rl_video_container.addView(mDocLayout)
                mDocLayout.setDocScrollable(true)
            }
        }
        // ????????????
        showFloatingDocLayout()
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
                    sensorsData?.let { data ->
                        SaUtil.saDotMinute(
                            data,
                            timeM,
                            (cCPlayer.currentPosition / 1000).toInt(),
                            mSpeed
                        )
                    }
                    recordPlayLog(if (replay_room_layout.isFinish) "2" else "1")
                }
                if (!progressUp) {
                    progressUp = replay_room_layout.isFinish
                    if (progressUp) {
                        //???????????????????????????????????????  ??????????????? ?????????????????????
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