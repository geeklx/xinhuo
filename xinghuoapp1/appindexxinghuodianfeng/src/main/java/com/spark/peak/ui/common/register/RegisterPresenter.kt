package com.spark.peak.ui.common.register

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.SMSCode
import com.spark.peak.bean.UserInfo
import com.spark.peak.utlis.SpUtil
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建者：
 * 时间：
 */
class RegisterPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun register(register: Map<String, String>, onNext: () -> Unit, onError: () -> Unit) {
//        api.register(register).sub({
//            var aa = ""
//            onNext()
//        }, {
//            onError()
//        })
        api.register(register).sub({ results ->
            SpUtil.user = results.body
            userInfo { info ->
                saRegister(info, results.body.userId ?: "")
                SpUtil.userInfo = info
                SpUtil.isLogin = false
                onNext()
            }
        }) {
            onError()
        }
    }

    fun sendCode(smsCode: SMSCode, onNext: () -> Unit, onError: () -> Unit) {
        api.getSms(smsCode).sub({ onNext() }) { onError() }
    }

    private fun saRegister(info: UserInfo, userId: String) {
        try {
            SensorsDataAPI.sharedInstance().login(userId);
            val property = JSONObject()
            property.put("source_registration", "普通注册")
            SensorsDataAPI.sharedInstance().track("registe_front_application", property)
            val properties = JSONObject()
            properties.put("phone_number", info.phone)
            properties.put("xinghuo_userid", userId)
            properties.put("user_nickname", info.name)
            properties.put(
                "register_time",
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE).format(
                    Date()
                )
            )
            SensorsDataAPI.sharedInstance().profileSet(properties)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}