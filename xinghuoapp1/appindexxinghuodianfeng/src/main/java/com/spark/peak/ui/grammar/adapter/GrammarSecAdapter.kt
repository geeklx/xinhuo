package com.spark.peak.ui.grammar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Grammar

/**
 * Created by Zzz on 2020/12/23
 */

class GrammarSecAdapter(val onItemClick: (Grammar,Grammar) -> Unit) : BaseAdapter<Grammar>() {
    private var selectIndex = -1
    override fun convert(holder: ViewHolder, item: Grammar) {
        val context = holder.itemView.context
        val rlv_grammar = holder.getView(R.id.rlv_grammar) as RecyclerView
        rlv_grammar.isNestedScrollingEnabled = false
        rlv_grammar.layoutManager = LinearLayoutManager(context)
        val threeAdapter = GrammarThreeAdapter {
            onItemClick(item,it)
        }
        rlv_grammar.adapter = threeAdapter
        threeAdapter.setData(item.catalogList)
        holder.setVisible(R.id.rlv_grammar, if (selectIndex == holder.adapterPosition) View.VISIBLE else View.GONE)
                .setSelected(R.id.tv_grammar_name, selectIndex == holder.adapterPosition)
        holder.setOnClickListener(R.id.tv_grammar_name) {
            if (selectIndex == holder.adapterPosition) {
                selectIndex = -1
            } else {
                selectIndex = holder.adapterPosition
            }
            notifyDataSetChanged()
        }
        holder.setText(R.id.tv_index, item.sort)
                .setText(R.id.tv_grammar_name, item.name)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_grammar_sec, parent, false)
    }
}

class GrammarThreeAdapter(val onItemClick: (Grammar) -> Unit) : BaseAdapter<Grammar>() {
    override fun convert(holder: ViewHolder, item: Grammar) {
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
        holder.setText(R.id.tv_index, item.sort).setText(R.id.tv_scan_name, item.name)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_grammar_three, parent, false)
    }
}