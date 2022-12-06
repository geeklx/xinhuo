package tuoyan.com.xinghuo_dayingindex.ui.books.report

import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_recommed_news.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.BaseV4FmPagerAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity

class RecommedNewsActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_recommed_news

    override val title: String by lazy { intent.getStringExtra(TITLE) ?: "" }

    //从测评报告过来 clssifyKey evalRecommendKey
    private val clssifyKey: String by lazy { intent.getStringExtra(CLSSIFY_KEY) ?: "" }
    private val evalRecommendKey: String by lazy { intent.getStringExtra(EVAL_RECOMMEND_KEY) ?: "" }

    //从首页 精选 四六级 考研 专四专八 过来gradeKey 0 1 2 3
    private val gradeKey: String by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }

    private val viewPageAdapter: BaseV4FmPagerAdapter by lazy {
        BaseV4FmPagerAdapter(
            supportFragmentManager
        )
    }

    // 备考干货 ，gradeKey调整为1四六级,1.1四级,1.2六级,2考研,3专四专八,3.1专四,3.2专八
    private val fragmentCET4 by lazy { RecommedNewsFragment.newInstance("1.1") }
    private val fragmentCET6 by lazy { RecommedNewsFragment.newInstance("1.2") }
    private val fragmentYan by lazy { RecommedNewsFragment.newInstance("2") }
    private val fragmentTEM4 by lazy { RecommedNewsFragment.newInstance("3.1") }
    private val fragmentTEM8 by lazy { RecommedNewsFragment.newInstance("3.2") }
    private val fragmentNews by lazy {
        RecommedNewsFragment.newInstance(
            clssifyKey,
            evalRecommendKey
        )
    }

    override fun configView() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        tv_title.text = title

        val fragmentList = mutableListOf<Fragment>()
        if (gradeKey.isNullOrEmpty()) {
            fragmentList.add(fragmentNews)
        } else {
            when {
                CET4AndCET6 == gradeKey -> {
                    rg_book.visibility = View.VISIBLE
                    d_line.visibility = View.VISIBLE
                    rb_one.text = "四级"
                    rb_two.text = "六级"
                    rb_three.visibility = View.GONE
                    rb_four.visibility = View.GONE
                    rb_five.visibility = View.GONE
                    fragmentList.add(fragmentCET4)
                    fragmentList.add(fragmentCET6)
                }
                YAN == gradeKey -> {
                    fragmentList.add(fragmentYan)
                }
                TEM4AndTEM6 == gradeKey -> {
                    rg_book.visibility = View.VISIBLE
                    d_line.visibility = View.VISIBLE
                    rb_one.text = "专四"
                    rb_two.text = "专八"
                    rb_three.visibility = View.GONE
                    rb_four.visibility = View.GONE
                    rb_five.visibility = View.GONE
                    fragmentList.add(fragmentTEM4)
                    fragmentList.add(fragmentTEM8)
                }
                else -> {
                    rg_book.visibility = View.VISIBLE
                    d_line.visibility = View.VISIBLE
                    rb_one.text = "四级"
                    rb_two.text = "六级"
                    rb_three.text = "考研"
                    rb_four.text = "专四"
                    rb_five.text = "专八"
                    fragmentList.add(fragmentCET4)
                    fragmentList.add(fragmentCET6)
                    fragmentList.add(fragmentYan)
                    fragmentList.add(fragmentTEM4)
                    fragmentList.add(fragmentTEM8)
                }
            }
        }
        viewPageAdapter.fragmentList = fragmentList
        view_pager.adapter = viewPageAdapter
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val rb = rg_book.getChildAt(position) as RadioButton
                if (!rb.isChecked) rb.isChecked = true
            }

        })
        rg_book.setOnCheckedChangeListener { radioGroup, i ->
            view_pager.currentItem = radioGroup.indexOfChild(radioGroup.findViewById(i))
        }
    }

    companion object {
        val TITLE = "title"
        val CLSSIFY_KEY = "clssifyKey"
        val EVAL_RECOMMEND_KEY = "evalRecommendKey"
        val GRADE_KEY = "grade_key"
        val CET4AndCET6 = "1"
        val YAN = "2"
        val TEM4AndTEM6 = "3"
    }

}


