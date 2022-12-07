package com.spark.peak.ui.netLessons

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Comment
import com.spark.peak.utlis.GradUtil
import org.jetbrains.anko.*

/**
 * 创建者： 霍述雷
 * 时间：  2018/4/16.
 */
class NetCommentAdapter(var commentNum: String = "0",
                        private val loadMore: () -> Unit,
                        var homepaoge: (String) -> Unit)
    : BaseAdapter<Comment>(isHeader = true, isMore = true, isEmpty = true) {

    override fun convert(holder: ViewHolder, item: Comment) {
        holder.setText(R.id.tv_name, item.name)
                .setText(R.id.tv_comment, item.content)
                .setText(R.id.tv_grade, GradUtil.parseGradStr(item.section, item.grade))
                .setText(R.id.tv_create_time, item.time)
                .setCircularImage(R.id.iv_avatar, item.img)
//                .setOnClickListener(R.id.iv_avatar){homepaoge(item.commentkey)}
    }

    override fun loadMore(holder: ViewHolder) {
        loadMore()
    }

    override fun convertView(context: Context, parent: ViewGroup): View =
            with(context) {
                relativeLayout {
                    lparams(matchParent, wrapContent)
                    horizontalPadding = dip(15)
                    topPadding = dip(15)
                    bottomPadding = dip(10)
                    imageView {
                        id = R.id.iv_avatar
                    }.lparams(dip(37), dip(37))

                    textView {
                        id = R.id.tv_name
                        textColor = resources.getColor(R.color.color_747474)
                        textSize = 12f
                    }.lparams {
                        rightOf(R.id.iv_avatar)
                        marginStart = dip(6)
                        topMargin = dip(7)
                        sameTop(R.id.iv_avatar)
                    }
                    textView {
                        id = R.id.tv_grade
                        gravity = Gravity.CENTER
                        backgroundResource = R.drawable.bg_grade
                        textColor = resources.getColor(R.color.color_ffffff)
                        textSize = 7f
                    }.lparams(dip(25), dip(12)) {
                        rightOf(R.id.tv_name)
                        marginStart = dip(5)
                        topMargin = dip(3)
                        sameTop(R.id.tv_name)
                    }
                    textView {
                        id = R.id.tv_comment
                        textColor = resources.getColor(R.color.color_1e1e1e)
                        setLineSpacing(dip(5).toFloat(), 1f)
                        textSize = 14f
                    }.lparams(matchParent, wrapContent) {
                        below(R.id.iv_avatar)
//                        topMargin = dip(5)
                        sameLeft(R.id.tv_name)
                    }
                    textView {
                        id = R.id.tv_create_time
                        textColor = resources.getColor(R.color.color_a7a7a7)
                        textSize = 11f
                    }.lparams(matchParent, wrapContent) {
                        below(R.id.tv_comment)
                        topMargin = dip(10)
                        sameLeft(R.id.tv_name)
                    }

                }
            }

    override fun header(holder: ViewHolder) {
        holder.setText(R.id.tv_comment_num, "全部评论（$commentNum 条）")

    }

    override fun headerView(context: Context) = with(context) {
        verticalLayout {
            lparams(matchParent, wrapContent)
            topPadding = dip(15)
            textView {
                id = R.id.tv_comment_num
                leftPadding = dip(15)
                textSize = 12f
                textColor = resources.getColor(R.color.color_666666)
            }
        }
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, matchParent)
            gravity = Gravity.CENTER
            imageView(R.mipmap.empty_4)
            textView("暂无评论~") {
                gravity = Gravity.CENTER
                textSize = 14f
                textColor = resources.getColor(R.color.color_999999)
            }
        }
    }
}