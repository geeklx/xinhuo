package com.spark.peak.ui.mine.order.pendingPayment

import android.view.View
import com.alipay.sdk.app.PayTask
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.wxapi.WXPayEntryActivity
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_payment_orderdf.*
import org.jetbrains.anko.startActivity

/**
 * 创建者：
 * 时间：
 */
class PaymentOrderActivity(override val layoutResId: Int = R.layout.activity_payment_orderdf)
    : LifeActivity<PaymentOrderPresenter>() {
    override val presenter by lazy { PaymentOrderPresenter(this) }
    private val key by lazy { intent.getStringExtra(KEY)?:"" }
    private val position by lazy { intent.getIntExtra(POSITION, -1) }
    private var isAliPay: Boolean = true
    override fun configView() {
        tv_ali.isSelected = isAliPay
        tv_we_chat.isSelected = !isAliPay
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        presenter.loadData(key) {
            tv_price.text = it.discount ?: "0"
            tv_name.text = it.title
        }
    }

    fun aliPay(v: View) {
        isAliPay = !v.isSelected
        v.isSelected = isAliPay
        tv_we_chat.isSelected = !isAliPay
    }

    fun weChatPay(v: View) {
        isAliPay = !v.isSelected
        v.isSelected = isAliPay
        tv_ali.isSelected = !isAliPay
    }

    fun confirmPayment(v: View) {
        // TODO: 2018/4/18 17:14 霍述雷 确认支付
        if (isAliPay) {
            presenter.payOrder(mutableMapOf("orderId" to key,
                    "type" to "1")) {
                aliPay(it["sign"] ?: "")
            }
        } else {
            presenter.payOrder(mutableMapOf("orderId" to key,
                    "type" to "2")) {
                weChatPay(it)
            }

        }
    }

    private fun aliPay(sign: String) {
        Single.create<Map<String, String>> { it.onSuccess(PayTask(this).payV2(sign, true)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    // TODO: 2018/5/17 12:35 霍述雷 确认支付状态
                    startActivity<PaymentSuccessActivity>(PaymentOrderActivity.KEY to key,
                            PaymentSuccessActivity.POSITION to position)
                    onBackPressed()
                }
                .doOnError {
                    mToast(it.message ?: "支付失败")
                }
                .subscribe()
    }

    private fun weChatPay(map: Map<String, String>) {
        val api = WXAPIFactory.createWXAPI(this, null)
        if (!api.isWXAppInstalled) {
            mToast("微信客户端未安装，请选择其它支付方式")
            return
        }
        WXPayEntryActivity.mWXPayResultListener = {
            // TODO: 2018/5/17 12:35 霍述雷 确认支付状态
            if (it == BaseResp.ErrCode.ERR_OK) {
                startActivity<PaymentSuccessActivity>(PaymentOrderActivity.KEY to key,
                        PaymentSuccessActivity.POSITION to position)
                onBackPressed()
            } else
                mToast("支付失败，请重试！")
        }

        val request = PayReq()
        api.registerApp(map["appid"] ?: "")
        request.appId = map["appid"] ?: ""
        request.partnerId = map["partnerid"] ?: ""
        request.prepayId = map["prepayid"] ?: ""
        request.packageValue = "Sign=WXPay"
        request.nonceStr = map["noncestr"] ?: ""
        request.timeStamp = map["timestamp"] ?: ""
        request.sign = map["sign"] ?: ""
        api.sendReq(request)
    }

    companion object {
        const val KEY = "key"
        const val POSITION = "position"
    }
}