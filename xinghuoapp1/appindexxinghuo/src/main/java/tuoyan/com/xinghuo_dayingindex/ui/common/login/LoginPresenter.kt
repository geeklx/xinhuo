package tuoyan.com.xinghuo_dayingindex.ui.common.login
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.LoginResponse
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建者：
 * 时间：
 */
class LoginPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun login(login: Map<String, String>, onNext: (Int) -> Unit, onError: () -> Unit) {
        api.login(login).sub({ it ->
            if (it.body == null) {
                onNext(it.ret)
                return@sub
            }
            val results = it
            SpUtil.user = it.body ?: LoginResponse()
            try {
                SensorsDataAPI.sharedInstance().login(it.body.userId ?: "");
            } catch (e: Exception) {
            }
            userInfo {
                SpUtil.userInfo = it
                SpUtil.isLogin = true
                try {
                    val properties = JSONObject()
                    properties.put("phone_number", it.phone)
                    properties.put("user_nickname", it.name)
                    properties.put("xinghuo_userid", results.body.userId)
                    properties.put("last_login_time", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE).format(Date()))
                    SensorsDataAPI.sharedInstance().profileSet(properties)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (SpUtil.userInfo.grade.isNullOrBlank())
                    changeUserInfo(mutableMapOf("grade" to SpUtil.defaultGrade.id)) {
                        onNext(results.ret)
                    }
                else
                    onNext(results.ret)
            }
        }) { onError() }
    }

    fun login_3(login: Map<String, String>, onNext: (Int) -> Unit, onError: () -> Unit) {
        api.login(login).sub({ it ->
            if (it.body == null) {
                onNext(it.ret)
                return@sub
            }
            SpUtil.user = it.body
            val results = it
            SensorsDataAPI.sharedInstance().login(it.body.userId ?: "");
            userInfo {
                SpUtil.userInfo = it
                SpUtil.isLogin = true
                if (SpUtil.userInfo.grade.isNullOrBlank())
                    changeUserInfo(mutableMapOf("grade" to SpUtil.defaultGrade.id)) {
                        onNext(results.ret)
                    }
                else
                    onNext(results.ret)
            }
        }) { onError() }
    }
}