package tuoyan.com.xinghuo_dayingindex.ui.common.changePhone

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil


/**
 * 创建者：
 * 时间：
 */
class ChangePhonePresenter(progress: OnProgress) : BasePresenter(progress) {


    fun bindPhone(smsCode: Map<String, String>, onNext: (Int) -> Unit, onError: () -> Unit) {
        api.bindPhone(smsCode).sub({ it ->
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
                    changeUserInfo(mutableMapOf("grade" to SpUtil.defaultGrade.id)) {
                        onNext(results.ret)
                    }
                else
                    onNext(results.ret)
            }
        }) { onError() }
    }

    fun changePhone(smsCode: Map<String, String>, onNext: (Int) -> Unit, onError: () -> Unit) {
        api.changePhone(smsCode).sub({ it ->
            onNext(it.ret)
        }) { onError() }
    }

}