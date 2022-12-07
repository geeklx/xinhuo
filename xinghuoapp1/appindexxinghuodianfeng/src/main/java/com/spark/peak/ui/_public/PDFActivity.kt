package com.spark.peak.ui._public

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.PdfImg
import com.spark.peak.ui._public.adapter.PDFAdapter
import kotlinx.android.synthetic.main.activity_pdf.*
import org.jetbrains.anko.toast

class PDFActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_pdf

    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private val url by lazy { intent.getStringExtra(URL) ?: "" }
    private val type by lazy { intent.getStringExtra(TYPE) ?: "" }
    private val name by lazy { intent.getStringExtra(NAME) ?: "" }
    private val pdfAdapter by lazy { PDFAdapter(this) }
    private var currentIndex = 0
    private var list = arrayListOf<PdfImg>()

    companion object {
        val URL = "url"
        val TITLE = "title"
        val TYPE = "type"
        val NAME = "name"
        val KEY = "key"
    }

    override fun configView() {
        super.configView()
        tv_title.text = name
        view_pager.adapter = pdfAdapter
    }

    fun freshBottom() {
        tv_pdf_num.text = "${currentIndex + 1}/${list.size}"
        tv_pre.isEnabled = currentIndex > 0
        tv_next.isEnabled = currentIndex < list.size - 1
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_pre.setOnClickListener {
            currentIndex--
            freshBottom()
            view_pager.currentItem = currentIndex
        }
        tv_next.setOnClickListener {
            currentIndex++
            freshBottom()
            view_pager.currentItem = currentIndex
        }
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentIndex = position
                freshBottom()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        img_toolbar_download.setOnClickListener {
            AlertDialog.Builder(this).setMessage("复制链接至电脑浏览器下载")
                .setPositiveButton("复制链接") { dialogInterface, _ ->
                    //获取剪贴板管理器：
                    val cm = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    // 创建普通字符型ClipData
                    val mClipData =
                        ClipData.newPlainText("Label", "${WEB_BASE_URL}scan/pdfMaterial?key=${key}")
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData)
                    toast("已复制到剪贴板")
                }.create().show()
        }
    }

    override fun initData() {
        super.initData()
        presenter.getResInfo(key, "2", "1") {
            if (it.imgList != null) {
                pdfAdapter.dataList = it.imgList
                list = it.imgList
                freshBottom()
            }
        }
    }
}
