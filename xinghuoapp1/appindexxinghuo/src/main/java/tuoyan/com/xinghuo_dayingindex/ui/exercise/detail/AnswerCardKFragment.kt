package tuoyan.com.xinghuo_dayingindex.ui.exercise.detail

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_answer_card.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.AnswerCardAdapter

class AnswerCardKFragment : LifeV4Fragment<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_answer_card
    private var lastCommitTime = 0L
    private val activity by lazy { getActivity() as ExerciseDetailKActivity }
    private val oAdapter = AnswerCardAdapter { qPosition, mPosition ->
        activity.positionQuestion(qPosition, mPosition)
    }

    override fun configView(view: View?) {
        tv_paper_name.text = activity.paperName
        rv_answer_card.layoutManager = LinearLayoutManager(context)
        rv_answer_card.adapter = oAdapter
    }

    fun showList() {
        oAdapter.setData(activity.answerList)
        oAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val FAST_CLICK_DELAY_TIME = 2000L
    }

    override fun handleEvent() {
        tv_submit.setOnClickListener {
            //避免连续点击
            if (System.currentTimeMillis() - lastCommitTime < FAST_CLICK_DELAY_TIME) {
                lastCommitTime = System.currentTimeMillis()
            } else {
                lastCommitTime = System.currentTimeMillis()
                activity.saOption("交卷")
                activity.submit()
            }
        }
    }
}