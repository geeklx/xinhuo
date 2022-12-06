package tuoyan.com.xinghuo_dayingindex.ui.book.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.BookRes

class MyBasicItemRecyclerViewAdapter(var onItemClick: (item: BookRes, position: Int) -> Unit) : BaseAdapter<BookRes>() {
    override fun convertView(context: Context,parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.fragment_basicitem, null)
    }

    override fun convert(holder: ViewHolder, item: BookRes) {
        holder.setText(R.id.tv_name, item.catalogName)
        holder.itemView.setOnClickListener {
            onItemClick(item, holder.adapterPosition)
        }
    }

}
