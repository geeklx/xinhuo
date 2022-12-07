package com.spark.peak.ui.lesson.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.NetLessonItem

/**
 * Created by 李昊 on 2018/4/20.
 */
class LessonsAdapter(var onItemClick: (NetLessonItem) -> Unit) : BaseAdapter<NetLessonItem>(isEmpty = true) {

    override fun convert(holder: ViewHolder, item: NetLessonItem) {
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
        Glide.with(holder.itemView.context).load(item.img).error(R.drawable.default_book).into(holder.getView(R.id.img_cover) as ImageView)
        holder.setText(R.id.tv_title, item.title)
                .setText(R.id.tv_word, "${item.period}课时")
                .setText(R.id.tv_num, "${item.buyers}人已学")
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_lessondf, parent, false)
    }

    override fun emptyView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_lesson_emptydf, parent, false)
    }
}