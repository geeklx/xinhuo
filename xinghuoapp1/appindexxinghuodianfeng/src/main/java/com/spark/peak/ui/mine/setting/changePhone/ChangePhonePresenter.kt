package com.spark.peak.ui.mine.setting.changePhone

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.SMSCode
import com.spark.peak.bean.UserInfo
import com.spark.peak.utlis.SpUtil

/**
 * 创建者：
 * 时间：
 */
class ChangePhonePresenter(progress: OnProgress) : BasePresenter(progress) {
    fun sendCode(smsCode: SMSCode, onNext: () -> Unit, onError: () -> Unit) {
        api.getSms(smsCode).sub({ onNext() }) { onError() }
    }

    fun bindPhone(smsCode: Map<String, String>, onNext: (Int) -> Unit, onError: () -> Unit) {
        api.bindPhone(smsCode).sub({
            if (it.body == null) {
                onNext(it.ret)
                return@sub
            }
            SpUtil.user = it.body
            val results = it
            userInfo {
                SpUtil.userInfo = it
                SpUtil.isLogin = true
                if (SpUtil.userInfo.grade.isNullOrBlank())
                    changeUserInfo(UserInfo(grade = SpUtil.defaultGrade.id)) {
                        onNext(results.ret)
                    }
                else
                    onNext(results.ret)
            }
        }) { onError() }
    }

}