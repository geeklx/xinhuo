package tuoyan.com.xinghuo_dayingindex.widegt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView


/**
 * Created by
 * 扇形倒计时控件
 * 单词专项 单词检测页面倒计时
 */

class SweepView : View {
    private var rectF: RectF? = null
    private var paint: Paint? = null
    private var mColor = Color.WHITE
    private var bgColor = Color.parseColor("#dbe6ff")
    private var bgPaint: Paint? = null

    private var mSweep = 0f

    /**
     * 设置扇形颜色
     * UIThred
     */
    fun setColor(color: Int) {
        this.mColor = color
        paint!!.color = mColor
        //调用onDraw重绘
        invalidate()
    }

    /**
     * 设置背景颜色
     * @param color
     */
    fun setBgColor(color: Int) {
        this.bgColor = color
        bgPaint!!.color = bgColor
        //调用onDraw重绘
        invalidate()
    }

    /**
     * 设置扇形的区域0-360
     *
     *
     * UIThread
     *
     * @param mSweep
     */
    fun setSweep(mSweep: Float) {
        this.mSweep = mSweep
        //调用onDraw重绘
        invalidate()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        bgPaint = Paint()
        bgPaint!!.color = bgColor //背景画笔颜色
        bgPaint!!.style = Paint.Style.FILL   //填充
        bgPaint!!.isAntiAlias = true   //是否抗锯齿

        paint = Paint()
        paint!!.color = mColor //倒计时区域画笔颜色
        paint!!.style = Paint.Style.FILL   //填充
        paint!!.isAntiAlias = true   //是否抗锯齿
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val resultWidth = measureWidth(widthMeasureSpec)

        val resultHeight = measureHeight(heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    /**
     * 绘制的宽
     */
    private fun measureWidth(widthMeasureSpec: Int): Int {
        val size = View.MeasureSpec.getSize(widthMeasureSpec)
        val mode = View.MeasureSpec.getMode(widthMeasureSpec)
        var result: Int
        if (mode == View.MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = DEFAULT_WIDTH
            if (mode == View.MeasureSpec.AT_MOST) {
                result = Math.min(size, DEFAULT_WIDTH)
            }

        }
        return result
    }

    /**
     * 绘制的高
     */
    private fun measureHeight(heightMeasureSpec: Int): Int {
        val size = View.MeasureSpec.getSize(heightMeasureSpec)
        val mode = View.MeasureSpec.getMode(heightMeasureSpec)
        var result: Int
        if (mode == View.MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = DEFAULT_HEIGHT
            if (mode == View.MeasureSpec.AT_MOST) {
                result = Math.min(size, DEFAULT_HEIGHT)
            }

        }
        return result
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rectF = RectF(0f, 0f, w.toFloat(), h.toFloat())
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawArc(rectF!!, -90f, 360f, true, bgPaint!!)
        canvas.drawArc(rectF!!, -90f, mSweep, true, paint!!)
    }

    companion object {
        private val DEFAULT_WIDTH = 100
        private val DEFAULT_HEIGHT = 100
    }
}

inline fun ViewManager.sweepView(): SweepView = sweepView() {}
inline fun ViewManager.sweepView(init: (SweepView).() -> Unit): SweepView {
    return ankoView({ SweepView(it) }, theme = 0) { init() }
}
