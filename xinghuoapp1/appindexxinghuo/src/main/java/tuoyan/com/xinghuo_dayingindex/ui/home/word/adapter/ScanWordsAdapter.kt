package tuoyan.com.xinghuo_dayingindex.ui.home.word.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.WordCatalog
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * type: 1:item 点击事件 2：收藏点击事件
 * from 1 扫码词表首页  2 收藏
 */
class ScanWordsAdapter(val click: (Int, WordCatalog) -> Unit) : BaseAdapter<WordCatalog>(isFooter = true) {
    var from = "1"
    override fun convert(holder: ViewHolder, item: WordCatalog) {
        holder.setText(R.id.tv_title, item.wordClassifyName)
        holder.setSelected(R.id.img_c, item.isCollection == "1" || from == "2")
            .setVisible(R.id.img_c, if (item.wordClassifyKey.isNullOrBlank()) View.GONE else View.VISIBLE)
        holder.setOnClickListener(R.id.img_c) {
            click(2, item)
        }.itemView.setOnClickListener {
            click(1, item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_scan_word, parent, false)
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }
}