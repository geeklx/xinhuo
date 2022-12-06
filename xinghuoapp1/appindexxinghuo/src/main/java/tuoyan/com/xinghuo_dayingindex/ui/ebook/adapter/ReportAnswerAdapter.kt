package tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter

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
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSheet
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * Created by Zzz on 2021/7/5
 * Email:
 */

class ReportAnswerAdapter(val onClick: (AnswerSheet) -> Unit) : BaseAdapter<AnswerSheet>() {
    override fun convert(holder: ViewHolder, item: AnswerSheet) {
        holder.setText(R.id.tv_title, item.groupName).setVisible(R.id.tv_title, if (item.groupName.isBlank()) View.GONE else View.VISIBLE)
        val rlv_item = holder.getView(R.id.rlv_item) as RecyclerView
        if (item.groupName.isBlank()) {
            val params = rlv_item.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = DeviceUtil.dp2px(holder.itemView.context, 25f).toInt()
        }
        rlv_item.isNestedScrollingEnabled = false
        rlv_item.layoutManager = GridLayoutManager(holder.itemView.context, 5)
        val adapter = ReportAnswerCardAdapter() {
            onClick(it)
        }
        rlv_item.adapter = adapter
        adapter.setData(item.questionlist)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_report_answer, parent, false)
    }
}