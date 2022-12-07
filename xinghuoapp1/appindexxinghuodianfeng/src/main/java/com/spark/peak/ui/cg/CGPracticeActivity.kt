package com.spark.peak.ui.cg


import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_cg_practice.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class CGPracticeActivity(override val layoutResId: Int = R.layout.activity_cg_practice) : LifeActivity<CGPresenter>() {
    override val presenter by lazy { CGPresenter(this) }
//    private val key by lazy { intent.getStringExtra(KEY) }
    private var page = 0
    private val adapter by lazy {
        CGPracticeAdapter({
            loadMore()
        }) {
            startActivity<CGPassActivity>(CGPassActivity.KEY to (it["key"] ?: ""),
                    CGPassActivity.TITLE to (it["name"] ?: ""))
        }
    }

    private fun loadMore() {
        page++
        presenter.cgPractice(SpUtil.userInfo.grade ?: SpUtil.defaultGrade.id, page) {
            adapter.addData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
    }

    override fun configView() {
        rv_cg_practice.layoutManager = LinearLayoutManager(ctx)
        rv_cg_practice.adapter = adapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
//        adapter.setData(mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1))
        page = 0
        presenter.cgPractice(SpUtil.userInfo.grade ?: SpUtil.defaultGrade.id, page) {
            if (it.body.isNotEmpty() && it.totalCount == 1) {
                startActivity<CGPassActivity>(CGPassActivity.KEY to (it.body[0]["key"] ?: ""),
                        CGPassActivity.TITLE to (it.body[0]["name"] ?: ""))
                onBackPressed()
                return@cgPractice
            }
            adapter.setData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
    }



    companion object {
        const val KEY = "key"
    }
}
