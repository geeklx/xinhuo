package com.spark.peak.ui.mine.order.comment

import android.view.View
import com.bumptech.glide.Glide
import com.spark.peak.R
import com.spark.peak.base.EventMsg
import com.spark.peak.base.LifeActivity
import com.spark.peak.widegt.StarBar
import kotlinx.android.synthetic.main.activity_order_comment.*
import org.greenrobot.eventbus.EventBus

class OrderCommentActivity(override val layoutResId: Int = R.layout.activity_order_comment) : LifeActivity<OrderCommentPresenter>() {
    override val presenter = OrderCommentPresenter(this)
    private val key by lazy { intent.getStringExtra(KEY)?:"" }
    private val position by lazy { intent.getIntExtra(POSITION, -1) }
    private var starNum = 5.0f
    override fun configView() {
//        tv_original_price.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        sb_star.setOnStarChangeListener(object : StarBar.OnStarChangeListener {
            override fun onStarChange(mark: Float) {
                starNum = mark
            }

        })
    }

    override fun initData() {
        presenter.loadDetail(key ?: "") {
            Glide.with(this).load(it.coverimg).placeholder(R.drawable.default_lesson).into(iv_img)
            tv_name.text = it.title
            tv_lesson.text = "${it.period ?: "0"}课时"
            tv_purchasers.text = "${it.buyers ?: 0}人已学"
//            tv_original_price.text = "￥${it.price ?: "0"}"
//            tv_price.text = "免费"
        }
    }

    fun submit(v: View) {
        val trim = et_content.text.toString().trim()
        if (trim.isBlank()) {
            mToast("评论不能为空")
            return
        }
//        mToast("咩有接口")
//        onBackPressed()
        presenter.comment(key, trim) {
            mToast("评论成功")
            EventBus.getDefault().post(EventMsg("order_comment", position))
            onBackPressed()
        }
    }

    companion object {
        const val KEY = "key"
        const val POSITION = "position"
    }
}
