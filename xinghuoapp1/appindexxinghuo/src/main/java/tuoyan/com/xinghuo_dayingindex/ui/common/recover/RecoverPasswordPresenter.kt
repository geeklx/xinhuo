package tuoyan.com.xinghuo_dayingindex.ui.common.recover

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.Register

/**
 * 创建者：
 * 时间：
 */
class RecoverPasswordPresenter(progress: OnProgress) : BasePresenter(progress) {

    fun findPwd(phone: String, code: String, pwd: String, onNext: () -> Unit, onError: () -> Unit) {
        val register = Register(phone, code,pwd)
        api.findPwd(register).sub({ onNext() }) { onError() }
    }

}