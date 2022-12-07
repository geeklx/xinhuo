package com.spark.peak.ui.mine.fans

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.bean.Attention
import com.spark.peak.ui.mine.homepage.HomepageActivity
import kotlinx.android.synthetic.main.activity_fans.*
import org.jetbrains.anko.*

/**
 * 创建者：
 * 时间：
 */
class FansActivity(override val layoutResId: Int = R.layout.activity_fans)
    : LifeActivity<FansPresenter>() {
    override val presenter by lazy { FansPresenter(this) }

    private val adapter = FansAdapter({ position, item, isFollow ->
        follow(position, isFollow, item)
    }) {
        startActivity<HomepageActivity>(HomepageActivity.URL to
                "${WEB_BASE_URL}mine/personalpage?id=$it")
    }

    override fun configView() {
        rv_fans.layoutManager = LinearLayoutManager(ctx)
        val itemDecoration = DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(resources.getDrawable(R.drawable.divider))
        rv_fans.addItemDecoration(itemDecoration)
        rv_fans.adapter = adapter
    }
    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
       /* val list = mutableListOf<Int>()
        list.add(1)
        list.add(1)
        list.add(1)
        list.add(1)
        adapter.setData(list)*/
        presenter.getFansList{
            adapter.setData(it)
        }

    }
    private fun follow(position: Int, isFollow: Boolean, item: Attention) {
        if (isFollow) {
            presenter.removeAttention(item.key) {
                item.isFollow = false
                adapter.notifyItemChanged(position)
            }
        } else {
            presenter.attention(item.key) {
                item.isFollow = true
                adapter.notifyItemChanged(position)
            }
        }
    }
}