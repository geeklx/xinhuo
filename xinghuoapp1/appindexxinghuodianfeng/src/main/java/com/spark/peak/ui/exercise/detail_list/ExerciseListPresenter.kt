package com.spark.peak.ui.exercise.detail_list

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.ExerciseBody
import com.spark.peak.bean.ExerciseHistory
import com.spark.peak.bean.Report

class ExerciseListPresenter(onProgress: OnProgress) : BasePresenter(onProgress){


    fun getExerciseList(bookkey: String,onNext :(ExerciseBody) -> Unit){
        api.getExerciseList(bookkey).sub({ onNext(it.body) }  )
    }

    /**
     * 获取练习记录
     */
    fun getExerciseHistory(page: Int, step: Int, onNext :(List<ExerciseHistory>) -> Unit){
        api.getExHistory(page,step).sub({ onNext(it.body) }  )
    }

    /**
     * 清空练习记录
     */
    fun clearExerciseHistory(onNext :(Any) -> Unit){
        api.clearExHistory().sub({ onNext("") }  )
    }

    /**
     * 获取历史习题练习报告
     */
    fun getReport(paperkey: String, userpractisekey: String, onNext :(Report) -> Unit){
        api.exerciseReport(paperkey,userpractisekey).sub({ onNext(it.body) } )
    }

}