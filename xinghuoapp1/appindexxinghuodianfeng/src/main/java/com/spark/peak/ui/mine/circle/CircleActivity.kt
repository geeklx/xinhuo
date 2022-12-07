package com.spark.peak.ui.mine.circle

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.bean.Circle
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.mine.homepage.HomepageActivity
import kotlinx.android.synthetic.main.activity_circle.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

/**
 * 创建者：
 * 时间：
 */
class CircleActivity(override val layoutResId: Int = R.layout.activity_circle)
    : LifeActivity<CirclePresenter>() {
    override val presenter by lazy { CirclePresenter(this) }
    private val adapter = CircleAdapter ({ position, item, isFollow ->
        follow(position, isFollow, item)
    }) {
        startActivity<PostActivity>(PostActivity.URL to
                "${WEB_BASE_URL}community/circle/circleDetails?key=$it",PostActivity.TITLE to "圈子详情")
    }


    override fun configView() {
        rv_circle.layoutManager = LinearLayoutManager(ctx)
        val itemDecoration = DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(resources.getDrawable(R.drawable.divider))
        rv_circle.addItemDecoration(itemDecoration)
        rv_circle.adapter = adapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        presenter.getCircleList {
            adapter.setData(it)
        }
    }

    private fun follow(position: Int, isFollow: Boolean, item: Circle) {
        if (isFollow) {
            presenter.delCircle(item.key) {
                item.isFollow = false
                adapter.notifyItemChanged(position)
            }
        } else {
            presenter.addCircle(item.key) {
                item.isFollow = true
                adapter.notifyItemChanged(position)
            }
        }
    }
}