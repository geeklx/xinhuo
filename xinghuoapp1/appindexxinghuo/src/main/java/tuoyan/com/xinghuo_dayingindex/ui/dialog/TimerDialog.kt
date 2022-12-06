package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.dialog_speed.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.ui.video.adapter.TimerAdapter

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class TimerDialog(
    context: Context,
    private val click: (Int) -> Unit
) : Dialog(context, R.style.custom_dialog) {
    val timer = listOf(-2, -1, 15, 30, 60)
    private val adapter by lazy {
        TimerAdapter {
            click(it)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_speed)
        tv_title.text = "定时关闭"
        img_close.setOnClickListener { dismiss() }
        rlv_content.layoutManager = GridLayoutManager(context, 5)
        rlv_content.adapter = adapter
        adapter.setData(timer)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
    }
}