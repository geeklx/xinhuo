package tuoyan.com.xinghuo_dayingindex.ui.mine.wrong

import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.WrongBook

/**
 * 创建者：
 * 时间：  2018/10/9.
 */
class WrongAdapter(val more: () -> Unit, val click: (WrongBook) -> Unit) :
    BaseAdapter<WrongBook>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: WrongBook) {
        holder.setText(R.id.tv_content, Html.fromHtml(item.questionsubject ?: ""))
            .setText(R.id.tv_date, item.subjectname ?: "")
            .setText(R.id.tv_time, item.time ?: "")
            .itemView.setOnClickListener {
                click(item)
            }
    }

    override fun loadMore(holder: ViewHolder) {
        more()
    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        verticalLayout {
            lparams(matchParent, wrapContent)
            backgroundResource = R.color.color_ffffff
            padding = dip(15)
            textView {
                id = R.id.tv_content
                textSize = 15f
                textColor = resources.getColor(R.color.color_222)
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
                setLineSpacing(dip(3).toFloat(), 1f)
            }
            space().lparams(wrapContent, dip(10))
            linearLayout {
                gravity = Gravity.CENTER_VERTICAL
                textView {
                    id = R.id.tv_date
                    horizontalPadding = dip(5)
                    verticalPadding = dip(3)
                    textSize = 10f
                    textColor = resources.getColor(R.color.color_4c84ff)
                    backgroundResource = R.drawable.bg_shape_2_dbe6ff
                }
                space().lparams(0, wrapContent, 1f)
                textView {
                    id = R.id.tv_time
                    textSize = 11f
                    textColor = resources.getColor(R.color.color_8d95a1)
                }
            }
        }
    }

    override fun emptyImageRes(): Int {
        return R.drawable.ic_emtry_content
    }

    override fun emptyText(): String {
        return "错题本里是空的哦"
    }
}