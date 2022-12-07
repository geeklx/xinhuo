package com.spark.peak.bean

import com.spark.peak.R
import java.io.Serializable


class HomeInfo {
    var sybnList: List<Advert>? = null
    var netList: List<NetClass>? = null
    var tsList: List<Advert>? = null
    var syggList: List<Announcement>? = null
    var xxzxList: List<EduInfo>? = null
}

class HomeData {
    var qrCodeList = arrayListOf<HomeQr>()
    var bookList = arrayListOf<HomeBook>()
    var noticeReadFlag = ""//1已读，0有消息未读
}

class HomeBook {
    var img = ""
    var name = ""
    var key = ""
}

class HomeQr {
    var name = ""
    var key = ""
    var type = ""
}

class NetClass {
    var img: String? = null
    var key: String? = null
    var order: Int = 0
}

class Advert(
        var img: String,//图片地址
        var sort: Int,//序号
        var url: String, //跳转url
        var link: String, //跳转url
        var title: String,
        var goodtype: String,//类型，net：网课，book：点读
        var goodkey: String,
        var type: String, //商品：goods，外链：link
        var key: String
)

class SubjectBody {
    var list: List<Subject>? = null
    var missionKey: String = ""
    var missionSize: Int = 0
}

class Subject : Serializable {
    var key: String? = null
    var name: String? = null
    var order: String? = null

    fun img() = when (name) {
        "语文" -> R.drawable.menu_chinese
        "英语" -> R.drawable.menu_english
        "物理" -> R.drawable.menu_physics
        "生物" -> R.drawable.menu_shengwu
        "历史" -> R.drawable.menu_lishi
        "地理" -> R.drawable.menu_dili
        "政治" -> R.drawable.menu_zhengzhi
        "数学" -> R.drawable.menu_shuxue

        "词汇" -> R.drawable.menu_cihui
        "翻译" -> R.drawable.menu_fanyi
        "口语" -> R.drawable.menu_kouyu
        "听力" -> R.drawable.menu_tingli
        "写作" -> R.drawable.menu_xiezuo
        "阅读" -> R.drawable.menu_yuedu
        "方法讲解" -> R.drawable.menu_ffjj
        "音标" -> R.drawable.menu_yinbiao
        "语法" -> R.drawable.menu_yufa

        else -> R.drawable.menu_shuxue
    }

    fun background() = when (name) {
        "语文" -> R.drawable.sub_yuwen
        "英语" -> R.drawable.sub_yingyu
        "物理" -> R.drawable.sub_wuli
        "生物" -> R.drawable.sub_shengwu
        "历史" -> R.drawable.sub_lishi
        "地理" -> R.drawable.sub_dili
        "政治" -> R.drawable.sub_zhengzhi
        "数学" -> R.drawable.sub_shuxue
        else -> R.drawable.sub_yuwen
    }

}

class EduInfo {
    var img: String? = null
    var key: String? = null
    var title: String? = null
    var pv: Int = 0
    var nofcomment: Int = 0
    var classifyname: String? = null
    var link: String? = null
    var cententtype: String? = null
    var source: String? = null
    var content: String?=null
}

class Announcement {
    var title: String? = null
    var key: String? = null
}

class AnnouncementDetail {
    var title: String? = null
    var time: String? = null
    var content: String? = null
}

class ScanResult {
    var key: String? = null
    var type: String? = null //1：资源，2：图书
}

class NewVersion {
    var isForce: String = "" //0不限制，1强制
    var msg: String = ""
    var v: String = ""
    var url: String = ""
    var patchUrl: String = ""
    var patchVersion: String = ""

}