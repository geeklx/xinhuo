package com.spark.peak.ui.home

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.EventMsg
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.*
import com.spark.peak.utlis.SpUtil
import org.greenrobot.eventbus.EventBus

class HomePresenter(progress: OnProgress) : BasePresenter(progress) {

    /**
     * 首页刷新数据
     */
    fun homePageInfo(onError: () -> Unit = {}, onNext: (HomeInfo) -> Unit) {
        api.homePageInfo().sub({ onNext(it.body) })
    }


    /**
     * 获取当前app下所有的学段与年级列表
     */
    fun getSectionAndGradeList(onNext: (List<Section>) -> Unit) {
        api.getSectionAndGradeList().sub({ onNext(it.body.list) })
    }

    /**
     * 获取首页广告位 或 推荐图书列表
     *  首页banner	sybn
    图书精选	tsjx
    闪屏	spgg
     */
    fun getBanner(adspace: String, page: Int, step: Int, onNext: (List<Advert>) -> Unit) {
        api.getAdvs(adspace, page, step).sub({ onNext(it.body) })
    }

    /**
     * 添加广告点击量
     */
    fun addAdPv(key: String, onNext: (Any) -> Unit) {
        api.addAdvsPv(key).sub({ onNext(it.body) })
    }

    /**
     * 获取公告
     */
    fun getAnnouncement(onNext: (List<Announcement>) -> Unit) {
        api.getAnnouncement().sub({ onNext(it.body) })
    }

    /**
     * 获取公告详情
     */
    fun getAnnouncementDetail(key: String, onNext: (AnnouncementDetail) -> Unit) {
        api.getAnnouncementDetail(key).sub({ onNext(it.body) })
    }

    /**
     * 获取推荐网课列表
     */
    fun getNetClassList(onNext: (List<NetClass>) -> Unit) {
        api.getNetClassList(1, 6).sub({ onNext(it.body) })
    }

    /**
     * 获取精选资讯
     */
    fun getEduInfos(page: Int, step: Int, onNext: (List<EduInfo>) -> Unit) {
        api.getEduInfos(page, step).sub({ onNext(it.body) })
    }
    /**
     * 扫码获取内容
     */
    fun getDataByCode(code: String, onNext: (ScanResult) -> Unit) {
        api.getDataByCode(code).sub({ onNext(it.body) })
    }

    /**
     * 获取新版本
     */
    fun getNewVersion(onNext: (NewVersion) -> Unit) {
        api.getNewVersion("", "", "").sub({ onNext(it.body) })
    }

    /**
     * 当前用户无学段 年级信息时，把本地存储的默认学段信息赋给该用户
     */
    fun updUserSG(sectionkey: String, gradekey: String, onNext: () -> Unit = {}) {
        api.updUserSG(sectionkey, gradekey).sub(onNext = {
            userInfo {
                SpUtil.userInfo = it
                EventBus.getDefault().post(EventMsg("home", -1))
                onNext()
            }
        })
    }

    fun informationPv(key: String) {
        api.informationPv(key).sub()
    }

    fun getUserQrcodeRecord(onNext: (List<HomeQr>) -> Unit) {
        api.getUserQrcodeRecord(1, 10).sub({ onNext(it.body) })
    }

    fun getHomePageInfoDF(onNext: (HomeData) -> Unit) {
        api.getHomePageInfoDF().sub({ onNext(it.body) })
    }

}