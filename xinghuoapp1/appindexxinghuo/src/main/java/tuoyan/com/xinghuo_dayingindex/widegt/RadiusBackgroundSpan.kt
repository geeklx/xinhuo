package tuoyan.com.xinghuo_dayingindex.widegt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import androidx.core.content.ContextCompat
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * 网课倒计时文本数字背景
 * Created by Zzz on 2020/7/22
 * Email:
 */

class RadiusBackgroundSpan(context: Context) : ReplacementSpan() {

    private var mContext: Context = context

    //    文本背景宽高
    private var mSize = DeviceUtil.dp2px(mContext, 20f).toInt()

    //圆角半径
    private var mRadius: Float = DeviceUtil.dp2px(mContext, 3f)

    //背景颜色
    private var mColor: Int = ContextCompat.getColor(mContext, R.color.color_4d000000)

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
//        当前文字所占宽度
        return mSize
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        paint.color = mColor//设置背景颜色
        paint.isAntiAlias = true//设置画笔锯齿效果
        var txtHeight = paint.descent() - paint.ascent()
        var tvWidth = paint.measureText(text, start, end)
        var rectf =
            RectF(
                x,
                y + paint.ascent() - (mSize - txtHeight) / 2,
                x + mSize,
                y + paint.descent() + (mSize - txtHeight) / 2
            )
        //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。
        // paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘，上下居中
        canvas.drawRoundRect(rectf, mRadius, mRadius, paint)//绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
        paint.color = ContextCompat.getColor(mContext, R.color.white)
        if (text != null) {
            canvas.drawText(text, start, end, x + (mSize - tvWidth) / 2, y.toFloat(), paint)
        }//回值文本居中展示
    }
}