package com.spark.peak.bean

import com.google.gson.annotations.SerializedName
import com.spark.peak.utlis.DownloadManager
import com.spark.peak.utlis.FileUtils
import java.io.Serializable

/**
 * 创建者： 霍述雷
 * 时间：  2019/2/15.
 */
class BookRes(val name: String, val num: String, val size: String)

class OfflineRes(val name: String, val path: String)
class BookResList(
    var list: List<AudioRes>,
    var bookKey: String,
    var bookName: String
)

class AudioRes(
    var downloadFlag: String = "",
    var lrcurl: String = "",
    @SerializedName(value = "name", alternate = ["showName"])
    var name: String = "",
    var lrcurl3: String = "",
    var lrcurl2: String = "",
    var playUrl: String = "",
    var count: String = "",
    var downUrl: String? = null,
    var isCollection: String = "",
    var id: String = ""
) : Serializable {
    var appResourceKey = ""
    fun islrcurl() = FileUtils.isFileExists(DownloadManager.getFilePath(lrcurl))
    var showLrc = "0"//是否展示字幕 1展示 0不展示
    var bookCover = ""
    var duration=""
}

class ResourceInfo(
    @SerializedName(value = "url", alternate = ["resourcePath"])
    var url: String,
    var downloadUrl: String,
    var type: String,
    @SerializedName(value = "name", alternate = ["resourceName"])
    var name: String,
    @SerializedName(value = "lrcurl1", alternate = ["lrcUrl"])
    var lrcurl1: String,
    var lrcurl2: String,
    var lrcurl3: String
) {
    var imgList = arrayListOf<PdfImg>()
    var showLrc = "0"//是否展示字幕 1展示 0不展示
    var bookCover = ""
    var downloadFlag = ""//"1" 可下载
}

/**
 * 我收藏的音频
 */
class Audio {
    /**
     * title : zn%jC
     * type : I^1Yp
     * date : IGy7
     * id : rWGrp
     */

    var key: String? = null
    var name: String? = null
    var bookName: String? = null
    var type: String? = null
    var size: String? = null
    var palyUrl: String? = null
    var downloadUrl: String? = null
    var lrcUrl: String? = null
    var lrcUrl2: String? = null
    var lrcUrl3: String? = null
    var downloadFlag: String? = null
    var duration = ""
}

class PdfImg {
    var img = ""
    var id = ""
}