package com.spark.peak.ui.practice

import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.exercise.detail.ExerciseDetailActivity2
import com.spark.peak.ui.practice.adapter.PapersAdapter
import kotlinx.android.synthetic.main.activity_papers.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class PapersActivity(override val layoutResId: Int = R.layout.activity_papers) : LifeActivity<PapersPresenter>() {
    override val presenter = PapersPresenter(this)
    private val key by lazy { intent.getStringExtra(KEY) ?:""}
    private val queue by lazy { intent.getBooleanExtra(QUEUE, false) }
    val title: String by lazy { intent.getStringExtra(TITLE) ?:""}

    companion object {
        const val KEY = "key" //关卡主键
        const val TITLE = "title"
        const val QUEUE = "queue"
    }

    private val adapter by lazy {
        PapersAdapter(queue) {
            startActivity<ExerciseDetailActivity2>(ExerciseDetailActivity2.KEY to it["paperkey"],
                    ExerciseDetailActivity2.NAME to it["papername"],
                    ExerciseDetailActivity2.BOOK_KEY to it["missionkey"],
                    ExerciseDetailActivity2.PARENT_KEY to key,
                    ExerciseDetailActivity2.TYPE to ExerciseDetailActivity2.TYPE_DFXL)
        }
    }

    override fun configView() {
        setSupportActionBar(tb_papers)
        rv_papers.layoutManager = LinearLayoutManager(ctx)
        tv_title.text = title
        rv_papers.adapter = adapter
//        var oAdapter = PapersAdapter{
//
//        }
//        var dataList = mutableListOf<Int>()
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//
//        oAdapter.setData(dataList)

//        rv_papers.adapter = oAdapter
    }

    override fun handleEvent() {
        tb_papers.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
//        presenter.cgPapers(key) {
//            adapter.setData(it.body.paperList)
//        }

    }

    override fun onResume() {
        super.onResume()
        presenter.cgPapers(key) {
            adapter.setData(it.body.paperList)
        }

    }
}
