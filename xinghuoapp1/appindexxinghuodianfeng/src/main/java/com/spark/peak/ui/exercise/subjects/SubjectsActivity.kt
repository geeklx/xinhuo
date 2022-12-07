package com.spark.peak.ui.exercise.subjects

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.Subject
import com.spark.peak.ui.common.grade.GradeActivity
import com.spark.peak.ui.exercise.adapter.SubjectsAdapter
import com.spark.peak.ui.exercise.history.ExHistoryActivity
import com.spark.peak.ui.exercise.version.SubVersionsActivity
import com.spark.peak.ui.home.HomePresenter
import com.spark.peak.utlis.GradUtil
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_subjects.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class SubjectsActivity : LifeActivity<HomePresenter>() {
    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_subjects

    private val subjectList by lazy { mutableListOf<Subject>() }

    var oAdapter = SubjectsAdapter{ name, key ->
        startActivity<SubVersionsActivity>(SubVersionsActivity.SUB_NAME to name,
                SubVersionsActivity.SUB_KEY to key)
    }

    companion object {
        var SUB_LIST = "list"

        var FROM_EX = "fromEx"
        var RESULT_STR = "str"
        var RESULT_KEY = "key"
    }
    override fun configView() {
        setSupportActionBar(tb_subjects)
        rv_subjects.layoutManager = GridLayoutManager(ctx, 3)
        tv_grade.text = GradUtil.parseGradStr(SpUtil.userInfo.sectionname?:SpUtil.defaultSection.catalogName, SpUtil.userInfo.gradename?:SpUtil.defaultGrade.name)
    }

    override fun initData() {
        getSubjects()
    }

    private fun getSubjects(key : String = SpUtil.userInfo.grade ?: SpUtil.defaultGrade.id){
        presenter.getSubjects(key){
            subjectList.clear()
            subjectList.addAll(it.list?:ArrayList<Subject>())
            oAdapter.setData(subjectList)
            rv_subjects.adapter = oAdapter
        }
    }

    override fun handleEvent() {
        tb_subjects.setNavigationOnClickListener { onBackPressed() }
        iv_history.setOnClickListener {
            startActivity<ExHistoryActivity>()
        }
        tv_grade.setOnClickListener {
            var intent = Intent()
            intent.putExtra(FROM_EX,true)
            intent.setClass(ctx, GradeActivity::class.java)
            startActivityForResult(intent,1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            tv_grade.text = data.getStringExtra(RESULT_STR)
            getSubjects(data.getStringExtra(RESULT_KEY)?:"")
        }

    }
//    fun onSubjectClick(view : View){
//        when(view.id){
//            R.id.sub_yuwen -> startActivity<SubVersionsActivity>(SubVersionsActivity.SUB_NAME to "语文")
//            R.id.sub_shuxue -> startActivity<SubVersionsActivity>(SubVersionsActivity.SUB_NAME to "数学")
//            R.id.sub_yingyu -> startActivity<SubVersionsActivity>(SubVersionsActivity.SUB_NAME to "英语")
//            R.id.sub_wuli -> startActivity<SubVersionsActivity>(SubVersionsActivity.SUB_NAME to "物理")
//            R.id.sub_shengwu -> startActivity<SubVersionsActivity>(SubVersionsActivity.SUB_NAME to "生物")
//            R.id.sub_lishi -> startActivity<SubVersionsActivity>(SubVersionsActivity.SUB_NAME to "历史")
//            R.id.sub_dili -> startActivity<SubVersionsActivity>(SubVersionsActivity.SUB_NAME to "地理")
//            R.id.sub_zhengzhi -> startActivity<SubVersionsActivity>(SubVersionsActivity.SUB_NAME to "政治")
//            R.id.sub_huaxue -> startActivity<SubVersionsActivity>(SubVersionsActivity.SUB_NAME to "化学")
//        }
//    }
}
