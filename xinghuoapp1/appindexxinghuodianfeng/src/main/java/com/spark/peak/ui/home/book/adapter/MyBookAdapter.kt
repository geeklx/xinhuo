package com.spark.peak.ui.home.book.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.MyBookNetClass
import com.spark.peak.utlis.DeviceUtil

/**
 * Created by Zzz on 2020/12/25
 */

class MyBookAdapter(val onItemClick: (item: MyBookNetClass) -> Unit, val scanClick: () -> Unit, val delete: (item: MyBookNetClass, pos: Int) -> Unit) : BaseAdapter<MyBookNetClass>(isHeader = true) {
    var isEdit = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: ViewHolder, item: MyBookNetClass) {
        val context = holder.itemView.context
        holder.setVisible(R.id.ctl_edit, if (isEdit) View.VISIBLE else View.GONE)
                .setText(R.id.tv_title, item.name)
        Glide.with(context).load(item.img).error(R.drawable.default_book).into(holder.getView(R.id.img_cover) as ImageView)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
        holder.setOnClickListener(R.id.ctl_edit) {
            delete(item, holder.adapterPosition)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_my_book, parent, false)
    }

    override fun header(holder: ViewHolder) {
        super.header(holder)
        holder.setOnClickListener(R.id.img_add) {
            scanClick()
        }
    }

    override fun headerView(context: Context): View {
        val layout = LinearLayout(context)
        layout.gravity = Gravity.CENTER_HORIZONTAL
        val imageView = ImageView(context)
        imageView.setImageResource(R.mipmap.icon_scan_book)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.id = R.id.img_add
        val params = LinearLayout.LayoutParams(DeviceUtil.dp2px(context, 105f).toInt(), DeviceUtil.dp2px(context, 137f).toInt())
        params.topMargin = DeviceUtil.dp2px(context, 15f).toInt()
        imageView.layoutParams = params
        layout.addView(imageView)
        return layout
    }
}