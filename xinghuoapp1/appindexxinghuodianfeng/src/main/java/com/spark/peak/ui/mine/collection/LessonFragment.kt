package com.spark.peak.ui.mine.collection

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeFragment
import com.spark.peak.ui.lesson.adapter.LessonsAdapter
import com.spark.peak.ui.netLessons.NetLessonsActivity
import com.spark.peak.ui.study.StudyPresenter
import kotlinx.android.synthetic.main.fragment_lesson.*
import org.jetbrains.anko.support.v4.startActivity

/**
 */
class LessonFragment : LifeFragment<StudyPresenter>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LessonFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }

    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_lesson
    private val lessonsAdapter by lazy {
        LessonsAdapter() {
            startActivity<NetLessonsActivity>(NetLessonsActivity.KEY to it.key)
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_list.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_list.adapter = lessonsAdapter
    }

    override fun handleEvent() {
        super.handleEvent()
    }

    override fun initData() {
        super.initData()
        presenter.getMyNetLesson {
            lessonsAdapter.setData(it)
        }
    }
}