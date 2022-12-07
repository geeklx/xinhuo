package com.spark.peak.ui.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.NetClass
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.rightPadding

/**
 * Created by 李昊 on 2018/4/16.
 */
class NetclassAdapter(var itemClick: (String)-> Unit) : BaseAdapter<NetClass>(){
    override fun convert(holder: ViewHolder, item: NetClass) {
        holder.setImageUrl(R.id.home_netclass_img,item.img?:"",R.drawable.default_lesson,4)
        holder.itemView.setOnClickListener {
            itemClick(getData()[holder.adapterPosition].key?:"")
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(RelativeLayout(context)){
        rightPadding = dip(10)
        imageView {
            id = R.id.home_netclass_img
            scaleType = ImageView.ScaleType.FIT_XY
            layoutParams = ViewGroup.LayoutParams(dip(163),dip(102))
        }
        this
    }
}