package tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter

import android.content.Context
import android.graphics.Paint
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.textColor
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.EBook
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * Created by Zzz on 2021/6/28
 * Email:
 * type 0 :学习训练营进  1：从训练营列表进
 */

class EBookAdapter(val type: String = "1", val onItemClick: (EBook, Int) -> Unit) : BaseAdapter<EBook>(isFooter = true, isEmpty = true) {
    override fun convert(holder: ViewHolder, item: EBook) {
        val tvOlderPrice = holder.getView(R.id.tv_disprice) as TextView
        tvOlderPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        holder.setImageUrl(R.id.img_cover, item.coverUrl, 0, 0)
            .setText(R.id.tv_title, item.name).setText(R.id.tv_time, item.endTime)
            .setText(R.id.tv_buy_num, "已购 ${item.buyers}")
        if (type == "0") {
            initStudyEBook(holder, item)
        } else {
            initEbookList(holder, item)
        }
        holder.itemView.setOnClickListener {
            onItemClick(item, holder.layoutPosition)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_ebook, parent, false)
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }

    override fun emptyView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_ebook_empty, parent, false)
    }


    private fun initEbookList(holder: ViewHolder, item: EBook) {
        val tvOlderPrice = holder.getView(R.id.tv_disprice) as TextView
        tvOlderPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        holder.setText(R.id.tv_price, Html.fromHtml("<small>￥</small>${item.price}"))
            .setText(R.id.tv_disprice, "￥${item.originalCost}")
            .setVisible(R.id.tv_price, if ("1" == item.isOwn || item.originalCost.isNullOrEmpty()) View.GONE else View.VISIBLE)
            .setVisible(R.id.tv_disprice, if ("1" == item.isOwn || item.originalCost.isNullOrEmpty()) View.GONE else View.VISIBLE)
            .setText(R.id.tv_right, if ("1" == item.isOwn) "去学习" else if (item.originalCost.isNullOrEmpty()) Html.fromHtml("<small>￥</small>${item.price}") else "")
            .setSelected(R.id.tv_right, item.isOwn == "1").setSelected(R.id.tv_buy_num, item.isOwn == "1")
            .setSelected(R.id.img_l, item.isOwn == "1").setSelected(R.id.v1, item.isOwn == "1")
    }

    private fun initStudyEBook(holder: ViewHolder, item: EBook) {
        holder.setVisible(R.id.tv_price, View.GONE)
            .setVisible(R.id.tv_disprice, View.GONE)
            .setText(R.id.tv_right, "去学习").setVisible(R.id.tv_right, if (item.isEffect == "0") View.VISIBLE else View.GONE)
            .setVisible(R.id.tv_out, if (item.isEffect == "0") View.GONE else View.VISIBLE).setVisible(R.id.img_l, if (item.isEffect == "0") View.VISIBLE else View.GONE)

        val tv_right = holder.getView(R.id.tv_right) as TextView
        val tv_buy_num = holder.getView(R.id.tv_buy_num) as TextView
        val img_l = holder.getView(R.id.img_l) as ImageView
        val v1 = holder.getView(R.id.v1)
        if (item.isEffect == "0") {//未过期
            tv_right.isSelected = true
            tv_buy_num.isSelected = true
            img_l.isSelected = true
            v1.isSelected = true
        } else {
            val context = holder.itemView.context
            tv_buy_num.textColor = ContextCompat.getColor(context, R.color.color_c4cbde)
            v1.background = ContextCompat.getDrawable(context, R.drawable.shape_9_f9fafc)
        }
    }
}