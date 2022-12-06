package tuoyan.com.xinghuo_dayingindex.ui.practice

import android.app.Activity
import android.content.Intent
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_teacher_modify_detail.*
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.QuestionPayplan
import tuoyan.com.xinghuo_dayingindex.ui.mine.order.PaymentOrderActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.UdeskUtils

class TeacherModifyDetailActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_teacher_modify_detail
    private val questionKey by lazy { intent.getStringExtra(QUESTION_KEY) ?: "" }
    private val evalKey by lazy { intent.getStringExtra(EVAL_KEY) ?: "" }
    private val name by lazy { intent.getStringExtra(NAME) ?: "" }
    private var questionPayPlan: QuestionPayplan? = null

    override fun configView() {
        super.configView()
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        webView.webViewClient = object : WebViewClient() {}
        webView.webChromeClient = object : WebChromeClient() {}
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun handleEvent() {
        super.handleEvent()
        ll_pay.setOnClickListener {
            saClick("购买")
            presenter.getDirectOrder(questionPayPlan!!.key, questionPayPlan!!.type, evalKey) {
                val intent = Intent(this, PaymentOrderActivity::class.java)
                intent.putExtra(PaymentOrderActivity.KEY, it.key)
                intent.putExtra(PaymentOrderActivity.TITLE, name)
                intent.putExtra(PaymentOrderActivity.PRICE, it.payPrice)
                intent.putExtra(PaymentOrderActivity.TYPE, "999")
                startActivityForResult(intent, 999)
//                startActivity<PaymentOrderActivity>(
//                        PaymentOrderActivity.KEY to it.key,
//                        PaymentOrderActivity.TITLE to questionPayPlan!!.tile,
//                        PaymentOrderActivity.PRICE to it.payPrice,
//                        PaymentOrderActivity.TYPE to "999"
//                )
//                finish()
            }
        }
        img_service.setOnClickListener {
            isLogin {
                saClick("咨询客服")
                UdeskUtils.openChatView(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun initData() {
        super.initData()
        presenter.getQuestionPayplan(questionKey) {
            questionPayPlan = it
            tv_price.text = "￥" + it.price
            webView.loadDataWithBaseURL(null, format(it.remarks), "text/html", "utf-8", null);
        }
    }

    companion object {
        const val QUESTION_KEY = "questionKey"
        const val EVAL_KEY = "evalKey"
        const val NAME = "name"
    }

    fun format(data: String) = "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\" />\n" +
            "<style>\n" +
            "*{margin:5; padding:25;}\n" + "img {\n" +
            "            max-width: 100% !important;\n" +
            "</style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            data +
            "</body>\n" +
            "</html>"

    override fun onResume() {
        super.onResume()
        try {
            SensorsDataAPI.sharedInstance().trackTimerStart("view_commodity_detail")
        } catch (e: Exception) {
        }
    }

    override fun onPause() {
        super.onPause()
        saDetail()
    }

    private fun setProperty(): JSONObject {
        val property = JSONObject()
        property.put("commodity_type", "人工批改")
        property.put("commodity_id", questionKey)
        property.put("commodity_name", name)
        property.put("current_price", questionPayPlan?.price)
        property.put("is_sign_in", SpUtil.isLogin)
        property.put("buy_it_or_not", false)
        return property
    }

    //神策收集网课详情
    private fun saDetail() {
        try {
            val property = setProperty()
            SensorsDataAPI.sharedInstance().trackTimerEnd("view_commodity_detail", property)
        } catch (e: Exception) {
        }
    }

    private fun saClick(operationName: String) {
        try {
            val property = setProperty()
            property.put("operation_name", operationName)
            SensorsDataAPI.sharedInstance().track("operation_commodity_detail", property)
        } catch (e: Exception) {
        }
    }
}
