package tuoyan.com.xinghuo_dayingindex.ui.main.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.GradeMenu
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.img.GlideImageLoader

/**
 * Created by Zzz on 2021/3/3
 * Email:
 */

class GradeMenuAdapter(val onItemClick: (GradeMenu, Int) -> Unit) : BaseAdapter<GradeMenu>() {
    override fun convert(holder: ViewHolder, item: GradeMenu) {
        holder.setText(R.id.tv_title, item.name)
        val imgView = holder.getView(R.id.img_menu) as ImageView
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.mipmap.icon_def)
        GlideImageLoader.create(imgView).load(item.imgUrl, requestOptions)
        holder.itemView.setOnClickListener {
            onItemClick(item, holder.adapterPosition)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.layout_grade_menu_item, parent, false)
    }
}

class TeacherAdapter : BaseAdapter<GradeMenu>(isFooter = true) {
    override fun convert(holder: ViewHolder, item: GradeMenu) {
        holder.setText(R.id.tv_teacher, item.name)
            .setImageUrl(R.id.img_teacher, item.imgUrl, 0, 0)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.layout_teacher, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getDateCount() == 1 && position < getDateCount()) {
            return O_H
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            11 -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_teacher_h, parent, false))
            }
            else -> {
                super.onCreateViewHolder(parent, viewType)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when {
            holder.itemViewType == 11 -> {
                convert(holder, getData()[position])
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    companion object {
        const val O_H = 11
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = RecyclerView.LayoutParams(DeviceUtil.dp2px(context, 30f).toInt(), DeviceUtil.dp2px(context, 10f).toInt())
        view.layoutParams = params
        return view
    }
}

class PointAdapter(val color: String) : BaseAdapter<String>() {
    override fun convert(holder: ViewHolder, item: String) {
        if (holder.layoutPosition == getDateCount() - 1) {
            holder.setVisible(R.id.v_p, View.GONE)
        }
        if (!color.isNullOrBlank()) {
            holder.setTextColor(R.id.tv_point, Color.parseColor(color))
        }
        holder.setText(R.id.tv_point, item)

    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.layout_point, parent, false)
    }

}