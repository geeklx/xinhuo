package com.spark.peak.base

import android.app.Fragment
import android.view.View
import org.jetbrains.anko.*

/**
 * 创建者： huoshulei
 * 时间：  2017/5/6.
 */
interface BaseFragmentComponent {
    fun createView(fragment: Fragment) = fragment.UI {
        createView(this)
    }

    fun createView(ui: AnkoContext<Fragment>): View

}
