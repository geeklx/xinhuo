package tuoyan.com.xinghuo_dayingindex.bean

import java.io.Serializable

open class DownloadBean : Serializable {
    var form: String = ""//4音频课
    var name: String = ""
    var downUrl: String = ""
    var duration: String = ""
    var path: String = ""
    var type: String = ""

    /** 3:video / 7 audio **/
    var state: Int = STATE_DEFAULT
    var progress: Int = 0
    var downloadInfo: String = ""
    var id: String = ""

    //TODO 直播回放下载时会用到这三个值，提到父类中来
    var liveKey: String? = null
    var liveToken: String? = null
    var liveType: String? = null //0 录播  1 直播   3 测评 4 音频

    var lrcUrls: String = "" //音频字幕，3个字幕逗号隔开，不存在的 用 none 占位
    var liveSource = ""// 1：欢拓；2：CC
    var isFinish = ""// 1测评提交完成

    var parentKey=""
    var parentName=""

    companion object {
        const val STATE_DEFAULT = 0 //初始状态
        const val STATE_DOWNLOADING = 1 //下载中
        const val STATE_PAUSED = 2 //暂停
        const val STATE_DONE = 3 //已完成

        //TODO 用于二级资源节点，判定为章节节点或资源节点
        const val TYPE_NODE = "NODE" //章节节点
        const val TYPE_DATA = "DATA" //资源

        //1:试卷,2:试卷解析,3:视频,4:图片,5:图文,6:文档,7:音频,8:链接,10:测评,11:资讯
        const val TYPE_EX = "1" //做题
        const val TYPE_PARS = "2" //题目解析
        const val TYPE_VIDEO = "3" //视频
        const val TYPE_IMG = "4" //图片
        const val TYPE_CONTENT = "5" //富文本
        const val TYPE_PDF = "6" //pdf
        const val TYPE_AUDIO = "7" //音频
        const val TYPE_LINK = "8" //外链
        const val TYPE_TEST = "10" //测评
        const val TYPE_NEWS = "11" //资讯
    }
}