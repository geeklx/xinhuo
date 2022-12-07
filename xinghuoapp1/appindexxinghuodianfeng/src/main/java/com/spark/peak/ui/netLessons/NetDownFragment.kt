package com.spark.peak.ui.netLessons

import android.os.Bundle
import com.spark.peak.bean.MyBookResource
import com.spark.peak.ui.study.book.BookResourceFragment

/**
 */
class NetDownFragment : BookResourceFragment() {
    companion object {
        fun instance(list: ArrayList<MyBookResource>,
                     count: String,
                     type: String,
                     name: String,
                     size: String,
                     isOwn: String,
                     enable: Boolean = false) = NetDownFragment().apply {
            arguments = Bundle().apply {
                putSerializable(DATA, list)
                putString(COUNT, count)
                putString(TYPE, type)
                putString(NAME, name)
                putString(SIZE, size)
                putString(IS_OWN, isOwn)
                putSerializable(ENABLE, enable)
            }
        }

        const val DATA = "data"
        const val COUNT = "count"
        const val SIZE = "size"
        private const val TYPE = "type"
        private const val NAME = "name"
        private const val IS_OWN = "isown"
        private const val ENABLE = "enable"

    }
}