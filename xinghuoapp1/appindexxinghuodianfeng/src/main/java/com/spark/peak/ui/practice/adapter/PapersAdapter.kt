package com.spark.peak.ui.practice.adapter

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import org.jetbrains.anko.*

/**
 * Created by 李昊 on 2018/5/17.
 */
class PapersAdapter(val queue: Boolean, var onClick: (Map<String, String>) -> Unit) : BaseAdapter<Map<String, String>>() {
    var score: String = "0"
    override fun convert(holder: ViewHolder, item: Map<String, String>) {
        val s = score
        holder.setText(R.id.tv_paper_name, item["papername"] ?: "")
                .setText(R.id.tv_paper_finish_count, "${item["practisecount"] ?: "0"}人练过")
                .setText(R.id.tv_paper_score, if (item["score"].isNullOrBlank()) "" else "${item["score"]}分")
        if (queue)
            holder.setTextColorRes(R.id.tv_paper_name, if (s.isBlank() && holder.layoutPosition != 0)
                R.color.color_999999
            else
                R.color.color_1e1e1e)
        holder.itemView.setOnClickListener {
            if (queue) {
                if (s.isNotBlank() || holder.layoutPosition == 0) onClick(item)
            } else onClick(item)
        }
        score = item["score"] ?: ""
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        relativeLayout {
            id = R.id.rl_top
            lparams(matchParent, wrapContent)
            backgroundResource = R.color.color_ffffff
            textView {
                id = R.id.tv_paper_name
                textSize = 15f
                textColor = Color.parseColor("#1e1e1e")
                ellipsize = TextUtils.TruncateAt.END
                maxLines = 1
            }.lparams {
                leftMargin = dip(15)
                topMargin = dip(15)
            }

            textView {
                id = R.id.tv_paper_finish_count
                textSize = 11f
                textColor = Color.parseColor("#999999")
            }.lparams {
                below(R.id.tv_paper_name)
                topMargin = dip(6)
                leftMargin = dip(15)
                bottomMargin = dip(15)
            }

            textView {
                id = R.id.tv_paper_score
                textSize = 12f
                textColor = Color.parseColor("#fa7062")
            }.lparams {
                alignParentRight()
                centerVertically()
                rightMargin = dip(15)
            }

            imageView {
                backgroundColor = Color.parseColor("#f7f7f7")
            }.lparams(matchParent, 2) {
                alignParentBottom()
            }
        }
    }
}