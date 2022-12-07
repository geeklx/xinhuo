package com.spark.peak.ui.book

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.AudioRes
import com.spark.peak.bean.GradeBean
import com.spark.peak.ui.book.adapter.HomeBookAdapter
import com.spark.peak.ui.book.adapter.HomeBookSelectAdapter
import com.spark.peak.ui.video.AudioActivity
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_home_bookdf.*
import kotlinx.android.synthetic.main.layout_book_selectdf.*
import kotlin.math.floor

class HomeBookActivity : LifeActivity<HomeBookPresenter>() {
    override val presenter: HomeBookPresenter
        get() = HomeBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_home_bookdf

    private val datalist = arrayListOf<AudioRes>()
    private var bookName = "jctb"
    private val homeBookAdapter by lazy {
        HomeBookAdapter() { pos ->
            val intent = Intent(this, AudioActivity::class.java)
            intent.putExtra(AudioActivity.DATA, datalist)
            intent.putExtra(AudioActivity.POSITION, pos)
            intent.putExtra(AudioActivity.TYPE, "jctb")
            intent.putExtra(AudioActivity.NAME, bookName)
            intent.putExtra(AudioActivity.SECTION, tv_class.text)
            startActivity(intent)
        }
    }

    //学段
    private val periodAdapter by lazy {
        HomeBookSelectAdapter() {
            this.period = it
            getSelectGrade(false)
        }
    }

    //年级
    private val gradeAdapter by lazy {
        HomeBookSelectAdapter() {
            tv_class.text = it.name
            this.grade = it
            getSelectEditor(false)
        }
    }

    //版本
    private val typeAdapter by lazy {
        HomeBookSelectAdapter() {
            this.edition = it
            getSelectSyncListen()
        }
    }

    //上下册
    private val bookAdapter by lazy {
        HomeBookSelectAdapter() {
            this.term = it
            getSelectSyncListen()
        }
    }

    private var period: GradeBean? = null
    private var grade: GradeBean? = null
    private var edition: GradeBean? = null
    private var term: GradeBean? = null
    private var subjectKey = ""
    private var resourceType = "1"

    override fun configView() {
        super.configView()
        rlv_unit.layoutManager = LinearLayoutManager(this)
        rlv_unit.adapter = homeBookAdapter
        rlv_period.layoutManager = GridLayoutManager(this, 3)
        rlv_period.adapter = periodAdapter
        rlv_grade.layoutManager = GridLayoutManager(this, 3)
        rlv_grade.adapter = gradeAdapter
        rlv_type.layoutManager = GridLayoutManager(this, 3)
        rlv_type.adapter = typeAdapter
        rlv_book.layoutManager = GridLayoutManager(this, 3)
        rlv_book.adapter = bookAdapter
    }

    override fun handleEvent() {
        super.handleEvent()
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
        rg_book.setOnCheckedChangeListener { group, checkedId ->
            resourceType = "${group.indexOfChild(group.findViewById(checkedId)) + 1}"
            getSelectSyncListen()
        }
    }

    override fun onBackPressed() {
        if (v_book_select.visibility == View.GONE) {
            super.onBackPressed()
        } else {
            v_book_select.visibility = View.GONE
        }
    }

    override fun initData() {
        super.initData()
        this.period = SpUtil.periodGrade
        this.grade = SpUtil.gradedGrade
        this.edition = SpUtil.editionGrade
        this.term = SpUtil.termGrade
        getSelectSection(true)
    }

    override fun onPause() {
        super.onPause()
        SpUtil.periodGrade = this.period!!
        SpUtil.gradedGrade = this.grade!!
        SpUtil.editionGrade = this.edition!!
        SpUtil.termGrade = this.term!!
    }

    fun getSelectSection(isFirst: Boolean) {
        presenter.getSelectSection { periods ->
            if (!isFirst || this.period?.dictKey.isNullOrEmpty()) {
                this.period = periods[0]
            }
            periodAdapter.name = this.period?.name!!
            periodAdapter.setData(periods)
            getSelectGrade(isFirst)
        }
    }

    fun getSelectGrade(isFirst: Boolean) {
        presenter.getSelectGrade(this.period?.dictKey!!) { grades ->
            if (!isFirst || this.grade?.dictKey.isNullOrEmpty()) {
                this.grade = grades[0]
            }
            tv_class.text = this.grade?.name
            gradeAdapter.name = this.grade?.name!!
            gradeAdapter.setData(grades)
            getSelectEditor(isFirst)
        }
    }

    fun getSelectEditor(isFirst: Boolean) {
        presenter.getSelectEditor(this.period?.key!!, this.grade?.key!!, this.grade?.dictKey!!) {
            this.subjectKey = it.subjectKey
            if (!isFirst || this.edition?.dictKey.isNullOrEmpty()) {
                this.edition = it.editorLists[0]
            }
            if (!isFirst || this.term?.name.isNullOrEmpty()) {
                this.term = it.termList[0]
            }
            typeAdapter.name = this.edition?.name!!
            typeAdapter.setData(it.editorLists)
            bookAdapter.name = this.term?.name!!
            bookAdapter.setData(it.termList)
            getSelectSyncListen()
        }
    }

    fun getSelectSyncListen() {
        presenter.getSelectSyncListen(
            this.period?.key!!,
            this.grade?.key!!,
            this.subjectKey,
            this.edition?.key!!,
            this.term?.key!!
        ) {
            if (it.body != null) {
                bookName = it.body.name
                getCatalogResources(it.body.appSyncStudyKey, resourceType)
            } else {
                datalist.clear()
                homeBookAdapter.setData(datalist)
            }
        }
    }

    fun getCatalogResources(appSyncStudyKey: String, resourceType: String) {
        datalist.clear()
        presenter.getCatalogResources(appSyncStudyKey, resourceType) {
            it.catalogList.forEach { bookCatalog ->
                bookCatalog.catalogResources.forEach { res ->
                    try {
                        val duration = res.duration.toInt()
                        res.duration = "${floor(duration / 60.0).toInt()}:${duration % 60}"
                    } catch (e: Exception) {
                        res.duration = "--:--"
                    }
                    res.id = res.appResourceKey
                    datalist.add(res)
                }
            }
            homeBookAdapter.setData(datalist)
        }
    }
}