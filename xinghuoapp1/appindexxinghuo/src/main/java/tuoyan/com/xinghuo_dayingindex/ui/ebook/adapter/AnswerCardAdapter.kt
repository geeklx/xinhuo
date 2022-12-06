package tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.AnswerQuestion

/**
 * Created by Zzz on 2021/7/3
 * Email:
 */

class AnswerCardAdapter(val onClick: (AnswerQuestion) -> Unit) : BaseAdapter<AnswerQuestion>() {
    override fun convert(holder: ViewHolder, item: AnswerQuestion) {
        val tv_sort = holder.getView(R.id.tv_sort) as TextView
        tv_sort.text = item.sort
        tv_sort.isSelected = item.haveDone
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_answer_card_item, parent, false)
    }
}