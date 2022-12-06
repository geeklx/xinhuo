package tuoyan.com.xinghuo_dayingindex.ui.ebook.word.adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * 巧记速记3：练习；4：练习解析,背景颜色不同
 */
class EBookWordSpellChoiceAdapter(var type: String = "3", val onClick: (QuestionInfoItem) -> Unit) : BaseAdapter<QuestionInfoItem>() {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (getItemViewType(position) == 101) {
                    return layoutManager.spanCount
                }
                return 1
            }
        }
    }

    override fun convert(holder: ViewHolder, item: QuestionInfoItem) {
        holder.setText(R.id.tv_content, item.content).setSelected(R.id.tv_content, item.isAnswer == "2")
            .itemView.setOnClickListener {
                if ("3" == type && !holder.isSelect(R.id.tv_content)) {
                    onClick(item)
                }
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_ebook_word_spell, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        if (getFooter() && position >= itemCount - 1) {
            return super.getItemViewType(position)
        } else {
            val data = getData()[position]
            return if (data is QuestionInfoItem && "1" == data.type) {
                101
            } else {
                return super.getItemViewType(position)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (101 == holder.itemViewType) {
            val data = getData()[position]
            val view = holder.itemView as ConstraintLayout
            view.isSelected = data.isAnswer == "2"
            val params = view.layoutParams as RecyclerView.LayoutParams
            params.topMargin = DeviceUtil.dp2px(view.context, 45f).toInt()
            holder.itemView.setOnClickListener {
                if ("3" == type) {
                    onClick(data)
                }
            }
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            101 -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_unknow, parent, false))
            }
            else -> {
                super.onCreateViewHolder(parent, viewType)
            }
        }
    }
}