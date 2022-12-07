package com.spark.peak.ui.mine.order.pendingPayment

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Order

/**
 * 创建者：
 * 时间：
 */
class PendingPaymentPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun deleteOrder(key: String, onNext: () -> Unit) {
        api.deleteOrder(mutableMapOf("key" to key)).sub({ onNext() })
    }

    fun cancelOrder(key: String, onNext: () -> Unit) {
        api.cancelOrder(mutableMapOf("key" to key)).sub({ onNext() })
    }

    fun loadDetail(orderkey: String, onNext: (Order) -> Unit) {
        api.orderDetail(orderkey).sub({ onNext(it.body) })
    }

}