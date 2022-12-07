package com.spark.peak.ui.mine.coupon.detail

import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.utlis.ItemDecoration
import kotlinx.android.synthetic.main.activity_coupon_detail.*
import org.jetbrains.anko.*

/**
 * 创建者：
 * 时间：
 */
class CouponDetailActivity(override val layoutResId: Int = R.layout.activity_coupon_detail)
    : LifeActivity<CouponDetailPresenter>() {
    override val presenter by lazy { CouponDetailPresenter(this) }
    private val key by lazy { intent.getStringExtra(KEY)?:"" }
    private val adapter = CouponDetailAdapter()


    override fun configView() {
        rv_coupon.layoutManager = LinearLayoutManager(ctx)
        rv_coupon.addItemDecoration(ItemDecoration(dip(15)))
        rv_coupon.adapter = adapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }

    }

    override fun initData() {
        presenter.couponDetail(key) {
            tv_name.text = it.name
            tv_price.text = it.facevalue.toString()
            tv_time.text = "${it.validitytime}到期"
            tv_description.text = it.content
            adapter.setData(it.goodslist)
        }
    }

    companion object {
        const val KEY = "key"
    }
}