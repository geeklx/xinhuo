package com.spark.peak.ui.scan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.HomeQr

/**
 * Created by 李昊 on 2018/5/23.
 */
class ScanHistoryAdapter(val onItemClick: (HomeQr) -> Unit) : BaseAdapter<HomeQr>() {
    override fun convert(holder: ViewHolder, item: HomeQr) {
        holder.setText(R.id.tv_name, item.name)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = LayoutInflater.from(context).inflate(R.layout.layout_scan_historydf, parent, false)
}