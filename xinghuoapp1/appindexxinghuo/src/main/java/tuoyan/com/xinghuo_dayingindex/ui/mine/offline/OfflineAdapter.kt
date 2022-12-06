package tuoyan.com.xinghuo_dayingindex.ui.mine.offline

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.realm.bean.Group

/**
 * 创建者：
 * 时间：  2018/10/9.
 */
class OfflineAdapter(val click: (Int, Group) -> Unit) : BaseAdapter<Group>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: Group) {
        holder.setText(R.id.tv_title, item.name)
                .setText(R.id.tv_size, (item.resList?.size?:"0").toString()+"个资源")
                .setSelected(R.id.iv_icon, item.type == "图书")
                .itemView.setOnClickListener {
            click(holder.adapterPosition, item)
        }
        holder.itemView.setOnLongClickListener {
            // TODO: 2018/10/25 15:29  长按
            true
        }
    }

    override fun convertView(context: Context,parent: ViewGroup) = with(context) {
        linearLayout {
            lparams(matchParent, dip(66))
            gravity = Gravity.CENTER_VERTICAL
            imageView(R.drawable.icon_offline) {
                id = R.id.iv_icon
            }.lparams(dip(20), dip(46))
            verticalLayout {
                space().lparams(wrapContent, 0, 1f)
                textView {
                    id = R.id.tv_title
                    lines = 1
                    textSize = 15f
                    textColor = resources.getColor(R.color.color_222831)
                }
                space().lparams(wrapContent, dip(10))
                textView {
                    id = R.id.tv_size
                    lines = 1
                    textSize = 11f
                    textColor = resources.getColor(R.color.color_8d95a1)
                    typeface = Typeface.DEFAULT_BOLD
                }
                space().lparams(wrapContent, 0, 1f)
                view { backgroundResource = R.drawable.divider }.lparams(matchParent, dip(0.5f))
            }.lparams(matchParent, matchParent) {
                leftMargin = dip(15)
            }
        }
    }
    override fun emptyImageRes(): Int {
        return R.drawable.ic_emtry_content
    }

    override fun emptyText(): String {
        return "暂无缓存内容哦"
    }
}