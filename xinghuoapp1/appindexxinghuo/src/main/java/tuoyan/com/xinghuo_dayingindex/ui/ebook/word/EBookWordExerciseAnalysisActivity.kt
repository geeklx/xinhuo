package tuoyan.com.xinghuo_dayingindex.ui.ebook.word

import android.os.Handler
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_ebook_word_exercise.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.base.LifeActivityStateAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.fragment.EBookWordExerciseAnalysisFragment
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailPresenter

class EBookWordExerciseAnalysisActivity : EBookLifeActivity<ExerciseDetailPresenter>() {
    override val layoutResId: Int
        get() = R.layout.activity_ebook_word_exercise
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)

    private val fragmentList by lazy { mutableListOf<Fragment>() }

    private val pagerAdapter by lazy { LifeActivityStateAdapter(this) }

    private val analyzeType by lazy { intent.getStringExtra(ANALYZE_TYPE) ?: "" }
    private var currentPos = 0

    override fun configView() {
        super.configView()
        tv_title.text = "解析"
        img_card.visibility = View.GONE
        vp_exercise.offscreenPageLimit = 100
        vp_exercise.adapter = pagerAdapter
    }

    override fun initData() {
        super.initData()
        fragmentList.clear()
        bookParam?.let { book ->
            var index = 0
            presenter.getExerciseParsingFrame(book.resourceKey ?: "", book.userpractisekey ?: "", isError = analyzeType) { paper ->
                paper.questionlist?.forEach { fQ ->
                    fQ.questionlist?.forEach { sQ ->
                        sQ.questionInfo?.let { info ->
                            val fragment = EBookWordExerciseAnalysisFragment.newInstance(info)
                            fragmentList.add(fragment)
                            if (!bookParam?.questionKey.isNullOrEmpty() && info.questionKey == bookParam?.questionKey) {
                                currentPos = index
                                Handler().postDelayed({
                                    vp_exercise.currentItem = currentPos
                                }, 1000)
                            }
                            index++
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
        vp_exercise.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pb_class.progress = position + 1
                tv_num.text = Html.fromHtml("<font color='#008aff'>${position + 1}</font>/${fragmentList.size}")
            }
        })
    }

    companion object {
        const val ANALYZE_TYPE = "ANALYZE_TYPE"
    }
}