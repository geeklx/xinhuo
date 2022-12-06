package tuoyan.com.xinghuo_dayingindex.ui.practice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.ui.practice.Composition

class CompositionListAdapter(val onItemClick: (position: Int, item: Composition) -> Unit) :
    BaseAdapter<Composition>() {
    override fun convert(holder: ViewHolder, item: Composition) {
        holder.itemView.setOnClickListener {
            onItemClick(holder.adapterPosition, item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View =
        LayoutInflater.from(context).inflate(R.layout.item_composition, null)

}