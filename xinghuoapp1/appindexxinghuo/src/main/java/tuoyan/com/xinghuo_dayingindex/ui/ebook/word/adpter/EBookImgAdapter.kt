package tuoyan.com.xinghuo_dayingindex.ui.ebook.word.adpter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.EBookImg
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils

/**
 * 1 整条点击事件 2视频点击事件
 */
class EBookImgAdapter(val onClick: (Int, EBookImg) -> Unit) : BaseAdapter<EBookImg>(isFooter = true) {
    override fun convert(holder: ViewHolder, item: EBookImg) {
        val context = holder.itemView.context
        val imgView = holder.getView(R.id.img_word) as ImageView
        try {
            Glide.with(context).asBitmap().load(item.imgUrl).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val height = resource.height
                    val width = resource.width
                    val params = imgView.layoutParams as ConstraintLayout.LayoutParams
                    params.dimensionRatio = "w,$width:$height"
                    imgView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        } catch (e: Exception) {
        }
        holder.setVisible(R.id.img_video, if (item.videoUrl.isNotBlank() && NetWorkUtils.isNetWorkReachable()) View.VISIBLE else View.GONE)
            .setOnClickListener(R.id.img_video) {
                onClick(2, item)
            }
            .itemView.setOnClickListener {
                if (item.audioUrl.isNotBlank()) {
                    onClick(1, item)
                }
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_ebook_img, parent, false)
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }
}