package tuoyan.com.xinghuo_dayingindex.ui.video.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.LrcRow
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

class AudioEnChAdapter(val onClick: (LrcRow) -> Unit) : BaseAdapter<LrcRow>(isHeader = true, isFooter = true) {
    val EN_SIZE = mutableMapOf("小号" to 14, "常规" to 16, "中号" to 18, "大号" to 21, "特大号" to 24)
    val CH_SIZE = mutableMapOf("小号" to 12, "常规" to 14, "中号" to 15, "大号" to 18, "特大号" to 20)

    var noShowCH = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var textSize = "常规"
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var title = ""
    var currentPos = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: ViewHolder, item: LrcRow) {
        val tv_en = holder.getView(R.id.tv_en) as TextView
        val tv_ch = holder.getView(R.id.tv_ch) as TextView
        tv_en.textSize = (EN_SIZE[textSize] ?: 16).toFloat()
        tv_ch.textSize = (CH_SIZE[textSize] ?: 14).toFloat()
        val content = item.rowData.split("<ch>")
        holder.setText(R.id.tv_en, content[0]).setText(R.id.tv_ch, if (content.size > 1) content[1] else content[0])
            .setSelected(R.id.tv_en, currentPos == holder.layoutPosition)
            .setTypeface(R.id.tv_en, Typeface.defaultFromStyle(if (currentPos == holder.layoutPosition) Typeface.BOLD else Typeface.NORMAL))
            .setVisible(R.id.tv_ch, if (noShowCH) View.GONE else View.VISIBLE)
            .itemView.setOnClickListener {
                onClick(item)
                currentPos = holder.layoutPosition
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_ch_en_item, parent, false)
    }

    override fun headerView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_en_ch_header, parent, false)
    }

    override fun header(holder: ViewHolder) {
        super.header(holder)
        holder.setText(R.id.tv_title, title)
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 80f).toInt())
        view.layoutParams = params
        return view
    }
}

class EbookAudioAdapter(val onClick: (LrcRow) -> Unit) : BaseAdapter<LrcRow>(isHeader = true, isFooter = true) {
    var currentPos = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: ViewHolder, item: LrcRow) {
        val contentStr = if (currentPos == holder.layoutPosition) {
            item.rowData
        } else {
            item.rowData.replace("<*>", "<font color='#222222'>").replace("</*>", "</font>")
        }
        holder.setVisible(R.id.tv_ch, View.GONE)
            .setText(R.id.tv_en, contentStr)
            .setTextColors(R.id.tv_en, R.color.color_008aff_afb3bf)
            .setSelected(R.id.tv_en, currentPos == holder.layoutPosition)
            .setTypeface(R.id.tv_en, Typeface.defaultFromStyle(if (currentPos == holder.layoutPosition) Typeface.BOLD else Typeface.NORMAL))
            .itemView.setOnClickListener {
                onClick(item)
                currentPos = holder.layoutPosition
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_ch_en_item, parent, false)
    }

    override fun headerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 35f).toInt())
        view.layoutParams = params
        return view
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }
}