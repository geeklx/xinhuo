package tuoyan.com.xinghuo_dayingindex.ui.ebook

import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_ebook_exercise_analysis.*
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.MaterialQesAnalysisFragment
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerAdapter
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailPresenter

class EBookExerciseAnalysisActivity : EBookLifeActivity<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_exercise_analysis

    private val analyzeType by lazy { intent.getStringExtra(ANALYZE_TYPE) ?: "" }
    private val type by lazy { intent.getStringExtra(TYPE) ?: "1" }
    private val fragmentList by lazy { mutableListOf<Fragment>() }
    private var currentIndex = 0//当前vp 的为止

    override fun configView() {
        super.configView()
    }

    override fun initData() {
        super.initData()
        fragmentList.clear()
        bookParam?.let { book ->
            presenter.getExerciseParsingFrame(book.resourceKey ?: "", book.userpractisekey ?: "", isError = analyzeType) { paper ->
                try {
                    paper.questionlist?.let { list -> if (list.isNotEmpty()) initExerciseData(list) else toast("数据异常~请您稍候再试~") }
                } catch (e: Exception) {
                    e.printStackTrace()
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

    private fun initExerciseData(dataList: List<ExerciseFrameItem>) {
        var pIndex = 0
        for (index in dataList.indices) { // 大题分类的结构 body->questionlist
            val item: ExerciseFrameItem = dataList[index]
            item.questionlist?.forEach { pItem ->//body->questionlist->questionlist
                if ("11" == pItem.questionType) {//听力材料题
                    val instance = MaterialQesAnalysisFragment.newInstance(type, pItem)
                    fragmentList.add(instance)
                    kotlin.run breaking@{
                        if (pItem.questionlist != null && pItem.questionlist!!.isNotEmpty() && true == bookParam?.questionKey?.isNotEmpty()) {
                            pItem.questionlist!!.forEachIndexed { index, exerciseFrameItem ->
                                if (true == bookParam?.questionKey?.isNotEmpty() && exerciseFrameItem.questionKey == bookParam?.questionKey) {
                                    val scrollPos = pIndex
                                    Handler().postDelayed({
                                        currentIndex = scrollPos
                                        vp_exercise.currentItem = scrollPos
                                        instance.scrollPos(index)
                                    }, 1000)
                                    return@breaking
                                }
                            }
                        }
                    }
                    pIndex++
                }
            }
        }
        vp_exercise.offscreenPageLimit = 100
        vp_exercise.adapter = ExercisePagerAdapter(supportFragmentManager, fragmentList)
    }

    companion object {
        const val ANALYZE_TYPE = "ANALYZE_TYPE"

        /**
         * 同 EbookListenExerciseActivity MaterialQesAnalysisFragment ChoiceQesAnalysisFragment EBookExerciseAnalysisActivity
         * type 1:点读书模考解析；2:点读书精练 听力练习做题状态；3：精练听力练习 解析状态 4:简听力  基础篇（练习）
         * 5:简听力 进阶篇 特训篇（试卷） 6:简听力  基础篇（练习） 解析状态 7：简听力 进阶篇 特训篇（试卷）解析状态
         */
        const val TYPE = "TYPE"
    }
}