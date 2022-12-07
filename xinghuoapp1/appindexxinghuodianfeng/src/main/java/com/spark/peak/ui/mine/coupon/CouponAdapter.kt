package com.spark.peak.ui.mine.coupon

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Coupon
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

/**
 * 创建者： 霍述雷
 * 时间：  2018/4/16.
 */
class CouponAdapter(var more: () -> Unit = {}, var detail: (Coupon) -> Unit) : BaseAdapter<Coupon>(isMore = true, isEmpty = true) {
    override fun convert(holder: ViewHolder, item: Coupon) {
        holder.setText(R.id.tv_name, item.name)
                .setText(R.id.tv_expire_date, item.validitytime)
                .setText(R.id.tv_price, item.facevalue.toString())
                .setText(R.id.tv_description, item.content)
        holder.itemView.setOnClickListener { detail(item) }
        if (item.status != 0) {
            updateTextColor(holder.itemView)
            holder.setVisible(R.id.iv_status, View.VISIBLE)
                    .setImageResource(R.id.iv_status, if (item.status == 1) R.mipmap.ic_used else R.mipmap.ic_expired)
        } else {
            holder.setVisible(R.id.iv_status, View.GONE)
            holder.setTextColorRes(R.id.tv_name, R.color.color_1e1e1e)
                    .setTextColorRes(R.id.tv_expire_date, R.color.color_999999)
                    .setTextColorRes(R.id.tv_price, R.color.color_1482ff)
                    .setTextColorRes(R.id.tv_rmb, R.color.color_1482ff)
                    .setTextColorRes(R.id.tv_description, R.color.color_666666)
        }
    }

    override fun loadMore(holder: ViewHolder) {
        more()
    }

    private fun updateTextColor(view: View?) {
        if (view is ViewGroup) {
            (0 until view.childCount).forEach {
                val child = view.getChildAt(it)
                updateTextColor(child)
            }
        } else if (view is TextView) {
            view.textColor = view.resources.getColor(R.color.color_cccccc)
        }
    }


    override fun convertView(context: Context, parent: ViewGroup): View =
            with(context) {
                linearLayout {
                    gravity = Gravity.CENTER_HORIZONTAL
                    lparams(matchParent, wrapContent)
                    cardView {
                        cardElevation = dip(5).toFloat()
//                        backgroundResource = 0
                        relativeLayout {
                            backgroundResource = R.mipmap.bg_voucher
                            textView {
                                id = R.id.tv_name
                                textSize = 15f
                                textColor = resources.getColor(R.color.color_1e1e1e)
                            }.lparams {
                                topMargin = dip(23)
                                marginStart = dip(15)
                            }
                            textView {
                                id = R.id.tv_expire_date
                                textSize = 11f
                                textColor = resources.getColor(R.color.color_999999)
                            }.lparams {
                                sameLeft(R.id.tv_name)
                                below(R.id.tv_name)
                                topMargin = dip(14)
                            }
                            textView("￥") {
                                id = R.id.tv_rmb
                                textSize = 15f
                                textColor = resources.getColor(R.color.color_1482ff)
                            }.lparams {
                                leftOf(R.id.tv_price)
                                sameBottom(R.id.tv_price)
                                baselineOf( R.id.tv_price)
                            }
                            textView {
                                id = R.id.tv_price
                                textSize = 29f
                                textColor = resources.getColor(R.color.color_1482ff)
                            }.lparams {
                                alignParentRight()
                                centerVertically()
                                bottomMargin = dip(37)
                                marginEnd = dip(20)
                            }
                            textView {
                                gravity = Gravity.CENTER_VERTICAL
                                id = R.id.tv_description
                                textSize = 12f
                                textColor = resources.getColor(R.color.color_666666)
                            }.lparams(wrapContent, dip(37)) {
                                alignParentBottom()
                                marginStart = dip(15)
                                marginEnd = dip(15)
                            }
                            imageView {
                                id = R.id.iv_status
                                visibility = View.GONE
                            }.lparams(dip(65), dip(50)) {
                                marginStart = dip(205)
                            }
                        }
                    }.lparams(dip(345), dip(122)) {
                        topMargin = dip(5)
                        bottomMargin = dip(10)
                    }
                }

            }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, matchParent)
            gravity = Gravity.CENTER_HORIZONTAL
            space().lparams(wrapContent,dip(120))
            imageView(R.mipmap.empty_3)
            textView("暂无优惠券~"){
                gravity=Gravity.CENTER
                textSize=14f
                textColor=resources.getColor(R.color.color_999999)
            }
        }
    }
}
