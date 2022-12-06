package tuoyan.com.xinghuo_dayingindex.ui.ebook
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_ebook_report.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSheet
import tuoyan.com.xinghuo_dayingindex.bean.Report
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.ReportAnswerAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordExerciseAnalysisActivity

class EBookReportActivity : EBookLifeActivity<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_report

    private val reportData by lazy { intent.getSerializableExtra(DATA) as? Report }
    private val type by lazy { intent.getStringExtra(TYPE) ?: "1" }
    private val adapter by lazy {
        ReportAnswerAdapter() {
            if (type == "8") {
                bookParam?.questionKey = it.questionKey
                startActivity<EBookWordExerciseAnalysisActivity>(EBOOK_PARAM to bookParam)
            } else {
                bookParam?.questionKey = it.questionKey
                startActivity<EBookExerciseAnalysisActivity>(EBOOK_PARAM to bookParam, EBookExerciseAnalysisActivity.TYPE to type)
            }
        }
    }

    override fun configView() {
        super.configView()
        if (type == "8") {
            tv_from.text = "数据来自本次章节测试"
        }
        rlv_answers.isNestedScrollingEnabled = false
        rlv_answers.layoutManager = LinearLayoutManager(this)
        rlv_answers.adapter = adapter
    }

    override fun initData() {
        super.initData()
        reportData?.let { data ->
            tv_an.visibility = if ("100" == data.accuracy) View.INVISIBLE else View.VISIBLE
            tv_err.visibility = if ("100" == data.accuracy) View.GONE else View.VISIBLE
            tv_all_an.visibility = if ("100" == data.accuracy) View.VISIBLE else View.GONE
            bookParam?.userpractisekey = data.userpractisekey ?: ""

            tv_right_num.text = "${data.sucs}"
            tv_stu_rate.text = "${data.beatRate}%"

            val sheetList = mutableListOf<AnswerSheet>()
            if (type == "8") {
                tv_total.text = "正确率"
                tv_score.text = "${data.accuracy}%"
                tv_right_rate.visibility = View.GONE
                tv_right_rate_1.visibility = View.GONE
                try {
                    pb_circle.max = 100
                    pb_circle.progress = data.accuracy.toFloat().toInt()
                } catch (e: Exception) {
                }
                data.answersheet.forEach { sheetItem ->
                    val sheet = AnswerSheet()
                    val list = mutableListOf<AnswerSheet>()
                    list.addAll(sheetItem.questionlist)
                    sheet.questionlist.addAll(list)
                    sheetList.add(sheet)
                }
            } else {
                tv_total.text = "满分${data.paperScore}"
                tv_score.text = "${data.totalscore}"
                tv_right_rate.text = "${data.accuracy}%"
                try {
                    val max = data.paperScore.toFloat().toInt()
                    pb_circle.max = if (max <= 0) 1 else max
                    pb_circle.progress = data.totalscore.toFloat().toInt()
                } catch (e: Exception) {
                }
                data.answersheet.forEach { sheetItem ->
                    val sheet = AnswerSheet()
                    val list = mutableListOf<AnswerSheet>()
                    sheet.groupName = sheetItem.groupName
                    sheetItem.questionlist.forEach { sheetI ->
                        list.addAll(sheetI.questionlist)
                    }
                    sheet.questionlist.addAll(list)
                    sheetList.add(sheet)
                }
            }
            adapter.setData(sheetList)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        tv_an.setOnClickListener {
            bookParam?.questionKey = ""
            if (type == "8") {
                startActivity<EBookWordExerciseAnalysisActivity>(EBOOK_PARAM to bookParam)
            } else {
                startActivity<EBookExerciseAnalysisActivity>(EBOOK_PARAM to bookParam, EBookExerciseAnalysisActivity.TYPE to type)
            }
        }
        tv_err.setOnClickListener {
            bookParam?.questionKey = ""
            if (type == "8") {
                startActivity<EBookWordExerciseAnalysisActivity>(EBOOK_PARAM to bookParam, EBookWordExerciseAnalysisActivity.ANALYZE_TYPE to "1")
            } else {
                startActivity<EBookExerciseAnalysisActivity>(EBOOK_PARAM to bookParam, EBookExerciseAnalysisActivity.ANALYZE_TYPE to "1", EBookExerciseAnalysisActivity.TYPE to type)
            }
        }
        tv_all_an.setOnClickListener {
            bookParam?.questionKey = ""
            if (type == "8") {
                startActivity<EBookWordExerciseAnalysisActivity>(EBOOK_PARAM to bookParam)
            } else {
                startActivity<EBookExerciseAnalysisActivity>(EBOOK_PARAM to bookParam, EBookExerciseAnalysisActivity.TYPE to type)
            }
        }
    }

    companion object {
        const val DATA = "reportData"

        /**
         * 同 EbookListenExerciseActivity MaterialQesAnalysisFragment ChoiceQesAnalysisFragment EBookExerciseAnalysisActivity
         * type 1:点读书模考解析；2:点读书精练 听力练习做题状态；3：精练听力练习 解析状态 4:简听力  基础篇（练习）
         * 5:简听力 进阶篇 特训篇（试卷） 6:简听力  基础篇（练习） 解析状态 7：简听力 进阶篇 特训篇（试卷）解析状态
         * 8:巧记速记 章节测试
         */
        const val TYPE = "type"
    }
}