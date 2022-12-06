package tuoyan.com.xinghuo_dayingindex.bean

/**
 * 我的图书&我的网课
 */
class MyBookLesson(
    var key: String,
    var img: String,
    var name: String,
    var state: String,//TODO 1直播中2已完结
    var form: String, //TODO 1直播课2录播课3升级包
    var iseffect: String,
    var endtime: String,//TODO 网课有效期
    var nextBeginTime: String,
    var teacher: String,
    var sellingPoint: String = "",
    var netSubjectName: String = ""
) {
    var learnState = ""//每日任务1未完成，2已完成，否则没有
    var repeatFlag = ""//是否开启重读服务 0-关闭 1-开启
}