package com.spark.peak.ui.scan

import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.home.HomePresenter
import com.spark.peak.ui.scan.adapter.ScanHistoryAdapter
import com.spark.peak.ui.study.book.BookDetailActivity
import kotlinx.android.synthetic.main.activity_scan_history.*
import org.jetbrains.anko.startActivity

class ScanHistoryActivity : LifeActivity<HomePresenter>() {
    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_scan_history

    private val scanHistoryAdapter by lazy {
        ScanHistoryAdapter {
            when (it.type) {
                "1" -> {
                    startActivity<PostActivity>(PostActivity.URL to it.key, PostActivity.TITLE to "")
                }
                "2" -> {
                    startActivity<BookDetailActivity>(BookDetailActivity.KEY to it.key, BookDetailActivity.TYPE to "1")
                }
            }
        }
    }

    override fun configView() {
        setSupportActionBar(toolbar)
        rlv_scan_history.layoutManager = LinearLayoutManager(this)
        rlv_scan_history.adapter = scanHistoryAdapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        super.initData()
        presenter.getUserQrcodeRecord() {
            scanHistoryAdapter.setData(it)
        }
    }


}
