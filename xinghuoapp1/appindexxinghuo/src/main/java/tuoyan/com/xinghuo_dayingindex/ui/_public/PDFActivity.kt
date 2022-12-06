package tuoyan.com.xinghuo_dayingindex.ui._public

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.activity_pdf.*
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL_PC
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_PDF
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File

class PDFActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_pdf

    private val resId by lazy { intent.getStringExtra(RES_ID) ?: "" }
    private val sourceType by lazy { intent.getStringExtra(SOURCE_TYPE) ?: "2" }
    private val url by lazy { intent.getStringExtra(URL) ?: "" }
    override val title by lazy { intent.getStringExtra(TITLE) ?: "" }

    private var downloadTask: BaseDownloadTask? = null

    companion object {
        val SOURCE_TYPE = "SOURCE_TYPE"
        val RES_ID = "res_id"
        val URL = "url"
        val TITLE = "title"
    }

    override fun onError(message: String) {
        if (url.startsWith("http")) {
            super.onError(message)
        }
    }

    private val downloadListener by lazy {
        object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask?) {
            }

            override fun completed(task: BaseDownloadTask?) {
                try {
                    pb_download.visibility = View.GONE
                    tv_download.visibility = View.GONE
                    val file = File(task?.path ?: "")
                    showPDF(file)
                    clearFlag = true
                } catch (e: Exception) {
                }
            }

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                toast("加载中，请稍候")
                pb_download.visibility = View.VISIBLE
                tv_download.visibility = View.VISIBLE
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                try {
                    toast("文件加载失败，请重试")
                    pb_download.visibility = View.GONE
                    tv_download.visibility = View.GONE
                } catch (e: Exception) {
                }
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                val i = (soFarBytes * 100) / totalBytes
                pb_download.progress = i
                tv_download.text = "$i%"
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

            }

        }
    }

    override fun configView() {
        super.configView()
        setSupportActionBar(tb_pdf_view)
        tv_title.text = title
        img_toolbar_download.visibility = if (resId.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun handleEvent() {
        super.handleEvent()
        tb_pdf_view.setNavigationOnClickListener { onBackPressed() }
        img_toolbar_download.setOnClickListener {
            AlertDialog.Builder(this).setMessage("复制链接至电脑浏览器下载").setPositiveButton("复制链接") { dialogInterface, _ ->
                //获取剪贴板管理器：
                val cm = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                // 创建普通字符型ClipData
                val mClipData = ClipData.newPlainText("Label", "${WEB_BASE_URL_PC}book/pdfMaterial?key=${resId}&source=${sourceType}")
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData)
                toast("已复制到剪贴板")
            }.create().show()
        }
    }

    override fun initData() {
        FileDownloader.getImpl()
        super.initData()
        if (url.startsWith("http")) {
            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
                val p = DownloadManager.getFilePathWithKey(resId, TYPE_PDF)
                downloadTask = FileDownloader.getImpl().create(url).setPath(p).setListener(downloadListener)
                downloadTask?.start()
            }
        } else {
            try {
                val pdfFile = File(url)
                showPDF(pdfFile)
            } catch (e: Exception) {
                toast("文件异常，重新下载后重试")
            }
        }
    }

    private fun showPDF(file: File) {
        try {
            pdfView.fromFile(file).defaultPage(0).enableSwipe(true).load()
        } catch (e: Exception) {
        }
    }

    var clearFlag = false
    override fun onDestroy() {
        downloadTask?.pause()
        if (resId != null && resId.isNotEmpty()) {
            val p = DownloadManager.getFilePathWithKey(resId, TYPE_PDF)
            if (p.isNotEmpty() && clearFlag) {//当前页下载的PDF资源清除下载记录
                FileUtils.deleteFile(p)
            }
        }
        super.onDestroy()
    }
}
