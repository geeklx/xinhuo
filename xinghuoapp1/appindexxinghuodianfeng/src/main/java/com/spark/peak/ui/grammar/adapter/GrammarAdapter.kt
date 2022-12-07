package com.spark.peak.ui.grammar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Grammar

/**
 * Created by Zzz on 2020/12/23
 */

class GrammarAdapter(val onItemClick: (Grammar) -> Unit) : BaseAdapter<Grammar>() {
    override fun convert(holder: ViewHolder, item: Grammar) {
        holder.itemView.setOnClickListener { onItemClick(item) }
        holder.setText(R.id.tv_index, formatNumber(item.sort))
                .setText(R.id.tv_scan_name, item.name)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_grammardf, parent, false)
    }

    private fun formatNumber(num: String): String {
        return if (num.length < 2) {
            "0$num"
        } else {
            "$num"
        }
    }
}