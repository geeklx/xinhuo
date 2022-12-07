package com.spark.peak.ui.community.circle

import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import kotlinx.android.synthetic.main.activity_community_circledf.*

/**
 * 创建者：
 * 时间：
 */
class CircleActivity(override val layoutResId: Int = R.layout.activity_community_circledf)
    : LifeActivity<CirclePresenter>() {
    override val presenter by lazy { CirclePresenter(this) }

    override fun configView() {

    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}