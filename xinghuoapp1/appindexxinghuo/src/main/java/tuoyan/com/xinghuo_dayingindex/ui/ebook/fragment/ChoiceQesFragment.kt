package tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_choice_qes.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.AnswerItem
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.ChoiceAdapter

private const val QUESTION_INFO = "QUESTIONINFO"

/**
 * 材料单选题
 */
class ChoiceQesFragment : LifeV4Fragment<EBookPresenter>() {
    private val questionInfo by lazy { arguments?.getSerializable(QUESTION_INFO) as? QuestionInfo }
    private val parentFragment by lazy { getParentFragment() as MaterialQesFragment }
    private val parentActivity by lazy { requireActivity() as EBookExerciseActivity }
    private val adapter by lazy {
        ChoiceAdapter() {
            initAnswer(it.order)
            parentFragment.goNext()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(questionInfo: QuestionInfo) =
            ChoiceQesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(QUESTION_INFO, questionInfo)
                }
            }
    }

    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_choice_qes

    override fun configView(view: View?) {
        super.configView(view)
        rlv_qes.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_qes.adapter = adapter
        adapter.setFooter(true)
    }

    override fun initData() {
        super.initData()
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val infoList = gson.fromJson<List<QuestionInfoItem>>(gson.toJson(questionInfo?.item), object : TypeToken<List<QuestionInfoItem>>() {}.type)//any 转为 questionInfo类型
        adapter.order = questionInfo?.useranswer ?: ""
        adapter.setData(infoList)
        questionInfo?.let { info ->
            if (info.useranswer.isNotEmpty()) {
                val time = try {
                    info.answerTime.toFloat()
                } catch (e: Exception) {
                    0f
                }
                initAnswer(info.useranswer, time)
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
    }

    private fun initAnswer(answer: String, time: Float = -1f) {
        parentActivity.isDown = true
        val answers = mutableListOf<AnswerItem>()
        val answerItem = AnswerItem("1", answer, "")
        answers.add(answerItem)
        // 答案单选多选问题answerQ.answers=
        // 图片作答answerQ.imgs
        //  answerQ.answerType
        // answerQ.answerText
        kotlin.run breaking@{
            parentActivity.answerList.forEach { qItem ->
                if (qItem.questionKey == questionInfo?.questionKey!!) {
                    qItem.haveDone = true
                    qItem.answers.clear()
                    qItem.answers.addAll(answers)
                    qItem.time = if (time == -1f) "${parentActivity.getAnswerTime()}" else "$time"
                    return@breaking
                }
            }
        }
    }
}