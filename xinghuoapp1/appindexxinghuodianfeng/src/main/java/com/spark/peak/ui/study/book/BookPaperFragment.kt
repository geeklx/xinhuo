package com.spark.peak.ui.study.book

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.LifeFragment
import com.spark.peak.bean.MyBookPaper
import com.spark.peak.bean.ResourceItem
import com.spark.peak.ui.exercise.detail.ExerciseDetailActivity2
import com.spark.peak.ui.study.book.adapter.PapersAdapter
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import kotlin.properties.Delegates


private const val DATA = "data"

/**
 * Created by 李昊 on 2018/5/9.
 */
class BookPaperFragment : LifeFragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int = 0

    var rv_papers by Delegates.notNull<RecyclerView>()
    val parent by lazy { activity as BookDetailActivity}

    override fun initView(): View? = with(this.requireContext()) {
        rv_papers = recyclerView {
            lparams(matchParent, matchParent)
            backgroundColor = Color.parseColor("#ffffff")
        }
        rv_papers
    }

    override fun configView(view: View?) {

    }

    var dataList by Delegates.notNull<ArrayList<MyBookPaper>>()
    override fun initData() {
        arguments?.let {
            dataList = it.getSerializable(DATA) as ArrayList<MyBookPaper>
            var oAdapter = PapersAdapter {
                if (parent is BookDetailActivity) {
                    val item = ResourceItem()
                    item.type = "1"
                    item.id = it.resourceKey
                    (parent as BookDetailActivity).operation("点击资源", item, "配套试题")
                }
                checkLogin {
                    if ((parent as BookDetailActivity).bookDetail?.isown == "1") {
                        startActivity<ExerciseDetailActivity2>(
                            ExerciseDetailActivity2.BOOK_KEY to (parent as BookDetailActivity).bookDetail?.supportingKey,
                            ExerciseDetailActivity2.BOOK_TITLE to (parent as BookDetailActivity).bookDetail?.titile,
                            ExerciseDetailActivity2.PARENT_KEY to it.catalogKey,
                            ExerciseDetailActivity2.NAME to it.resourceName,
                            ExerciseDetailActivity2.KEY to it.resourceKey,
                            ExerciseDetailActivity2.TYPE to ExerciseDetailActivity2.TYPE_QYT
                        )
                    } else {
                        toast("请先加入我的学习计划再做题")
                    }

                }
            }

            oAdapter.setData(dataList)
            rv_papers.layoutManager = LinearLayoutManager(this.requireContext())
            rv_papers.adapter = oAdapter
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(item: ArrayList<MyBookPaper>) =
            BookPaperFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(DATA, item)
                }
            }
    }
}