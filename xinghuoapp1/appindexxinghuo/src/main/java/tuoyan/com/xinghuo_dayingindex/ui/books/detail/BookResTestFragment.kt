package tuoyan.com.xinghuo_dayingindex.ui.books.detail
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.Manifest
import android.app.AlertDialog
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.activity_net_lessons.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DEFAULT
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DONE
import tuoyan.com.xinghuo_dayingindex.ui._public.ImageActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.book.BasicWordActivity
import tuoyan.com.xinghuo_dayingindex.ui.book.BookTranActivity
import tuoyan.com.xinghuo_dayingindex.ui.book.TestListActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.adapter.BookResTestAdapter
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing.ExerciseParsingActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.composition.CompositionDetailWebActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File
import kotlin.properties.Delegates

/**
 * ??????????????? ???????????? ???????????? ???????????? ????????????
 */
class BookResTestFragment : LifeV4Fragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = 0
    private var gradeKey: String = ""
    val bookInfo by lazy {
        (activity as BookDetailActivity).getBookInfo()
    }
    private val parentActivity by lazy { activity as BookDetailActivity }
    var rv by Delegates.notNull<RecyclerView>()

    //    val dataList by lazy { arguments?.getSerializable(DATA_LIST) as ArrayList<BookRes> }
    private var dataList: ArrayList<BookRes>? = null

    companion object {
        //        val DATA_LIST = "data_list"
        fun newInstance(): BookResTestFragment {
            val f = BookResTestFragment()
//            val args = Bundle()
//            args.putSerializable(DATA_LIST, dataList)
//            f.arguments = args
            return f
        }
    }

    /**
     * ????????????????????????????????????
     */
    fun ownFlg(flag: Boolean) {
        mAdapter.isOwn = flag
        if (onLoad) bookInfo.isown = if (flag) "1" else "0"
        mAdapter.notifyDataSetChanged()
    }

    fun gradeKey(gradeKey: String) {
        this.gradeKey = gradeKey
    }

    override fun initView(): View? = with(ctx) {
        rv = recyclerView {
            lparams(matchParent, matchParent)
            layoutManager = LinearLayoutManager(ctx)
        }
        return rv
    }

    private var mAdapter = BookResTestAdapter({ item, position ->
        isLogin {
            resourceJump(item, position)
        }
    }) { position, res ->
        isLogin {
            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
                when (res.state) {
                    STATE_DEFAULT -> {
                        getResInfo(res.id, "2") { resourceInfo ->
                            res.playUrl = resourceInfo.url
                            res.downUrl = resourceInfo.downloadUrl
                            res.lrcurl = resourceInfo.lrcurl1
                            res.lrcurl2 = resourceInfo.lrcurl2
                            res.lrcurl3 = resourceInfo.lrcurl3

                            val p = DownloadManager.getFilePathWithKey(res.id, res.type)
                            if (p.isNotEmpty()) {
                                val task = FileDownloader.getImpl().create(res.downUrl).setPath(p).setListener(listener)
                                task.tag = res.id //TODO ?????????task?????????tag?????????????????????id???????????????????????????????????????
                                DownloadManager.taskList.add(task)
                                task.start()
                                refreshState(position, "", DownloadBean.STATE_DOWNLOADING)
                                //TODO ?????????????????????????????????
                                if (res.lrcurl.isNotEmpty()) FileDownloader.getImpl().create(res.lrcurl).setPath(DownloadManager.getFilePath(res.lrcurl)).start()
                                if (res.lrcurl2.isNotEmpty()) FileDownloader.getImpl().create(res.lrcurl2).setPath(DownloadManager.getFilePath(res.lrcurl2)).start()
                                if (res.lrcurl3.isNotEmpty()) FileDownloader.getImpl().create(res.lrcurl3).setPath(DownloadManager.getFilePath(res.lrcurl3)).start()
                            } else {
                                toast("???????????????????????????")
                            }
                        }
                    }
                    DownloadBean.STATE_DOWNLOADING -> {
                        val task = DownloadManager.getRunTask(res.id)
                        task?.let { downloadTask ->
                            downloadTask.pause()
                            refreshState(position, "", DownloadBean.STATE_PAUSED)
                        }
                    }
                    DownloadBean.STATE_PAUSED -> {
                        val task = DownloadManager.getRunTask(res.id)
                        task?.let { downloadTask ->
                            downloadTask.reuse()
                            downloadTask.start()
                            refreshState(position, "", DownloadBean.STATE_DOWNLOADING)
                        }
                    }
                }
            }
        }
    }

    /**
     * ????????????????????????
     * 1-paper:?????????2-paperAnalysis:????????????;3-media:??????  4- picture:?????? 5-imagesText:??????;6-document:?????????7-frequency :??????; 8-??????
     */
    private fun resourceJump(item: BookRes, position: Int) {
        parentActivity.saOption("????????????", item.type)
        if (item.field5 == "2" && (item.field3 == "0" || item.field3 == "2")) {
            //??????????????????????????????????????????
            startActivity<CompositionDetailWebActivity>(
                CompositionDetailWebActivity.PRACTISE_KEY to item.id,
                CompositionDetailWebActivity.EVAL_KEY to item.field1,
                CompositionDetailWebActivity.TITLE to item.name
            )
        } else {
            when (item.type) {
                DownloadBean.TYPE_EX -> {
                    parentActivity.refresh = true
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
                        ExerciseParsingActivity.KEY to item.id, ExerciseParsingActivity.P_KET to "",
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
                    startActivity<ImageActivity>(ImageActivity.URL to item.link, ImageActivity.NAME to item.name)
                }
                DownloadBean.TYPE_CONTENT -> {
                    startActivity<WebViewActivity>(WebViewActivity.URL to item.content, WebViewActivity.TITLE to item.name)
                }
                DownloadBean.TYPE_PDF -> {
                    val res = item
                    presenter.getResourceInfo(res.id, "2") { info ->
                        res.playUrl = info.url
                        res.downUrl = info.downloadUrl

                        val p = DownloadManager.getFilePathWithKey(res.id, item.type)
                        if (p.isNotEmpty() && File(p).exists()) {
                            startActivity<PDFActivity>(PDFActivity.URL to p, PDFActivity.TITLE to item.name, PDFActivity.RES_ID to res.id)
                        } else {
                            netCheck(null) {
                                startActivity<PDFActivity>(PDFActivity.URL to res.downUrl, PDFActivity.TITLE to item.name, PDFActivity.RES_ID to res.id)
                            }
                        }
                    }
                }
                DownloadBean.TYPE_AUDIO -> {
//                    presenter.getResourceInfo(item.id, "2") { info ->
//                        item.playUrl = info.url
//                        item.downUrl = info.downloadUrl
                    val resList = ArrayList<BookRes>()
//                        val bookRes = item
                    resList.add(item)
//                        val p = DownloadManager.getFilePathWithKey(item.id, item.type)
//                        if (p.isNotEmpty() && File(p).exists()) {
//                            MyApp.instance.bookres = resList
//                            startActivity<AudioActivity>()
//                        } else {
//                            netCheck(null) {
//                    MyApp.instance.bookres = resList
                    // ???
                    val list1: List<BookRes> = ArrayList()
                    val gson1 = Gson()
                    val data1 = gson1.toJson(resList)
                    SPUtils.getInstance().put("BookRes", data1)
                    startActivity<AudioActivity>()
                    startActivity<AudioActivity>(
                        AudioActivity.SUPPORT_KEY to bookInfo.supportingKey
                    )
//                            }
//                        }
//                    }
                }
                DownloadBean.TYPE_LINK -> {
                    startActivity<WebViewActivity>(WebViewActivity.URL to item.link, WebViewActivity.TITLE to item.name)
                }
                DownloadBean.TYPE_TEST -> {
                    parentActivity.refresh = true
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
                                BookReportActivity.PAPERKEY to item.id,//??????key
                                BookReportActivity.PAPER_NAME to item.name,//????????????
                                BookReportActivity.USERPRACTISEKEY to item.userPracticeKey,
                                BookReportActivity.NEED_UP_LOAD to false,
                                BookReportActivity.ANSWER_TYPE to "0"
                            )
                        }
                    }
                }
                DownloadBean.TYPE_NEWS -> {
//              field1:  ??????????????????key??????????????????????????? 1???????????? 2????????????
//               field2	:???????????????????????? 0????????? 1????????? 2???????????????????????????
//               field3 ?????????????????????????????? 0????????? 1????????? 2????????????????????????????????????
                    when (item.field1) {
                        "1" -> startActivity<NewsAndAudioActivity>(NewsAndAudioActivity.KEY to item.id, NewsAndAudioActivity.TITLE to item.name)
                        "2" -> startActivity<WebViewActivity>(WebViewActivity.URL to item.field2, WebViewActivity.TITLE to item.name)
                    }

                }
                else -> {
                    val mBookDetail = BookDetail()
                    mBookDetail.supportingKey = parentActivity.bookDetail.supportingKey
                    mBookDetail.titile = parentActivity.bookDetail.titile
                    mBookDetail.gradeName = parentActivity.bookDetail.gradeName
                    when (position) {
                        1 -> startActivity<BasicWordActivity>(
                            BasicWordActivity.BOOK_DETAIL to mBookDetail,
                            BasicWordActivity.GRADE_KEY to gradeKey,
                            BasicWordActivity.CATALOG_KEY to item.catalogKey,
                        )
                        2 -> {
                            startActivity<TestListActivity>(
                                TestListActivity.CATALOG_KEY to item.catalogKey,
                                TestListActivity.GRADE_KEY to gradeKey,
                                TestListActivity.BOOK_DETAIL to mBookDetail,
                                TestListActivity.TITLE to "????????????"
                            )
                        }
                        3 -> {
                            startActivity<BookTranActivity>(
                                BookTranActivity.BOOK_DETAIL to mBookDetail,
                                BookTranActivity.TITLE to "??????????????????",
                                BookTranActivity.CATALOG_KEY to item.catalogKey,
                                BookTranActivity.GRADE_KEY to gradeKey,
                            )
                        }
                        4 -> {
                            startActivity<TestListActivity>(
                                TestListActivity.CATALOG_KEY to item.catalogKey,
                                TestListActivity.GRADE_KEY to gradeKey,
                                TestListActivity.BOOK_DETAIL to mBookDetail,
                                TestListActivity.TITLE to "????????????"
                            )
                        }
                        5 -> {
                            startActivity<TestListActivity>(
                                TestListActivity.CATALOG_KEY to item.catalogKey,
                                TestListActivity.GRADE_KEY to gradeKey,
                                TestListActivity.BOOK_DETAIL to mBookDetail,
                                TestListActivity.TITLE to "????????????"
                            )
                        }
                        else -> {
                            presenter.getResourcesByCatalog(item.catalogKey, gradeKey) { detail ->
                                when (position) {
                                    0 -> {
                                        if (detail.resourceList.size == 1) {
                                            //?????????????????????
                                            resourceJump(detail.resourceList[0], position)
                                        } else {
                                            //???????????????
                                            startActivity<TestListActivity>(
                                                TestListActivity.CATALOG_KEY to item.catalogKey,
                                                TestListActivity.GRADE_KEY to gradeKey,
                                                TestListActivity.BOOK_DETAIL to mBookDetail,
                                                TestListActivity.TITLE to "????????????"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun gotoAudio(position: Int) {
//        MyApp.instance.bookres = mAdapter.getData()
        // ???
        val list1: List<ResourceListBean> = ArrayList()
        val gson1 = Gson()
        val data1 = gson1.toJson(mAdapter.getData())
        SPUtils.getInstance().put("BookRes", data1)
        startActivity<AudioActivity>(
            AudioActivity.POSITION to position,
            AudioActivity.CAN_DOWNLOAD to (bookInfo.isown == "1")
        )
    }

    private var listener = object : FileDownloadListener() {
        override fun warn(task: BaseDownloadTask?) {
            toast("?????????????????????")
        }

        override fun completed(task: BaseDownloadTask?) {
            try {
                task?.let {
                    val key = it.tag as String?
                    saveDownload(key ?: "")
                    Log.e("DOWNLOAD_FLAG", "----------------saved------------")
                    val thisTask = DownloadManager.getRunTask(key ?: "")
                    DownloadManager.taskList.remove(thisTask)
                    refreshState(-1, key ?: "", STATE_DONE)
                }
                toast("????????????")
            } catch (e: Exception) {
                Log.e("DOWNLOAD_FLAG", "--catch--" + e.message)
            }
        }

        override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            toast("????????????...")
        }

        override fun error(task: BaseDownloadTask?, e: Throwable?) {
            try {
                task?.let {
                    val key = it.tag as String?
                    refreshState(-1, key ?: "", STATE_DEFAULT)
                }
                AlertDialog.Builder(ctx).setMessage("??????????????? " + e?.message).setPositiveButton("??????") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.create().show()
            } catch (e: Exception) {
            }

        }

        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            task?.let {
                val key = it.tag as String?
                val progress = if (soFarBytes > 10000) (soFarBytes / 100) / (totalBytes / 10000) else soFarBytes * 100 / totalBytes
                refreshProgress(key ?: "", progress)
            }
        }

        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            toast("??????...")
        }

    }

    var onLoad = false
    override fun initData() {
        initDownloader()
//        mAdapter.setData(dataList)
        rv.adapter = mAdapter
    }

    fun setDataList(dataList: ArrayList<BookRes>) {
        this.dataList = dataList
        mAdapter.setData(dataList)
    }

    private fun getResInfo(key: String, source: String, onNext: (ResourceInfo) -> Unit) {
        presenter.getResourceInfo(key, source, "1") { info ->
            onNext(info)
        }
    }

    private fun initDownloader() {
        FileDownloader.setup(ctx)
        FileDownloader.setGlobalPost2UIInterval(150)
        configDownloadState()
        reShowDownload()
        onLoad = true
    }

    /**
     * ??????????????????
     */
    private fun refreshState(position: Int, key: String, state: Int) {
        if (position == -1) {
            dataList?.forEach { bookRes ->
                if (bookRes.id == key) {
                    bookRes.state = state
                }
            }
        } else {
            dataList?.let { list ->
                list[position].state = state
            }
        }
        mAdapter.notifyDataSetChanged()
    }

    /**
     * ??????????????????
     */
    private fun refreshProgress(key: String, progress: Int) {
        dataList?.forEach { bookRes ->
            if (bookRes.id == key) {
                bookRes.progress = progress
            }
        }
        mAdapter.notifyDataSetChanged()
    }

    /**
     * ?????????????????????realm
     */
    private fun saveDownload(key: String) {
        dataList?.forEach { bookRes ->
            if (bookRes.id == key) {
                bookRes.lrcUrls = "${if (bookRes.lrcurl.isEmpty()) "none" else bookRes.lrcurl},${if (bookRes.lrcurl2.isEmpty()) "none" else bookRes.lrcurl2},${if (bookRes.lrcurl3.isEmpty()) "none" else bookRes.lrcurl3}"
                DownloadManager.saveDownloadInfo(bookInfo.titile, bookInfo.supportingKey, "??????", bookRes)
            }
        }
    }

    /**
     * ?????? ??????????????????????????????
     */
    private fun configDownloadState() {
        val resList = DownloadManager.getDownloadedResAll()
        resList.forEach { resource ->
            val res = resource
            dataList?.forEach { bookRes ->
                if (bookRes.id == res.key || (bookRes.name == res.name && bookRes.type == res.type)) {
                    val p = DownloadManager.getFilePathWithKey(bookRes.id, bookRes.type)
                    if (p.isNotEmpty() && File(p).exists()) {
                        //????????????????????? ????????????????????????
                        bookRes.state = STATE_DONE
                    }
                }
            }
        }
    }

    /**
     * ???????????????????????????????????????
     * ????????????
     */
    private fun reShowDownload() {
        try {
            DownloadManager.taskList.forEach { downloadTask ->
                val task = downloadTask
                val key = task.tag as String?
                dataList?.forEach { bookRes ->
                    if (key == bookRes.id) {
                        task.listener = listener
                        bookRes.state = DownloadManager.getDownloadState(task.id, bookRes)
                        val progress = if (task.soFarBytes > 10000) (task.soFarBytes / 100) / (task.totalBytes / 10000) else task.soFarBytes * 100 / task.totalBytes
                        bookRes.progress = progress
                        return@forEach
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    var refreshFlag = false
    override fun onResume() {
        super.onResume()
        if (refreshFlag) {
            reShowDownload()
        }
        refreshFlag = true
    }
}