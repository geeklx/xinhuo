package com.spark.peak.ui.exercise.detail_list

import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.exercise.adapter.ExUnitAdapter
import com.spark.peak.ui.exercise.detail.ExerciseDetailActivity2
import com.spark.peak.ui.exercise.report.ReportActivity
import kotlinx.android.synthetic.main.activity_exercise_list.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class ExerciseListActivity : LifeActivity<ExerciseListPresenter>() {
    override val presenter: ExerciseListPresenter
        get() = ExerciseListPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_exercise_list

    val bookKey by lazy { intent.getStringExtra(BOOK_KEY)?:"" }
    val bookName by lazy { intent.getStringExtra(BOOK_NAME)?:"" }

    var oAdapter : ExUnitAdapter ?= null

    companion object {
        val BOOK_KEY = "book_key"
        val BOOK_NAME = "book_name"
    }
    override fun configView() {
        setSupportActionBar(tb_ex_list)
        rv_ex_list.layoutManager = LinearLayoutManager(ctx)
        tv_subject.text = bookName
    }

    override fun initData() {

    }

    override fun handleEvent() {
        tb_ex_list.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        getExData()
    }

    /**
     * 获取试卷信息
     */
    private fun getExData() {
        presenter.getExerciseList(bookKey) {
            if (oAdapter == null) {
                oAdapter = ExUnitAdapter({ key, name, parentKey ->
                    checkLogin {
                        startActivity<ExerciseDetailActivity2>(ExerciseDetailActivity2.KEY to key,
                                ExerciseDetailActivity2.NAME to name, ExerciseDetailActivity2.PARENT_KEY to parentKey,
                                ExerciseDetailActivity2.BOOK_KEY to bookKey, ExerciseDetailActivity2.TYPE to ExerciseDetailActivity2.TYPE_QYT)
                    }
                }) {
                    var item = it
                    presenter.getReport(it.catalogKey ?: "", it.foreignKey ?: "") {
                        it.userpractisekey = item.foreignKey ?: ""
                        startActivity<ReportActivity>(ReportActivity.DATA to it, ReportActivity.TIME to getTimeStr(item.time!!.toInt()), ReportActivity.KEY to item.catalogKey, ReportActivity.NAME to item.catalogName,
                                ReportActivity.BOOK_KEY to bookKey, ReportActivity.PARENT_KEY to item.parentKey, ReportActivity.TYPE to ExerciseDetailActivity2.TYPE_QYT)
                    }
                }
                oAdapter!!.setData(it.list)
                rv_ex_list.adapter = oAdapter
            }else{
                oAdapter!!.setData(it.list)
            }

        }
    }

    fun getTimeStr(time : Int) : String{
        var hourStr = time/3600
        var minuteStr =  (time%3600)/60
        var secondStr = (time%3600)%60
        return if (hourStr>0) (hourStr.toString()+":") else "" + if (minuteStr>=10) (minuteStr.toString()+":") else "0"+ minuteStr.toString() + ":" + if (secondStr>=10) (secondStr.toString()+":") else "0"+ secondStr.toString()
    }
}
