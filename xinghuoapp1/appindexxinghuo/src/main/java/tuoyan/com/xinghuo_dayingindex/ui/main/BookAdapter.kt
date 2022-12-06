package tuoyan.com.xinghuo_dayingindex.ui.main

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.horizontalPadding
import org.jetbrains.anko.textColor
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.BookList

class BookAdapter(var onItemClick: (item: BookList) -> Unit) : BaseAdapter<BookList>(isFooter = true, isEmpty = true) {
    override fun convert(holder: ViewHolder, item: BookList) {
        var img_book_cover = holder.getView(R.id.img_book_cover) as ImageView
        Glide.with(holder.itemView.context).load(item.img).into(img_book_cover)
        holder.setText(R.id.tv_title, item.title)
                .setText(R.id.tv_disprice, "￥${item.disprice}")
                .setText(R.id.tv_saleNum, "已有${item.saleNum}人购买")

        var ll_tags = holder.getView(R.id.ll_tags) as LinearLayout
        ll_tags.removeAllViews()
        if (!item.sellingPoint.isNullOrBlank()) {
            item.sellingPoint.split(",").forEach {
                var tv_tag = TextView(mContext)
                var layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                layoutParams.rightMargin = 10
                mContext?.let {
                    tv_tag.background = ContextCompat.getDrawable(it, R.drawable.shape_remark)
                }
                tv_tag.gravity = Gravity.CENTER
                tv_tag.horizontalPadding = 14
                tv_tag.text = it
                mContext?.let {
                    tv_tag.textColor = ContextCompat.getColor(it, R.color.color_7a)
                }
                tv_tag.textSize = 11f
                ll_tags.addView(tv_tag, layoutParams)
            }
        }
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun convertView(context: Context,parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_book_store_item, null)
    }

    override fun emptyImageRes(): Int {
        return R.drawable.empty_study
    }

    override fun emptyText(): String {
        return "暂无内容"
    }
}