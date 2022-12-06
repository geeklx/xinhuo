package tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.EBookLineData
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * Created by Zzz on 2021/7/1
 * Email:
 */

class PracticeAdapter(val isOwn: String = "0", val onClick: (EBookLineData, Int) -> Unit) : BaseAdapter<EBookLineData>(isFooter = true) {
    override fun convert(holder: ViewHolder, item: EBookLineData) {
        val title = if ("0" == isOwn && 0 == holder.adapterPosition) "\u3000\u3000\u0020\u0020${item.name}" else item.name
        holder.setText(R.id.tv_title, title).setVisible(R.id.tv_try, if ("0" == isOwn && 0 == holder.adapterPosition) View.VISIBLE else View.GONE)
            .setText(R.id.tv_num, "共${item.totalCount}题")
        holder.itemView.setOnClickListener {
            onClick(item, holder.adapterPosition)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_practice_item, parent, false)
    }

    override fun footerView(context: Context,parent: ViewGroup): View {
        val view = View(context)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 55f).toInt())
        view.layoutParams = params
        return view
    }
}