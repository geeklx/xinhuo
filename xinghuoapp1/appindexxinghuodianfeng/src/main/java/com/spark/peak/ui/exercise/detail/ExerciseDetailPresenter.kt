package com.spark.peak.ui.exercise.detail

import com.google.gson.JsonObject
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.AnswerSubmit
import com.spark.peak.bean.ExerciseFrame
import com.spark.peak.bean.QFeedBack
import com.spark.peak.bean.Report
import com.spark.peak.ui.exercise.detail.ExerciseDetailActivity.Companion.TYPE_DFXL
import com.spark.peak.ui.exercise.detail.ExerciseDetailActivity.Companion.TYPE_QYT

class ExerciseDetailPresenter (onProgress: OnProgress) : BasePresenter(onProgress){

    /**
     * 试卷结构及试卷详情
     * 做题时调用
     */
    fun getExerciseFrame(practisekey: String, ishistory: String, onNext :(ExerciseFrame) -> Unit){
        api.getExerciseFrame(practisekey,ishistory).subs({ onNext(it.body) }  )
    }
 /**
     * 试卷结构及试卷详情
     * 做题时调用
     */
    fun getExerciseFrame1(practisekey: String, ishistory: String, onNext :(JsonObject) -> Unit){
        api.getExerciseFrame1(practisekey,ishistory).subs({ onNext(it.body) }  )
    }

    /**
     * 试卷结构及试卷详情
     * 解析时调用
     */
    fun getExerciseParsingFrame(practisekey: String, userpractisekey: String, ishistory: String, onNext :(ExerciseFrame) -> Unit){
        api.getExerciseParsingFrame(practisekey,userpractisekey,ishistory).subs({ onNext(it.body) }  )
    }
 /**
     * 试卷结构及试卷详情
     * 解析时调用
     */
    fun getExerciseParsingFrame1(practisekey: String, userpractisekey: String, ishistory: String, onNext :(JsonObject) -> Unit){
        api.getExerciseParsingFrame1(practisekey,userpractisekey,ishistory).subs({ onNext(it.body) }  )
    }

    /**
     * 获取题目详情
     */
    fun getExerciseDetail(practisekey: String, groupkey: String,
                          questiontype: String, questionkey: String,
                          questionsort: String, onNext :(Any) -> Unit){

        api.getExerciseDetail(practisekey, groupkey, questiontype,
                questionkey,questionsort).sub({ onNext(it.body) }  )
    }
    /**
     * 获取题目解析
     */
    fun getExerciseParsing(practisekey: String, questionkey: String,
                          questiontype: String, userpractisekey: String,
                          questionsort: String, onNext :(Any) -> Unit){

        api.getExerciseParsing(practisekey, questionkey, questiontype,
                userpractisekey,questionsort).sub({ onNext(it.body) }  )
    }

    /**
     * 交卷
     */
    fun submit(answer: AnswerSubmit, type: String, onNext :(Report) -> Unit){
        if (type == TYPE_QYT){
            api.exerciseSubmit(answer).subs({ onNext(it.body) })
        }else if (type == TYPE_DFXL){
            api.exerciseSubmitDF(answer).subs({ onNext(it.body) })
        }
    }


    /**
     * 题目反馈
     */
    fun questionFB(map: Map<String, String>, onNext: (Any) -> Unit) {
        api.questionFB(map).sub({ onNext(it) })
    }

    /**
     * 问题反馈
     */
    fun paperFB(map: Map<String, String>, onNext: () -> Unit) {
        api.paperFB(map).sub({ onNext() })
    }

    /**
     *问题类型获取接口
     */
    fun getDictInfo(onNext: (List<QFeedBack>) -> Unit) {
        api.getDictInfo("paperError").sub({ onNext(it.body) })
    }
}