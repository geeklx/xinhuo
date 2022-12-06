package tuoyan.com.xinghuo_dayingindex.ui.ebook.word.fragment

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_e_book_word_exercise_analysis.*
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.ChoiceAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordExerciseActivity
import tuoyan.com.xinghuo_dayingindex.utlis.MediaPlayerUtlis

private const val QUESTION_INFO = "QuestionInfo"

class EBookWordExerciseAnalysisFragment : LifeV4Fragment<EBookPresenter>() {
    override val layoutResId: Int
        get() = R.layout.fragment_e_book_word_exercise_analysis
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)

    private val info by lazy { arguments?.getSerializable(QUESTION_INFO) as QuestionInfo }
    private val activity by lazy { requireActivity() as EBookWordExerciseActivity }

    private val adapter by lazy {
        ChoiceAdapter("6") {
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(info: QuestionInfo) =
            EBookWordExerciseAnalysisFragment().apply {
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
        //英译汉4,汉译英5 选词填空:8 听音选词:9 场景释义10
        when (info.examType) {
            "4" -> {
                tv_en.visibility = View.VISIBLE
                tv_en.textSize = 32f
                tv_en.text = Html.fromHtml(info.stem ?: "")
            }
            "5" -> {
                tv_en.visibility = View.VISIBLE
                tv_en.textSize = 20f
                tv_en.text = Html.fromHtml(info.stem ?: "")
            }
            "8" -> {
                tv_title.visibility = View.VISIBLE
                tv_title.text = Html.fromHtml(info.stem ?: "")
                tv_title_sec.visibility = View.VISIBLE
                tv_title_sec.text = Html.fromHtml(info.boSource ?: "")
            }
            "9" -> {
                lav_audio.visibility = View.VISIBLE
                tv_title.visibility = View.VISIBLE
                tv_title.text = Html.fromHtml(info.stem ?: "")
                tv_title_sec.visibility = View.VISIBLE
                tv_title_sec.text = Html.fromHtml(info.boSource ?: "")
            }
            "10" -> {
                tv_title.visibility = View.VISIBLE
                tv_title.text = Html.fromHtml(info.stem ?: "")
                tv_title_sec.visibility = View.VISIBLE
                tv_title_sec.text = Html.fromHtml(info.boSource ?: "")
            }
        }
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val infoList = gson.fromJson<List<QuestionInfoItem>>(gson.toJson(info.item), object : TypeToken<List<QuestionInfoItem>>() {}.type)//any 转为 questionInfo类型
        adapter.setData(infoList)
        Glide.with(this.requireContext()).load(info.imgUrl).into(img_word)
    }

    override fun handleEvent() {
        super.handleEvent()
        lav_audio.setOnClickListener {
            audioStart()
        }
    }

    private fun audioStart() {
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