package tuoyan.com.xinghuo_dayingindex.ui.books.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import tuoyan.com.xinghuo_dayingindex.utlis.FragmentV4PagerAdapter

/**
 * Created by  on 2018/9/14.
 */
class BooksPagerAdapter(fm: FragmentManager) : FragmentV4PagerAdapter(fm) {
    var dataList = mutableListOf<Fragment>()

    val titles = arrayOf("四级", "六级", "考研", "专四", "专八")

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun getItem(position: Int): Fragment = dataList[position]

    override fun getCount(): Int = dataList.size
}