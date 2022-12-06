package tuoyan.com.xinghuo_dayingindex.ui.cc.adapter

import android.graphics.Paint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.bean.Lesson
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

class LessonAdapter(val onItemClick: (key: String) -> Unit) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {
    private var dataList: List<Lesson>? = null

    fun setData(data: List<Lesson>?) {
        dataList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_home_class_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val context = viewHolder.itemView.context
        if (i == 0) {
            val params = viewHolder.itemView.layoutParams as RecyclerView.LayoutParams
            params.topMargin = DeviceUtil.dp2px(context, 15f).toInt()
        }
        viewHolder.itemView.background = ContextCompat.getDrawable(context, R.drawable.bg_network_item_ht)
        val item = dataList!![i]
        Glide.with(context)
            .load(item.img)
            .into(viewHolder.img_cover)
        viewHolder.tv_old_price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        viewHolder.tv_title.text = item.title
        viewHolder.tv_buy_num.text = buyNum(item)
        viewHolder.tv_new_price.text = ownStr(item)
        viewHolder.tv_old_price.text = getOlderPrice(item)
        viewHolder.tv_cover_tag.text = item.netSubjectName
        viewHolder.tv_cover_tag.visibility = coverTagShow(item)
        viewHolder.tv_time.text = getTime(item)
        viewHolder.tv_teacher.text = item.teacher.replace(",", "\u0020")
        viewHolder.ll_tags.removeAllViews()
        if (item.sellingPoint != null && !item.sellingPoint.isEmpty()) {
            for (str in item.sellingPoint.split(",".toRegex()).toTypedArray()) {
                val tvPoint = TextView(context)
                tvPoint.text = str
                tvPoint.textSize = 11f
                tvPoint.gravity = Gravity.CENTER
                tvPoint.setPadding(14, 0, 14, 0)
                tvPoint.setTextColor(ContextCompat.getColor(context, R.color.color_7a))
                tvPoint.background = ContextCompat.getDrawable(context, R.drawable.shape_remark)
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                params.rightMargin = 10
                viewHolder.ll_tags.addView(tvPoint, params)
            }
        }
        viewHolder.itemView.setOnClickListener { onItemClick(item.key) }
    }

    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    private fun getTime(item: Lesson): String {
        return if (item.liveTime != null && item.liveTime.isEmpty()) {
            item.period + "课时"
        } else {
            item.liveTime + "|" + item.period + "课时"
        }
    }

    private fun coverTagShow(item: Lesson): Int {
        return if (item.netSubjectName.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun getOlderPrice(item: Lesson): String {
        return if ("1" == item.isown || "0" == item.chargeType) "" else "¥" + item.price
    }

    private fun buyNum(item: Lesson): String {
        return if ("1" != item.isown && "1" == item.isAppointment) {
            //没有购买，预约
            "将于" + item.upStartTime + "开售"
        } else if ("1" == item.form || "3" == item.form) {
            "限购" + item.limitBuyers + "人\u3000已有" + item.buyers + "人购买"
        } else {
            "已有" + item.buyers + "人购买"
        }
    }

    private fun ownStr(item: Lesson): String {
        return if ("1" == item.isown && "0" == item.chargeType) {
            "已领取"
        } else if ("1" == item.isown && "1" == item.chargeType) {
            "已购买"
        } else if ("1" == item.saleOut) {
            "已售罄"
        } else if ("1" == item.isAppointedNet && "1" == item.isAppointment) {
            "已预约"
        } else if ("1" == item.isAppointment) {
            "正在预约"
        } else if ("0" == item.chargeType) {
            "免费"
        } else if ("1" == item.isLimitFree) {
            "限时免费"
        } else {
            "¥" + item.disprice
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_cover: ImageView
        var tv_cover_tag: TextView
        var tv_title: TextView
        var ll_tags: LinearLayout
        var tv_teacher: TextView
        var tv_time: TextView
        var tv_buy_num: TextView
        var tv_new_price: TextView
        var tv_old_price: TextView

        init {
            img_cover = itemView.findViewById(R.id.img_cover)
            tv_cover_tag = itemView.findViewById(R.id.tv_cover_tag)
            tv_title = itemView.findViewById(R.id.tv_title)
            ll_tags = itemView.findViewById(R.id.ll_tags)
            tv_teacher = itemView.findViewById(R.id.tv_teacher)
            tv_time = itemView.findViewById(R.id.tv_time)
            tv_buy_num = itemView.findViewById(R.id.tv_buy_num)
            tv_new_price = itemView.findViewById(R.id.tv_new_price)
            tv_old_price = itemView.findViewById(R.id.tv_old_price)
        }
    }
}