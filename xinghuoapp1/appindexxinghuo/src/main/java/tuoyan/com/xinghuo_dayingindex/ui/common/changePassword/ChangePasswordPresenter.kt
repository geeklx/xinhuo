package tuoyan.com.xinghuo_dayingindex.ui.common.changePassword

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.Register

/**
 * 创建者：
 * 时间：
 */
class ChangePasswordPresenter(progress: OnProgress) : BasePresenter(progress) {

    fun findPwd(register: Register, onNext: () -> Unit, onError: () -> Unit) {
        api.findPwd(register).sub({ onNext() }) { onError() }
    }
}