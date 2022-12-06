package tuoyan.com.xinghuo_dayingindex.ui.ebook
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Handler
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_ebook_exercise_analysis.*
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.AnswerQuestion
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSubmit
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.EBookAnswerCardFragment
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.MaterialQesAnalysisFragment
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerAdapter
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import java.util.*

/**
 * 简听力进阶篇 特殊篇  做题  每个材料题 一个音频，音频可以拖动
 */
class EBookListenExerciseActivity : EBookLifeActivity<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_exercise_analysis

    private val type by lazy { intent.getStringExtra(TYPE) ?: "4" }
    private val answerCardFragment by lazy { EBookAnswerCardFragment.newInstance() }
    private val fragmentList by lazy { mutableListOf<Fragment>() }
    private var currentIndex = 0//当前vp 的位置

    /**
     * 当前小题的位置 循环+1 赋值 最后为所有小题的数量
     */
    private var current = 0
    var isDown = false

    /**
     * 构造显示题目及答题卡的list
     * 普通做题模块下调用
     * 答题卡页面调用
     */
    var answerList = mutableListOf<AnswerQuestion>()

    private var dDialog: DDialog? = null
//    private var seekToTime = 0L

    override fun configView() {
        super.configView()
        if ("5" == type) {
            //简听力进阶篇（试卷）显示答题卡   基础篇（练习） 做一个出一个答案
            img_card.visibility = View.VISIBLE
            initAnswerCard()
        } else if ("4" == type) {
            layout_guide.visibility = if (SpUtil.isFirstModal) View.VISIBLE else View.GONE
            SpUtil.isFirstModal = false
        }
    }

    override fun initData() {
        super.initData()
        fragmentList.clear()
        presenter.getExerciseParsingFrame(bookParam?.resourceKey ?: "", bookParam?.userpractisekey ?: "") { paper ->
            try {
                paper.questionlist?.let { list -> if (list.isNotEmpty()) initExerciseData(paper.lastQuestionKey ?: "", list) else toast("数据异常~请您稍候再试~") }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        vp_exercise.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var flag = false
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(pos: Int) {
                setMusicPause(currentIndex)
                currentIndex = pos
                initTitle()
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_DRAGGING -> flag = true
                    ViewPager.SCROLL_STATE_SETTLING -> flag = false
                    ViewPager.SCROLL_STATE_IDLE -> {
                        if (flag && vp_exercise.currentItem == fragmentList.size - 1 && "5" == type) {//简听力进阶篇（试卷）最后一页继续滑动，弹出答题卡
                            img_card.performClick()
                        }
                    }
                }
            }
        })
        img_card.setOnClickListener {
            if (View.VISIBLE == fl_answer_card.visibility) {
                initTitle()
                fl_answer_card.visibility = View.GONE
                img_card.visibility = View.VISIBLE
            } else {
                tv_title.text = "答题卡"
                setMusicPause(currentIndex)
                img_card.visibility = View.GONE
                fl_answer_card.visibility = View.VISIBLE
                answerCardFragment.showAnswerCard()
            }
        }
        layout_guide.setOnClickListener {
            layout_guide.visibility = View.GONE
        }
    }

    private fun initExerciseData(lastQuestionKey: String, dataList: List<ExerciseFrameItem>) {
        var pIndex = 0
        for (index in dataList.indices) { // 大题分类的结构 body->questionlist
            val item: ExerciseFrameItem = dataList[index]
            item.questionlist?.forEach { pInfo ->//body->questionlist->questionlist
                if ("11" == pInfo.questionType) {//听力材料题
                    val instance = MaterialQesAnalysisFragment.newInstance(type, pInfo)
                    instance.qIndex = current
                    fragmentList.add(instance)
                    if (pInfo.questionlist != null && pInfo.questionlist!!.isNotEmpty()) {
                        //如果当前题目中存在小题（材料题、多选题，填空题都算做一个题），则解析该题结构
                        pInfo.questionlist!!.forEachIndexed { index, info ->//body->questionlist->questionlist->questionlist
                            ++current
                            val answerQuestionItem = AnswerQuestion()
                            answerQuestionItem.sort = info.questionSort ?: ""
                            answerQuestionItem.type = info.questionType ?: ""
                            answerQuestionItem.questionKey = info.questionKey
                            answerQuestionItem.haveDone = false
                            answerQuestionItem.qPosition = fragmentList.size - 1
                            answerQuestionItem.mPosition = index
                            answerQuestionItem.parentType = pInfo.questionType ?: ""
                            answerList.add(answerQuestionItem)
                            if (info.questionKey == lastQuestionKey) {
                                val scrollPos = pIndex
                                currentIndex = scrollPos
                                Handler().postDelayed({
                                    vp_exercise.currentItem = scrollPos
                                    instance.scrollPos(index)
                                }, 1000)
                            }
                        }
                    }
                    pIndex++
                }
            }
        }
        vp_exercise.offscreenPageLimit = 100
        vp_exercise.adapter = ExercisePagerAdapter(supportFragmentManager, fragmentList)
        initTitle()
    }

    private fun initAnswerCard() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_answer_card, answerCardFragment)
        transaction.show(answerCardFragment).commit()
    }

    override fun onBackPressed() {
        when (View.VISIBLE) {
            layout_guide.visibility -> {
                layout_guide.visibility = View.GONE
            }
            fl_answer_card.visibility -> {
                fl_answer_card.visibility = View.GONE
                img_card.visibility = View.VISIBLE
                initTitle()
            }
            else -> {
                dDialog = DDialog(this).setMessage("你正在进行做题，是否确定退出？")
                    .setNegativeButton("确定") {
                        dDialog?.dismiss()
                        save()
                    }.setPositiveButton("取消") {
                        dDialog?.dismiss()
                    }
                dDialog?.show()
            }
        }
    }

    fun initTitle() {
        when (val fragment = fragmentList[currentIndex]) {
            is MaterialQesAnalysisFragment -> {
                tv_title.text = Html.fromHtml("<font color='#008AFF'>${fragment.qIndex + fragment.answerIndex + 1}</font>/${current}")
            }
        }
    }

    fun scrollPos(fPos: Int, sPos: Int) {
        img_card.performClick()
        vp_exercise.currentItem = fPos
        when (val fragment = fragmentList[fPos]) {
            is MaterialQesAnalysisFragment -> {
                fragment.scrollPos(sPos)
            }
        }
    }

    /**
     * 非最后一题，作答后 跳转下一题
     */
    fun goNext() {
        setMusicPause(currentIndex)
        if (vp_exercise.currentItem == fragmentList.size - 1) {
            //是最后一题，则显示答题卡，非最后一题，则跳转到下一题
            img_card.performClick()
        } else {
            currentIndex += 1
            vp_exercise.currentItem = currentIndex
        }
    }

    /**
     * 提交试卷startActivity<EBookReportActivity>()
     */
    fun submit() {
        if (!isDown) {
            Toast.makeText(this, "不能交白卷喔~", Toast.LENGTH_LONG).show()
            return
        }
        checkAnswers(answerList) {
            val submit = AnswerSubmit()
            submit.answerlist = answerList as ArrayList<AnswerQuestion>
            submit.time = "0"
            submit.catalogkey = bookParam?.catalogKey ?: ""
            if ("5" == type) {//进阶篇 统计正确率
                submit.source = "9"
            }
            submit.practicekey = bookParam?.bookKey ?: ""
            submit.paperkey = bookParam?.resourceKey ?: ""
            submit.userPractiseKey = bookParam?.userpractisekey ?: ""
            submit.submitType = "1"
            presenter.submitType(submit) {
                startActivity<EBookReportActivity>(EBookReportActivity.DATA to it, EBOOK_PARAM to bookParam, EBookReportActivity.TYPE to "7")
                super.onBackPressed()
            }
        }
    }

    /**
     * 退出保存试卷
     */
    fun save() {
        if (!isDown) {
            super.onBackPressed()
        } else {
            when (val fragment = fragmentList[currentIndex]) {
                is MaterialQesAnalysisFragment -> {
                    val submit = AnswerSubmit()
                    submit.answerlist = answerList as ArrayList<AnswerQuestion>
//                    submit.time = "$enterTime"
                    submit.catalogkey = bookParam?.catalogKey ?: ""
                    if ("5" == type) {//进阶篇 统计正确率
                        submit.source = "9"
                    }
                    submit.practicekey = bookParam?.bookKey ?: ""
                    submit.paperkey = bookParam?.resourceKey ?: ""
                    submit.lastQuestionKey = answerList[fragment.qIndex + fragment.answerIndex].questionKey
                    submit.userPractiseKey = bookParam?.userpractisekey ?: ""
                    submit.submitType = "0"
                    presenter.submitType(submit) {
                        super.onBackPressed()
                    }
                }
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

    fun setMusicPause(pos: Int) {
        when (val fragment = fragmentList[pos]) {
            is MaterialQesAnalysisFragment -> {
                fragment.pauseMusic()
            }
        }
    }

    companion object {
        //4:简听力 基础篇（练习） 5：简听力 进阶篇（试卷）
        const val TYPE = "type"
    }
}