package com.spark.peak.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.HomeBook

class BooksAdapter(var onclick: (book: HomeBook) -> Unit, val scanClick: () -> Unit) : BaseAdapter<HomeBook>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: HomeBook) {
        Glide.with(holder.itemView.context).load(item.img).error(R.drawable.default_book).into(holder.getView(R.id.img_book_cover) as ImageView)
        holder.setText(R.id.tv_book_name, item.name)
        holder.itemView.setOnClickListener {
            onclick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_bookdf, parent, false)
    }

    override fun emptyView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_home_book_emptydf, parent, false)
    }

    override fun empty(holder: ViewHolder) {
        super.empty(holder)
        holder.setOnClickListener(R.id.tv_scan) {
            scanClick()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (getItemViewType(position) == EMPTY) {
                    return layoutManager.spanCount
                }
                return 1
            }

        }
    }

    override fun getItemCount(): Int {
        if (getData().size >= 3) {
            return 3
        } else {
            return super.getItemCount()
        }
    }
}