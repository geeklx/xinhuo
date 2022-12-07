package com.spark.peak.ui.mine.order.pendingPayment

import android.graphics.Paint
import android.view.View
import com.bumptech.glide.Glide

import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.base.EventMsg
import com.spark.peak.bean.Order
import com.spark.peak.ui.dialog.AlertDialog
import com.spark.peak.ui.mine.order.comment.OrderCommentActivity
import kotlinx.android.synthetic.main.activity_pending_payment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

/**
 * 创建者：
 * 时间：
 */
class PendingPaymentActivity(override val layoutResId: Int = R.layout.activity_pending_payment)
    : LifeActivity<PendingPaymentPresenter>() {
    override val presenter by lazy { PendingPaymentPresenter(this) }
    private val key by lazy { intent.getStringExtra(PendingPaymentActivity.KEY)?:"" }
    private val position by lazy { intent.getIntExtra(PendingPaymentActivity.POSITION, -1) }
    private var order: Order? = null
    override fun configView() {
        tv_original_price.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
        EventBus.getDefault().register(this)
        update(-1)
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        presenter.loadDetail(key) {
            order = it
            Glide.with(this).load(it.img).placeholder(R.drawable.default_lesson).into(iv_img)
            tv_name.text = it.title
            tv_lesson.text = "${it.period ?: "0"}课时"
            tv_purchasers.text = "${it.buyers ?: 0}人购买"
            tv_validity_period.text = "有效期：${it.validitytime ?: ""}"
            tv_original_price.text = "￥${it.price ?: "0"}"
            tv_price.text = "￥${it.realprice ?: "0"}"
            tv_not_support.visibility = if (it.isunsub == 0) View.VISIBLE else View.GONE
            tv_total_price.text = "￥${it.realprice ?: "0"}"
            tv_offer.text = if (it.facevalue.isNullOrBlank()) "无可用优惠券" else "￥${it.realprice
                    ?: ""}"
            tv_real_price.text = "￥${it.discount ?: "0"}"
            tv_order_number.text = "订单编号：${it.ordernumber ?: ""}"
            tv_create_time.text = "创建时间：${it.createtime ?: ""}"
            tv_payment_time.text = "付款时间：${it.paytime ?: ""}"
            tv_payment_method.text = "支付方式：这个地方是写死的改不了 别问我为什么 我也不知道"
            update(it.status)
        }
    }

    private fun update(status: Int) {
        when (status) {
            0 -> {
                tv_title.text = "待付款"
                tv_payment_time.visibility = View.GONE
                tv_payment_method.visibility = View.GONE
                tv_cancel.visibility = View.VISIBLE
                tv_delete.visibility = View.GONE
                tv_pay.visibility = View.VISIBLE
                tv_commit.visibility = View.GONE
            }
            1 -> {
                tv_title.text = "待评价"
                tv_payment_time.visibility = View.VISIBLE
                tv_payment_method.visibility = View.VISIBLE
                tv_cancel.visibility = View.GONE
                tv_delete.visibility = View.VISIBLE
                tv_pay.visibility = View.GONE
                tv_commit.visibility = View.VISIBLE
            }
            2 -> {
                tv_title.text = "已取消"
                tv_payment_time.visibility = View.GONE
                tv_payment_method.visibility = View.GONE
                tv_cancel.visibility = View.GONE
                tv_delete.visibility = View.VISIBLE
                tv_pay.visibility = View.GONE
                tv_commit.visibility = View.GONE
            }
            3 -> {
                tv_title.text = "已评价"
                tv_payment_time.visibility = View.VISIBLE
                tv_payment_method.visibility = View.VISIBLE
                tv_cancel.visibility = View.GONE
                tv_delete.visibility = View.VISIBLE
                tv_pay.visibility = View.GONE
                tv_commit.visibility = View.GONE
            }
            4 -> {
                tv_title.text = "已失效"
                tv_payment_time.visibility = View.GONE
                tv_payment_method.visibility = View.GONE
                tv_cancel.visibility = View.GONE
                tv_delete.visibility = View.VISIBLE
                tv_pay.visibility = View.GONE
                tv_commit.visibility = View.GONE
            }
            else -> {
                tv_title.text = "变异订单"
                tv_payment_time.visibility = View.GONE
                tv_payment_method.visibility = View.GONE
                tv_cancel.visibility = View.GONE
                tv_delete.visibility = View.GONE
                tv_pay.visibility = View.GONE
                tv_commit.visibility = View.GONE
            }
        }
    }

    fun pay(v: View) {
        startActivity<PaymentOrderActivity>(PaymentOrderActivity.KEY to key,
                PaymentOrderActivity.POSITION to position)
    }

    fun delete(v: View) {
        // : 2018/4/18 16:44 霍述雷 删除订单
        AlertDialog(ctx, "确定要删除订单吗？") {
            presenter.deleteOrder(key) {
                EventBus.getDefault().post(EventMsg("order_delete", position))
                onBackPressed()
            }
        }.show()
    }

    fun cancel(v: View) {
        // : 2018/4/18 16:44 霍述雷 取消订单
        AlertDialog(this, "确定要取消订单吗？") {
            presenter.cancelOrder(key) {
                EventBus.getDefault().post(EventMsg("order_cancel", position))
                update(2)
            }
        }.show()
    }

    fun commit(v: View) {
        // : 2018/4/18 16:44 霍述雷 评论
        order?.let {
            startActivity<OrderCommentActivity>(OrderCommentActivity.KEY to it.goodkey)
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun update(msg: EventMsg) {
        if (msg.action == "order_comment") {
            update(3)
        }
        if (msg.action == "order_pay") {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    companion object {
        const val KEY = "key"
        const val POSITION = "position"
    }
}