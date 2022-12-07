package com.spark.peak.ui.study.config

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.MyBookNetClass
import com.spark.peak.ui.lesson.LessonsActivity
import com.spark.peak.ui.scan.ScannerActivity
import com.spark.peak.ui.study.StudyPresenter
import com.spark.peak.ui.study.adapter.MyBooksAdapter
import com.spark.peak.ui.study.adapter.MyNetclassAdapter
import com.spark.peak.ui.study.adapter.StudyBaseAdapter
import com.spark.peak.ui.study.book.BookDetailActivity
import com.spark.peak.utlis.PermissionUtlis
import com.zxing.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_books_configdf.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class BooksConfigActivity : LifeActivity<StudyPresenter>() {
    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_books_configdf


    private val isBooks by lazy { intent.getBooleanExtra(IS_BOOKS, true) }
    private val dataList by lazy { intent.getSerializableExtra(DATA) as ArrayList<MyBookNetClass> }

    private var oAdapter by Delegates.notNull<StudyBaseAdapter<MyBookNetClass>>()

    companion object {
        var IS_BOOKS = "type"
        var DATA = "dataList"
    }

    override fun configView() {
        tv_title.text = if (isBooks) "书架" else "网课"
        btn_add.text = if (isBooks) "添加图书" else "添加网课"

        setSupportActionBar(tb_books_config)
    }

    override fun initData() {
        if (isBooks) {
            oAdapter = MyBooksAdapter(true, false, {
                btn_add.performClick()
            }, {

            }){ key, position ->
                AlertDialog.Builder(ctx).setMessage("确定要删除该图书吗？").setPositiveButton("确定",{_,_ ->
                    var map = HashMap<String,String>()
                    map.put("key",key)
                    presenter.deleteMyBooks(map){
                        toast(it)
                        oAdapter.remove(position)
                    }
                }).setNegativeButton("取消",{ dialog,_ ->
                    dialog.dismiss()
                }).create().show()
            }
            rv_config_books.layoutManager = GridLayoutManager(ctx, 3)
        } else {
            oAdapter = MyNetclassAdapter(true, false, {
                btn_add.performClick()
            },{
                // TODO: 2018/5/15 11:24 霍述雷 hdfdsahgfkdsgfkjhsa
            }){ key, position ->
                AlertDialog.Builder(ctx).setMessage("确定要删除该网课吗？").setPositiveButton("确定",{_,_ ->
                    var map = HashMap<String,String>()
                    map.put("key",key)
                    presenter.deleteMyNetClass(map){
                        toast(it)
                        oAdapter.remove(position)
                    }
                }).setNegativeButton("取消",{ dialog,_ ->
                    dialog.dismiss()
                }).create().show()
            }
            rv_config_books.layoutManager = GridLayoutManager(ctx, 2)
        }
        oAdapter.setData(dataList)
        rv_config_books.adapter = oAdapter
    }

    override fun handleEvent() {
        tb_books_config.setNavigationOnClickListener {
            onBackPressed()
        }
        tv_config.setOnClickListener {
            if (oAdapter.isConfig) {
                oAdapter.isConfig = false
                tv_config.text = "编辑"
                btn_add.visibility = View.VISIBLE
            } else {
                oAdapter.isConfig = true
                tv_config.text = "完成"
                btn_add.visibility = View.GONE
            }
            oAdapter.notifyDataSetChanged()
        }
        btn_add.setOnClickListener {
            if (isBooks) {
                PermissionUtlis.checkPermissions(this, Manifest.permission.CAMERA) {
                    IntentIntegrator(this)
                            .setOrientationLocked(false)
                            .setCaptureActivity(ScannerActivity::class.java)
                            .setPrompt("将二维码放入框内，即可自动扫描")
                            .initiateScan()
                }
            } else {
                startActivity<LessonsActivity>()
            }
        }

        srl_config.setOnRefreshListener {
            if (isBooks){
                presenter.getMyBooks {
                    srl_config.isRefreshing = false
                    oAdapter.setData(it)
                    rv_config_books.adapter = oAdapter
                }
            }else{
                presenter.getMyNetClass {
                    srl_config.isRefreshing = false
                    oAdapter.setData(it)
                    rv_config_books.adapter = oAdapter
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        intentResult?.let {
            it.contents?.let {
                val dataMap = HashMap<String, String>()
                dataMap.put("qrcode", it)
                scanTo(dataMap)
            }
        }

        var resultData = data.getStringExtra("code")
        resultData?.let {
            var data = HashMap<String,String>()
            data.put("qrcode",it)
            presenter.getDataByScan(data){
                it.key?.let {
                    startActivity<BookDetailActivity>(BookDetailActivity.KEY to it)
                }
            }
        }
    }

}
