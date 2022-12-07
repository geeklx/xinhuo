package com.spark.peak.utlis

import android.annotation.SuppressLint
import com.spark.peak.bean.*
import kotlin.properties.Delegates


@SuppressLint("StaticFieldLeak")
object SpUtil {
    var isLogin by Delegates.preference("kotlin_isLogin", false)
    var user: LoginResponse by Delegates.preference("kotlin_user", LoginResponse())
    var uuid: String by Delegates.preference("uuid", "")

    var userInfo: UserInfo by Delegates.preference("kotlin_userInfo", UserInfo())

    var defaultSection: Section by Delegates.preference(
        "default_section",
        Section("none", "none", ArrayList<Grade>())
    )

    var defaultGrade: Grade by Delegates.preference("default_grade", Grade("none", "none"))


    var noFirst: Boolean by Delegates.preference("kotlin_welcome", false)
    var agreement: Boolean by Delegates.preference("Sparke_Agreement", false)

    var showAlert: Boolean by Delegates.preference("show_alert", true) //TODO 做题模块防智障提示

    var apkDownLoad: Boolean by Delegates.preference("kotlin_isDown", false)

    var templateCode: Int by Delegates.preference("kotlin_template_code", 0)

    var downloadInfo: ArrayList<DownloadInfo> by Delegates.preference("download_info", ArrayList())

    var videoProgress: String by Delegates.preference("video_progress", "")

    var lrcInfo: String by Delegates.preference("lrc_info", "[]")

    //专项微课和专项试题学段缓存,如果没有取个人中心学段
    var grade: Grade by Delegates.preference("grade", Grade("", ""))

    //教材同步学段缓存，没有默认第一组
    var periodGrade: GradeBean by Delegates.preference("periodGrade", GradeBean())
    var gradedGrade: GradeBean by Delegates.preference("gradedGrade", GradeBean())
    var editionGrade: GradeBean by Delegates.preference("editionGrade", GradeBean())
    var termGrade: GradeBean by Delegates.preference("termGrade", GradeBean())
    var SenorData: String by Delegates.preference("SenorData", "")
}



