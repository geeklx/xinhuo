package com.spark.peak.ui.exercise.listen

import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.exercise.ExercisePresenter
import com.spark.peak.ui.exercise.detail.ExerciseDetailActivity2
import com.spark.peak.ui.scan.adapter.ExerciseListAdapter
import kotlinx.android.synthetic.main.activity_scan_historydf.*
import org.jetbrains.anko.startActivity

class ExerciseListActivity : LifeActivity<ExercisePresenter>() {
    override val presenter: ExercisePresenter
        get() = ExercisePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_scan_historydf

    private val specialKey by lazy { intent.getStringExtra("key") ?: "" }
    private val title by lazy { intent.getStringExtra("title") ?: "专项练习" }
    private val exerciseAdapter by lazy {
        ExerciseListAdapter() {
            startActivity<ExerciseDetailActivity2>(
                ExerciseDetailActivity2.KEY to it.paperKey,
                ExerciseDetailActivity2.NAME to it.name,
                ExerciseDetailActivity2.PARENT_KEY to it.specialPaperKey,
                ExerciseDetailActivity2.BOOK_KEY to it.specialKey,
                ExerciseDetailActivity2.BOOK_TITLE to title,
                ExerciseDetailActivity2.SOURCE to "8",
                ExerciseDetailActivity2.TYPE to ExerciseDetailActivity2.TYPE_QYT
            )
        }
    }

    override fun configView() {
        tv_title.text = title
        rlv_scan_history.layoutManager = LinearLayoutManager(this)
        rlv_scan_history.adapter = exerciseAdapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        super.initData()
        presenter.getSpecialPaperList(specialKey) {
            exerciseAdapter.setData(it)
        }
    }
}
