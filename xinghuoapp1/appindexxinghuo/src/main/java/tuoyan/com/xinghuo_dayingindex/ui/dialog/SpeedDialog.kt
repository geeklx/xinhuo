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
import tuoyan.com.xinghuo_dayingindex.ui.video.adapter.SpeedAdapter

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class SpeedDialog(
    context: Context,
    private val click: (Float) -> Unit
) : Dialog(context, R.style.custom_dialog) {
    val speeds = listOf(0.5f, 0.8f, 1.0f, 1.2f, 1.5f, 1.8f, 2.0f)
    private val adapter by lazy {
        SpeedAdapter {
            click(it)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_speed)
        img_close.setOnClickListener { dismiss() }
        rlv_content.layoutManager = GridLayoutManager(context, 7)
        rlv_content.adapter = adapter
        adapter.setData(speeds)
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