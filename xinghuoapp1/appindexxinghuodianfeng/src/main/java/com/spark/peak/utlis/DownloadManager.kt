package com.spark.peak.utlis

import android.os.Environment
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.model.FileDownloadStatus
import com.spark.peak.DOWNLOAD_PATH
import com.spark.peak.bean.DownloadInfo
import com.spark.peak.bean.StudyResource
import java.io.File

/**
 * Created by 李昊 on 2018/5/11.
 */
object DownloadManager {

    val downloadPath = File(Environment.getExternalStorageDirectory().path, DOWNLOAD_PATH + "/").path
    val taskList = ArrayList<BaseDownloadTask>()


    /**
     * 获取文件下载路径
     * 根据下载url命名
     */
    fun getFilePath(url: String): String {
        if (url.isNullOrBlank()) return ""
        val index = url.lastIndexOf("/")
        if (index == -1) return ""
        return downloadPath + "/" + url.substring(index)
    }

    /**
     * 获取文件下载路径
     * 根据下载url命名
     * @param type pt(配套) wk（网课）
     * @param bookName(网课/图书 名称)
     * @param name(资源名称)
     */
    fun getFilePath(type: String, bookName: String, name: String, url: String): String {
        if (type.isNullOrBlank()
                || bookName.isNullOrBlank()
                || name.isNullOrBlank()
                || url.isNullOrBlank()) return ""
        val index = url.lastIndexOf(".")
        if (index == -1) return ""
        val t = if (url.contains("?")) url.substring(index, url.indexOf("?")) else url.substring(index)
        return "$downloadPath/$type/$bookName/$name$t"
    }

    /**
     * 通过资源key 和 类型 拼接下载文件名
     */
    fun getFilePathWithKey(key: String, type: String): String{
        return if (key.isNullOrEmpty() || type.isNullOrEmpty()){
            ""
        }else{
            when(type){
                "3"-> "$downloadPath/$key.mp4"
                "6"-> "$downloadPath/$key.pdf"
                "7"-> "$downloadPath/$key.mp3"
                else ->""
            }
        }
    }

    fun getFileByType(type: String): List<File> {
        val list = mutableListOf<File>()
        val file = File("$downloadPath/$type")
        try {
            list.addAll(file.listFiles())
        } catch (e: Exception) {
        }
        return list
    }

    fun getFileByBook(type: String, bookName: String): List<File> {
        val list = mutableListOf<File>()
        val file = File("$downloadPath/$type/$bookName")
        try {
            list.addAll(file.listFiles())
        } catch (e: Exception) {
        }
        return list
    }

    /**
     * 获取当前正在运行中的下载任务
     */
    fun getRunTask(id: String): BaseDownloadTask? {
        var task: BaseDownloadTask? = null
        taskList.forEach {
            var key = it.tag as String?
            if (key == id) {
                task = it
                return@forEach
            }
        }
        return task
    }


    /**
     * 获取当前进行中的任务的下载状态
     */
    fun getDownloadState(id: Int,type: String, bookName: String, name: String, content: String): Int {
        if (content == "") return 0
        return when (FileDownloader.getImpl().getStatus(id, getFilePath(type,bookName,name,content))) {
            FileDownloadStatus.completed -> StudyResource.STATE_DONE
            FileDownloadStatus.blockComplete -> StudyResource.STATE_DONE
            FileDownloadStatus.INVALID_STATUS -> StudyResource.STATE_DEFAULT
            FileDownloadStatus.paused -> StudyResource.STATE_PAUSED
            FileDownloadStatus.progress -> StudyResource.STATE_DOWNLOADING
            else -> StudyResource.STATE_DEFAULT
        }
    }

    /**
     * 缓存下载记录
     * 去重
     */
    fun saveDownloadInfo(id: Int, url: String) {
        var hasSame = false
        SpUtil.downloadInfo.forEach {
            if (id == it.id) {
                hasSame = true
            }
        }

        if (!hasSame) {
            var downloadInfoList = SpUtil.downloadInfo
            var downloadInfo = DownloadInfo()
            downloadInfo.id = id
            downloadInfo.url = url
            downloadInfoList.add(downloadInfo)
            SpUtil.downloadInfo = downloadInfoList
        }
    }
}