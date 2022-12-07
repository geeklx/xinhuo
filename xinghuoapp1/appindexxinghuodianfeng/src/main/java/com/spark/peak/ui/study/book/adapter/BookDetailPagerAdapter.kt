package com.spark.peak.ui.study.book.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.spark.peak.utlis.FragmentPagerAdapter


/**
 * Created by 李昊 on 2018/5/9.
 */
class BookDetailPagerAdapter(private var dataList: ArrayList<Fragment>, val fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = dataList[position]

    override fun getCount(): Int = dataList.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        return fragment
    }

    fun removeAll() {
        val ft = fm.beginTransaction()
        for (i in 0 until dataList.size) {
            ft.remove(dataList[i])
        }
        ft.commit()
        fm.executePendingTransactions()
    }
}