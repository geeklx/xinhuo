package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.bokecc.sdk.mobile.live.DWLive
import com.bokecc.sdk.mobile.live.DWLiveLoginListener
import com.bokecc.sdk.mobile.live.Exception.DWLiveException
import com.bokecc.sdk.mobile.live.pojo.*
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.shuyu.gsyvideoplayer.GSYVideoManager
import kotlinx.android.synthetic.main.activity_lesson_deatil.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import org.json.JSONObject
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL_PC
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.LifeActivityStateAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.GoodsLiveActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.CouponDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.mine.order.PaymentSuccessActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.LessonsPresenter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.adapter.LessonCouponAdapter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoPortraitActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.MineLessonActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.UdeskUtils
import tuoyan.com.xinghuo_dayingindex.widegt.RadiusBackgroundSpan
import udesk.core.model.Product
import kotlin.math.ceil
import kotlin.math.floor

class LessonDetailActivity : LifeActivity<LessonsPresenter>() {
    override val presenter: LessonsPresenter
        get() = LessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_lesson_deatil
    var updateCatalog = false//在onresmue中更新lessonCatalogFragment 重新获取数据
    private val commentFragment by lazy { LessonComFragment() }
    private val TENDAYLIMIT = 10 * 24 * 3600 * 1000//拼团网课10天限制，10天以内展示倒计时，10天意外不展示

    private val isLogin by lazy { SpUtil.isLogin }//进来时是否登录
    val key by lazy { intent.getStringExtra(KEY) ?: "" }
    val type by lazy { intent.getStringExtra(TYPE) ?: "" }
    val fromType by lazy { intent.getStringExtra(FROM_TYPE) ?: "" }
    var detail: LessonDetail? = null
    private val catalogFragment by lazy { LessonCatalogFragment.newInstance(key) }

    private var cTimer: CountDownTimer? = null
    private var checkLogin = false;
    private val adapter by lazy {
        Adapter() {
            detail?.let { it1 -> saClick(it1, "切换组合课") }
            startActivity<LessonDetailActivity>(KEY to it.key)
            finish()
        }
    }

    private val couponAdapter by lazy {
        LessonCouponAdapter()
    }

    private val couponDialog by lazy {
        CouponDialog(this) { list, pos ->
            detail?.let { saClick(it, "领取优惠券") }
            freshDialog(list, pos)
        }
    }
    private var lav_play: LottieAnimationView? = null
    private var free_lav_play: LottieAnimationView? = null
    private var liveFlag: LiveFlag? = null
    private val pagerAdapter by lazy { LifeActivityStateAdapter(this) }

    companion object {
        val KEY = "key"
        val TYPE = "type"
        val FROM_TYPE = "fromType"
    }

    private fun freshDialog(data: List<Coupon>, pos: Int) {
        val item = data[pos]
        presenter.exchangeCoupon(mutableMapOf("pKey" to item.key)) {
            saSensors(it)
            item.isOwn = it.isOwn
            couponDialog.couponAdapter.notifyDataSetChanged()
            Toast.makeText(LessonDetailActivity@ this, "优惠券领取成功", Toast.LENGTH_LONG).show()
        }
    }

    override fun configView() {
        setSupportActionBar(tb_lesson_detail)
        val manager = LinearLayoutManager(this@LessonDetailActivity)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        rlv_lessons.layoutManager = manager
        adapter.key = key
        rlv_lessons.adapter = adapter
        val couponManager = LinearLayoutManager(this@LessonDetailActivity)
        couponManager.orientation = LinearLayoutManager.HORIZONTAL
        rlv_coupon.layoutManager = couponManager
        rlv_coupon.adapter = couponAdapter

        vp_lesson_detail.offscreenPageLimit = 10
        vp_lesson_detail.adapter = pagerAdapter
        drag_float.setOnClickListener({})
        lav_play = drag_float.findViewById<LottieAnimationView>(R.id.lav_play)
        drag_float.findViewById<View>(R.id.live_goods).setOnClickListener({
            checkLogin = false;
            isLogin {  //点击进入到直播带货
                checkLogin = true
                liveFlag?.let {
                    goLiveGoodsLiving(liveFlag!!)
                }
            }


        })
        drag_float.findViewById<View>(R.id.iv_close).setOnClickListener({
            if (drag_float.visibility == View.VISIBLE) {
                drag_float.visibility = View.GONE
            }
        })
        free_drag.setOnClickListener({})
        free_lav_play = free_drag.findViewById<LottieAnimationView>(R.id.free_lav_play)
        free_drag.findViewById<View>(R.id.rl).setOnClickListener({
            val videoParam = VideoParam()
            videoParam.key = detail?.playbackSourceKey ?: ""
            videoParam.type = "1.1"
            startActivity<VideoPortraitActivity>(VideoActivity.VIDEO_PARAM to videoParam)
        })

    }

    private fun goLiveGoodsLiving(liveFlag: LiveFlag) {
        val loginInfo = LoginInfo()
        loginInfo.roomId = liveFlag.roomId
        loginInfo.userId = liveFlag.userId
        loginInfo.viewerName = SpUtil.userInfo.name
        loginInfo.viewerToken = liveFlag.viewertoken
        DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
            override fun onLogin(templateInfo: TemplateInfo?, viewer: Viewer?, roomInfo: RoomInfo?, publishInfo: PublishInfo?) {
                val intent = Intent(this@LessonDetailActivity, GoodsLiveActivity::class.java)
                val bundle = Bundle()
//                bundle.putSerializable("marquee", viewer?.getMarquee())
                bundle.putString("title", getString(R.string.live_goods_name))
//                bundle.putSerializable("lesson", netLesson)
//                bundle.putParcelable("SensorsData", data)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onException(e: DWLiveException?) {
                runOnUiThread {
                    Toast.makeText(this@LessonDetailActivity, e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }, loginInfo)
        // 执行登录操作
        DWLive.getInstance().startLogin()
    }

    override fun handleEvent() {
        rlv_coupon.setOnTouchListener { view, motionEvent ->
            if (MotionEvent.ACTION_UP == motionEvent.action) {
                ctl_coupon.performClick()
            }
            false
        }
        ctl_coupon.setOnClickListener {
//            优惠券弹窗入口
            presenter.netPromotional(key) {
                couponDialog.setData(it as ArrayList<Coupon>)
                couponDialog.show()
            }
        }
        rl_assemble.setOnClickListener {
            detail?.let { it1 -> initAssembleDialog(it1.assembleRemarks) }
        }
        tb_lesson_detail.setNavigationOnClickListener { onBackPressed() }
        ic_share.setOnClickListener {
            isLogin {
                ShareDialog(this) {
                    detail?.let { it1 -> saClickShare(it1, it.toString()) }
                    ShareUtils.setCallback(object : ShareUtils.Callback {
                        override fun onSure() {
                            addIntegral("网课")
                        }
                    })
                    ShareUtils.share(
                        this, it, detail?.title ?: "", "", WEB_BASE_URL + "network/network?key=" + key
                    )
                }.show()
            }
        }
        tv_service.setOnClickListener {
            isLogin {
                detail?.let { it1 -> saClick(it1, "咨询客服") }
                val product = Product()
                product.url = "${WEB_BASE_URL_PC}network/networkDetails?key=${key}"
                product.imgUrl = detail?.coverTopImg ?: ""
                product.name = detail?.title ?: ""
                val bean = Product.ParamsBean()
                bean.color = "#ff7100"
                bean.size = 15
                bean.isFold = true
                bean.text = tv_price.text.toString()
                val params = mutableListOf<Product.ParamsBean>()
                params.add(bean)
                product.params = params
                UdeskUtils.openChatView(this, product)
            }
        }
        btn_buy.setOnClickListener {
            isLogin {
                detail?.let { detail ->
                    if ((detail.dpState == "21" || detail.dpState == "11") && detail.isown == "0") {
                        saClick(detail, "购买")
                        startActivity<PostActivity>(PostActivity.URL to "ordersubmit?dpKey=${detail.dpKey}&dpOrderKey=${detail.dpOrderKey}&goodsKey=$key&goodsNum=1&goodsType=2&isCart=0" + if (!TextUtils.isEmpty(fromType)) "&liveManagementKey=$fromType" else "")
                    } else if (detail.dpState == "20" && detail.isown == "0") {

                    } else if (detail.isown == "0" && detail.isAppointedNet == "1" && detail.isAppointment == "1" || "3.1" == type && detail.state == "1") {
                        //未购买 已预约，不做任何操作
                    } else if (detail.isown == "0" && detail.isAppointment == "1") {
                        //为购买，未预约，调用预约，弹出预约成功提示
                        presenter.addShoppingAppointment(key) {
                            initDialog()
                            btn_buy.text = "已预约"
                            detail.isAppointedNet = "1"
                        }
                    } else if ((detail.isown == "0" || detail.iseffect == "1") && detail.isSoldOut == "0") {
                        if (detail.chargetype == "0" || detail.isLimitFree == "1") {
                            saClick(detail, "领取免费课程")
                            presenter.freeBuyNetCourse(key) {
                                if ("1" == it.isAdditiveGroup) {
                                    //一键加群
                                    val intent = Intent(
                                        this@LessonDetailActivity, PaymentSuccessActivity::class.java
                                    )
                                    intent.putExtra(PaymentSuccessActivity.DATA, it)
                                    startActivity(intent)
                                } else {
                                    detail.isown = "1"
                                    detail.iseffect = "0"
                                    mToast("领取成功")
                                    btn_buy.text = "去上课"
                                }
                            }
                        } else {
                            saClick(detail, "购买")
                            startActivity<PostActivity>(PostActivity.URL to "ordersubmit?goodsKey=$key&goodsNum=1&goodsType=2&isCart=0" + if (!TextUtils.isEmpty(fromType)) "&liveManagementKey=$fromType" else "")
                        }
                    } else if (detail.isown == "1" && detail.iseffect != "1") {
                        startActivity<MineLessonActivity>(MineLessonActivity.KEY to key)
                    }
                }
            }
        }
        tv_buy.setOnClickListener {
            isLogin {
                if ("1" != detail?.isown) {
                    startActivity<PostActivity>(PostActivity.URL to "ordersubmit?goodsKey=$key&goodsNum=1&goodsType=2&isCart=0" + if (!TextUtils.isEmpty(fromType)) "&liveManagementKey=$fromType" else "")
                }
            }
        }
        tv_assemble_buy.setOnClickListener {
            detail?.let { detail ->
                isLogin {
                    if (!detail.jointProgramKey.isNullOrBlank()) {
                        startActivity<PostActivity>(
                            PostActivity.URL to "jointbuy?goodsKey=$key&goodsNum=1&goodsType=2&isCart=0&jointProgramKey=${detail.jointProgramKey}" + if (!TextUtils.isEmpty(fromType)) "&liveManagementKey=$fromType" else "",
                            PostActivity.ASSEMBLE_KEY to detail.assembleKey,
                            PostActivity.LESSON_KEY to key
                        )
                    } else if (detail.orderKey.isNullOrEmpty()) {
                        startActivity<PostActivity>(
                            PostActivity.URL to "ordersubmit?goodsKey=$key&goodsNum=1&goodsType=2&isCart=0&assembleKey=${detail.assembleKey}", PostActivity.ASSEMBLE_KEY to detail.assembleKey, PostActivity.LESSON_KEY to key
                        )
                    } else {
                        startActivity<PostActivity>(PostActivity.URL to "orderdetail?orderId=${detail.orderKey}" + if (!TextUtils.isEmpty(fromType)) "&liveManagementKey=$fromType" else "")
                    }
                }
            }
        }
        rg_lesson.setOnCheckedChangeListener { radioGroup, checkedId ->
            val rb = radioGroup.findViewById(checkedId) as RadioButton
            vp_lesson_detail.currentItem = radioGroup.indexOfChild(rb) / 2
            detail?.let { saClick(it, rb.text.toString()) }
        }
        vp_lesson_detail.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val rb = rg_lesson.getChildAt(position * 2) as RadioButton
                if (!rb.isChecked) {
                    rb.isChecked = true
                }
            }
        })
    }

    override fun initData() {
        if (key.isNullOrBlank()) return
        presenter.getLessonDetail(key) { lessonDetail ->
            detail = lessonDetail
            if (lessonDetail.state == "1") {
                if ("3" != type && lessonDetail.isAppointment != "1") {
                    toast("该网课未上架")
                    onBackPressed()
                }
            } else if (lessonDetail.state == "9") {
                toast("该网课不存在")
                onBackPressed()
            }
            if (lessonDetail.composeList != null && lessonDetail.composeList!!.size > 0) {
                rlv_lessons.visibility = View.VISIBLE
                adapter.setData(lessonDetail.composeList)
            } else {
                rlv_lessons.visibility = View.GONE
            }
            bindData(lessonDetail)
            bindPager(lessonDetail)
            if (!lessonDetail.resourceKey.isNullOrEmpty() && lessonDetail.resourceKey.isNotEmpty()) {
                presenter.getResourceInfo(lessonDetail.resourceKey, "1.1") {
                    lessonDetail.videourl = it.url
                    initLessonMedia()
                }
            } else {
                initLessonMedia()
            }
        }
        getLiveFlag()
    }

    private fun getLiveFlag() {
        presenter.getLiveFlag("h3", {
            liveFlag = it
            if (it.isShow == 0) {
                drag_float.visibility = View.GONE
                lav_play?.cancelAnimation()
                lav_play?.progress = 0f
            } else {
                drag_float.visibility = View.VISIBLE
                lav_play?.playAnimation()
            }
        })
    }

    private fun getData() {
        if (key.isNullOrBlank()) return
        presenter.getLessonDetail(key) { lessonDetail ->
            detail = lessonDetail
            if (lessonDetail.state == "1") {
                if ("3" != type && lessonDetail.isAppointment != "1") {
                    toast("该网课未上架")
                    onBackPressed()
                }
            } else if (lessonDetail.state == "9") {
                toast("该网课不存在")
                onBackPressed()
            }
            bindData(lessonDetail)
        }
    }

    private fun bindData(data: LessonDetail) {
        if (data.promotionalList.isNullOrEmpty()) {
            ctl_coupon.visibility = View.GONE
        } else {
            ctl_coupon.visibility = View.VISIBLE
            couponAdapter.setData(data.promotionalList)
        }
        //倒计时背景颜色默认拼团颜色color_ff9c00，正在预约颜色#15D25F
        if (!data.assembleKey.isNullOrEmpty() && data.isown != "1" && data.isSoldOut != "1") {//拼团活动结束之后按正常网课处理assembleKey为空
            rl_assemble.visibility = View.VISIBLE
            tv_assemble_num.text = "邀请${data.assembleLimitNum.toInt() - 1}名好友即可拼团购买"
            ll_assemble_buy.visibility = View.VISIBLE
            btn_buy.visibility = View.GONE
            if ("1" == data.assembleState) {// 0预热 1拼团开始
                ll_assemble_buy.visibility = View.VISIBLE
                btn_buy.visibility = View.GONE

                tv_buy.text = Html.fromHtml("${data.disprice}元<br><small>原价购买</small>")
                tv_assemble_buy.text = if (data.assembleNumber.isNullOrEmpty()) {
                    Html.fromHtml("${data.assemblePrice}元<br><small>${data.assembleLimitNum}人拼团</small>")
                } else {
                    Html.fromHtml("(${data.assembleNumber}/${data.assembleLimitNum})<br><small>拼团中</small>")
                }
            } else {
                ll_assemble_buy.visibility = View.GONE
                btn_buy.visibility = View.VISIBLE
            }
            if ("0" == data.assembleState) {
                downTimer(data.assembleStartTime - System.currentTimeMillis())
            } else {
                var time = data.assembleEndTime - System.currentTimeMillis()
                if (time < TENDAYLIMIT) {
                    downTimer(data.assembleEndTime - System.currentTimeMillis())
                }
            }
        } else if (data.isAppointment == "1" && data.appointmentStartTime > 0) {
            //预约课倒计时
            ll_down_time.setBackgroundResource(R.color.color_15d25f)
            downTimer(data.appointmentStartTime - System.currentTimeMillis())
        } else if (!data.jointProgramKey.isNullOrBlank() && data.isown != "1") {
            ll_down_time.visibility = View.GONE
            rl_assemble.visibility = View.GONE
            ctl_union.visibility = View.VISIBLE
            ll_assemble_buy.visibility = View.VISIBLE
            btn_buy.visibility = View.GONE
            tv_union_w.text = data.jPSaleRemark
        } else if (data.dpState == "11" && data.isown != "1") {
            ll_down_time.visibility = View.VISIBLE
            tv_down_time.textColor = ContextCompat.getColor(this, R.color.color_222)
            img_down_time.setImageResource(R.mipmap.icon_count_down_black)
            ll_down_time.setBackgroundResource(R.color.color_ffea00)
            downTimer(data.dpEndTime - System.currentTimeMillis())
        } else if (data.dpState == "21" && data.isown != "1") {
            ll_down_time.visibility = View.VISIBLE
            tv_down_time.textColor = ContextCompat.getColor(this, R.color.color_222)
            img_down_time.setImageResource(R.mipmap.icon_count_down_black)
            ll_down_time.setBackgroundResource(R.color.color_ffea00)
            downTimer(data.dpFinalEndTime - System.currentTimeMillis())
        } else if (data.dpState == "20" && data.isown != "1") {
            ll_down_time.visibility = View.VISIBLE
            img_down_time.visibility = View.GONE
            ll_down_time.setBackgroundResource(R.color.color_ffea00)
            tv_down_time.text = "${data.dpFinalStartTime} 开始支付尾款"
            tv_down_time.textColor = ContextCompat.getColor(this, R.color.color_222)
        } else {
            ll_down_time.visibility = View.GONE
            rl_assemble.visibility = View.GONE
            ll_assemble_buy.visibility = View.GONE
            btn_buy.visibility = View.VISIBLE
        }
        tv_buyer_count.text = "已购 " + data.buyers.toString()
//        tv_out_date.text = data.downTime
        tv_date_info.text = if (data.form == "2" || data.form == "4") data.validitytime else data.liveTime
        tv_num.text = "${data.period}课时"
        tv_lesson_title.text = data.title
        tv_price.text = if (!data.assembleKey.isNullOrEmpty() && "1" == data.assembleState) "${data.assemblePrice}元 拼团"
        else if (data.chargetype == "0" || data.isLimitFree == "1") "免费"
        else "¥" + data.disprice
        if ("3.1" == type && data.state == "1") {
            btn_buy.text = "已下架"
            btn_buy.setTextColor(ContextCompat.getColor(this, R.color.color_c4cbde))
            btn_buy.backgroundResource = R.drawable.shape_22_f5f5f9
        } else if (data.isAppointment == "1" && data.isAppointedNet == "1") {
            btn_buy.text = "已预约"
            tv_buyer_count.visibility = View.GONE
            btn_buy.textColor = ContextCompat.getColor(this, R.color.color_15d25f)
            btn_buy.backgroundResource = R.drawable.shape_22_1415d25f
        } else if (data.isAppointment == "1") {
            tv_buyer_count.visibility = View.GONE
            btn_buy.text = "免费预约"
            btn_buy.backgroundResource = R.drawable.shape_22_15d25f
        } else if (data.isown == "1" && data.iseffect != "1") {
            btn_buy.text = "去上课"
            btn_buy.backgroundResource = R.drawable.shape_22_008aff
        } else if (data.isSoldOut == "1") {
            btn_buy.text = "已售罄"
            btn_buy.setTextColor(ContextCompat.getColor(this, R.color.color_c4cbde))
            btn_buy.backgroundResource = R.drawable.shape_22_f5f5f9
        } else if (data.isLimitFree == "1" || data.chargetype == "0") {
            btn_buy.text = "免费领取"
        } else if (data.dpState == "11") {
            btn_buy.text = "付定金￥${data.dpPrice}"
            btn_buy.setTextColor(ContextCompat.getColor(this, R.color.color_222))
            btn_buy.backgroundResource = R.drawable.shape_22_ffea00
        } else if (data.dpState == "21") {
            btn_buy.text = "付尾款￥${data.dpFinalPrice}"
            btn_buy.setTextColor(ContextCompat.getColor(this, R.color.color_222))
            btn_buy.backgroundResource = R.drawable.shape_22_ffea00
        } else if (data.dpState == "20") {
            btn_buy.text = "已付定金"
            btn_buy.setTextColor(ContextCompat.getColor(this, R.color.color_ffc800))
            btn_buy.backgroundResource = R.drawable.shape_22_33ffea00
        }


        if (data.isLimitFree == "1") {
            rl_limit_info.visibility = View.VISIBLE
            tv_limit_date.text = "截止到" + data.limitEndTime.toString()
        }
        if (TextUtils.isEmpty(data.playbackSourceKey)) {
            free_drag.visibility = View.GONE
            free_lav_play?.cancelAnimation()
            free_lav_play?.progress = 0f

        } else {
            free_drag.visibility = View.VISIBLE
            free_lav_play?.playAnimation()
        }
    }

    private fun bindPager(data: LessonDetail) {
        val pagerDataList = mutableListOf<Fragment>()
        pagerDataList.add(LessonDetailFragment.newInstance(data.details ?: ""))
        pagerDataList.add(catalogFragment)
        pagerDataList.add(LessonTeacherFragment.newInstance(data.teacherList ?: ArrayList()))
        pagerDataList.add(commentFragment)
        pagerAdapter.fragmentList = pagerDataList
    }

    /**
     * 初始化网课封面图片 或 预览视频
     */
    private fun initLessonMedia() {
        detail?.let { detail ->
            if (detail.videourl.isEmpty()) {
                iv_cover.visibility = View.VISIBLE
                video_player.visibility = View.INVISIBLE
                Glide.with(this).load(detail.coverTopImg).into(iv_cover)//coverTopImg
            } else {
                iv_cover.visibility = View.GONE
                video_player.visibility = View.VISIBLE
                try {
//                if (null!= App.instance.replaceDNS){
//                    data.videourl = data.videourl.replace("https://$MEDIA_HOST","http://${App.instance.replaceDNS}")
//                }
                    initCoverVideo(detail.videourl, detail.coverTopImg)
                } catch (e: Exception) {
                }
            }
        }
    }

    /**
     * 初始化视频播放器
     */
    private fun initCoverVideo(url: String, img: String?) {
        img?.let {
            var imgView = ImageView(this)
            var lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
            )
            imgView.layoutParams = lp
            imgView.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(this).load(img).into(imgView)
            video_player.thumbImageView = imgView
        }
        video_player.setUp(url, false, "")
        video_player.setIsTouchWiget(true)
        video_player.fullscreenButton.setOnClickListener {
            video_player.startWindowFullscreen(this, false, false)
        }
        video_player.backButton.visibility = View.GONE
        video_player.startClick {
            detail?.let { saClick(it, "预览视频") }
            ll_down_time.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        video_player.onVideoResume()
        if (updateCatalog) {
            catalogFragment.initData()
            updateCatalog = false
        }
        try {
            SensorsDataAPI.sharedInstance().trackTimerStart("view_commodity_detail")
        } catch (e: Exception) {
        }
        if (!isLogin && SpUtil.isLogin) {
            getData()
        }
        if ((!checkLogin) && SpUtil.isLogin) {
            getLiveFlag()
        }
    }

    override fun onPause() {
        super.onPause()
        video_player.onVideoPause()
        detail?.let { saDetail(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        video_player.releaseOU()
        video_player.setVideoAllCallBack(null)
        lav_play?.cancelAnimation()
        lav_play?.progress = 0f
    }

    private fun initDialog() {
        val view = LayoutInflater.from(this@LessonDetailActivity).inflate(R.layout.dialog_reserve_lesson, null);
        val dialog = AlertDialog.Builder(this@LessonDetailActivity).setView(view).create()
        (view.findViewById<TextView>(R.id.tv_ok)).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun initAssembleDialog(content: String) {
        val view = LayoutInflater.from(this@LessonDetailActivity).inflate(R.layout.dialog_assemble, null)
        val tvAssembleContent = view.findViewById<TextView>(R.id.tv_assemble_content)
        tvAssembleContent.text = content
        val dialog = AlertDialog.Builder(this@LessonDetailActivity).setView(view).create()
        (view.findViewById<ImageView>(R.id.img_assemble_cancel)).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun downTimer(time: Long) {
        ll_down_time.visibility = View.VISIBLE
        cTimer?.cancel()
        cTimer = null
        detail?.let { detail ->
            when {
                time > 0 -> {
                    cTimer = object : CountDownTimer(time, 1000) {
                        override fun onFinish() {
                            startActivity<LessonDetailActivity>(KEY to key)
                            finish()
                        }

                        override fun onTick(millisUntilFinished: Long) {
                            when {
                                detail.dpState == "11" -> {
                                    initCu("距离付定金结束还有 " + formatTime(millisUntilFinished))
                                }
                                detail.dpState == "21" -> {
                                    initCu("距离付尾款结束还有 " + formatTime(millisUntilFinished))
                                }
                                detail.isAppointment == "1" -> {
                                    initCu("距离开售还剩 " + formatTime(millisUntilFinished))
                                }
                                "1" == detail.assembleState -> {
                                    initCu("距离拼团活动结束还有 " + formatTime(millisUntilFinished))
                                }
                                else -> {
                                    initCu("距离拼团活动开始还有 " + formatTime(millisUntilFinished))
                                }
                            }
                        }
                    }.start()
                }
                "0" == detail.assembleState -> {
                    //0:拼团开始倒计时；1：拼团结束倒计时；状态assembleState==0时，time<=0,状态assembleState=1
                    detail.assembleState = "1"
                    ll_assemble_buy.visibility = View.VISIBLE
                    btn_buy.visibility = View.GONE
                    downTimer(detail.assembleEndTime - System.currentTimeMillis())
                }
                else -> {
                    //assembleState=1，time<=0,变为正常网课状态
                    detail.assembleKey = ""
                    tv_price.text = if (detail.assembleKey.isNotEmpty() && "1" == detail.assembleState) {
                        "${detail.assemblePrice}元 拼团"
                    } else if (detail.chargetype == "0" || detail.isLimitFree == "1") {
                        "免费"
                    } else {
                        "¥" + detail.disprice
                    }
                    ll_down_time.visibility = View.GONE
                    rl_assemble.visibility = View.GONE
                    ll_assemble_buy.visibility = View.GONE
                    btn_buy.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun formatTime(time: Long): String {
        val allTime = ceil((time / 1000).toDouble())
        val day = floor(allTime / (60 * 60 * 24)).toInt()
        val h = floor((allTime - day * (60 * 60 * 24)) / (60 * 60)).toInt()
        val m = floor((allTime - day * (60 * 60 * 24) - h * (60 * 60)) / 60).toInt()
        val s = ((allTime - day * (60 * 60 * 24) - h * (60 * 60)) % 60).toInt()
        return "${toDouble(day)} 天 ${toDouble(h)} : ${toDouble(m)} : ${toDouble(s)}"
    }

    private fun toDouble(time: Int): String {
        if (time < 10) {
            return "0${time}"
        } else {
            return "$time"
        }
    }

    private fun initCu(txt: String) {
        val spanStr = SpannableString(txt)
        spanStr.setSpan(
            RadiusBackgroundSpan(this), txt.length - 2, txt.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spanStr.setSpan(
            RadiusBackgroundSpan(this), txt.length - 7, txt.length - 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spanStr.setSpan(
            RadiusBackgroundSpan(this), txt.length - 12, txt.length - 10, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spanStr.setSpan(
            RadiusBackgroundSpan(this), txt.length - 17, txt.length - 15, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        tv_down_time.text = spanStr
    }

    //神策收集网课详情
    private fun saDetail(detail: LessonDetail) {
        try {
            val property = setProperty(detail)
            SensorsDataAPI.sharedInstance().trackTimerEnd("view_commodity_detail", property)
        } catch (e: Exception) {
        }
    }

    private fun setProperty(detail: LessonDetail): JSONObject {
        val property = JSONObject()
        property.put("commodity_id", key)
        property.put("commodity_name", detail.title)
        property.put("is_live", "1" == detail.form)
        property.put("commodity_validity", detail.validitytime)
        property.put("original_price", detail.price ?: "0")
        property.put("current_price", detail.disprice ?: "0")
        property.put("teacher_name", detail.teacher)
        property.put("is_combination_course", detail.composeList != null && detail.composeList!!.size > 0)
        property.put("is_appointment_course", detail.isAppointment == "1")
        property.put("is_group_work", null != detail.assembleKey && detail.assembleKey.isNotEmpty())
        property.put("is_there_trial_course", "1" == detail.isThereTrialCourse)
        property.put("internal_name_online_course", detail.privateName)
        property.put("is_sign_in", SpUtil.isLogin)
        property.put("buy_it_or_not", "1" == detail.isown)
        property.put("commodity_type", "网课")
        return property
    }

    //网课操作
    fun saClick(detail: LessonDetail, operationName: String) {
        try {
            val property = setProperty(detail)
            property.put("operation_name", operationName)
            SensorsDataAPI.sharedInstance().track("operation_commodity_detail", property)
        } catch (e: Exception) {
        }
    }

    fun saClick(operationName: String) {
        detail?.let { saClick(it, operationName) }
    }

    //网课操作
    fun saClickShare(detail: LessonDetail, operationName: String) {
        val operation = when (operationName) {
            "WEIXIN_CIRCLE" -> {
                "朋友圈"
            }
            "WEIXIN" -> {
                "微信聊天"
            }
            "QQ" -> {
                "qq"
            }
            "QZONE" -> {
                "qq空间"
            }
            "SINA" -> {
                "微博"
            }
            else -> "其他"
        }
        try {
            val property = setProperty(detail)
            property.put("share_type", operation)
            SensorsDataAPI.sharedInstance().track("share_commodity", property)
        } catch (e: Exception) {
        }
    }

    private fun saCoupon(item: Coupon): JSONObject {
        val property = JSONObject()
        property.put("coupon_id", item.key)
        property.put("coupon_name", item.name)
        property.put("coupon_threshold", if (item.orderAmountLimit.isNullOrEmpty() || item.orderAmountLimit == "0") "不限制" else "${item.orderAmountLimit}")
        property.put("coupon_amount", item.facevalue)
        property.put("coupon_validity", "${item.startTime}-${item.endTime}")
        property.put("receiving_location", "网课详情")
        property.put("receiving_method", "手动领取")
        property.put("coupon_notes", item.remarks)
        return property
    }

    private fun saSensors(item: Coupon) {
        try {
            val property = saCoupon(item)
            SensorsDataAPI.sharedInstance().track("collect_selected_coupons", property)
        } catch (e: Exception) {
        }
    }

}

class Adapter(val onItemClick: (item: Compose) -> Unit) : BaseAdapter<Compose>() {
    var key: String = ""
    override fun convert(holder: ViewHolder, item: Compose) {
        val tvLessonName = holder.getView(R.id.tv_lesson_name) as TextView
        tvLessonName.text = item.title
        tvLessonName.isSelected = item.key == key
        tvLessonName.typeface = if (tvLessonName.isSelected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        val layoutParams = tvLessonName.layoutParams as RecyclerView.LayoutParams
        if (holder.layoutPosition == 0) {
            layoutParams.leftMargin = DeviceUtil.dp2px(holder.itemView.context, 20f).toInt()
        } else if (holder.layoutPosition == getDateCount() - 1) {
            layoutParams.rightMargin = DeviceUtil.dp2px(holder.itemView.context, 20f).toInt()
        }
        holder.itemView.setOnClickListener {
            if (item.key != key) {
                onItemClick(item)
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = LayoutInflater.from(context).inflate(R.layout.layout_detail_lessson_item, parent, false)
}
