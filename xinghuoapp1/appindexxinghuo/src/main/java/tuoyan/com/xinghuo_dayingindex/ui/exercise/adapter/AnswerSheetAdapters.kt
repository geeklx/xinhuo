package tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSheet

class ASTypeAdapter(var onClick: (Int, Int) -> Unit) : BaseAdapter<AnswerSheet>() {
    override fun convert(holder: ViewHolder, item: AnswerSheet) {
        var dataList = ArrayList<AnswerSheet>()
        initGroupData(dataList, item.questionlist)

        var oAdapter = ASItemAdapter(onClick)
        oAdapter.setData(dataList)

        holder.setText(R.id.tv_q_name, if ("1" != item.isSubtitle) {
            item.groupName
        } else {
            ""
        }).setAdapter(R.id.rv_answers, oAdapter)
    }

    override fun convertView(context: Context,parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, wrapContent)
            textView {
                id = R.id.tv_q_name
                textColor = Color.parseColor("#1e1e1e")
                textSize = 12f
            }.lparams(wrapContent, wrapContent) {
                leftMargin = dip(15)
            }
            recyclerView {
                id = R.id.rv_answers
                layoutManager = GridLayoutManager(context, 5)
            }.lparams(matchParent, wrapContent) {
                topMargin = dip(18)
            }
        }
    }

    /**
     * 构建数据源，存在材料题时，List<AnswerSheet> 层级不统一
     */
    fun initGroupData(dataList: ArrayList<AnswerSheet>, itemList: List<AnswerSheet>) {
        itemList.forEach {
            if (it.questionlist != null && it.questionlist.isNotEmpty()) {
                initGroupData(dataList, it.questionlist)
            } else {
                dataList.add(it)
            }
        }
    }
}

class ASItemAdapter(var onClick: (Int, Int) -> Unit) : BaseAdapter<AnswerSheet>() {
    override fun convert(holder: ViewHolder, item: AnswerSheet) {
        holder.setText(R.id.tv_sort, item.questionSort)
                .setBackgroundResource(R.id.tv_sort, getBackground(item))
        holder.itemView.setOnClickListener {
            onClick(item.index, holder.adapterPosition)
        }
    }

    override fun convertView(context: Context,parent: ViewGroup): View = with(context) {
        relativeLayout {
            //            leftPadding = dip(18)
//            rightPadding = dip(18)
            bottomPadding = dip(18)
            textView {
                id = R.id.tv_sort
                backgroundResource = R.drawable.shape_as_normal
                textColor = Color.parseColor("#ffffff")
                textSize = 14f
                gravity = Gravity.CENTER
            }.lparams {
                centerHorizontally()
            }

        }
    }

    private fun getBackground(answer: AnswerSheet): Int {
        if (answer.questionType == "6" && answer.isRight != null && answer.userAnswer.isNullOrEmpty()) {//主观题根据阅卷方式显示得分或等待中
            return R.drawable.shape_as_waiting
        } else {
            return when {
                answer.isRight == null -> R.drawable.shape_as_normal
                answer.isRight == "0" -> R.drawable.shape_as_wrong
                answer.isRight == "1" -> R.drawable.shape_as_right
                else -> R.drawable.shape_as_normal
            }
        }
    }
}