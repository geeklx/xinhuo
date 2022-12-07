package com.spark.peak.bean

import java.io.Serializable

/**
 * keep	int	持续打卡天数
total	int	累计打卡天数
unpunched	int	未打卡天数
rowno		排名
iscard	int	打卡狀態，0未打卡，1已打卡

 */
class SignInfo (
        var keep : Int = 0,
        var total : Int = 0,
        var unpunched :Int = 0,
        var rowno : String = "",
        var iscard : Int  = 0 ) : Serializable

class SignListItem (
        var img : String = "",
        var name : String = "",
        var keep :Int,
        var userkey : String = "",
        var sectionname: String = "",
        var gradename: String = "")


class MyBookNetClass : Serializable{
    var img = ""
    var period = ""
    var subsection = ""
    var endtime = ""
    var name = ""
    var key = ""
    var order = ""
    var iseffect = "" //1 过期 0 有效
}

class MyBookDetail {
    var usernum : String ?= null
    var img :String ?=null
    var titile :String ?=null
    var paper :ArrayList<MyBookPaper> ?=null
    var resource :ArrayList<MyBookResource> ?=null
    var editionName :String ?=null //冀教版
    var mediasize :String ?= null //总文件大小
    var mediacount : String = "" //音频文件个数
    var isown: String = "" //是否加入我的学习，0未加入，1加入
    var supportingKey: String = "" //图书配套中，试卷资源所需的bookkey
    var bookkey: String = ""
    var gradeName: String = ""
}

class MyBookPaper : Serializable{
    var title : String? = null
//    var class : String? = null
    var key : String? = null
    var order : Int = 0
    var children : Int = 0
    var total : Int = 0
    var score : String = ""
    var resourceName : String = ""
    var totalCount: Int = 0
    var resourceKey: String = ""
    var catalogKey: String = ""
    var practiseCount : String = ""
    var createTime : String = ""
}

class MyBookResource : Serializable{
    var catalogKey : String ?= null
    var catalogList : List<MyBookResource> ?= null
    var resourceList : List<ResourceItem> ?= null
    var catalogName : String ?= null
    var floor : String ?= null
    var parentKey : String ?= null
    var score : String ?= null
    var sort : String ?= null
    var total : String ?= null
}

class ResourceItem : Serializable{
    var count : String ?= null
    var downUrl : String ?= null
    var id : String ?= null
    var name : String ?= null
    var playUrl : String ?= null
    var score : String ?= null
    var type : String ?= null
    var link: String ?= null
    var parentKey: String ?= null
    var lrcurl: String = ""
    var content: String = ""
    var teacher: String = ""
    var duration: String = ""
    var isCollection: String = ""
}