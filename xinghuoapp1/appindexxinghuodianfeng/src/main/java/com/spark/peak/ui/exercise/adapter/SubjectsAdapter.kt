package com.spark.peak.ui.exercise.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Subject
import org.jetbrains.anko.*

class SubjectsAdapter(var onClick : (name: String, key: String) -> Unit) : BaseAdapter<Subject>(){
    override fun convert(holder: ViewHolder, item: Subject) {
        holder.setBackgroundResource(R.id.tv_subject,item.background())
                .setText(R.id.tv_subject,item.name)
                .setOnClickListener(R.id.tv_subject){

                    onClick(getData()[holder.adapterPosition].name?:"",getData()[holder.adapterPosition].key?:"")
                }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        relativeLayout {
            textView {
                id = R.id.tv_subject
                textSize = 18f
                textColor = Color.parseColor("#323232")
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                topPadding = dip(22)
                leftPadding = dip(56)
            }.lparams(dip(115),dip(143)){
                topMargin = dip(10)
            }
        }
    }
}