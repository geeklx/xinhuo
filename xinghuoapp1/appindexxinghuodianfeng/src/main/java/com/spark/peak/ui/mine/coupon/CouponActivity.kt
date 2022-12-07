package com.spark.peak.ui.mine.coupon

import com.google.android.material.tabs.TabLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.ui.mine.coupon.detail.CouponDetailActivity
import com.spark.peak.utlis.ItemDecoration
import kotlinx.android.synthetic.main.activity_coupondf.*
import org.jetbrains.anko.*

/**
 * 创建者：
 * 时间：
 */
class CouponActivity(override val layoutResId: Int = R.layout.activity_coupondf)
    : LifeActivity<CouponPresenter>() {
    override val presenter by lazy { CouponPresenter(this) }
    private var status = 0
    private var page = 0

    private val adapter = CouponAdapter({
        more()
    }) {
        // : 2018/6/5 15:36 霍述雷 详情
        startActivity<CouponDetailActivity>(CouponDetailActivity.KEY to it.key)
    }


    override fun configView() {
        rv_coupon.layoutManager = LinearLayoutManager(ctx)
        rv_coupon.addItemDecoration(ItemDecoration(dip(15)))
        rv_coupon.adapter = adapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                status = tab?.position ?: 0
                page = 0
                presenter.couponList(status) { total, body ->
                    adapter.setData(body)
                    adapter.setMore(total > adapter.getDateCount())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                status = tab?.position ?: 0
                page = 0
                presenter.couponList(status) { total, body ->
                    adapter.setData(body)
                    adapter.setMore(total > adapter.getDateCount())
                }
            }
        })
    }

    override fun initData() {
        tab_layout.getTabAt(0)?.select()
//        presenter.couponList(status) { total, body ->
//            adapter.setData(body)
//            adapter.setMore(total > adapter.getDateCount())
//        }
    }

    private fun more() {
        page++
        presenter.couponList(status, page) { total, body ->
            adapter.addData(body)
            adapter.setMore(total > adapter.getDateCount())
        }
    }

    fun exchange(v: View) {
        // : 2018/4/19 10:48 霍述雷 兑换
        val code = et_code.text.toString().trim()
        if (code.isNotBlank())
            presenter.exchangeCoupon(mutableMapOf("code" to code)) {
                mToast("兑换成功")
                tab_layout.getTabAt(0)?.select()
            }
        else mToast("兑换码不能为空")
    }
}
