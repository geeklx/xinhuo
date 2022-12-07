package com.spark.peak.ui.netLessons

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Comment
import com.spark.peak.bean.NetLesson
import com.spark.peak.bean.NetRes
import com.spark.peak.net.Results

/**
 * 创建者：
 * 时间：
 */
class NetLessonsPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun loadData(key: String, onNext: (NetLesson) -> Unit) {
        api.getNetLesson(key).sub({ onNext(it.body) }) { }
    }

    fun getNetLessonCatalogue(s: String, onNext: (NetRes) -> Unit) {
        api.getCatalogue(s).sub({ onNext(it.body) })
    }

    fun loadComment(key: String, page: Int, onNext: (Results<List<Comment>>) -> Unit) {
        api.getComments(key, page).sub({ onNext(it) })
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