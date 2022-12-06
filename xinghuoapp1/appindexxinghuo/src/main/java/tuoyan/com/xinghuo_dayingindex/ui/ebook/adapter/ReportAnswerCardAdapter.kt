package tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSheet

/**
 * Created by Zzz on 2021/7/3
 * Email:
 */

class ReportAnswerCardAdapter(val onClick: (AnswerSheet) -> Unit) : BaseAdapter<AnswerSheet>() {
    override fun convert(holder: ViewHolder, item: AnswerSheet) {
        val tv_sort = holder.getView(R.id.tv_sort) as TextView
        tv_sort.text = item.questionSort
        tv_sort.isSelected = "1" == item.isRight
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_report_answer_card_item, parent, false)
    }
}