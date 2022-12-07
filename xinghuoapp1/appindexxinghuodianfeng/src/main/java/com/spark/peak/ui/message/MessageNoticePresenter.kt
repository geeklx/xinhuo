package com.spark.peak.ui.message

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.MsgList

/**
 * 创建者：
 * 时间：
 */
class MessageNoticePresenter(progress: OnProgress) : BasePresenter(progress) {
    fun loadData(isMsg: Boolean, onNext: (List<Map<String, String>>) -> Unit) {
        if (isMsg) {
            api.feedbacks().sub(onNext = {
                onNext(it.body.list)
//                api.cancel().sub()
            })
        } else {
            api.notices().sub(onNext = {
                onNext(it.body)
//                api.cancel(1).sub()
            })
        }
    }

    fun getMessage(page: Int, step: Int, onNext: (MsgList) -> Unit, error: () -> Unit) {
        api.getMessage(page, step).sub({ onNext(it.body) }) { error() }
    }

}