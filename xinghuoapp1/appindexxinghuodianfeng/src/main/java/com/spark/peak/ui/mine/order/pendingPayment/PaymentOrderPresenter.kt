package com.spark.peak.ui.mine.order.pendingPayment

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Order

/**
 * 创建者：
 * 时间：
 */
class PaymentOrderPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun loadData(key: String, onNext: (Order) -> Unit) {
        api.orderDetail(key).sub({ onNext(it.body) })
    }

    fun payOrder(map: Map<String, String>, onNext: (Map<String, String>) -> Unit) {
        api.payOrder(map).sub(onNext = { onNext(it.body) })
    }

}