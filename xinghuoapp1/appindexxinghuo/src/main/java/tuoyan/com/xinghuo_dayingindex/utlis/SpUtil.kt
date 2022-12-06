package tuoyan.com.xinghuo_dayingindex.utlis

import android.annotation.SuppressLint
import tuoyan.com.xinghuo_dayingindex.GRAD_KEY_CET4
import tuoyan.com.xinghuo_dayingindex.bean.Grade
import tuoyan.com.xinghuo_dayingindex.bean.LoginResponse
import tuoyan.com.xinghuo_dayingindex.bean.UserInfo
import kotlin.properties.Delegates


@SuppressLint("StaticFieldLeak")
object SpUtil {
    var eBookExerciseShow: Boolean by Delegates.preference("eBookExerciseShow", false)
    var isFreeLogin: String by Delegates.preference("isFreeLogin", "")
    var updateVersion: String by Delegates.preference("updateVersion", "")

    var isLogin by Delegates.preference("kotlin_isLogin", false)
    var user: LoginResponse by Delegates.preference("kotlin_user", LoginResponse())
    var uuid: String by Delegates.preference("uuid", "")

    var userInfo: UserInfo by Delegates.preference("kotlin_userInfo", UserInfo())

    var defaultSection: String by Delegates.preference("default_section", GRAD_KEY_CET4)
    var defaultGrade: Grade by Delegates.preference("default_grade", Grade("none", "none"))


    var first: Boolean by Delegates.preference("kotlin_welcome", true)

    var apkDownLoad: Boolean by Delegates.preference("kotlin_isDown", false)

    var templateCode: Int by Delegates.preference("kotlin_template_code", 0)

    var gradStr: String by Delegates.preference("grad_str", "四级")

    var loginInfo: String by Delegates.preference("login_info", "")

    var videoProgress: String by Delegates.preference("video_progress", "")

    var gradeSelect: String by Delegates.preference("grade_select", "")
    var ipStr: String by Delegates.preference("ipStr", "")
    var audioSpeed: Float by Delegates.preference("AudioSpeed", 1f)
    var audioModel: Boolean by Delegates.preference("AudioModel", false)

    var agreement: Boolean by Delegates.preference("SparkeAgreement", false)
    var isFirstModal: Boolean by Delegates.preference("isFirstModal", true)//简听力 基础篇（练习）第一次进入  展示弹窗
    var showedEBookWord by Delegates.preference("firstEBookWord", false)
}



