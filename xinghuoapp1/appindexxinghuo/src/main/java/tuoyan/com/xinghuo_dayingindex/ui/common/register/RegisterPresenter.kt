package tuoyan.com.xinghuo_dayingindex.ui.common.register

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建者：
 * 时间：
 */
class RegisterPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun register(register: Map<String,String>, onNext: () -> Unit, onError: () -> Unit) {
        api.register(register).sub({ login ->
            try {
                SensorsDataAPI.sharedInstance().login(login.body.userId ?: "");
                val property = JSONObject()
                property.put("source_registration", "普通注册")
                SensorsDataAPI.sharedInstance().track("registe_front_application", property)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            SpUtil.user = login.body
            userInfo {
                try {
                    val properties = JSONObject()
                    properties.put("phone_number", it.phone)
                    properties.put("xinghuo_userid", login.body.userId)
                    properties.put("user_nickname", it.name)
                    properties.put("register_time", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE).format(Date()))
                    SensorsDataAPI.sharedInstance().profileSet(properties)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                SpUtil.userInfo = it
                SpUtil.isLogin = true
                onNext()
            }
        }) { onError() }

    }



}