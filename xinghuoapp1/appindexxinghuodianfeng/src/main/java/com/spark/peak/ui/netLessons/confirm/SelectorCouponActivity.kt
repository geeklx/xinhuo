package com.spark.peak.ui.netLessons.confirm

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.utlis.ItemDecoration
import com.spark.peak.R
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.Coupon
import com.spark.peak.ui.mine.coupon.CouponAdapter
import kotlinx.android.synthetic.main.activity_selector_coupondf.*
import org.jetbrains.anko.*

/**
 * 创建者：
 * 时间：
 */
class SelectorCouponActivity(override val layoutResId: Int = R.layout.activity_selector_coupondf)
    : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    private val coupons by lazy { intent.getSerializableExtra(COUPONS) as List<Coupon> }
    private val adapter = CouponAdapter {
        val intent = Intent()
        intent.putExtra("coupon", it)
        setResult(0, intent)
    }


    override fun configView() {
        rv_coupon.layoutManager = LinearLayoutManager(ctx)
        rv_coupon.addItemDecoration(ItemDecoration(dip(15)))
        rv_coupon.adapter = adapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }

    }

    override fun initData() {
        adapter.setMore(false)
        adapter.setData(coupons)
//        presenter.couponList(status) { total, body ->
//            adapter.setData(body)
//            adapter.setMore(total > adapter.getDateCount())
//        }
    }

    companion object {
        const val COUPONS = "coupons"
    }

}