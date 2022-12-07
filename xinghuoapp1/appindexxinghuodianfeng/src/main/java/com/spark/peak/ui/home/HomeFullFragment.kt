package com.spark.peak.ui.home

import android.Manifest
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
import com.spark.peak.WX_MINI_ID
import com.spark.peak.base.LifeFragment
import com.spark.peak.ui.book.HomeBookActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.exercise.GradeExerciseActivity
import com.spark.peak.ui.grammar.GrammarActivity
import com.spark.peak.ui.home.adapter.BooksAdapter
import com.spark.peak.ui.home.adapter.ScansAdapter
import com.spark.peak.ui.home.book.MyBooksActivity
import com.spark.peak.ui.home.sa.SaEvent
import com.spark.peak.ui.lesson.LessonsActivity
import com.spark.peak.ui.message.MessageNoticeActivity
import com.spark.peak.ui.scan.ScanHistoryActivity
import com.spark.peak.ui.scan.ScannerActivity
import com.spark.peak.ui.study.book.BookDetailActivity
import com.spark.peak.utlis.DownloadManager
import com.spark.peak.utlis.FileUtils
import com.spark.peak.utlis.PermissionUtlis
import com.spark.peak.utlis.WxMini
import com.zxing.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_home_fulldf.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

const val FRESH_FLAG = "receiver_flag" //切换学段时，需要刷新首页数据

class HomeFullFragment : LifeFragment<HomePresenter>() {
    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_home_fulldf

    private val bookAdapter by lazy {
        BooksAdapter({
            startActivity<BookDetailActivity>(
                BookDetailActivity.KEY to it.key,
                BookDetailActivity.TYPE to "2"
            )
        }) {
            SaEvent.scanClick()
            PermissionUtlis.checkPermissions(this.requireActivity(), Manifest.permission.CAMERA) {
                IntentIntegrator(this.requireActivity())
                    .setOrientationLocked(false)
                    .setCaptureActivity(ScannerActivity::class.java)
//                        .setPrompt("将二维码放入框内，即可自动扫描")
                    .initiateScan()
            }
        }
    }
    private val scanAdapter by lazy {
        ScansAdapter {
            when (it.type) {
                "1" -> {
                    startActivity<PostActivity>(
                        PostActivity.URL to it.key,
                        PostActivity.TITLE to ""
                    )
                }
                "2" -> {
                    startActivity<BookDetailActivity>(
                        BookDetailActivity.KEY to it.key,
                        BookDetailActivity.TYPE to "1"
                    )
                }
            }
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        getNewVersion()
        rlv_my_book.layoutManager = GridLayoutManager(this.requireContext(), 3)
        rlv_my_book.adapter = bookAdapter
        rlv_scan.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_scan.adapter = scanAdapter
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_my_book_more.setOnClickListener {
            checkLogin {
                startActivity(Intent(this.requireContext(), MyBooksActivity::class.java))
            }
        }
        tv_home_scan_more.setOnClickListener {
            checkLogin {
                startActivity(Intent(this.requireContext(), ScanHistoryActivity::class.java))
            }
        }
        tv_word.setOnClickListener {
            SaEvent.bannerClick("同步词汇")
            WxMini.goWxMini(this.requireContext(), WX_MINI_ID)
        }
        tv_grammar.setOnClickListener {
            SaEvent.bannerClick("语法词典")
            startActivity(Intent(this.requireContext(), GrammarActivity::class.java))
        }
        tv_book.setOnClickListener {
            SaEvent.bannerClick("教材同步")
            startActivity(Intent(this.requireContext(), HomeBookActivity::class.java))
        }
        tv_video.setOnClickListener {
            SaEvent.bannerClick("专项微课")
            checkLogin {
                startActivity(Intent(this.requireContext(), LessonsActivity::class.java))
            }
        }
        tv_exercise.setOnClickListener {
            SaEvent.bannerClick("专项试题")
            checkLogin {
                startActivity(Intent(this.requireContext(), GradeExerciseActivity::class.java))
            }
        }
        img_msg.setOnClickListener {
            checkLogin {
                startActivity<MessageNoticeActivity>()
            }
        }
        tv_question.setOnClickListener {
            SaEvent.bannerClick("在线问答")
            checkLogin {
                startActivity<PostActivity>(
                    PostActivity.TITLE to "",
                    PostActivity.URL to WEB_BASE_URL + "community/faq/OnlineQA"
                )
            }
        }
    }

    override fun initData() {
        super.initData()
        presenter.getHomePageInfoDF {
            img_msg.isSelected = it.noticeReadFlag == "0"
            tv_my_book_more.visibility = if (it.bookList.isEmpty()) View.GONE else View.VISIBLE
            tv_home_scan_more.visibility = if (it.qrCodeList.isEmpty()) View.GONE else View.VISIBLE
            var size =
                if (it.bookList.isNotEmpty() && it.bookList.size >= 3) 3 else if (it.bookList.isNotEmpty()) it.bookList.size else 3
            rlv_my_book.layoutManager = GridLayoutManager(this.requireContext(), size)
            rlv_my_book.adapter = bookAdapter
            bookAdapter.setData(it.bookList)
            scanAdapter.setData(it.qrCodeList)
        }
    }

    /**
     * 获取新版本
     */
    var isForce = ""

    private fun getNewVersion() {
        presenter.getNewVersion {
            if (it.isForce == "0" || it.isForce == "1") {
                isForce = it.isForce
                AlertDialog.Builder(this.requireContext()).setCancelable(false)
                    .setTitle("发现新版本" + it.v).setMessage(it.msg).setPositiveButton("下载更新") { _, _ ->
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

    private fun downloadApk(url: String) {
        if (url.isNotEmpty()) {
            PermissionUtlis.checkPermissions(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                onCancel = {
                    this.requireActivity().finish()
                }) {
                showDownloadDialog()
                FileDownloader.setup(this.requireContext())
                FileDownloader.getImpl().create(url)
                    ./*setForceReDownload(true).*/setPath(DownloadManager.getFilePath(url))
                    .setListener(downloadListener).start()
            }
        } else {
            toast("下载url异常")
        }
    }

    val downloadDialog by lazy { ProgressDialog(this.requireContext()) }
    private fun showDownloadDialog() {
        with(downloadDialog) {
            setTitle("正在下载")
            setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL)
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
                    HomeFragment.apkFilePath = task?.path ?: ""
                    checkIsAndroidO {
                        FileUtils.installAPK(
                            this@HomeFullFragment.requireContext(), task?.path
                                ?: ""
                        )
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
            val b = this.requireContext().packageManager.canRequestPackageInstalls()
            if (b) {
                onNext()
            } else {
                val packageURI = Uri.parse("package:" + this.requireContext().packageName)
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
                startActivityForResult(intent, HomeFragment.REQUEST_APK_INSTALL)
            }
        } else {
            onNext()
        }
    }

    //初始化刷新首页数据的receiver
    private fun initReceiver() {
        val mReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
//                initHomeData()
            }
        }
        val iF = IntentFilter()
        iF.addAction(FRESH_FLAG)
        this.requireContext().registerReceiver(mReceiver, iF)
    }
}