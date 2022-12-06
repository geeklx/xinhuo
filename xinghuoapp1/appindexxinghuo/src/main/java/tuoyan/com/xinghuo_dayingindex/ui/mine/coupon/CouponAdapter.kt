package tuoyan.com.xinghuo_dayingindex.ui.mine.coupon

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Coupon

/**
 */
class CouponAdapter(val click: (List<Coupon>, Int) -> Unit) : BaseAdapter<Coupon>(isEmpty = true) {
    var isUsed = false

    //是否从网课详情过来，是：展示领取按钮或者已领取标识
    var fromLessonDetail = false
    override fun emptyImageRes(): Int {
        return R.drawable.ic_emtry_coupon
    }

    override fun emptyText(): String {
        return "暂无优惠券哦"
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return context.layoutInflater.inflate(R.layout.layout_coupon_item, null)
    }

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
            .setText(R.id.tv_title_2,item.name2)
            .setText(
                R.id.tv_limit,
                if (item.orderAmountLimit == "0") "无门槛优惠券" else "满${item.orderAmountLimit}可用"
            )
            .setText(
                R.id.tv_time,
                "${item.startTime?.replace("-", ".")}-${item.endTime?.replace("-", ".")}"
            )
            .setText(R.id.tv_price, priceStr)
//            .setText(
//                R.id.tv_rule_content,
//                "可用商品：${
//                    when (item.isUniversal) {
//                        "1" -> "全商品可用\n"
//                        "2" -> "全店网课可用\n"
//                        "3" -> "全店图书可用\n"
//                        else -> str
//                    }
//                }使用说明：${item.content ?: ""}"
//            )
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
      holder.setVisible(R.id.tv_live_label,if (item.isLive=="1")View.VISIBLE else View.GONE)
        if (isUsed) setTimeOut(holder)
        fromLesson(holder, item)
    }

    private fun fromLesson(holder: ViewHolder, item: Coupon) {
        if (fromLessonDetail) {
            if ("1" == item.isOwn) {
                holder.setVisible(R.id.tv_had, View.GONE)
                    .setVisible(R.id.img_coupon_tag, View.VISIBLE)
            } else {
                holder.setVisible(R.id.tv_had, View.VISIBLE)
                    .setVisible(R.id.img_coupon_tag, View.GONE)
            }
        } else {
            //img_coupon_tag 过期：isSelected：true；以获取：normal；其他隐藏
            holder.setVisible(R.id.tv_had, View.GONE)
                .setVisible(R.id.img_coupon_tag, if (isUsed) View.VISIBLE else View.GONE)
                .setSelected(R.id.img_coupon_tag, isUsed)
        }
        holder.setOnClickListener(R.id.tv_had) {
            click(getData(), holder.adapterPosition)
        }
    }

    private fun setTimeOut(holder: ViewHolder) {
        holder.setBackgroundResource(R.id.rl_item, R.drawable.shape_coupon_item_f5f5f9)
            .setBackgroundResource(R.id.rl_left, R.drawable.shape_10_l_fff)
            .setBackgroundResource(R.id.rl_right, R.drawable.shape_10_f8f8fa)
            .setBackgroundResource(R.id.tv_rule, R.drawable.shape_10_f5f5f9)
            .setBackgroundResource(R.id.v_line, R.mipmap.line_coupon_timeout)
            .setBackgroundResource(R.id.tv_live_label, R.drawable.shape_10_f5f5f9_tl_br)
            .setTextColor(
                R.id.tv_live_label,
                ContextCompat.getColor(holder.itemView.context, R.color.color_c4cbde)
            )
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
}

class CouponMainAdapter(val click: (Coupon) -> Unit) : BaseAdapter<Coupon>(isEmpty = true, isHeader = true, isFooter = true) {
    var isUsed = false

    //是否从网课详情过来，是：展示领取按钮或者已领取标识
    var fromLessonDetail = false
    override fun emptyImageRes(): Int {
        return R.drawable.ic_emtry_coupon
    }

    override fun emptyText(): String {
        return "暂无优惠券哦"
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return context.layoutInflater.inflate(R.layout.layout_coupon_item, null)
    }

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
            .setText(
                R.id.tv_limit,
                if (item.orderAmountLimit == "0") "无门槛优惠券" else "满${item.orderAmountLimit}可用"
            )
            .setText(
                R.id.tv_time,
                "${item.startTime?.replace("-", ".")}-${item.endTime?.replace("-", ".")}"
            )
            .setText(R.id.tv_price, priceStr)
//            .setText(
//                R.id.tv_rule_content,
//                "可用商品：${
//                    when (item.isUniversal) {
//                        "1" -> "全商品可用\n"
//                        "2" -> "全店网课可用\n"
//                        "3" -> "全店图书可用\n"
//                        else -> str
//                    }
//                }使用说明：${item.content ?: ""}"
//            )
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
        holder.setVisible(R.id.tv_live_label,if (item.isLive=="1")View.VISIBLE else View.GONE)
        if (isUsed) setTimeOut(holder)
        fromLesson(holder, item)
    }

    private fun fromLesson(holder: ViewHolder, item: Coupon) {
        if (fromLessonDetail) {
            if ("1" == item.isOwn) {
                holder.setVisible(R.id.tv_had, View.GONE)
                    .setVisible(R.id.img_coupon_tag, View.VISIBLE)
            } else {
                holder.setVisible(R.id.tv_had, View.VISIBLE)
                    .setVisible(R.id.img_coupon_tag, View.GONE)
            }
        } else {
            //img_coupon_tag 过期：isSelected：true；以获取：normal；其他隐藏
            holder.setVisible(R.id.tv_had, View.GONE)
                .setVisible(R.id.img_coupon_tag, if (isUsed) View.VISIBLE else View.GONE)
                .setSelected(R.id.img_coupon_tag, isUsed)
        }
        holder.setOnClickListener(R.id.tv_had) {
            click(item)
        }
    }

    private fun setTimeOut(holder: ViewHolder) {
        holder.setBackgroundResource(R.id.rl_item, R.drawable.shape_coupon_item_f5f5f9)
            .setBackgroundResource(R.id.rl_left, R.drawable.shape_10_l_fff)
            .setBackgroundResource(R.id.rl_right, R.drawable.shape_10_f8f8fa)
            .setBackgroundResource(R.id.tv_rule, R.drawable.shape_10_f5f5f9)
            .setBackgroundResource(R.id.v_line, R.mipmap.line_coupon_timeout)
            .setBackgroundResource(R.id.tv_live_label, R.drawable.shape_10_f5f5f9_tl_br)
            .setTextColor(
                R.id.tv_price,
                ContextCompat.getColor(holder.itemView.context, R.color.color_c4cbde)
            )
            .setTextColor(
                R.id.tv_live_label,
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

    override fun headerView(context: Context,parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, dip(20))
        }
    }

    override fun footerView(context: Context,parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, dip(40))
        }
    }
}
