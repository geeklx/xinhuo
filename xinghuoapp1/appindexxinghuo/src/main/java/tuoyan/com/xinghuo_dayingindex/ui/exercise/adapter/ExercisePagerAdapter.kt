package tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import tuoyan.com.xinghuo_dayingindex.utlis.FragmentV4PagerAdapter

class ExercisePagerAdapter(val fm: FragmentManager, var data: List<Fragment>) : FragmentV4PagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = data[position]

    override fun getCount(): Int = data.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        return fragment
    }
}