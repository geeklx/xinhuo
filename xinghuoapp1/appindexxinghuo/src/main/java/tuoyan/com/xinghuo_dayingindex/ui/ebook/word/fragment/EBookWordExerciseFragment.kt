package tuoyan.com.xinghuo_dayingindex.ui.ebook.word.fragment

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_e_book_word_exercise.*
import kotlinx.android.synthetic.main.fragment_practice.lav_audio
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.AnswerItem
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.ChoiceAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordExerciseActivity
import tuoyan.com.xinghuo_dayingindex.utlis.MediaPlayerUtlis

private const val QUESTION_INFO = "QuestionInfo"

class EBookWordExerciseFragment : LifeV4Fragment<EBookPresenter>() {
    override val layoutResId: Int
        get() = R.layout.fragment_e_book_word_exercise
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)

    private val info by lazy { arguments?.getSerializable(QUESTION_INFO) as QuestionInfo }
    private val activity by lazy { requireActivity() as EBookWordExerciseActivity }

    private val adapter by lazy {
        ChoiceAdapter("5") {
            initAnswer(it.order)
            activity.goNext()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(info: QuestionInfo) =
            EBookWordExerciseFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(QUESTION_INFO, info)
                }
            }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_opt.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_opt.adapter = adapter
    }

    override fun initData() {
        super.initData()
        //?????????4,?????????5 ????????????:8 ????????????:9 ????????????10
        when (info.examType) {
            "4" -> {
                tv_en_l.visibility = View.VISIBLE
                tv_en.visibility = View.VISIBLE
                tv_en.textSize = 32f
                tv_en.text = Html.fromHtml(info.stem ?: "")
            }
            "5" -> {
                tv_en_l.visibility = View.VISIBLE
                tv_en.visibility = View.VISIBLE
                tv_en.textSize = 20f
                tv_en.text = Html.fromHtml(info.stem ?: "")
            }
            "8" -> {
                scroll.visibility = View.VISIBLE
                v_shadow.visibility = View.VISIBLE
                tv_title.text = Html.fromHtml(info.stem ?: "")
                tv_title_sec.text = Html.fromHtml(info.boSource ?: "")
            }
            "9" -> {
                lav_audio.visibility = View.VISIBLE
                scroll.visibility = View.VISIBLE
                v_shadow.visibility = View.VISIBLE
                tv_title.text = Html.fromHtml(info.stem ?: "")
                tv_title_sec.text = Html.fromHtml(info.boSource ?: "")
            }
            "10" -> {
                scroll.visibility = View.VISIBLE
                v_shadow.visibility = View.VISIBLE
                tv_title.text = Html.fromHtml(info.stem ?: "")
                tv_title_sec.text = Html.fromHtml(info.boSource ?: "")
            }
        }
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val infoList = gson.fromJson<List<QuestionInfoItem>>(gson.toJson(info.item), object : TypeToken<List<QuestionInfoItem>>() {}.type)//any ?????? questionInfo??????
        adapter.setData(infoList)
    }

    override fun handleEvent() {
        super.handleEvent()
        lav_audio.setOnClickListener {
            audioStart()
        }
    }

    private fun initAnswer(answer: String) {
        activity.isDone = true
        val answers = mutableListOf<AnswerItem>()
        val answerItem = AnswerItem("1", answer, "")
        answers.add(answerItem)
        // ????????????????????????answerQ.answers=
        // ????????????answerQ.imgs
        //  answerQ.answerType
        // answerQ.answerText
        activity.answerList.forEach { qItem ->
            if (qItem.questionKey == info.questionKey) {
                qItem.haveDone = true
                qItem.answers.clear()
                qItem.answers.addAll(answers)
                return
            }
        }
    }

    fun audioStart() {
        if (info.resourceKey.isNotBlank() && info.examType == "9") {
            MediaPlayerUtlis.start(this.requireContext(), info.resourceKey, "", {
                lav_audio?.playAnimation()
            }, {
                lav_audio?.pauseAnimation()
                lav_audio?.frame = 0
            }) {
                lav_audio?.pauseAnimation()
                lav_audio?.frame = 0
            }
        }
    }

    fun audioPause() {
        if (info.resourceKey.isNotBlank() && info.examType == "9") {
            MediaPlayerUtlis.pause()
        }
    }
}