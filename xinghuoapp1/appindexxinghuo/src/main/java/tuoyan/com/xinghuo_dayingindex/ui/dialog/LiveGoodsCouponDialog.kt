package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.layout_couponlist_dialog_item.view.*
import kotlinx.android.synthetic.main.layout_googdslist_item.view.*
import kotlinx.android.synthetic.main.layout_lieve_goods_coupon_dialog.*
import org.jetbrains.anko.matchParent
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.bean.Coupon
import java.util.*

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class LiveGoodsCouponDialog(context: Context, val click: (item: Coupon) -> Unit) :
    Dialog(context, R.style.custom_dialog) {

    var data: Coupon? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_lieve_goods_coupon_dialog)

        iv_close.setOnClickListener({
            dismiss()
        })


        tv_get.setOnClickListener({

            data?.let { it1 ->
                if(data?.ownNum == "0") {
                    click(it1)
                }
            };

        })
    }

    fun setCoupon(coupon: Coupon) {
        data = coupon
        tv_time.text =
            "一 有效期：${coupon.startTime?.replace("-", ".")}-${coupon.endTime?.replace("-", ".")} 一"
        tv_coupon_num.text = coupon.facevalue
        tv_limit.text =
            if (coupon.orderAmountLimit == "0") "无门槛优惠券" else "满${coupon.orderAmountLimit}可用"
        tv_title.text = coupon.name
        tv_title_2.text = coupon.name2
        if (coupon.ownNum == "0") {
            tv_get.text = "立即领取"
            ll_bg.setBackgroundResource(R.drawable.ic_coupon_center)
        } else {
            tv_get.text = "已领取"
            ll_bg.setBackgroundResource(R.drawable.ic_coupon_center_get)
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
        window?.setGravity(Gravity.CENTER)
    }

    fun timeShow() {
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                dismiss()
            }
        }, (1000 * 5).toLong(), 3000)
        show();
    }

    override fun show() {
        super.show()

    }

    var timer: Timer? = null


    override fun dismiss() {
        super.dismiss()
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }

    }
}