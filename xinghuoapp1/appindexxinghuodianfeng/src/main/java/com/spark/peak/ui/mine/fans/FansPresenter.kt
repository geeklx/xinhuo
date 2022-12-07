package com.spark.peak.ui.mine.fans

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Attention

/**
 * 创建者：
 * 时间：
 */
class FansPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun getFansList(onNext: (List<Attention>) -> Unit) {
        api.getFansList().sub({ onNext(it.body) })
    }

    fun attention(key: String, onNext: () -> Unit) {
        api.attention(mutableMapOf("userkey" to key)).sub({ onNext() })
    }

    fun removeAttention(key: String, onNext: () -> Unit) {
        api.removeAttention(mutableMapOf("userkey" to key)).sub({ onNext() })
    }
}