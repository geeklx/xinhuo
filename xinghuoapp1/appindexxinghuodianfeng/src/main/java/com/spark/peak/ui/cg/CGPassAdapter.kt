package com.spark.peak.ui.cg

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import org.jetbrains.anko.*

/**
 * 创建者： 霍述雷
 * 时间：  2018/6/25.
 */
class CGPassAdapter(private val navigation: (Map<String, String>) -> Unit) : BaseAdapter<Map<String, String>>() {
    var status = -1
    override fun convert(holder: ViewHolder, item: Map<String, String>) {
        val s = status
        holder.setText(R.id.tv_page, "${holder.layoutPosition + 1}")
                .setText(R.id.tv_title, item["name"] ?: "")
                .setText(R.id.tv_content, item["condition"] ?: "")
                .setVisible(R.id.iv_status,
                        if (item["isthrough"]?.toInt() ?: 0 == 1) View.VISIBLE else View.GONE)
                // TDO: 2018/7/7 9:56 霍述雷 这里也缺少状态
                .setBackgroundResource(R.id.tv_page, load(holder.layoutPosition, item["isthrough"]?.toInt()
                        ?: 0))
        holder.itemView.setOnClickListener {
            val i = item["isthrough"]?.toInt() ?: -1
            if (i == 1 || i == 0 || holder.layoutPosition == 0)
                navigation(item)
            if (s == 1 && i == -1) navigation(item)
        }
        status = item["isthrough"]?.toInt() ?: -1
        if (item["isthrough"]?.toInt() ?: 0 != -1 || holder.layoutPosition == 0) {// TDO: 2018/7/7 9:56 霍述雷 这个地方也是状态管理的
            holder.setBackgroundResource(R.id.ll_content, R.mipmap.cg_bg_selected)
                    .setTextColorRes(R.id.tv_title, R.color.color_1e1e1e)
                    .setTextColorRes(R.id.tv_content, R.color.color_747474)
        } else {
            holder.setBackgroundResource(R.id.ll_content, R.mipmap.cg_bg_normal)
                    .setTextColorRes(R.id.tv_title, R.color.color_999999)
                    .setTextColorRes(R.id.tv_content, R.color.color_999999)
        }

    }

    @DrawableRes
    private fun load(position: Int, status: Int): Int =
            when (position) {
                0 -> when (status) {
                    1 -> R.mipmap.cg_start_selected
                    else -> R.mipmap.cg_start_normal
                }
                getDateCount() - 1 -> when (status) {
                    0, 1 -> R.mipmap.cg_end_selected
                    else -> R.mipmap.cg_end_normal
                }
                else -> when (status) {
                    1 -> R.mipmap.cg_center_selected
                    0 -> R.mipmap.cg_center
                    else -> R.mipmap.cg_center_normal
                }
            }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        linearLayout {
            horizontalPadding = dip(15)
            gravity = Gravity.CENTER_VERTICAL
            lparams(matchParent, wrapContent)
            textView {
                id = R.id.tv_page
                gravity = Gravity.CENTER
                textSize = 12f
                textColor = resources.getColor(R.color.color_ffffff)
                backgroundResource = R.mipmap.cg_start_normal
            }.lparams(dip(25), dip(100))
            linearLayout {
                id = R.id.ll_content
                leftPadding = dip(20)
                rightPadding = dip(15)
                gravity = Gravity.CENTER_VERTICAL
                backgroundResource = R.mipmap.cg_bg_normal
                verticalLayout {
                    textView {
                        id = R.id.tv_title
                        textSize = 15f
                        textColor = resources.getColor(R.color.color_1e1e1e)
                        lines = 1
                    }
                    space().lparams(wrapContent, dip(10))
                    textView {
                        id = R.id.tv_content
                        lines = 2
                        ellipsize = TextUtils.TruncateAt.END
                        textSize = 12f
                        textColor = resources.getColor(R.color.color_747474)
                    }
                }.lparams(0, wrapContent, 1f)
                imageView(R.mipmap.ic_pass) {
                    id = R.id.iv_status
                }.lparams(dip(44), dip(44))
            }.lparams(matchParent, dip(85)) {
                leftMargin = dip(10)
            }
        }
    }
}