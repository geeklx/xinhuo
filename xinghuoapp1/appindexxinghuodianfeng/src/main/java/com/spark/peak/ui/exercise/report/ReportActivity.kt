package com.spark.peak.ui.exercise.report

import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Html
import android.view.View
import com.spark.peak.R
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.Report
import com.spark.peak.ui.exercise.adapter.ASTypeAdapter
import com.spark.peak.ui.exercise.detail.ExerciseDetailActivity2
import com.spark.peak.ui.exercise.parsing.ExerciseParsingActivity
import kotlinx.android.synthetic.main.activity_report.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class ReportActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_report


    val report: Report by lazy { intent.getSerializableExtra(DATA) as Report }
    val time by lazy { intent.getStringExtra(TIME)?:"" }

    val key by lazy { intent.getStringExtra(KEY)?:"" }
    val name by lazy { intent.getStringExtra(NAME)?:"" }

    val parentKey by lazy { intent.getStringExtra(PARENT_KEY)?:"" }
    val bookKey by lazy { intent.getStringExtra(BOOK_KEY)?:"" }
    val type by lazy { intent.getStringExtra(TYPE)?:"" }

    companion object {
        const val DATA = "data"
        const val TIME = "time"

        const val KEY = "key"
        const val NAME = "name"

        const val PARENT_KEY = "parentKey"
        const val BOOK_KEY = "bookKey"

        const val TYPE = "type"
    }


    override fun configView() {
        setSupportActionBar(tb_report)
        tb_report.setNavigationOnClickListener { onBackPressed() }
        tv_title.text = name
    }

    override fun initData() {
        setData()
        rv_answer_card.layoutManager = LinearLayoutManager(ctx)
        var oAdapter = ASTypeAdapter {
            startActivity<ExerciseParsingActivity>(ExerciseParsingActivity.KEY to key,
                    ExerciseParsingActivity.NAME to name,
                    ExerciseParsingActivity.P_KET to report.userpractisekey,
                    ExerciseParsingActivity.SELECT_SORT to it)
        }
        oAdapter.setData(report.answersheet)
        rv_answer_card.adapter = oAdapter
        rv_answer_card.isFocusable = false
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_check.setOnClickListener {
            startActivity<ExerciseParsingActivity>(ExerciseParsingActivity.KEY to key,
                    ExerciseParsingActivity.NAME to name,
                    ExerciseParsingActivity.P_KET to report.userpractisekey)
        }
        tv_continue.setOnClickListener {
            startActivity<ExerciseDetailActivity2>(ExerciseDetailActivity2.KEY to key,
                    ExerciseDetailActivity2.NAME to name,
                    ExerciseDetailActivity2.PARENT_KEY to parentKey,
                    ExerciseDetailActivity2.BOOK_KEY to bookKey,
                    ExerciseDetailActivity2.TYPE to type)
            onBackPressed()
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
            tv_right_ret.text = report.accuracy + "%"
        }
        tv_time.text = if (time.endsWith(":")) time.substring(0, time.length - 1) else time
        var reportMark = "共" + report.totalcount + "题，答对<font color='#4cd964'>" + report.sucs + "</font>题，答错<font color='#fa7062'>" + report.errs + "</font>题，未答<font color='#b4b4b4'>" + (report.totalcount.toInt() - report.sucs.toInt() - report.errs.toInt()) + "</font>题"
        tv_report_mark.text = Html.fromHtml(reportMark)
    }
}
