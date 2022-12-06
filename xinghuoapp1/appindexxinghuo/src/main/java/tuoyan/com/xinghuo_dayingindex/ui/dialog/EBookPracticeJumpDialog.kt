package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_ebook_practice_jump.*
import tuoyan.com.xinghuo_dayingindex.R

class EBookPracticeJumpDialog(context: Context) : Dialog(context, R.style.custom_dialog) {
    private var type = 1
    private var num = 0
    private var fTitle = ""
    private var sTitle = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_ebook_practice_jump)
        initType()
        progress()
    }

    fun progress() {
        Handler().postDelayed({
            if (num > 20) {
                num = 0
                dismiss()
            } else {
                initView(num)
                num++
                progress()
            }
        }, 100)
    }

    fun setType(type: Int): EBookPracticeJumpDialog {
        this.type = type
        return this
    }

    fun setFTitle(title: String): EBookPracticeJumpDialog {
        this.fTitle = title
        return this
    }

    fun setSTitle(title: String): EBookPracticeJumpDialog {
        this.sTitle = title
        return this
    }

    fun initType() {
        when (type) {
            1 -> {
                tv1.isSelected = true
            }
            2 -> {
                pb1.progress = 10
                tv1.isSelected = true
                tv2.isSelected = true
                v1.isSelected = true
                img2_2.isSelected = true
            }
            3 -> {
                pb1.progress = 10
                pb2.progress = 10
                tv1.isSelected = true
                tv2.isSelected = true
                img2_2.isSelected = true
                tv3.isSelected = true
                img3_3.isSelected = true
                v1.isSelected = true
                v2.isSelected = true
            }
        }
        findViewById<TextView>(R.id.tv_p_title).text = fTitle
        findViewById<TextView>(R.id.tv_title).text = sTitle
    }

    fun initView(progress: Int) {
        when (type) {
            1 -> {
                pb1.progress = progress
            }
            2 -> {
                pb2.progress = progress
            }
            3 -> {
                pb3.progress = progress
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window!!.attributes = lp
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
    }
}