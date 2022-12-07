package com.spark.peak.ui.exercise.adapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Answer
import com.spark.peak.bean.AnswerQuestion
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView


class AnswerCardAdapter(var onclick : (Int,Int)->Unit) : BaseAdapter<Answer>(){
    override fun convert(holder: ViewHolder, item: Answer) {
        var oAdapter = AnswerCardItemAdapter(onclick)
        oAdapter.setData(item.qList)
        holder.setText(R.id.tv_q_name,item.qName)
                .setAdapter(R.id.rv_answers,oAdapter)

    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        verticalLayout {
            lparams(matchParent, wrapContent)
            textView {
                id = R.id.tv_q_name
                textColor = Color.parseColor("#1e1e1e")
                textSize = 12f
            }.lparams(wrapContent, wrapContent){
                leftMargin = dip(15)
            }
            recyclerView {
                id = R.id.rv_answers
                layoutManager = GridLayoutManager(context, 5)
            }.lparams(matchParent, wrapContent){
                topMargin = dip(18)
            }
        }
    }

}

class AnswerCardItemAdapter(var onclick : (Int,Int)->Unit) : BaseAdapter<AnswerQuestion>(){
    override fun convert(holder: ViewHolder, item: AnswerQuestion) {
        holder.setText(R.id.tv_sort,item.index.toString())
                .setBackgroundResource(R.id.tv_sort,if (item.haveDone) R.drawable.shape_answer_done else R.drawable.shape_answer_normal)
                .setTextColor(R.id.tv_sort,if (item.haveDone) Color.parseColor("#ffffff") else Color.parseColor("#1482ff"))
        holder.itemView.setOnClickListener {
            onclick(item.qPosition, item.mPosition)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        relativeLayout {
//            leftPadding = dip(18)
//            rightPadding = dip(18)
            bottomPadding = dip(18)
            textView {
                id = R.id.tv_sort
                backgroundResource = R.drawable.shape_answer_normal
                textColor = Color.parseColor("#1482ff")
                textSize = 14f
                gravity = Gravity.CENTER
            }.lparams{
                centerHorizontally()
            }

        }
    }

}