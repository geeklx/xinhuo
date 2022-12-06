package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import android.view.Window
import kotlinx.android.synthetic.main.layout_dialog_coupon.*
import org.jetbrains.anko.matchParent
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.bean.Coupon
import tuoyan.com.xinghuo_dayingindex.ui.mine.coupon.CouponAdapter

/**
 * Created by Zzz on 2020/7/23
 * Email:
 */

class CouponDialog(context: Context, var click: (List<Coupon>, Int) -> Unit) : Dialog(context, R.style.custom_dialog) {
    val couponAdapter by lazy {
        CouponAdapter { list, pos ->
            click(list, pos)
        }
    }
    private var data = ArrayList<Coupon>()
    fun setData(data: ArrayList<Coupon>) {
        this.data = data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_dialog_coupon)
        initView()
    }

    private fun initView() {
        rlv_coupon.layoutManager = LinearLayoutManager(context)
        rlv_coupon.adapter = couponAdapter
        couponAdapter.fromLessonDetail = true
        couponAdapter.setData(data)
        tv_close.setOnClickListener {
            dismiss()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
//        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.width = matchParent
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
    }
}