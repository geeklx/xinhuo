package com.spark.peak.ui.mine.coupon.detail

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Coupon

/**
 * 创建者：
 * 时间：
 */
class CouponDetailPresenter(progress: OnProgress) : BasePresenter(progress) {


    fun couponDetail(key: String, onNext: (Coupon) -> Unit) {
        api.getCouponDetail(key).sub({ onNext(it.body) })
    }
}