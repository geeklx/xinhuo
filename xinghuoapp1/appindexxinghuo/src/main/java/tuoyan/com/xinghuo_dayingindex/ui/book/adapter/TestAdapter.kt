package tuoyan.com.xinghuo_dayingindex.ui.book.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.BookRes

class TestAdapter(var onItemClick: (pos: Int, item: BookRes) -> Unit, var onRankClick: (item: BookRes) -> Unit) : BaseAdapter<BookRes>() {
    override fun convert(holder: ViewHolder, item: BookRes) {
        holder.setText(R.id.tv_title, item.name)
        holder.itemView.setOnClickListener {
            onItemClick(holder.adapterPosition, item)
        }
        if (item.field5 == "1") {
            //只有测评有排行榜
            holder.setVisible(R.id.img_rank_icon, View.VISIBLE)
        } else {
            holder.setVisible(R.id.img_rank_icon, View.GONE)
        }
        holder.setOnClickListener(R.id.img_rank_icon) {
            onRankClick(item)
        }
    }

    override fun convertView(context: Context,parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.item_exercise, null)
    }
}