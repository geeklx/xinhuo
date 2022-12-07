package com.spark.peak.ui.common.login

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.UserInfo
import com.spark.peak.utlis.SpUtil

/**
 * 创建者：
 * 时间：
 */
class LoginPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun login_3(login: Map<String, String>, onNext: (Int) -> Unit, onError: () -> Unit) {
        api.login(login).subs({ results ->
            if (results.body == null) {
                onNext(results.ret)
                return@subs
            }
            SpUtil.user = results.body
            userInfo { info ->
                saLogin(info, results.body.userId ?: "")
                SpUtil.userInfo = info
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