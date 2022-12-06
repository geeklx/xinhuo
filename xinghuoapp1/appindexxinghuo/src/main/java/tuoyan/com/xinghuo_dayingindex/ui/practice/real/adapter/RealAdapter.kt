package tuoyan.com.xinghuo_dayingindex.ui.practice.real.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.RealListItem

class RealAdapter(var onClick: (RealListItem) -> Unit) :
    BaseAdapter<RealListItem>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: RealListItem) {
        holder.setText(R.id.tv_explain_title, item.paperName)
            .setText(R.id.tv_explai_count, item.practiseCount + "人做过")
            .setVisible(R.id.tv_date, if (item.showYear) View.VISIBLE else View.GONE)
            .setText(R.id.tv_date, item.year)
        holder.getView(R.id.ll_item).setOnClickListener {
            onClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, wrapContent)
            textView {
                id = R.id.tv_date
                textSize = 17f
                textColor = Color.parseColor("#222831")
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_title_right, 0)
                leftPadding = dip(15)
                compoundDrawablePadding = dip(5)
                typeface = Typeface.DEFAULT_BOLD
                bottomPadding = dip(16)
                topPadding = dip(5)
            }.lparams(wrapContent, wrapContent)

            verticalLayout {
                id = R.id.ll_item
                backgroundResource = R.drawable.bg_item_explain
                textView {
                    id = R.id.tv_explain_title
                    textColor = Color.parseColor("#222831")
                    textSize = 15f
                    paint.isFakeBoldText = true
                }.lparams {
                    leftMargin = dip(30)
                    topMargin = dip(15)
                }

                textView {
                    id = R.id.tv_explai_count
                    textColor = Color.parseColor("#c3c7cb")
                    textSize = 12f
                }.lparams {
                    leftMargin = dip(30)
                    topMargin = dip(5)
                }
            }.lparams(matchParent, context.dip(78))

        }
    }

    override fun emptyImageRes(): Int {
        return R.drawable.empty_study
    }

    override fun emptyText(): String {
        return "暂无内容"
    }
}