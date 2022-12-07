package com.spark.peak.ui.exercise.adapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.example.questions.Item
import com.example.questions.Question
import com.example.questions.QuestionInfo
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Answer
import com.spark.peak.bean.AnswerQuestion
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView


class AnswerCardAdapter2(var onclick: (String?) -> Unit) : BaseAdapter<Question>() {
    override fun convert(holder: ViewHolder, item: Question) {
        val data = mutableListOf<Any>()
        if (item.questionlist() != null && item.questionlist()!!.isNotEmpty()) {
            item.questionlist()?.forEach {
                if (it.questionlist() != null && it.questionlist()!!.isNotEmpty()) {
                    it.questionInfo()?.item()?.let {
                        data.addAll(it)
                    }
                } else if (it.questionInfo() != null) {
                    data.add(it.questionInfo()!!)
                }
            }
        } else if (item.questionInfo() != null) {
            data.add(item.questionInfo()!!)
        }
        var oAdapter = AnswerCardItemAdapter2(onclick)
        oAdapter.setData(data)
        holder.setText(R.id.tv_q_name, item.groupName())
                .setAdapter(R.id.rv_answers, oAdapter)

    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, wrapContent)
            textView {
                id = R.id.tv_q_name
                textColor = Color.parseColor("#1e1e1e")
                textSize = 12f
            }.lparams(wrapContent, wrapContent) {
                leftMargin = dip(15)
            }
            recyclerView {
                id = R.id.rv_answers
                layoutManager = GridLayoutManager(context, 5)
            }.lparams(matchParent, wrapContent) {
                topMargin = dip(18)
            }
        }
    }

}

class AnswerCardItemAdapter2(var onclick: (String?) -> Unit) : BaseAdapter<Any>() {
    override fun convert(holder: ViewHolder, item: Any) {
        if (item is Item ) {
            holder.setText(R.id.tv_sort, item.sort())
                    .setBackgroundResource(R.id.tv_sort,
                            if (item.useranswer().isNullOrBlank()) R.drawable.shape_answer_normal else R.drawable.shape_answer_done)
                    .setTextColor(R.id.tv_sort,
                            if (item.useranswer().isNullOrBlank()) Color.parseColor("#1482ff") else Color.parseColor("#ffffff"))
            holder.itemView.setOnClickListener {
                onclick(item.sort())
            }
        }
        if (item is QuestionInfo) {
            holder.setText(R.id.tv_sort, item.sort())
                    .setBackgroundResource(R.id.tv_sort,
                            if (item.useranswer().isNullOrBlank()) R.drawable.shape_answer_normal else R.drawable.shape_answer_done)
                    .setTextColor(R.id.tv_sort,
                            if (item.useranswer().isNullOrBlank()) Color.parseColor("#1482ff") else Color.parseColor("#ffffff"))
            holder.itemView.setOnClickListener {
                onclick(item.sort())
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
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
            }.lparams {
                centerHorizontally()
            }

        }
    }

}

class AnswerCardQuestionAdapter2(var onclick: (String?) -> Unit) : BaseAdapter<QuestionInfo>() {
    override fun convert(holder: ViewHolder, item: QuestionInfo) {
        holder.setText(R.id.tv_sort, item.sort())
                .setBackgroundResource(R.id.tv_sort,
                        if (item.useranswer().isNullOrBlank()) R.drawable.shape_answer_normal else R.drawable.shape_answer_done)
                .setTextColor(R.id.tv_sort,
                        if (item.useranswer().isNullOrBlank()) Color.parseColor("#1482ff") else Color.parseColor("#ffffff"))
        holder.itemView.setOnClickListener {
            onclick(item.sort())
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
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
            }.lparams {
                centerHorizontally()
            }

        }
    }

}