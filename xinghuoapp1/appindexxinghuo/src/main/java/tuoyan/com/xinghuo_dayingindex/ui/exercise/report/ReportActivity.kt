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
        const val CAT_KEY = "cat_key" //TODO ????????????????????? ??????????????????key
        const val PRA_KEY = "pra_key"
        const val SP_Q_KEY = "sp_q_key"//TODO ?????????????????????
        const val SP_G_NAME = "sp_g_name"//TODO ?????????????????????

        const val EX_TYPE = "ex_type"//????????????
        const val EX_TYPE_0 = 0 //????????????????????????????????????

        @Deprecated("???????????????????????????")
        const val EX_TYPE_SP = 1 //??????????????????
        const val EX_TYPE_PG = 2 //???????????????
        const val EX_TYPE_WORK = 3 //??????????????????

        const val TYPE = "TYPE"

        const val EVAL_STATE = "evalState" //?????????????????? 1 ???????????? 0???2 ???????????? ?????????????????????

        const val NEED_UP_LOAD = "needUpLoad" //??????????????? ???????????????????????????????????????
        const val IS_HIDE_BOTTOM = "IS_HIDE_BOTTOM" //?????????????????????????????????
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
            saOption("?????????????????????")
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
                        "??????????????????????????????APP??????????????????????????????????????????",
                        "??????????????????${report.totalcount}???????????????${report.accuracy}%?????????${tv_time.text}?????????????????????",
                        "${WEB_BASE_URL}/share?type=3&title=????????????&source=${report.totalscore}" +
                                "&time=${time}&userName=${SpUtil.userInfo.name}&rightrate=${report.accuracy}"
                    )
                    // TODO: 2018/11/23 9:41  ????????????
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
                // ??????????????????????????????
                startActivity<BookReportActivity>(
                    BookReportActivity.EVALKEY to catKey,
                    BookReportActivity.PAPERKEY to key,//??????key
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
            scor.text = "?????????"
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
            "???" + report.totalcount + "????????????<font color='#4cd964'>" + report.sucs + "</font>????????????<font color='#fa7062'>" + report.errs + "</font>????????????<font color='#b4b4b4'>" + (report.totalcount - report.sucs.toInt() - report.errs.toInt()) + "</font>???"
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
                    tv_continue.text = "??????????????????"
                    tv_continue.isEnabled = true
                } else {
                    tv_continue.text = "?????????"
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
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????
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
     * ???????????????????????????????????????????????????????????????????????????
     */
    private fun changeText() {
        if (nextIndex == -1) {
            tv_continue.text = "???????????????"
            tv_continue.backgroundColor = Color.parseColor("#cbcbcb")
            tv_continue.isEnabled = false
        } else {
            tv_continue.text = "?????????\n" + questionList[nextIndex].groupName
            tv_continue.isEnabled = true
        }
    }

    /**
     * ??????????????????????????????????????????????????????
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
