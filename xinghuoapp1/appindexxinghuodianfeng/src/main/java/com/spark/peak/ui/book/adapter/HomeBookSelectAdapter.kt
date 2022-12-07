package com.spark.peak.ui.book.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.GradeBean


/**
 * Created by Zzz on 2020/12/23
 */

class HomeBookSelectAdapter(val onItemClick: (GradeBean) -> Unit) : BaseAdapter<GradeBean>() {
    var name = ""
        set(value) {
            field = value
        }

    override fun convert(holder: ViewHolder, item: GradeBean) {
        val tv_name = holder.getView(R.id.tv_name) as TextView
        tv_name.isSelected = name == item.name
        holder.itemView.setOnClickListener {
            if (name != item.name) {
                name = item.name
                notifyDataSetChanged()
                onItemClick(item)
            }
        }
        tv_name.text = item.name
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_home_book_select, parent, false)
    }
}