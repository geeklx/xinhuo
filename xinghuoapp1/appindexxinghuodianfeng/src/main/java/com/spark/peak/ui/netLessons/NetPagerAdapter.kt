package com.spark.peak.ui.netLessons

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.spark.peak.base.BaseFragment
import com.spark.peak.utlis.FragmentPagerAdapter


/**
 * Created by  on 2018/5/9
 *
 */
class NetPagerAdapter( var dataList: ArrayList<out BaseFragment>, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = dataList[position]

    override fun getCount(): Int = dataList.size

//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        var fragment = super.instantiateItem(container, position) as Fragment
//        return fragment
//    }
}