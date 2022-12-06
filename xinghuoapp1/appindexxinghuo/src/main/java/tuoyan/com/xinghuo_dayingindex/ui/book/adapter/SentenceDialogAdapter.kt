package tuoyan.com.xinghuo_dayingindex.ui.book.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.LrcDetail
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

class SentenceDialogAdapter(val onClick: (LrcDetail) -> Unit) : BaseAdapter<LrcDetail>(isFooter = true) {
    var duration = 0L
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    //当前播放的位置
    private var currentId = "0"

    fun setCurrentId(id: String) {
        this.currentId = id
        notifyDataSetChanged()
    }

    //展示全部 展示标记
    var onlyShowCollect = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: ViewHolder, item: LrcDetail) {
        if ("0" == currentId && holder.layoutPosition == 0) {
            currentId = item.lrcDetailKey
        }
        val tv_en = holder.getView(R.id.tv_en) as TextView
        val enStr = if ("1" == item.isSign) {
            "\u3000\u3000\u3000\u0020\u0020${item.enContent}"
        } else {
            item.enContent
        }
        tv_en.text = enStr
        holder.setVisible(R.id.tv_collect, if ("1" == item.isSign) View.VISIBLE else View.GONE)
            .setText(R.id.tv_ch, item.cnContent).setText(R.id.tv_num, "已听${item.listenNum}次")
            .setText(R.id.tv_time, getFormatTime(holder, item)).setText(R.id.tv_sort, item.sort)
            .setSelected(R.id.tv_en, currentId == item.lrcDetailKey)
            .setTypeface(R.id.tv_en, Typeface.defaultFromStyle(if (currentId == item.lrcDetailKey) Typeface.BOLD else Typeface.NORMAL))
            .itemView.setOnClickListener {
                currentId = item.lrcDetailKey
                onClick(item)
            }
        if (onlyShowCollect && "1" != item.isSign) {
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
            holder.itemView.layoutParams = params
        } else {
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            holder.itemView.layoutParams = params
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_sentence_dialog, parent, false)
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }

    private fun getFormatTime(holder: ViewHolder, item: LrcDetail): String {
        return if (getDateCount() - 1 == holder.layoutPosition) {
            formatTime(duration - item.startTime)
        } else {
            try {
                formatTime(item.endTime.toLong() - item.startTime)
            } catch (e: Exception) {
                "00:00"
            }
        }
    }

    private fun formatTime(time: Long): String {
        val i = time / 1000
        val s = i % 60
        val m = i / 60
        return "${if (m < 10) "0$m" else "$m"}:${if (s < 10) "0$s" else "$s"}"
    }
}