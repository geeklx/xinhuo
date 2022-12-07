package com.spark.peak.ui.grammar.adapter

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.GrammarSearch
import org.jetbrains.anko.*

/**
 * Created by Zzz on 2020/12/23
 */

class GrammarSearchAdapter(val onItemClick: (GrammarSearch) -> Unit) : BaseAdapter<GrammarSearch>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: GrammarSearch) {
        holder.itemView.setOnClickListener { onItemClick(item) }
        holder.setText(R.id.tv_title, replaceContent(item.title)).setText(R.id.tv_content, replaceContent(item.content))
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_grammar_searchdf, parent, false)
    }

    private fun replaceContent(content: String): Spanned {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            Html.fromHtml(content.replace("<em>", "<font color='#2ca7ff'>").replace("</em>", "</font>"), Html.FROM_HTML_MODE_COMPACT)
            Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
        } else {
//            Html.fromHtml(content.replace("<em>", "<font color='#2ca7ff'>").replace("</em>", "</font>"))
            Html.fromHtml(content)
        }
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, matchParent)
            gravity = Gravity.CENTER_HORIZONTAL
            space().lparams(wrapContent, dip(120))
            imageView(R.mipmap.empty_3)
            textView("暂无内容~") {
                gravity = Gravity.CENTER
                textSize = 14f
                textColor = resources.getColor(R.color.color_999999)
            }
        }
    }
}