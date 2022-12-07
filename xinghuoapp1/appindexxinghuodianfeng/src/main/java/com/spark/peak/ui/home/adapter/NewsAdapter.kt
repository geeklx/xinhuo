package com.spark.peak.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.EduInfo

/**
 * Created by 李昊 on 2018/4/16.
 */
class NewsAdapter(var itemClick: (EduInfo)->Unit) : BaseAdapter<EduInfo>(){
    override fun convert(holder: ViewHolder, item: EduInfo) {
        holder.setText(R.id.tv_news_title,item.title)
        holder.setText(R.id.tv_news_mark,item.pv.toString()+"人阅读")
        holder.setImageUrl(R.id.iv_news,item.img?:"",R.drawable.default_news,4)
                .setText(R.id.tv_news_source,item.source?:"")
        holder.itemView.setOnClickListener {
            itemClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = LayoutInflater.from(context).inflate(R.layout.item_home_news,null)

}