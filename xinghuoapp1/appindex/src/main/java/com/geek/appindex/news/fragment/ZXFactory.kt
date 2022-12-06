package com.geek.appindex.news.fragment

import androidx.fragment.app.Fragment

object ZXFactory {

    const val TAG_RECOMMAND = "com.geek.appindex.news.fragment.RecommandFragment"
    const val TAG_DJDT = "com.geek.appindex.news.fragment.DJDTFragment"
    const val TAG_TZGG = "com.geek.appindex.news.fragment.TZGGFragment"
    const val TAG_ZCWJ = "com.geek.appindex.news.fragment.ZCWJFragment"
    const val TAG_XZYW = "com.geek.appindex.news.fragment.XZYWFragment"

    fun getFragmentByURL(url: String): Fragment {

        return when (url) {
            TAG_RECOMMAND -> {
                RecommandFragment()
            }
            TAG_DJDT -> {
                DJDTFragment()
            }
            TAG_TZGG -> {
                TZGGFragment()
            }
            TAG_ZCWJ -> {
                ZCWJFragment()
            }
            TAG_XZYW -> {
                XZYWFragment()
            }
            else -> {
                RecommandFragment()
            }
        }

    }
}