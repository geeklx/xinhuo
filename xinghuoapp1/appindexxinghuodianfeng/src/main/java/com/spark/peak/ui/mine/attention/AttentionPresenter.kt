package com.spark.peak.ui.mine.attention

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Attention

/**
 * 创建者：
 * 时间：
 */
class AttentionPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun getAttentionList(onNext: (List<Attention>) -> Unit) {
        api.getAttentionList().sub({ onNext(it.body) })
    }

    fun attention(key: String, onNext: () -> Unit) {
        api.attention(mutableMapOf("userkey" to key)).sub({ onNext() })
    }

    fun removeAttention(key: String, onNext: () -> Unit) {
        api.removeAttention(mutableMapOf("userkey" to key)).sub({ onNext() })
    }

}