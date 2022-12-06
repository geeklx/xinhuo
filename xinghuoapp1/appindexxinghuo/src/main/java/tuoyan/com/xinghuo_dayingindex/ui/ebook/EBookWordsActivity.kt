package tuoyan.com.xinghuo_dayingindex.ui.ebook
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_ebook_words.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.AnswerItem
import tuoyan.com.xinghuo_dayingindex.bean.AnswerQuestion
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSubmit
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.EBookPracticeJumpDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.EBookWordFragment
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.EBookWordListActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerAdapter
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailPresenter

class EBookWordsActivity : EBookLifeActivity<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_words

    private val fragmentList by lazy { mutableListOf<Fragment>() }
    private var currentPos = 0

    val questionList by lazy { mutableListOf<QuestionInfo>() }
    private var dDialog: DDialog? = null

    override fun configView() {
        super.configView()
        EBookPracticeJumpDialog(this).setFTitle(bookParam?.catalogName ?: "").setSTitle(bookParam?.name ?: "").setType(1).show()
        vp_word.offscreenPageLimit = 100
    }

    override fun initData() {
        super.initData()
        fragmentList.clear()
        presenter.getRefineQuestion(bookParam?.wordKey ?: "", bookParam?.userpractisekey ?: "") { paper ->
            questionList.clear()
            questionList.addAll(paper.qestionList)
            paper.qestionList.forEachIndexed { index, info ->
                fragmentList.add(EBookWordFragment.newInstance(info))
                if (info.questionKey == paper.lastQuestionKey) {
                    Handler().postDelayed({
                        vp_word.currentItem = index
                    }, 1000)
                }
            }
            vp_word.adapter = ExercisePagerAdapter(supportFragmentManager, fragmentList)
            Handler().postDelayed({
                setMusicStart(currentPos)
            }, 2500)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        vp_word.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var flag = false
            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_DRAGGING -> flag = true
                    ViewPager.SCROLL_STATE_SETTLING -> flag = false
                    ViewPager.SCROLL_STATE_IDLE -> {
                        if (flag && currentPos == fragmentList.size - 1) {
                            submit("1")
                        }
                    }
                }
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (currentPos < position) {
                    setMusicStart(position)
                }
                currentPos = position
            }
        })
        tv_practice.setOnClickListener {
            startActivity<EBookListenActivity>(EBOOK_PARAM to bookParam)
            super.onBackPressed()
        }
    }

    fun setMusicPause(pos: Int) {
        when (val fragment = fragmentList[pos]) {
            is EBookWordFragment -> {
                fragment.pauseMusic()
            }
        }
    }

    fun setMusicStart(pos: Int) {
        when (val fragment = fragmentList[pos]) {
            is EBookWordFragment -> {
                fragment.startMusic()
                fragment.setIndex(pos + 1, fragmentList.size)
            }
        }
    }

    fun scrollPos() {
        if (currentPos + 1 < fragmentList.size) {
            vp_word.currentItem = currentPos + 1
        } else {
            currentPos = fragmentList.size - 1
            submit("1")
        }
    }

    companion object {
        val DATA = "DATA"
    }

    /**
     * 提交试卷
     */
    fun submit(type: String) {
        val answerList = mutableListOf<AnswerQuestion>()
        questionList.forEach { info ->
            val answer = AnswerQuestion()
            answer.questionKey = info.questionKey
            answer.type = info.questionType
            answer.sort = info.questionSort
            val answers = mutableListOf<AnswerItem>()
            val answerItem = AnswerItem("1", info.useranswer, "")
            answers.add(answerItem)
            answer.answers.clear()
            answer.answers.addAll(answers)
            answerList.add(answer)
        }
        val submit = AnswerSubmit()
        submit.answerlist = answerList as ArrayList<AnswerQuestion>
        submit.time = "0"
        submit.catalogkey = bookParam?.catalogKey ?: ""
        submit.source = "10"
        submit.practicekey = bookParam?.bookKey ?: ""
        submit.paperkey = bookParam?.wordKey ?: ""
        submit.submitType = type
        if ("0" == type) {
            submit.lastQuestionKey = answerList[currentPos].questionKey ?: ""
        }
        submit.userPractiseKey = bookParam?.userpractisekey ?: ""
        presenter.submitType(submit) {
            if ("1" == type) {
                startActivity<EBookWordListActivity>(
                    EBOOK_PARAM to bookParam,
                    EBookWordListActivity.ANSWER_LIST to questionList
                )
            }
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        dDialog = DDialog(this).setMessage("你正在进行精练，是否确定退出？")
            .setNegativeButton("确定") {
                dDialog?.dismiss()
                submit("0")
            }.setPositiveButton("取消") {
                dDialog?.dismiss()
            }
        dDialog?.show()
    }
}