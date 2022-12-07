package com.spark.peak.ui.study

import android.Manifest
import android.app.AlertDialog
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.spark.peak.base.LifeFragment
import com.spark.peak.ui.scan.ScannerActivity
import com.spark.peak.ui.study.adapter.MyBooksAdapter
import com.spark.peak.ui.study.book.BookDetailActivity
import com.spark.peak.utlis.PermissionUtlis
import com.spark.peak.utlis.SpUtil
import com.zxing.google.zxing.integration.android.IntentIntegrator
import org.jetbrains.anko.dip
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.verticalLayout
import kotlin.properties.Delegates

/**
 * Created by 李昊 on 2018/4/20.
 */
class MyBooksFragment : LifeFragment<StudyPresenter>() {
    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int = 0
    private var rv_books  by Delegates.notNull<RecyclerView>()
    private var srl_books  by Delegates.notNull<SwipeRefreshLayout>()

    private var oAdapter = MyBooksAdapter(true, true, {
        Scan()
    }, {
        startActivity<BookDetailActivity>(BookDetailActivity.KEY to it, BookDetailActivity.TYPE to "2")
    }) { key, position ->
        AlertDialog.Builder(this.requireContext()).setMessage("确定要删除该图书吗？").setPositiveButton("确定", { _, _ ->
            var map = HashMap<String, String>()
            map.put("key", key)
            presenter.deleteMyBooks(map) {
                toast(it)
                removeView(position)
            }
        }).setNegativeButton("取消", { dialog, _ ->
            dialog.dismiss()
        }).create().show()
    }

    /**
     * 删除列表中的图书
     */
    fun removeView(position: Int){
        oAdapter.remove(position)
    }
    override fun initView(): View? = UI {
        verticalLayout {
            leftPadding = dip(15)

            srl_books = swipeRefreshLayout {
                rv_books = recyclerView {
                    lparams(matchParent, matchParent)
                }
            }.lparams(matchParent, matchParent)

        }

    }.view

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        if (SpUtil.isLogin){
            getData()
        }else{
            oAdapter.setData(ArrayList())
            oAdapter.notifyDataSetChanged()
        }
    }
    override fun configView(view: View?) {
        rv_books.layoutManager = GridLayoutManager(this.requireContext(), 3)
        rv_books.adapter = oAdapter
        srl_books.setOnRefreshListener {
            refreshData()
        }
    }

    private fun Scan (){
        PermissionUtlis.checkPermissions(this, Manifest.permission.CAMERA) {
            IntentIntegrator(activity)
                    .setOrientationLocked(false)
                    .setCaptureActivity(ScannerActivity::class.java)
                    .setPrompt("将二维码放入框内，即可自动扫描")
                    .initiateScan()
        }
    }

    private fun getData(){
        presenter.getMyBooks {
            oAdapter.setData(it)

//        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
//            override fun getSpanSize(position: Int): Int {
//                if (position == 0){
//                    return gridLayoutManager.spanCount
//                }
//                return 1
//            }
//
//        }
            rv_books.adapter = oAdapter
        }
    }

    private fun refreshData(){
        presenter.getMyBooks (onError = {
            srl_books.isRefreshing = false
        }){
            oAdapter.setData(it)

//        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
//            override fun getSpanSize(position: Int): Int {
//                if (position == 0){
//                    return gridLayoutManager.spanCount
//                }
//                return 1
//            }
//
//        }
            rv_books.adapter = oAdapter
            srl_books.isRefreshing = false
        }
    }
}