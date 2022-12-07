package com.spark.peak.ui.study.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spark.peak.base.BaseAdapter

/**
 * Created by 李昊 on 2018/4/23.
 */
abstract class StudyBaseAdapter<T>(var isConfig: Boolean = false, isHeader : Boolean, isFooter : Boolean) : BaseAdapter<T>(isHeader = isHeader, isEmpty = true, isFooter = isFooter) {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0 && (getDateCount() == 0 || getIsHeader())) {
                    return layoutManager.spanCount
                }
                return 1
            }
        }
    }
}