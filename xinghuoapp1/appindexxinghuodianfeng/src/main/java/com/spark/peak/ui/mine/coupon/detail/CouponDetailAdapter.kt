package com.spark.peak.ui.mine.coupon.detail

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Product
import org.jetbrains.anko.*

/**
 * 创建者： 霍述雷
 * 时间：  2018/4/16.
 */
class CouponDetailAdapter : BaseAdapter<Product>() {

    override fun convert(holder: ViewHolder, item: Product) {
        holder.setImageUrl(R.id.iv_img, "https://3-im.guokr.com/AhubuSLSJbBENORLGQ5eFWNptElPqRmnmu-hprJxRWDeAgAArAEAAFBO.png")
                .setText(R.id.tv_name, item.name)
                .setText(R.id.tv_lesson, "520课时")
                .setText(R.id.tv_purchasers, "15555人购买")
                .setText(R.id.tv_original_price, "￥599")
                .setText(R.id.tv_price, "￥299")
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        relativeLayout {
            padding = dip(15)
            lparams(matchParent, wrapContent)
            backgroundColor = resources.getColor(R.color.color_white)
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
            }
            textView {
                id = R.id.tv_price
                textSize = 16f
                textColor = Color.parseColor("#1e1e1e")
            }.lparams {
                sameBottom(R.id.tv_original_price)
                alignParentRight()
            }
        }
    }


}
