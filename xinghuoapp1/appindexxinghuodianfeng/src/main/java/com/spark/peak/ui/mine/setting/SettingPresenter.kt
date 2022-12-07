package com.spark.peak.ui.mine.setting

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress

/**
 * 创建者：
 * 时间：
 */
class SettingPresenter(progress: OnProgress) : BasePresenter(progress) {


    fun logout() {
        api.logout().subNoLife()

    }

    fun cancellation(onNext: () -> Unit) {
        api.cancellation().subs({ onNext() })
    }

}