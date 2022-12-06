package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import tuoyan.com.xinghuo_dayingindex.R

/**
 * 创建者：
 * 时间：  2018/1/5.
 */
class CGCompleteDialog(context: Context) : Dialog(context, R.style.custom_dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            frameLayout {
                lparams(matchParent, matchParent)
//                backgroundResource = R.mipmap.bg_cg_complete
                setOnClickListener {
                    this@CGCompleteDialog.dismiss()
                }
            }
        })
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = matchParent
        lp?.height = matchParent
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }
}