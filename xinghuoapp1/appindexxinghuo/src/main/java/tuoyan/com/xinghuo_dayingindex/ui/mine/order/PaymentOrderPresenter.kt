package tuoyan.com.xinghuo_dayingindex.ui.mine.order

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.Order
import tuoyan.com.xinghuo_dayingindex.bean.OrderDetail

/**
 * 创建者：
 * 时间：
 */
class PaymentOrderPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun loadData(key: String, onNext: (Order) -> Unit) {
//        api.orderDetail(key).sub({ onNext(it.body) })
    }

    //
    fun payOrder(map: Map<String, String>, onNext: (Map<String, String>) -> Unit) {
        api.payOrder(map).sub(onNext = { onNext(it.body) })
    }

    fun orderDetail(key: String, onNext: (OrderDetail) -> Unit) {
        api.orderDetail(key).sub({ onNext(it.body) })
    }
}