//package tuoyan.com.xinghuo_daying.ui.mine.history
//
//import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
//import tuoyan.com.xinghuo_dayingindex.base.OnProgress
//import tuoyan.com.xinghuo_dayingindex.bean.ExerciseHistory
//import tuoyan.com.xinghuo_dayingindex.bean.Report
//import tuoyan.com.xinghuo_dayingindex.bean.WrongBookDate
//import tuoyan.com.xinghuo_dayingindex.net.Results
//
//class HistoryPresenter(onProgress: OnProgress) : BasePresenter(onProgress) {
//
//
//    fun paperHistoryTimes(onNext :(List<WrongBookDate>) -> Unit){
//        api.paperHistoryTimes().sub({ onNext(it.body) }  )
//    }
//
//    /**
//     * 获取练习记录
//     */
//    fun history(page: Int,year:String, onNext: (Results<List<ExerciseHistory>>) -> Unit) {
//        api.historyPractice(page, year).sub(onNext)
//    }
//
//    /**
//     * 清空练习记录
//     */
//    fun clearExerciseHistory(onNext: () -> Unit) {
//        api.clearExHistory().sub(onNext = { onNext() })
//    }
//
//}