package tuoyan.com.xinghuo_dayingindex.ui.scan
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.zxing.util.ZxingUtil
import kotlinx.android.synthetic.main.activity_scan_history.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean
import tuoyan.com.xinghuo_dayingindex.bean.Scan
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.ui._public.ImageActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.ScanResListActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.detail.BookDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.QRDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing.ExerciseParsingActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.word.BackWordActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.word.words.ScanWordsActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.EvalCardActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.evaluation.EntryActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.composition.CompositionDetailWebActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.real.RealActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.TypeUtil
import tuoyan.com.xinghuo_dayingindex.widegt.onLoadMoreListener
import java.io.File

class ScanHistoryActivity : LifeActivity<BasePresenter>() {

    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_scan_history
    private var oldDate = ""//更新数据
    private val adapter by lazy {
        ScanAdapter({
            goDetail(it)
        }) {
            saOption(it, "查看原图")
            val bitmap = ZxingUtil.createQRCode(it.qrCode, 500, 500)
            QRDialog(this, bitmap).show()
        }
    }
    private var page = 1//每页数据20条
    private var hasMore = true
    override fun configView() {
        super.configView()
        rlv_scan.layoutManager = LinearLayoutManager(this)
        rlv_scan.adapter = adapter
    }

    override fun initData() {
        super.initData()
        page = 1
        hasMore = true
        presenter.getQrCodeRecord(page, {
            oldDate = ""
            if (it.isEmpty()) {
                empty.visibility = View.VISIBLE
                rlv_scan.visibility = View.GONE
            } else {
                empty.visibility = View.GONE
                rlv_scan.visibility = View.VISIBLE
                adapter.setData(initScanData(it))
            }
            srf_scan.isRefreshing = false
        }) {
            srf_scan.isRefreshing = false
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        tl_back.setNavigationOnClickListener {
            onBackPressed()
        }
        img_del.setOnClickListener {
            saOption(Scan(), "清空")
            AlertDialog.Builder(this@ScanHistoryActivity)
                .setMessage("将删除整个扫码历史记录")
                .setPositiveButton("确定") { _, _ ->
                    presenter.clearQrcodeRecord() {
                        empty.visibility = View.VISIBLE
                        rlv_scan.visibility = View.GONE
                        toast("扫码历史记录已清空")
                    }
                }
                .setNegativeButton("取消") { _, _ -> }.show()
        }
        srf_scan.setOnRefreshListener {
            initData()
        }
        rlv_scan.addOnScrollListener(object : onLoadMoreListener() {
            override fun onLoading(countItem: Int, lastItem: Int) {
                more()
            }
        })
    }

    fun initScanData(dataList: List<Scan>): List<Scan> {
        dataList.forEach {
            if (it.createDate == oldDate) {
                it.showType = "2"
            } else {
                oldDate = it.createDate
            }
        }
        return dataList
    }

    fun more() {
        if (hasMore) {
            page++
            presenter.getQrCodeRecord(page, {
                if (it.isEmpty()) {
                    hasMore = false
                } else {
                    adapter.addData(initScanData(it))
                }
                srf_scan.isRefreshing = false
            }) {
                srf_scan.isRefreshing = false
            }
        }
    }

    fun saOption(item: Scan, name: String) {
        try {
            val property = JSONObject()
            property.put("operation_name", name)
            property.put("resource_id", if ("1" == item.type) item.targetKey else item.key)
            property.put("resource_name", item.name)
            property.put("QR_code_type", TypeUtil.getScanType(item.type))
            SensorsDataAPI.sharedInstance().track("scan_code_record_page_operation", property)
        } catch (e: Exception) {
        }
    }

    fun goDetail(item: Scan) {
        saOption(item, "点击进入资源")
        when (item.type) {
            "1" -> item.targetKey?.let { key ->
                presenter.getResourcesByCatalog(key, "") { bookDetail ->
                    if (bookDetail.resourceList.size == 1) {
                        resourceJump(bookDetail.resourceList[0])
                    } else {
                        startActivity<ScanResListActivity>(
                            ScanResListActivity.KEY to key
                        )
                    }
                }
            }
            "2" -> item.key?.let { key ->
                startActivity<BookDetailActivity>(
                    BookDetailActivity.KEY to key,
                    BookDetailActivity.TYPE to "1"
                )
            }
            //配套二维码 无需判断上下架状态
            "3" -> {
                startActivity<LessonDetailActivity>(
                    LessonDetailActivity.KEY to item.key,
                    LessonDetailActivity.TYPE to item.type
                )
            }
            //  营销二维码 需要判断上下架状态 下架状态 显示已下架
            "3.1" -> {
                startActivity<LessonDetailActivity>(
                    LessonDetailActivity.KEY to item.key,
                    LessonDetailActivity.TYPE to item.type
                )
            }
            "5" -> item.key?.let { key ->
                startActivity<WebViewActivity>(
                    WebViewActivity.URL to key,
                    WebViewActivity.TITLE to ""
                )
            }
            "8" -> item.key?.let { key ->
                startActivity<WebViewActivity>(
                    WebViewActivity.URL to key,
                    WebViewActivity.TITLE to ""
                )
            }
            "9" -> item.key?.let { key ->
                startActivity<BackWordActivity>(BackWordActivity.GRADE_KEY to key)
            }
            "10" -> item.key?.let { key ->
                presenter.getEvalByScan(key) { list ->
                    if (list.isNotEmpty()) {
                        if ("1" != list[0].isFinish) {
                            if (list[0].answerType == "0") {
                                startActivity<ExerciseDetailKActivity>(
                                    ExerciseDetailKActivity.KEY to list[0].appPaperKey,
                                    ExerciseDetailKActivity.CAT_KEY to list[0].evalKey,
                                    ExerciseDetailKActivity.NAME to list[0].evalName,
                                    ExerciseDetailKActivity.EX_TYPE to ExerciseDetailKActivity.EX_TYPE_PG,
                                    ExerciseDetailKActivity.TYPE to item.type,
                                    ExerciseDetailKActivity.USER_PRACTISE_KEY to list[0].userPracticeKey
                                )
                            } else {//TODO 线下录题
                                startActivity<EntryActivity>(
                                    EntryActivity.KEY to list[0].appPaperKey,
                                    EntryActivity.CAT_KEY to list[0].evalKey,
                                    EntryActivity.NAME to list[0].evalName,
                                    EntryActivity.USER_PRACTISE_KEY to list[0].userPracticeKey
                                )
                            }
                        } else if (list[0].state == "1") {
                            //测评报告
                            startActivity<BookReportActivity>(
                                BookReportActivity.EVALKEY to list[0].evalKey,
                                BookReportActivity.PAPERKEY to list[0].appPaperKey,
                                BookReportActivity.USERPRACTISEKEY to list[0].userPracticeKey,
                                BookReportActivity.PAPER_NAME to list[0].evalName,
                                BookReportActivity.NEED_UP_LOAD to (list[0].evalMode == "1"),
                                BookReportActivity.ANSWER_TYPE to list[0].answerType
                            )
                        } else {
                            var scanItem = list[0]
                            presenter.getReport(
                                list[0].appPaperKey,
                                list[0].userPracticeKey
                            ) { report ->
                                report.userpractisekey = scanItem.userPracticeKey
                                if (scanItem.answerType == "0") {
                                    //成绩报告
                                    startActivity<ReportActivity>(
                                        ReportActivity.KEY to scanItem.appPaperKey,
                                        ReportActivity.NAME to scanItem.evalName,
                                        ReportActivity.NEED_UP_LOAD to (scanItem.evalMode == "1"),
                                        ReportActivity.CAT_KEY to scanItem.evalKey,
                                        ReportActivity.EVAL_STATE to scanItem.state,
                                        ReportActivity.EX_TYPE to 2,
                                        ReportActivity.TYPE to item.type,
                                        ReportActivity.DATA to report
                                    )
                                } else {
                                    startActivity<EvalCardActivity>(
                                        EvalCardActivity.KEY to scanItem.appPaperKey,
                                        EvalCardActivity.NAME to scanItem.evalName,
                                        EvalCardActivity.EVAL_STATE to scanItem.state,
                                        EvalCardActivity.DATA to report,
                                        EvalCardActivity.CAT_KEY to scanItem.evalKey,
                                        EvalCardActivity.NEED_UP_LOAD to (scanItem.evalMode == "1")
                                    )
                                }
                            }
                        }

                    }
                }
            }
            "11" -> item.key?.let { key ->
                android.app.AlertDialog.Builder(ctx)
                    .setOnDismissListener {
                    }
                    .setTitle("是否绑定本书防伪码")
                    .setMessage("防伪码只能关联唯一用户，请慎重绑定")
                    .setPositiveButton("确定") { dialog, _ ->
                        dialog.dismiss()
                        presenter.activatedFakeCode(
                            mutableMapOf(
                                "key" to key,
                                "type" to "support"
                            )
                        ) {
                            toast("图书激活成功并已加入学习计划")
                        }
                    }.setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            }
            "12" -> item.key?.let { key ->
                startActivity<RealActivity>(RealActivity.GRAD_KEY to key)
            }
            "15" -> {//点读书
                item.key?.let { key ->
                    startActivity<EBookDetailActivity>(EBookDetailActivity.KEY to key)
                }
            }
            "16" -> {//扫码单词
                item.key?.let { key ->
                    startActivity<ScanWordsActivity>(ScanWordsActivity.GRADE_KEY to key)
                }
            }
            else -> {
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
//                    var res = item
//                    presenter.getResourceInfo(res.id, "2") {
//                        res.playUrl = it.url
//                        res.downUrl = it.downloadUrl
//
//                        var p = DownloadManager.getFilePathWithKey(res.id, item.type)
//                        if (p.isNotEmpty() && File(p).exists()) {
//                            startActivity<VideoActivity>(VideoActivity.URL to p)
//                        } else {
//                            netCheck(null) {
//                                startActivity<VideoActivity>(VideoActivity.URL to res.playUrl)
//                            }
//                        }
//                    }
                }
                DownloadBean.TYPE_IMG -> {
                    startActivity<ImageActivity>(
                        ImageActivity.URL to item.link,
                        ImageActivity.NAME to item.name
                    )
                }
                DownloadBean.TYPE_CONTENT -> {
                    startActivity<WebViewActivity>(
                        WebViewActivity.URL to item.content,
                        WebViewActivity.TITLE to item.name
                    )
                }
                DownloadBean.TYPE_PDF -> {
                    var res = item
                    presenter.getResourceInfo(res.id, "2") {
                        res.playUrl = it.url
                        res.downUrl = it.downloadUrl

                        var p = DownloadManager.getFilePathWithKey(res.id, item.type)
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
                        WebViewActivity.URL to item.link,
                        WebViewActivity.TITLE to item.name
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
                            presenter.getReport(item.id, item.userPracticeKey) { report ->
                                startActivity<ReportActivity>(
                                    ReportActivity.DATA to report,
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
                            WebViewActivity.URL to item.field2,
                            WebViewActivity.TITLE to item.name
                        )
                    }
                }
            }
        }
    }
}

class ScanAdapter(var detail: (Scan) -> Unit, var pic: (Scan) -> Unit) : BaseAdapter<Scan>(isFooter = true) {
    override fun convert(holder: ViewHolder, item: Scan) {
        holder.setText(R.id.tv_time, item.createDate).setText(R.id.tv_title, item.name).setVisible(R.id.tv_time, if ("1" == item.showType) View.VISIBLE else View.GONE)
        holder.setOnClickListener(R.id.tv_go) {
            detail(item)
        }
        holder.setOnClickListener(R.id.tv_pic) {
            pic(item)
        }
        holder.itemView.setOnClickListener {
            detail(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_scan, parent, false)
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            DeviceUtil.dp2px(context, 60f).toInt()
        )
        view.layoutParams = params
        return view
    }

}