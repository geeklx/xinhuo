package com.spark.peak.ui.netLessons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.ResourceItem

/**
 * Created by Zzz on 2020/12/24
 */

class NetListAdapter : BaseAdapter<ResourceItem>() {
    override fun convert(holder: ViewHolder, item: ResourceItem) {
        holder.setText(R.id.tv_title, item.name)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_catalog, parent, false)
    }
}