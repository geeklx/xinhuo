package com.spark.peak.ui.wrongbook.adapter

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import org.jetbrains.anko.*

class SelecteAdapter(var onClick : (Int)->Unit) : BaseAdapter<String>() {
    var position = -1
    override fun convert(holder: ViewHolder, item: String) {
        holder.setText(R.id.tv_name, item)
        if (position == holder.layoutPosition) {
            holder.setBackgroundResource(R.id.tv_name, R.drawable.bg_shape_5_1482ff)
                    .setTextColorRes(R.id.tv_name, R.color.color_ffffff)
        } else {
            holder.setBackgroundResource(R.id.tv_name, R.drawable.bg_shape_e6e6e6)
                    .setTextColorRes(R.id.tv_name, R.color.color_666666)
        }
        holder.itemView.setOnClickListener {
            if (position != holder.layoutPosition){
                position = holder.layoutPosition
                notifyDataSetChanged()
                onClick(position)
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        linearLayout {
            padding = dip(5)
            textView {
                id = R.id.tv_name
                gravity = Gravity.CENTER
                textColor = resources.getColor(R.color.color_666666)
                backgroundResource = R.drawable.bg_shape_e6e6e6
            }.lparams(matchParent, dip(30))
        }
    }
}