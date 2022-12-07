package com.spark.peak.ui.netLessons

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeFragment
import com.spark.peak.ui.netLessons.adapter.NetListAdapter
import kotlinx.android.synthetic.main.fragment_net_listdf.*

/**
 */
class NetListFragment : LifeFragment<NetLessonsPresenter>() {
    companion object {
        fun instance(key: String) = NetListFragment().apply {
            arguments = Bundle().apply {
                putString(KEY, key)
            }
        }

        const val KEY = "key"
    }

    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_net_listdf

    private val netListAdapter by lazy { NetListAdapter() }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_list.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_list.adapter = netListAdapter
    }

    override fun initData() {
        super.initData()
        presenter.getNetLessonCatalogue(arguments?.getString(KEY)!!) {
            if (it.children.isNotEmpty()) {
                netListAdapter.setData(it.children[0].resourceList)
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
    }

}