package tuoyan.com.xinghuo_dayingindex.bean

import java.io.Serializable

class Feedback(
    var content: String,
    var deviceInfo: String,
    var imgs: Any?,
    var wtflKey: String,//问题分类key，固定
    var wtKey: String//
)

class Img(
    var uuid: String,
    var order: Int
)

class FeedbackQuestion() : Serializable {
    var code: String = ""
    var name: String = ""
    var id: String = ""
}