package com.spark.peak.bean

import com.spark.peak.utlis.DownloadManager

/**
 * Created by 李昊 on 2018/5/10.
 */
class StudyResource(var type : Int,var name : String,var content : String) {

    var resource: ResourceItem ?= null //当前项 对应的资源详情
    var progress : Int = 0 //下载进度，max = 100
    var downloadId : Int = 0
    var nodeName: String = ""
//    fun downloadState() : Int = DownloadManager.getDownloadState(downloadId,content)

    var downloadState = STATE_DEFAULT
    companion object {
        val TYPE_NODE = 0 //章节节点
        val TYPE_DATA = 1 //资源

        val STATE_DEFAULT = 0 //初始状态
        val STATE_DOWNLOADING = 1 //下载中
        val STATE_PAUSED = 2 //暂停
        val STATE_DONE = 3 //已完成
    }
    fun getResType() = when(resource?.type){
        "3"-> ".mp4"
        "6"-> ".pdf"
        "7"-> ".mp3"
        else -> ""
    }
}