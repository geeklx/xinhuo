package com.spark.peak.ui.study.book

import android.annotation.SuppressLint
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.MyBookDetail
import com.spark.peak.bean.MyBookPaper
import com.spark.peak.bean.MyBookResource
import com.spark.peak.bean.ResourceItem
import com.spark.peak.ui.home.sa.SaEvent
import com.spark.peak.ui.study.StudyPresenter
import com.spark.peak.ui.study.book.adapter.BookDetailPagerAdapter
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_book_detail.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class BookDetailActivity : LifeActivity<StudyPresenter>() {
    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_book_detail

    var dataList = ArrayList<Fragment>()
    val bookKey by lazy { intent.getStringExtra(KEY) ?: "" }
    val type by lazy { intent.getStringExtra(TYPE) ?: "" }

    var bookDetail: MyBookDetail? = null

    val oAdapter by lazy { BookDetailPagerAdapter(dataList, supportFragmentManager) }
    var resFragment: BookResourceFragment? = null

    companion object {
        val KEY = "key"
        val TYPE = "type"
    }

    override fun configView() {
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_add.setOnClickListener {
            val data = HashMap<String, String>()
            data.put("key", bookKey)
            data.put("type", "support")
            presenter.addMyStudy(data) {
                toast(it)
                tv_add.visibility = View.GONE
                refreshState()
            }
        }
        rg_book.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById(checkedId) as RadioButton
            vp_book_detail.currentItem = group.indexOfChild(rb)
            operation(rb.text.toString())
        }
        vp_book_detail.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val rb = rg_book.getChildAt(position) as RadioButton
                rb.isChecked = true
                operation(rb.text.toString())
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    /**
     * operationName：操作名称
     */
    fun operation(operationName: String) {
        val property = JSONObject()
        bookDetail?.let { detail ->
            property.put("book_matching_id", detail.bookkey)
            property.put("book_matching_name", detail.titile)
            property.put("section", detail.gradeName)
        }
        property.put("operation_name", operationName)
        SaEvent.baseClick(property, "df_operation_book_matching_page")
    }

    /**
     * operationName：操作名称
     * typeName：资源类型
     * resourceId：操作资源id
     * resourceName：资源所属模块名称
     */
    fun operation(
        operationName: String,
        item: ResourceItem,
        resourceName: String
    ) {
        val property = JSONObject()
        bookDetail?.let { detail ->
            property.put("book_matching_id", detail.supportingKey)
            property.put("book_matching_name", detail.titile)
            property.put("section", detail.gradeName)
        }
        property.put("operation_name", operationName)
        property.put("resource_type", typeName(item))
        property.put("resource_id", item.id)
        property.put("resource_module", resourceName)
        SpUtil.SenorData = property.toString()
        SaEvent.baseClick(property, "df_operation_book_matching_page")
        when (item.type) {
            "4", "5", "6", "8", "13" -> {
                SaEvent.baseClick(property, "df_open_document")
            }
            "3" -> {
                SaEvent.baseClick(property, "df_start_watch_course_video")
            }
        }
    }

    private fun typeName(item: ResourceItem): String {
        return when (item.type) {
            "1" -> "试卷"
            "2" -> "试卷解析"
            "3" -> "视频"
            "4" -> "图片"
            "5" -> "图文"
            "6" -> "文档"
            "7" -> "音频"
            "8" -> "外链"
            "13" -> "外链"
            else -> ""
        }
    }

    override fun onResume() {
        super.onResume()
        if (resFragment?.isAdded == true) {
            presenter.getMyBookDetail(bookKey, type) {
                resFragment!!.setData(it.resource!!)
            }
        }
    }

    override fun initData() {
        presenter.getMyBookDetail(bookKey, type) {
            bookDetail = it
            tv_title.text = it.titile
            if (it.bookkey.isNotEmpty()) {
                initBookData(it)
            } else {
                onBackPressed()
            }
        }
    }

    private fun initBookData(detail: MyBookDetail) {
        Glide.with(this).load(detail.img).error(R.drawable.default_book).into(iv_book_cover)
        oAdapter.removeAll()
        dataList.clear()
        if (!detail.resource.isNullOrEmpty()) {
            rb_one.visibility = View.VISIBLE
            resFragment = BookResourceFragment.newInstance(
                list = detail.resource ?: ArrayList<MyBookResource>(),
                count = detail.mediacount,
                type = "pt",
                name = detail.titile ?: "",
                size = detail.mediasize ?: "",
                isOwn = detail.isown,
                enable = true
            )
            dataList.add(resFragment!!)
        }
        if (!detail.paper.isNullOrEmpty()) {
            rb_two.visibility = View.VISIBLE
            dataList.add(BookPaperFragment.newInstance(detail.paper ?: ArrayList<MyBookPaper>()))
        }
        vp_book_detail.adapter = oAdapter
        if (detail.isown == "0") {
            tv_add.visibility = View.VISIBLE
            if (detail.supportingKey.isEmpty()) {
                tv_add.isEnabled = false
                tv_add.text = "未上架"
            }
        } else if (detail.isown == "1") {
            tv_add.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshState() {
        bookDetail?.isown = "1"
        if (dataList.isNotEmpty()) {
            val f = dataList[0]
            if (f is BookResourceFragment) {
                f.refreshState()
            }
        }
    }
}
