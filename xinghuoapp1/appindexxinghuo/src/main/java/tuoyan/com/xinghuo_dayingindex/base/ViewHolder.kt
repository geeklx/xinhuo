package tuoyan.com.xinghuo_dayingindex.base

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.util.Linkify
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import tuoyan.com.xinghuo_dayingindex.R


class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val views by lazy { SparseArray<View>() }

    fun setText(@IdRes viewId: Int, text: CharSequence?): ViewHolder {
        val view = getView(viewId)
        if (view is TextView) {
            view.text = text ?: ""
            return this
        }
        throw ClassCastException("目标不是TextView")
    }

    fun setText(@IdRes viewId: Int, text: String?): ViewHolder {
        val view = getView(viewId)
        if (view is TextView) {
            view.text = Html.fromHtml(text ?: "")
            return this
        }
        throw ClassCastException("目标不是TextView")
    }

    fun setText(@IdRes viewId: Int, @StringRes resId: Int): ViewHolder {
        val view = getView(viewId)
        if (view is TextView) {
            view.setText(resId)
            return this
        }
        throw ClassCastException("目标不是TextView")
    }

    fun setImageResource(@IdRes viewId: Int, @DrawableRes resId: Int): ViewHolder {
        val view = getView(viewId)
        if (view is ImageView) {
            view.setImageResource(resId)
            return this
        }
        throw ClassCastException("目标不是ImageView")
    }

    fun setContentDescription(@IdRes viewId: Int, content: String): ViewHolder {
        val view = getView(viewId)
        if (view is ImageView) {
            view.contentDescription = content
            return this
        }
        throw ClassCastException("目标不是ImageView")
    }

    /**
     * @param url A file path, or a uri or url handled by {@link com.bumptech.glide.load.model.UriLoader}.
     */
    fun setImageUrl(@IdRes viewId: Int, url: String, @DrawableRes placeHolder: Int = 0, radius: Int = 3): ViewHolder {
        val view = getView(viewId)
        if (view is ImageView) {
            if (radius == 0)
                if (placeHolder == 0)
                    Glide.with(view.context)
                        .load(url).into(view)
                else
                    Glide.with(view.context)
                        .load(url).placeholder(placeHolder).into(view)
            else
                if (placeHolder == 0)
                    Glide.with(view.context)
//                            .asBitmap()
                        .load(url)
                        .centerCrop()
                        .transform(RoundedCorners((Resources.getSystem().displayMetrics.density * radius).toInt()))
                        .transition(DrawableTransitionOptions.withCrossFade(200))
                        .into(view)
                else
                    Glide.with(view.context)
//                            .asBitmap()
                        .load(url)
                        .placeholder(placeHolder)
                        .error(placeHolder)
                        .centerCrop()
                        .transform(RoundedCorners((Resources.getSystem().displayMetrics.density * radius).toInt()))
                        .transition(DrawableTransitionOptions.withCrossFade(200))
                        .into(view)
            return this
        }
        throw ClassCastException("目标不是ImageView")
    }

    fun setCircularImage(@IdRes viewId: Int, url: String): ViewHolder {
        val view = getView(viewId)
        if (view is ImageView) {
            Glide.with(view.context)
                .asBitmap()
                .load(url)
                .placeholder(R.mipmap.ic_avatar)
                .error(R.mipmap.ic_avatar)
                .centerCrop()
                .into(object : BitmapImageViewTarget(view) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(view.context.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        view.setImageDrawable(circularBitmapDrawable)
                    }
                })
            return this
        }
        throw ClassCastException("目标不是ImageView")
    }

    fun setCircularImage(@IdRes viewId: Int, @DrawableRes url: Int): ViewHolder {
        val view = getView(viewId)
        if (view is ImageView) {
            Glide.with(view.context)
                .asBitmap()
                .load(url)
                .placeholder(R.mipmap.ic_avatar)
                .error(R.mipmap.ic_avatar)
                .centerCrop()
                .into(object : BitmapImageViewTarget(view) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(view.context.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        view.setImageDrawable(circularBitmapDrawable)
                    }
                })
            return this
        }
        throw ClassCastException("目标不是ImageView")
    }

    fun setBackgrounColor(@IdRes viewId: Int, @ColorInt color: Int): ViewHolder {
        getView(viewId).setBackgroundColor(color)
        return this
    }

    fun setBackgroundResource(viewId: Int, @DrawableRes backgroundRes: Int): ViewHolder {
        getView(viewId).setBackgroundResource(backgroundRes)
        return this
    }

    fun setTextColors(@IdRes viewId: Int, @ColorRes colors: Int): ViewHolder {
        val view = getView(viewId)
        if (view is TextView) {
            view.setTextColor(ContextCompat.getColorStateList(view.context, colors))
            return this
        }
        throw ClassCastException("目标不是TextView")
    }

    fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int): ViewHolder {
        val view = getView(viewId)
        if (view is TextView) {
            view.setTextColor(color)
            return this
        }
        throw ClassCastException("目标不是TextView")
    }

    fun setTextColorRes(@IdRes viewId: Int, @ColorRes color: Int): ViewHolder {
        val view = getView(viewId)
        if (view is TextView) {
            view.setTextColor(view.resources.getColor(color))
            return this
        }
        throw ClassCastException("目标不是TextView")
    }

    fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable): ViewHolder {
        val view = getView(viewId)
        if (view is ImageView) {
            view.setImageDrawable(drawable)
            return this
        }
        throw ClassCastException("目标不是ImageView")
    }

    fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap): ViewHolder {
        val view = getView(viewId)
        if (view is ImageView) {
            view.setImageBitmap(bitmap)
            return this
        }
        throw ClassCastException("目标不是ImageView")
    }

    fun setAlpha(@IdRes viewId: Int, alpha: Float): ViewHolder {
        getView(viewId).alpha = alpha
        return this
    }

    fun setVisible(@IdRes viewId: Int, @Visibility visibility: Int): ViewHolder {
        getView(viewId).visibility = visibility
        return this
    }

    fun getVisible(@IdRes viewId: Int): Int {
        return getView(viewId).visibility

    }

    fun linkify(@IdRes viewId: Int): ViewHolder {
        val view = getView(viewId)
        if (view is TextView) {
            Linkify.addLinks(view, Linkify.ALL)
            return this
        }
        throw ClassCastException("目标不是ImageView")
    }

    fun setTypeface(@IdRes viewId: Int, typeface: Typeface): ViewHolder {
        val view = getView(viewId)
        if (view is TextView) {
            view.typeface = typeface
            view.paintFlags = view.paintFlags or 128
            return this
        }
        throw ClassCastException("目标不是TextView")
    }

    fun setTypeface(typeface: Typeface, @IdRes vararg viewId: Int): ViewHolder {
        for (id in viewId) {
            setTypeface(id, typeface)
        }
        return this
    }


    fun setOnClickListener(@IdRes viewId: Int, onClick: (View) -> Unit = {}): ViewHolder {
        getView(viewId).setOnClickListener { onClick(it) }
        return this
    }

    fun setSelected(@IdRes viewId: Int, selected: Boolean): ViewHolder {
        getView(viewId).isSelected = selected
        return this
    }

    fun isSelect(@IdRes viewId: Int): Boolean {
        return getView(viewId).isSelected
    }

    fun setOnClickListener(listener: View.OnClickListener, @IdRes vararg viewIds: Int): ViewHolder {
        for (id in viewIds) {
            getView(id).setOnClickListener(listener)
        }
        return this
    }

    fun setOnLongClickListener(@IdRes viewId: Int, listener: View.OnLongClickListener): ViewHolder {
        getView(viewId).setOnLongClickListener(listener)
        return this
    }

    fun setAdapter(@IdRes viewId: Int, adapter: RecyclerView.Adapter<*>): ViewHolder {
        val view = getView(viewId)
        if (view is RecyclerView) {
            view.adapter = adapter
            return this
        }
        throw ClassCastException(view.javaClass.simpleName + "不能转换成" + RecyclerView::class.java.simpleName)
    }

    fun getView(@IdRes viewId: Int): View {
        var view = views.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view
    }

    /**
     * @hide
     */
    @IntDef(View.VISIBLE, View.INVISIBLE, View.GONE)
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class Visibility
}
//
//class GlideRoundTransform(dp: Int = 4) : BitmapTransformation() {
//
//    private val radius: Float = Resources.getSystem().displayMetrics.density * dp
//
//    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
//        val bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight)
//        return roundCrop(pool, bitmap)
//    }
//
//    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
//
//    }
//
//
//    private fun roundCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
//        if (source == null) return null
//
//        var result: Bitmap? = pool.get(source.width, source.height, Bitmap.Config.ARGB_8888)
//        if (result == null) {
//            result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
//        }
//
//        val canvas = Canvas(result)
//        val paint = Paint()
//        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
//        paint.isAntiAlias = true
//        val rectF = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
//        canvas.drawRoundRect(rectF, radius, radius, paint)
//        return result
//    }
//
//}
