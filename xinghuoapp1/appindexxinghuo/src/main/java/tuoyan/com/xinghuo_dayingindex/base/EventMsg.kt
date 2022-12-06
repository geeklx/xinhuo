package tuoyan.com.xinghuo_dayingindex.base

import tuoyan.com.xinghuo_dayingindex.bean.LrcDetail
import tuoyan.com.xinghuo_dayingindex.bean.ResourceInfo

/**
 * 创建者：
 * 时间：  2018/6/25.
 */
class EventMsg(
    val action: String,
    val position: Int
)

class AudioCallBack(
    var type: Int,
    var msg: String = "",
    var position: Int = 0,
    var time: Long = 0L,
    var flag: Boolean = false,
    var key: String = "",
    var function: (ResourceInfo?) -> Unit = {},
    var getLrc: (List<LrcDetail>?) -> Unit = {}
) {
    companion object {
        val TYPE_COMPLETE = 1
        val TYPE_ERROR = 2
        val TYPE_START = 3
        val TYPE_STOP = 4
        val TYPE_STATUS = 5
        val TYPE_CALLBACK = 6
        val TYPE_RES_INFO = 7
        val TYPE_NET_ERROR = 8

        val TYPE_SENTENCE_COMPLETE = 11
        val TYPE_SENTENCE_ERROR = 12
        val TYPE_SENTENCE_START = 13
        val TYPE_SENTENCE_STOP = 14
        val TYPE_SENTENCE_STATUS = 15
        val TYPE_SENTENCE_CALLBACK = 16
        val TYPE_SENTENCE_INFO = 17
        val TYPE_SENTENCE_NET_ERROR = 18
        val TYPE_SENTENCE_DURATION = 19
    }
}