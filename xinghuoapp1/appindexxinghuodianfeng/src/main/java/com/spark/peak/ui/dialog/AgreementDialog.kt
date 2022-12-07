package com.spark.peak.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import com.spark.peak.R
import kotlin.system.exitProcess

/**
 * 第一次启动隐私协议
 */
class AgreementDialog(context: Context,
                      private val agreeClick: (type: Int) -> Unit,
                      private val disMiss: () -> Unit) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_agreement)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tv_disagree).setOnClickListener {
            exitProcess(0)
        }
        findViewById<TextView>(R.id.tv_agree).setOnClickListener {
            dismiss()
            disMiss()
        }
        var tv_warming = findViewById<TextView>(R.id.tv_warming)
        var warmingStr = SpannableStringBuilder()
        warmingStr.append("点击同意代表您已阅读并同意《用户服务协议》和《隐私政策》")
        var service = object : ClickableSpan() {
            override fun onClick(p0: View) {
                agreeClick(1)
            }
        }
        var personal = object : ClickableSpan() {
            override fun onClick(p0: View) {
                agreeClick(2)
            }
        }
        warmingStr.setSpan(service, 13, 21, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(personal, 22, 28, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(ForegroundColorSpan(Color.parseColor("#5467ff")), 13, 21, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        warmingStr.setSpan(ForegroundColorSpan(Color.parseColor("#5467ff")), 22, 28, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        tv_warming.text = warmingStr
        tv_warming.movementMethod = LinkMovementMethod.getInstance()
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
//        val lp = window?.attributes
//        lp?.width = context.dip(285)
//        window?.attributes = lp
        window?.setBackgroundDrawable(null)
        window?.setGravity(Gravity.CENTER)
    }
}