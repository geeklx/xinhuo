//package tuoyan.com.xinghuo_daying.ui.exercise.history
//
//import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
//import tuoyan.com.xinghuo_dayingindex.base.OnProgress
//import tuoyan.com.xinghuo_dayingindex.bean.ExerciseBody
//import tuoyan.com.xinghuo_dayingindex.bean.ExerciseHistory
//import tuoyan.com.xinghuo_dayingindex.bean.Report
//
//
//class ExerciseListPresenter(onProgress: OnProgress) : BasePresenter(onProgress){
//    /**
//     * 获取练习记录
//     */
//    fun getExerciseHistory(page: Int, step: Int, onNext :(List<ExerciseHistory>) -> Unit){
//        api.getExHistory(page,step).sub({ onNext(it.body) }  )
//    }
//
//    /**
//     * 清空练习记录
//     */
//    fun clearExerciseHistory(onNext :(Any) -> Unit){
//        api.clearExHistory().sub({ onNext("") }  )
//    }
//
//}