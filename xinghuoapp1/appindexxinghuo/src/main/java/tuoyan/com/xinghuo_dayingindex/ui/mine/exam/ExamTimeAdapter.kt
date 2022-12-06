package tuoyan.com.xinghuo_dayingindex.ui.mine.exam

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.examList
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

class ExamTimeAdapter:BaseAdapter<examList>(isEmpty = true, isFooter = true) {
    private var nums= arrayListOf<Int>(R.id.num_0,R.id.num_1,R.id.num_2,R.id.num_3,)
    override fun convert(holder: ViewHolder, item: examList) {
        if (holder.bindingAdapterPosition==0){
            holder.setVisible(R.id.view,View.VISIBLE)
        }else{holder.setVisible(R.id.view,View.GONE)}
        holder.setText(R.id.tv_name,item.name)
        val b = if (item.days .toInt()< 10)"0"+item.days else item.days
     var chars= b.toString().toCharArray();
        if (chars.size>0){
            for (i in nums.indices){
                if (i<chars.size){
                    holder.setText(nums[i],chars[i].toString())
                    holder.setVisible(nums[i],View.VISIBLE)
                }else{
                    holder.setVisible(nums[i],View.GONE)
                }
            }

        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.item_exam_time, parent,false)
    }
    override fun emptyView(context: Context, parent: ViewGroup): View {
        var view=LayoutInflater.from(context).inflate(R.layout.layout_ebook_empty, parent, false)
        view.findViewById<TextView>(R.id.tv).text="暂无倒计时~"
        return view
    }
//    override fun emptyImageRes(): Int {
//        return R.mipmap.icon_empty_sentence
//    }
//
//    override fun emptyText(): String {
//        return "购课后才能显示倒计时哦~"
//    }
    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            DeviceUtil.dp2px(context, 60f).toInt()
        )
        view.layoutParams = params
        return view
    }
}