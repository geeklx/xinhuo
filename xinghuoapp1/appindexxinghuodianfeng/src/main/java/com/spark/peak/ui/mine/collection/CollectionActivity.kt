package com.spark.peak.ui.mine.collection

import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.mine.order.OrderPresenter
import com.spark.peak.ui.study.book.adapter.BookDetailPagerAdapter
import kotlinx.android.synthetic.main.activity_collectiondf.*

/**
 * 创建者：
 * 时间：
 */
class CollectionActivity(override val layoutResId: Int = R.layout.activity_collectiondf) : LifeActivity<OrderPresenter>() {
    override val presenter by lazy { OrderPresenter(this) }
    private val fragmentList by lazy { arrayListOf<Fragment>() }
    private val fragmentPagerAdapter by lazy { BookDetailPagerAdapter(fragmentList, supportFragmentManager) }
    private val lessonFragment by lazy { LessonFragment.newInstance() }
    private val audioFragment by lazy { AudioFragment.newInstance() }
    override fun configView() {
        fragmentList.clear()
        fragmentList.add(lessonFragment)
        fragmentList.add(audioFragment)
        view_pager.adapter = fragmentPagerAdapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        rg_collect.setOnCheckedChangeListener { group, checkedId ->
            view_pager.currentItem = group.indexOfChild(group.findViewById(checkedId))
        }
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                (rg_collect.getChildAt(position) as RadioButton).isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun initData() {
    }
}