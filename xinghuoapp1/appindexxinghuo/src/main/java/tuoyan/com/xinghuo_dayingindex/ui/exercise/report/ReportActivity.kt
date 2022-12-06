package tuoyan.com.xinghuo_dayingindex.ui.exercise.report

import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.graphics.Color
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_report_normal.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.ctx
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.TYPE_EX
import tuoyan.com.xinghuo_dayingindex.TYPE_TEST
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSheet
import tuoyan.com.xinghuo_dayingindex.bean.Report
import tuoyan.com.xinghuo_dayingindex.bean.SensorsExercise
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ASTypeAdapter
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing.ExerciseParsingActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.special.SpecialDataManager.questionList
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

class ReportActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_report_normal


    val report: Report by lazy { intent.getSerializableExtra(DATA) as Report }
    val time by lazy { intent.getStringExtra(TIME) ?: "" }

    val key by lazy { intent.getStringExtra(KEY)?:"" }
    val name by lazy { intent.getStringExtra(NAME)?:"" }
    val exType by lazy { intent.getIntExtra(EX_TYPE, EX_TYPE_0) }
    val typeName by lazy { intent.getStringExtra(TYPE) ?: TYPE_EX }
    val catKey by lazy { intent.getStringExtra(CAT_KEY) ?:""}
    val praKey by lazy { intent.getStringExtra(PRA_KEY) ?:""}
    val spQKey by lazy { intent.getStringExtra(SP_Q_KEY)?:"" }

    val spGName by lazy { intent.getStringExtra(SP_G_NAME)?:"" }
    val evalState by lazy { intent.getStringExtra(EVAL_STATE)?:"" }

    val needUpLoad by lazy { intent.getBooleanExtra(NEED_UP_LOAD, false) }
    val isHideBottom by lazy { intent.getBooleanExtra(IS_HIDE_BOTTOM, false) }
    val sensorsExercise by lazy { intent.getParcelableExtra("SensorsExercise") as? SensorsExercise }


    companion object {
        const val DATA = "data"
        const val TIME = "time"

        const val KEY = "key"
        const val NAME = "name"
        const val CAT_KEY = "cat_key" //TODO 某些做题类型下 需要传相关的key
        const val PRA_KEY = "pra_key"
        const val SP_Q_KEY = "sp_q_key"//TODO 专项练习下存在
        const val SP_G_NAME = "sp_g_name"//TODO 专项练习下存在

        const val EX_TYPE = "ex_type"//做题类型
        const val EX_TYPE_0 = 0 //正常做题（最普通的类型）

        @Deprecated("没有专项这一类型了")
        const val EX_TYPE_SP = 1 //专项练习类型
        const val EX_TYPE_PG = 2 //过级包类型
        const val EX_TYPE_WORK = 3 //网课课后作业

        const val TYPE = "TYPE"

        const val EVAL_STATE = "evalState" //测评判卷状态 1 判卷完成 0、2 判卷中， 仅在测评时存在

        const val NEED_UP_LOAD = "needUpLoad" //过级包测评 需要上传照片提供阅卷的情况
        const val IS_HIDE_BOTTOM = "IS_HIDE_BOTTOM" //测评报告跳转到成绩报告
    }


    override fun configView() {
        setSupportActionBar(tb_report)
        tb_report.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        if (typeName == TYPE_TEST) {
            iv_share.visibility = View.GONE
        }
        setData()
        rv_answer_card.layoutManager = LinearLayoutManager(ctx)
        var oAdapter = ASTypeAdapter { sort, index ->
            saOption("试卷中查看解析")
            startActivity<ExerciseParsingActivity>(
                ExerciseParsingActivity.KEY to key,
                ExerciseParsingActivity.NAME to name,
                ExerciseParsingActivity.P_KET to report.userpractisekey,
                ExerciseParsingActivity.SELECT_SORT to if (exType == EX_TYPE_SP) (index + 1) else sort,
                ExerciseParsingActivity.EX_TYPE to exType,
                ExerciseParsingActivity.EVAL_KEY to catKey,
                ExerciseParsingActivity.SP_Q_KEY to spQKey,
                ExerciseParsingActivity.SP_G_NAME to spGName
            )
        }
        if (exType == EX_TYPE_SP) {
            var list = mutableListOf<AnswerSheet>()
            var answer = AnswerSheet(report.answersheet, name, "", "")
            list.add(answer)
            oAdapter.setData(list)
        } else {
            oAdapter.setData(setIndex(report.answersheet))
        }
        rv_answer_card.adapter = oAdapter
        rv_answer_card.isFocusable = false
    }

    var index = 1;
    fun setIndex(answerSheets: List<AnswerSheet>): List<AnswerSheet> {
        answerSheets.forEach {
            if (it.questionlist.isNullOrEmpty()) {
                it.index = index;
                index += 1;
            } else {
                setIndex(it.questionlist)
            }
        }
        return answerSheets
    }

    override fun handleEvent() {
        super.handleEvent()
        iv_share.setOnClickListener {
            report?.let {
                ShareDialog(ctx) {
                    ShareUtils.share(
                        this, it,
                        "我发现了一个提分利器APP，众多提分技巧，安利给你哦！",
                        "本次练习题共${report.totalcount}题，正确率${report.accuracy}%，用时${tv_time.text}，你也来试试？",
                        "${WEB_BASE_URL}/share?type=3&title=成绩报告&source=${report.totalscore}" +
                                "&time=${time}&userName=${SpUtil.userInfo.name}&rightrate=${report.accuracy}"
                    )
                    // TODO: 2018/11/23 9:41  分享链接
                }.show()
            }

        }

        tv_check.setOnClickListener {
            saOption(tv_check.text.toString())
            startActivity<ExerciseParsingActivity>(
                ExerciseParsingActivity.KEY to key,
                ExerciseParsingActivity.NAME to name,
                ExerciseParsingActivity.P_KET to report.userpractisekey,
                ExerciseParsingActivity.EX_TYPE to exType,
                ExerciseParsingActivity.SP_Q_KEY to spQKey,
                ExerciseParsingActivity.SP_G_NAME to spGName
            )
        }
        tv_continue.setOnClickListener {
            saOption(tv_continue.text.toString())
            if (typeName == TYPE_TEST) {
                // 跳转过级包测评报告页
                startActivity<BookReportActivity>(
                    BookReportActivity.EVALKEY to catKey,
                    BookReportActivity.PAPERKEY to key,//试卷key
                    BookReportActivity.USERPRACTISEKEY to report.userpractisekey,
                    BookReportActivity.NEED_UP_LOAD to needUpLoad,
                    BookReportActivity.SP_Q_KEY to spQKey,
                    BookReportActivity.ANSWER_TYPE to "0"
                )

            } else {
                startActivity<ExerciseDetailKActivity>(
                    ExerciseDetailKActivity.KEY to key,
                    ExerciseDetailKActivity.NAME to name,
                    ExerciseDetailKActivity.CAT_KEY to catKey,
//                    ExerciseDetailKActivity.PRA_KEY to praKey,
                    ExerciseDetailKActivity.EX_TYPE to exType,
                    ExerciseDetailKActivity.TYPE to typeName
                )
                onBackPressed()
            }
        }
    }

    fun setData() {
        if (report.totalscore == "0") {
            scor.text = "正确率"
            tv_scor.text = report.accuracy
            tv_scor_mark.text = "%"
            rl_mark_left.visibility = View.GONE
        } else {
            tv_scor.text = report.totalscore
            tv_right_ret.text = "${report.accuracy}%"
        }
        if (time.isNotEmpty()) {
            tv_time.text = if (time.endsWith(":")) time.substring(0, time.length - 1) else time
        } else {
            try {
                tv_time.text = "${formatNumber(report.time.toInt() / 60)}:${formatNumber(report.time.toInt() % 60)}"
            } catch (e: Exception) {
            }
        }
        val reportMark =
            "共" + report.totalcount + "题，答对<font color='#4cd964'>" + report.sucs + "</font>题，答错<font color='#fa7062'>" + report.errs + "</font>题，未答<font color='#b4b4b4'>" + (report.totalcount - report.sucs.toInt() - report.errs.toInt()) + "</font>题"
        tv_report_mark.text = Html.fromHtml(reportMark)

        if (exType == EX_TYPE_SP) {
            haveNext()
            changeText()
            changeListState()
        } else if (typeName == TYPE_TEST) {
            rl_scor.visibility = View.GONE
            ll_mark.visibility = View.GONE
            if (isHideBottom) {
                ll_bottom.visibility = View.GONE
            } else {
                tv_check.visibility = View.GONE
                if (evalState == "1") {
                    tv_continue.text = "查看测评报告"
                    tv_continue.isEnabled = true
                } else {
                    tv_continue.text = "批改中"
                    tv_continue.backgroundColor = Color.parseColor("#c3c7cb")
                    tv_continue.isEnabled = false
                }
            }
        }
    }

    private fun formatNumber(num: Int): String {
        return if (num < 10) "0${num}" else "$num"
    }

    /**
     * 专项练习模式下，若果存在下一个未做的题目，则显示”下一题“，并点击跳转
     */
    var nextIndex = -1

    private fun haveNext() {
        var index = -1
        (0 until questionList.size).forEach {
            if (questionList[it].questionKey == spQKey) {
                index = it
            }
            if (index != -1 && it > index && (questionList[it].userPracticeKey == null || questionList[it].userPracticeKey == "")) {
                nextIndex = it
                return
            }
        }

        if (nextIndex == -1) {
            (0 until index).forEach {
                if (questionList[it].userPracticeKey == null || questionList[it].userPracticeKey == "") {
                    nextIndex = it
                    return
                }
            }
        }
    }

    /**
     * 根据是否有专项练习的下一题，修改右下方按钮的文字等
     */
    private fun changeText() {
        if (nextIndex == -1) {
            tv_continue.text = "没有下一题"
            tv_continue.backgroundColor = Color.parseColor("#cbcbcb")
            tv_continue.isEnabled = false
        } else {
            tv_continue.text = "下一题\n" + questionList[nextIndex].groupName
            tv_continue.isEnabled = true
        }
    }

    /**
     * 修改前面存储的当前题型列表的已做状态
     */
    private fun changeListState() {
        questionList.forEach {
            if (it.groupName == name) {
                it.userPracticeKey = report.userpractisekey
            }
        }
    }

    fun saOption(name: String) {
        try {
            val property = JSONObject(Gson().toJson(sensorsExercise))
            property.put("button_name", name)
            SensorsDataAPI.sharedInstance().track("submit_order", property)
        } catch (e: Exception) {
        }
    }
}
