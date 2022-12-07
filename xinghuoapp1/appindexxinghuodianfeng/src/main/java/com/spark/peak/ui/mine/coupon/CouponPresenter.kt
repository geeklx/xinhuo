package com.spark.peak.ui.mine.coupon

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Coupon

/**
 * 创建者：
 * 时间：
 */
class CouponPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun exchangeCoupon(code:  Map<String,String>, onNext: (Coupon) -> Unit) {
        api.exchangeCoupon(code).sub({ onNext(it.body) })
    }

    fun couponList(status: Int, page: Int = 0, onNext: (total: Int, List<Coupon>) -> Unit) {
        api.getCouponList(status,page).sub({ onNext(it.totalCount, it.body) }){
//            couponList(status,page,onNext)
        }
    }
}