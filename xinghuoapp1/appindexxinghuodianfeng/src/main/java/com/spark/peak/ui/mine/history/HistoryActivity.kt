package com.spark.peak.ui.mine.history

import android.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.exercise.detail.ExerciseDetailActivity
import com.spark.peak.ui.exercise.report.ReportActivity
import kotlinx.android.synthetic.main.activity_historydf.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class HistoryActivity(override val layoutResId: Int = R.layout.activity_historydf)
    : LifeActivity<HistoryPresenter>() {
    override val presenter = HistoryPresenter(this)
    private var page = 0
    private val adapter by lazy {
        HistoryAdapter({
            var item = it
            presenter.getReport(it.paperkey ?: "", it.userpractisekey ?: "") {
                it.userpractisekey = item.userpractisekey ?: ""
                startActivity<ReportActivity>(ReportActivity.DATA to it,
                        ReportActivity.TIME to getTimeStr(item.time!!.toInt()),
                        ReportActivity.KEY to item.paperkey, ReportActivity.NAME to item.paperName,
                        ReportActivity.BOOK_KEY to item.practicekey,
                        ReportActivity.PARENT_KEY to item.catalogKey,
                        ReportActivity.TYPE to ExerciseDetailActivity.TYPE_QYT)
            }
        }) {
            loadMore()
        }
    }

    private fun loadMore() {
        page++
        presenter.history(page) {
            adapter.addData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
    }

    override fun configView() {
        rv_ex_history.layoutManager = LinearLayoutManager(ctx)
        rv_ex_history.adapter = adapter
    }

    override fun handleEvent() {
        tb_ex_history.setNavigationOnClickListener { onBackPressed() }
        tv_clear.setOnClickListener {
            dialog.show()
        }
    }

    override fun initData() {
        page = 0
        presenter.history(page) {
            adapter.setData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
    }

    private val dialog by lazy {
        AlertDialog.Builder(ctx).setMessage("确定清空所有历史记录吗？").setPositiveButton("确定") { _, _ ->
            presenter.clearExerciseHistory {
                toast("已清空历史记录")
                initData()
            }
        }.setNegativeButton("取消") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }.create()
    }

    fun getTimeStr(time: Int): String {
        var hourStr = time / 3600
        var minuteStr = (time % 3600) / 60
        var secondStr = (time % 3600) % 60
        return if (hourStr > 0) (hourStr.toString() + ":") else "" + if (minuteStr >= 10) (minuteStr.toString() + ":") else "0" + minuteStr.toString() + ":" + if (secondStr >= 10) (secondStr.toString() + ":") else "0" + secondStr.toString()
    }

}
