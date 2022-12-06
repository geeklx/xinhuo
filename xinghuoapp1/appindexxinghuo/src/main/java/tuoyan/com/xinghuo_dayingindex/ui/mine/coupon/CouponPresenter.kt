package tuoyan.com.xinghuo_dayingindex.ui.mine.coupon

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.Coupon
import tuoyan.com.xinghuo_dayingindex.net.Results

/**
 * 创建者：
 * 时间：
 */
class CouponPresenter(progress: OnProgress) : BasePresenter(progress) {


    fun getCouponList(status: Int, onNext: (Results<List<Coupon>>) -> Unit) {
        api.getCouponList(status).sub(onNext = { onNext(it) })
    }
}