package com.spark.peak.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 创建者： 霍述雷
 * 时间：  2018/5/17.
 */
/**
 * 关注人
 */
class Attention(@SerializedName("userkey")
        var key: String,
                var img: String,
                var name: String,
                var section: String,
                var grade: String,
                var signature: String,
                var isFollow: Boolean? = null)

/**
 * 关注圈子
 */
class Circle(var img: String,
             var titile: String,
//             @SerializedName("userkey")
             var key: String,
             var summary: String,
             var attentionnum: String,
             var nofcomment: String,
             var isFollow: Boolean? = null)

/**
 * 个人信息
 */

class UserInfo(var gradename: String? = null,
               var img: String? = null,
               var address: String? = null,
               var phone: String? = null,
               var signature: String? = null,
               var sysUserKey: String? = null,
               var sex: String? = null,
               var grade: String? = null,
               var name: String? = null,
               var section: String? = null,
               var age: String? = null,
               var sectionname: String? = null)


/**
 * 个人主页信息
 */
class HomePageInfo(var img: String? = null,
                   var name: String? = null,
                   var fanscount: Int = 0,
                   var attentioncount: Int = 0,
                   var gradename: String? = null,
                   var sectionname: String? = null,
                   var signature: String? = null,
                   var followCirclecount: Int = 0,
                   var discountcount: Int = 0,
                   var collectioncount: Int = 0,
                   var quizcount: Int = 0,
                   var ordercount: Int = 0)

/**
 * 优惠券
 */
class Coupon(
        @SerializedName(value = "key", alternate = ["coupon"])
        var key: String,
        @SerializedName(value = "name", alternate = ["title"])
        var name: String,
        var content: String,
        var status: Int,
        var facevalue: Double,
        var validitytime: String,
        var goodslist: List<Product>) : Serializable

class Product(var id: String,
              var name: String)