package tuoyan.com.xinghuo_dayingindex.ui.practice.real

import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_real_cet.*
import tuoyan.com.xinghuo_dayingindex.GRAD_KEY_CET4
import tuoyan.com.xinghuo_dayingindex.GRAD_KEY_CET6
import tuoyan.com.xinghuo_dayingindex.GRAD_KEY_YAN
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.PracticePresenter
import tuoyan.com.xinghuo_dayingindex.utlis.FragmentV4PagerAdapter

class RealCETActivity : LifeActivity<PracticePresenter>() {
    override val presenter: PracticePresenter
        get() = PracticePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_real_cet
    private val gradeKey by lazy { intent.getStringExtra(GRAD_KEY) ?: "" }
    private val realCET4Fragment by lazy { RealCETFragment.newInstance(GRAD_KEY_CET4) }
    private val realCET6Fragment by lazy { RealCETFragment.newInstance(GRAD_KEY_CET6) }
    private val realYanFragment by lazy { RealCETFragment.newInstance(GRAD_KEY_YAN) }

    override fun configView() {
        setSupportActionBar(toolbar)
        val fgs = ArrayList<Fragment>()
        if ("1" == gradeKey) {
            rg_real_cet.visibility = View.VISIBLE
            line.visibility = View.VISIBLE
            rb_three.visibility = View.GONE
            fgs.add(realCET4Fragment)
            fgs.add(realCET6Fragment)
        } else if ("2" == gradeKey) {
            rg_real_cet.visibility = View.GONE
            line.visibility = View.GONE
            fgs.add(realYanFragment)
        } else {
            rg_real_cet.visibility = View.VISIBLE
            line.visibility = View.VISIBLE
            fgs.add(realCET4Fragment)
            fgs.add(realCET6Fragment)
            fgs.add(realYanFragment)
        }
        vp_real_cet.adapter = RealPageAdapter(supportFragmentManager, fgs)
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        rg_real_cet.setOnCheckedChangeListener { group, checkedId ->
            vp_real_cet.currentItem = group.indexOfChild(group.findViewById(checkedId))
        }
        vp_real_cet.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val rb = rg_real_cet.getChildAt(position) as RadioButton
                if (!rb.isChecked) rb.isChecked = true
            }
        })
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        if (realCET4Fragment.isAdded) {
            realCET4Fragment.setData()
        }
        if (realCET6Fragment.isAdded) {
            realCET6Fragment.setData()
        }
        if (realYanFragment.isAdded) {
            realYanFragment.setData()
        }
    }

    companion object {
        const val GRAD_KEY = "gradkey"
    }
}

private class RealPageAdapter(fm: FragmentManager, val fgs: ArrayList<Fragment>) : FragmentV4PagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = fgs[position]

    override fun getCount(): Int = fgs.size
}
