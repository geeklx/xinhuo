package com.spark.peak.ui.exercise

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.Grade
import com.spark.peak.ui.exercise.listen.ExerciseListActivity
import com.spark.peak.ui.lesson.adapter.GradeAdapter
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_grade_exercise.*
import kotlinx.android.synthetic.main.layout_lesson_select.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class GradeExerciseActivity : LifeActivity<ExercisePresenter>() {
    override val presenter: ExercisePresenter
        get() = ExercisePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_grade_exercise
    private val exerciseAdapter by lazy {
        GradeExerciseAdapter() {
            startActivity<ExerciseListActivity>("key" to it.key, "title" to it.name)
        }
    }
    private val gradeList = arrayListOf<Grade>()

    private var grade: Grade? = null
    private val gradeAdapter by lazy {
        GradeAdapter { item ->
            saSection(item.parentName + item.name)
            tv_class.text = item.parentName + item.name
            getSpecialList(item.parentId, item.id)
            v_book_select.visibility = View.GONE
            grade = item
        }
    }

    override fun configView() {
        super.configView()
        rlv_exercise.layoutManager = LinearLayoutManager(this)
        rlv_exercise.adapter = exerciseAdapter
        rlv_grade.layoutManager = GridLayoutManager(this, 3)
        rlv_grade.isNestedScrollingEnabled = false
        rlv_grade.adapter = gradeAdapter
    }

    override fun initData() {
        super.initData()
        if (SpUtil.grade.parentName.isNullOrEmpty()) {
            grade = Grade(SpUtil.userInfo.grade ?: "", SpUtil.userInfo.gradename ?: "")
            grade?.parentId = SpUtil.userInfo.section ?: ""
            grade?.parentName = SpUtil.userInfo.sectionname ?: ""
        } else {
            grade = SpUtil.grade
        }
        tv_class.text = "${grade!!.parentName}${grade!!.name}"
        gradeAdapter.selectKey = grade!!.id
        getSpecialList(grade!!.parentId, grade!!.id)
        presenter.getSectionAndGradeList {
            gradeList.clear()
            it.forEach { section ->
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
    }

    override fun onPause() {
        super.onPause()
        SpUtil.grade = grade!!
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
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

    fun getSpecialList(sectonKey: String, gradeKey: String) {
        presenter.getSpecialList(sectonKey, gradeKey) {
            exerciseAdapter.setData(it)
        }
    }

    private fun saSection(section: String) {
        try {
            val property = JSONObject()
            property.put("paper_section", section)
            SensorsDataAPI.sharedInstance().track("df_paper_section", property)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}