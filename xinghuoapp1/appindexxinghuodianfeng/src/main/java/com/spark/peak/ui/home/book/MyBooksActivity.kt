package com.spark.peak.ui.home.book

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.home.book.adapter.MyBookAdapter
import com.spark.peak.ui.home.sa.SaEvent
import com.spark.peak.ui.scan.ScannerActivity
import com.spark.peak.ui.study.StudyPresenter
import com.spark.peak.ui.study.book.BookDetailActivity
import com.spark.peak.utlis.PermissionUtlis
import com.zxing.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_my_booksdf.*
import org.jetbrains.anko.startActivity

class MyBooksActivity : LifeActivity<StudyPresenter>() {

    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_my_booksdf
    private val bookAdapter by lazy {
        MyBookAdapter({//item.click
            startActivity<BookDetailActivity>(
                BookDetailActivity.KEY to it.key,
                BookDetailActivity.TYPE to "2"
            )
        }, {//scanclick
            SaEvent.scanClick()
            PermissionUtlis.checkPermissions(this, Manifest.permission.CAMERA) {
                IntentIntegrator(this@MyBooksActivity)
                    .setOrientationLocked(false)
                    .setCaptureActivity(ScannerActivity::class.java)
                    .initiateScan()
            }
        }) { item, pos ->
            //deleteclick
            AlertDialog.Builder(this).setMessage("确定要删除该图书吗？").setPositiveButton("确定") { _, _ ->
                val map = hashMapOf<String, String>()
                map.put("key", item.key!!)
                presenter.deleteMyBooks(map) {
                    Toast.makeText(this@MyBooksActivity, it, Toast.LENGTH_LONG).show()
                    remove(pos)
                }
            }.setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
        }
    }

    override fun configView() {
        super.configView()
        rlv_book.layoutManager = GridLayoutManager(this, 3)
        rlv_book.adapter = bookAdapter
    }

    override fun onResume() {
        super.onResume()
        presenter.getMyBooks {
            bookAdapter.setData(it)
            tv_num.text = "共${it.size}本图书"
        }
    }

    override fun initData() {
        super.initData()
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_edit_book.setOnClickListener {
            tv_edit_book.isSelected = !tv_edit_book.isSelected
            if (tv_edit_book.isSelected) {
                tv_edit_book.text = "完成编辑"
            } else {
                tv_edit_book.text = "编辑"
            }
            bookAdapter.isEdit = tv_edit_book.isSelected
        }
        srl_books.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        presenter.getMyBooks(onError = {
            srl_books.isRefreshing = false
        }) {
            bookAdapter.setData(it)
            tv_num.text = "共${it.size}本图书"
            srl_books.isRefreshing = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        intentResult?.let {
            it.contents?.let {
                val dataMap = hashMapOf<String, String>()
                dataMap.put("qrcode", it)
                scanTo(dataMap)
            }
        }
    }

    fun remove(pos: Int) {
        bookAdapter.remove(pos)
    }
}