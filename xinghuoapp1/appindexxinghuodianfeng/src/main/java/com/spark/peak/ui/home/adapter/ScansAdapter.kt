package com.spark.peak.ui.home.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.HomeQr
import com.spark.peak.utlis.DeviceUtil
import org.jetbrains.anko.textColor

class ScansAdapter(var onclick: (book: HomeQr) -> Unit) : BaseAdapter<HomeQr>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: HomeQr) {
        holder.setText(R.id.tv_name, item.name)
        holder.itemView.setOnClickListener {
            onclick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_scandf, parent, false)
    }

    override fun emptyView(context: Context, parent: ViewGroup): View {
        val text = TextView(context)
        text.text = "暂无扫码记录"
        text.textSize = 14f
        text.textColor = ContextCompat.getColor(context, R.color.color_999999)
        text.gravity = Gravity.CENTER
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 70f).toInt())
        text.layoutParams = params
        return text
    }
}