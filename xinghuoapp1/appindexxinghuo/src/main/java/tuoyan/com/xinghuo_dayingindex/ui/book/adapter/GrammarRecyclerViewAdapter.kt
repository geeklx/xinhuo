package tuoyan.com.xinghuo_dayingindex.ui.book.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.BookRes

class GrammarRecyclerViewAdapter(var onItemClick: (item: BookRes, position: Int) -> Unit) : BaseAdapter<BookRes>() {
    override fun convertView(context: Context,parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.fragment_grammar_item, null)
    }

    override fun convert(holder: ViewHolder, item: BookRes) {
        holder.setText(R.id.tv_name, item.name)
        holder.itemView.setOnClickListener {
            onItemClick(item, holder.adapterPosition)
        }
        // 1:试卷,2:试卷解析,3:视频,4:图片,5:图文,6:文档,7:音频,8:链接,10:测评,11:资讯
        if (item.type == "1" || item.type == "2" || item.type == "11") {
            holder.setImageResource(R.id.img_type, R.mipmap.icon_grammar_practice)
        } else {
            holder.setImageResource(R.id.img_type, R.mipmap.icon_grammar_notes)
        }
    }

}
