package com.spark.peak.ui.cg


import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.dialog.AlertDialog
import com.spark.peak.ui.dialog.CGCompleteDialog
import com.spark.peak.ui.practice.PapersActivity
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_cg_pass.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class CGPassActivity(override val layoutResId: Int = R.layout.activity_cg_pass) : LifeActivity<CGPresenter>() {
    override val presenter by lazy { CGPresenter(this) }
    private val key by lazy { intent.getStringExtra(KEY)?:"" }
    private val title by lazy { intent.getStringExtra(TITLE)?:"" }
    private val adapter by lazy {
        CGPassAdapter {
            startActivity<PapersActivity>(PapersActivity.KEY to (it["missionlevelkey"] ?: ""),
                    PapersActivity.TITLE to (it["name"] ?: ""),
                    PapersActivity.QUEUE to ((it["workorder"] ?: "") == "1"))
        }
    }

    override fun configView() {
        rv_cg_pass.layoutManager = LinearLayoutManager(ctx)
        tv_title.text = title
        rv_cg_pass.adapter = adapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
//
//    override fun initData() {
////        adapter.setData(mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
//        presenter.cgPass(key) {
//            adapter.setData(it.body.levelList)
////            adapter.setMore(adapter.getDateCount() < it.totalCount)
//        }
//    }

    override fun onResume() {
        super.onResume()
        presenter.cgPass(key) {
            adapter.setData(it.body.levelList)
//            adapter.setMore(adapter.getDateCount() < it.totalCount)
            if (it.body.isshow?.toInt() == 0 && it.body.isfinish?.toInt() == 1) {
                CGCompleteDialog(this).show()
                presenter.wordShowed(key)
            }
        }
    }
    fun q(v: View) {// : 2018/8/7 9:22 霍述雷  任务说明 http://10.100.10.2/exercise/GameInstruction/?missionKey=584611311745686272
        startActivity<PostActivity>(PostActivity.URL to "${WEB_BASE_URL}exercise/gameinstruction?missionKey=$key",
                PostActivity.TITLE to "任务说明")
    }

    companion object {
        const val KEY = "key"
        const val TITLE = "title"
    }
}
