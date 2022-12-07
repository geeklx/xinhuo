package com.spark.peak.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.LinearLayout
import com.spark.peak.R
import org.jetbrains.anko.*

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class SelectedDialog(val title1: String,
                     val title2: String,
                     context: Context,
                     val title3: String = "取消",
                     val click: (Int) -> Unit) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            verticalLayout {
                lparams(matchParent, matchParent)
                verticalLayout {
                    dividerDrawable = resources.getDrawable(R.drawable.divider)
                    showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                    backgroundResource = R.drawable.bg_selected_dialog
                    horizontalPadding = dip(5)
                    textView(title1) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_1e1e1e)
                        setOnClickListener {
                            dismiss()
                            click(1)
                        }
                    }.lparams(matchParent, dip(50))
                    textView(title2) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_1e1e1e)
                        setOnClickListener {
                            dismiss()
                            click(2)
                        }
                    }.lparams(matchParent, dip(50))
                }.lparams(matchParent, wrapContent)
                space().lparams(wrapContent, dip(5))
                button(title3) {
                    textSize = 15f
                    gravity = Gravity.CENTER
                    padding=0
                    textColor = resources.getColor(R.color.color_1e1e1e)
                    backgroundResource = R.drawable.bg_selected_dialog
                    setOnClickListener {
                        dismiss()
                        click(3)
                    }
                }.lparams(matchParent, dip(50))
                space().lparams(wrapContent, dip(15))
            }
        })

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
//        lp.width=WindowManager.LayoutParams.MATCH_PARENT;
        window?.attributes = lp
        window?.setBackgroundDrawable(null)
        window?.setGravity(Gravity.BOTTOM)
    }
}