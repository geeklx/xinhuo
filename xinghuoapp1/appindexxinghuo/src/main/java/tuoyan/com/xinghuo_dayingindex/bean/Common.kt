package tuoyan.com.xinghuo_dayingindex.bean

import com.google.gson.annotations.SerializedName

class FreeLogin() {
    var isNew = ""//1新用户
    var userInfo: LoginResponse? = null
}
/**
 * 创建者：
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

/**
 *  token	    string	登录标识
 *  userId	    string	用户主键
 *  sectionkey	string	学段id
 *  gradekey	string	年级id
 *  list	    list	学段
 */
class LoginResponse(
    var token: String? = null,
    var userId: String? = null,
    var sectionkey: String? = null,
    var gradekey: String? = null,
    var state: String? = null,
    var list: List<Section>? = null
) {
    var type = ""//0： 登录用户； 1：注册用户
}

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
    override fun toString(): String {
        return name
    }
}

class ResourceInfo(
    var url: String,
    var downloadUrl: String,
    var type: String,
    var name: String,
    var lrcurl1: String,
    var lrcurl2: String,
    var lrcurl3: String,
    var textContext: String = "",
    var coverImg: String = "",
    var teacherName: String = "",
    var teacherImg: String = ""
) {
    var recommendTime = ""
    var lrcKey2 = ""
}

class QuestionPayplan(
        var key: String,
        var tile: String,
        var price: String,
        var type: String,
        var remarks: String
)