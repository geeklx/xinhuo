package com.spark.peak.ui.mine.circle

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Circle
import org.jetbrains.anko.*

/**
 * 创建者： 霍述雷
 * 时间：  2018/4/16.
 */
class CircleAdapter(var follow: (Int, Circle, Boolean) -> Unit,
                    private val homepage: (String) -> Unit)
    : BaseAdapter<Circle>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: Circle) {
        holder.setText(R.id.tv_name, item.titile)
                .setText(R.id.tv_follow_num, "关注${item.attentionnum}人")
                .setText(R.id.tv_post_num, "发帖${item.nofcomment}条")
                .setCircularImage(R.id.iv_avatar, item.img)
                .setSelected(R.id.tv_follow, item.isFollow?:true)
        updateFollow(holder, holder.getView(R.id.tv_follow))
        holder.setOnClickListener(R.id.tv_follow) {
            follow(holder.layoutPosition, item, it.isSelected)
//            it.isSelected = !it.isSelected
//            updateFollow(holder, it)
        }
        holder.itemView.setOnClickListener { homepage(item.key) }
    }

    private fun updateFollow(holder: ViewHolder, v: View) {
        holder.setText(R.id.tv_follow, if (v.isSelected) "" else "关注")
        (v as TextView).setCompoundDrawablesWithIntrinsicBounds(if (v.isSelected) 0 else R.drawable.ic_follow,
                if (v.isSelected) R.drawable.ic_follow else 0, 0, 0)
        v.setPadding(v.paddingStart, if (v.isSelected) v.dip(5) else 0, v.paddingEnd, v.paddingBottom)
    }

    override fun convertView(context: Context, parent: ViewGroup): View =
            with(context) {
                relativeLayout {
                    lparams(matchParent, dip(61))
                    horizontalPadding = dip(15)
                    imageView {
                        id = R.id.iv_avatar
                    }.lparams(dip(37), dip(37)) {
                        centerVertically()
                    }

                    textView {
                        id = R.id.tv_name
                        textColor = resources.getColor(R.color.color_1e1e1e)
                        textSize = 15f
                    }.lparams {
                        rightOf(R.id.iv_avatar)
                        marginStart = dip(10)
//                        topMargin = dip(5)
                        sameTop(R.id.iv_avatar)
                    }

                    textView {
                        id = R.id.tv_follow_num
                        textColor = resources.getColor(R.color.color_999999)
                        textSize = 11f
                    }.lparams(dip(83), wrapContent) {
                        below(R.id.tv_name)
//                        topMargin = dip(5)
                        sameLeft(R.id.tv_name)
                    }
                    textView {
                        id = R.id.tv_post_num
                        textColor = resources.getColor(R.color.color_999999)
                        textSize = 11f
                    }.lparams {
                        rightOf(R.id.tv_follow_num)
                        sameTop(R.id.tv_follow_num)
                    }

                    textView {
                        id = R.id.tv_follow
//                        compoundDrawablePadding = dip(3)
                        textColor = resources.getColor(R.color.color_ffffff)
                        gravity = Gravity.CENTER
                        visibility=View.GONE
                        textSize = 12f
                        horizontalPadding = dip(8)
                        backgroundResource = R.drawable.bg_follow
//                        setBackgroundResource(R.drawable.bg_follow)
                    }.lparams(dip(55), dip(21)) {
                        alignParentRight()
                        centerVertically()
                    }
                }
            }
    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, matchParent)
            gravity = Gravity.CENTER_HORIZONTAL
            space().lparams(wrapContent,dip(120))
            imageView(R.mipmap.empty_3)
            textView("暂无圈子~") {
                gravity = Gravity.CENTER
                textSize = 14f
                textColor = resources.getColor(R.color.color_999999)
            }
        }
    }
}