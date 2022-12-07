package com.spark.peak.ui.mine.order

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Order
import com.spark.peak.net.Results

/**
 * 创建者：
 * 时间：
 */
class OrderPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun getOrderList(type: Int?, onNext: (Results<List<Order>>) -> Unit) {
        api.getOrderList(type).sub(onNext)
    }

    fun deleteOrder(key: String, onNext: () -> Unit) {
        api.deleteOrder(mutableMapOf("key" to key)).sub({ onNext() })
    }
fun cancelOrder(key: String, onNext: () -> Unit) {
        api.cancelOrder(mutableMapOf("key" to key)).sub({ onNext() })
    }

}