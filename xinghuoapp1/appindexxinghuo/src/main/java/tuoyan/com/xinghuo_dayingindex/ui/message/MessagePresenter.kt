package tuoyan.com.xinghuo_dayingindex.ui.message

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

/**
 * 创建者：
 * 时间：
 */
class MessagePresenter(progress: OnProgress) : BasePresenter(progress) {

    fun notices(page: Int, onNext: (MMMM) -> Unit,onError:()->Unit) {
        api.notices(page = page).sub(onNext = { onNext(it.body) },onError = {onError()})
    }
    fun deletedMessage(key:String, onNext: () -> Unit) {
        api.deletedMessage(key,SpUtil.user.userId?:"").sub(onNext = { onNext() })
    }
}