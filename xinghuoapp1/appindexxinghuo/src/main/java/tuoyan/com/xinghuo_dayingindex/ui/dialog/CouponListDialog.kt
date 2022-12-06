package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.layout_couponlist_dialog_item.view.*
import kotlinx.android.synthetic.main.layout_googdslist_item.view.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Coupon
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class CouponListDialog(
    context: Context,
    val click: (item: Coupon) -> Unit,
    val clickBack: () -> Unit
) : Dialog(context, R.style.custom_dialog) {
    private val adapter by lazy {
        Adapter() {
            click(it)
//            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_coupon_list_dialog)
        val tv_title = findViewById<TextView>(R.id.tv_title)
        tv_title.setOnClickListener({
            clickBack()
            dismiss()
        })

        val rlv_coupon_list = findViewById<RecyclerView>(R.id.rlv_coupon_list)
        rlv_coupon_list.layoutManager = LinearLayoutManager(context)
//        var strs = ArrayList<String>()
//        strs.add("1")
//        strs.add("2")
//        strs.add("3")
//        strs.add("4")
        rlv_coupon_list.adapter = adapter

    }

    fun dataRefresh() {
        adapter.notifyDataSetChanged()
    }

    fun setData(data: List<Coupon>) {
        adapter.setData(data)
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

    class Adapter(val itemClick: (item: Coupon) -> Unit) :
        BaseAdapter<Coupon>(isEmpty = true, isFooter = true) {
        var isUsed = false
        override fun convert(holder: ViewHolder, item: Coupon) {
            holder.setIsRecyclable(false)//禁止复用
            val price = "¥${item.facevalue ?: ""}"
            val index = price.indexOf(".")
            val priceStr = SpannableString(price)
            var str = ""
            item.goodList?.forEach {
                str = str + (it.name ?: "") + "\n"
            }
            priceStr.setSpan(
                AbsoluteSizeSpan(mContext!!.sp(18)),
                0,
                1,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            priceStr.setSpan(
                AbsoluteSizeSpan(mContext!!.sp(32)),
                1,
                priceStr.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            if (index > 0) {
                priceStr.setSpan(
                    AbsoluteSizeSpan(mContext!!.sp(18)),
                    index,
                    priceStr.length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
//        img_coupon_tag 过期：isSelected：true；以获取：normal；其他隐藏

            var ruleContent = "可用商品：${
                when (item.isUniversal) {
                    "1" -> "全商品可用\n"
                    "2" -> "全店网课可用\n"
                    "3" -> "全店图书可用\n"
                    else -> str + "\n"
                }
            }" + "使用说明：${item.content ?: ""}"
//            Log.d("coupon", "convert: $ruleContent")
            (holder.getView(R.id.tv_rule_content) as TextView).text = ruleContent
            holder.setText(R.id.tv_title, item.name)
                .setText(R.id.tv_explain, item.name2)
                .setText(
                    R.id.tv_limit,
                    if (item.orderAmountLimit == "0") "无门槛优惠券" else "满${item.orderAmountLimit}可用"
                )
                .setText(
                    R.id.tv_time,
                    "${item.startTime?.replace("-", ".")}-${item.endTime?.replace("-", ".")}"
                )
                .setText(R.id.tv_price, priceStr)

                .setOnClickListener(R.id.tv_rule) {
                    if (!isUsed) {
                        if (holder.getView(R.id.tv_rule_content).visibility == View.GONE) {
                            holder.getView(R.id.tv_rule_content).visibility = View.VISIBLE
                            it.isSelected = true
                        } else {
                            holder.getView(R.id.tv_rule_content).visibility = View.GONE
                            it.isSelected = false
                        }
                    }
                }
            if (isUsed) setTimeOut(holder)
            fromLesson(holder, item)

//            holder.itemView.tv_had.setOnClickListener {
//                itemClick(item)
//            }
        }

        private fun fromLesson(holder: ViewHolder, item: Coupon) {
            if (true) {
                if ("1" == item.isOwn) {
//                    holder.setVisible(R.id.tv_had, View.GONE)
//                        .setVisible(R.id.img_coupon_tag, View.VISIBLE)
                    holder.setText(R.id.tv_had, "已领取")
                    holder.setBackgroundResource(R.id.tv_had, R.drawable.shape_30_ffd9d9)
                    holder.setTextColor(R.id.tv_had, Color.parseColor("#ffe7ef"))
                } else {
                    holder.setText(R.id.tv_had, "立即领取")
                    holder.setBackgroundResource(R.id.tv_had, R.drawable.shape_30_ff1717)
                    holder.setTextColor(R.id.tv_had, Color.parseColor("#ffe7ef"))
//                    holder.setVisible(R.id.tv_had, View.VISIBLE)
//                        .setVisible(R.id.img_coupon_tag, View.GONE)
                }
            } else {
                //img_coupon_tag 过期：isSelected：true；以获取：normal；其他隐藏
                holder.setVisible(R.id.tv_had, View.GONE)
                    .setVisible(R.id.img_coupon_tag, if (isUsed) View.VISIBLE else View.GONE)
                    .setSelected(R.id.img_coupon_tag, isUsed)
            }
            holder.setOnClickListener(R.id.tv_had) {
                if (item.isOwn == "0") {
                    itemClick(item)
                } else {

                }


            }
        }

        private fun setTimeOut(holder: ViewHolder) {
            holder.setBackgroundResource(R.id.rl_item, R.drawable.shape_coupon_item_f5f5f9)
                .setBackgroundResource(R.id.rl_left, R.drawable.shape_10_l_fff)
                .setBackgroundResource(R.id.rl_right, R.drawable.shape_10_f8f8fa)
                .setBackgroundResource(R.id.tv_rule, R.drawable.shape_10_f5f5f9)
                .setBackgroundResource(R.id.v_line, R.mipmap.line_coupon_timeout)
                .setTextColor(
                    R.id.tv_price,
                    ContextCompat.getColor(holder.itemView.context, R.color.color_c4cbde)
                )
                .setTextColor(
                    R.id.tv_limit,
                    ContextCompat.getColor(holder.itemView.context, R.color.color_c4cbde)
                )
                .setTextColor(
                    R.id.tv_title,
                    ContextCompat.getColor(holder.itemView.context, R.color.color_c4cbde)
                )
                .setTextColor(
                    R.id.tv_time,
                    ContextCompat.getColor(holder.itemView.context, R.color.color_c4cbde)
                )
                .setTextColor(
                    R.id.tv_rule,
                    ContextCompat.getColor(holder.itemView.context, R.color.color_c4cbde)
                )
            var tvRule = holder.getView(R.id.tv_rule) as TextView
            tvRule.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        override fun convertView(context: Context, parent: ViewGroup): View {
            return LayoutInflater.from(context)
                .inflate(R.layout.layout_couponlist_dialog_item, null)
        }

        override fun emptyText(): String {
            return "暂无优惠券"
        }

        override fun footerView(context: Context, parent: ViewGroup): View {
            val view = View(context)
            val params = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                DeviceUtil.dp2px(context, 60f).toInt()
            )
            view.layoutParams = params
            return view
        }
    }
}