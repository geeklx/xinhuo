package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.dialog_share.*
import tuoyan.com.xinghuo_dayingindex.R

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class ShareDialog(
    context: Context,
    private val click: (SHARE_MEDIA) -> Unit
) : Dialog(context, R.style.custom_dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_share)
        tv_sina.setOnClickListener { click(SHARE_MEDIA.SINA) }
        tv_wx.setOnClickListener { click(SHARE_MEDIA.WEIXIN) }
        tv_wx_friends.setOnClickListener { click(SHARE_MEDIA.WEIXIN_CIRCLE) }
        tv_wx_collect.setOnClickListener { click(SHARE_MEDIA.WEIXIN_FAVORITE) }
        tv_qq.setOnClickListener { click(SHARE_MEDIA.QQ) }
        tv_zome.setOnClickListener { click(SHARE_MEDIA.QZONE) }
        img_close.setOnClickListener { dismiss() }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
    }
}