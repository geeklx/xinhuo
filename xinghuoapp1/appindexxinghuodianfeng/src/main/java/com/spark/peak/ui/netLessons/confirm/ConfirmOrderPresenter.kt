package com.spark.peak.ui.netLessons.confirm

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Coupon
import com.spark.peak.bean.NetLesson
import com.spark.peak.bean.SubmitOrder

/**
 * 创建者：
 * 时间：
 */
class ConfirmOrderPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun loadDetail(key: String, onNext: (NetLesson) -> Unit) {
        api.getNetLesson(key).sub({ onNext(it.body) })
    }

    fun loadCoupons(key: String, onNext: (List<Coupon>) -> Unit) {
        api.coupons(key).sub({ onNext(it.body) })
    }

    fun submit(submitOrder: SubmitOrder, onNext: (String?) -> Unit) {
        api.submitOrder(submitOrder).sub({ onNext(it.body["key"]) })
    }

}