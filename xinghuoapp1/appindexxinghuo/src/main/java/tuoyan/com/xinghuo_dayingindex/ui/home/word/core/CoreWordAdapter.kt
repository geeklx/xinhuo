package tuoyan.com.xinghuo_dayingindex.ui.home.word.core

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.ClassifyList

/**
 * 创建者：
 * 时间：  2018/10/9.
 */
class CoreWordAdapter(val click: (ClassifyList) -> Unit) : BaseAdapter<ClassifyList>() {
    override fun convert(holder: ViewHolder, item: ClassifyList) {
        holder.setText(R.id.tv_title, item.catalogName ?: "")
                .setText(R.id.tv_rate, if (item.rightRate.isNullOrBlank()) "" else "掌握率：${item.rightRate
                        ?: ""}")
//                .setVisible(R.id.tv_rate, if (item.rightRate.isNullOrBlank()) View.GONE else View.VISIBLE)
        holder.itemView.setOnClickListener {
            click(item)
        }
    }

    override fun convertView(context: Context,parent: ViewGroup) = with(context) {
        linearLayout {
            lparams(matchParent, dip(55))
            textView {
                textSize = 15f
                id = R.id.tv_title
                rightPadding = dip(15)
                typeface = Typeface.DEFAULT_BOLD
                textColor = resources.getColor(R.color.color_222831)
                gravity = Gravity.CENTER_VERTICAL
            }.lparams(0, matchParent, 1f)
            textView {
                textSize = 12f
                id = R.id.tv_rate
                rightPadding = dip(15)
                textColor = resources.getColor(R.color.color_8d95a1)
                compoundDrawablePadding = dip(10)
                gravity = Gravity.CENTER_VERTICAL
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_navigation_right, 0)
            }.lparams(wrapContent, matchParent)

        }
    }

}