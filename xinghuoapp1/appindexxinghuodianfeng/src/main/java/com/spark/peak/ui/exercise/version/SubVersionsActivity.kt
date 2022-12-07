package com.spark.peak.ui.exercise.version

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.ExBooks
import com.spark.peak.bean.Version
import com.spark.peak.ui.exercise.adapter.VersionBooksAdapter
import com.spark.peak.ui.exercise.adapter.VersionsAdapter
import com.spark.peak.ui.exercise.detail_list.ExerciseListActivity
import com.spark.peak.ui.exercise.history.ExHistoryActivity
import kotlinx.android.synthetic.main.activity_sub_versionsdf.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class SubVersionsActivity : LifeActivity<SubVersionPresenter>() {
    override val presenter: SubVersionPresenter
        get() = SubVersionPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_sub_versionsdf

    private val subject by lazy { intent.getStringExtra(SUB_NAME)?:"" }
    private val subjectKey by lazy { intent.getStringExtra(SUB_KEY)?:"" }

    var versions = mutableListOf<Version>()
    var exBooks = mutableListOf<ExBooks>()
    companion object {
        val SUB_NAME = "sub_name"
        val SUB_KEY = "sub_key"

    }
    override fun configView() {
        setSupportActionBar(tb_sub_version)
        tv_title.text = subject

        rv_versions.layoutManager = LinearLayoutManager(ctx)

        rv_books.layoutManager = GridLayoutManager(ctx, 2)
    }

    override fun initData() {
        presenter.getVersions(subjectKey){
            if (it.isNotEmpty()) {
                initBooks(it[0].key?:"")
                versions.addAll(it)
                var oAdapter = VersionsAdapter{
                    initBooks(it)
                }
                oAdapter.setData(it)
                rv_versions.adapter = oAdapter
            }
        }
    }


    /**
     * 请求图书列表
     */
    private fun initBooks(key : String){
        presenter.getBooks(key,1,1000){
            var oAdapter = VersionBooksAdapter{
                startActivity<ExerciseListActivity>(ExerciseListActivity.BOOK_KEY to it.key, ExerciseListActivity.BOOK_NAME to it.name)
            }
            oAdapter.setData(it)
            rv_books.adapter = oAdapter
        }
    }
    override fun handleEvent() {
        tb_sub_version.setNavigationOnClickListener { onBackPressed() }
        iv_history.setOnClickListener {
            startActivity<ExHistoryActivity>()
        }
    }
}
