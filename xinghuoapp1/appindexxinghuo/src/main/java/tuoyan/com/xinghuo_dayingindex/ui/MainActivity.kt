package tuoyan.com.xinghuo_dayingindex.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.analytics.android.sdk.SensorsDataIgnoreTrackAppViewScreen
import com.tencent.sonic.sdk.SonicEngine
import com.umeng.commonsdk.utils.UMUtils
import com.umeng.message.PushAgent
import com.zxing.google.zxing.integration.android.IntentIntegrator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_book_grade.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.*
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui._public.ImageActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.ScanResListActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.detail.BookDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing.ExerciseParsingActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.HomePresenter
import tuoyan.com.xinghuo_dayingindex.ui.home.word.BackWordActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.word.words.ScanWordsActivity
import tuoyan.com.xinghuo_dayingindex.ui.main.BookStoreFragment
import tuoyan.com.xinghuo_dayingindex.ui.main.BookStoreGradeAdapter
import tuoyan.com.xinghuo_dayingindex.ui.main.HomeFragment
import tuoyan.com.xinghuo_dayingindex.ui.main.StudyFragment
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.MineFragment
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.EvalCardActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.evaluation.EntryActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.composition.CompositionDetailWebActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.real.RealActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils.Companion.installAPK
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import java.io.File
import java.util.concurrent.TimeUnit

@SensorsDataIgnoreTrackAppViewScreen
class MainActivity : LifeActivity<HomePresenter>() {
    override val presenter = HomePresenter(this)
    override val layoutResId = R.layout.activity_main
    private val homeFragment by lazy { HomeFragment() }
    private val studyFragment by lazy { StudyFragment() }
    private val bookFragment by lazy { BookStoreFragment() }
    private val mineFragment by lazy { MineFragment() }
    private var currentIndex = 0
    private var gradeBean: Level = Level()
    private var contentBean: Level = Level()
    private val gradeAdapter by lazy {
        BookStoreGradeAdapter() { level ->
            gradeBean = level
            contentBean = Level()
            if (level.isAdd) {
                presenter.getLevelByGrade(level.key) { list ->
                    if (list.isNotEmpty()) {
                        tv_content.visibility = View.VISIBLE
                        rlv_content.visibility = View.VISIBLE
                        contentAdapter.setData(list)
                    } else {
                        tv_content.visibility = View.GONE
                        rlv_content.visibility = View.GONE
                    }
                }
            } else {
                tv_content.visibility = View.GONE
                rlv_content.visibility = View.GONE
            }
        }
    }
    private val pos by lazy { intent.getStringExtra(POS) ?: "0" }//推送图书商城 其他地方用不到

    private val contentAdapter by lazy {
        BookStoreGradeAdapter() {
            contentBean = it
        }
    }

    val screenHeight by lazy {
        val dm = resources.displayMetrics
        dm.heightPixels
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //强制不调用，防止崩溃后 首页数据显示异常
//        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        fullScreen = true
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        if (SpUtil.agreement) {
            PushAgent.getInstance(this).onAppStart()
        }
    }

    override fun configView() {
        initFirstFragment()
        rg_home.setOnCheckedChangeListener { view, index ->
            val transaction = supportFragmentManager.beginTransaction()
            when (index) {
                R.id.rb_home -> {
                    ImmersionBar.with(this).statusBarDarkFont(true).init()
                    transaction.hide(currentFragments()).show(homeFragment).commit()
                    currentIndex = 0
                }
                R.id.rb_study -> {
                    ImmersionBar.with(this).statusBarDarkFont(true).init()
                    if (!studyFragment.isAdded) transaction.add(R.id.fl_home, studyFragment)
                    transaction.hide(currentFragments()).show(studyFragment).commit()
                    currentIndex = 1
                }
                R.id.rb_community -> {
                    if (currentIndex != 2) {
                        ImmersionBar.with(this).statusBarDarkFont(true).init()
                        if (!bookFragment.isAdded) transaction.add(R.id.fl_home, bookFragment)
                        transaction.hide(currentFragments()).show(bookFragment).commit()
                        currentIndex = 2
                    }

                }
                R.id.rb_mine -> {
                    ImmersionBar.with(this).statusBarDarkFont(true).init()
                    if (!mineFragment.isAdded) transaction.add(R.id.fl_home, mineFragment)
                    transaction.hide(currentFragments()).show(mineFragment).commit()
                    currentIndex = 3
                    rb_mine.isSelected = false
                }
            }
        }
        fl_home.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            fl_home.getWindowVisibleDisplayFrame(r)
            val deleteHeight = screenHeight - r.bottom

            if (deleteHeight > 150) {
                //屏幕被压缩的高度 大于150 则判定为键盘弹出
                rg_home.visibility = View.GONE
            } else {
                rg_home.visibility = View.VISIBLE
            }
        }
        rlv_grade.layoutManager = GridLayoutManager(this, 3)
        rlv_grade.adapter = gradeAdapter
        rlv_content.layoutManager = GridLayoutManager(this, 3)
        rlv_content.adapter = contentAdapter
    }

    override fun initData() {
        super.initData()
        initNetStateListener()
        getNewVersion()

        val id = PushAgent.getInstance(this).registrationId
        if (id.isNotEmpty()) {
//            MyApp.instance.equipmentId = id
            SPUtils.getInstance().put("equipmentId",id)
            presenter.userEquipmentId(id) {
                SpUtil.isFreeLogin = it["isFreeLogin"] ?: ""
            }
            try {
                val properties = JSONObject()
                properties.put("jiguang_id", id)
                SensorsDataAPI.sharedInstance().profileSet(properties)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        gradeAdapter.setData(initLevel())
        saTrackAppInstall()
        if ("3" == pos) {
            rb_community.postDelayed({
                runOnUiThread {
                    rb_community.performClick()
                }
            }, 500)
        }
    }

    fun saBanner(item: Advert, type: String) {
        try {
            val property = JSONObject()
            property.put("advertisement_id", item.key)
            property.put("advertisement_name", item.title)
            property.put("location_of_advertisement", "弹窗广告")
            property.put("advertising_sequence", "1号")
            property.put("types_of_advertisement", type)
            SensorsDataAPI.sharedInstance().track("click_advertisement", property)
        } catch (e: Exception) {
        }
    }

    //是否有新的优惠券展示提醒
    private fun getPromotionalFlag() {
        presenter.getPromotionalFlag() {
            rb_mine.isSelected = "1" == it["newPromotional"]
            if (mineFragment.isAdded) {
                mineFragment.updated(rb_mine.isSelected)
            }
        }
    }

    fun initLevel(): ArrayList<Level> {
        val beans = ArrayList<Level>()
        beans.add(Level(name = "四级", key = GRAD_KEY_CET4))
        beans.add(Level(name = "六级", key = GRAD_KEY_CET6))
        beans.add(Level(name = "考研", key = GRAD_KEY_YAN))
        beans.add(Level(name = "专四", key = GRAD_KEY_TEM4))
        beans.add(Level(name = "专八", key = GRAD_KEY_TEM8))
        return beans
    }

    fun showBookStoreGrade() {
        layout_book_grade.visibility = View.VISIBLE
    }

    val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
//            bookFragment.sendWebToken()
//            toast("收到消息 刷新购物车")
        }
    }

    private fun initFirstFragment() {
        supportFragmentManager.beginTransaction().add(R.id.fl_home, homeFragment).show(homeFragment)
            .commit()
        val intentFilter = IntentFilter()
        intentFilter.addAction(SEND_WEB_TOKEN)
        registerReceiver(refreshReceiver, intentFilter)
    }

    private fun currentFragments(): BaseV4Fragment {
        return when (currentIndex) {
            0 -> homeFragment
            1 -> studyFragment
            2 -> bookFragment
            3 -> mineFragment
            else -> homeFragment
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        downloadDialog.setOnDismissListener {
            ImmersionBar.with(this@MainActivity).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init()
        }
        tv_reset.setOnClickListener {
            gradeAdapter.setData(initLevel())
            tv_content.visibility = View.GONE
            rlv_content.visibility = View.GONE
            gradeBean = Level()
            contentBean = Level()
            bookFragment.selByGrade(gradeBean, contentBean)
        }
        tv_ok.setOnClickListener {
            bookFragment.selByGrade(gradeBean, contentBean)
            layout_book_grade.visibility = View.GONE
        }
        layout_book_grade.setOnClickListener {}
    }

    private var isDropOut = true
    override fun onBackPressed() {
        if (layout_book_grade.visibility == View.VISIBLE) {
            layout_book_grade.visibility = View.GONE
            return
        }
        if (isDropOut) {
            isDropOut = false
            mToast("再按一次退出程序")
            Observable.interval(0, 1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .take(3).doOnComplete {
                    isDropOut = true
                }.subscribe()
            return
        }
        super.onBackPressed()
    }


    /**
     * 获取新版本
     */
    var isForce = ""
    var version: NewVersion? = null
    var apkFilePath = "" //TODO 8.0以上安装未知来源应用需要申请的权限
    val REQUEST_APK_INSTALL = 9898

    private fun getNewVersion() {
        //1强制,2不提示,3仅提示一次,0每次都提示
        presenter.getNewVersion(BuildConfig.FLAVOR) { version ->
            this.version = version
            // ceshi
//            version.isForce = "1"
            if (version.isForce == "0" || version.isForce == "1") {
                isForce = version.isForce
                showUpdateDialog(version)
            } else if ("3" == version.isForce && SpUtil.updateVersion != version.v) {
                SpUtil.updateVersion = version.v
                showUpdateDialog(version)
            } else {
                FileUtils.delete(DownloadManager.getExternalFilePath(version.url))
            }
        }
    }

    private fun showUpdateDialog(version: NewVersion) {
        AlertDialog.Builder(this).setCancelable(false).setTitle("发现新版本" + version.v)
            .setMessage(version.msg).setPositiveButton("立即更新") { _, _ ->
                version.url.let { url ->
                    downloadApk(url)
                }
            }.setNegativeButton(if (version.isForce == "1") "暂不更新" else "暂不更新") { dialog, _ ->
                if (version.isForce == "1") {
//                    this.finish()
                    // 此处不退出 后台静默下载即可
                    dialog.dismiss()
                    version.url.let { url ->
                        downloadApk_local(url)
                    }
                } else {
                    dialog.dismiss()
                }
            }.setOnDismissListener {
                ImmersionBar.with(this@MainActivity).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                    .init()
            }.create().show()
    }

    val downloadDialog by lazy { ProgressDialog(this@MainActivity) }
    private fun downloadApk(url: String) {
        if (url.isNotEmpty()) {
            if (FileUtils.Companion.createOrExistsFile2(DownloadManager.getExternalFilePath(url))){
//                showDownloadDialog()
                checkIsAndroidO {
                    installAPK(this@MainActivity, DownloadManager.getExternalFilePath(url) , isForce)
                }
                return
            }
            showDownloadDialog()
            FileDownloader.setup(this@MainActivity)
            FileDownloader.getImpl().create(url).setPath(DownloadManager.getExternalFilePath(url))
                .setListener(downloadListener).start()
        } else {
            toast("下载url异常")
        }
    }
    private fun downloadApk_local(url: String) {
        if (url.isNotEmpty()) {
            FileDownloader.setup(this@MainActivity)
            FileDownloader.getImpl().create(url).setPath(DownloadManager.getExternalFilePath(url))
                .setListener(downloadListener_local).start()
        } else {
            toast("下载url异常")
        }
    }

    private fun showDownloadDialog() {
        with(downloadDialog) {
            setTitle("正在下载")
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            setProgressNumberFormat("%1d kb/%2d kb")
            setCancelable(false)
            show()
        }

    }

    private val downloadListener by lazy {
        object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask?) {
                toast("出错啦~请稍后重试")
                task?.path?.let {
                    FileUtils.delete(it)
                }
                downloadDialog.dismiss()
            }

            override fun completed(task: BaseDownloadTask?) {
                try {
                    downloadDialog.dismiss()
                    apkFilePath = task?.path ?: ""
                    checkIsAndroidO {
                        installAPK(this@MainActivity, task?.path ?: "", isForce)
                    }
                } catch (e: Exception) {
                    e.message
                }
            }

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                toast("apk下载失败，请重试")
                task?.path?.let {
                    FileUtils.delete(it)
                }
                downloadDialog.dismiss()
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                downloadDialog.max = totalBytes / 1024
                downloadDialog.progress = soFarBytes / 1024
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

            }

        }
    }

    private val downloadListener_local by lazy {
        object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask?) {
//                toast("出错啦~请稍后重试")
                task?.path?.let {
                    FileUtils.delete(it)
                }
            }

            override fun completed(task: BaseDownloadTask?) {
                try {
                    apkFilePath = task?.path ?: ""

                } catch (e: Exception) {
                    e.message
                }
            }

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
//                toast("apk下载失败，请重试")
                task?.path?.let {
                    FileUtils.delete(it)
                }
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

            }

        }
    }

    /**
     * 判断是否是8.0,8.0需要处理未知应用来源权限问题,否则直接安装
     */
    fun checkIsAndroidO(onNext: () -> Unit) {
        if (Build.VERSION.SDK_INT >= 26) {
            val b = this.packageManager.canRequestPackageInstalls()
            if (b) {
                onNext()
            } else {
                val packageURI = Uri.parse("package:" + this.packageName)
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
                startActivityForResult(intent, REQUEST_APK_INSTALL)
            }
        } else {
            onNext()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            //选择学段
            homeFragment.onActivityResult(requestCode, resultCode, data)
        } else if (requestCode == REQUEST_APK_INSTALL && resultCode == Activity.RESULT_OK && apkFilePath.isNotEmpty()) {
            checkIsAndroidO {
                installAPK(this, apkFilePath, isForce)
            }
        } else if (requestCode == REQUEST_APK_INSTALL && resultCode == Activity.RESULT_CANCELED) {
            if (isForce == "1") {
                AlertDialog.Builder(this).setMessage("您拒绝了权限，无法安装新版本").setCancelable(false)
                    .setPositiveButton("关闭应用") { _, _ ->
                        this.finish()
                    }.setOnDismissListener {
                        ImmersionBar.with(this@MainActivity)
                            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init()
                    }.create().show()
            }
        } else {
            data ?: return
            val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            intentResult?.let { result ->
                result.contents?.let { contents ->
                    if (contents.contains("weixin.qq.com")) {
                        toast("请使用微信扫描此二维码")
                        return
                    }
                    goDetail(contents)
                }
            }
        }
    }

    private fun goDetail(contents: String) {
        val requestData = HashMap<String, String>()
        requestData["qrcode"] = contents
        presenter.getDataByScan(requestData) { scanResult ->
            when (scanResult.type) {
                "1" -> scanResult.targetKey?.let { targetKey ->
                    presenter.getResourcesByCatalog(targetKey, "") { bookDetail ->
                        if (bookDetail.resourceList.size == 1) {
                            resourceJump(bookDetail.resourceList[0])
                        } else {
                            startActivity<ScanResListActivity>(
                                ScanResListActivity.KEY to targetKey
                            )
                        }
                    }
                }
                "2" -> scanResult.key?.let { key ->
                    startActivity<BookDetailActivity>(
                        BookDetailActivity.KEY to key, BookDetailActivity.TYPE to "1"
                    )
                }
                //配套二维码 无需判断上下架状态
                "3" -> {
                    startActivity<LessonDetailActivity>(
                        LessonDetailActivity.KEY to scanResult.key,
                        LessonDetailActivity.TYPE to scanResult.type
                    )
                }
                //  营销二维码 需要判断上下架状态 下架状态 显示已下架
                "3.1" -> {
                    startActivity<LessonDetailActivity>(
                        LessonDetailActivity.KEY to scanResult.key,
                        LessonDetailActivity.TYPE to scanResult.type
                    )
                }
                "5" -> scanResult.key?.let { key ->
                    startActivity<WebViewActivity>(
                        WebViewActivity.URL to key, WebViewActivity.TITLE to ""
                    )
                }
                "8" -> scanResult.key?.let { key ->
                    if (key.contains("grade/checkgrade")) {
                        startActivity<PostActivity>(PostActivity.URL to key)
                    } else {
                        startActivity<WebViewActivity>(
                            WebViewActivity.URL to key, WebViewActivity.TITLE to ""
                        )
                    }
                }
                "9" -> scanResult.key?.let { key ->
                    startActivity<BackWordActivity>(BackWordActivity.GRADE_KEY to key)
                }
                "10" -> scanResult.key?.let { key ->
                    presenter.getEvalByScan(key) { evalList ->
                        if (evalList.isNotEmpty()) {
                            if ("1" != evalList[0].isFinish) {
                                if (evalList[0].answerType == "0") {
                                    startActivity<ExerciseDetailKActivity>(
                                        ExerciseDetailKActivity.KEY to evalList[0].appPaperKey,
                                        ExerciseDetailKActivity.CAT_KEY to evalList[0].evalKey,
                                        ExerciseDetailKActivity.NAME to evalList[0].evalName,
                                        ExerciseDetailKActivity.EX_TYPE to ExerciseDetailKActivity.EX_TYPE_PG,
                                        ExerciseDetailKActivity.TYPE to "10",
                                        ExerciseDetailKActivity.USER_PRACTISE_KEY to evalList[0].userPracticeKey
                                    )
                                } else {
                                    startActivity<EntryActivity>(
                                        EntryActivity.KEY to evalList[0].appPaperKey,
                                        EntryActivity.CAT_KEY to evalList[0].evalKey,
                                        EntryActivity.NAME to evalList[0].evalName,
                                        EntryActivity.USER_PRACTISE_KEY to evalList[0].userPracticeKey
                                    )
                                }
                            } else if (evalList[0].state == "1") {
                                //测评报告
                                startActivity<BookReportActivity>(
                                    BookReportActivity.EVALKEY to evalList[0].evalKey,
                                    BookReportActivity.PAPERKEY to evalList[0].appPaperKey,
                                    BookReportActivity.USERPRACTISEKEY to evalList[0].userPracticeKey,
                                    BookReportActivity.PAPER_NAME to evalList[0].evalName,
                                    BookReportActivity.NEED_UP_LOAD to (evalList[0].evalMode == "1"),
                                    BookReportActivity.ANSWER_TYPE to evalList[0].answerType
                                )
                            } else {
                                val item = evalList[0]
                                presenter.getReport(
                                    evalList[0].appPaperKey, evalList[0].userPracticeKey
                                ) { report ->
                                    report.userpractisekey = item.userPracticeKey
                                    if (item.answerType == "0") {
                                        //成绩报告
                                        startActivity<ReportActivity>(
                                            ReportActivity.KEY to item.appPaperKey,
                                            ReportActivity.NAME to item.evalName,
                                            ReportActivity.NEED_UP_LOAD to (item.evalMode == "1"),
                                            ReportActivity.CAT_KEY to item.evalKey,
                                            ReportActivity.EVAL_STATE to item.state,
                                            ReportActivity.EX_TYPE to 2,
                                            ReportActivity.TYPE to scanResult.type,
                                            ReportActivity.DATA to report
                                        )
                                    } else {
                                        startActivity<EvalCardActivity>(
                                            EvalCardActivity.KEY to item.appPaperKey,
                                            EvalCardActivity.NAME to item.evalName,
                                            EvalCardActivity.EVAL_STATE to item.state,
                                            EvalCardActivity.DATA to report,
                                            EvalCardActivity.CAT_KEY to item.evalKey,
                                            EvalCardActivity.NEED_UP_LOAD to (item.evalMode == "1")
                                        )
                                    }
                                }
                            }

                        }
                    }
                }
                "11" -> scanResult.key?.let { key ->
                    AlertDialog.Builder(this).setOnDismissListener {
                        ImmersionBar.with(this@MainActivity)
                            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init()
                    }.setTitle("是否绑定本书防伪码").setMessage("防伪码只能关联唯一用户，请慎重绑定")
                        .setPositiveButton("确定") { dialog, _ ->
                            dialog.dismiss()
                            presenter.activatedFakeCode(
                                mutableMapOf(
                                    "key" to key, "type" to "support"
                                )
                            ) {
                                toast("图书激活成功并已加入学习计划")
                                studyFragment.booksFragment.refreshData()
                            }
                        }.setNegativeButton("取消") { dialog, _ ->
                            dialog.dismiss()
                        }.create().show()

                }
                "12" -> scanResult.key?.let { key ->
                    startActivity<RealActivity>(RealActivity.GRAD_KEY to key)
                }
                "15" -> {//点读书
                    scanResult.key?.let { key ->
                        startActivity<EBookDetailActivity>(EBookDetailActivity.KEY to key)
                    }
                }
                "16" -> {//扫码单词
                    scanResult.key?.let { key ->
                        startActivity<ScanWordsActivity>(ScanWordsActivity.GRADE_KEY to key)
                    }
                }
            }
        }
    }


    /**
     * 不同资源进行跳转
     * 1-paper:试卷；2-paperAnalysis:试卷解析;3-media:视频  4- picture:图片 5-imagesText:图文;6-document:文档；7-frequency :音频; 8-外链
     */
    private fun resourceJump(item: BookRes) {
        if (item.field5 == "2" && (item.field3 == "0" || item.field3 == "2")) {
            //主观题批改中跳转到正在批改中
            startActivity<CompositionDetailWebActivity>(
                CompositionDetailWebActivity.PRACTISE_KEY to item.id,
                CompositionDetailWebActivity.EVAL_KEY to item.field1,
                CompositionDetailWebActivity.TITLE to item.name
            )
        } else {
            when (item.type) {
                DownloadBean.TYPE_EX -> {
                    if ("1" != item.isFinish) {
                        startActivity<ExerciseDetailKActivity>(
                            ExerciseDetailKActivity.KEY to item.id,
                            ExerciseDetailKActivity.NAME to item.name,
                            ExerciseDetailKActivity.CAT_KEY to item.parentKey,
                            ExerciseDetailKActivity.TYPE to item.type,
                            ExerciseDetailKActivity.USER_PRACTISE_KEY to item.userPracticeKey
                        )
                    } else {
                        presenter.getReport(item.id, item.userPracticeKey) {
                            startActivity<ReportActivity>(
                                ReportActivity.DATA to it,
                                ReportActivity.TIME to "",
                                ReportActivity.KEY to item.id,
                                ReportActivity.CAT_KEY to item.parentKey,
                                ReportActivity.NAME to item.name,
                                ReportActivity.TYPE to item.type,
                                ReportActivity.EVAL_STATE to "1"
                            )
                        }
                    }
                }
                DownloadBean.TYPE_PARS -> {
                    startActivity<ExerciseParsingActivity>(
                        ExerciseParsingActivity.KEY to item.id,
                        ExerciseParsingActivity.P_KET to "",
                        ExerciseParsingActivity.NAME to item.name
                    )
                }
                DownloadBean.TYPE_VIDEO -> {
                    val videoParam = VideoParam()
                    videoParam.key = item.id
                    videoParam.type = "2"
                    startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam)
                }
                DownloadBean.TYPE_IMG -> {
                    startActivity<ImageActivity>(
                        ImageActivity.URL to item.link, ImageActivity.NAME to item.name
                    )
                }
                DownloadBean.TYPE_CONTENT -> {
                    startActivity<WebViewActivity>(
                        WebViewActivity.URL to item.content, WebViewActivity.TITLE to item.name
                    )
                }
                DownloadBean.TYPE_PDF -> {
                    val res = item
                    presenter.getResourceInfo(res.id, "2") {
                        res.playUrl = it.url
                        res.downUrl = it.downloadUrl

                        val p = DownloadManager.getFilePathWithKey(res.id, item.type)
                        if (p.isNotEmpty() && File(p).exists()) {
                            startActivity<PDFActivity>(
                                PDFActivity.URL to p,
                                PDFActivity.TITLE to item.name,
                                PDFActivity.RES_ID to res.id
                            )
                        } else {
                            netCheck(null) {
                                startActivity<PDFActivity>(
                                    PDFActivity.URL to res.downUrl,
                                    PDFActivity.TITLE to item.name,
                                    PDFActivity.RES_ID to res.id
                                )
                            }
                        }
                    }
                }
                DownloadBean.TYPE_AUDIO -> {
                    val resList = ArrayList<BookRes>()
                    resList.add(item)
//                    MyApp.instance.bookres = resList
                    // 存
                    val list1: List<BookRes> = ArrayList()
                    val gson1 = Gson()
                    val data1 = gson1.toJson(resList)
                    SPUtils.getInstance().put("BookRes", data1)
                    startActivity<AudioActivity>(AudioActivity.SUPPORT_KEY to item.supportingKey)
                }
                DownloadBean.TYPE_LINK -> {
                    startActivity<WebViewActivity>(
                        WebViewActivity.URL to item.link, WebViewActivity.TITLE to item.name
                    )
                }
                DownloadBean.TYPE_TEST -> {
                    if ("1" != item.isFinish) {
                        startActivity<ExerciseDetailKActivity>(
                            ExerciseDetailKActivity.KEY to item.id,
                            ExerciseDetailKActivity.CAT_KEY to item.field1,
                            ExerciseDetailKActivity.NAME to item.name,
                            ExerciseDetailKActivity.EX_TYPE to ExerciseDetailKActivity.EX_TYPE_PG,
                            ExerciseDetailKActivity.TYPE to item.type,
                            ExerciseDetailKActivity.USER_PRACTISE_KEY to item.userPracticeKey
                        )
                    } else {
                        if (item.field3 == "0" || item.field3 == "2") {
                            presenter.getReport(item.id, item.userPracticeKey) {
                                startActivity<ReportActivity>(
                                    ReportActivity.DATA to it,
                                    ReportActivity.TIME to "",
                                    ReportActivity.KEY to item.id,
                                    ReportActivity.NAME to item.name,
                                    ReportActivity.EX_TYPE to 2,
                                    ReportActivity.TYPE to item.type,
                                    ReportActivity.CAT_KEY to item.field1,
                                    ReportActivity.PRA_KEY to "",
                                    ReportActivity.SP_Q_KEY to "",
                                    ReportActivity.SP_G_NAME to "",
                                    ReportActivity.NEED_UP_LOAD to true,
                                    ReportActivity.EVAL_STATE to "0"
                                )
                            }
                        } else {
                            startActivity<BookReportActivity>(
                                BookReportActivity.EVALKEY to item.field1,
                                BookReportActivity.PAPERKEY to item.id,//试卷key
                                BookReportActivity.PAPER_NAME to item.name,//试卷名曾
                                BookReportActivity.USERPRACTISEKEY to item.userPracticeKey,
                                BookReportActivity.NEED_UP_LOAD to false,
                                BookReportActivity.ANSWER_TYPE to "0"
                            )
                        }
                    }
                }
                DownloadBean.TYPE_NEWS -> {
//              field1:  测评时，测评key；资讯时，资讯类型 1文字内容 2外链内容
//               field2	:测评时，阅卷方式 0是自判 1是人工 2可选；资讯时，外链
//               field3 测评时，试卷批改状态 0未批改 1已批改 2批改中；资讯时，文字内容
                    when (item.field1) {
                        "1" -> startActivity<NewsAndAudioActivity>(
                            NewsAndAudioActivity.KEY to item.id,
                            NewsAndAudioActivity.TITLE to item.name
                        )
                        "2" -> startActivity<WebViewActivity>(
                            WebViewActivity.URL to item.field2, WebViewActivity.TITLE to item.name
                        )
                    }
                }
            }
        }
    }

    public override fun onResume() {
        ImmersionBar.with(this).fullScreen(true).statusBarColor(R.color.transparent)
            .statusBarDarkFont(true).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init()
        getPromotionalFlag()
        getNewVersion()
        super.onResume()
        if (mineFragment.isAdded) {
            mineFragment.onResume()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun busMessage(msg: String) {
        when (msg) {
            "book" -> {
                rb_community.performClick()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        //关闭程序时，清理sonic缓存
        try {
            SonicEngine.getInstance().cleanCache()
            unregisterReceiver(refreshReceiver)
        } catch (e: Exception) {
        }
    }

    //神策调用 trackAppInstall() 方法记录激活事件，多次调用此方法只会在第一次调用时触发激活事件
    //申请权限结果回调无论申请权限成功失败，都要调用 trackAppInstall()：
    private fun saTrackAppInstall() {
        trackAppInstall()
    }

    private fun trackAppInstall() {
        try {
            //记录渠道
            val properties = JSONObject()
            properties.put("DownloadChannel", UMUtils.getChannelByXML(this))
            SensorsDataAPI.sharedInstance().trackAppInstall(properties)
        } catch (e: Exception) {
        }
    }

    companion object {
        const val POS = "position"
    }
}
