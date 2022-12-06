package tuoyan.com.xinghuo_dayingindex.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Level

class BookStoreGradeAdapter(var onItemClick: (item: Level) -> Unit) : BaseAdapter<Level>() {
    override fun convert(holder: ViewHolder, item: Level) {
        holder.setText(R.id.tv_grade, item.name)
        holder.setSelected(R.id.tv_grade, item.isAdd)
        holder.itemView.setOnClickListener {
            getData().forEach {
                if (it == item) {
                    it.isAdd = !it.isAdd
                } else {
                    it.isAdd = false
                }
            }
            notifyDataSetChanged()
            onItemClick(item)
        }
    }

    override fun convertView(context: Context,parent: ViewGroup): View = LayoutInflater.from(context).inflate(R.layout.layout_book_store_grade, null)
}