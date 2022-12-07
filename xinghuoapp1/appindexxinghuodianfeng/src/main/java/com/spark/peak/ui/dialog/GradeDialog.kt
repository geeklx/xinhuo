package com.spark.peak.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.spark.peak.R
import org.jetbrains.anko.*

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class GradeDialog(val title1: String,
                  val title2: String,
                  val title3: String,
                  val title4: String,
                  val title5: String,
                  context: Context,
                  val title6: String = "取消",
                  val click: (String) -> Unit) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            verticalLayout {
                lparams(matchParent, matchParent)
                verticalLayout {
                    dividerDrawable = resources.getDrawable(R.drawable.divider)
                    showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                    dividerPadding = dip(15)
                    backgroundResource = R.color.color_f2f2f2
                    horizontalPadding = dip(5)
                    textView(title1) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222831)
                        visibility = if (title1 =="") View.GONE else View.VISIBLE
                        setOnClickListener {
                            dismiss()
                            click(title1)
                        }
                    }.lparams(matchParent, dip(55))
                     textView(title2) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222831)
                         visibility = if (title2 =="") View.GONE else View.VISIBLE
                         setOnClickListener {
                            dismiss()
                            click(title2)
                        }
                    }.lparams(matchParent, dip(55))
                     textView(title3) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222831)
                         visibility = if (title3 =="") View.GONE else View.VISIBLE

                         setOnClickListener {
                            dismiss()
                            click(title3)
                        }
                    }.lparams(matchParent, dip(55))
                     textView(title4) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222831)
                         visibility = if (title4 =="") View.GONE else View.VISIBLE

                         setOnClickListener {
                            dismiss()
                            click(title4)
                        }
                    }.lparams(matchParent, dip(55))
                     textView(title5) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222831)
                         visibility = if (title5 =="") View.GONE else View.VISIBLE

                         setOnClickListener {
                            dismiss()
                            click(title5)
                        }
                    }.lparams(matchParent, dip(55))

                }.lparams(matchParent, wrapContent)
//                space().lparams(wrapContent, dip(5))
                button(title6) {
                    textSize = 15f
                    gravity = Gravity.CENTER
                    padding = 0
                    typeface = Typeface.DEFAULT_BOLD
                    textColor = resources.getColor(R.color.color_222831)
                    backgroundResource = R.color.color_ffffff
                    setOnClickListener {
                        dismiss()
//                        click("")
                    }
                }.lparams(matchParent, dip(55))
//                space().lparams(wrapContent, dip(15))
            }
        })

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
//        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.width= matchParent
        window?.attributes = lp
        window?.setBackgroundDrawable(null)
        window?.setGravity(Gravity.BOTTOM)
    }
}