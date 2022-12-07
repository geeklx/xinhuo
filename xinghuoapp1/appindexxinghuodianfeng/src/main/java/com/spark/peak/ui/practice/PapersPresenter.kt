package com.spark.peak.ui.practice

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.net.PL
import com.spark.peak.net.Results

/**
 * 创建者： 霍述雷
 * 时间：  2018/6/25.
 */
class PapersPresenter(progress: OnProgress) : BasePresenter(progress) {


    fun cgPapers(key: String, onNext: (Results<PL<List<Map<String, String>>>>) -> Unit) {
        api.cgPaper(key).sub(onNext)
    }
}