package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.report

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.EvalReport


/**
 * 创建者：
 * 时间：
 */
class ReportPresenter(progress: OnProgress) : BasePresenter(progress) {
    /**
     * 获取历史习题练习报告
     */
    fun evalReport(evalkey: String, paperkey: String, userpractisekey: String, onNext: (EvalReport) -> Unit) {
        api.evalReport(evalkey, paperkey, userpractisekey).sub({ onNext(it.body) })
    }
}