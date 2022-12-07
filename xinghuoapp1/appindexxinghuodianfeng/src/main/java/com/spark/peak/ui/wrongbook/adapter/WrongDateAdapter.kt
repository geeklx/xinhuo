package com.spark.peak.ui.wrongbook.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.WrongBookDate
import org.jetbrains.anko.*

class WrongDateAdapter(var click : (String,Int) -> Unit) : BaseAdapter<WrongBookDate>(){
    override fun convert(holder: ViewHolder, item: WrongBookDate) {
        holder.setText(R.id.tv_date,item.time + "(" + item.count + "题)")
                .setOnClickListener(R.id.tv_date){
                    it as TextView
                    click(item.time?:"",item.count)
                }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        verticalLayout {
            textView {
                id = R.id.tv_date
//                setCompoundDrawables(resources.getDrawable(R.drawable.ic_wrongbook),null,resources.getDrawable(R.mipmap.ic_navigation_right),null)
                textSize = 12f
                textColor = Color.parseColor("#323232")
                gravity = Gravity.CENTER_VERTICAL
            }.lparams(matchParent, dip(40)){
                leftPadding = dip(15)
            }
            view {
                backgroundColor = Color.parseColor("#e6e6e6")
            }.lparams(matchParent,2)
        }
    }
}