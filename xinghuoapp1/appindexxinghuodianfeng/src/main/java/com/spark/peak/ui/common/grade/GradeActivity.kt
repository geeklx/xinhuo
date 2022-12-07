package com.spark.peak.ui.common.grade

import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.Grade
import com.spark.peak.ui.lesson.adapter.GradeAdapter
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_gradedf.*

/**
 * 创建者：
 * 时间：
 */
class GradeActivity(override val layoutResId: Int = R.layout.activity_gradedf) : LifeActivity<GradePresenter>() {
    override val presenter by lazy { GradePresenter(this) }
    private val gradeAdapter by lazy {
        GradeAdapter {
            selectedGrad = it
        }
    }
    private val gradeList = arrayListOf<Grade>()
    private var selectedGrad: Grade? = null
    private var type = ""//grade表示未选择学段，不能进行操作

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_ok.setOnClickListener {
            presenter.updUserSG(selectedGrad?.parentId ?: "", selectedGrad?.id ?: "") {
                type = ""
                SpUtil.isLogin = true
                refreshHomeData()
                onBackPressed()
            }
        }
    }

    override fun configView() {
        val userInfo = SpUtil.userInfo
        selectedGrad = Grade(userInfo.grade ?: "", userInfo.gradename ?: "")
        selectedGrad!!.parentName = userInfo.sectionname ?: ""
        selectedGrad!!.parentId = userInfo.section ?: ""
        type = intent.getStringExtra("type") ?: ""
        rlv_grade.layoutManager = GridLayoutManager(this, 3)
        rlv_grade.adapter = gradeAdapter
        gradeAdapter.selectKey = selectedGrad!!.id
    }

    override fun initData() {
        super.initData()
        presenter.getSectionAndGradeList {
            gradeList.clear()
            it.forEach { section ->
                val gradeSec = Grade(section.catalogKey, section.catalogName)
                gradeSec.type = "1"
                gradeList.add(gradeSec)
                section.resourceList.forEach { grade ->
                    grade.type = "2"
                    grade.parentId = section.catalogKey
                    gradeList.add(grade)
                }
            }
            gradeAdapter.setData(gradeList)
        }
    }

    override fun onBackPressed() {
        if (type != "grade") {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "请选择学段", Toast.LENGTH_LONG).show()
        }
    }
}