package com.spark.peak.ui.cg

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.net.LL
import com.spark.peak.net.Results

/**
 * 创建者： 霍述雷
 * 时间：  2018/6/25.
 */
class CGPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun cgPractice(grade: String, page: Int = 0, onNext: (Results<List<Map<String, String>>>) -> Unit) {
        api.cgPractice(grade = grade, page = page).sub(onNext = {
            onNext(it)
        })
    }

    fun cgPass(key: String, onNext: (Results<LL<List<Map<String, String>>>>) -> Unit) {
        api.cgPass(key).sub(onNext = {
            onNext(it)
        })
    }

    fun wordShowed(key: String) {
        api.wordShowed(mutableMapOf("key" to key)).sub()
    }
}