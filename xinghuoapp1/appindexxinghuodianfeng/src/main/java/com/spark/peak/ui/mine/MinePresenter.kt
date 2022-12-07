package com.spark.peak.ui.mine

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.HomePageInfo

/**
 * 创建者：
 * 时间：
 */
class MinePresenter(progress: OnProgress) : BasePresenter(progress) {
    fun updated(onNext: (HomePageInfo) -> Unit) {
        api.getHomePageProfile().sub({ onNext(it.body) })
    }

    fun getMsgReaded(onNext: (Map<String, String>) -> Unit) {
        api.getMsgReaded().sub({ onNext(it.body) })
    }
}