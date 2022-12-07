package com.spark.peak.ui.common.grade

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.EventMsg
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Section
import com.spark.peak.utlis.SpUtil
import org.greenrobot.eventbus.EventBus

/**
 * 创建者：
 * 时间：
 */
class GradePresenter(progress: OnProgress) : BasePresenter(progress) {
    fun getSectionAndGradeList(onNext: (List<Section>) -> Unit) {
        api.getSectionAndGradeList().sub({ onNext(it.body.list) })
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