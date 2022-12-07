package com.spark.peak.ui.exercise.detail

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeFragment
import com.spark.peak.ui.exercise.adapter.AnswerCardAdapter
import kotlinx.android.synthetic.main.fragment_answer_card.*

class AnswerCardFragment : LifeFragment<ExerciseDetailPresenter>(){
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_answer_card

    val activity : ExerciseDetailActivity by lazy { getActivity() as ExerciseDetailActivity}
    var oAdapter = AnswerCardAdapter{ qPosition, mPosition ->
        activity.positionQuestion(qPosition, mPosition)
    }
    override fun configView(view: View?) {
        tv_paper_name.text = activity.paperName
        rv_answer_card.layoutManager = LinearLayoutManager(this.requireContext())
        rv_answer_card.adapter = oAdapter
    }

    fun showList(){
        oAdapter.setData(activity.answerList)
        oAdapter.notifyDataSetChanged()
    }
    override fun handleEvent() {
        tv_submit.setOnClickListener {
            activity.submit()
        }
    }
}