package com.spark.peak.ui.lesson

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.NetLesson
import com.spark.peak.bean.NetRes

class LessonDetailPresenter(onProgress: OnProgress) : BasePresenter(onProgress) {
    fun loadData(key: String, onNext: (NetLesson) -> Unit) {
        api.getNetLesson(key).sub({ onNext(it.body) }) { }
    }

    fun getNetLessonCatalogue(s: String, onNext: (NetRes) -> Unit) {
        api.getCatalogue(s).sub({ onNext(it.body) })
    }

    fun freeBuyNetCourse(key: String, onNext: () -> Unit) {
        api.freeBuyNetCourse(mutableMapOf("key" to key)).sub({ onNext() })
    }

    /**
     * 删除图书
     */
    fun deleteMyNetClass(map: Map<String, String>, onNext: (String) -> Unit) {
        api.deleteMyNetClass(map).sub({ onNext(it.msg) })
    }

}