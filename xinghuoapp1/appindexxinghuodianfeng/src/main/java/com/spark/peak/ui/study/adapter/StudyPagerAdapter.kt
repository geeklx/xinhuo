package com.spark.peak.ui.study.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.spark.peak.utlis.FragmentPagerAdapter

/**
 * Created by 李昊 on 2018/4/20.
 */
class StudyPagerAdapter(private var dataList : ArrayList<Fragment>, fm : FragmentManager) : FragmentPagerAdapter(fm){

    var titles = arrayOf("书架","网课")
    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
    override fun getItem(position: Int): Fragment = dataList[position]

    override fun getCount(): Int = dataList.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var fragment = super.instantiateItem(container, position) as Fragment

        return fragment
    }
}