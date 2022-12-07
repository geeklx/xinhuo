package com.spark.peak.ui.mine.offline

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.OfflineRes
import org.jetbrains.anko.*

/**
 * 创建者： 霍述雷
 * 时间：  2018/4/16.
 */
class OfflineResAdapter(var delete: (Int, OfflineRes) -> Unit,var click: (Int, OfflineRes) -> Unit) : BaseAdapter<OfflineRes>(isEmpty = true) {
    var type: Int = 0

    override fun convert(holder: ViewHolder, item: OfflineRes) {
        holder.setText(R.id.tv_title, item.name ?: "")
                .setImageResource(R.id.iv_icon, when {
                    item.name.endsWith("mp4") -> R.drawable.src_type_video
                    item.name.endsWith("mp3") -> R.drawable.src_type_audio
                    item.name.endsWith("pdf") -> R.drawable.src_type_content
                    else -> R.drawable.src_type_doc
                })
                .itemView.setOnClickListener {
            click(holder.adapterPosition, item)
        }
        holder.itemView.setOnLongClickListener {
            // TODO: 2018/10/25 15:29 霍述雷 长按
            delete(holder.adapterPosition, item)
            true
        }
    }


    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        linearLayout {
            lparams(matchParent, dip(60))
            gravity = Gravity.CENTER_VERTICAL
            horizontalPadding = dip(15)
            gravity = Gravity.CENTER_VERTICAL
            imageView {
                id = R.id.iv_icon
            }.lparams(dip(20), dip(20))
            space().lparams(dip(15), wrapContent)
            textView {
                id = R.id.tv_title
                lines = 1
                textSize = 15f
                textColor = resources.getColor(R.color.color_1e1e1e)
            }

        }
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
