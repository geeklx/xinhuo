package tuoyan.com.xinghuo_dayingindex.ui.ebook.word.adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem

class EBookWordSpellAdapter(val onClick: (QuestionInfoItem) -> Unit) : BaseAdapter<QuestionInfoItem>() {
    override fun convert(holder: ViewHolder, item: QuestionInfoItem) {
        holder.setText(R.id.tv_title, item.content)
            .setVisible(R.id.tv_title, if (item.content.isBlank()) View.INVISIBLE else View.VISIBLE)
            .setVisible(R.id.tv_line, if (item.content.isBlank()) View.VISIBLE else View.GONE)
            .itemView.setOnClickListener {
                if (item.content.isNotBlank()) {
                    onClick(item)
                }
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_spell, parent, false)
    }
}