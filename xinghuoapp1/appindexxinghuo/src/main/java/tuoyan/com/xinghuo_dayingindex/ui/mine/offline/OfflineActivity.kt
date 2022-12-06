package tuoyan.com.xinghuo_dayingindex.ui.mine.offline

import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_collection.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseActivity
import tuoyan.com.xinghuo_dayingindex.base.LifeActivityStateAdapter

class OfflineActivity : BaseActivity() {
    override val layoutResId = R.layout.activity_offline
    private val position by lazy { intent.getIntExtra(POSITION, 0) }
    private val adapter by lazy { LifeActivityStateAdapter(this) }

    companion object {
        val POSITION = "position"
    }

    override fun configView() {
        view_pager.offscreenPageLimit = 3
        view_pager.adapter = adapter
    }

    override fun initData() {
        val list = mutableListOf<Fragment>()
        list.add(OLessonFragment())
        list.add(OBookFragment())
        list.add(EBookDownLoadFragment.newInstance())
        adapter.fragmentList = list
        if (position < list.size) {
            view_pager.currentItem = position
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        rg_c.setOnCheckedChangeListener { radioGroup, checkedId ->
            view_pager.currentItem = radioGroup.indexOfChild(radioGroup.findViewById(checkedId))
        }
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val rb = rg_c.getChildAt(position) as RadioButton
                rb.isChecked = true
            }
        })
    }
}
