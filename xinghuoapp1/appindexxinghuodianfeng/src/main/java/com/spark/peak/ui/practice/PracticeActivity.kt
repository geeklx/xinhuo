package com.spark.peak.ui.practice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.spark.peak.R
import com.spark.peak.base.BaseActivity
import com.spark.peak.ui.practice.adapter.PapersAdapter
import kotlinx.android.synthetic.main.activity_practicedf.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class PracticeActivity : BaseActivity() {
    override val layoutResId: Int
        get() = R.layout.activity_practicedf

    @RequiresApi(Build.VERSION_CODES.O)
    override fun configView() {
        setSupportActionBar(tb_practice)

        rv_papers.layoutManager = LinearLayoutManager(ctx)

        var oAdapter = PapersAdapter(false){
//            startActivity<PapersActivity>()
        }
//        var dataList = mutableListOf<Int>()
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//        dataList.add(1)
//
//        oAdapter.setData(dataList)

        rv_papers.adapter = oAdapter

    }

    override fun handleEvent() {
        tb_practice.setNavigationOnClickListener { onBackPressed() }
    }

    fun onSubjectClick(view : View){
        var title = ""
        when(view.id){
            R.id.tv_kouyu -> title = "口语"
            R.id.tv_tingli -> title = "听力"
            R.id.tv_yuedu -> title = "阅读"
            R.id.tv_cihui -> title = "词汇"
            R.id.tv_xiezuo -> title = "写作"
            R.id.tv_fanyi -> title = "翻译"
        }
        startActivity<PapersActivity>(PapersActivity.TITLE to title)
    }
}
