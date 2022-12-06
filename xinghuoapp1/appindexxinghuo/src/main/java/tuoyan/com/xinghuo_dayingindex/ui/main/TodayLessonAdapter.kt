package tuoyan.com.xinghuo_dayingindex.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.textColor
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.TodyLesson
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil


//"id":"]sj",
//"name":"L(Wo)t",
//"parentKey":"jq99Jo",
//"teacher":"S3uk1",
//"liveState":"J8Rw9r",
//"liveTime":"Ulg55"

class TodayLessonAdapter(var itemClick: (TodyLesson) -> Unit) : BaseAdapter<TodyLesson>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: TodyLesson) {
        val rl_item = holder.getView(R.id.rl_item) as RelativeLayout
        val lparams = rl_item.layoutParams as RecyclerView.LayoutParams
        if (holder.adapterPosition == 0) {
            lparams.leftMargin = DeviceUtil.dp2px(holder.itemView.context,15f).toInt()
            lparams.rightMargin = DeviceUtil.dp2px(holder.itemView.context,4f).toInt()
        } else if (holder.adapterPosition == getDateCount() - 1) {
            lparams.leftMargin = DeviceUtil.dp2px(holder.itemView.context,4f).toInt()
            lparams.rightMargin = DeviceUtil.dp2px(holder.itemView.context,15f).toInt()
        } else {
            lparams.leftMargin = DeviceUtil.dp2px(holder.itemView.context,4f).toInt()
            lparams.rightMargin = DeviceUtil.dp2px(holder.itemView.context,4f).toInt()
        }
        holder.setText(R.id.tv_time, item.liveTime)
            .setText(R.id.tv_title, item.name)
            .setText(R.id.tv_teacher, item.teacher)
        setLiveStateStr(holder, item)
        holder.itemView.setOnClickListener {
            itemClick(item)
        }

    }

    fun setLiveStateStr(holder: ViewHolder, item: TodyLesson) {
        val tv_status = holder.getView(R.id.tv_status) as TextView
        when (item.liveState) {
            "0" -> {
//                "<span style='color:#c7cdd7'>未开课</span>"
                tv_status.visibility = View.VISIBLE
                tv_status.text = "未开课"
                tv_status.textColor = mContext?.let {
                    ContextCompat.getColor(it, R.color.color_8e)
                }!!
                tv_status.background = mContext?.let {
                    ContextCompat.getDrawable(it, R.drawable.shape_10_ededf1)
                }
                tv_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.point_w_12_c_8e, 0, 0, 0)
            }
            "1" -> {
//                "<span style='color:#00ca0d'>直播中</span>"
                tv_status.visibility = View.VISIBLE
                tv_status.text = "直播中"
                tv_status.textColor = mContext?.let {
                    ContextCompat.getColor(it, R.color.color_5467ff)
                }!!
                tv_status.background = mContext?.let {
                    ContextCompat.getDrawable(it, R.drawable.shape_10_e8ebff)
                }
                tv_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_living, 0, 0, 0)
            }
            "2" -> {
                tv_status.visibility = View.VISIBLE
                tv_status.text = "回放生成中"
                tv_status.textColor = mContext?.let {
                    ContextCompat.getColor(it, R.color.color_ff7100)
                }!!
                tv_status.background = mContext?.let {
                    ContextCompat.getDrawable(it, R.drawable.bg_c_ff_r_10)
                }
                tv_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.point_w_12_c_ff, 0, 0, 0)
            }
            "3" -> {
//                "<span style='color:#00ca0d'>已转录</span>"
                tv_status.visibility = View.VISIBLE
                tv_status.text = "已转录"
            }
            "4" -> {
//                "<span style='color:#4c84ff'>观看回放</span>"
                tv_status.visibility = View.VISIBLE
                tv_status.text = "观看回放"
                tv_status.textColor = mContext?.let {
                    ContextCompat.getColor(it, R.color.color_5467ff)
                }!!
                tv_status.background = mContext?.let {
                    ContextCompat.getDrawable(it, R.drawable.shape_10_e8ebff)
                }
                tv_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_replay, 0, 0, 0)
            }
            "5" -> {
//                "<span style='color:#c7cdd7'>未上课</span>"
                tv_status.visibility = View.VISIBLE
                tv_status.text = "未上课"
                tv_status.textColor = mContext?.let {
                    ContextCompat.getColor(it, R.color.color_8e)
                }!!
                tv_status.background = mContext?.let {
                    ContextCompat.getDrawable(it, R.drawable.shape_10_ededf1)
                }
                tv_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.point_w_12_c_8e, 0, 0, 0)
            }
            "99" -> {
//                "<span style='color:#4cf4ff'>今日开课</span>"
                tv_status.visibility = View.VISIBLE
                tv_status.text = "今日开课"
            }
            else -> {
                tv_status.visibility = View.GONE
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = LayoutInflater.from(context).inflate(R.layout.layout_today_class_item, parent, false)

    override fun emptyView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_empty_today_lesson,parent,false)
    }
}