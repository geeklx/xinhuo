package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import org.jetbrains.anko.dip
import tuoyan.com.xinghuo_dayingindex.R

/**
 *
 */
class DDialog(
    context: Context
) : Dialog(context, R.style.custom_dialog) {

    private var msg = ""
    private var width = 210
    private var gravity = Gravity.CENTER
    private var positiveName = "确定"
    private var negativeName = "取消"
    private var listenerPositive: View.OnClickListener? = null
    private var listenerNegative: View.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_d_dialog)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        initView()
    }

    private fun initView() {
        val tvMsg = findViewById<TextView>(R.id.tv_text)
        tvMsg.text = Html.fromHtml(msg)
        tvMsg.gravity = gravity
        val tv2 = findViewById<TextView>(R.id.tv_2)
        tv2.text = positiveName
        tv2.setOnClickListener(listenerPositive)
        val tv1 = findViewById<TextView>(R.id.tv_1)
        tv1.text = negativeName
        tv1.setOnClickListener(listenerNegative)
    }

    fun setMessage(msg: String): DDialog {
        this.msg = msg
        return this
    }

    fun setGravity(gravity: Int): DDialog {
        this.gravity = gravity
        return this
    }

    fun setWidth(width: Int): DDialog {
        this.width = width
        return this
    }

    fun setPositiveButton(name: String, listener: View.OnClickListener): DDialog {
        this.positiveName = name
        this.listenerPositive = listener
        return this
    }

    fun setNegativeButton(name: String, listener: View.OnClickListener): DDialog {
        this.negativeName = name
        this.listenerNegative = listener
        return this
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.dip(width)
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }
}