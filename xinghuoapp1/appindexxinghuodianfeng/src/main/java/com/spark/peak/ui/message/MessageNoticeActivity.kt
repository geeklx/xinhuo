package com.spark.peak.ui.message

import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.MsgBean
import com.spark.peak.bean.WrongBook
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.netLessons.NetLessonsActivity
import com.spark.peak.ui.wrongbook.WrongDetailActivity
import kotlinx.android.synthetic.main.activity_message_noticedf.*
import org.jetbrains.anko.startActivity

/**
 * 创建者：
 * 时间：
 */
class MessageNoticeActivity(override val layoutResId: Int = R.layout.activity_message_noticedf) : LifeActivity<MessageNoticePresenter>() {
    override val presenter by lazy { MessageNoticePresenter(this) }
    private var page = 1
    private var step = 20
    private val adapter = MessageNoticeAdapter({
        goTo(it)
    }) {
        page++
        getMessage()
    }

    private fun goTo(item: MsgBean) {
        if ("1" == item.source) { //消息
            when (item.targetType) {
                "4" -> {
                    startActivity<NetLessonsActivity>(NetLessonsActivity.KEY to item.targetUrl)
                }
                "7" -> {
                    startActivity<PostActivity>(PostActivity.URL to item.targetUrl, PostActivity.TITLE to item.title)
                }
                "8" -> {
                    val wrongBook = WrongBook()
                    val target = item.targetUrl.split("####")
                    wrongBook.paperkey = item.targetKey
                    wrongBook.questionkey = target[0]
                    wrongBook.questiontype = target[1]
                    startActivity<WrongDetailActivity>(WrongDetailActivity.TYPE to "1", WrongDetailActivity.WRONG_ITEM to wrongBook)
                }
            }
        } else if ("2" == item.source) {//通知
            when (item.targetType) {
                "1" -> {
                    startActivity<NetLessonsActivity>(NetLessonsActivity.KEY to item.targetUrl)
                }
                else -> {
                    startActivity<PostActivity>(PostActivity.URL to item.targetUrl, PostActivity.TITLE to item.title)
                }
            }
        }
    }

    override fun configView() {
        rlv_message.layoutManager = LinearLayoutManager(this)
        rlv_message.adapter = adapter
    }

    override fun initData() {
        refresh()
    }

    fun refresh() {
        page = 1
        getMessage()
    }

    fun getMessage() {
        presenter.getMessage(page, step, {
            if (page > 1) {
                adapter.addData(it.list)
            } else {
                adapter.setData(it.list)
            }
            srfl.isRefreshing = false
        }) {
            srfl.isRefreshing = false
        }
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        srfl.setOnRefreshListener {
            srfl.isRefreshing = true
            refresh()
        }

    }
}