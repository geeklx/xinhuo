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
 * 创建者： 霍述雷
 * 时间：  2018/1/5.
 */
class CGCompleteDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            frameLayout {
                lparams(matchParent, matchParent)
                backgroundResource = R.mipmap.bg_cg_complete
                setOnClickListener {
                    this@CGCompleteDialog.dismiss()
                }
            }
        })
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = matchParent
        lp?.height = matchParent
        window?.attributes = lp
        window?.setBackgroundDrawable(null)
        window?.setGravity(Gravity.CENTER)
    }
}