package com.spark.peak.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.spark.peak.R
import com.umeng.socialize.bean.SHARE_MEDIA
import org.jetbrains.anko.*

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class ShareDialog(context: Context,
                  private val click: (SHARE_MEDIA) -> Unit) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            verticalLayout {
                lparams(matchParent, wrapContent)
                dividerDrawable = resources.getDrawable(R.drawable.divider)
                backgroundResource = R.drawable.bg_share
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                linearLayout {
                    padding = dip(15)
                    textView("微信朋友圈") {
                        textColor = resources.getColor(R.color.color_999999)
                        gravity = Gravity.CENTER
                        textSize = 11f
                        compoundDrawablePadding = dip(10)
                        setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_we_chat_friends, 0, 0)
                        setOnClickListener {
                            dismiss()
                            click(SHARE_MEDIA.WEIXIN_CIRCLE)
                        }
                    }
                    space().lparams(0, wrapContent, 1f)
                    textView("微信好友") {
                        textColor = resources.getColor(R.color.color_999999)
                        gravity = Gravity.CENTER
                        textSize = 11f
                        compoundDrawablePadding = dip(10)
                        setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_we_chat, 0, 0)
                        setOnClickListener {
                            dismiss()
                            click(SHARE_MEDIA.WEIXIN)
                        }
                    }
                    space().lparams(0, wrapContent, 1f)
                    textView("QQ好友") {
                        textColor = resources.getColor(R.color.color_999999)
                        gravity = Gravity.CENTER
                        textSize = 11f
                        compoundDrawablePadding = dip(10)
                        setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_qq, 0, 0)
                        setOnClickListener {
                            dismiss()
                            click(SHARE_MEDIA.QQ)
                        }
                    }
                    space().lparams(0, wrapContent, 1f)
                    textView("QQ空间") {
                        textColor = resources.getColor(R.color.color_999999)
                        textSize = 11f
                        compoundDrawablePadding = dip(10)
                        gravity = Gravity.CENTER
                        setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_qq_zome, 0, 0)
                        setOnClickListener {
                            dismiss()
                            click(SHARE_MEDIA.QZONE)
                        }
                    }
                    space().lparams(0, wrapContent, 1f)
                    textView("新浪微博") {
                        textColor = resources.getColor(R.color.color_999999)
                        gravity = Gravity.CENTER
                        textSize = 11f
                        compoundDrawablePadding = dip(10)
                        setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_sina, 0, 0)
                        setOnClickListener {
                            dismiss()
                            click(SHARE_MEDIA.SINA)
                        }
                    }

                }.lparams(matchParent, wrapContent)
                textView("取消") {
                    textSize = 15f
                    textColor = resources.getColor(R.color.color_1e1e1e)
                    gravity = Gravity.CENTER
                    setOnClickListener {
                        dismiss()
                    }
                }.lparams(matchParent, dip(40))
            }
        })

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
//        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp
        window?.setBackgroundDrawable(null)
        window?.setGravity(Gravity.BOTTOM)
    }
}