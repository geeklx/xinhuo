package com.spark.peak.ui.mine.offline

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.BookRes
import org.jetbrains.anko.*

/**
 * 创建者： 霍述雷
 * 时间：  2018/4/16.
 */
class OfflineAdapter(var click: (String, BookRes) -> Unit) : BaseAdapter<BookRes>(isEmpty = true) {
    var type: String = "pt"

    override fun convert(holder: ViewHolder, item: BookRes) {
        holder.setText(R.id.tv_title, item.name)
                .setText(R.id.tv_sub_title, item.num + "个资源")
                .setImageResource(R.id.img_cover, if (type == "pt") R.mipmap.icon_offline_book else R.mipmap.icon_offline_lesson)
                .itemView.setOnClickListener {
                    click(type, item)
                }
        holder.itemView.setOnLongClickListener {
            // TODO: 2018/10/25 15:29 霍述雷 长按
            true
        }
    }


    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_offlinedf, parent, false)
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, matchParent)
            gravity = Gravity.CENTER_HORIZONTAL
            space().lparams(wrapContent, dip(120))
            imageView(R.mipmap.empty_3)
            textView("暂无离线文件~") {
                gravity = Gravity.CENTER
                textSize = 14f
                textColor = resources.getColor(R.color.color_999999)
            }
        }
    }
}
