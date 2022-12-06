package tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_choice_qes_analysis.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui.ebook.*
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.ChoiceAdapter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import java.util.regex.Pattern

private const val TYPE = "type"
private const val QUESTION_INFO = "QuestionInfo"

/**
 * 材料单选题
 */
class ChoiceQesAnalysisFragment : LifeV4Fragment<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_choice_qes_analysis

    //type 1:点读书模考解析；2:点读书精练 听力练习做题状态；3：精练听力练习 解析状态 4:简听力  基础篇（练习）
    // 5:简听力 进阶篇 特训篇（试卷） 6:简听力  基础篇（练习） 解析状态 7：简听力 进阶篇 特训篇（试卷）解析状态
    private val type by lazy { arguments?.getString(TYPE) ?: "" }
    private val questionInfo: QuestionInfo? by lazy { arguments?.getSerializable(QUESTION_INFO) as? QuestionInfo }
    private val parentActivity by lazy { requireActivity() }
    private val parentFragment by lazy { getParentFragment() as MaterialQesAnalysisFragment }
    private val adapter by lazy {
        ChoiceAdapter(if ("2" == type || "4" == type || "5" == type) "1" else "2") { item ->
            when (type) {
                "2" -> {
                    questionInfo?.let { info ->
                        if (info.analyze.contains("您选择的答案：")) {
                            tv_analysis.text = Html.fromHtml(info.analyze.replace("您选择的答案：", "您选择的答案：${item.order}"))
                        } else {
                            tv_analysis.text = Html.fromHtml("<p>您选择的答案：${item.order}</p><p>正确答案：${info.rightanswer}</p>${info.analyze}")
                        }
                    }
                    updateAdapter(item)
                }
                "4" -> {
                    questionInfo?.let { info ->
                        if (info.analyze.contains("您选择的答案：")) {
                            tv_analysis.text = Html.fromHtml(info.analyze.replace("您选择的答案：", "您选择的答案：${item.order}"))
                        } else {
                            tv_analysis.text = Html.fromHtml("<p>您选择的答案：${item.order}</p><p>正确答案：${info.rightanswer}</p>${info.analyze}")
                        }
                    }
                    updateAdapter(item)
                }
                "5" -> {
                    initAnswer(item.order)
                    parentFragment.goNext()
                }
            }
        }
    }

    companion object {
        /**
         * type 1:点读书模考解析；2:点读书精练 听力练习做题状态；3：精练听力练习 解析状态 4:简听力  基础篇（练习）
         * 5:简听力 进阶篇 特训篇（试卷） 6:简听力  基础篇（练习） 解析状态 7：简听力 进阶篇 特训篇（试卷）解析状态
         */
        @JvmStatic
        fun newInstance(type: String, questionInfo: QuestionInfo) = ChoiceQesAnalysisFragment().apply {
            arguments = Bundle().apply {
                putString(TYPE, type)
                putSerializable(QUESTION_INFO, questionInfo)
            }
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_qes.isNestedScrollingEnabled = false
        rlv_qes.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_qes.adapter = adapter
    }

    override fun initData() {
        super.initData()
        questionInfo?.let { info ->
            val gson = GsonBuilder().disableHtmlEscaping().create()
            val infoList = gson.fromJson<List<QuestionInfoItem>>(gson.toJson(info.item), object : TypeToken<List<QuestionInfoItem>>() {}.type)//any 转为 questionInfo类型
            adapter.setData(infoList)
            //适合做题直接出答案的类型
            if (info.useranswer.isNotEmpty() && type != "1" && type != "7") {
                val qItem = QuestionInfoItem()
                qItem.order = info.useranswer
                updateAdapter(qItem)
            } else {
                //解析状态非直接做题出结论的
                initVisible(info, type)
            }
            tv_answer_time.text = Html.fromHtml("${info.answerTime}<small>秒</small>")
            tv_answer_average.text = Html.fromHtml("${info.avgTime}<small>秒</small>")
            try {
                tv_answer_rate.text = Html.fromHtml("${(info.avgAccuracy.toFloat() * 100).toInt()}<small>%</small>")
            } catch (e: Exception) {
            }
            //简听力进阶篇  正确率拼接在info.analyze上，展示听前预测
            if ("7" == type) {
                try {
                    val avg = (info.avgAccuracy.toFloat() * 100).toInt()
                    tv_analysis.text = Html.fromHtml("<p><font color='#62CA00'>全站正确率：${avg}%</font></p>${info.analyze}")
                } catch (e: Exception) {
                    tv_analysis.text = Html.fromHtml(info.analyze)
                }
                tv_pre_listen.text = Html.fromHtml(info.preAuditoryPrediction)
            } else {
                tv_analysis.text = Html.fromHtml(info.analyze)
            }
            if ("4" == type && !info.stem.isNullOrEmpty()) {
                tv_content.visibility = View.VISIBLE
                configViewRead(info.stem)
            }
        }
    }

    fun setLrc(lrcStr: String) {
        if (tv_lrc.text.isNullOrEmpty()) {
            tv_lrc.text = Html.fromHtml(lrcStr)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        ctl_video.setOnClickListener {
            //视频解析
            val mActivity = activity
            if (mActivity is EBookListenActivity) {
                parentFragment.itemData?.questionInfo?.anylizeVideoKey?.let { key ->
                    val videoParam = VideoParam()
                    videoParam.key = key
                    startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam, EBookLifeActivity.EBOOK_PARAM to mActivity.bookParam)
                }
            } else if (mActivity is EBookExerciseAnalysisActivity) {
                parentFragment.itemData?.questionInfo?.anylizeVideoKey?.let { key ->
                    val videoParam = VideoParam()
                    videoParam.key = key
                    startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam, EBookLifeActivity.EBOOK_PARAM to mActivity.bookParam)
                }
            }
        }
        ctl_audio.setOnClickListener {
            val mActivity = activity
            if (mActivity is EBookListenActivity) {
                parentFragment.itemData?.questionInfo?.let { info ->
                    val list = mutableListOf<BookRes>()
                    val bookRes = BookRes()
                    bookRes.ebookLrc = info.lrcUrl
                    bookRes.id = info.resourceKey
                    bookRes.name = ""
                    list.add(bookRes)
                    startActivity<EBookAudioActivity>(EBookAudioActivity.AUDIO_DATAS to list, EBookLifeActivity.EBOOK_PARAM to mActivity.bookParam)
                }
            }
        }
        tv_practice.setOnClickListener {
            ctl_audio.performClick()
        }
    }

    private fun initVisible(info: QuestionInfo, mType: String) {
        img1.visibility = if ("1" == mType) View.VISIBLE else View.GONE
        tv1.visibility = if ("1" == mType) View.VISIBLE else View.GONE
        ctl_answer_data.visibility = if ("1" == mType) View.VISIBLE else View.GONE

        img2.visibility = if (("3" == mType || "1" == mType || "6" == mType || "7" == mType) && info.analyze.isNotEmpty()) View.VISIBLE else View.GONE
        tv2.visibility = if (("3" == mType || "1" == mType || "6" == mType || "7" == mType) && info.analyze.isNotEmpty()) View.VISIBLE else View.GONE
        tv_analysis.visibility = if (("3" == mType || "1" == mType || "6" == mType || "7" == mType) && info.analyze.isNotEmpty()) View.VISIBLE else View.GONE

        img21.visibility = if ("7" == mType) View.VISIBLE else View.GONE
        tv21.visibility = if ("7" == mType) View.VISIBLE else View.GONE
        tv_pre_listen.visibility = if ("7" == mType) View.VISIBLE else View.GONE

        ctl_video.visibility = if (("3" == mType || "1" == mType) && parentFragment.itemData?.questionInfo?.anylizeVideoKey?.isNotEmpty() == true) View.VISIBLE else View.GONE
        ctl_audio.visibility = if ("3" == mType) View.VISIBLE else View.GONE

        tv_practice.visibility = if ("2" == mType) View.VISIBLE else View.GONE

        img4.visibility = if ("1" == mType || "6" == mType || "7" == mType) View.VISIBLE else View.GONE
        tv4.visibility = if ("1" == mType || "6" == mType || "7" == mType) View.VISIBLE else View.GONE
        tv_lrc.visibility = if ("1" == mType || "6" == mType || "7" == mType) View.VISIBLE else View.GONE
    }

    private fun updateAdapter(item: QuestionInfoItem) {
        val mActivity = activity
        if (mActivity is EBookListenActivity || mActivity is EBookListenExerciseActivity) {
            val answers = mutableListOf<AnswerItem>()
            val answerItem = AnswerItem("1", item.order, "")
            answers.add(answerItem)
            kotlin.run breaking@{
                if (mActivity is EBookListenActivity) {
                    mActivity.answerList.forEach { qItem ->
                        if (qItem.questionKey == questionInfo?.questionKey!!) {
                            qItem.haveDone = true
                            qItem.answers.clear()
                            qItem.answers.addAll(answers)
                            return@breaking
                        }
                    }
                } else if (mActivity is EBookListenExerciseActivity) {
                    mActivity.isDown = true
                    mActivity.answerList.forEach { qItem ->
                        if (qItem.questionKey == questionInfo?.questionKey!!) {
                            qItem.haveDone = true
                            qItem.answers.clear()
                            qItem.answers.addAll(answers)
                            return@breaking
                        }
                    }
                }
            }
            if ("5" == type) {//type=2，解析状态显示答案的正确；一期模考和简听力的进阶篇->试卷保存状态时只显示用户的选择
                adapter.type = "1"
                adapter.order = questionInfo?.useranswer ?: ""
            } else {
                adapter.type = "2"
                questionInfo?.let { info ->
                    if ("2" == type) {
                        //精练2  对应解析状态3
                        initVisible(info, "3")
                    } else if ("4" == type) {
                        //基础篇（练习） 对应解析状态
                        initVisible(info, "6")
                    }
                }
                val datas = adapter.getData()
                kotlin.run breaking@{
                    datas.forEach {
                        if (item.order == it.order && "0" == it.isAnswer) {
                            it.isAnswer = "2"
                            return@breaking
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun initAnswer(answer: String) {
        when (val ctx = parentActivity) {
            is EBookListenExerciseActivity -> {
                ctx.isDown = true
                val answers = mutableListOf<AnswerItem>()
                val answerItem = AnswerItem("1", answer, "")
                answers.add(answerItem)
                // 答案单选多选问题answerQ.answers=
                // 图片作答answerQ.imgs
                //  answerQ.answerType
                // answerQ.answerText
                kotlin.run breaking@{
                    ctx.answerList.forEach { qItem ->
                        if (qItem.questionKey == questionInfo?.questionKey!!) {
                            qItem.haveDone = true
                            qItem.answers.clear()
                            qItem.answers.addAll(answers)
                            return@breaking
                        }
                    }
                }
            }
        }
    }

    private fun configViewRead(mStem: String) {
        val stem = mStem.replace("<p>", "").replace("</p>", "")
        val matchers = regex(stem)
        if (matchers.isNotEmpty()) {
            refreshSpan(matchers)
        }
    }

    private fun regex(stem: String): List<MatcherBean> {
        val listMatch = mutableListOf<String>()
        val regex = "<h>.*?</h>"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(stem)
        while (matcher.find()) {
            val tempStr = matcher.group()
            val temp = tempStr.replace("<h>", "").replace("</h>", "")
            if (temp.isNotEmpty()) {
                listMatch.add(tempStr)
            }
        }
        val temp = stem.replace("<h>", "").replace("</h>", "")
        var stemTemp = stem
        val matchers = mutableListOf<MatcherBean>()
        listMatch.forEach { regex ->
            val regexTemp = regex.replace("<h>", "").replace("</h>", "")
            val bean = MatcherBean()
            bean.parentStr = temp
            bean.patternStr = regexTemp
            bean.startIndex = stemTemp.indexOf(regex)
            matchers.add(bean)
            stemTemp = stemTemp.replaceFirst(regex, regexTemp)
        }
        return matchers
    }

    private fun refreshSpan(matchers: List<MatcherBean>) {
        val contentSpan = SpannableStringBuilder()
        contentSpan.append(matchers[0].parentStr)
        try {
            matchers.forEach { bean ->
                if (!bean.isClick) {
                    contentSpan.setSpan(object : ClickableSpan() {
                        override fun onClick(view: View) {
                            bean.isClick = true
                            refreshSpan(matchers)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                        }
                    }, bean.startIndex, bean.startIndex + (bean.patternStr?.length ?: 0), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    contentSpan.setSpan(ForegroundColorSpan(Color.parseColor("#D6ECFF")), bean.startIndex, bean.startIndex + (bean.patternStr?.length ?: 0), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    contentSpan.setSpan(BackgroundColorSpan(Color.parseColor("#D6ECFF")), bean.startIndex, bean.startIndex + (bean.patternStr?.length ?: 0), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                } else {
                    contentSpan.setSpan(ForegroundColorSpan(Color.parseColor("#62CA00")), bean.startIndex, bean.startIndex + (bean.patternStr?.length ?: 0), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                }
            }
        } catch (e: Exception) {
        }
        tv_content.text = contentSpan
        tv_content.movementMethod = LinkMovementMethod.getInstance()
    }
}