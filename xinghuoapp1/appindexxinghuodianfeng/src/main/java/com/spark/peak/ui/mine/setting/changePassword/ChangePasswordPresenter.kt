package com.spark.peak.ui.mine.setting.changePassword

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Register
import com.spark.peak.bean.SMSCode

/**
 * 创建者：
 * 时间：
 */
class ChangePasswordPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun sendCode(smsCode: SMSCode, onNext: () -> Unit, onError: () -> Unit) {
        api.getSms(smsCode).sub({ onNext() }) { onError() }
    }
    fun findPwd(register: Register, onNext: () -> Unit, onError: () -> Unit) {
        api.findPwd(register).sub({ onNext() }) { onError() }
    }
}