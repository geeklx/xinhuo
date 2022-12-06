//package tuoyan.com.xinghuo_daying.ui.netLesson.overPackage
//
//import android.view.View
//import android.widget.ImageView
//import android.widget.RelativeLayout
//import androidx.fragment.app.Fragment
//import com.shuyu.gsyvideoplayer.GSYVideoManager
//import kotlinx.android.synthetic.main.activity_over_package.*
//import org.jetbrains.anko.ctx
//import tuoyan.com.xinghuo_dayingindex.*
//import tuoyan.com.xinghuo_dayingindex.base.BaseV4FmPagerAdapter
//import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
//import tuoyan.com.xinghuo_dayingindex.bean.LessonDetail
//import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
//import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.NetLessonsPresenter
//import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
//import kotlin.properties.Delegates
//
//
///**
// * 创建者：
// * 时间：
// */
//class OverPackageActivity(override val layoutResId: Int = R.layout.activity_over_package)
//    : LifeActivity<NetLessonsPresenter>() {
//    override val presenter = NetLessonsPresenter(this)
//    private val key by lazy { intent.getStringExtra(KEY) }
//
//    //    private val key = ""
//    private var lessonDetail: LessonDetail by Delegates.notNull<LessonDetail>()
//    private val adapter by lazy { BaseV4FmPagerAdapter(supportFragmentManager) }
//    private var lesson: Map<String, String>? = null
//    private val dialog by lazy {
//        ShareDialog(ctx) {
//            ShareUtils.share(
//                this, it, lesson?.get("title") ?: "", lesson?.get("content") ?: "",
//                "${WEB_BASE_URL}network/network?key=$key", lesson?.get("coverimg")
//            )
//        }
//    }
//
//    override fun configView() {
//        setSupportActionBar(toolbar)
//    }
//
//    override fun handleEvent() {
//
//    }
//
//    override fun initData() {
//        presenter.getMyLessonDetail(key){
//            lessonDetail = it
//            bindPager(it)
//            tv_title.text = it.title
//            tv_validity_period.text = it.endTime
//            if (it.resourceKey.isNotEmpty()){
//                presenter.getResourceInfo(it.resourceKey,"1.1"){
//                    lessonDetail.videourl = it.url
//                    initLessonMedia()
//                }
//            }else{
//                initLessonMedia()
//            }
//        }
//    }
//
//    private fun bindPager(detail: LessonDetail){
//        var fList = mutableListOf<Fragment>()
//        fList.add(NetLessonFragment.newInstance(detail))
//        fList.add(EvaluationFragment.newInstance(detail.evalList?:ArrayList()))
//
//        adapter.fragmentList = fList
//        adapter.titles = arrayOf("网课", "测评")
//
//        view_pager.adapter = adapter
//        tab_layout.setupWithViewPager(view_pager)
//    }
//
//    /**
//     * 初始化网课封面图片 或 预览视频
//     */
//    private fun initLessonMedia(){
//        if (lessonDetail.videourl.isEmpty()){
//            iv_cover.visibility = View.VISIBLE
//            video_player.visibility = View.GONE
//            Glide.with(ctx).load(lessonDetail.coverimg).placeholder(R.drawable.default_lesson).into(iv_cover)
//        }else{
//            iv_cover.visibility = View.GONE
//            video_player.visibility = View.VISIBLE
//            try {
////                    if (null!= App.instance.replaceDNS){
////                        it.videourl = it.videourl.replace("https://$MEDIA_HOST","http://${App.instance.replaceDNS}")
////                    }
//                initCoverVideo(lessonDetail.videourl, lessonDetail.coverimg)
//            } catch (e: Exception) {
//            }
//        }
//    }
//
//    /**
//     * 初始化视频播放器
//     */
//    private fun initCoverVideo(url: String, img: String?){
//        img?.let {
//            var imgView = ImageView(ctx)
//            var lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
//            imgView.layoutParams = lp
//            Glide.with(ctx).load(img).into(imgView)
//            video_player.thumbImageView = imgView
//        }
//        video_player.setUp(url,false,"")
//        video_player.setIsTouchWiget(true)
//        video_player.fullscreenButton.setOnClickListener {
//            video_player.startWindowFullscreen(this,false,false)
//        }
//        video_player.backButton.visibility = View.GONE
//    }
//
//    fun buy(v: View) {
//        // : 2018/5/15 10:45  购买
////        if (!v.isEnabled) return
////        if (lesson?.isown == 0 || lesson?.iseffect == "1") {
////            if (lesson?.chargetype == 0) {
////                presenter.freeBuyNetCourse(key) {
////                    mToast("领取成功")
////                    initData()
////                }
////
////            } else
////                startActivity<ConfirmOrderActivity>(ConfirmOrderActivity.KEY to key)
////            return
////        }
////        if (lesson?.isown == 1) {
////            startActivity<LessonsActivity>(LessonsActivity.KEY to key)
////        }
////        if (lesson?.iscomment == 0) {
////            startActivity<OrderCommentActivity>(OrderCommentActivity.KEY to key)
////            return
////        }
//    }
//
//    fun service(v: View) {
////        startActivity<FeedbackActivity>()
//        // : 2018/5/15 10:46  客服
////        val intent = Intent(ACTION_DIAL, Uri.parse("tel:$servicePhone"))
////        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
////        startActivity(intent)
//    }
//
//    fun download(v: View) {
//        // : 2018/9/25 11:47  下载
//    }
//
//    var refreshFlag = false
//    override fun onResume() {
//        super.onResume()
//        video_player.onVideoResume()
//        if (refreshFlag){
//            presenter.getMyLessonDetail(key){
//                var f = adapter.fragmentList[1]
//                if (f is EvaluationFragment&& it.evalList!= null){
//                    f.refreshData(it.evalList!!)
//                }
//            }
//        }
//        refreshFlag = true
//    }
//    override fun onPause() {
//        super.onPause()
//        video_player.onVideoPause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        GSYVideoManager.releaseAllVideos()
//        video_player.releaseOU()
//        video_player.setVideoAllCallBack(null)
//    }
//
//    companion object {
//        const val KEY = "key"
//    }
//}