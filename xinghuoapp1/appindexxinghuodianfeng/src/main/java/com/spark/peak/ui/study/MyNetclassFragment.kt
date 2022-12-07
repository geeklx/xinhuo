package com.spark.peak.ui.study

import android.app.AlertDialog
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.spark.peak.base.LifeFragment
import com.spark.peak.ui.lesson.LessonDetailActivity
import com.spark.peak.ui.lesson.LessonsActivity
import com.spark.peak.ui.study.adapter.MyNetclassAdapter
import com.spark.peak.utlis.SpUtil
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.support.v4.toast
import kotlin.properties.Delegates

/**
 * Created by 李昊 on 2018/4/20.
 */
class MyNetclassFragment : LifeFragment<StudyPresenter>() {
    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int = 0
    private var srl_net_class  by Delegates.notNull<SwipeRefreshLayout>()
    private var rv_net_class by Delegates.notNull<RecyclerView>()

    var oAdapter = MyNetclassAdapter(true, true, {
        startActivity<LessonsActivity>()
    }, {
        startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to it)
    }){ key, position ->
        if (key == "") {
            toast("未过期网课不允许删除")
        } else {
            AlertDialog.Builder(this.requireContext()).setMessage("确定要删除该网课吗？").setPositiveButton("确定", { _, _ ->
                var map = HashMap<String, String>()
                map.put("key", key)
                presenter.deleteMyNetClass(map) {
                    toast(it)
                    removeView(position)
                }
            }).setNegativeButton("取消", { dialog, _ ->
                dialog.dismiss()
            }).create().show()
        }
    }

    /**
     * 删除列表中的网课
     */
    fun removeView(position: Int){
        oAdapter.remove(position)
    }

    override fun initView(): View? = UI {
        verticalLayout {
            leftPadding = dip(15)
            backgroundColor = Color.parseColor("#f7f7f7")
            srl_net_class = swipeRefreshLayout {
                rv_net_class = recyclerView {
                    lparams(matchParent, wrapContent)
                }
            }.lparams(matchParent, matchParent)
        }
    }.view


    override fun initData() {

    }

    // 90 + 10
    //天赋 425
    //经验 135

    // 91 + 9
    //天赋 200
    //经验 190
    override fun onResume() {
        super.onResume()
        if (SpUtil.isLogin){
            getData()
        }else{
            oAdapter.setData(ArrayList())
            oAdapter.notifyDataSetChanged()
        }
    }

    override fun configView(view: View?) {
        rv_net_class.layoutManager = GridLayoutManager(this.requireContext(), 2)
        rv_net_class.adapter = oAdapter
        srl_net_class.setOnRefreshListener {
            refreshData()
        }
    }

    private fun getData(){
        presenter.getMyNetClass {
            oAdapter.setData(it)
//        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
//            override fun getSpanSize(position: Int): Int {
//                if (position == 0){
//                    return gridLayoutManager.spanCount
//                }
//                return 1
//            }
//
//        }
            rv_net_class.adapter = oAdapter
        }
    }

    private fun refreshData(){
        presenter.getMyNetClass (onError = {
            srl_net_class.isRefreshing = false
        }){
            oAdapter.setData(it)
            rv_net_class.adapter = oAdapter
            srl_net_class.isRefreshing = false
        }
    }
}