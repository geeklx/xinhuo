package com.spark.peak.ui._public

import com.spark.peak.R
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.LifeActivity
import kotlinx.android.synthetic.main.activity_local_pdfdf.*
import java.io.File

class LocalPdfActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_local_pdfdf

    private val pdfFile by lazy { intent.getStringExtra(PATH) ?: "" }
    private val name by lazy { intent.getStringExtra(NAME) ?: "" }

    override fun configView() {
        super.configView()
        tv_title.text = name
    }

    override fun initData() {
        super.initData()
        pdf_view.fromFile(File(pdfFile)).enableSwipe(true)
                .defaultPage(0).swipeHorizontal(false)
                .load()
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    companion object {
        val PATH = "path"
        val NAME = "name"
    }
}