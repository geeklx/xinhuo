package tuoyan.com.xinghuo_dayingindex.widegt

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil


/**
 * Created by Zzz on 2021/6/25
 * Email:
 */

class SectionDecoration(var context: Context) : ItemDecoration() {
    private val textPaint by lazy { TextPaint() }
    private val paint by lazy { Paint() }
    private var topGap = 0
    private val dataList by lazy { mutableListOf<String>() }

    fun setDataList(list: List<String>) {
        this.dataList.clear()
        this.dataList.addAll(list)
    }

    init {
        textPaint.typeface = Typeface.DEFAULT_BOLD;
        textPaint.isAntiAlias = true;
        textPaint.textSize = DeviceUtil.dp2px(context, 15f)
        textPaint.color = Color.BLACK;
        textPaint.textAlign = Paint.Align.LEFT;
        paint.color = Color.WHITE;
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val itemCount = state.itemCount
        val childCount = parent.childCount
        val left = parent.paddingLeft

        for (index in 0 until childCount) {
            val view = parent.getChildAt(index)
            val pos = parent.getChildAdapterPosition(view)
            val viewBottom = view.bottom
            var textY = Math.max(topGap, view.top)
            if (pos + 1 < itemCount && viewBottom < textY) {
                textY = viewBottom
            }

            getBitmap(context, R.drawable.shape_month)?.let { c.drawBitmap(it, left.toFloat(), textY.toFloat(), paint) }
            if (pos < this.dataList.size) {
                c.drawText(this.dataList.get(pos), DeviceUtil.dp2px(context, 25f), textY + DeviceUtil.dp2px(context, 30f), textPaint)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view)
        if (pos != 0) {
            outRect.top = DeviceUtil.dp2px(context, 5f).toInt()
        } else {
            outRect.top = 0
        }
    }

    private fun getBitmap(context: Context, vectorDrawableId: Int): Bitmap? {
        var bitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val vectorDrawable = context.getDrawable(vectorDrawableId)
            bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
        } else {
            bitmap = BitmapFactory.decodeResource(context.resources, vectorDrawableId)
        }
        return bitmap
    }
}