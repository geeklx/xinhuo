package com.spark.peak.ui.exercise.detail

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.questions.Question
import com.spark.peak.R
import com.spark.peak.base.LifeFragment
import com.spark.peak.ui.exercise.adapter.AnswerCardAdapter2
import kotlinx.android.synthetic.main.fragment_answer_carddf.*

class AnswerCardFragment2 : LifeFragment<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_answer_carddf
    var data: MutableList<Question>? = null
    val activity: ExerciseDetailActivity2 by lazy { getActivity() as ExerciseDetailActivity2 }
    var oAdapter = AnswerCardAdapter2 {
      it?.let {
          activity.positionQuestion(it)
      }
    }

    override fun configView(view: View?) {
        tv_paper_name.text = activity.paperName
        rv_answer_card.layoutManager = LinearLayoutManager(this.requireContext())
        rv_answer_card.adapter = oAdapter
    }

    fun showList() {
        oAdapter.setData(data)
//        oAdapter.notifyDataSetChanged()
    }

    override fun handleEvent() {
        tv_submit.setOnClickListener {
            activity.submit()
        }
    }

}