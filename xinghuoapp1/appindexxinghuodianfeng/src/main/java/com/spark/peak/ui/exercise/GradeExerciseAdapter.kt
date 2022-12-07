package com.spark.peak.ui.exercise

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.GradeExercise
import org.jetbrains.anko.*

/**
 */
class GradeExerciseAdapter(var onItemClick: (GradeExercise) -> Unit) : BaseAdapter<GradeExercise>(isEmpty = true) {

    override fun convert(holder: ViewHolder, item: GradeExercise) {
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
        holder.setText(R.id.tv_name, item.name).setImageUrl(R.id.img_cover, item.img, placeHolder = R.mipmap.icon_default)
        val progress = holder.getView(R.id.progress) as ProgressBar
        try {
            progress.progress = item.finishNum.toInt() * 100 / item.totalNum.toInt()
        } catch (e: Exception) {
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_grade_exercise, parent, false)
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, matchParent)
            gravity = Gravity.CENTER_HORIZONTAL
            space().lparams(wrapContent, dip(120))
            imageView(R.mipmap.empty_3)
            textView("暂无内容~") {
                gravity = Gravity.CENTER
                textSize = 14f
                textColor = resources.getColor(R.color.color_999999)
            }
        }
    }
}