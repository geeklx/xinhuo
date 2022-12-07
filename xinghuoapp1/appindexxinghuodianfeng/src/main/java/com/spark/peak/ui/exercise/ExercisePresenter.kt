package com.spark.peak.ui.exercise

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.EventMsg
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.GradeExercise
import com.spark.peak.bean.Section
import com.spark.peak.bean.SpacialPaper
import com.spark.peak.utlis.SpUtil
import org.greenrobot.eventbus.EventBus

/**
 * Created by Zzz on 2021/1/2
 */

class ExercisePresenter(progress: OnProgress) : BasePresenter(progress) {
    fun getSpecialList(sectionKey: String, gradeKey: String, onNext: (List<GradeExercise>) -> Unit) {
        api.getSpecialList(sectionKey, gradeKey, "").sub({ onNext(it.body) })
    }

    fun getSectionAndGradeList(onNext: (List<Section>) -> Unit) {
        api.getSectionAndGradeList().sub({ onNext(it.body.list) })
    }

    fun getSpecialPaperList(specialKey: String, onNext: (List<SpacialPaper>) -> Unit) {
        api.getSpecialPaperList(specialKey).sub({ onNext(it.body) })
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