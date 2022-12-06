package tuoyan.com.xinghuo_dayingindex.ui.mine.wrong

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.net.Results

/**
 * 创建者：
 * 时间：
 */
class WrongPresenter(progress: OnProgress) : BasePresenter(progress) {

    /**
     * 获取当前app下所有的学段与年级列表
     */
    fun getSectionAndGradeList(onNext: (List<Section>) -> Unit) {
        api.getSectionAndGradeList().sub({ onNext(it.body.list) })
    }

    /**
     * 获取科目列表
     */
    fun getSubjects(key : String,onNext: (List<Subject>) -> Unit){
        api.getWrongSubjects(key).sub({ onNext(it.body) })
    }

    /**
     * 获取存在错题的日期 ---> 年-月
     */
    fun getWrongDate(page: Int=0, step: Int=99, onNext: (List<WrongBookDate>) -> Unit){
        api.getWrongDate(page,step).sub({ onNext(it.body) })
    }


    /**
     * 获取指定日期下的错题列表
     */
    fun getWrongList(year: String, gradekey: String, subjectkey: String,
                     page: Int, step: Int=20, onNext: (Results<List<WrongBook>>) -> Unit){
        api.getWrongList(year,gradekey, subjectkey,page,step).sub({ onNext(it) })
    }

    /**
     * 获取题目解析
     * 错题详情页调用
     */
    fun getExerciseParsing(practisekey: String, questionkey: String,
                           questiontype: String, userpractisekey: String,
                           questionsort: String, onNext :(Any) -> Unit){

        api.getExerciseParsing(practisekey, questionkey, questiontype,
                userpractisekey,questionsort).sub({ onNext(it.body) }  )
    }

    /**
     * 移除错题本
     */
    fun deleteWrongItem(map: Map<String,String>, onNext :(Any) -> Unit){
        api.deleteWrongItem(map).sub({ onNext("") }  )
    }

    /**
     * 题目反馈
     */
    fun questionFB(map: Map<String,String>, onNext :(Any) -> Unit){
        api.questionFB(map).sub({ onNext(it) })
    }

}