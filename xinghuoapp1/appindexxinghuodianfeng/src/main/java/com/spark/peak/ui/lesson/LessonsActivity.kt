package com.spark.peak.ui.lesson

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.Grade
import com.spark.peak.ui.lesson.adapter.GradeAdapter
import com.spark.peak.ui.lesson.adapter.LessonsAdapter
import com.spark.peak.ui.netLessons.NetLessonsActivity
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_lesson_listdf.*
import kotlinx.android.synthetic.main.layout_lesson_selectdf.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject


class LessonsActivity : LifeActivity<LessonPresenter>() {
    override val presenter: LessonPresenter
        get() = LessonPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_lesson_listdf

    private val gradeList = arrayListOf<Grade>()
    private var grade: Grade? = null

    private val lessonsAdapter by lazy {
        LessonsAdapter() {
            startActivity<NetLessonsActivity>(
                NetLessonsActivity.KEY to it.key,
                NetLessonsActivity.GRADE to tv_class.text.toString()
            )
        }
    }

    private val gradeAdapter by lazy {
        GradeAdapter { item ->
            saSection(item.parentName + item.name)
            tv_class.text = item.parentName + item.name
            getLessons(item.id)
            v_book_select.visibility = View.GONE
            grade = item
        }
    }

    override fun configView() {
        rlv_lesson.layoutManager = LinearLayoutManager(this)
        rlv_lesson.adapter = lessonsAdapter
        rlv_grade.layoutManager = GridLayoutManager(this, 3)
        rlv_grade.isNestedScrollingEnabled = false
        rlv_grade.adapter = gradeAdapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_class.setOnClickListener {
            tv_class.isSelected = !tv_class.isSelected
            if (v_book_select.visibility == View.VISIBLE) {
                v_book_select.visibility = View.GONE
            } else {
                v_book_select.visibility = View.VISIBLE
            }
        }
        v_book_select.setOnClickListener {
            v_book_select.visibility = View.GONE
        }
    }

    override fun initData() {
        super.initData()
        presenter.getSectionAndGradeList { list ->
            gradeList.clear()
            list.forEach { section ->
                val gradeSec = Grade(section.catalogKey, section.catalogName)
                gradeSec.type = "1"
                gradeList.add(gradeSec)
                section.resourceList.forEach { grade ->
                    grade.type = "2"
                    grade.parentId = section.catalogKey
                    grade.parentName = section.catalogName
                    gradeList.add(grade)
                }
            }
            gradeAdapter.setData(gradeList)
        }
        if (SpUtil.grade.parentName.isNullOrEmpty()) {
            grade = Grade(SpUtil.userInfo.grade ?: "", SpUtil.userInfo.gradename ?: "")
            grade?.parentId = SpUtil.userInfo.section ?: ""
            grade?.parentName = SpUtil.userInfo.sectionname ?: ""
        } else {
            grade = SpUtil.grade
        }
        tv_class.text = grade?.parentName + grade?.name
        gradeAdapter.selectKey = grade!!.id
        getLessons(grade!!.id)
    }

    override fun onPause() {
        super.onPause()
        SpUtil.grade = grade!!
    }

    fun getLessons(id: String) {
        presenter.getLesson(id) {
            lessonsAdapter.setData(it)
        }
    }

    private fun saSection(name: String) {
        try {
            val property = JSONObject()
            property.put("minicourse_section", name)
            SensorsDataAPI.sharedInstance().track("df_minicourse_section", property)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
