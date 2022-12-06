package tuoyan.com.xinghuo_dayingindex.ui.practice.special

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_sp_catalog.*
import org.jetbrains.anko.ctx
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.special.SpecialDataManager.questionList
import tuoyan.com.xinghuo_dayingindex.ui.practice.special.adapter.CataolgAdapter

class SpCatalogActivity : BaseActivity() {
    override val layoutResId: Int
        get() = R.layout.activity_sp_catalog

    override fun configView() {
        setSupportActionBar(tb_sp_catalog)
        rv_catalog.layoutManager = LinearLayoutManager(ctx)
    }

    override fun handleEvent() {
        tb_sp_catalog.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        var mAdapter = CataolgAdapter{position, item->
            var intent = Intent()
            intent.putExtra("data",item)
            intent.putExtra("position",position)
            when {
                item.questionType=="6" -> setResult(202,intent)
                item.userPracticeKey.isNullOrEmpty() -> //TODO 不存在userPracticeKey，属于未做的习题
                    setResult(200,intent)
                else -> setResult(201,intent)
            }
            finish()
        }

        mAdapter.setData(questionList)
        rv_catalog.adapter = mAdapter
    }


}
