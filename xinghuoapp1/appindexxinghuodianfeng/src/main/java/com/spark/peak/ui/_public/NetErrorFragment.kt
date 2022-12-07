package com.spark.peak.ui._public

import android.view.View
import com.spark.peak.R
import com.spark.peak.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_net_error.*

/**
 * Created by 李昊 on 2018/7/18.
 */
class NetErrorFragment: BaseFragment(){
    override val layoutResId: Int
        get() = R.layout.fragment_net_error


    var listener: Listener ?= null

    override fun configView(view: View?) {
        super.configView(view)
        re_try.setOnClickListener {
            listener?.reTry()
        }
    }

    interface Listener{
        fun reTry()
    }
}