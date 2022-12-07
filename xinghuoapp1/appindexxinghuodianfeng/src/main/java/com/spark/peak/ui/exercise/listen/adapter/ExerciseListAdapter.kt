package com.spark.peak.ui.scan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.SpacialPaper

/**
 */
class ExerciseListAdapter(val onItemClick: (SpacialPaper) -> Unit) : BaseAdapter<SpacialPaper>() {
    override fun convert(holder: ViewHolder, item: SpacialPaper) {
        holder.setText(R.id.tv_name, "${item.name} (${item.totalCount})")
                .setText(R.id.tv_state, if (item.state == "1") "已完成" else "")
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = LayoutInflater.from(context).inflate(R.layout.layout_exercise, parent, false)
}