package com.spark.peak.fileDownloader

import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener

/**
 * Created by 李昊 on 2018/3/19.
 */
class DownloadManager {
    val dir = "下载路径文件夹"

    companion object {
        var listener = object : FileDownloadListener(){
            override fun warn(task: BaseDownloadTask?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun completed(task: BaseDownloadTask?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }
}