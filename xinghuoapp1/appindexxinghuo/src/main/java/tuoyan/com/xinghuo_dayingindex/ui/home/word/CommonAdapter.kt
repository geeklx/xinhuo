package tuoyan.com.xinghuo_dayingindex.ui.home.word

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.CatalogList

/**
 * 创建者：
 * 时间：  2018/10/9.
 */
class CommonAdapter(val click: (CatalogList) -> Unit) : BaseAdapter<CatalogList>() {
    override fun convert(holder: ViewHolder, item: CatalogList) {
        holder.setText(R.id.tv_title, item.catalogName)
                .itemView.setOnClickListener {
            click(item)
        }
    }

    override fun convertView(context: Context,parent: ViewGroup) = with(context) {
        verticalLayout {
            textView {
                id = R.id.tv_title
                textSize = 15f
                horizontalPadding = dip(15)
                textColor = resources.getColor(R.color.color_222)
                gravity = Gravity.CENTER_VERTICAL
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_navigation_right, 0)
            }.lparams(matchParent, dip(55))
            view { backgroundResource = R.drawable.divider }.lparams(matchParent, dip(0.5f)) { marginStart = dip(15) }
        }
    }
}