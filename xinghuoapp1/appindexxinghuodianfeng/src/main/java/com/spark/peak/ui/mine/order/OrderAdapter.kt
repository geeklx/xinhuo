package com.spark.peak.ui.mine.order

import android.content.Context
import android.graphics.Paint
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Order
import org.jetbrains.anko.*

/**
 * 创建者： 霍述雷
 * 时间：  2018/4/18.
 */
class OrderAdapter(private val pendingPayment: (String, Int) -> Unit,
                   private val toPay: (String,Int) -> Unit,
                   private val cancel: (String, Int) -> Unit,
                   private val delete: (String, Int) -> Unit,
                   private val comment: (String, Int) -> Unit,
                   private val more: () -> Unit) : BaseAdapter<Order>(isMore = true, isEmpty = true) {
    override fun convert(holder: ViewHolder, item: Order) {
        holder.setImageUrl(R.id.iv_img, item.img,R.drawable.default_lesson)
                .setText(R.id.tv_name, item.title)
                .setText(R.id.tv_lesson, "${item.paytime ?: 0}课时")
                .setText(R.id.tv_purchasers, "${item.buyers ?: 0}人购买")
                .setText(R.id.tv_original_price, "￥${item.originalcost}")
                .setText(R.id.tv_price, "￥${item.money}")
                .setOnClickListener(R.id.tv_cancel) { cancel(item.orderkey, holder.layoutPosition) }
                .setOnClickListener(R.id.tv_delete) { delete(item.orderkey, holder.layoutPosition) }
                .setOnClickListener(R.id.tv_pay) { toPay(item.orderkey,holder.layoutPosition) }
                .setOnClickListener(R.id.tv_comment) { comment(item.goodkey, holder.layoutPosition) }
        holder.itemView.setOnClickListener { pendingPayment(item.orderkey, holder.layoutPosition) }
        val tvStatus = holder.getView(R.id.tv_status) as TextView
        when (item.status) {
            0 -> {
                holder.setVisible(R.id.tv_cancel, View.VISIBLE)
                        .setVisible(R.id.tv_delete, View.GONE)
                        .setVisible(R.id.tv_pay, View.VISIBLE)
                        .setVisible(R.id.tv_comment, View.GONE)
                tvStatus.text = "待付款"
                tvStatus.textColor = tvStatus.resources.getColor(R.color.color_ff5b35)
            }
            1 -> {
                holder.setVisible(R.id.tv_cancel, View.GONE)
                        .setVisible(R.id.tv_delete, View.VISIBLE)
                        .setVisible(R.id.tv_pay, View.GONE)
                        .setVisible(R.id.tv_comment, View.VISIBLE)
                tvStatus.text = "已支付"
                tvStatus.textColor = tvStatus.resources.getColor(R.color.color_999999)
            }
            2 -> {
                holder.setVisible(R.id.tv_cancel, View.GONE)
                        .setVisible(R.id.tv_delete, View.VISIBLE)
                        .setVisible(R.id.tv_pay, View.GONE)
                        .setVisible(R.id.tv_comment, View.GONE)
                tvStatus.text = "已取消"
                tvStatus.textColor = tvStatus.resources.getColor(R.color.color_999999)
            }
            3 -> {
                holder.setVisible(R.id.tv_cancel, View.GONE)
                        .setVisible(R.id.tv_delete, View.VISIBLE)
                        .setVisible(R.id.tv_pay, View.GONE)
                        .setVisible(R.id.tv_comment, View.GONE)
                tvStatus.text = "已评价"
                tvStatus.textColor = tvStatus.resources.getColor(R.color.color_999999)
            }
            4 -> {
                holder.setVisible(R.id.tv_cancel, View.GONE)
                        .setVisible(R.id.tv_delete, View.VISIBLE)
                        .setVisible(R.id.tv_pay, View.GONE)
                        .setVisible(R.id.tv_comment, View.GONE)
                tvStatus.text = "已失效"
                tvStatus.textColor = tvStatus.resources.getColor(R.color.color_999999)
            }
            else->{
                holder.setVisible(R.id.tv_cancel, View.GONE)
                        .setVisible(R.id.tv_delete, View.GONE)
                        .setVisible(R.id.tv_pay, View.GONE)
                        .setVisible(R.id.tv_comment, View.GONE)
                tvStatus.text = "变异订单"
                tvStatus.textColor = tvStatus.resources.getColor(R.color.color_999999)
            }
        }
    }

    override fun loadMore(holder: ViewHolder) {
        more()
    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        verticalLayout {
            dividerDrawable = resources.getDrawable(R.drawable.divider)
            showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            backgroundResource = R.color.color_ffffff
            lparams(matchParent, wrapContent) { bottomMargin = dip(5) }
            relativeLayout {
                padding = dip(15)
                imageView {
                    id = R.id.iv_img
                }.lparams(dip(145), dip(91))
                imageView(R.mipmap.ic_play) {
                }.lparams(dip(37), dip(37)) {
                    centerVertically()
                    sameLeft(R.id.iv_img)
                    sameRight(R.id.iv_img)
                }
                textView {
                    id = R.id.tv_name
                    textSize = 16f
                    textColor = resources.getColor(R.color.color_1e1e1e)
                    lines = 2
                    ellipsize = TextUtils.TruncateAt.END
                }.lparams {
                    rightOf(R.id.iv_img)
                    marginStart = dip(10)
                }
                textView {
                    id = R.id.tv_lesson
                    textSize = 12f
                    textColor = resources.getColor(R.color.color_999999)
                }.lparams {
                    sameLeft(R.id.tv_name)
                    below(R.id.tv_name)
                    topMargin = dip(5)
                }
                textView {
                    id = R.id.tv_purchasers
                    textSize = 12f
                    textColor = resources.getColor(R.color.color_999999)
                }.lparams {
                    sameLeft(R.id.tv_name)
                    below(R.id.tv_lesson)
                    topMargin = dip(18)
                }
                textView {
                    id = R.id.tv_original_price
                    textSize = 12f
                    paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                    textColor = resources.getColor(R.color.color_999999)
                }.lparams {
                    sameBottom(R.id.tv_purchasers)
                    leftOf(R.id.tv_price)
                    marginEnd = dip(3)
//                    alignParentRight()
                }
                textView {
                    id = R.id.tv_price
                    textSize = 16f
                    textColor = resources.getColor(R.color.color_ff5b35)
                }.lparams {
                    sameBottom(R.id.tv_original_price)
                    alignParentRight()
                }
            }.lparams(matchParent, wrapContent)
            linearLayout {
                horizontalPadding = dip(15)
                gravity = Gravity.CENTER_VERTICAL
                textView {
                    id = R.id.tv_status
                    textSize = 12f
                }
                space().lparams(0, wrapContent, 1f)
                textView("取消订单") {
                    id = R.id.tv_cancel
                    backgroundResource = R.drawable.bg_shape_25_b4b4b4
                    textColor = resources.getColor(R.color.color_1e1e1e)
                    textSize = 12f
                    gravity = Gravity.CENTER
                }.lparams(dip(69), dip(25)) {
                    leftMargin = dip(15)
                }

                textView("删除订单") {
                    id = R.id.tv_delete
                    textColor = resources.getColor(R.color.color_1e1e1e)
                    backgroundResource = R.drawable.bg_shape_25_b4b4b4
                    textSize = 12f
                    gravity = Gravity.CENTER
                }.lparams(dip(69), dip(25)) {
                    leftMargin = dip(15)
                }

                textView("去支付") {
                    id = R.id.tv_pay
                    textColor = resources.getColor(R.color.color_1482ff)
                    backgroundResource = R.drawable.bg_shape_25_1482ff
                    textSize = 12f
                    gravity = Gravity.CENTER
                }.lparams(dip(54), dip(25)) {
                    leftMargin = dip(15)
                }

                textView("去评论") {
                    id = R.id.tv_comment
                    backgroundResource = R.drawable.bg_shape_25_b4b4b4
                    textSize = 12f
                    textColor = resources.getColor(R.color.color_1e1e1e)
                    gravity = Gravity.CENTER
                }.lparams(dip(54), dip(25)) {
                    leftMargin = dip(15)
                }

            }.lparams(matchParent, dip(37))
        }
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, matchParent)
            gravity = Gravity.CENTER_HORIZONTAL
            space().lparams(wrapContent,dip(120))
            imageView(R.mipmap.empty_3)
            textView("暂无订单~") {
                gravity = Gravity.CENTER
                textSize = 14f
                textColor = resources.getColor(R.color.color_999999)
            }
        }
    }
}
