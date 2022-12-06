package tuoyan.com.xinghuo_dayingindex.ui.practice.special.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem

class CataolgAdapter(var onClick: (Int, ExerciseFrameItem) -> Unit) :
    BaseAdapter<ExerciseFrameItem>() {
    override fun convert(holder: ViewHolder, item: ExerciseFrameItem) {
        holder.setText(R.id.tv_name, item.groupName)
            .setTextColor(
                R.id.tv_name,
                if (item.userPracticeKey.isNullOrEmpty()) Color.parseColor("#222831") else Color.parseColor(
                    "#8d95a1"
                )
            )
        holder.itemView.setOnClickListener {
            onClick(holder.adapterPosition, item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        relativeLayout {
            lparams(matchParent, context.dip(55))
            textView {
                id = R.id.tv_name
                textColor = Color.parseColor("#222831")
                textSize = 14f
                typeface = Typeface.defaultFromStyle(Typeface.BOLD) // 加粗
            }.lparams(wrapContent, wrapContent) {
                centerVertically()
                leftMargin = dip(15)
            }

            view {
                backgroundColor = Color.parseColor("#edeff0")
            }.lparams(matchParent, 3) {
                alignParentBottom()
                leftMargin = dip(15)
            }
        }
    }
}