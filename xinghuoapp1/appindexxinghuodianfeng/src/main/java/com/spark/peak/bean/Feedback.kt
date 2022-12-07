package com.spark.peak.bean

class Feedback(
        var content: String,
        var imgs: Any?
)

class Img(var uuid: String,
          var order: Int)

class QFeedBack {
    var code = ""
    var name = ""
    var id = ""
}