package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_googdslist_item.view.*
import org.jetbrains.anko.matchParent
import tuoyan.com.xinghuo_dayingindex.R
import java.util.*

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class StateDescriptionDialog(context: Context, str: String) :
    Dialog(context, R.style.custom_dialog) {
    val message = str;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_state_description_dialog)
        val tv = findViewById<TextView>(R.id.tv)
        tv.text = message;
        tv.setOnClickListener({
            dismiss()
        })


    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
//        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.width = matchParent
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }
    var timer: Timer? = null


    override fun dismiss() {
        super.dismiss()
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }

    }
    override fun show() {
        super.show()
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                dismiss()
            }
        }, (1000 * 3).toLong(), 3000)
    }
}