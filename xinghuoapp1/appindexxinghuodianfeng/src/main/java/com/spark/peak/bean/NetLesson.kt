package com.spark.peak.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * 创建者： 霍述雷
 * 时间：  2018/5/16.
 */

/**
 * 提交订单
 */
class SubmitOrder(
        var goodkey: String,
        var goodtype: Int = 1,
        var price: String? = null,
        var actualprice: String? = null,
        var derate: String? = null,
        var discountkey: String? = null
)

/**
 * 订单列表
 */
class Order(var img: String,            //商品缩略图
            var title: String,          //商品名称
            var period: String,         //课时
            var status: Int,            //订单状态 订单状态，0 :未付款；1:已付款;;3:已评价;4:已失效
            var goodkey: String,        //商品主键
            var goodtype: String,       //商品类型
            var orderkey: String,       //订单主键
            var price: String,          //原价
            var buyers: String,          //人数
            var realprice: String? = null,      //实际价格
            var originalcost: String,   //原价
            var money: String,          //金额
            var validitytime: String,   //有效期
            var isunsub: Int,           //0:不支持退订
            var coupontitile: String,   //优惠券名称
            var facevalue: String,      //面值
            var discount: String,       //优惠后价格
            var ordernumber: String,    //订单编号
            var createtime: String,     //创建时间
            var paytime: String) : Serializable    //付款时间

/**
 * 网课详情
 */
class NetLesson(
        /**
         * videourl	    string	视频播放地址
         * title	    string	网课标题
         * content	    string	简介
         * price	    int	    原价
         * disprice	    int	    优惠价
         * buyers	    int	    购买人数
         * validitytime	string	有效期
         * details	    string	详情
         * isunsub	    int	    0:是否支持退订
         * period	    int	    课时
         */
        var buyers: String = "",
        var content: String = "",
        var details: String = "",
        var disprice: String,
        var isown: Int = 0,
        var iscomment: Int = 0,
        var chargetype: Int = 0,
        var isunsub: Int = 0,
        var period: String = "",
        var price: String = "",
        var state: String = "0",
        var iseffect: String = "0",//针对已购买，1过期，0未过期；否则为空
        var title: String = "",
        var coverimg: String = "",
        var coverTopImg: String = "",
        var validitytime: String = "",
        var videourl: String = "") : Serializable

/**
 * 网课资源
 */
class NetRes(
        var mediasize: String,
        var mediacount: String,
        var children: ArrayList<MyBookResource>
)

/**
 * 网课搜索item
 */
class NetLessonItem(
        /**
        img	string	网课图片
        key	string	网课主键
        order	int	序号
        title	string	网课名称
        buyers	int	购买人数
        period	int	课时
        isown	int	0:失效或未购买状态；1:已购买状态
        price	int	原价
        disprice	int	优惠价

         */
        var img: String = "",
        var key: String = "",
        var order: String = "",
        @SerializedName(value = "title", alternate = ["name"])
        var title: String = "",
        var buyers: String = "",
        var period: String = "",
        var isown: String = "",
        var price: String = "",
        var disprice: String = "") : Serializable

/**
 * 评论
 */
class Comment(
        var img: String,
        var name: String,
        var content: String,
        var time: String,
        var recunm: String,
        var likesnum: String,
        var commentkey: String,
        var isofficial: Int,
        var isthumbup: Int,
        var thumbnails: String,
        var section: String,
        var grade: String)