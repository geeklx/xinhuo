package tuoyan.com.xinghuo_dayingindex.ui.video.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder

class TimerAdapter(val onClick: (Int) -> Unit) : BaseAdapter<Int>() {
    var timer = -2
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: ViewHolder, item: Int) {
        holder.setText(
            R.id.tv_content,
            when (item) {
                -2 -> "不开启"
                -1 -> "播完<br>当前音频"
                else -> "${item}分钟"
            }
        )
            .setSelected(R.id.tv_content, timer == item)
            .setTypeface(R.id.tv_content, Typeface.defaultFromStyle(if (timer == item) Typeface.BOLD else Typeface.NORMAL))
            .itemView.setOnClickListener {
                timer = item
                onClick(item)
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_speed, parent, false)
    }
}