package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.ImageView
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageBitmap
import tuoyan.com.xinghuo_dayingindex.R

/**
 * Created by Zzz on 2021/6/16
 * Email:
 */

class QRDialog(context: Context, val bitmap: Bitmap) : Dialog(context, R.style.custom_dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_qr)
        val img_qr = findViewById<ImageView>(R.id.img_qr)
        img_qr.imageBitmap = bitmap
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.dip(265)
        window?.attributes = lp
        window?.setDimAmount(0.6f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }
}