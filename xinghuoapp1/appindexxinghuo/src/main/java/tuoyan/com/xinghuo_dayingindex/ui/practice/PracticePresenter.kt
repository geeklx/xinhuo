package tuoyan.com.xinghuo_dayingindex.ui.practice

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
import tuoyan.com.xinghuo_dayingindex.bean.RealListItem
import tuoyan.com.xinghuo_dayingindex.bean.SpecialInfos

class PracticePresenter(onProgress: OnProgress): BasePresenter(onProgress){
    fun getSpecialInfo(gradeKey: String, paperType: String, onNext: (SpecialInfos) -> Unit){
        api.getSpecialInfo(gradeKey, paperType).sub( {onNext(it.body)} )
    }

    fun getRealList(gradeKey: String, onNext: (List<RealListItem>) -> Unit){
        api.getRealList(gradeKey,"4").sub( {onNext(it.body)} )
    }

    ///////////////////专项练习/////////////////////////////////////////////////

    /**
     * 获取专项练习题目详情
     * 所有题目的完整列表
     */
    fun getSpQuestions(practisekey: String, groupkey: String, paperType: String, onNext :(List<ExerciseFrameItem>) -> Unit){
        api.getSpQuestions(practisekey, groupkey, paperType).subs({ onNext(it.body) })
    }

}