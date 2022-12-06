package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import kotlinx.android.synthetic.main.dialog_ebook_img.*
import org.jetbrains.anko.dip
import tuoyan.com.xinghuo_dayingindex.R

class EBookImgDialog(context: Context) : Dialog(context, R.style.custom_dialog) {
    var mType = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_ebook_img)
        tv_next.setOnClickListener {
            if (mType == 3) {
                dismiss()
            } else {
                setType(++mType)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.dip(295f)
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    fun setType(type: Int) {
        when (type) {
            2 -> {
                tv1.text = "离线下载"
                tv2.text = "1.点击界面右上角的目录图标"
                img.setImageResource(R.mipmap.ebook_img2)
                tv_next.text = "下一项 (2/3)"
            }
            3 -> {
                tv1.text = "离线下载"
                tv2.text = "2.点击界面底部的离线下载按钮"
                img.setImageResource(R.mipmap.ebook_img3)
                tv_next.text = "知道了"
            }
        }
    }
}


