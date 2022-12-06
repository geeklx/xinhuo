package tuoyan.com.xinghuo_dayingindex.utlis

import android.content.Context
import android.widget.Toast
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import tuoyan.com.xinghuo_dayingindex.WX_APPID
import tuoyan.com.xinghuo_dayingindex.WX_PROGRAM_ID_SPARKE

/**
 * Created by Zzz on 2020/11/9
 * Email:
 */

class WxMini {
    companion object {
        fun goWxMini(context: Context, id: String) {
            goWxMini(context, id, "")
        }

        fun goWxMini(context: Context, id: String, imgUrl: String) {
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
            // 网课小程序 打开特定页面
            if (imgUrl.isNotBlank() && id == WX_PROGRAM_ID_SPARKE) {
                req.path = "${ADD_US}${imgUrl}"
            }
            // 可选打开 开发版，体验版和正式版
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
            api.openWXApp()
            api.sendReq(req)
        }

        private const val ADD_US = "pages/addWx/addWx?qrCode="
    }
}