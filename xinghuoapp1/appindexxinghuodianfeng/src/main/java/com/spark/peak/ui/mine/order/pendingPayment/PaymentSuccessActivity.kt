package com.spark.peak.ui.mine.order.pendingPayment

import android.view.View
import com.spark.peak.R
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.EventMsg
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.MainActivity
import kotlinx.android.synthetic.main.activity_payment_successdf.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

/**
 * 创建者：
 * 时间：
 */
class PaymentSuccessActivity(override val layoutResId: Int = R.layout.activity_payment_successdf) : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    private val key by lazy { intent.getStringExtra(KEY) }
    private val position by lazy { intent.getIntExtra(POSITION, -1) }
    override fun configView() {

    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        EventBus.getDefault().post(EventMsg("order_pay", position))
        super.onBackPressed()
    }
    fun home(v: View) {
        startActivity<MainActivity>()
        finish()
    }

    fun detail(v: View) {
        startActivity<PendingPaymentActivity>(PendingPaymentActivity.KEY to key,
                PendingPaymentActivity.POSITION to position)
        finish()
        EventBus.getDefault().post(EventMsg("order_pay", position))
    }

    companion object {
        const val KEY = "key"
        const val POSITION = "position"
    }
}