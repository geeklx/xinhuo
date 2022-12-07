package com.spark.peak.ui.cg

import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

/**
 * 创建者： 霍述雷
 * 时间：  2018/6/25.
 */
class CGPracticeAdapter(private val loadMore: () -> Unit, private val navigation: (Map<String, String>) -> Unit) : BaseAdapter<Map<String, String>>(isMore = true) {
    override fun convert(holder: ViewHolder, item: Map<String, String>) {
        holder.setText(R.id.tv_title, item["name"] ?: "")
                .setText(R.id.tv_content, Html.fromHtml(item["summary"] ?: ""))
                .setImageUrl(R.id.iv_img, item["image"] ?: "",placeHolder = R.mipmap.ic_cg)
                .setImageResource(R.id.iv_status,
                        if (item["isfinish"]?.toInt() ?: 0 == 0)
                            R.mipmap.ic_go_complete
                        else R.mipmap.ic_complete)
        // TOO: 2018/7/6 15:26 霍述雷 这里缺个状态
        holder.itemView.setOnClickListener {
            navigation(item)
        }

    }

    override fun loadMore(holder: ViewHolder) {
        loadMore()
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        linearLayout {
            lparams(matchParent, wrapContent)
            cardView {
                cardElevation = dip(2).toFloat()
                linearLayout {
                    rightPadding = dip(15)
                    gravity = Gravity.CENTER_VERTICAL
                    imageView {
                        id = R.id.iv_img
                    }.lparams(dip(55), dip(55)) {
                        margin = dip(10)
                    }
                    verticalLayout {
                        textView {
                            id = R.id.tv_title
                            textSize = 16f
                            textColor = resources.getColor(R.color.color_1e1e1e)
                            lines = 1
                        }
                        textView {
                            id = R.id.tv_content
                            lines = 2
                            textSize = 12f
                            ellipsize = TextUtils.TruncateAt.END
                            textColor = resources.getColor(R.color.color_666666)
                        }
                    }.lparams(0, wrapContent, 1f) {
                        marginEnd = dip(15)
                    }
                    imageView(R.mipmap.ic_complete) {
                        id = R.id.iv_status
                    }.lparams(dip(60), dip(25))
                }.lparams(matchParent, dip(75))
            }.lparams(matchParent, wrapContent) {
                horizontalMargin = dip(15)
                verticalMargin = dip(7.5f)
            }
        }
    }
}