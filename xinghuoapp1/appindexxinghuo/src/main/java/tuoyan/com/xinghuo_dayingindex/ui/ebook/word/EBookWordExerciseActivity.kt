package tuoyan.com.xinghuo_dayingindex.ui.ebook.word
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_ebook_word_exercise.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.base.LifeActivityStateAdapter
import tuoyan.com.xinghuo_dayingindex.bean.AnswerQuestion
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSubmit
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.EBookAnswerCardFragment
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.fragment.EBookWordExerciseFragment
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailPresenter

class EBookWordExerciseActivity : EBookLifeActivity<ExerciseDetailPresenter>() {
    override val layoutResId: Int
        get() = R.layout.activity_ebook_word_exercise
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    private val answerCardFragment by lazy { EBookAnswerCardFragment.newInstance() }
    private var dDialog: DDialog? = null

    private val fragmentList by lazy { mutableListOf<Fragment>() }
    private var currentIndex = 0//当前vp 的位置

    private val pagerAdapter by lazy { LifeActivityStateAdapter(this) }

    var paperKey = ""
    var isDone = false

    /**
     * 构造显示题目及答题卡的list
     * 普通做题模块下调用
     * 答题卡页面调用
     */
    var answerList = mutableListOf<AnswerQuestion>()

    override fun configView() {
        super.configView()
        initAnswerCard()
        vp_exercise.offscreenPageLimit = 100
        vp_exercise.adapter = pagerAdapter
    }

    override fun initData() {
        super.initData()
        fragmentList.clear()
        answerList.clear()
        presenter.paperQuestions(bookParam?.catalogKey ?: "") { paper ->
            this.paperKey = paper.paperKey
            bookParam?.let {
                it.resourceKey = paper.paperKey
            }
            var index = 0
            paper.questionlist?.let { list ->
                list.forEach { question ->
                    question.questionlist?.let { infos ->
                        infos.forEach { frame ->
                            frame.questionInfo?.let { info ->
                                val fragment = EBookWordExerciseFragment.newInstance(info)
                                fragmentList.add(fragment)
                                val answerQuestion = AnswerQuestion()
                                answerQuestion.sort = info.sort
                                answerQuestion.type = info.questionType
                                answerQuestion.questionKey = info.questionKey
                                answerQuestion.qPosition = index++
                                answerList.add(answerQuestion)
                            }
                        }
                    }
                }
                pagerAdapter.fragmentList = fragmentList
                pb_class.max = fragmentList.size
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        img_card.setOnClickListener {
            if (View.VISIBLE == fl_answer_card.visibility) {
                tv_title.text = "章节测试"
                fl_answer_card.visibility = View.GONE
                img_card.visibility = View.VISIBLE
            } else {
                tv_title.text = "答题卡"
                img_card.visibility = View.GONE
                fl_answer_card.visibility = View.VISIBLE
                answerCardFragment.showAnswerCard()
            }
        }
        vp_exercise.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var flag = false
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                (fragmentList[currentIndex] as EBookWordExerciseFragment).audioPause()
                currentIndex = position
                pb_class.progress = position + 1
                tv_num.text = Html.fromHtml("<font color='#008aff'>${currentIndex + 1}</font>/${fragmentList.size}")
                (fragmentList[position] as EBookWordExerciseFragment).audioStart()
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> flag = true
                    ViewPager2.SCROLL_STATE_SETTLING -> flag = false
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        if (flag && vp_exercise.currentItem == fragmentList.size - 1) {//最后一页继续滑动，弹出答题卡
                            img_card.performClick()
                        }
                    }
                }
            }
        })
    }

    private fun initAnswerCard() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_answer_card, answerCardFragment)
        transaction.show(answerCardFragment).commit()
    }

    override fun onBackPressed() {
        when (View.VISIBLE) {
            fl_answer_card.visibility -> {
                fl_answer_card.visibility = View.GONE
                img_card.visibility = View.VISIBLE
                tv_title.text = "章节测试"
            }
            else -> {
                dDialog = DDialog(this).setMessage("你正在进行章节测试，是否确定退出？")
                    .setNegativeButton("确定") {
                        dDialog?.dismiss()
                        super.onBackPressed()
                    }.setPositiveButton("取消") {
                        dDialog?.dismiss()
                    }
                dDialog?.show()
            }
        }
    }

    fun scrollPos(fPos: Int) {
        img_card.performClick()
        vp_exercise.currentItem = fPos
    }

    /**
     * 非最后一题，作答后 跳转下一题
     */
    fun goNext() {
        if (vp_exercise.currentItem == fragmentList.size - 1) {
            //是最后一题，则显示答题卡，非最后一题，则跳转到下一题
            img_card.performClick()
        } else {
            vp_exercise.currentItem = currentIndex + 1
        }
    }

    fun submit() {
        if (!isDone) {
            Toast.makeText(this, "不能交白卷喔~", Toast.LENGTH_LONG).show()
            return
        }
        checkAnswers(answerList) {
            val submit = AnswerSubmit()
            submit.answerlist = answerList as ArrayList<AnswerQuestion>
            submit.catalogkey = bookParam?.catalogKey ?: ""
            submit.source = "12"
            submit.paperkey = paperKey
            submit.practicekey = bookParam?.bookKey ?: ""
            presenter.submitType(submit) {
                startActivity<EBookReportActivity>(EBookReportActivity.DATA to it, EBOOK_PARAM to bookParam, EBookReportActivity.TYPE to "8")
                super.onBackPressed()
            }
        }
    }

    private fun checkAnswers(answerList: List<AnswerQuestion>, action: () -> Unit) {
        var noDownNum = 0
        answerList.forEach {
            if (!it.haveDone) {
                noDownNum++
            }
        }
        when (noDownNum) {
            0 -> {
                action()
            }
            else -> {
                dDialog = DDialog(this).setMessage("你还有 <font color='#FF7800'><b>${noDownNum}</b></font> 道题没有答，是否提交？")
                    .setNegativeButton("提交答案") {
                        dDialog?.dismiss()
                        action()
                    }.setPositiveButton("继续作答") {
                        dDialog?.dismiss()
                    }
                dDialog?.show()
            }
        }
    }
}