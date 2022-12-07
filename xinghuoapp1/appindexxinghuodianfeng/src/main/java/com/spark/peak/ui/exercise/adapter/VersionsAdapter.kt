package com.spark.peak.ui.exercise.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Version
import org.jetbrains.anko.*

/**
 * Created by 李昊 on 2018/5/3.
 */
class VersionsAdapter(var onclick : (key: String)-> Unit) : BaseAdapter<Version>(){

    var selectedPosition = 0
    override fun convert(holder: ViewHolder, item: Version) {
        holder.itemView.backgroundColor = if (holder.adapterPosition == selectedPosition) Color.parseColor("#ffffff") else Color.parseColor("#f7f7f7")
        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onclick(item.key?:"")
        }
        holder.setText(R.id.tv_version_name,item.name)
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        relativeLayout {
            lparams(matchParent,dip(42))
            backgroundColor = Color.parseColor("#f7f7f7")
            textView {
                id = R.id.tv_version_name
                textSize = 12f
                textColor = Color.parseColor("#747474")
            }.lparams(wrapContent, wrapContent){
                centerInParent()
            }
        }
    }


}