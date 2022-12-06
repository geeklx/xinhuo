package tuoyan.com.xinghuo_dayingindex.ui.mine.setting

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress

/**
 * 创建者：
 * 时间：
 */
class SettingPresenter(progress: OnProgress) : BasePresenter(progress) {


    fun logout() {
        api.logout().subNoLife()

    }

    fun deleteUser(onNext: () -> Unit) {
        api.deleteUser().sub({ onNext() })
    }

}