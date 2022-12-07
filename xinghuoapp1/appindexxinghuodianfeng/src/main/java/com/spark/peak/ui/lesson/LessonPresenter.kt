package com.spark.peak.ui.lesson

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.EventMsg
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.NetLessonItem
import com.spark.peak.bean.Section
import com.spark.peak.utlis.SpUtil
import org.greenrobot.eventbus.EventBus

class LessonPresenter(onProgress: OnProgress) : BasePresenter(onProgress) {

    fun search(keyword: String, sortingmode: Int,
               grade: String, subject: String,
               page: Int, step: Int,
               onNext: (List<NetLessonItem>) -> Unit) {
        api.search(keyword, sortingmode, grade, subject, page, step).sub({ onNext(it.body) })
    }

    fun getSectionAndGradeList(onNext: (List<Section>) -> Unit) {
        api.getSectionAndGradeList().sub({ onNext(it.body.list) })
    }

    fun getLesson(grade: String, onNext: (List<NetLessonItem>) -> Unit) {
        api.getLessons(grade).sub({ onNext(it.body) })
    }

    fun updUserSG(sectionkey: String, gradekey: String, onNext: () -> Unit = {}) {
        api.updUserSG(sectionkey, gradekey).sub(onNext = {
            userInfo {
                SpUtil.userInfo = it
                EventBus.getDefault().post(EventMsg("home", -1))
                onNext()
            }
        })
    }
}