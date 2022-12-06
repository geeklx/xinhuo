package tuoyan.com.xinghuo_dayingindex.ui.book.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder

class LoopAdapter(val onClick: (Int) -> Unit) : BaseAdapter<Int>() {
    var loopNum = 1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: ViewHolder, item: Int) {
        holder.setText(
            R.id.tv_content,
            when (item) {
                -1 -> "无限"
                else -> "${item}次"
            }
        )
            .setSelected(R.id.tv_content, loopNum == item)
            .setTypeface(R.id.tv_content, Typeface.defaultFromStyle(if (loopNum == item) Typeface.BOLD else Typeface.NORMAL))
            .itemView.setOnClickListener {
                loopNum = item
                onClick(item)
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_speed, parent, false)
    }
}