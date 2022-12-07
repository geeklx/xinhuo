package com.spark.peak.ui.lesson.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Grade

/**
 */
class GradeAdapter(val onItemClick: (Grade) -> Unit) : BaseAdapter<Grade>() {
    var selectKey = ""
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (getData()[position].type == "1") {
                    return layoutManager.spanCount
                }
                return 1
            }
        }
    }

    override fun convert(holder: ViewHolder, item: Grade) {
        holder.setText(R.id.tv_name, item.name)
        val tv_name = holder.getView(R.id.tv_name) as TextView
        tv_name.isSelected = selectKey == item.id
        holder.itemView.setOnClickListener {
            if (selectKey != item.id) {
                selectKey = item.id
                onItemClick(item)
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_home_book_selectdf, parent, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TITLE) {
            return ViewHolder(titleView(parent))
        } else {
            return super.onCreateViewHolder(parent, viewType)
        }
    }

    private fun titleView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.layout_grade_listdf, parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == TITLE) {
            bindTitleHolder(holder, getData()[position - if (getIsHeader()) 1 else 0])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    private fun bindTitleHolder(holder: ViewHolder, grade: Grade) {
        holder.setText(R.id.tv_name, grade.name)
    }


    override fun getItemViewType(position: Int): Int {
        if (getData().isNotEmpty()) {
            val item = getData()[position]
            if (item.type == "1") return TITLE
        }
        return super.getItemViewType(position)
    }

    companion object {
        val TITLE = 9
    }
}