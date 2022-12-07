package com.spark.peak.ui.wrongbook

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Section
import com.spark.peak.bean.Subject
import com.spark.peak.bean.WrongBook
import com.spark.peak.bean.WrongBookDate

class WrongBookPresenter (progress: OnProgress) : BasePresenter(progress){

    /**
     * 获取当前app下所有的学段与年级列表
     */
    fun getSectionAndGradeList(onNext: (List<Section>) -> Unit) {
        api.getSectionAndGradeList().sub({ onNext(it.body.list) })
    }

    /**
     * 获取科目列表
     */
    fun getSubject(key : String,onNext: (List<Subject>) -> Unit){
        api.getWrongSubjects(key).sub({ onNext(it.body) })
    }

    /**
     * 获取存在错题的日期 ---> 年-月
     */
    fun getWrongDate(page: Int, step: Int, onNext: (List<WrongBookDate>) -> Unit){
        api.getWrongDate(page,step).sub({ onNext(it.body) })
    }


    /**
     * 获取指定日期下的错题列表
     */
    fun getWrongList(year: String, gradekey: String, subjectkey: String,
                     page: Int, step: Int, onNext: (List<WrongBook>) -> Unit){
        api.getWrongList(year,gradekey, subjectkey,page,step).sub({ onNext(it.body) })
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