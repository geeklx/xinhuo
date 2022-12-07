package com.spark.peak.ui.mine.collection

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Audio
import com.spark.peak.net.Results

/**
 * 创建者：
 * 时间：
 */
class CollectionPresenter(progress: OnProgress) : BasePresenter(progress) {
//    fun collectionPaper(page: Int, onNext: (Results<List<Paper>>) -> Unit) {
//        api.collectionPaper(page).sub(onNext = { onNext(it) })
//    }
//
//    fun questionAnalyze(questionkey: String, questiontype: String, onNext: (Any) -> Unit) {
//        api.questionAnalyze(questionkey, questiontype).sub(onNext = { onNext(it.body) })
//    }


    fun collectionAvdio(page: Int, onNext: (Results<List<Audio>>) -> Unit) {
        api.collectionAvdio(page).sub(onNext = { onNext(it) })
    }

}