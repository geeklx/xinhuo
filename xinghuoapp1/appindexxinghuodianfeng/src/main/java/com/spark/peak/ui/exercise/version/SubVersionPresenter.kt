package com.spark.peak.ui.exercise.version

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.ExBooks
import com.spark.peak.bean.Version

class SubVersionPresenter(onProgress: OnProgress) : BasePresenter(onProgress){

    /**
     * 获取图书版本列表
     */
    fun getVersions(subjectkey : String,onNext :(List<Version>) -> Unit){
        api.getVersions(subjectkey).sub({ onNext(it.body) })
    }

    /**
     * 获取图书列表
     */
    fun getBooks(editionkey : String, page: Int, step: Int,
                    onNext :(List<ExBooks>) -> Unit){
        api.getBooks(editionkey,page,step).sub({ onNext(it.body) })
    }
}