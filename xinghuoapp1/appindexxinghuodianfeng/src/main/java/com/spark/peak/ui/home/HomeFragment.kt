package com.spark.peak.ui.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.LifeFragment
import com.spark.peak.bean.*
import com.spark.peak.ui._public.NetErrorFragment
import com.spark.peak.ui.cg.CGPassActivity
import com.spark.peak.ui.cg.CGPracticeActivity
import com.spark.peak.ui.common.grade.GradeActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.home.adapter.NetclassAdapter
import com.spark.peak.ui.home.adapter.NewsAdapter
import com.spark.peak.ui.home.broadcast.BroadcastDetailActivity
import com.spark.peak.ui.lesson.LessonsActivity
import com.spark.peak.ui.message.MessageNoticeActivity
import com.spark.peak.ui.netLessons.NetLessonsActivity
import com.spark.peak.ui.scan.ScannerActivity
import com.spark.peak.ui.wrongbook.WrongBookActivity
import com.spark.peak.utlis.*
import com.spark.peak.utlis.FileUtils.Companion.installAPK
import com.spark.peak.widegt.FlyBanner.CENTER
import com.zxing.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast


const val RECEIVER_FLAG = "receiver_flag" //切换学段时，需要刷新首页数据

class HomeFragment : LifeFragment<HomePresenter>() {

    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_home

    var subjectList = ArrayList<Subject>()
    var broadcastKey: String = ""
    var flag = true //true 随堂练习  false 练一练

    var missionKey = ""
    var missionSize = 0

    companion object {
        var apkFilePath = "" //TODO 8.0以上安装未知来源应用需要申请的权限
        val REQUEST_APK_INSTALL = 9898
    }

    override fun configView(view: View?) {
        banner_home.setPoinstPosition(CENTER)
        initNetError()
    }

    override fun initData() {
        if (NetWorkUtils.isNetWorkReachable()) {
            getNewVersion()
            if (SpUtil.userInfo.grade == null && SpUtil.defaultSection.catalogKey == "none") { //判断当前没有登录，且本地无默认年级信息时，请求学段-年级列表
                initDefaultData()
            } else if (SpUtil.isLogin && (SpUtil.userInfo.grade == null || SpUtil.userInfo.grade == "")) { //判断若当前登录用户没有学段 年级信息时，把本地默认的学段 年级赋给该用户
                presenter.updUserSG(SpUtil.defaultSection.catalogKey, SpUtil.defaultGrade.id) {
                    initHomeData()
                }
            } else {
                initHomeData()
            }
            initReceiver()
        } else {
            showNetError()
        }
    }

    val bookUrl = "https://xinghuots.tmall.com/category-1231159515.htm?spm=a1z10.3-b-s.w4011-16591451289.13.bf391ad0yDbs6e&tsearch=y&user_number_id=2759175296&rn=f8ca249c7dae1362b100fa8eabcc17ac&keyword=%D0%C7%BB%F0%D3%A2%D3%EF#TmshopSrchNav"
    override fun handleEvent() {
        tv_grad.setOnClickListener {
            startActivity<GradeActivity>()
        }
        ic_scan.setOnClickListener {
            PermissionUtlis.checkPermissions(this, Manifest.permission.CAMERA) {
                IntentIntegrator(activity)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScannerActivity::class.java)
                        .setPrompt("将二维码放入框内，即可自动扫描")
                        .initiateScan()
            }
        }

        search_bar.setOnClickListener {
            startActivity<LessonsActivity>()
        }

        tv_more_class.setOnClickListener {
            startActivity<LessonsActivity>()
        }

        home_model1.setOnClickListener {
            //            startActivity<SubjectsActivity>( SubjectsActivity.SUB_LIST to subjectList)
            when (missionSize) {
                0 -> mToast("该学段暂无闯关")
                1 -> startActivity<CGPassActivity>(CGPassActivity.KEY to missionKey,
                        CGPassActivity.TITLE to "")
                else -> startActivity<CGPracticeActivity>(CGPracticeActivity.KEY to "")
            }// -: 2018/7/6 14:02 霍述雷

        }

        home_model2.setOnClickListener {
            checkLogin {
                startActivity<WrongBookActivity>(WrongBookActivity.SUBJECTS to subjectList)
            }
        }

        ic_message.setOnClickListener {
            checkLogin {
                startActivity<MessageNoticeActivity>()
            }
        }
        tv_more_news.setOnClickListener {
            startActivity<PostActivity>(PostActivity.TITLE to "学习资讯", PostActivity.URL to WEB_BASE_URL + "information/infoList")
        }

        tv_broadcast.setOnClickListener {
            if (broadcastKey != "") {
                startActivity<BroadcastDetailActivity>(BroadcastDetailActivity.KEY to broadcastKey)
            }
        }

//        tv_more_books.setOnClickListener {
//            startActivity<WebViewActivity>(WebViewActivity.TITLE to "星火书城", WebViewActivity.URL to bookUrl)
//        }

        /**
         * 首页数据下拉刷新
         */
        srl_home.setOnRefreshListener {
            presenter.homePageInfo({
                srl_home.isRefreshing = false
            }) {
                srl_home.isRefreshing = false
                refreshHomeData(it)
            }
        }
    }

    private fun refreshHomeData(homeInfo: HomeInfo) {
        initBanner(homeInfo.sybnList ?: ArrayList())
        initNetClass(homeInfo.netList ?: ArrayList())
//        initBooks(homeInfo.tsList ?: ArrayList())
        initAnnouncement(homeInfo.syggList ?: ArrayList())
        initEduInfo(homeInfo.xxzxList ?: ArrayList())
    }

    /**
     * 初始化默认值
     * 未登录状态下 默认的 学段主键、年级主键、年级名
     */
    private fun initDefaultData() {
//        presenter.getSectionAndGradeList {
//            if (it.isNotEmpty()){
//                var section0 = it[0]
//                SpUtil.defaultSection = section0
//                if (section0.resourceList.isNotEmpty()){
//                    SpUtil.defaultGrade = section0.resourceList[0]
//                }
//                initHomeData()
//            }
//        }
        startActivity<GradeActivity>()
    }

    /**
     * 获取首页信息
     */
    private fun initHomeData() {
        tv_grad.text = GradUtil.parseGradStr(if (SpUtil.isLogin) SpUtil.userInfo.sectionname
                ?: "" else SpUtil.defaultSection.catalogName,
                if (SpUtil.isLogin) SpUtil.userInfo.gradename ?: "" else SpUtil.defaultGrade.name)
        presenter.getBanner("sybn", 1, 4) {
            initBanner(it)
        }

        presenter.getAnnouncement {
            initAnnouncement(it)
        }

//        presenter.getBanner("tsjx", 1, 6) {
//            initBooks(it)
//        }

        presenter.getNetClassList {
            initNetClass(it)
        }

        presenter.getEduInfos(1, 3) {
            initEduInfo(it)
        }
//
        presenter.getSubjects(SpUtil.userInfo.grade ?: SpUtil.defaultGrade.id) {
            missionKey = it.missionKey
            missionSize = it.missionSize

            initSubject(it.list ?: ArrayList<Subject>())
        }
    }

    /**
     * banner数据
     */
    private fun initBanner(list: List<Advert>) {
        var dataList = list //1
        banner_home.setImagesUrl(list)
        banner_home.setOnItemClickListener {
            var item = dataList[it]
            if (item.type == "link") {
                if (item.link != "") {
                    startActivity<PostActivity>(PostActivity.URL to item.link, PostActivity.TITLE to item.title)
                }
            } else if (item.type == "goods") {
                if (item.goodtype == "net" && item.goodkey != "") {
                    startActivity<NetLessonsActivity>(NetLessonsActivity.KEY to item.goodkey)
                } else if (item.goodtype == "book") {
                    //跳转点读
                }
            }

            presenter.addAdPv(item.key) {

            }
        }
    }

    /**
     * 公告数据
     */
    private fun initAnnouncement(dataList: List<Announcement>) {
        if (dataList.isNotEmpty()) { //2
            tv_broadcast.visibility = View.VISIBLE
            dataList[0].title?.let {
                tv_broadcast.text = it
            }
            dataList[0].key?.let {
                broadcastKey = it
            }
        } else {
            tv_broadcast.visibility = View.GONE
        }
    }

    /**
     * 图书精选
     */
//    private fun initBooks(dataList: List<Advert>) {
//        if (dataList.isNotEmpty()) { //3
//            var booksAdapter = BooksAdapter {
//                if (it.type == "link") {
//                    if (it.link != "") {
//                        startActivity<WebViewActivity>(WebViewActivity.URL to it.link, WebViewActivity.TITLE to it.title)
//                    }
//                } else if (it.type == "goods") {
//                    if (it.goodtype == "net") {
//                        startActivity<NetLessonsActivity>(NetLessonsActivity.KEY to it.goodkey)
//                    } else if (it.goodtype == "book") {
//                        //跳转点读
//                    }
//                }
//
//                presenter.addAdPv(it.key) {
//
//                }
//            }
//            booksAdapter.setData(dataList)
//            rv_books.layoutManager = GridLayoutManager(activity, if (dataList.size < 6) dataList.size else 6)
//            rv_books.adapter = booksAdapter
//        }
//    }

    /**
     * 网课精选
     */
    private fun initNetClass(dataList: List<NetClass>) {
        if (dataList.isNotEmpty()) { //4
            var netclassAdapter = NetclassAdapter {
                startActivity<NetLessonsActivity>(NetLessonsActivity.KEY to it)
            }
            netclassAdapter.setData(dataList)
            rv_netclass.layoutManager = GridLayoutManager(activity, if (dataList.size < 6) dataList.size else 6)
            rv_netclass.adapter = netclassAdapter
        }
    }

    /**
     * 教育咨询
     */
    private fun initEduInfo(list: List<EduInfo>) {
        var newsAdapter = NewsAdapter {
            //5
            var url = if (it.cententtype == "1") {
                WEB_BASE_URL + "information/infoDetails?key=" + it.key
            } else {
                it.content ?: ""
            }
            it.key?.let {
                presenter.informationPv(it)
            }
            startActivity<PostActivity>(PostActivity.TITLE to it.title, PostActivity.URL to url)
        }
        newsAdapter.setData(list)
        rv_news.layoutManager = LinearLayoutManager(activity)
        rv_news.adapter = newsAdapter
    }

    /**
     * 學科列表
     */
    private fun initSubject(dataList: List<Subject>) {
        subjectList.clear() //6
        subjectList.addAll(dataList)

        home_menu.setData(subjectList) {
            goNetClassList(it)
        }
    }


    private fun goNetClassList(subject: Subject) {
//        startActivity<LessonsActivity>(LessonsActivity.KEY to subject.key, LessonsActivity.NAME to subject.name)
    }

    //初始化刷新首页数据的receiver
    private fun initReceiver() {
        var mReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                initHomeData()
            }
        }
        var iF = IntentFilter()
        iF.addAction(RECEIVER_FLAG)
        this.requireContext().registerReceiver(mReceiver, iF)
    }

    /**
     * 获取新版本
     */
    var isForce = ""

    private fun getNewVersion() {
        presenter.getNewVersion {
            if (it.isForce == "0" || it.isForce == "1") {
                isForce = it.isForce
                AlertDialog.Builder(this.requireContext()).setCancelable(false).setTitle("发现新版本" + it.v).setMessage(it.msg).setPositiveButton("下载更新") { _, _ ->
                    it.url.let {
                        downloadApk(it)
                    }
                }.setNegativeButton("不更新") { dialog, _ ->
                    if (it.isForce == "1") {
                        this.requireActivity().finish()
                    } else {
                        dialog.dismiss()
                    }
                }.create().show()
            }
        }
    }

    val downloadDialog by lazy { ProgressDialog(this.requireContext()) }
    private fun downloadApk(url: String) {
        if (url.isNotEmpty()) {
            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, onCancel = {
                this.requireActivity().finish()
            }) {
                showDownloadDialog()
                FileDownloader.setup(this.requireContext())
                FileDownloader.getImpl().create(url)./*setForceReDownload(true).*/setPath(DownloadManager.getFilePath(url)).setListener(downloadListener).start()
            }
        } else {
            toast("下载url异常")
        }
    }

    private fun showDownloadDialog() {
        with(downloadDialog) {
            setTitle("正在下载")
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            setCancelable(false)
            show()
        }

    }

    private val downloadListener by lazy {
        object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask?) {
                toast("出错啦~请稍后重试")
                downloadDialog.dismiss()
            }

            override fun completed(task: BaseDownloadTask?) {
                try {
                    downloadDialog.dismiss()
                    apkFilePath = task?.path ?: ""
                    checkIsAndroidO {
                        installAPK(this@HomeFragment.requireContext(), task?.path ?: "")
                    }
                } catch (e: Exception) {
                    e.message
                }
            }

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                toast("apk下载失败，请重试")
                downloadDialog.dismiss()
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                downloadDialog.max = totalBytes
                downloadDialog.progress = soFarBytes
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

            }

        }
    }

    /**
     * 判断是否是8.0,8.0需要处理未知应用来源权限问题,否则直接安装
     */
//    @RequiresApi(api = Build.VERSION_CODES.O)
    fun checkIsAndroidO(onNext: () -> Unit) {
        if (Build.VERSION.SDK_INT >= 26) {
            var b = this.requireContext().packageManager.canRequestPackageInstalls()
            if (b) {
                onNext()
            } else {
                //请求安装未知应用来源的权限
//                PermissionUtlis.checkPermissions(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) {
//                    onNext()
//                }
                var packageURI = Uri.parse("package:" + this.requireContext().packageName)
                var intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
                startActivityForResult(intent, REQUEST_APK_INSTALL)
            }
        } else {
            onNext()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HomeFragment.REQUEST_APK_INSTALL && resultCode == Activity.RESULT_OK && HomeFragment.apkFilePath.isNotEmpty()) {
            checkIsAndroidO {
                FileUtils.installAPK(this.requireContext(), apkFilePath)
            }
        } else if (requestCode == HomeFragment.REQUEST_APK_INSTALL && resultCode == Activity.RESULT_CANCELED) {
            if (isForce == "1") {
                AlertDialog.Builder(this.requireContext()).setMessage("您拒绝了权限，无法安装新版本").setCancelable(false).setPositiveButton("关闭应用") { _, _ ->
                    this.requireActivity().finish()
                }.create().show()
            }
        }
    }

    private val netErrorFragment by lazy { NetErrorFragment() }
    private fun initNetError() {
        netErrorFragment.listener = object : NetErrorFragment.Listener {
            override fun reTry() {
                if (NetWorkUtils.isNetWorkReachable()) {
                    fl_netError.visibility = View.GONE
                    initData()
                }
            }
        }
        val transaction = fragmentManager?.beginTransaction()
        transaction?.add(R.id.fl_netError, netErrorFragment)
        transaction?.show(netErrorFragment)?.commit()
        fl_netError.setOnClickListener {
            //do nothing...
            // 防止点击事件向下传递
        }
    }

    override fun onResume() {
        super.onResume()
        upmsg()
    }

    fun upmsg() {
        try {
            iv_msg_red_dot?.visibility = View.GONE
            if (!SpUtil.isLogin) return
            presenter.ifNewFB {
                if (it == 1)
                    iv_msg_red_dot?.visibility = View.VISIBLE
            }
            presenter.ifNewNotice {
                if (it == 1)
                    iv_msg_red_dot?.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
        }
    }

    fun showNetError() {
        runOnUiThread {
            fl_netError.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.detach(netErrorFragment)
    }
}
