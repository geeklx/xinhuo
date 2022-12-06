package tuoyan.com.xinghuo_dayingindex.ui.mine.order
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.app.Activity
import android.content.Intent
import android.view.View
import com.alipay.sdk.app.PayTask
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_payment_order.*
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.OrderDetail
import tuoyan.com.xinghuo_dayingindex.wxapi.WXPayEntryActivity


/**
 * 创建者：
 * 时间：
 */
class PaymentOrderActivity(override val layoutResId: Int = R.layout.activity_payment_order) :
    LifeActivity<PaymentOrderPresenter>() {
    override val presenter by lazy { PaymentOrderPresenter(this) }
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private val price by lazy { intent.getStringExtra(PRICE) ?: "" }
    override val title by lazy { intent.getStringExtra(TITLE) ?: "" }
    private val mainOrderNumber by lazy { intent.getStringExtra(MAIN_ORDER_NUMBER) ?: "" }
    private val dpOrderNumber by lazy { intent.getStringExtra(DP_ORDER_NUMBER) ?: "" }
    private val payType by lazy { intent.getStringExtra(TYPE) ?: "" }
    private val assembleKey by lazy { intent.getStringExtra(ASSEMBLE_KEY) ?: "" }
    private val lessonKey by lazy { intent.getStringExtra(LESSON_KEY) ?: "" }
    private var isAliPay: Boolean = true
    private var orderDetail: OrderDetail? = null
    fun onBack() {
        onBackPressed()
    }

    override fun configView() {
        tv_ali.isSelected = isAliPay
        tv_we_chat.isSelected = !isAliPay
    }

    override fun handleEvent() {
    }

    override fun initData() {
        tv_price.text = price ?: "0"
        tv_name.text = title
        presenter.orderDetail(key) {
            orderDetail = it
        }
    }

    @SensorsDataTrackViewOnClick
    fun aliPay(v: View) {
        isAliPay = !v.isSelected
        v.isSelected = isAliPay
        tv_we_chat.isSelected = !isAliPay
    }

    @SensorsDataTrackViewOnClick
    fun weChatPay(v: View) {
        isAliPay = v.isSelected
        v.isSelected = !isAliPay
        tv_ali.isSelected = isAliPay
    }

    @SensorsDataTrackViewOnClick
    fun confirmPayment(v: View) {
        saClick()
        // : 2018/4/18 17:14  确认支付
        val type = if (isAliPay) "1" else "2"
        val params = mutableMapOf("orderId" to key, "type" to type, "assembleKey" to assembleKey)
        params["mainOrderNumber"] = mainOrderNumber
        params["dpOrderNumber"] = dpOrderNumber
        presenter.payOrder(params) {
            if (isAliPay) {
                aliPay(it["sign"] ?: "")
            } else {
                weChatPay(it)
            }
        }
    }

    private fun aliPay(sign: String) {
        Single.create<Map<String, String>> {
            //            EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX)// TODO: 2018/11/7 16:36  沙箱
            it.onSuccess(PayTask(this).payV2(sign, true))
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                //todo : 2018/5/17 12:35  确认支付状态
                    it ->
                if (it["resultStatus"] == "9000") {
                    saPaySuccess()
                    if (payType == "999") {
                        //做题 主观题付费成功之后的页面
                        val intent = Intent(this, PaymentSuccessActivity::class.java)
                        intent.putExtra(PaymentSuccessActivity.IS_SET_RESULT, true)
                        this@PaymentOrderActivity.startActivityForResult(intent, 999)
                    } else {
                        //
                        startActivity<PaymentSuccessActivity>(
                            KEY to key,
                            PaymentSuccessActivity.ASSEMBLE_KEY to assembleKey,
                            PaymentSuccessActivity.TITLE to title,
                            PaymentSuccessActivity.LESSON_KEY to lessonKey
                        )
                        onBackPressed()
                    }
                } else {
                    saClickPay(false, Throwable("支付宝resultStatus=" + it["resultStatus"] + ",memo=" + it["memo"]))
                    it["memo"]?.let {
                        mToast(it)
                    }
                }
            }
            .doOnError {
                saClickPay(false, it)
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
            // : 2018/5/17 12:35  确认支付状态
            if (it == BaseResp.ErrCode.ERR_OK) {
                saPaySuccess()
                if (payType == "999") {
                    val intent = Intent(this, PaymentSuccessActivity::class.java)
                    intent.putExtra(PaymentSuccessActivity.IS_SET_RESULT, true)
                    this@PaymentOrderActivity.startActivityForResult(intent, 999)
                } else {
                    startActivity<PaymentSuccessActivity>(
                        KEY to key,
                        PaymentSuccessActivity.ASSEMBLE_KEY to assembleKey,
                        PaymentSuccessActivity.TITLE to title,
                        PaymentSuccessActivity.LESSON_KEY to lessonKey
                    )
                    onBackPressed()
                }
            } else {
                saClickPay(false, Throwable("微信支付返回code=$it"))
                mToast("支付失败，请重试！")
            }
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
        const val PRICE = "price"
        const val TITLE = "title"

        //录题页面进入
        const val TYPE = "TYPE"
        const val ASSEMBLE_KEY = "ASSEMBLE_KEY"
        const val LESSON_KEY = "lessonKey"
        const val MAIN_ORDER_NUMBER = "mainOrderNumber"//联报课
        const val DP_ORDER_NUMBER = "dpOrderNumber"//预付定金
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun saProperty(): JSONObject {
        val property = JSONObject()
        orderDetail?.let {
            val commodityId = arrayListOf<String>()
            val name = arrayListOf<String>()
            val privateName = arrayListOf<String>()
            it.goodsList.forEach { good ->
                commodityId.add(good.goodsKey)
                name.add(good.goodsName)
                privateName.add(good.privateName)
            }
            property.put(
                "commodity_type", when (it.orderType) {
                    "4" -> "人工批改"
                    "2" -> "网课"
                    "1" -> "图书"
                    "7" -> "智能书"
                    else -> "其他"
                }
            )
            property.put("commodity_id", commodityId.joinToString(","))
            property.put("order_id", it.orderNumber)
            property.put("commodity_name", name.joinToString(","))
            property.put("internal_name_online_course", privateName.joinToString(","))
        }
        property.put("payment_amount", price)
        property.put("payment_method", if (isAliPay) "支付宝" else "微信")
        return property
    }

    private fun saClick() {
        try {
            val property = saProperty()
            SensorsDataAPI.sharedInstance().track("confirm_payment", property)
        } catch (e: Exception) {
        }
    }

    private fun saPaySuccess() {
        try {
            saClickPay(true, null)
            val properties = JSONObject()
            properties.put("is_buy", true)
            SensorsDataAPI.sharedInstance().profileSet(properties)
        } catch (e: Exception) {
        }
    }

    private fun saClickPay(success: Boolean, e: Throwable?) {
        try {
            val property = saProperty()
            property.put("is_succeed", success)
            if (!success) {
                property.put("fail_result", e?.message)
            }
            SensorsDataAPI.sharedInstance().track("buy_result_client", property)
        } catch (e: Exception) {
        }
    }
}