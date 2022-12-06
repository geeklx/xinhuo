package tuoyan.com.xinghuo_dayingindex.bean

import java.io.Serializable

class AudioTime(var id: Int, var name: String = "", var time: Int = 0, var isChecked: Boolean = false)
class AudioSpeed(var id: Int, var name: String = "", var speed: Float = 0f, var isChecked: Boolean = false)

class LrcRow {
    constructor(rowData: String, timeText: String, currentRowTime: Long) {
        this.rowData = rowData
        this.timeText = timeText
        this.currentRowTime = currentRowTime
    }

    var rowData: String = ""
    var timeText: String = ""
    var currentRowTime: Long = 0L


}


class LrcData {
    var signNum = 0
    var list = mutableListOf<LrcDetail>()
}

class LrcDetail : Serializable {
    var sort = ""
    var lrcKey = ""
    var lrcDetailKey = ""
    var startTime = 0L
    var endTime = ""
    var enContent = ""
    var cnContent = ""
    var isSign = ""
    var listenNum = 0

}

