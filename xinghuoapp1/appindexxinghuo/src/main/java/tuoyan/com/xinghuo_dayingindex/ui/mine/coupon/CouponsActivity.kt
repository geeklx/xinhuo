package tuoyan.com.xinghuo_dayingindex.ui.mine.coupon

import android.view.View
import android.widget.RadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_collection.*
import kotlinx.android.synthetic.main.activity_coupons.*
import kotlinx.android.synthetic.main.activity_coupons.rg_c
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

class CouponsActivity : LifeActivity<CouponPresenter>() {
    override val presenter = CouponPresenter(this)
    override val layoutResId = R.layout.activity_coupons
    private val adapter by lazy {
        CouponAdapter { _, _ ->

        }
    }
    private var isFirst = true//onresume是否第一次执行

    override fun configView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
        rg_c.setOnCheckedChangeListener { radioGroup, checkedId ->

            loadData( radioGroup.indexOfChild(radioGroup.findViewById(checkedId)))
//            view_pager.currentItem =
        }

//        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                loadData()
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//            }
//
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                loadData()
//            }
//
//        })
    }

    private fun loadData(positon :Int) {
        if (positon == 0) {
            presenter.getCouponList(0) {
                adapter.isUsed = false
                adapter.setData(it.body)
            }
        } else if (positon == 1) {
            presenter.getCouponList(-1) {
                adapter.isUsed = true
                adapter.setData(it.body)
            }
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        if (freeLoginTo) {
            freeLoginTo = false
        } else if (!SpUtil.isLogin && !isFirst) {
            onBackPressed()
        } else {
            isLogin {
                val rb = rg_c.getChildAt(0) as RadioButton
                rb.isChecked = true
//                tab_layout.getTabAt(0)?.select()
                presenter.delPromotionalFlag() {}
            }
        }
        isFirst = false
    }

    @SensorsDataTrackViewOnClick
    fun exchange(v: View) {
        // : 2018/10/24 15:23   兑换
        val code = et_code.text.toString().trim()
        if (code.isBlank()) {
            mToast("兑换码不能为空")
        } else {
            presenter.exchangeCoupon(mutableMapOf("code" to code)) {
                et_code.text.clear()
                loadData( rg_c.indexOfChild(rg_c.findViewById(rg_c.checkedRadioButtonId)))
            }
        }
    }
}
