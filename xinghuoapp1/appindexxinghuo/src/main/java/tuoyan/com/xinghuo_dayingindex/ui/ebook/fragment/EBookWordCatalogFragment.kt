package tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.fragment_ebook_word_catalog.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeFullActivity
import tuoyan.com.xinghuo_dayingindex.base.LifeV4FullFragment
import tuoyan.com.xinghuo_dayingindex.bean.EBook
import tuoyan.com.xinghuo_dayingindex.bean.EBookCatalog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.CatalogWordAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordImgActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordPracticeActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils
import tuoyan.com.xinghuo_dayingindex.utlis.UnZiper
import java.io.File

private const val KEY = "key"
private const val ISOWN = "isOwn"

class EBookWordCatalogFragment : LifeV4FullFragment<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_ebook_word_catalog

    private val activity by lazy { this.requireActivity() as EBookWordImgActivity }

    private val key by lazy { arguments?.getString(KEY) ?: "" }

    val catalogKeys by lazy { mutableListOf<String>() }
    private var dDialog: DDialog? = null

    private var downloadTaskId = 0

    private var downloadState = "1"//state: ???????????? 1??? ????????? 2???????????? 3?????????  4:????????? 5???????????????

    private val wordAdapter by lazy {
        CatalogWordAdapter(false) { type, item ->
            toCatalogDetail(type, item)
        }
    }
    private val isOwn by lazy { arguments?.getString(ISOWN) ?: "0" }

    companion object {
        @JvmStatic
        fun newInstance(key: String, isOwn: String) =
            EBookWordCatalogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY, key)
                    putString(ISOWN, isOwn)
                }
            }
    }

    override fun configView(view: View?) {
        super.configView(view)
        initDownloader()
        rlv_c.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_c.adapter = wordAdapter
        wordAdapter.setHeader(false)
        wordAdapter.catalogType = "1"
        wordAdapter.isOwn = isOwn

        if (DownloadManager.isDownEBookWord(key)) {
            downloadState = "5"
            downloadState()
        }
        ctl_download.visibility = if (isOwn == "1") View.VISIBLE else View.GONE
        tv_w.visibility = if (isOwn == "1") View.VISIBLE else View.GONE
    }

    override fun initData() {
        super.initData()
        presenter.getSmartBookCatalogList(key, "3") { list ->
            wordAdapter.setData(formatCatalogList(list))
        }
        initZip()
    }

    private fun toCatalogDetail(type: Int, item: EBookCatalog) {
        isLogin {
            when (type) {
                1 -> {
                    activity.bookParam?.let {
                        if (it.catalogKey != item.catalogKey) {
                            it.catalogKey = item.catalogKey
                            activity.getReadList()
                        }
                    }
                    activity.catalogClick()
                }
                2 -> {//??????
                    activity.bookParam?.let {
                        it.catalogKey = item.catalogKey
                    }
                    startActivity<EBookWordPracticeActivity>(EBookLifeFullActivity.EBOOK_PARAM to activity.bookParam)
                    activity.finish()
                }
                3 -> {
                    activity.goBuy()
                }
                4 -> {//????????????
                    activity.bookParam?.let {
                        it.catalogKey = item.catalogKey
                    }
                    startActivity<EBookWordExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to activity.bookParam)
                    activity.finish()
                }
            }
        }
    }

    private fun formatCatalogList(list: List<EBookCatalog>): List<EBookCatalog> {
        catalogKeys.clear()
        val dataList = mutableListOf<EBookCatalog>()
        list.forEach { pCatalog ->
            pCatalog.type = "1"
            pCatalog.showType = "1"
            dataList.add(pCatalog)
            pCatalog.catalogList.forEach { cCatalog ->
                cCatalog.showType = "1"
                cCatalog.type = "2"
                cCatalog.parentKey = pCatalog.catalogKey
                dataList.add(cCatalog)
                if (cCatalog.existRead == "1" || cCatalog.existRead == "2") {
                    catalogKeys.add(cCatalog.catalogKey)
                }
            }
            dataList.last().lineType = "1"
        }
        return dataList
    }

    override fun handleEvent() {
        super.handleEvent()
        fragment.setOnClickListener {
            activity.catalogClick()
        }
        ctl_download.setOnClickListener {
            activity.bookDetail?.let { book ->
                if (book.downloadUrl != null && book.downloadUrl.isNotBlank()) {
                    when (downloadState) {
                        "1" -> showDialog(book)
                        "2" -> downloadPause()
                        "3" -> download(book.downloadUrl)
                    }
                }
            }
        }
    }

    private fun showDialog(book: EBook) {
        if (NetWorkUtils.isWifiConnected()) {
            download(book.downloadUrl)
        } else {
            dDialog = DDialog(this.requireContext()).setMessage("?????????WiFi????????????????????????????????????????????????")
                .setNegativeButton("????????????") {
                    dDialog?.dismiss()
                    download(book.downloadUrl)

                }.setPositiveButton("????????????") {
                    dDialog?.dismiss()
                }
            dDialog?.show()
        }
    }

    /**
     * ????????????,
     * cc
     */

    private fun startUnzip(filePath: String) {
        downloadState = "4"
        downloadState()
        activity.bookParam?.let { book ->
            DownloadManager.saveEBookWord(book.name, key)
        }
        val unZiper = UnZiper(object : UnZiper.UnZipListener {
            override fun onError(errorCode: Int, message: String?) {
                if (!activity.isFinishing) {
                    runOnUiThread {
                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onUnZipFinish() {
                if (!activity.isFinishing) {
                    downloadState = "5"
                    runOnUiThread {
                        downloadState()
                        Toast.makeText(activity, "????????????", Toast.LENGTH_LONG).show()
                    }
                    activity.isDown = true
                    activity.initDownRes { }
                }
            }
        })
        unZiper.startUnzip(filePath, FileUtils.getUnzipDir(filePath))
    }

    /**
     * state: ???????????? 1??? ????????? 2???????????? 3????????? 4???????????????
     */
    private fun downloadState() {
        when (downloadState) {
            "1" -> {//?????????????????????
                img_d.visibility = View.VISIBLE
                temp1.visibility = View.VISIBLE
                tv_size.visibility = View.VISIBLE
                tv_download_state.visibility = View.GONE
            }
            "2" -> {
                img_d.visibility = View.GONE
                temp1.visibility = View.GONE
                tv_size.visibility = View.GONE
                tv_download_state.visibility = View.VISIBLE
                tv_download_state.text = "?????????"
            }
            "3" -> {
                img_d.visibility = View.GONE
                temp1.visibility = View.GONE
                tv_size.visibility = View.GONE
                tv_download_state.visibility = View.VISIBLE
                tv_download_state.text = "??????"
            }
            "4" -> {
                img_d.visibility = View.GONE
                temp1.visibility = View.GONE
                tv_size.visibility = View.GONE
                tv_download_state.visibility = View.VISIBLE
                tv_download_state.text = "?????????"
                tv_download_state.isSelected = true
                pb_download.progress = 100
            }
            "5" -> {
                img_d.visibility = View.GONE
                temp1.visibility = View.GONE
                tv_size.visibility = View.GONE
                tv_download_state.visibility = View.VISIBLE
                tv_download_state.text = "?????????"
                tv_download_state.isSelected = true
                pb_download.progress = 100
            }
        }

    }

    private fun initDownloader() {
        // ?????????FileDownloader???????????????
        FileDownloader.setup(this.requireContext())
        FileDownloader.setGlobalPost2UIInterval(150)
    }

    private fun download(url: String) {
        val zipPath = DownloadManager.downloadPathEN + "/$key.zip"
        downloadTaskId = FileDownloader.getImpl().create(url).setPath(zipPath)
            .setAutoRetryTimes(3)
            .setListener(object : FileDownloadListener() {
                override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (!activity.isFinishing) {
                        downloadState = "2"
                        downloadState()
                        pb_download.progress = (soFarBytes / 1024) * 100 / (totalBytes / 1024)
                    }
                }

                override fun completed(task: BaseDownloadTask?) {
                    if (!activity.isFinishing) {
                        toast("????????????")
                        startUnzip(zipPath)
                    }
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (!activity.isFinishing) {
                        downloadState = "3"
                        downloadState()
                    }
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    if (!activity.isFinishing) {
                        downloadState = "3"
                        downloadState()
                    }
                }

                override fun warn(task: BaseDownloadTask?) {
                }
            }).start()
    }

    //???????????? ?????????
    private fun initZip() {
        val zipPath = "${DownloadManager.downloadPathEN}/${key}.zip"
        val filePath = "${DownloadManager.downloadPathEN}/${key}"
        val zipFile = File(zipPath)
        val file = File(filePath)
        if (zipFile.exists() && !file.exists()) {
            startUnzip(zipPath)
        }
    }

    fun currentCatalogKey(key: String) {
        var pos = 0
        wordAdapter.currentCatalogKey = key
        kotlin.run breaking@{
            wordAdapter.getData().forEachIndexed { index, eBookCatalog ->
                if (eBookCatalog.catalogKey == key) {
                    pos = index
                    return@breaking
                }
            }
        }
        Handler().postDelayed({
            (rlv_c.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(pos, 100)
        }, 500)
    }

    private fun downloadPause() {
        FileDownloader.getImpl().pause(downloadTaskId)
    }

    fun setSize(size: String) {
        tv_size.text = "${size}M"
    }
}