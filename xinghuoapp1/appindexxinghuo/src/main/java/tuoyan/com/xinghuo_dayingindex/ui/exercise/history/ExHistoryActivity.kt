//package tuoyan.com.xinghuo_daying.ui.exercise.history
//
//import android.app.AlertDialog
//import androidx.recyclerview.widget.LinearLayoutManager
//import kotlinx.android.synthetic.main.activity_ex_history.*
//import org.jetbrains.anko.ctx
//import org.jetbrains.anko.startActivity
//import org.jetbrains.anko.toast
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
//import tuoyan.com.xinghuo_dayingindex.bean.ExerciseHistory
//import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.HistoryAdapter
//import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
//
//class ExHistoryActivity : LifeActivity<ExerciseListPresenter>() {
//    override val presenter: ExerciseListPresenter
//        get() = ExerciseListPresenter(this)
//    override val layoutResId: Int
//        get() = R.layout.activity_ex_history
//
//    val step = 10
//    var page = 1
//    var oAdapter = HistoryAdapter ({
//        var item = it
//        presenter.getReport(it.paperkey ?: "", it.userpractisekey ?: "") {
//            it.userpractisekey = item.userpractisekey ?: ""
//            startActivity<ReportActivity>(ReportActivity.DATA to it,
//                    ReportActivity.TIME to getTimeStr(item.time!!.toInt()),
//                    ReportActivity.KEY to item.paperkey,
//                    ReportActivity.NAME to item.paperName)
//        }
//    }){
//        onLoadMore()
//    }
//
//    override fun configView() {
//        setSupportActionBar(tb_ex_history)
//        rv_ex_history.layoutManager = LinearLayoutManager(ctx)
//    }
//
//    override fun initData() {
//        page = 1
//        presenter.getExerciseHistory(page, step) {
//            oAdapter.setData(it)
//            rv_ex_history.adapter = oAdapter
//        }
//    }
//
//    private val dialog by lazy {
//        AlertDialog.Builder(ctx).setMessage("确定清空所有历史记录吗？").setPositiveButton("确定") { dialogInterface, i ->
//            presenter.clearExerciseHistory {
//                toast("已清空历史记录")
//                initData()
//            }
//        }.setNegativeButton("取消") { dialogInterface, i ->
//            dialogInterface.dismiss()
//        }.create()
//    }
//
//    override fun handleEvent() {
//        tb_ex_history.setNavigationOnClickListener { onBackPressed() }
//
//        tv_clear.setOnClickListener {
//            dialog.show()
//        }
//
//        srl_history.setOnRefreshListener {
//            onRefresh()
//        }
//
//    }
//
//    /**
//     * 下拉刷新
//     */
//    private fun onRefresh() {
//        page = 1
//        presenter.getExerciseHistory(page, step) {
//            oAdapter.setData(it)
//            oAdapter.setMore(true)
//            rv_ex_history.adapter = oAdapter
//            if (srl_history.isRefreshing) srl_history.isRefreshing = false
//        }
//    }
//
//    /**
//     * 上拉加载
//     */
//    private fun onLoadMore() {
//        page++
//        presenter.getExerciseHistory(page, step) {
//            loadMoreData(it)
//        }
//    }
//    private fun loadMoreData(list: List<ExerciseHistory>){
//        if (list.isEmpty()){
//            oAdapter.setMore(false)
//            oAdapter.notifyDataSetChanged()
//        }else{
//            oAdapter.addData(list)
//        }
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
