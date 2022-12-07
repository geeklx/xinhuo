package com.spark.peak.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory


/**
 * 如果要使用该回调页面，去掉后缀_noUse即可
 */
class WXPayEntryActivity : Activity(), IWXAPIEventHandler {

    private var api: IWXAPI? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        api = WXAPIFactory.createWXAPI(this, "")
        api!!.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        api!!.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {

    }

    override fun onResp(resp: BaseResp) {
        if (resp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            onBackPressed()
            mWXPayResultListener?.let { it(resp.errCode) }
            mWXPayResultListener = null
        }
    }

    companion object {
        var mWXPayResultListener: ((Int) -> Unit)? = null
    }
}

