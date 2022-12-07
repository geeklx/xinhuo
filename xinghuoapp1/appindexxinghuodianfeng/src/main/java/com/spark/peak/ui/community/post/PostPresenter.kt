package com.spark.peak.ui.community.post

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.BookResList

/**
 * 创建者：
 * 时间：
 */
class PostPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun supportingResourceList(key: String,onNext:(BookResList)->Unit) {
        api.supportingResourceList("7", "2", key).sub ({
            onNext(it.body)
        })
    }

}