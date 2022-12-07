package com.spark.peak.ui.study.book.adapter

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.MyBookPaper
import com.spark.peak.utlis.DeviceUtil
import org.jetbrains.anko.*

/**
 * Created by 李昊 on 2018/5/9.
 */
class PapersAdapter(var itemClick: (MyBookPaper) -> Unit) :
    BaseAdapter<MyBookPaper>(isEmpty = true, isFooter = true) {

    override fun convert(holder: ViewHolder, item: MyBookPaper) {
        holder.setText(R.id.tv_paper_name, item.resourceName)
            .setText(
                R.id.tv_paper_finish_count,
                item.practiseCount + "人已练习   " + if (item.createTime != null) item.createTime else ""
            )
            .setText(R.id.tv_paper_score, "")
        holder.itemView.setOnClickListener {
            itemClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        relativeLayout {
            lparams(matchParent, wrapContent)
            textView {
                id = R.id.tv_paper_name
                textSize = 15f
                textColor = Color.parseColor("#1e1e1e")
                ellipsize = TextUtils.TruncateAt.END
                maxLines = 1
            }.lparams{
                leftMargin = dip(15)
                topMargin = dip(15)
            }

            textView {
                id = R.id.tv_paper_finish_count
                textSize = 11f
                textColor = Color.parseColor("#999999")
            }.lparams{
                below(R.id.tv_paper_name)
                topMargin = dip(6)
                leftMargin = dip(15)
                bottomMargin = dip(15)
            }

            textView {
                id = R.id.tv_paper_score
                textSize = 15f
                textColor = Color.parseColor("#cccccc")
            }.lparams{
                alignParentRight()
                centerVertically()
                rightMargin = dip(15)
            }

            imageView {
                backgroundColor = Color.parseColor("#f7f7f7")
            }.lparams(matchParent,2){
                alignParentBottom()
            }
        }
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context){
        linearLayout {
            lparams(matchParent, matchParent)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            imageView {
                imageResource = R.drawable.empty_study
            }.lparams {
                topMargin = dip(40)
            }
            textView {
                text = "暂无配套资源"
                textSize = 15f
                textColor = Color.parseColor("#666666")
            }.lparams {
                topMargin = dip(10)
            }

        }
    }

    override fun footerView(context: Context): View {
        val view = TextView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            DeviceUtil.dp2px(context, 60f).toInt()
        )
        view.layoutParams = params
        return view
    }
}