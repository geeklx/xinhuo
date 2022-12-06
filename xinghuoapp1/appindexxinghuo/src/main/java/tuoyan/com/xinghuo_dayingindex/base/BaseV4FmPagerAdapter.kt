package tuoyan.com.xinghuo_dayingindex.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlin.properties.Delegates

/**
 * 使用ViewPager2和LifeFragmentStateAdapter
 */
@Deprecated("")
class BaseV4FmPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    var fragmentList = mutableListOf<Fragment>()
    var titles by Delegates.notNull<Array<String>>()

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}