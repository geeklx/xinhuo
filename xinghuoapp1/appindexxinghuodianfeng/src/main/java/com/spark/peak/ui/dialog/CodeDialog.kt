package com.spark.peak.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Base64
import android.view.Gravity
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.spark.peak.R
import com.spark.peak.base.BasePresenter
import org.jetbrains.anko.*
import kotlin.properties.Delegates

/**
 * 创建者： 霍述雷
 * 时间：  2018/1/5.
 */
class CodeDialog(context: Context,
                 private val phone: String,
                 private val type: String,
                 private val presenter: BasePresenter,
                 private val determine: () -> Unit) : Dialog(context) {
    private var editText by Delegates.notNull<EditText>()
    private var imageView by Delegates.notNull<ImageView>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            verticalLayout {
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
//                dividerDrawable = resources.getDrawable(R.drawable.divider)
//                dividerPadding = dip(17)
                backgroundResource = R.drawable.bg_radius_w_20
                space().lparams(wrapContent, dip(20))
                textView("请输入下方图片验证码") {
                    gravity = Gravity.CENTER
                    textSize = 15f
                    textColor = resources.getColor(R.color.color_1e1e1e)
                }.lparams(matchParent, wrapContent)
                linearLayout {
                    horizontalPadding = dip(15)
                    topPadding = dip(20)
                    bottomPadding = dip(15)
                    editText = editText {
                        textColor = resources.getColor(R.color.color_1e1e1e)
                        gravity = Gravity.CENTER
                        padding = 0
                        backgroundResource = R.drawable.bg_shape_e6e6e6_r
                    }.lparams(0, matchParent, 1f)
                    space().lparams(dip(5), wrapContent)
                    imageView = imageView {
                        backgroundResource = R.color.color_a7a7a7
//                        imageBitmap = CodeUtils.createBitmap()
                        scaleType = ImageView.ScaleType.FIT_XY
                        setOnClickListener {
                            //                            imageBitmap = CodeUtils.createBitmap()
                            randomCode()
                        }
                    }.lparams(dip(85), matchParent)
                }.lparams(matchParent, dip(70))
                view { backgroundResource = R.drawable.divider }.lparams(matchParent, dip(1))
                frameLayout {
                    textView("取消") {
                        textSize = 14f
                        padding = 0
                        gravity = Gravity.CENTER
//                        backgroundResource = R.drawable.bg_card_radius_f2f2f2_40
                        backgroundResource = R.color.color_ffffff
                        textColor = resources.getColor(R.color.color_666666)
                        setOnClickListener { dismiss() }
                    }.lparams(dip(90), dip(30)) {
                        gravity = Gravity.CENTER_VERTICAL
                        leftMargin = dip(32)
                    }
                    view {
                        backgroundResource = R.drawable.divider
                    }.lparams(dip(1), matchParent) {
                        gravity = Gravity.CENTER
                    }
                    textView("确定") {
                        textSize = 14f
                        padding = 0
                        gravity = Gravity.CENTER
//                        backgroundResource = R.drawable.bg_card_radius_17c8ce_40
                        backgroundResource = R.color.color_ffffff
//                        textColor = resources.getColor(R.color.color_white)
                        setOnClickListener {
                            presenter.validateCode(phone, editText.text.toString().trim(), type, {
                                determine()
                                dismiss()
                            }) {
                                //                                ctx.toast("验证码错误")
                                editText.text = SpannableStringBuilder.valueOf("")
//                                imageView.imageBitmap = CodeUtils.createBitmap()
//                                randomCode()
                                dismiss()
                            }
//                            if (editText.text.toString().trim() == CodeUtils.code) {
//                                determine()
//                                dismiss()
//                            } else {
//                                ctx.toast("验证码错误")
//                                editText.text = SpannableStringBuilder.valueOf("")
////                                imageView.imageBitmap = CodeUtils.createBitmap()
//                                randomCode()
//                            }
                        }
                    }.lparams(dip(90), dip(30)) {
                        gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        rightMargin = dip(32)
                    }

                }.lparams(matchParent, dip(50))
            }
        })
        randomCode()// TODO: 2018/8/8 9:21 霍述雷
    }

    private fun randomCode() {
        presenter.randomCode(phone) {
            val s = it["codeImage"] ?: ""
            val bytes = Base64.decode(s, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageView.imageBitmap = bitmap
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.dip(285)
//        lp?.height = context.dip(133)
        window?.attributes = lp
        window?.setBackgroundDrawable(null)
        window?.setGravity(Gravity.CENTER)
    }
}