package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class GradeDialog(
    val title1: String,
    val title2: String,
    val title3: String,
    val title4: String,
    val title5: String,
    context: Context,
    val title6: String = "取消",
    val click: (String) -> Unit
) : Dialog(context, R.style.custom_dialog) {
    var ll_time: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            verticalLayout {
                lparams(matchParent, matchParent)
                verticalLayout {
                    id = R.id.ll_time
                    dividerDrawable = resources.getDrawable(R.drawable.divider)
                    showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                    dividerPadding = dip(15)
                    backgroundResource = R.color.color_f2f2f2
                    horizontalPadding = dip(5)
                    textView(title1) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222)
                        visibility = if (title1 == "") View.GONE else View.VISIBLE
                        setOnClickListener {
                            dismiss()
                            click(title1)
                            selColor()
                            textColor = resources.getColor(R.color.color_5467ff)
                        }
                    }.lparams(matchParent, dip(55))
                    textView(title2) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222)
                        visibility = if (title2 == "") View.GONE else View.VISIBLE
                        setOnClickListener {
                            dismiss()
                            click(title2)
                            selColor()
                            textColor = resources.getColor(R.color.color_5467ff)
                        }
                    }.lparams(matchParent, dip(55))
                    textView(title3) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222)
                        visibility = if (title3 == "") View.GONE else View.VISIBLE
                        setOnClickListener {
                            dismiss()
                            click(title3)
                            selColor()
                            textColor = resources.getColor(R.color.color_5467ff)
                        }
                    }.lparams(matchParent, dip(55))
                    textView(title4) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222)
                        visibility = if (title4 == "") View.GONE else View.VISIBLE
                        setOnClickListener {
                            dismiss()
                            click(title4)
                            selColor()
                            textColor = resources.getColor(R.color.color_5467ff)
                        }
                    }.lparams(matchParent, dip(55))
                    textView(title5) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222)
                        visibility = if (title5 == "") View.GONE else View.VISIBLE
                        setOnClickListener {
                            dismiss()
                            click(title5)
                            selColor()
                            textColor = resources.getColor(R.color.color_5467ff)
                        }
                    }.lparams(matchParent, dip(55))

                }.lparams(matchParent, wrapContent)
                button(title6) {
                    textSize = 15f
                    gravity = Gravity.CENTER
                    padding = 0
                    typeface = Typeface.DEFAULT_BOLD
                    textColor = resources.getColor(R.color.color_222)
                    backgroundResource = R.color.color_ffffff
                    setOnClickListener {
                        dismiss()
//                        click("")
                    }
                }.lparams(matchParent, dip(55))
            }
        })
        ll_time = findViewById<LinearLayout>(R.id.ll_time);
    }

    fun selColor() {
        ll_time?.let {
            var count = it.childCount
            for (index in 0 until count) {
                (it.getChildAt(index) as TextView).textColor = context.resources.getColor(R.color.color_222)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
//        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.width = matchParent
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
    }
}