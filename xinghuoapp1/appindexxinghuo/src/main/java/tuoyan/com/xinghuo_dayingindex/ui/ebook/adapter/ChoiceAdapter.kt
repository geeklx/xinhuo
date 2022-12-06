package tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * Created by Zzz on 2021/7/2
 * Email:
 * type: 1:模考做题；2：模考解析；3：精练单词做题；4：精练单词解析,背景颜色不同 5:巧记速记章节测试 6:巧记速记章节测试解析
 */

class ChoiceAdapter(var type: String = "1", val onClick: (QuestionInfoItem) -> Unit) : BaseAdapter<QuestionInfoItem>() {
    var order = ""
    override fun convert(holder: ViewHolder, item: QuestionInfoItem) {
        holder.setText(R.id.tv_pre, item.order)
        val tv_content = holder.getView(R.id.tv_content) as TextView
        tv_content.text = Html.fromHtml(item.content)
        if ("2" == type) {
            setState(holder, item.isAnswer)
            setSelected(holder, "1" == item.isAnswer || "2" == item.isAnswer)
        } else if ("3" == type) {
            setState(holder, "3")
            setSelected(holder, order == item.order)
        } else if ("4" == type) {
            setState(holder, if ("1" == item.isAnswer || "2" == item.isAnswer) item.isAnswer else "3")
            setSelected(holder, "1" == item.isAnswer || "2" == item.isAnswer)
        } else if ("5" == type) {
            setState(holder, "3")
            setSelected(holder, order == item.order)
        } else if ("6" == type) {
            setState(holder, if (item.isAnswer == "0") "4.0" else if (item.isAnswer == "1") "4.1" else "4.2")
            setSelected(holder, "1" == item.isAnswer || "2" == item.isAnswer)
        } else {
            setState(holder)
            setSelected(holder, order == item.order)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_choice_item, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        if (getFooter() && position >= itemCount - 1) {
            return super.getItemViewType(position)
        } else {
            val data = getData()[position]
            return if (data is QuestionInfoItem && "1" == data.type) {
                101
            } else {
                return super.getItemViewType(position)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getFooter() && position >= itemCount - 1) {
            super.onBindViewHolder(holder, position)
        } else {
            val item = getData()[position]
            holder.itemView.setOnClickListener {
                if ("1" == type || "3" == type || "5" == type) {
                    order = item.order
                    notifyDataSetChanged()
                    onClick(item)
                }
            }
            if ("4" == type && 101 == holder.itemViewType) {
                setState(holder, if ("1" == item.isAnswer || "2" == item.isAnswer) item.isAnswer else "3")
                setSelected(holder, "1" == item.isAnswer || "2" == item.isAnswer)
            } else if (101 != holder.itemViewType) {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            101 -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_unknow, parent, false))
            }
            else -> {
                super.onCreateViewHolder(parent, viewType)
            }
        }
    }

    fun setSelected(holder: ViewHolder, isSelected: Boolean) {
        val itemView = holder.getView(R.id.ctl_item) as ConstraintLayout
        itemView.isSelected = isSelected
        if (holder.itemViewType != 101) {
            val tv_pre = holder.getView(R.id.tv_pre) as TextView
            val tv_content = holder.getView(R.id.tv_content) as TextView
            tv_pre.isSelected = isSelected
            tv_content.isSelected = isSelected
            tv_pre.typeface = Typeface.defaultFromStyle(if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            tv_content.typeface = Typeface.defaultFromStyle(if (isSelected) Typeface.BOLD else Typeface.NORMAL)
        } else {
            val tv_1 = holder.getView(R.id.tv_1) as TextView
            tv_1.typeface = Typeface.defaultFromStyle(if (isSelected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    /**
     * type 0:默认状态  1：正确状态；2：错误状态；3：精练单词默认状态自定义的 4.0：巧记速记未做 4.1 正确 4.2错误
     */
    fun setState(holder: ViewHolder, mType: String = "") {
        val itemView = holder.getView(R.id.ctl_item) as ConstraintLayout
        when (mType) {
            "1" -> {
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_30_dffadc_fff)
            }
            "2" -> {
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_30_ffe6e0_fff)
            }
            "3" -> {
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_30_008aff_f5f6f9)
            }
            "4.0" -> {
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_30_008aff_f5f6f9)
            }
            "4.1" -> {
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_30_dffadc_f5f6f9)
            }
            "4.2" -> {
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_30_ffe6e0_f5f6f9)
            }
            else -> {
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_30_008aff_fff)
            }
        }
        if (holder.itemViewType != 101) {
            val tv_pre = holder.getView(R.id.tv_pre) as TextView
            val tv_content = holder.getView(R.id.tv_content) as TextView
            when (mType) {
                "1", "4.1" -> {
                    tv_content.setTextColor(ContextCompat.getColorStateList(itemView.context, R.color.color_62ca00_222))
                    tv_pre.setTextColor(ContextCompat.getColorStateList(itemView.context, R.color.color_62ca00_c4cbde))
                }
                "2", "4.2" -> {
                    tv_content.setTextColor(ContextCompat.getColorStateList(itemView.context, R.color.color_ff2e00_222))
                    tv_pre.setTextColor(ContextCompat.getColorStateList(itemView.context, R.color.color_ff2e00_c4cbde))
                }
                "3" -> {
                    tv_content.setTextColor(ContextCompat.getColorStateList(itemView.context, R.color.color_fff_222))
                    tv_pre.setTextColor(ContextCompat.getColorStateList(itemView.context, R.color.color_fff_c4cbde))
                }
                else -> {
                    tv_content.setTextColor(ContextCompat.getColorStateList(itemView.context, R.color.color_fff_222))
                    tv_pre.setTextColor(ContextCompat.getColorStateList(itemView.context, R.color.color_fff_c4cbde))
                }
            }
        }
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = TextView(context)
        val param = when (type) {
            "1" -> {
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 50f).toInt())
            }
            "3" -> {
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 80f).toInt())
            }
            else -> {
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 0f).toInt())
            }
        }
        view.layoutParams = param
        return view
    }
}