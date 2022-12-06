package tuoyan.com.xinghuo_dayingindex.ui.ebook.word.fragment

import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_practice.*
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.EBookPractice
import tuoyan.com.xinghuo_dayingindex.bean.EBookPracticeQuestion
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import tuoyan.com.xinghuo_dayingindex.bean.QuestionP
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.ChoiceAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordPracticeActivity
import tuoyan.com.xinghuo_dayingindex.utlis.MediaPlayerUtlis

private const val PRACTICE = "PRACTICE"

class PracticeFragment : LifeV4Fragment<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_practice

    private val practice by lazy { arguments?.getParcelable(PRACTICE) as? EBookPractice }

    private val activity by lazy { requireActivity() as EBookWordPracticeActivity }

    private val answer by lazy { EBookPracticeQuestion() }

    private val adapter by lazy {
        ChoiceAdapter("3") {
            updateAdapter(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(practice: EBookPractice) =
            PracticeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PRACTICE, practice)
                }
            }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_opt.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_opt.adapter = adapter
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_next.setOnClickListener {
            activity.toNext()
        }
        lav_audio.setOnClickListener {
            practice?.let { pra ->
                val question = pra.questionList[pra.qIndex]
                audioStart(question)
            }
        }
    }

    override fun initData() {
        super.initData()
        practice?.let { pra ->
            // 英译汉4,汉译英5 听音选释义:7 选词填空:8
            val question = pra.questionList[pra.qIndex]
            answer.appQuestionKey = question.questionKey
            when (question.examType) {
                //英译汉 字体大小32   汉译英 字体大小20
                "4" -> {
                    tv_en_l.visibility = View.VISIBLE
                    tv_en.visibility = View.VISIBLE
                    tv_en.textSize = 32f
                    tv_en.text = Html.fromHtml(question.stem)
                }
                "5" -> {
                    tv_en_l.visibility = View.VISIBLE
                    tv_en.visibility = View.VISIBLE
                    tv_en.textSize = 20f
                    tv_en.text = Html.fromHtml(question.stem)
                }
                "7" -> {
                    lav_audio.visibility = View.VISIBLE
                    audioStart(question)
                }
                "8" -> {
                    scroll.visibility = View.VISIBLE
                    v_shadow.visibility = View.VISIBLE
                    tv_title.text = Html.fromHtml(question.stem)
                }
            }
            val list = mutableListOf<QuestionInfoItem>()
            list.addAll(question.item)
            val item = QuestionInfoItem()
            item.type = "1"
            list.add(item)
            adapter.setData(list)
        }
    }

    private fun updateAdapter(item: QuestionInfoItem) {
        adapter.type = "4"
        val datas = adapter.getData()
        kotlin.run breaking@{
            datas.forEach {
                if (item.order == it.order && "0" == it.isAnswer) {
                    it.isAnswer = "2"
                    return@breaking
                }
            }
        }
        adapter.notifyDataSetChanged()
        answer.isRight = if (item.isAnswer == "1") "1" else "0"
        answer.questionAnswer = item.order
        activity.addAnswer(answer)
        Handler().postDelayed({
            if (item.isAnswer == "1") {
                activity.toNext()
            } else {
                //往fragmentList 添加当前单词的题
                practice?.let {
                    activity.addFragment(it)
                    val question = it.questionList[it.qIndex]
                    Glide.with(this.requireActivity()).load(question.imgUrl).into(img_word)
                }
                img_word.visibility = View.VISIBLE
                tv_next.visibility = View.VISIBLE
            }
        }, 1000)
    }

    private fun audioStart(ques: QuestionP) {
        MediaPlayerUtlis.start(this.requireContext(), ques.resourceKey, "", {
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