package com.spark.peak.net

import com.blankj.utilcode.util.AppUtils
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.BuildConfig
import com.spark.peak.utlis.SpUtil

class RequestHeader {
    /**
     * appId	    产品id	每一个app都会有一个唯一的id	"除web端调用的获取appid接口外，其他的任何一个接口调用都必须带有此字段"
     * v	        版本号
     * os	        操作系统	终端操作系统
     * terminalType	终端类型	"1（手机客户端）、2（wap端）、3（PC端）、4（微信端）"
     * channel	    安装渠道	主要针对安卓端
     * terminalid	访客id	终端唯一识别id,由终端生产
     * timestamp	时间戳	"前端时间戳，时间的前后误差不超过10分钟是有效"
     * token		登录用户的授权令牌
     * sign	        签名
     * page	        分页页数	正整数，从1开始计数	用于列表接口
     * step	        步长		用于列表接口
     * userId	    用户id	后台生成的用户唯一标识
     * model	    业务码	业务模块对应的业务码
     */
    var appId: String = ""  //210951669544977409  468095093884016960  211043216772939776
    var v: String? = AppUtils.getAppVersionName()
    var os: String = "android"
    var terminalType = 1
    var channel: String = ""
    var terminalid: String = if (SpUtil.uuid.isNullOrBlank()) "1" else SpUtil.uuid
    var timestamp: String = ""
    var token: String = ""
    var appSign: String = ""

    //    var page: Int? = null
//    var step: Int? = null
    var userId: String = ""

    //    var model: String? = null
    var anonymousId = SensorsDataAPI.sharedInstance().anonymousId
}
