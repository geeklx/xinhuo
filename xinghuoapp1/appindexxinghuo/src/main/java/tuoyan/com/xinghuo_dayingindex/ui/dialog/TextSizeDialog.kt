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
import tuoyan.com.xinghuo_dayingindex.ui.video.adapter.TextSizeAdapter

/**
 */
class TextSizeDialog(
    context: Context,
    private val click: (String) -> Unit
) : Dialog(context, R.style.custom_dialog) {
    //14 16 18 21 24
    //12 14 15 18 20
    val textSizes = listOf("小号", "常规", "中号", "大号", "特大号")
    private val adapter by lazy {
        TextSizeAdapter {
            click(it)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_speed)
        tv_title.text = "字号大小"
        img_close.setOnClickListener { dismiss() }
        rlv_content.layoutManager = GridLayoutManager(context, 5)
        rlv_content.adapter = adapter
        adapter.setData(textSizes)
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