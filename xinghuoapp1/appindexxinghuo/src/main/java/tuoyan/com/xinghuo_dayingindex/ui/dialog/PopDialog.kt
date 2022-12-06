package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.utlis.img.GlideImageLoader

/**
 * 创建者：
 * 时间：  2018/1/5.
 */
class PopDialog(
    context: Context,
    val url: String,
    private val determine: (Dialog) -> Unit, private val disMiss: () -> Unit
) : Dialog(context, R.style.custom_dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            verticalLayout {
                gravity = Gravity.CENTER_HORIZONTAL
                imageView() {
                    id = R.id.img
                    setOnClickListener {
                        determine(this@PopDialog)
                    }
                    val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .transform(RoundedCorners(dip(5)))
                    GlideImageLoader.create(this).load(url, requestOptions)
//                    Glide.with(context)
//                            .load(url)
////                            .placeholder(R.drawable.img_pop_place_holder)
////                            .error(R.drawable.img_pop_place_holder)
//                            .centerCrop()
//                            .transform(RoundedCorners(dip(5)))
//                            .transition(DrawableTransitionOptions.withCrossFade(200))
//                            .into(this)
                }.lparams(dip(280), dip(359))
                space().lparams(wrapContent, dip(35))
                imageView(R.drawable.ic_colse_pop) {
                    setOnClickListener {
                        dismiss()
                    }
                }.lparams(dip(37), dip(37))
            }
        })
        setOnDismissListener {
            disMiss()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.dip(285)
//        lp?.height = context.dip(133)
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }
}