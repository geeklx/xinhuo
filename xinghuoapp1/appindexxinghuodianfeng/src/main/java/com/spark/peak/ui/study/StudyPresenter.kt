package com.spark.peak.ui.study

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.*

class StudyPresenter(onProgress: OnProgress) : BasePresenter(onProgress) {

    /**
     * 获取用户打卡信息
     */
    fun getSignInfo(onNext: (SignInfo) -> Unit) {
        api.getSignInfo().sub({ onNext(it.body) })
    }

    /**
     * 用户打卡
     */
    fun sign(onNext: () -> Unit) {
        api.sign().sub({ onNext() })
    }

    /**
     * 获取打卡排行
     */
    fun getSignList(page : Int, step: Int, onNext: (List<SignListItem>) -> Unit) {
        api.getSignList(page,step).sub({ onNext(it.body) })
    }


    /**
     * 获取图书列表
     */
    fun getMyBooks(onError: ()->Unit = {}, onNext: (List<MyBookNetClass>) -> Unit) {
        api.getMyBooks(1,1000).sub({ onNext(it.body) }){ onError() }
    }

    /**
     * 删除图书
     */
    fun deleteMyBooks(map: Map<String, String>, onNext: (String) -> Unit) {
        api.deleteMyBooks(map).sub({ onNext(it.msg) })
    }

    /**
     * 获取网课列表
     */
    fun getMyNetClass(onError: () -> Unit = {}, onNext: (List<MyBookNetClass>) -> Unit) {
        api.getMyNetClass().sub({ onNext(it.body) }) { onError() }
    }

    /**
     * 获取网课列表
     */
    fun getMyNetLesson(onError: () -> Unit = {}, onNext: (List<NetLessonItem>) -> Unit) {
        api.getMyNetLesson().sub({ onNext(it.body) }) { onError() }
    }


    /**
     * 删除图书
     */
    fun deleteMyNetClass(map: Map<String, String>, onNext: (String) -> Unit) {
        api.deleteMyNetClass(map).sub({ onNext(it.msg) })
    }

    /**
     * 获取图书详情
     */
    fun getMyBookDetail(code : String, type: String/** 1：扫描，2：点击书架 **/, onNext: (MyBookDetail) -> Unit) {
        api.getMyBookDetail(code, type).sub({ onNext(it.body) })
    }

    /**
     * 加入我的学习
     */
    fun addMyStudy(data : Map<String,String>, onNext: (String) -> Unit) {
        api.addMyStudy(data).sub({ onNext(it.msg) })
    }

    /**
     * 扫码获取内容
     */
    fun getDataByCode(code: String, onNext: (ScanResult) -> Unit){
        api.getDataByCode(code).sub({ onNext(it.body) })
    }
}