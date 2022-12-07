package com.spark.peak.ui.mine.order.comment

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.NetLesson
import com.spark.peak.bean.Order

/**
 * 创建者：
 * 时间：
 */
class OrderCommentPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun loadDetail(key: String, onNext: (NetLesson) -> Unit) {
        api.getNetLesson(key).sub({ onNext(it.body) })
    }

    fun comment(key: String, content: String, onNext: () -> Unit) {
        api.addComment(mutableMapOf("targetkey" to key, "content" to content,"type" to "4"),"DDGL").sub({ onNext() }) //赵志坤让改的
    }
}