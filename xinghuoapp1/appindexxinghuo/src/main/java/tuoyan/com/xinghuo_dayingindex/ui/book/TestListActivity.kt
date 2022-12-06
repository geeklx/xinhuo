package tuoyan.com.xinghuo_dayingindex.ui.book
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_test_list.*
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.BookDetail
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.ui._public.ImageActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.book.adapter.TestAdapter
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing.ExerciseParsingActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.PracticeRankActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.composition.CompositionDetailWebActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.TypeUtil
import java.io.File

class TestListActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_test_list

    //    val dataList by lazy { intent.getSerializableExtra(LIST) as? ArrayList<BookRes> }
    override val title by lazy { intent.getStringExtra(TITLE) ?: "" }
    val catalogKey by lazy { intent.getStringExtra(CATALOG_KEY) ?: "" }
    val gradeKey by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }
    val bookDetail by lazy { intent.getSerializableExtra(BOOK_DETAIL) as? BookDetail }
    var firstEnter = true

    companion object {
        val CATALOG_KEY = "catalogKey"
        val GRADE_KEY = "gradeKey"
        val LIST = "list"
        val TITLE = "title"
        val BOOK_DETAIL = "bookDetail"
    }

    override fun configView() {
        super.configView()
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_title.text = title
        rlv_list.layoutManager = LinearLayoutManager(this)
        rlv_list.adapter = adapter
    }

//    override fun initData() {
//        super.initData()
//        if (dataList.size > 0) {
//            view_empty.visibility = View.GONE
//            rlv_list.visibility = View.VISIBLE
//            adapter.setData(dataList)
//        } else {
//            rlv_list.visibility = View.GONE
//            view_empty.visibility = View.VISIBLE
//        }
//    }

    override fun onResume() {
        super.onResume()
        presenter.getResourcesByCatalog(catalogKey, gradeKey) { detail ->
            if (detail.resourceList.size > 0) {
                view_empty.visibility = View.GONE
                rlv_list.visibility = View.VISIBLE
                adapter.setData(detail.resourceList)
            } else {
                rlv_list.visibility = View.GONE
                view_empty.visibility = View.VISIBLE
            }
        }
    }

    private val adapter by lazy {
        TestAdapter({ pos, item ->
            resourceJump(item, pos)
        }) {
            startActivity<PracticeRankActivity>(PracticeRankActivity.BOOK_RES to it)
        }
    }

    private fun resourceJump(item: BookRes, position: Int) {
        saOption("点击资源", item.type)
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
                    presenter.getResourceInfo(res.id, "2") {
                        res.playUrl = it.url
                        res.downUrl = it.downloadUrl

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
//                    presenter.getResourceInfo(item.id, "2") {
//                        item.playUrl = it.url
//                        item.downUrl = it.downloadUrl
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
                    // 存
                    val list1: List<BookRes> = ArrayList()
                    val gson1 = Gson()
                    val data1 = gson1.toJson(resList)
                    SPUtils.getInstance().put("BookRes", data1)
                    startActivity<AudioActivity>()
                    startActivity<AudioActivity>()
//                            }
//                        }
//                    }
                }
                DownloadBean.TYPE_LINK -> {
                    startActivity<WebViewActivity>(WebViewActivity.URL to item.link, WebViewActivity.TITLE to item.name)
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
                        "1" -> startActivity<NewsAndAudioActivity>(NewsAndAudioActivity.KEY to item.id, NewsAndAudioActivity.TITLE to item.name)
                        "2" -> startActivity<WebViewActivity>(WebViewActivity.URL to item.field2, WebViewActivity.TITLE to item.name)
                    }

                }
            }
        }
    }

    fun saOption(optionName: String, type: String) {
        try {
            val property = saProperty(bookDetail)
            property.put("operation_name", optionName)
            property.put("resource_type", TypeUtil.getType(type))
            SensorsDataAPI.sharedInstance().track("operation_book_matching_page", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saProperty(bookDetail: BookDetail?): JSONObject {
        val property = JSONObject()
        bookDetail?.let {
            property.put("book_matching_id", bookDetail.supportingKey)
            property.put("book_matching_name", bookDetail.titile)
            property.put("section", bookDetail.gradeName)
        }
        return property
    }
}
