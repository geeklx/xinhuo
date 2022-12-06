package tuoyan.com.xinghuo_dayingindex.ui.main.list

import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_lesson_rec_list.*
import tuoyan.com.xinghuo_dayingindex.*
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.GradeMenu
import tuoyan.com.xinghuo_dayingindex.ui.books.adapter.BooksPagerAdapter

//首页金刚区推荐课程
class LessonRecListActivity : LifeActivity<BasePresenter>() {

    private val mAdapter by lazy { BooksPagerAdapter(supportFragmentManager) }
    private val dataList by lazy { mutableListOf<Fragment>() }

    private val key by lazy { intent.getStringExtra(KEY) ?: "" }//精选好课key
    private val mTitle by lazy { intent.getStringExtra(TITLE) ?: "" }//标题
    private val grade by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }//精选：0；四六级 ：1；考研：2；专四专八：3；

    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_lesson_rec_list

    override fun configView() {
        super.configView()
        tv_title.text = mTitle
        vp_lesson.adapter = mAdapter
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        rg_lesson.setOnCheckedChangeListener { radioGroup, checkedId ->
            vp_lesson.currentItem = radioGroup.indexOfChild(radioGroup.findViewById(checkedId))
        }
        vp_lesson.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val rb = rg_lesson.getChildAt(position) as RadioButton
                if (!rb.isChecked) rb.isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    override fun initData() {
        super.initData()
        val gradeList = mutableListOf<GradeMenu>()
        dataList.clear()
        when (grade) {
            "0" -> {
                val item1 = GradeMenu()
                item1.name = "四六级"
                item1.key = "1"
                val item2 = GradeMenu()
                item2.name = "考研"
                item2.key = "2"
                val item3 = GradeMenu()
                item3.name = "专四专八"
                item3.key = "3"
                gradeList.add(item1)
                gradeList.add(item2)
                gradeList.add(item3)
            }
            "1" -> {
                val item1 = GradeMenu()
                item1.name = "四级"
                item1.key = GRAD_KEY_CET4
                val item2 = GradeMenu()
                item2.name = "六级"
                item2.key = GRAD_KEY_CET6
                gradeList.add(item1)
                gradeList.add(item2)
            }
            "2" -> {
                val item1 = GradeMenu()
                item1.name = "考研"
                item1.key = GRAD_KEY_YAN
                gradeList.add(item1)
            }
            "3" -> {
                val item1 = GradeMenu()
                item1.name = "专四"
                item1.key = GRAD_KEY_TEM4
                val item2 = GradeMenu()
                item2.name = "专八"
                item2.key = GRAD_KEY_TEM8
                gradeList.add(item1)
                gradeList.add(item2)
            }
        }
        gradeList.forEachIndexed { index, gradeMenu ->
            val rb = RadioButton(this)
            val params = RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.MATCH_PARENT)
            params.weight = 1f
            rb.buttonDrawable = null
            rb.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.bg_rb_fr)
            rb.text = gradeMenu.name
            rb.setTextColor(ContextCompat.getColorStateList(this, R.color.color_book_rb))
            rb.textSize = 14f
            rb.layoutParams = params
            rb.gravity = Gravity.CENTER
            rg_lesson.addView(rb)
            dataList.add(LessonListFragment.newInstance(key, gradeMenu.key))
        }
        (rg_lesson.getChildAt(0) as RadioButton).isChecked = true
        if (gradeList.size == 1) {
            rg_lesson.visibility = View.GONE
            v_line.visibility = View.GONE
        }
        mAdapter.dataList = dataList
        mAdapter.notifyDataSetChanged()
    }

    companion object {
        const val KEY = "key"
        const val GRADE_KEY = "grade_key"
        const val TITLE = "title"
    }
}