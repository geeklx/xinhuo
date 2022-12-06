package tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_e_book_answer_card.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListenExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.AnswerCardAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordExerciseActivity

class EBookAnswerCardFragment : LifeV4Fragment<EBookPresenter>() {
    private val mActivity by lazy { requireActivity() }
    private val adapter by lazy {
        AnswerCardAdapter() { item ->
            when (val ctx = mActivity) {
                is EBookExerciseActivity -> {
                    ctx.scrollPos(item.qPosition, item.mPosition)
                }
                is EBookListenExerciseActivity -> {
                    ctx.scrollPos(item.qPosition, item.mPosition)
                }
                is EBookWordExerciseActivity -> {
                    ctx.scrollPos(item.qPosition)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EBookAnswerCardFragment().apply {}
    }

    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_e_book_answer_card

    override fun configView(view: View?) {
        super.configView(view)
        rlv_answers.layoutManager = GridLayoutManager(this.requireContext(), 5)
        rlv_answers.adapter = adapter
    }

    override fun initData() {
        super.initData()
    }

    fun showAnswerCard() {
        when (val ctx = mActivity) {
            is EBookExerciseActivity -> {
                adapter.setData(ctx.answerList)
            }
            is EBookListenExerciseActivity -> {
                adapter.setData(ctx.answerList)
            }
            is EBookWordExerciseActivity -> {
                adapter.setData(ctx.answerList)
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_submit.setOnClickListener {
            when (val ctx = mActivity) {
                is EBookExerciseActivity -> {
                    ctx.submit()
                }
                is EBookListenExerciseActivity -> {
                    ctx.submit()
                }
                is EBookWordExerciseActivity -> {
                    ctx.submit()
                }
            }
        }
    }
}