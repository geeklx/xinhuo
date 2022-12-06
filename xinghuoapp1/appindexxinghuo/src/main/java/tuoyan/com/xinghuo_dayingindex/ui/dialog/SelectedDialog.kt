package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.LinearLayout
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class SelectedDialog(
    val title1: String,
    val title2: String,
    context: Context,
    val title3: String = "取消",
    val click: (Int) -> Unit
) : Dialog(context, R.style.custom_dialog) {
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
                        textColor = resources.getColor(R.color.color_222)
                        setOnClickListener {
                            dismiss()
                            click(1)
                        }
                    }.lparams(matchParent, dip(55))
                    textView(title2) {
                        textSize = 15f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222)
                        setOnClickListener {
                            dismiss()
                            click(2)
                        }
                    }.lparams(matchParent, dip(55))
                }.lparams(matchParent, wrapContent)
//                space().lparams(wrapContent, dip(5))
                button(title3) {
                    textSize = 15f
                    gravity = Gravity.CENTER
                    padding = 0
                    typeface = Typeface.DEFAULT_BOLD
                    textColor = resources.getColor(R.color.color_222)
                    backgroundResource = R.color.color_ffffff
                    setOnClickListener {
                        dismiss()
                        click(3)
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
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
    }
}