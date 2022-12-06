package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.TextView
import org.jetbrains.anko.dip
import tuoyan.com.xinghuo_dayingindex.R

/**
 * 课程服务课程资料领取提醒
 */
class ResWarmingDialog(context: Context) : Dialog(context, R.style.custom_dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_res_warming)
        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tv_ok).setOnClickListener {
            dismiss()
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.dip(265)
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }
}