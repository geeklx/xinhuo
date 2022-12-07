package com.spark.peak.ui.book.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.AudioRes
import org.jetbrains.anko.*


/**
 * Created by Zzz on 2020/12/23
 */

class HomeBookAdapter(val onItemClick: (Int) -> Unit) : BaseAdapter<AudioRes>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: AudioRes) {
        holder.setText(R.id.tv_title, item.name.replace("/AA/", "   "))
        holder.itemView.setOnClickListener {
            onItemClick(holder.adapterPosition)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_home_bookdf, parent, false)
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