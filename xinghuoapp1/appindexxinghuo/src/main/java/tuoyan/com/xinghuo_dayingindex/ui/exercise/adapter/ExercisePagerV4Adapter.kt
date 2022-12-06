package tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import tuoyan.com.xinghuo_dayingindex.utlis.FragmentPagerV4Adapter

class ExercisePagerV4Adapter(val fm: FragmentManager, var data: List<BaseV4Fragment>) : FragmentPagerV4Adapter(fm) {
    override fun getItem(position: Int): Fragment = data[position]

    override fun getCount(): Int = data.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var fragment = super.instantiateItem(container, position) as Fragment

        return fragment
    }
}