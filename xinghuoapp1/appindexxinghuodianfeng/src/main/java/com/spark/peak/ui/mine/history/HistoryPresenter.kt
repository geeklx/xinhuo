package com.spark.peak.ui.mine.history

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.ExerciseBody
import com.spark.peak.bean.ExerciseHistory
import com.spark.peak.bean.Report
import com.spark.peak.net.Results

class HistoryPresenter(onProgress: OnProgress) : BasePresenter(onProgress) {

//
//    fun getExerciseList(bookkey: String,onNext :(ExerciseBody) -> Unit){
//        api.getExerciseList(bookkey).sub({ onNext(it.body) }  )
//    }

    /**
     * 获取练习记录
     */
    fun history(page: Int, onNext: (Results<List<ExerciseHistory>>) -> Unit) {
        api.historyPractice(page).sub(onNext)
    }

    /**
     * 清空练习记录
     */
    fun clearExerciseHistory(onNext: () -> Unit) {
        api.clearExHistory().sub(onNext = { onNext() })
    }

    /**
     * 获取历史习题练习报告
     */
    fun getReport(paperkey: String, userpractisekey: String, onNext: (Report) -> Unit) {
        api.exerciseReport(paperkey, userpractisekey).sub({ onNext(it.body) })
    }

}