package com.spark.peak.ui.exercise.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.ExBooks
import org.jetbrains.anko.*

/**
 * Created by 李昊 on 2018/5/3.
 */
class VersionBooksAdapter(var onclick : (ExBooks)->Unit) : BaseAdapter<ExBooks>(){
    override fun convert(holder: ViewHolder, item: ExBooks) {
        holder.setText(R.id.iv_version_book_name,item.name)
                .setImageUrl(R.id.iv_version_book_img,item.img?:"",R.drawable.default_book)

        holder.itemView.setOnClickListener {
            onclick(getData()[holder.adapterPosition])
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        relativeLayout {
            imageView {
                id = R.id.iv_version_book_img
                scaleType = ImageView.ScaleType.FIT_XY
            }.lparams(dip(129),dip(167)){
                topMargin = dip(15)
                leftMargin = dip(15)
            }
            textView {
                id = R.id.iv_version_book_name
                textSize = 15f
                textColor = Color.parseColor("#1e1e1e")
            }.lparams(wrapContent, wrapContent){
                leftMargin = dip(20)
                bottomMargin = dip(5)
                below(R.id.iv_version_book_img)
                topMargin = dip(5)
            }

        }
    }
}