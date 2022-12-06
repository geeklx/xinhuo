package tuoyan.com.xinghuo_dayingindex.ui.video.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder

class TextSizeAdapter(val onClick: (String) -> Unit) : BaseAdapter<String>() {
    var textsize = "常规"
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: ViewHolder, item: String) {
        holder.setText(R.id.tv_content, item)
            .setSelected(R.id.tv_content, textsize == item)
            .setTypeface(R.id.tv_content, Typeface.defaultFromStyle(if (textsize == item) Typeface.BOLD else Typeface.NORMAL))
            .itemView.setOnClickListener {
                textsize = item
                onClick(item)
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_speed, parent, false)
    }
}