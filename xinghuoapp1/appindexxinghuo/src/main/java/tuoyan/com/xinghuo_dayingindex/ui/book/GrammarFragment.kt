package tuoyan.com.xinghuo_dayingindex.ui.book
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_basicitem_list.*
import org.jetbrains.anko.support.v4.ctx
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.ui._public.ImageActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.book.adapter.GrammarRecyclerViewAdapter
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing.ExerciseParsingActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.composition.CompositionDetailWebActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import java.io.File

class GrammarFragment : LifeV4Fragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_basicitem_list

    //    val dataList by lazy { arguments?.getSerializable(DATA_LIST) as ArrayList<BookRes> }
    private val activity by lazy { requireActivity() as BasicWordActivity }

    companion object {
        val DATA_LIST = "data_list"
        fun newInstance(): GrammarFragment {
            val f = GrammarFragment()
            val args = Bundle()
//            args.putSerializable(DATA_LIST, dataList)
            f.arguments = args
            return f
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        list.layoutManager = LinearLayoutManager(ctx)
        list.adapter = adapter
    }

    override fun initData() {
        super.initData()

    }

    fun setData(dataList: List<BookRes>) {
        if (dataList.size > 0) {
            view_empty.visibility = View.GONE
            list.visibility = View.VISIBLE
            adapter.setData(dataList)
        } else {
            list.visibility = View.GONE
            view_empty.visibility = View.VISIBLE
        }
    }

    private var adapter = GrammarRecyclerViewAdapter { item, position ->
        resourceJump(item, position)
    }

    /**
     * ????????????????????????
     * 1-paper:?????????2-paperAnalysis:????????????;3-media:??????  4- picture:?????? 5-imagesText:??????;6-document:?????????7-frequency :??????; 8-??????
     */
    private fun resourceJump(item: BookRes, position: Int) {
        activity.saOption("????????????", item.type)
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
                    if ("1" != item.isFinish) {
                        startActivity<ExerciseDetailKActivity>(
                            ExerciseDetailKActivity.KEY to item.id,
                            ExerciseDetailKActivity.NAME to item.name, ExerciseDetailKActivity.CAT_KEY to item.parentKey,
                            ExerciseDetailKActivity.TYPE to item.type,
                            ExerciseDetailKActivity.USER_PRACTISE_KEY to item.userPracticeKey
                        )
                    } else {
                        presenter.getReport(item.id, item.userPracticeKey) {
                            startActivity<ReportActivity>(
                                ReportActivity.DATA to it,
                                ReportActivity.TIME to "",
                                ReportActivity.KEY to item.id,
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
                    startActivity<ImageActivity>(ImageActivity.URL to item.link, ImageActivity.NAME to item.name)
                }
                DownloadBean.TYPE_CONTENT -> {
                    startActivity<WebViewActivity>(WebViewActivity.URL to item.content, WebViewActivity.TITLE to item.name)
                }
                DownloadBean.TYPE_PDF -> {
                    var res = item
                    presenter.getResourceInfo(res.id, "2") {
                        res.playUrl = it.url
                        res.downUrl = it.downloadUrl

                        var p = DownloadManager.getFilePathWithKey(res.id, item.type)
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
                    resList.add(item)
//                        var p = DownloadManager.getFilePathWithKey(item.id, item.type)
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
            }
        }
    }
}
