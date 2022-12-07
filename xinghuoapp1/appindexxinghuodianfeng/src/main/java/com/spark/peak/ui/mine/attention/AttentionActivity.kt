package com.spark.peak.ui.mine.attention

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.bean.Attention
import com.spark.peak.ui.mine.homepage.HomepageActivity
import kotlinx.android.synthetic.main.activity_attentiondf.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

/**
 * 创建者：
 * 时间：
 */
class AttentionActivity(override val layoutResId: Int = R.layout.activity_attentiondf)
    : LifeActivity<AttentionPresenter>() {
    override val presenter by lazy { AttentionPresenter(this) }
    private val adapter = AttentionAdapter({ position, item, isFollow ->
        follow(position, isFollow, item)
    }) {
        startActivity<HomepageActivity>(HomepageActivity.URL to
                "${WEB_BASE_URL}mine/personalpage?id=$it")
    }

    override fun configView() {
        rv_attention.layoutManager = LinearLayoutManager(ctx)
        val itemDecoration = DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(resources.getDrawable(R.drawable.divider))
        rv_attention.addItemDecoration(itemDecoration)
        rv_attention.adapter = adapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        presenter.getAttentionList {
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