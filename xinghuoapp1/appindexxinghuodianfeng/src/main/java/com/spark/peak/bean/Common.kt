package com.spark.peak.bean

import com.google.gson.annotations.SerializedName


/**
 * 创建者： 霍述雷
 * 时间：  2018/5/17.
 */
/**
 * 登录请求参数
 */
class Login(var username: String, var password: String)

/**
 * 注册请求参数
 */
class Register(var phone: String, var code: String, var password: String)

/**
 * 验证码请求参数
 */
class SMSCode(var phone: String, var type: String)

class FreeLogin() {
    var isNew = ""//1新用户
    var userInfo: LoginResponse? = null
}

/**
 *  token	    string	登录标识
 *  userId	    string	用户主键
 *  sectionkey	string	学段id
 *  gradekey	string	年级id
 *  list	    list	学段
 */
class LoginResponse(var token: String? = null,
                    var userId: String? = null,
                    var sectionkey: String? = null,
                    var gradekey: String? = null,
                    var state: String? = null,
                    var list: List<Section>? = null)

class Lt<T>(var list: T)
/**
 * 学段
 */
class Section(
        @SerializedName(value = "catalogKey", alternate = ["key"])
        var catalogKey: String,
        @SerializedName(value = "catalogName", alternate = ["name"])
        var catalogName: String,
        var resourceList: List<Grade>) {
    override fun toString(): String {
        return catalogName
    }
}

/**
 * 年级
 */
class Grade(@SerializedName(value = "id", alternate = ["key"])
            var id: String,
            var name: String) {
    var type = ""//1:标题；2：选项
    var parentId = ""
    var parentName = ""
    override fun toString(): String {
        return name
    }
}

/**
 * 消息通知
 */
class Message(
        var noticestatus: String,
        var list: List<Map<String, String>>
)

class MsgBean {
    var img = ""
    var isInstation = ""//是否站内消息，1是 0否
    var targetType = ""//消息{1:订单（1.1: 图书、 1.2:网课 ）、2:网课、3:资讯、4：图书、5：退款、7：问答详情}，通知{1-网课，2-资讯，3-图书}
    var source = ""//1消息，2通知
    var title = ""
    var targetKey = ""
    var targetUrl = ""
    var key = ""
    var content = ""
    var userKey = ""
    var createDate = ""
}

class MsgList {
    var list = arrayListOf<MsgBean>()
    var readTime = ""
    var readFlag = ""
}