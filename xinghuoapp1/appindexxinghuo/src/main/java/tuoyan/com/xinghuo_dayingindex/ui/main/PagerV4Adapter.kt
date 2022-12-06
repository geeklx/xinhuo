package tuoyan.com.xinghuo_dayingindex.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import tuoyan.com.xinghuo_dayingindex.widegt.FragmentV4StatePagerAdapter

class PagerV4Adapter(fm: FragmentManager?, val fgs: ArrayList<Fragment>) :
    FragmentV4StatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return fgs.get(position)
    }

    override fun getCount(): Int {
        return fgs.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}