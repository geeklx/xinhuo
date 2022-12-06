package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import kotlinx.android.synthetic.main.dialog_ebook_word.*
import org.jetbrains.anko.dip
import tuoyan.com.xinghuo_dayingindex.R

class EBookWordDialog(context: Context, val onClick: () -> Unit) : Dialog(context, R.style.custom_dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_ebook_word)
        tv_ok.setOnClickListener {
            dismiss()
            onClick()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window?.setGravity(Gravity.TOP)
        val lp = window?.attributes
        lp?.width = context.dip(230f)
        lp?.verticalMargin = 0.26f
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}


