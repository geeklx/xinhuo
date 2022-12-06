package tuoyan.com.xinghuo_dayingindex.base

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LifeFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    var fragmentList = mutableListOf<Fragment>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun createFragment(pos: Int): Fragment {
        return fragmentList[pos]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}