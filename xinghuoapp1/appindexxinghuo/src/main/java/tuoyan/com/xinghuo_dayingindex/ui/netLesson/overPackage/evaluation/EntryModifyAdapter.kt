package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.evaluation

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseModel
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo

/**
 * 创建者：
 * 时间：  2018/9/27.
 */
class EntryModifyAdapter(val click: (map: ExerciseModel) -> Unit, val modifyClick: (item: ExerciseModel) -> Unit) : BaseAdapter<ExerciseModel>() {
    private var recyclerView: RecyclerView? = null
    private var isEnable = false
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (getItemViewType(position) == TITLE) {
                    return layoutManager.spanCount
                } else if (getData()[position].type == "6") {
                    //主观题
                    var infoJson = Gson().toJson(getData()[position].questionInfo)
                    var questionInfo = Gson().fromJson(infoJson, QuestionInfo::class.java)
                    var evalMode = questionInfo.evalMode
                    //evalMode    0自判 ,1免费人工, 2付费人工, 10自判+免费人工,20自判+付费人工
                    if ("0" == evalMode) {
                        return 1
                    } else if ("1" == evalMode || "2" == evalMode) {
                        return 2
                    } else if ("10" == evalMode || "20" == evalMode) {
                        return 3
                    }
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
        var questionInfo = item.questionInfo
        var chb_teacher_modify = holder.getView(R.id.chb_teacher_modify) as TextView
        var chb_self_modify = holder.getView(R.id.chb_self_modify) as TextView
        var ll_teac = holder.getView(R.id.ll_teac)
        var ll_self = holder.getView(R.id.ll_self)
        // evalMode    0自判 ,1免费人工, 2付费人工, 10自判+免费人工,20自判+付费人工
        if (item.type == "6") {
            when (questionInfo!!.evalMode) {
                "0" -> {
                    ll_teac.visibility = View.GONE
                    ll_self.visibility = View.GONE
                }
                "1", "2" -> {
                    ll_teac.visibility = View.VISIBLE
                    ll_self.visibility = View.GONE
                }
                "10", "20" -> {
                    ll_teac.visibility = View.VISIBLE
                    ll_self.visibility = View.VISIBLE
                }
            }
            if (questionInfo.isOwn == "1" || questionInfo.evalMode == "1") {
                chb_self_modify.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_uncheked, 0, 0, 0)
                chb_teacher_modify.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_checked, 0, 0, 0)
            } else {
                chb_self_modify.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_checked, 0, 0, 0)
                chb_teacher_modify.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_uncheked, 0, 0, 0)
            }
        } else {
            ll_teac.visibility = View.GONE
            ll_self.visibility = View.GONE
        }
        holder.setOnClickListener(R.id.ll_answer, onClick = {
            try {
                click(item)
            } catch (e: Exception) {
            }
        })
        ll_teac.setOnClickListener {
            // evalMode  1免费人工, 2付费人工, 10自判+免费人工,20自判+付费人工
            if (questionInfo!!.evalMode == "2" || questionInfo.evalMode == "20") {
                //走付费流程
                if (questionInfo!!.isOwn != "1") {
                    //没有购买
                    modifyClick(item)
                }
            } else if (questionInfo!!.evalMode == "10" && questionInfo.isOwn == "0") {
                //    1免费人工不可切换，10自判+免费人工可以切换
                questionInfo.isOwn = "1"
                notifyDataSetChanged()
                modifyClick(item)
            }
        }
        ll_self.setOnClickListener {
            // 10自判+免费人工,20自判+付费人工的时候显示，其他情况隐藏
            if (questionInfo!!.evalMode == "10" && questionInfo.isOwn == "1") {
                questionInfo!!.isOwn = "0"
                notifyDataSetChanged()
                modifyClick(item)
            }
        }
        holder.setText(R.id.tv_num, item.sort)
        if (item.state == "2" && item.answers.isEmpty() && item.imgPaths.isEmpty()) item.state = "5" //TODO 状态为录入完成但答案列表 或记录的图片列表为空时，判定为未录入
        when (item.state) {
            "1" -> {//选择中
                holder.setText(R.id.tv_answer, "录入中")
                    .setTextColorRes(R.id.tv_num, R.color.color_ffffff)
                    .setTextColorRes(R.id.tv_answer, R.color.color_222831)
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
            linearLayout {
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
            }.lparams(width = 0, height = wrapContent, weight = 1f)

            linearLayout {
                id = R.id.ll_self
                visibility = View.GONE
                gravity = Gravity.CENTER
                textView {
                    id = R.id.chb_self_modify
                    text = "自我批改"
                    compoundDrawablePadding = dip(5)
                    background = null
                    gravity = Gravity.CENTER
                    textColor = resources.getColor(R.color.color_222831)
                    textSize = 11f
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_modify, 0, 0, 0)
                }.lparams {
                    width = wrapContent
                    height = wrapContent
                    padding = dip(5)
                }
            }.lparams(width = 0, height = dip(34), weight = 1f)
            linearLayout {
                id = R.id.ll_teac
                visibility = View.GONE
                gravity = Gravity.CENTER
                textView {
                    id = R.id.chb_teacher_modify
                    text = "人工批改"
                    compoundDrawablePadding = dip(5)
                    background = null
                    gravity = Gravity.CENTER
                    textColor = resources.getColor(R.color.color_222831)
                    textSize = 11f
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_modify, 0, 0, 0)
                }.lparams {
                    width = wrapContent
                    height = wrapContent
                    padding = dip(5)
                }
            }.lparams(width = 0, height = dip(34), weight = 1f)
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