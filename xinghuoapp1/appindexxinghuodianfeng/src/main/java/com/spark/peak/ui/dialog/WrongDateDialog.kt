package com.spark.peak.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import android.view.Window
import com.spark.peak.R
import com.spark.peak.ui.wrongbook.adapter.WrongDateAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class WrongDateDialog(context: Context, var mAdapter : WrongDateAdapter ) : Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context){
            verticalLayout {
                backgroundResource = R.drawable.shape_corner_white
                relativeLayout {
                    textView {
                        text = "请选择时间"
                        textColor = Color.parseColor("#1e1e1e")
                        textSize = 14f
                        gravity = Gravity.CENTER
                    }.lparams (matchParent,dip(45))

                    imageView {
                        imageResource = R.drawable.ic_cancel
                        setOnClickListener {
                            dismiss()
                        }
                        padding = dip(17)
                    }.lparams(dip(45),dip(45)){
                        alignParentRight()
                        gravity = Gravity.CENTER
                    }
                }

                view {
                    backgroundColor = Color.parseColor("#e6e6e6")
                }.lparams(matchParent,2)
                recyclerView {
                    layoutManager = LinearLayoutManager(context)
                    adapter = mAdapter
                }.lparams(matchParent, matchParent)
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.height = context.resources.displayMetrics.heightPixels / 10 * 6 //设置高度
//        lp.width=WindowManager.LayoutParams.MATCH_PARENT;
        window?.attributes = lp
        window?.setBackgroundDrawable(null)
        window?.setGravity(Gravity.CENTER)
    }
}