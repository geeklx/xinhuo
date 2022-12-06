//package tuoyan.com.xinghuo_daying.ui.mine.history
//
//import android.app.AlertDialog
//import androidx.recyclerview.widget.LinearLayoutManager
//import android.view.View
//import kotlinx.android.synthetic.main.activity_history.*
//import org.jetbrains.anko.ctx
//import org.jetbrains.anko.startActivity
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
//import tuoyan.com.xinghuo_dayingindex.ui.dialog.WrongDateAdapter
//import tuoyan.com.xinghuo_dayingindex.ui.dialog.WrongDateDialog
//import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
//
//class HistoryActivity(override val layoutResId: Int = R.layout.activity_history)
//    : LifeActivity<HistoryPresenter>() {
//    override val presenter = HistoryPresenter(this)
//    private var page = 0
//    private var year = ""
//    private var data = ""
//    private val adapter by lazy {
//        HistoryAdapter({ it ->
//            var item = it
//            presenter.getReport(it.paperkey ?: "", it.userpractisekey ?: "") {
//                it.userpractisekey = item.userpractisekey ?: ""
//                startActivity<ReportActivity>(ReportActivity.DATA to it,
//                        ReportActivity.TIME to getTimeStr(item.time!!.toInt()),
//                        ReportActivity.KEY to item.paperkey, ReportActivity.NAME to item.paperName)
//            }
//        }) {
//            loadMore()
//        }
//    }
//    private val dateAdapter by lazy {
//        WrongDateAdapter(str="套") {
//            data = it.time ?: ""
//            var count = it.count
//            tv_date.text = "$data($count 套试卷)"
//            year = data.replace("-", "")
//            selectedDate()
//        }
//    }
//    private val dateDialog by lazy {
//        WrongDateDialog(ctx, dateAdapter)
//    }
//
//    private fun loadMore() {
//        page++
//        presenter.history(page, year) {
//            adapter.addData(it.body)
//            adapter.setMore(adapter.getDateCount() < it.totalCount)
//        }
//    }
//
//    override fun configView() {
//        rv_ex_history.layoutManager = LinearLayoutManager(ctx)
//        rv_ex_history.adapter = adapter
//    }
//
//    override fun handleEvent() {
//        tv_clear.setOnClickListener {
//            if (adapter.getDateCount() == 0) return@setOnClickListener
//            dialog.show()
//        }
//    }
//
//    override fun initData() {
//        presenter.paperHistoryTimes { it ->
//            dateAdapter.setData(it)
//
//            if (it.isNotEmpty()) {
//                data = it[0].time ?: ""
//                val count = it[0].count
//                tv_date.text = "$data($count 套试卷)"
//                year = it[0].time?.replace("-", "") ?: ""
//            } else {
//                data = ""
//                tv_date.text = ""
//                year = ""
//            }
//            history()
//        }
//
//    }
//
//    private fun history() {
//        page = 0
//        presenter.history(page, year) {
//            adapter.setData(it.body)
//            adapter.setMore(adapter.getDateCount() < it.totalCount)
//        }
//    }
//
//    fun date(v: View) {
//        dateDialog.show()
//    }
//
//    private fun selectedDate() {
//        dateDialog.dismiss()
//        history()
//    }
//
//
//    private val dialog by lazy {
//        AlertDialog.Builder(ctx).setMessage("确定清空所有历史记录吗？").setPositiveButton("确定") { _, _ ->
//            presenter.clearExerciseHistory {
//                mToast("已清空历史记录")
//                initData()
//            }
//        }.setNegativeButton("取消") { dialogInterface, _ ->
//            dialogInterface.dismiss()
//        }.create()
//    }
//
//    fun getTimeStr(time: Int): String {
//        var hourStr = time / 3600
//        var minuteStr = (time % 3600) / 60
//        var secondStr = (time % 3600) % 60
//        return if (hourStr > 0) (hourStr.toString() + ":") else "" + if (minuteStr >= 10) (minuteStr.toString() + ":") else "0" + minuteStr.toString() + ":" + if (secondStr >= 10) (secondStr.toString() + ":") else "0" + secondStr.toString()
//    }
//
//}
