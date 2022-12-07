package com.spark.peak.ui.netLessons.confirm

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide

import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.base.EventMsg
import com.spark.peak.bean.Coupon
import com.spark.peak.bean.NetLesson
import com.spark.peak.bean.SubmitOrder
import com.spark.peak.ui.mine.order.pendingPayment.PaymentOrderActivity
import kotlinx.android.synthetic.main.activity_confirm_orderdf.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*

/**
 * 创建者：
 * 时间：
 */
class ConfirmOrderActivity(override val layoutResId: Int = R.layout.activity_confirm_orderdf)
    : LifeActivity<ConfirmOrderPresenter>() {
    override val presenter by lazy { ConfirmOrderPresenter(this) }
    private val key by lazy { intent.getStringExtra(KEY)?:"" }
    var coupons: List<Coupon>? = null
    var netLesson: NetLesson? = null
    var coupon: Coupon? = null
    override fun configView() {
        EventBus.getDefault().register(this)
//        tv_offer.textColor=resources.getColor(R.color.color_cccccc)
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        presenter.loadDetail(key ?: "") {
            netLesson = it
            Glide.with(this).load(it.coverimg).placeholder(R.drawable.default_lesson).into(iv_img)
            tv_name.text = it.title
            tv_lesson.text = "${it.period}课时"
            tv_purchasers.text = "${it.buyers}人购买"
            tv_validity_period.text = "有效期：${it.validitytime}"
            tv_original_price.text = "￥${it.price}"
            tv_price.text = "￥${it.disprice}"
            tv_not_support.visibility = if (it.isunsub == 0) View.VISIBLE else View.GONE
            tv_total_price.text = "￥${it.disprice}"
            tv_real_price.text = "￥${it.disprice}"
            tv_offer_1.text = "已优惠 ￥0"
            tv_total.text = "￥${(it.disprice)}"
        }
        presenter.loadCoupons(key ?: "") {
            coupons = it
            if (coupons?.isEmpty() != false) return@loadCoupons
            tv_offer.text = "${it.size}张可用优惠券"
            tv_offer.textColor = resources.getColor(R.color.color_ff5b35)
        }
    }

    fun coupon(v: View) {
        // : 2018/6/5 15:01 霍述雷 选择优惠券
        if (coupons?.isEmpty() != false) return
        startActivityForResult<SelectorCouponActivity>(COUPON, SelectorCouponActivity.COUPONS to coupons)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            if (requestCode == COUPON) {
                coupon = data.getSerializableExtra("coupon") as Coupon
                coupon?.let {
                    tv_offer.text = it.name
                    tv_real_price.text = "￥${((netLesson?.disprice?.toDouble()
                            ?: 0.0) - it.facevalue)}"
                    tv_offer_1.text = "已优惠 ￥${it.facevalue}"
                    tv_total.text = "￥${((netLesson?.disprice?.toDouble() ?: 0.0) - it.facevalue)}"
                }
            }
        }
    }

    fun submit(v: View) {
        // : 2018/4/19 15:29 霍述雷 提交订单
        presenter.submit(SubmitOrder(key, discountkey = coupon?.key)) {
            it?.let {
                startActivity<PaymentOrderActivity>(PaymentOrderActivity.KEY to it)

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun update(msg: EventMsg) {
        if (msg.action == "pay") {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    companion object {
        const val KEY = "key"
        const val COUPON = 0x1d2
    }
}