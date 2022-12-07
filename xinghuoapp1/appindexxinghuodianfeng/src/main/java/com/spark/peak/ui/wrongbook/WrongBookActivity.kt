package com.spark.peak.ui.wrongbook

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.Section
import com.spark.peak.bean.Subject
import com.spark.peak.bean.WrongBook
import com.spark.peak.bean.WrongBookDate
import com.spark.peak.ui.dialog.WrongDateDialog
import com.spark.peak.ui.wrongbook.adapter.SelecteAdapter
import com.spark.peak.ui.wrongbook.adapter.WrongDateAdapter
import com.spark.peak.ui.wrongbook.adapter.WrongExAdapter
import com.spark.peak.utlis.GradUtil
import kotlinx.android.synthetic.main.activity_wrong_bookdf.*
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class WrongBookActivity : LifeActivity<WrongBookPresenter>() {
    override val presenter: WrongBookPresenter
        get() = WrongBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_wrong_bookdf

    var subjectKey = "" //科目或试卷分类主键
    var wrongDateList: List<WrongBookDate>? = null
    var currentDate = ""
    val sectionAdapter by lazy { SelecteAdapter{
        Log.d("Section==adapter=",selectedSection.toString())
        selectedSection = it
        selectedSubject = 0 //选择年级后 默认选中第一个学科
        subjectAdapter.position = 0
        getSubjects()
    } }
    val subjectAdapter by lazy { SelecteAdapter{
        selectedSubject = it
    } }
    var selectedSection = -1 //选中的年级索引
    var selectedSubject = -1 //选中的科目索引

    val sections by lazy { mutableListOf<Section>() }
    val gradKeyList by lazy { mutableListOf<String>() }

    val subjects by lazy { mutableListOf<Subject>() }

    var page = 1
    val step = 10

    var selectedIndex = 0 //选中错题日期的索引

    companion object {
        val SUBJECTS = "subjects"
    }

    private val popupWindow by lazy {
        val popupView = with(ctx) {
            verticalLayout {
                lparams(matchParent, wrapContent)
                backgroundResource = R.color.color_ffffff
                textView {
                    text = "选择年级"
                    textColor = Color.parseColor("#999999")
                }.lparams{
                    leftMargin = dip(10)
                }
                recyclerView {
                    padding = dip(10)
                    layoutManager = androidx.recyclerview.widget.GridLayoutManager(ctx, 3)
                    adapter = this@WrongBookActivity.sectionAdapter
                }
                view { backgroundResource = R.color.color_f0f0f0 }.lparams(matchParent, dip(0.5f))

                textView {
                    text = "选择类型"
                    textColor = Color.parseColor("#999999")
                }.lparams{
                    leftMargin = dip(10)
                }
                recyclerView {
                    padding = dip(10)
                    layoutManager = androidx.recyclerview.widget.GridLayoutManager(ctx, 3)
                    adapter = this@WrongBookActivity.subjectAdapter
                }
                view { backgroundResource = R.color.color_f0f0f0 }.lparams(matchParent, dip(0.5f))

                linearLayout {
                    textView("取消") {
                        gravity = Gravity.CENTER
                        setOnClickListener {
                            cancel()
                        }
                    }.lparams(0, dip(42), 1f)
                    textView("确认") {
                        gravity = Gravity.CENTER
                        backgroundResource = R.color.color_1482ff
                        textColor = resources.getColor(R.color.color_ffffff)
                        setOnClickListener {
                            confirm()
                        }
                    }.lparams(0, dip(42), 1f)
                }
            }
        }
        PopupWindow(popupView, matchParent, wrapContent).apply {
            setOnDismissListener {
                bg_view.visibility = View.GONE
            }
        }
    }

    private fun cancel() {
        popupWindow.dismiss()
    }

    private fun confirm() {
        tv_date.text = currentDate
        getWrongInDate()
        popupWindow.dismiss()
    }

    override fun configView() {
        setSupportActionBar(tb_wrong_book)

        rv_wrong_list.layoutManager = LinearLayoutManager(ctx)

        rv_wrong_list.adapter = oAdapter
    }


    override fun initData() {
        getSections()
        presenter.getWrongDate(1, 1000) {
            if (it.isNotEmpty()){
                wrongDateList = it
                currentDate = if (wrongDateList?.isNotEmpty()!!) wrongDateList!![selectedIndex].time ?: "" else ""
                tv_date.text = currentDate + "(" + (if (wrongDateList?.isNotEmpty()!!) wrongDateList!![selectedIndex].count else "") + "题)"
                initDialog()
                getWrongInDate()
            }else{
                tv_date.visibility = View.GONE
            }
        }

    }

    /**
     * 更新当前页面数据
     * 错题数目、错题时间
     */
    private fun refreshInfo(){
        presenter.getWrongDate(1, 1000) {
            if (it.isNotEmpty()){
                wrongDateList = it
                currentDate = if (wrongDateList?.isNotEmpty()!!) wrongDateList!![selectedIndex].time ?: "" else ""

                if (tv_date.text.endsWith(")")) { //若当前显示了错题数，则更新
                    tv_date.text = currentDate + "(" + (if (wrongDateList?.isNotEmpty()!!) wrongDateList!![selectedIndex].count else "") + "题)"
                }
                initDialog()
            }else{
                tv_date.visibility = View.GONE
            }
        }
    }
    /**
     * 先获取全部学段和年级
     */
    private fun getSections(){
        presenter.getSectionAndGradeList {
            sections.addAll(it)
        }
    }

    /**
     * 点击对应的年级后，请求对应的学科列表
     */
    private fun getSubjects() {
        Log.d("Section==get==", selectedSection.toString())
        presenter.getSubject(gradKeyList[selectedSection]) {
            subjects.clear()
            subjects.addAll(it)
            val dataList = mutableListOf<String>()
            dataList.add("全部")
            subjects.forEach {
                dataList.add(it.name ?: "")
            }
            subjectAdapter.setData(dataList)
        }
    }

    override fun handleEvent() {
        tb_wrong_book.setNavigationOnClickListener { onBackPressed() }
        tv_date.setOnClickListener {
            mDialog?.let {
                it.show()
            }
        }
        tv_select.setOnClickListener {
            // TODO: 2018/6/11 13:40 霍述雷 设置数据
            val dataList = mutableListOf<String>()
            sections.forEach {
                var sectionStr = it.catalogName
                it.resourceList.forEach {
                    dataList.add(GradUtil.parseGradStr(sectionStr, it.name))
                    gradKeyList.add(it.id)
                }
            }
            sectionAdapter.setData(dataList)
            if (popupWindow.isShowing) popupWindow.dismiss()
            else {
                bg_view.visibility = View.VISIBLE
                popupWindow.showAsDropDown(tb_wrong_book)
            }
        }

        srl.setOnRefreshListener {
            onRefresh()
        }
    }


    var mDialog: WrongDateDialog? = null
    @SuppressLint("SetTextI18n")
    private fun initDialog() {
        var oAdapter = WrongDateAdapter {data,count ->
            reset()
            tv_date.text = data + "(" + count + "题)"
            currentDate = data
            getWrongInDate()
            mDialog?.let {
                it.dismiss()
            }
        }
        oAdapter.setData(wrongDateList)
        mDialog = WrongDateDialog(ctx, oAdapter)
    }

    /**
     * 选择时间后，重置筛选条件
     */
    private fun reset(){
        sectionAdapter.position = -1
        subjectAdapter.position = -1
        selectedSubject = 0
        selectedSection = -1
        subjectAdapter.setData(ArrayList())
        subjectAdapter.notifyDataSetChanged()
        tv_select.text = "全部"
    }

    var oAdapter = WrongExAdapter({
        var intent = Intent()
        intent.setClass(ctx,WrongDetailActivity::class.java)
        intent.putExtra(WrongDetailActivity.WRONG_ITEM,it)
        startActivityForResult(intent,66)
    },{}){
        onLoadMore()
    }

    private fun getWrongInDate() {
        if (selectedSubject==0){
            subjectKey = ""
            tv_select.text = "全部"
        }else if (selectedSubject!=-1) {
            subjectKey = subjects[selectedSubject-1].key?:""
            tv_select.text = subjects[selectedSubject-1].name
        }

        page = 1
        presenter.getWrongList(currentDate, if (selectedSection == -1) "" else gradKeyList[selectedSection], subjectKey, page, step) {
            setData(it)
        }
    }

    fun setData(list: List<WrongBook>) {
        oAdapter.setMore(true)
        oAdapter.setData(list)
        rv_wrong_list.adapter = oAdapter
    }

    private fun onRefresh(){
        if (selectedSubject==0){
            subjectKey = ""
            tv_select.text = "全部"
        }else if (selectedSubject!=-1) {
            subjectKey = subjects[selectedSubject-1].key?:""
            tv_select.text = subjects[selectedSubject-1].name
        }

        page = 1
        presenter.getWrongList(currentDate, if (selectedSection == -1) "" else gradKeyList[selectedSection], subjectKey, page, step) {
            setData(it)
            srl.isRefreshing = false
        }
    }

    private fun onLoadMore(){
        if (selectedSubject==0){
            subjectKey = ""
            tv_select.text = "全部"
        }else if (selectedSubject!=-1) {
            subjectKey = subjects[selectedSubject-1].key?:""
            tv_select.text = subjects[selectedSubject-1].name
        }

        page++
        presenter.getWrongList(currentDate, if (selectedSection == -1) "" else gradKeyList[selectedSection], subjectKey, page, step) {
            if (it.isEmpty()){
                oAdapter.setMore(false)
                oAdapter.notifyDataSetChanged()
            }else{
                oAdapter.addData(it)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 123){
            refreshInfo()
            getWrongInDate()
        }
    }

    override fun onBackPressed() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }else{
            super.onBackPressed()
        }
    }
}
