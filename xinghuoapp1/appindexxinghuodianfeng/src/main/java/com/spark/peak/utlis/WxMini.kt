package com.spark.peak.utlis

import android.content.Context
import android.widget.Toast
import com.spark.peak.WX_APPID
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * Created by Zzz on 2020/12/23
 * Email:zgqmax@foxmail.com
 */

class WxMini {
    companion object {
        fun goWxMini(context: Context, id: String) {
            //appid 应用AppId
            val api = WXAPIFactory.createWXAPI(context, WX_APPID)
            if (!api.isWXAppInstalled) {
                Toast.makeText(context, "跳转小程序需安装微信客户端", Toast.LENGTH_LONG).show()
                return
            }
            val req = WXLaunchMiniProgram.Req()
            //填小程序原始id
            req.userName = id
            //拉起小程序页面的可带参路径，不填默认拉起小程序首页，
            // 对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
            //req.path = ""
            // 可选打开 开发版，体验版和正式版
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
            api.openWXApp()
            api.sendReq(req)
        }
    }
}