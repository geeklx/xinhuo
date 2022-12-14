package tuoyan.com.xinghuo_dayingindex.ui.cc
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.bokecc.livemodule.live.DWLiveCoreHandler
import com.bokecc.livemodule.live.chat.KeyboardHeightProvider
import com.bokecc.livemodule.live.chat.OnChatComponentClickListener
import com.bokecc.livemodule.live.chat.adapter.LivePublicChatAdapter
import com.bokecc.livemodule.live.function.FunctionCallBack
import com.bokecc.livemodule.live.function.FunctionHandler
import com.bokecc.livemodule.utils.AppPhoneStateListener
import com.bokecc.livemodule.view.BaseRelativeLayout
import com.bokecc.sdk.mobile.live.pojo.BroadCastMsg
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_goods_live.*
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.Coupon
import tuoyan.com.xinghuo_dayingindex.bean.NetLesson
import tuoyan.com.xinghuo_dayingindex.ui.cc.dialog.AlertDialogFactory
import tuoyan.com.xinghuo_dayingindex.ui.cc.dialog.LessonJumpDialog
import tuoyan.com.xinghuo_dayingindex.ui.cc.livegoods.LiveGoodsRoomLayout
import tuoyan.com.xinghuo_dayingindex.ui.cc.livegoods.LiveGoodsVideoView
import tuoyan.com.xinghuo_dayingindex.ui.cc.livegoods.OnGoodsChatComponentClickListener
import tuoyan.com.xinghuo_dayingindex.ui.cc.util.NotificationUtil
import tuoyan.com.xinghuo_dayingindex.ui.dialog.CouponListDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.GoodsListDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.LiveGoodsCouponDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.StateDescriptionDialog
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.NetLessonsPresenter
import java.util.*

class GoodsLiveActivity : LifeActivity<NetLessonsPresenter>() {


    override val layoutResId: Int
        get() = R.layout.activity_goods_live
    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)

    private val mTitle by lazy { intent.getStringExtra("title") ?: "" }
    private val lessonJumpDialog by lazy { LessonJumpDialog(this, mTitle) }

    private var mRoot: View? = null
//    private val sensorsData by lazy { intent.getParcelableExtra("SensorsData") as? SensorsData }

    // ???????????????
    private var broadcastReceiver: BroadcastReceiver? = null

    private val lesson by lazy { intent.getSerializableExtra("lesson") as NetLesson }

    // ????????????
    private val mLiveVideoView by lazy { LiveGoodsVideoView(this) }
    private val dwLiveCoreHandler by lazy { DWLiveCoreHandler.getInstance() }

    // ?????????????????????????????????????????????/???????????????????????????
    private val mFunctionHandler by lazy { FunctionHandler() }
    private var isVote = false//?????????????????????

    //*************************************** ???????????? ***************************************/
    private val mLiveInfoList by lazy { ArrayList<View>() }
    private val mIdList by lazy { ArrayList<Int>() }

    val dialog by lazy {
        GoodsListDialog(this@GoodsLiveActivity,
            fun(item, liveManagementKey): Unit {
//                      toast("????????????item$item")
                val intent = Intent(this@GoodsLiveActivity, LessonDetailActivity::class.java)
                intent.putExtra(LessonDetailActivity.KEY, item.key)
                intent.putExtra(LessonDetailActivity.FROM_TYPE, liveManagementKey)
                startActivity(intent)

            }, fun(): Unit {
                getLivePromotionals()
            })
    }
    val couponListDialog by lazy {
        CouponListDialog(this@GoodsLiveActivity, fun(item) {
//            toast("??????????????????$item")
            getCoupon(item)
        }, fun() {
//            toast("????????????")
        })
    }
    val liveGoodsCouponDialog by lazy {
        LiveGoodsCouponDialog(this@GoodsLiveActivity) { item ->
            run {
                presenter.exchangeCoupon(mutableMapOf("pKey" to item.key, "fromType" to "7"), {
                    saSensors(it)
                    item?.ownNum = it.isOwn
                    onRefresh(item)
                    if (it.isOwn == "1") {
                        stateDescriptionDialog.show()
                    }
                })
            }
        }
    }

    fun onRefresh(item: Coupon) {
        liveGoodsCouponDialog?.setCoupon(item)
    }

    val stateDescriptionDialog by lazy {
        StateDescriptionDialog(this@GoodsLiveActivity, "?????????????????????")
    }
    var coupon: Coupon? = null
    var allCoupon: Coupon? = null
    fun getCoupon(item: Coupon) {
        presenter.exchangeCoupon(mutableMapOf("pKey" to item.key, "fromType" to "7")) {
            saSensors(it)
            item.isOwn = it.isOwn
            couponListDialog.dataRefresh()
//            adapter.notifyDataSetChanged()
            if (it.isOwn == "1") {
                stateDescriptionDialog.show()
            }
//            Toast.makeText(MainCouponActivity@ this, "?????????????????????", Toast.LENGTH_LONG).show()
        }
    }

    private fun initReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (NotificationUtil.ACTION_PLAY_PAUSE.equals(action)) {
                    NotificationUtil.sendLiveGoodNotification(this@GoodsLiveActivity)
                } else if (NotificationUtil.ACTION_DESTROY.equals(action)) {
                    finish()
                } else if (AppPhoneStateListener.ACTION_RESUME == action) {
                    // ????????????video
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

    //---------------------------------- ????????????????????? --------------------------------------------/
    private val roomStatusListener by lazy {
        object : LiveGoodsRoomLayout.LiveRoomStatusListener {
            // ??????/?????????????????? ???????????? #Called From LiveRoomLayout
            @Synchronized
            override fun switchVideoDoc(isVideoMain: Boolean) {
//                sensorsData!!.let {
//                    SaUtil.saOption(it, "????????????")
//                }
                switchView(isVideoMain) // ????????????
            }

            override fun openVideoDoc() {

            }

            // ???????????????
            override fun closeRoom() {
                runOnUiThread { // ??????????????????????????????????????????????????????????????????????????????
                    if (isPortrait()) {
                        showExitTips()
                    } else {
                        quitFullScreen()
                    }
                }
            }

            // ??????
            override fun fullScreen() {
//                sensorsData!!.let {
//                    SaUtil.saOption(it, "????????????")
//                }

            }

            // ???????????????
            override fun kickOut() {
                runOnUiThread {
                    Toast.makeText(this@GoodsLiveActivity, "???????????????????????????", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onClickDocScaleType(scaleType: Int) {

            }

            override fun onStreamEnd() {

            }

            override fun onStreamStart() {

            }

            override fun switchDanMu() {
//                sensorsData!!.let {
//                    SaUtil.saOption(it, "??????????????????")
//                }
            }

            override fun sendChat() {
//                sensorsData!!.let {
//                    SaUtil.saOption(it, "????????????")
//                }
            }

            override fun onDismissFloatView() {

            }
        }
    }

    private fun switchView(isVideoMain: Boolean) {
        if (mLiveVideoView.parent != null) {
            (mLiveVideoView.parent as ViewGroup).removeView(mLiveVideoView);
        }
//        if (DWLiveCoreHandler.getInstance().hasPdfView()) {
//            if (mDocLayout.parent != null) {
//                (mDocLayout.parent as ViewGroup).removeView(mDocLayout);
//            }
//        }
        //---------------------------------------------player??????????????????
//        if (isVideoMain) {
//            if (DWLiveCoreHandler.getInstance().hasPdfView()) {
//                mLiveFloatingView.addView(mDocLayout);
//                mDocLayout.setDocScrollable(false);
//            }
        rl_video_container.addView(mLiveVideoView);
//        } else {
//            mLiveFloatingView.addView(mLiveVideoView);
//            if (DWLiveCoreHandler.getInstance().hasPdfView()) {
//                rl_video_container.addView(mDocLayout, 0);
//                mDocLayout.setDocScrollable(true);
//            }
//        }
    }

    private val keyboardHeightProvider by lazy { KeyboardHeightProvider(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
//        requestFullScreenFeature()
        fullScreen = true
//        // ????????????
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //    ImmersionBar.with(this@GoodsLiveActivity).fitsSystemWindows(true).statusBarColor(R.color.black).statusBarDarkFont(false).keyboardEnable(true).init()
        ImmersionBar.with(this)
            .fullScreen(true)
            .statusBarColor(R.color.transparent)
            .statusBarDarkFont(true)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()
        super.onCreate(savedInstanceState)

        lessonJumpDialog.show()
    }

    //    private var startTime = 0L
    override fun onResume() {
        super.onResume()
        keyboardHeightProvider.addKeyboardHeightObserver(live_goods_chat)
//        keyboardHeightProvider.addKeyboardHeightObserver(live_room_layout)
//        keyboardHeightProvider.addKeyboardHeightObserver(mQaLayout)
        mFunctionHandler.setRootView(mRoot)
        // ????????????
        mLiveVideoView.start()
        // ????????????
        live_room_layout.openLiveBarrage()
        // ????????????
        NotificationUtil.cancelNotification(this)
//        sensorsData!!.let { SaUtil.saStartView(it) };
        initTask()
        if (dialog?.isShowing!!) {//???????????????????????????????????????
            presenter.getLiveShopping {
//                    toast("????????????");
                live_goods_chat.findViewById<TextView>(R.id.tv_goods_num).text =
                    it.list?.size.toString()
                it.list?.let { it1 ->
                    dialog.setData(it1, it.msg, it.liveManagementKey)
                }

            }
        }
    }

    override fun configView() {
        super.configView()
        mRoot = window.decorView.findViewById(android.R.id.content)
        // ???????????????????????????????????????????????????
        live_room_layout.init(false)
        live_room_layout.setActivity(this)
        initViewPager()
//        initLessons()
//        switchView(false)
        live_room_layout.mLiveTitle.text = mTitle
        // ????????????????????????
        initReceiver()
        // ???????????????
//        mLiveVideoView.setAntiRecordScreen(this)
    }


    override fun initData() {
        super.initData()
        presenter.getLiveShopping {
//                    toast("????????????");
            live_goods_chat.findViewById<TextView>(R.id.tv_goods_num).text =
                it.list?.size.toString()
            it.list?.let { it1 ->
                dialog.setData(it1, it.msg, it.liveManagementKey)
            }

        }
    }

    //???????????????????????????
    private val functionCallBack: FunctionCallBack by lazy {
        object : FunctionCallBack() {
            override fun onMinimize(isVote: Boolean) {
                super.onMinimize(isVote)
                this@GoodsLiveActivity.isVote = isVote
            }

            override fun onClose() {
                super.onClose()
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()

        initRoomStatusListener()
        mFunctionHandler.initFunctionHandler(this, functionCallBack)

        mRoot?.viewTreeObserver?.addOnGlobalLayoutListener {
            keyboardHeightProvider.start()
        }
        live_room_layout.setEvent(object : BaseRelativeLayout.SaEvent {
            override fun changeSpeed(speed: Float) {
            }

            override fun changeLine(line: Int) {
//                sensorsData!!.let {
//                    SaUtil.saOption(it, "????????????")
//                }
            }

            override fun changeProgress() {
            }

            override fun changePlayState() {
            }
        })
        live_room_layout.setOnClickFunctionListener {
            allCoupon?.key?.let { key ->
                presenter.couponDetail(key, "7") {
                    live_room_layout?.setCoupon(it, true)
                    allCoupon = it
                    allCoupon?.key = key
                    liveGoodsCouponDialog.show()
                    liveGoodsCouponDialog.setCoupon(it)
                }
            }


        }
//        presenter.couponDetail("1559786324998390400","7") {
//            live_room_layout?.setCoupon(it, true)
//            coupon=it
//            coupon?.key="1559786324998390400"
//        }
    }

    override fun onBackPressed() {
        if (!isPortrait()) {
            if (!live_room_layout.onBackPressed()) {
                quitFullScreen()
            }
            return
        } else {
            if (live_goods_chat != null && live_goods_chat.onBackPressed()) {
                return
            }
        }
        // ??????????????????
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

    // ????????????
    private fun quitFullScreen() {

    }

    /**
     * ?????????ViewPager
     */
    private fun initViewPager() {

        initComponents()

    }

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider.clearObserver()
        mFunctionHandler.removeRootView()
        // ????????????
        live_room_layout.closeLiveBarrage()
        mLiveVideoView.stop()
//        sensorsData!!.let { SaUtil.saFinishView(it) }
        cancelTask()
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishing) {
            NotificationUtil.sendLiveGoodNotification(this)
        }
    }

    /**
     * ?????????????????????
     */
    fun recordPlayLog() {
        val map = mutableMapOf<String, String>()
//        map["courseKey"] = "${sensorsData?.course_id ?: ""}"
//        map["videoKey"] = "${sensorsData?.period_id ?: ""}"
//        map["total"] = "60"
//        presenter.recordPlayLog(map)
    }

    override fun onDestroy() {
        keyboardHeightProvider.close()
        mLiveVideoView.stop()
        live_room_layout.destroy()
        mFunctionHandler.onDestroy(this)
        mLiveVideoView.destroy()
        // ??????????????????
        unregisterReceiver(broadcastReceiver)
        lessonJumpDialog?.let {
            lessonJumpDialog?.dismiss()
        }
        super.onDestroy()
    }

    //??????????????????????????????????????????
    private fun initComponents() {
        initVideoLayout()


        // ????????????????????????????????????"??????"??????
//        if (dwLiveCoreHandler.hasChatView()) {
        initChatLayout()
//        }
        // ????????????????????????
        dwLiveCoreHandler.setNoticeCallback { content ->
//            sensorsData?.let { SaUtil.saNotice(it, content) }
        }

    }

    private fun initVideoLayout() {
        if (mLiveVideoView.parent != null) {
            (mLiveVideoView.parent as ViewGroup).removeView(mLiveVideoView);
        }
        rl_video_container.addView(mLiveVideoView);
    }


    // ???????????????????????????
    private fun initChatLayout() {
        mIdList.add(R.id.live_portrait_info_chat)
        live_goods_chat.setPopView(mRoot)
        mLiveInfoList.add(live_goods_chat)
        live_goods_chat.setBarrageLayout(live_room_layout.liveBarrageLayout)
        live_goods_chat.setOnChatComponentClickListener(OnChatComponentClickListener { bundle ->
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
        live_goods_chat.setOnGoodsChatComponentClickListener(object :
            OnGoodsChatComponentClickListener {
            override fun onClickGoodsList() {
                dialog.show()
//                presenter.getLiveShopping {
//                    live_goods_chat.findViewById<TextView>(R.id.tv_goods_num).text =
//                        it.list?.size.toString()
////                    toast("????????????");
//                    it.list?.let { it1 ->
//                        dialog.show()
//                        dialog.setData(it1, it.msg,it.liveManagementKey)
//                    }
//
//                }

            }

            override fun onClickLikes() {
//                toast("??????");
//                CommentDialog(this@GoodsLiveActivity).show()
            }

            override fun onBroadcastMsg(msg: BroadCastMsg?) {
                msg?.content?.let { content ->
                    if (content == "????????????") {
                        presenter.getLiveShopping {
                            live_goods_chat.findViewById<TextView>(R.id.tv_goods_num).text =
                                it.list?.size.toString()
                            it.list?.let { it1 ->
                                dialog.setData(it1, it.msg, it.liveManagementKey)
                            }

                        }
                    } else if (content.startsWith("???????????????")) {
                        presenter.couponDetail(content.substring(5), "7") {
                            Log.d("zhao", "onBroadcastMsg: ${it.isUniversal}")
                            //0?????????????????????
                            if (it.isUniversal == "5") {

                                live_room_layout?.setCoupon(it, true)
                                allCoupon = it
                                allCoupon?.key = content.substring(5)
                                liveGoodsCouponDialog.timeShow()
                                liveGoodsCouponDialog.setCoupon(it)
                            } else if (it.isUniversal == "0") {
                                coupon = it
                                coupon?.key = content.substring(5)
                                liveGoodsCouponDialog.timeShow()
                                liveGoodsCouponDialog.setCoupon(it)

                            }

                        }
                    }

                }
            }

        })
    }


    // ???????????????????????????
    private fun initRoomStatusListener() {
        if (live_room_layout == null) {
            return
        }
        live_room_layout.setLiveRoomStatusListener(roomStatusListener)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // ?????????????????????
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.decorView.systemUiVisibility = getSystemUiVisibility(true)
        } else {
            window.decorView.systemUiVisibility = getSystemUiVisibility(false)
        }

        // ????????????????????????
        live_room_layout.showInput(newConfig.orientation)
    }

    //********************************** ???????????? *******************************************/
    // ???????????????
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
//                sensorsData?.let { data -> SaUtil.saDotMinute(data, stayTime, 0, "1") }
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

    //????????????????????????
    private fun getLivePromotionals() {
        presenter.getLivePromotionals {
            couponListDialog.setData(it)
            couponListDialog.show()
        }

    }

    private fun saSensors(item: Coupon) {
        try {
            val property = saProperty(item)
            SensorsDataAPI.sharedInstance().track("collect_selected_coupons", property)
        } catch (e: Exception) {
        }
    }

    private fun saProperty(item: Coupon): JSONObject {
        val property = JSONObject()
        property.put("coupon_id", item.key)
        property.put("coupon_name", item.name)
        property.put(
            "coupon_threshold",
            if (item.orderAmountLimit.isNullOrEmpty() || item.orderAmountLimit == "0") "?????????" else "${item.orderAmountLimit}"
        )
        property.put("coupon_amount", item.facevalue)
        property.put("coupon_validity", "${item.startTime}-${item.endTime}")
        property.put("receiving_location", "????????????")
        property.put("receiving_method", "????????????")
        property.put("coupon_notes", item.remarks)
        return property
    }

}