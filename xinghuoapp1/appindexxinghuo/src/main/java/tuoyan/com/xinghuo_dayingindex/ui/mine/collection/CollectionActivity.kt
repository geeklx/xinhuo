package tuoyan.com.xinghuo_dayingindex.ui.mine.collection

import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_collection.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivityStateAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeFullActivity

class CollectionActivity : LifeFullActivity<CollectionPresenter>() {
    override val presenter = CollectionPresenter(this)
    override val layoutResId = R.layout.activity_collection
    private val adapter by lazy { LifeActivityStateAdapter(this) }

    override fun configView() {
        view_pager.offscreenPageLimit = 10
        view_pager.adapter = adapter
    }

    override fun initData() {
        val list = mutableListOf<Fragment>()
        list.add(QuestionFragment.newInstance())
        list.add(AudioFragment.newInstance())
        presenter.collectedCount("6") {
            if ((it["count"]?.toInt() ?: 0) > 0) {
                radio.visibility = View.VISIBLE
                list.add(WordsFragment.newInstance())
            }
            adapter.fragmentList = list
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

