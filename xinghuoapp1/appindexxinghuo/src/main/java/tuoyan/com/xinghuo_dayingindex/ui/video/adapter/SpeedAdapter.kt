package tuoyan.com.xinghuo_dayingindex.ui.video.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder

class SpeedAdapter(val onClick: (Float) -> Unit) : BaseAdapter<Float>() {
    var speed = 1.0f
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: ViewHolder, item: Float) {
        holder.setText(R.id.tv_content, "${item}x")
            .setSelected(R.id.tv_content, speed == item)
            .setTypeface(R.id.tv_content, Typeface.defaultFromStyle(if (speed == item) Typeface.BOLD else Typeface.NORMAL))
            .itemView.setOnClickListener {
                speed = item
                onClick(item)
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_speed, parent, false)
    }
}