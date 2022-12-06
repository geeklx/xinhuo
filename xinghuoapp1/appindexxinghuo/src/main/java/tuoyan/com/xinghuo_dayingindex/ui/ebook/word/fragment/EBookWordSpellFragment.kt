package tuoyan.com.xinghuo_dayingindex.ui.ebook.word.fragment

import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_ebook_word_spell.*
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.EBookPractice
import tuoyan.com.xinghuo_dayingindex.bean.EBookPracticeQuestion
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordPracticeActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.adpter.EBookWordSpellAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.adpter.EBookWordSpellChoiceAdapter

private const val PRACTICE = "PRACTICE"

class EBookWordSpellFragment : LifeV4Fragment<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_ebook_word_spell

    private val practice by lazy { arguments?.getParcelable(PRACTICE) as? EBookPractice }

    private val activity by lazy { requireActivity() as EBookWordPracticeActivity }
    private val answers by lazy { mutableListOf<QuestionInfoItem>() }
    private val optList by lazy { mutableListOf<QuestionInfoItem>() }

    private val answer by lazy { EBookPracticeQuestion() }

    private val adapter by lazy {
        EBookWordSpellAdapter() { info ->
            deleteOpt(info)
        }
    }
    private val optAdapter by lazy {
        EBookWordSpellChoiceAdapter { info ->
            addOpt(info)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(practice: EBookPractice) = EBookWordSpellFragment().apply {
            arguments = Bundle().apply {
                putParcelable(PRACTICE, practice)
            }
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        val layoutManager = LinearLayoutManager(this.requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rlv_spell.layoutManager = layoutManager
        rlv_spell.adapter = adapter
        rlv_opt.layoutManager = GridLayoutManager(this.requireContext(), 3)
        rlv_opt.adapter = optAdapter
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_next.setOnClickListener {
            activity.toNext()
        }
    }

    override fun initData() {
        super.initData()
        answers.clear()
        optList.clear()
        practice?.let { que ->
            val question = que.questionList[que.qIndex]
            answer.appQuestionKey = question.questionKey
            when (question.examType) {
                "6" -> {
                    tv_spell.text = Html.fromHtml(question.stem)
                    for (index in 1..question.rightanswer.length) {
                        val item = QuestionInfoItem()
                        answers.add(item)
                    }
                    adapter.setData(answers)
                    optList.addAll(question.item)
                    val item = QuestionInfoItem()
                    item.type = "1"
                    optList.add(item)
                    optAdapter.setData(optList)
                }
            }
            var answerContent = ""
            question.rightanswer.forEach { order ->
                question.item.forEach { info ->
                    if ("$order" == info.order) {
                        answerContent = "$answerContent${info.content}"
                    }
                }
            }
            question.rightAnswerContent = answerContent
        }
    }

    private fun deleteOpt(info: QuestionInfoItem) {
        kotlin.run breaking@{
            optList.forEach { opt ->
                if (opt.content == info.content) {
                    opt.isAnswer = "0"
                    return@breaking
                }
            }
        }
        info.content = ""
        info.order = ""
        adapter.notifyDataSetChanged()
        optAdapter.notifyDataSetChanged()
    }

    private fun addOpt(info: QuestionInfoItem) {
        info.isAnswer = "2"
        optAdapter.notifyDataSetChanged()
        if (info.type == "1") {
            showError("未作答")
        } else {
            var answerNum = 0
            kotlin.run breaking@{
                answers.forEach { answer ->
                    answerNum++
                    if (answer.content.isBlank()) {
                        answer.content = info.content
                        answer.order = info.order
                        return@breaking
                    }
                }
            }
            if (answerNum == answers.size) {
                var userWord = ""
                var userOrder = ""
                answers.forEach { answer ->
                    userWord = "${userWord}${answer.content}"
                    userOrder = "${userOrder}${answer.order}"
                }
                answer.questionAnswer = userOrder
                practice?.let { que ->
                    val question = que.questionList[que.qIndex]
                    if (userOrder == question.rightanswer) {
                        showRight()
                    } else {
                        showError(userWord)
                    }
                }
            } else {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showError(txt: String) {
        practice?.let { que ->
            val question = que.questionList[que.qIndex]
            tv_right.text = question.rightAnswerContent
        }
        line_r.visibility = View.VISIBLE
        tv_right.visibility = View.VISIBLE
        img_r_r.visibility = View.VISIBLE
        rlv_spell.visibility = View.GONE
        //错误作答 展示正确答案 展示错误答案
        tv_err.text = txt
        tv_err.visibility = View.VISIBLE
        line_error.visibility = View.VISIBLE
        img_e_r.visibility = View.VISIBLE
        Handler().postDelayed({
            showAnalysis()
        }, 1000)
    }

    private fun showRight() {
        //正确作答 展示正确答案 隐藏错误控件
        practice?.let { que ->
            val question = que.questionList[que.qIndex]
            tv_right.text = question.rightAnswerContent
        }
        line_r.visibility = View.VISIBLE
        tv_right.visibility = View.VISIBLE
        img_r_r.visibility = View.VISIBLE
        rlv_spell.visibility = View.GONE
        answer.isRight = "1"
        activity.addAnswer(answer)
        tv_err.visibility = View.GONE
        line_error.visibility = View.GONE
        img_e_r.visibility = View.GONE
        Handler().postDelayed({
            activity.toNext()
        }, 1000)
    }

    private fun showAnalysis() {
        answer.isRight = "0"
        activity.addAnswer(answer)
        practice?.let {
            activity.addFragment(it)
            val question = it.questionList[it.qIndex]
            Glide.with(this.requireActivity()).load(question.imgUrl).into(img_word)
        }
        img_word.visibility = View.VISIBLE
        tv_next.visibility = View.VISIBLE
    }
}