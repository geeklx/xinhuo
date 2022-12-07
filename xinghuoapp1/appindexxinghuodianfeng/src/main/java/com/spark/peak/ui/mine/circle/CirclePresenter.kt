package com.spark.peak.ui.mine.circle

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Circle

/**
 * 创建者：
 * 时间：
 */
class CirclePresenter(progress: OnProgress) : BasePresenter(progress) {
    fun getCircleList(onNext: (List<Circle>) -> Unit) {
        api.getCircleList().sub({ onNext(it.body) })
    }

    fun addCircle(key: String,onNext: () -> Unit) {
        api.addCircle(mutableMapOf("communitykey" to key)).sub({onNext()})
    }

    fun delCircle(key: String,onNext: () -> Unit) {
        api.delCircle(mutableMapOf("communitykey" to key)).sub({onNext()})
    }
}