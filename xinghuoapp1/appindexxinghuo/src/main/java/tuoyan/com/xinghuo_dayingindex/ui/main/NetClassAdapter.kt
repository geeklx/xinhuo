package tuoyan.com.xinghuo_dayingindex.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.layout_study_class_item.view.*
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.MyBookLesson
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * itemClick   1：重读
 */
class NetClassAdapter(var itemClick: (MyBookLesson, Int) -> Unit, var deleteClick: (String, Int) -> Unit) : BaseAdapter<MyBookLesson>(isEmpty = true, isFooter = true) {
    var showDelete = false
    override fun convert(holder: ViewHolder, item: MyBookLesson) {
        Glide.with(holder.itemView.context).load(item.img)
            .into(holder.getView(R.id.img_cover) as ImageView)
        holder.setText(R.id.tv_title, item.name)
            .setText(R.id.tv_teacher, item.teacher.replace(",", "\u0020"))
            .setText(R.id.tv_time, item.endtime)
            .setText(R.id.tv_cover_tag, item.netSubjectName)
            .setVisible(R.id.tv_cover_tag, if (item.netSubjectName.isEmpty()) View.GONE else View.VISIBLE)
            .setVisible(R.id.tv_repeat, if ("1" == item.repeatFlag) View.VISIBLE else View.GONE).setOnClickListener(R.id.tv_repeat) {
                itemClick(item, 1)
            }.setOnClickListener(R.id.tv_class_status) {
                //删除按钮时，执行删除操作
                if (showDelete) {
                    if (item.iseffect == "1") {
                        deleteClick(item.key, holder.adapterPosition)
                    } else {
                        mContext?.toast("未过期网课不允许删除")
                    }
                }
            }
            .itemView.setOnClickListener {
                itemClick(item, 0)
            }
        initStatus(holder, item)

//        val ll_tags = holder.getView(R.id.ll_tags) as LinearLayout
//        ll_tags.removeAllViews()
//        if (!item.sellingPoint.isNullOrBlank()) {
//            item.sellingPoint.split(",").forEach {
//                val tv_tag = TextView(mContext)
//                val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
//                layoutParams.rightMargin = 10
//                mContext?.let {
//                    tv_tag.background = ContextCompat.getDrawable(it, R.drawable.shape_remark)
//                }
//                tv_tag.gravity = Gravity.CENTER
//                tv_tag.horizontalPadding = 14
//                tv_tag.text = it
//                mContext?.let {
//                    tv_tag.textColor = ContextCompat.getColor(it, R.color.color_7a)
//                }
//                tv_tag.textSize = 11f
//                ll_tags.addView(tv_tag, layoutParams)
//            }
//        }
        //当前任务是否完成isSelected==true 任务完成；否则任务未完成；没有就隐藏tvWorkState
        val tvWorkState = holder.getView(R.id.tv_work_state) as TextView
        if ("2" == item.learnState) {
            tvWorkState.visibility = View.VISIBLE
            tvWorkState.isSelected = true
            tvWorkState.text = "任务已完成"
        } else if ("1" == item.learnState) {
            tvWorkState.visibility = View.VISIBLE
            tvWorkState.isSelected = false
            tvWorkState.text = "任务未完成"
        } else {
            tvWorkState.visibility = View.GONE
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = LayoutInflater.from(context).inflate(R.layout.layout_study_class_item, parent, false)

    fun initStatus(holder: ViewHolder, item: MyBookLesson) {
        val context = holder.itemView.context
        val tv_status = holder.itemView.tv_class_status as TextView
        if (showDelete) {
            tv_status.setPadding(DeviceUtil.dp2px(context, 23f).toInt(), 0, DeviceUtil.dp2px(context, 23f).toInt(), 0)
            //显示删除按钮
            tv_status.text = "删除"
            tv_status.textColor = mContext?.let {
                ContextCompat.getColor(it, R.color.color_ff0000)
            }!!
            tv_status.background = mContext?.let {
                ContextCompat.getDrawable(it, R.drawable.shape_8_ffe5e5)
            }
            tv_status.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            holder.setVisible(R.id.tv_repeat, View.GONE)
        } else {
            when {
                item.iseffect == "1" -> {
                    tv_status.setPadding(DeviceUtil.dp2px(context, 11f).toInt(), 0, DeviceUtil.dp2px(context, 11f).toInt(), 0)
                    tv_status.text = "已过期"
                    tv_status.textColor = mContext?.let {
                        ContextCompat.getColor(it, R.color.color_de)
                    }!!
                    tv_status.background = mContext?.let {
                        ContextCompat.getDrawable(it, R.drawable.shape_8_border_de)
                    }
                    tv_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_overdue, 0, 0, 0)
                }
                item.nextBeginTime.isNotEmpty() -> {
                    tv_status.setPadding(0, 0, DeviceUtil.dp2px(context, 10f).toInt(), 0)
                    tv_status.text = item.nextBeginTime + "开始直播"
                    tv_status.textColor = mContext?.let {
                        ContextCompat.getColor(it, R.color.color_ff7100)
                    }!!
                    tv_status.background = mContext?.let {
                        ContextCompat.getDrawable(it, R.color.transparent)
                    }
                    tv_status.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
                item.state == "1" -> {
                    tv_status.setPadding(DeviceUtil.dp2px(context, 11f).toInt(), 0, DeviceUtil.dp2px(context, 11f).toInt(), 0)
                    tv_status.text = "直播中"
                    tv_status.textColor = mContext?.let {
                        ContextCompat.getColor(it, R.color.color_5467ff)
                    }!!
                    tv_status.background = mContext?.let {
                        ContextCompat.getDrawable(it, R.drawable.shape_8_e8ebff)
                    }
                    tv_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_living, 0, 0, 0)
                }
                item.state == "2" -> {
                    tv_status.setPadding(DeviceUtil.dp2px(context, 11f).toInt(), 0, DeviceUtil.dp2px(context, 11f).toInt(), 0)
                    tv_status.text = "已完结"
                    tv_status.textColor = mContext?.let {
                        ContextCompat.getColor(it, R.color.color_5467ff)
                    }!!
                    tv_status.background = mContext?.let {
                        ContextCompat.getDrawable(it, R.drawable.shape_8_e8ebff)
                    }
                    tv_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_complete, 0, 0, 0)
                }
                else -> {
                    tv_status.setPadding(DeviceUtil.dp2px(context, 11f).toInt(), 0, DeviceUtil.dp2px(context, 11f).toInt(), 0)
                    tv_status.text = "进入学习"
                    tv_status.textColor = mContext?.let {
                        ContextCompat.getColor(it, R.color.color_5467ff)
                    }!!
                    tv_status.background = mContext?.let {
                        ContextCompat.getDrawable(it, R.drawable.shape_8_e8ebff)
                    }
                    tv_status.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        }
    }

    override fun emptyImageRes(): Int {
        return R.drawable.empty_study
    }

    override fun emptyText(): String {
        return "暂无课程"
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }
}