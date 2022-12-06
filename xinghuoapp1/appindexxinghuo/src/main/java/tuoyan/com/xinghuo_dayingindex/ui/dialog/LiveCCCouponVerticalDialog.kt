package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.*
import kotlinx.android.synthetic.main.item_live_cc_coupon.view.*
import kotlinx.android.synthetic.main.layout_lieve_cc_coupon_vertical_dialog.*

import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Coupon
import java.util.*

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class LiveCCCouponVerticalDialog(context: Context, val click: (item: Coupon,dialog:LiveCCCouponVerticalDialog) -> Unit) :
    Dialog(context, R.style.custom_dialog) {
    private val adapter by lazy {
        Adapter(context) {
            click(it,this)
        }
    }
    var data: Coupon? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_lieve_cc_coupon_vertical_dialog)

        iv_colse.setOnClickListener({
            dismiss()
        })
        rv_coupon.layoutManager = LinearLayoutManager(context)

        rv_coupon.adapter = adapter
//        var list=ArrayList<String>()
//        list.add("")
//        list.add("")
//        list.add("")
//
//        adapter.setData(list)
    }
    fun dataRefresh() {
        adapter.notifyDataSetChanged()
    }

    fun setData(data: List<Coupon>) {
        adapter.setData(data)
    }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setCancelable(false)
        val lp = window?.attributes
//        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.width = matchParent

        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }

    class Adapter(context: Context, val itemClick: (item: Coupon) -> Unit) :
        BaseAdapter<Coupon>() {
        private val context1 = context
        override fun convert(holder: ViewHolder, item: Coupon) {

            holder.setText(R.id.tv_content, item.name)
                .setText(R.id.tv_time,"${item.startTime?.replace("-", ".")}-${item.endTime?.replace("-", ".")}")
                .setText(R.id.tv_num, item.facevalue)
            if ("1" == item.isOwn) {
                holder.itemView.iv_get.visibility=View.VISIBLE
                holder.itemView.tv_submit.visibility=View.GONE
            }else{
                holder.itemView.iv_get.visibility=View.GONE
                holder.itemView.tv_submit.visibility=View.VISIBLE
            }
            holder.itemView.tv_submit.setOnClickListener {
                itemClick(item)
            }
        }

        override fun convertView(context: Context, parent: ViewGroup): View {
            return LayoutInflater.from(context).inflate(R.layout.item_live_cc_coupon_vertical, parent,false)
        }

        override fun emptyText(): String {
            return "暂无优惠券"
        }

        override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
            verticalLayout {
                lparams(matchParent, matchParent)
//                backgroundResource = Color.parseColor("#ff000000")
                space().lparams(wrapContent, 0, 0.5f)
                textView {
                    text = emptyText()
                    textSize = 15f
                    gravity = Gravity.CENTER
//                compoundDrawablePadding = dip(10)
                    setCompoundDrawablesWithIntrinsicBounds(0, emptyImageRes(), 0, 0)
                    textColor = Color.parseColor("#666666")
                }
                space().lparams(wrapContent, 0, 1.5f)
            }
        }
//        override fun footerView(context: Context, parent: ViewGroup): View {
//            val view = View(context)
//            val params = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                DeviceUtil.dp2px(context, 60f).toInt()
//            )
//            view.layoutParams = params
//            return view
//        }
    }
}