package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_eval_card.*
import org.jetbrains.anko.ctx
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSheet
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseModel
import tuoyan.com.xinghuo_dayingindex.bean.Report
import tuoyan.com.xinghuo_dayingindex.bean.SensorsExercise
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing.ExerciseParsingActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.evaluation.EntryAdapter

class EvalCardActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_eval_card


    val dataList by lazy { mutableListOf<ExerciseModel>() }
    val report: Report by lazy { intent.getSerializableExtra(DATA) as Report }

    val key by lazy { intent.getStringExtra(KEY) ?: "" }
    val name by lazy { intent.getStringExtra(NAME) ?: "" }
    val evalState by lazy { intent.getStringExtra(EVAL_STATE) ?: "" }
    val catKey by lazy { intent.getStringExtra(CAT_KEY) ?: "" }
    val report2this by lazy { intent.getBooleanExtra(REPORT2THIS, false) }

    val needUpLoad by lazy { intent.getBooleanExtra(NEED_UP_LOAD, false) }
    private val sensorsData by lazy { SensorsExercise() }

    private val adapter by lazy {
        EntryAdapter { position, item ->
            saOption("查看解析")
            startActivity<ExerciseParsingActivity>(
                ExerciseParsingActivity.KEY to key,
                ExerciseParsingActivity.NAME to name,
                ExerciseParsingActivity.EVAL_KEY to catKey,
                ExerciseParsingActivity.P_KET to report.userpractisekey,
                ExerciseParsingActivity.SELECT_SORT to position,
                ExerciseParsingActivity.EX_TYPE to 2
            )
        }
    }

    companion object {
        const val DATA = "data"

        const val KEY = "key"
        const val NAME = "name"
        const val CAT_KEY = "cat_key" //TODO 某些做题类型下 需要传相关的key

        const val EVAL_STATE = "evalState" //测评判卷状态 1 判卷完成 0、2 判卷中， 仅在测评时存在

        const val NEED_UP_LOAD = "needUpLoad" //过级包测评 需要上传照片提供阅卷的情况
        const val REPORT2THIS = "REPORT2THIS" //测评报告跳转到当前页面
    }

    override fun configView() {
        sensorsData.test_paper_id = key
        sensorsData.paper_name = name
        sensorsData.section = report.gradeName ?: ""
        sensorsData.is_there_a_score = "1" == report.scoreSwitch
        sensorsData.number_of_topics = report.totalcount
        sensorsData.test_paper_form = "包含大题"
        sensorsData.presentation_form_paper = "测评"
        tv_title.text = name
        recycler_view.layoutManager = GridLayoutManager(ctx, 3)
        recycler_view.adapter = adapter
        when (evalState) {
//            null -> {
//                //TODO evalState为空 判定为从交卷入口进入，根据needUpload判断显示 查看测评报告或 判卷中
//                if (needUpLoad) {
//                    btn_check.isEnabled = false
//                    btn_check.text = "判卷中..."
//                    btn_check.background = resources.getDrawable(R.drawable.bg_shape_5_c3c7cb)
//                } else {
//                    btn_check.isEnabled = true
//                    btn_check.text = "查看测评报告"
//                    btn_check.background = resources.getDrawable(R.drawable.bg_shape_5_4c84ff)
//                }
//            }
            "1" -> {
                btn_check.isEnabled = true
                //TODO 阅卷完成则按钮显示 “查看测评报告”
                btn_check.text = "查看测评报告"
                btn_check.background = resources.getDrawable(R.drawable.bg_shape_5_4c84ff)
            }
            else -> {
                btn_check.text = "判卷中..."
                btn_check.isEnabled = false
                btn_check.background = resources.getDrawable(R.drawable.bg_shape_5_c3c7cb)
            }
        }
    }

    override fun handleEvent() {
        btn_check.setOnClickListener {
            if (report2this) {
                //如果从测评报告过来
                finish()
            } else {
                startActivity<BookReportActivity>(
                    BookReportActivity.USERPRACTISEKEY to report.userpractisekey,
                    BookReportActivity.PAPERKEY to key,
                    BookReportActivity.EVALKEY to catKey,
                    BookReportActivity.PAPER_NAME to name,
                    BookReportActivity.ANSWER_TYPE to "1",
                    BookReportActivity.EVAL2THIS to true
                )
            }
        }
    }

    override fun initData() {
        initList()
        adapter.setData(dataList)
    }

    /**
     * report返回的answersheet，构建显示列表数据
     */
    private fun initList() {
        var index = 1
        report.answersheet.forEach {
            var item = it
            if ("1" != item.isSubtitle) {
                dataList.add(ExerciseModel("NODE", item.groupName))//TODO 添加一条节点数据
            }
            item.questionlist?.forEach {
                var ex = it //题目的结构
                if (it.questionlist != null && it.questionlist.isNotEmpty()) { //如果当前题目中存在小题（材料题），则解析该题结构
                    it.questionlist.forEach {
                        var model = ExerciseModel("DATA", item.groupName)
                        model.sort = it.questionSort
                        model.userAnswer = it.userAnswer ?: ""
                        model.index = index
                        setQState(model, it)
                        dataList.add(model)
                        index += 1
                    }
                } else { //如果当前题目中没有小题（单选题,主观题），直接使用当前数据
                    var model = ExerciseModel("DATA", item.groupName)
                    model.sort = it.questionSort
                    model.userAnswer = it.userAnswer ?: ""
                    model.index = index
                    setQState(model, it)
                    dataList.add(model)
                    index += 1
                }
            }
        }
    }

    private fun setQState(model: ExerciseModel, answer: AnswerSheet) {
        if (answer.questionType == "6") {//主观题根据阅卷方式显示得分或等待中
            if (answer.userAnswer.isNullOrEmpty()) {
                model.state = "6"
                model.userAnswer = "等待中..."
            } else {
                model.state = if (answer.isRight == null) "5" else "3"
                model.userAnswer = answer.userAnswer ?: ""
            }
        } else {
            when {
                answer.isRight == "0" -> model.state = "4"
                answer.isRight == "1" -> model.state = "3"
                answer.isRight == null -> model.state = "5"
            }
        }
    }

    fun saOption(name: String) {
        try {
            val property = JSONObject(Gson().toJson(sensorsData))
            property.put("button_name", name)
            SensorsDataAPI.sharedInstance().track("submit_order", property)
        } catch (e: Exception) {
        }
    }
}
