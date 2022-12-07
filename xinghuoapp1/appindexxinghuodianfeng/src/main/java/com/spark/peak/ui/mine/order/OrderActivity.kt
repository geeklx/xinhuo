package com.spark.peak.ui.mine.order

import com.google.android.material.tabs.TabLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.base.EventMsg
import com.spark.peak.ui.dialog.AlertDialog
import com.spark.peak.ui.mine.order.comment.OrderCommentActivity
import com.spark.peak.ui.mine.order.pendingPayment.PaymentOrderActivity
import com.spark.peak.ui.mine.order.pendingPayment.PendingPaymentActivity
import kotlinx.android.synthetic.main.activity_orderdf.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

/**
 * 创建者：
 * 时间：
 */
class OrderActivity(override val layoutResId: Int = R.layout.activity_orderdf)
    : LifeActivity<OrderPresenter>() {
    override val presenter by lazy { OrderPresenter(this) }
    var type: Int? = null
    var page = 0
    private val adapter by lazy {
        OrderAdapter({ key, position ->
            startActivity<PendingPaymentActivity>(PendingPaymentActivity.KEY to key,
                    PendingPaymentActivity.POSITION to position)
        }, {key, position ->
            startActivity<PaymentOrderActivity>(PaymentOrderActivity.KEY to key,
                    PaymentOrderActivity.POSITION to position)
        }, this::cancelOrder, this::deleteOrder, { key, position ->
            startActivity<OrderCommentActivity>(OrderCommentActivity.KEY to key,
                    OrderCommentActivity.POSITION to position)
        }) {
            more()
        }
    }

    private fun deleteOrder(key: String, position: Int) {
        // : 2018/6/5 14:00 霍述雷  删除订单
        AlertDialog(ctx, "确定要删除订单吗？") {
            presenter.deleteOrder(key) {
                adapter.remove(position)
            }
        }.show()
    }

    private fun cancelOrder(key: String, position: Int) {
        // : 2018/6/5 14:00 霍述雷  删除订单
        AlertDialog(ctx, "确定要取消订单吗？") {
            presenter.cancelOrder(key) {
                adapter.getData()[position].status = 2
                adapter.notifyItemChanged(position)
            }
        }.show()

    }

    private fun more() {
        page += 1
        presenter.getOrderList(type) {
            adapter.addData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
    }

    override fun configView() {
        rv_order.layoutManager = LinearLayoutManager(ctx)
//        val itemDecoration = DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL)
//        itemDecoration.setDrawable(resources.getDrawable(R.drawable.divider))
//        rv_order.addItemDecoration(itemDecoration)
        rv_order.adapter = adapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        EventBus.getDefault().register(this)
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                page = 0
                type = if (tab?.position == 0) null else (tab?.position ?: 0) - 1
                clear()
                presenter.getOrderList(type) {
                    adapter.setData(it.body)
                    adapter.setMore(adapter.getDateCount() < it.totalCount)
                }
            }

        })
    }

    override fun initData() {
        presenter.getOrderList(type) {
            adapter.setData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun update(msg: EventMsg) {
        if (msg.action == "order_delete" && msg.position >= 0) adapter.remove(msg.position)
        if (msg.action == "order_cancel" && msg.position >= 0) {
            adapter.getData()[msg.position].status = 2
            adapter.notifyItemChanged(msg.position)
        }
        if (msg.action == "order_comment" && msg.position >= 0) {
            adapter.getData()[msg.position].status = 3
            adapter.notifyItemChanged(msg.position)
        }
        if (msg.action == "order_pay") {
            adapter.getData()[msg.position].status = 1
            adapter.notifyItemChanged(msg.position)
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}