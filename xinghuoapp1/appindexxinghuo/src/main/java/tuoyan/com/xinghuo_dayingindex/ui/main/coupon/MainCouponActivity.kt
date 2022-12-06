package tuoyan.com.xinghuo_dayingindex.ui.main.coupon

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_main_coupon.*
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.Coupon
import tuoyan.com.xinghuo_dayingindex.ui.mine.coupon.CouponMainAdapter

class MainCouponActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_main_coupon
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    val adapter by lazy {
        CouponMainAdapter { item ->
            isLogin {
                getCoupon(item)
            }
        }
    }

    fun getCoupon(item: Coupon) {
        presenter.exchangeCoupon(mutableMapOf("pKey" to item.key)) {
            saSensors(it)
            item.isOwn = it.isOwn
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "优惠券领取成功", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .fullScreen(true)
            .statusBarColor(R.color.transparent)
            .statusBarDarkFont(false)
            .init()
        fullScreen = true
        super.onCreate(savedInstanceState)
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.getHomePromotionals(key) {
            if (it.isNotEmpty()) {
                ctl_rlv.visibility = View.VISIBLE
                ctl_empty.visibility = View.GONE
                adapter.setData(it)
            }
        }
    }

    override fun initData() {
        super.initData()
    }

    override fun configView() {
        super.configView()
        rlv_coupon.layoutManager = LinearLayoutManager(this)
        rlv_coupon.adapter = adapter
        adapter.fromLessonDetail = true
    }

    companion object {
        val KEY = "key"
    }

    private fun saProperty(item: Coupon): JSONObject {
        val property = JSONObject()
        property.put("coupon_id", item.key)
        property.put("coupon_name", item.name)
        property.put("coupon_threshold", if (item.orderAmountLimit.isNullOrEmpty() || item.orderAmountLimit == "0") "不限制" else "${item.orderAmountLimit}")
        property.put("coupon_amount", item.facevalue)
        property.put("coupon_validity", "${item.startTime}-${item.endTime}")
        property.put("receiving_location", "领券中心")
        property.put("receiving_method", "手动领取")
        property.put("coupon_notes", item.remarks)
        return property
    }

    private fun saSensors(item: Coupon) {
        try {
            val property = saProperty(item)
            SensorsDataAPI.sharedInstance().track("collect_selected_coupons", property)
        } catch (e: Exception) {
        }
    }
}