package tuoyan.com.xinghuo_dayingindex.ui.mine

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.Eval
import tuoyan.com.xinghuo_dayingindex.bean.HomePageInfo
import tuoyan.com.xinghuo_dayingindex.net.Results

/**
 * 创建者：
 * 时间：
 */
class MinePresenter(progress: OnProgress) : BasePresenter(progress) {
    fun updated(onNext: (HomePageInfo) -> Unit) {
        api.getHomePageProfile().sub({onNext(it.body)})
    }
    fun userEvalList(page:Int,onNext: (Results<List<Eval>>) -> Unit){
        api.userEvalList(page).sub(onNext = {onNext(it)})
    }
}