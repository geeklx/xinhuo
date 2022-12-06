package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.LinearLayout
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R

/**
 * 创建者：
 * 时间：  2018/1/5.
 */
class AlertDialog(
    context: Context,
    val title: String,
    private val determine: () -> Unit
) : Dialog(context, R.style.custom_dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            verticalLayout {
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                dividerDrawable = resources.getDrawable(R.drawable.divider)
                dividerPadding = dip(17)
                backgroundResource = R.drawable.bg_radius_w_20
                textView(title) {
                    gravity = Gravity.CENTER
                    textSize = 15f
                    textColor = resources.getColor(R.color.color_333333)
                }.lparams(matchParent, dip(70))
                frameLayout {
                    textView("取消") {
                        textSize = 14f
                        padding = 0
                        gravity = Gravity.CENTER
//                        backgroundResource = R.drawable.bg_card_radius_f2f2f2_40
                        backgroundResource = R.color.color_ffffff
                        textColor = resources.getColor(R.color.color_1482ff)
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
                        textColor = resources.getColor(R.color.color_1482ff)
//                        textColor = resources.getColor(R.color.color_white)
                        setOnClickListener {
                            determine()
                            dismiss()
                        }
                    }.lparams(dip(90), dip(30)) {
                        gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        rightMargin = dip(32)
                    }

                }.lparams(matchParent, dip(50))
            }
        })
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.dip(285)
//        lp?.height = context.dip(133)
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }
}