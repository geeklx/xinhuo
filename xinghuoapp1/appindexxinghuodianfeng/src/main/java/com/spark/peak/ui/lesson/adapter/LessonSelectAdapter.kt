package com.spark.peak.ui.lesson.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Grade


/**
 * Created by Zzz on 2020/12/23
 */

class LessonSelectAdapter(val onItemClick: (Grade) -> Unit) : BaseAdapter<Grade>() {
    private var selectIndex = 0
    override fun convert(holder: ViewHolder, item: Grade) {
        val tv_name = holder.getView(R.id.tv_name) as TextView
        tv_name.isSelected = selectIndex == holder.adapterPosition
        holder.itemView.setOnClickListener {
            if (selectIndex != holder.adapterPosition) {
                selectIndex = holder.adapterPosition
                notifyDataSetChanged()
                onItemClick(item)
            }
        }
        tv_name.text = item.name
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_home_book_selectdf, parent, false)
    }
}