package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.evaluation

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseModel

/**
 * 创建者：
 * 时间：  2018/9/27.
 */
class EntryAdapter(val click: (position: Int, map: ExerciseModel) -> Unit) : BaseAdapter<ExerciseModel>() {
    private var recyclerView: RecyclerView? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (getItemViewType(position) == TITLE) {
                    return layoutManager.spanCount
                }
                return 1
            }
        }
    }

    private fun titleConvert(holder: ViewHolder, model: ExerciseModel) {
        if (holder.itemView is TextView)
            (holder.itemView as TextView).text = model.nodeName

    }

    override fun convert(holder: ViewHolder, item: ExerciseModel) {
        holder.itemView.setOnClickListener {
            click(item.index, item)
        }
        holder.setText(R.id.tv_num, item.sort)
        if (item.state == "2" && item.answers.isEmpty() && item.imgPaths.isEmpty()) item.state = "5" //TODO 状态为录入完成但答案列表 或记录的图片列表为空时，判定为未录入
        when (item.state) {
            "1" -> {//选择中
                holder.setText(R.id.tv_answer, "录入中")
                        .setTextColorRes(R.id.tv_num, R.color.color_ffffff)
                        .setTextColorRes(R.id.tv_answer, R.color.color_222)
                        .setBackgroundResource(R.id.tv_num, R.drawable.bg_shape_34_4c84ff)
                        .setBackgroundResource(R.id.ll_answer, R.drawable.bg_shape_stroke_34_4c84ff)
            }
            "2" -> {//录入完成
                holder.setText(R.id.tv_answer, if (item.type == "6") "录入完成" else item.answers[0].answer)
                        .setTextColorRes(R.id.tv_num, R.color.color_4c84ff)
                        .setTextColorRes(R.id.tv_answer, R.color.color_222831)
                        .setBackgroundResource(R.id.tv_num, R.drawable.bg_shape_stroke_34_4c84ff)
                        .setBackgroundResource(R.id.ll_answer, R.drawable.bg_shape_stroke_34_4c84ff)
            }
            "3" -> {//正确
                holder.setText(R.id.tv_answer, item.userAnswer)
                        .setTextColorRes(R.id.tv_num, R.color.color_00ca0d)
                        .setTextColorRes(R.id.tv_answer, R.color.color_ffffff)
                        .setBackgroundResource(R.id.tv_num, R.drawable.bg_shape_34_00ca0d_ffffff)
                        .setBackgroundResource(R.id.ll_answer, R.drawable.bg_shape_34_00ca0d)
            }
            "4" -> { //错误
                holder.setText(R.id.tv_answer, item.userAnswer)
                        .setTextColorRes(R.id.tv_num, R.color.color_ff5d32)
                        .setTextColorRes(R.id.tv_answer, R.color.color_ffffff)
                        .setBackgroundResource(R.id.tv_num, R.drawable.bg_shape_34_ff5d32_ffffff)
                        .setBackgroundResource(R.id.ll_answer, R.drawable.bg_shape_34_ff5d32)
            }
            "5" -> { //未录入
                holder.setText(R.id.tv_answer, "未录入")
                        .setTextColorRes(R.id.tv_num, R.color.color_c3c7cb)
                        .setTextColorRes(R.id.tv_answer, R.color.color_c3c7cb)
                        .setBackgroundResource(R.id.tv_num, R.drawable.bg_shape_34_c3c7cb_ffffff)
                        .setBackgroundResource(R.id.ll_answer, R.drawable.bg_shape_stroke_34_c3c7cb)
            }
            "6" -> { //等待中
                holder.setText(R.id.tv_answer, item.userAnswer)
                        .setTextColorRes(R.id.tv_num, R.color.color_ffaf30)
                        .setTextColorRes(R.id.tv_answer, R.color.color_ffffff)
                        .setBackgroundResource(R.id.tv_num, R.drawable.bg_shape_34_ffaf30_ffffff)
                        .setBackgroundResource(R.id.ll_answer, R.drawable.bg_shape_34_ffaf30)
            }
            else -> {//等待录入
                holder.setText(R.id.tv_answer, "录入答案")
                        .setTextColorRes(R.id.tv_num, R.color.color_4c84ff)
                        .setTextColorRes(R.id.tv_answer, R.color.color_c3c7cb)
                        .setBackgroundResource(R.id.tv_num, R.drawable.bg_shape_stroke_34_4c84ff)
                        .setBackgroundResource(R.id.ll_answer, R.drawable.bg_shape_stroke_34_4c84ff)
            }
        }
    }


    fun notifyItemChanged(item: ExerciseModel) {
        val index = getData().indexOf(item)
        if (index >= 0) {
            notifyItemChanged(index)
            recyclerView?.scrollToPosition(index)
        }
    }

    private fun titleView(parent: ViewGroup) = TextView(parent.context).apply {
        setPadding(0, dip(10), 0, dip(8))
        textSize = 12f
        textColor = resources.getColor(R.color.color_222831)
    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        linearLayout {
            lparams(matchParent, wrapContent)
            verticalPadding = dip(10)
            gravity = Gravity.CENTER
            linearLayout {
                id = R.id.ll_answer
                backgroundResource = R.drawable.bg_shape_stroke_34_4c84ff
                textView {
                    id = R.id.tv_num
                    textSize = 14f
                    gravity = Gravity.CENTER
                    backgroundResource = R.drawable.bg_shape_stroke_34_4c84ff
                }.lparams(dip(34), dip(34))
                textView {
                    id = R.id.tv_answer
                    textSize = 12f
                    gravity = Gravity.CENTER
                }.lparams(matchParent, matchParent)
            }.lparams(dip(97), dip(34))

        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getData()[position].itemType == "NODE") return TITLE
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) mContext = parent.context
        return if (viewType == TITLE) ViewHolder(titleView(parent))
        else super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == TITLE) titleConvert(holder, getData()[position - if (getIsHeader()) 1 else 0])
        else super.onBindViewHolder(holder, position)
    }

    companion object {
        const val TITLE = 0x15d4
    }
}