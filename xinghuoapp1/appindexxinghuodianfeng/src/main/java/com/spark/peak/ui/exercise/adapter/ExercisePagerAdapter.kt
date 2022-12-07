package com.spark.peak.ui.exercise.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.spark.peak.base.BaseFragment
import com.spark.peak.utlis.FragmentPagerAdapter

class ExercisePagerAdapter(val fm : FragmentManager, var data : List<BaseFragment>) : FragmentPagerAdapter(fm){
    override fun getItem(position: Int): Fragment = data[position]

    override fun getCount(): Int = data.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var fragment = super.instantiateItem(container, position) as Fragment

        return fragment
    }
}