package tuoyan.com.xinghuo_dayingindex.ui.ebook

import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_ebook_exercise_analysis.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.EBookPracticeJumpDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.MaterialQesAnalysisFragment
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerAdapter
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailPresenter
import java.util.*

//practise/questionAnalyze， get ，参数questionkey 、questiontype=11
class EBookListenActivity : EBookLifeActivity<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_exercise_analysis

    private val fragmentList by lazy { mutableListOf<Fragment>() }
    private var currentIndex = 0//当前vp 的为止
    val answerList by lazy { mutableListOf<AnswerQuestion>() }
    private var dDialog: DDialog? = null

    override fun configView() {
        super.configView()
        tv_title.text = "听力练习"
        EBookPracticeJumpDialog(this).setFTitle(bookParam?.catalogName ?: "").setSTitle(bookParam?.name ?: "").setType(3).show()
    }

    override fun initData() {
        super.initData()
        fragmentList.clear()
        presenter.getRefineListeningQuestion(bookParam?.resourceKey ?: "", bookParam?.userpractisekey ?: "", bookParam?.matpKey ?: "", "11") { info ->
            val exerciseFrameItem = ExerciseFrameItem()
            val gson = GsonBuilder().disableHtmlEscaping().create()
            exerciseFrameItem.questionInfo = info
            val instance = MaterialQesAnalysisFragment.newInstance("2", exerciseFrameItem)
            instance.qIndex = 0
            fragmentList.add(instance)
            vp_exercise.offscreenPageLimit = 10
            vp_exercise.adapter = ExercisePagerAdapter(supportFragmentManager, fragmentList)
            val list = info.item as? ArrayList<*>
            list?.forEachIndexed { index, infoItem ->
                val questionInfo = gson.fromJson(gson.toJson(infoItem), QuestionInfo::class.java)//any 转为 questionInfo类型
                val answerQuestionItem = AnswerQuestion()
                answerQuestionItem.sort = questionInfo.sort ?: ""
                answerQuestionItem.type = questionInfo.questionType ?: ""
                answerQuestionItem.questionKey = questionInfo.questionKey
                answerList.add(answerQuestionItem)
                if (info.lastQuestionKey == questionInfo.questionKey) {
                    Handler().postDelayed({
                        instance.scrollPos(index)
                    }, 1000)
                }
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        vp_exercise.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(pos: Int) {
                setMusicPause(currentIndex)
                currentIndex = pos
            }

            override fun onPageScrollStateChanged(p0: Int) {
            }

        })
    }

    fun setMusicPause(pos: Int) {
        when (val fragment = fragmentList[pos]) {
            is MaterialQesAnalysisFragment -> {
                fragment.pauseMusic()
            }
        }
    }

    override fun onBackPressed() {
        checkAnswers(answerList) {
            submit()
        }
    }

    private fun checkAnswers(answerList: List<AnswerQuestion>, action: () -> Unit) {
        var allDown = true
        kotlin.run breaking@{
            answerList.forEach {
                if (!it.haveDone) {
                    allDown = false
                    return@breaking
                }
            }
        }
        when (allDown) {
            true -> {
                action()
            }
            else -> {
                dDialog = DDialog(this).setMessage("你正在进行精练，是否确定退出？")
                    .setNegativeButton("确定") {
                        dDialog?.dismiss()
                        action()
                    }.setPositiveButton("取消") {
                        dDialog?.dismiss()
                    }
                dDialog?.show()
            }
        }
    }

    /**
     * 提交试卷
     */
    fun submit() {
        when (val fragment = fragmentList[currentIndex]) {
            is MaterialQesAnalysisFragment -> {
                val submit = AnswerSubmit()
                submit.answerlist = answerList as ArrayList<AnswerQuestion>
                submit.time = "0"
                submit.catalogkey = bookParam?.catalogKey ?: ""
                submit.source = "11"
                submit.practicekey = bookParam?.bookKey ?: ""
                submit.paperkey = bookParam?.matpKey ?: ""
                submit.submitType = if (currentIndex == answerList.size - 1) "1" else "0"
                submit.lastQuestionKey = answerList[fragment.qIndex + fragment.answerIndex].questionKey
                submit.userPractiseKey = bookParam?.userpractisekey ?: ""
                presenter.submitType(submit) {
                    super.onBackPressed()
                }
            }
        }
    }
}