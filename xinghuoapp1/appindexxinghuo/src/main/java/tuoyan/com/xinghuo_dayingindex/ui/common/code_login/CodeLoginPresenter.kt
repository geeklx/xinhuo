package tuoyan.com.xinghuo_dayingindex.ui.common.code_login

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.LoginResponse
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil


/**
 * 创建者：
 * 时间：
 */
class CodeLoginPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun login(login: Map<String, String>, onNext: (Int) -> Unit, onError: () -> Unit) {
        api.login(login).sub({ it ->
            if (it.body == null) {
                onNext(it.ret)
                return@sub
            }
            val results = it
            SpUtil.user = it.body ?: LoginResponse()
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