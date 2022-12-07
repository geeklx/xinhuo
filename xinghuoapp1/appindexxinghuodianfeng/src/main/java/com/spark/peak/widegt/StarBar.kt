package com.spark.peak.widegt

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.spark.peak.R
import org.jetbrains.anko.dip

/**
 * 创建人: 霍述雷
 * 时 间:2017/1/22 18:08.
 */

class StarBar : View {
    var starDistance = dip(15)//星星间距
    var starCount = 5  //星星个数
    var starSize: Int = dip(16)    //星星高度大小，星星一般正方形，宽度等于高度
    /**
     * 获取显示星星的数目

     * @return starMark
     */
    /**
     * 设置显示的星星的分数

     */
    //调用监听接口
    private var starMark = 5.0f
        set(mark) {
            field = if (integerMark) {
                Math.ceil(mark.toDouble()).toInt().toFloat()
            } else {
                Math.round(mark * 10) * 1.0f / 10
            }
            onStarChangeListener?.onStarChange(this.starMark)
            invalidate()
        }   //评分星星
    private val starFillBitmap by lazy { drawableToBitmap(resources.getDrawable(R.mipmap.star_full)) }
    private val starEmptyDrawable: Drawable? by lazy { resources.getDrawable(R.mipmap.star_empty) }//暗星星
    private var onStarChangeListener: OnStarChangeListener? = null//监听星星变化接口
    private val paint by lazy {
        with(Paint()) {
            isAntiAlias = true
            shader = BitmapShader(starFillBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            this
        }
    }       //绘制星星画笔
    private var integerMark = true
    private var indicator: Boolean = true

    constructor(context: Context?) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 初始化UI组件
     */
    init {
        isClickable = true
    }

    /**
     * 设置是否需要整数评分

     * @param integerMark
     */
    fun setIntegerMark(integerMark: Boolean) {
        this.integerMark = integerMark
    }

    fun setIndicator(indicator: Boolean) {
        this.indicator = indicator
    }


    /**
     * 定义星星点击的监听接口
     */
    interface OnStarChangeListener {
        fun onStarChange(mark: Float)
    }

    /**
     * 设置监听

     * @param onStarChangeListener
     */
    fun setOnStarChangeListener(onStarChangeListener: OnStarChangeListener) {
        this.onStarChangeListener = onStarChangeListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(starSize * starCount + starDistance * (starCount - 1), starSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (starEmptyDrawable == null) {
            return
        }
        for (i in 0 until starCount) {
            starEmptyDrawable?.setBounds((starDistance + starSize) * i, 0, (starDistance + starSize) * i + starSize, starSize)
            starEmptyDrawable?.draw(canvas)
        }
        if (this.starMark > 1) {
            canvas.drawRect(0f, 0f, starSize.toFloat(), starSize.toFloat(), paint)
            if (this.starMark - this.starMark.toInt() == 0f) {
                var i = 1
                while (i < this.starMark) {
                    canvas.translate((starDistance + starSize).toFloat(), 0f)
                    canvas.drawRect(0f, 0f, starSize.toFloat(), starSize.toFloat(), paint)
                    i++
                }
            } else {
                var i = 1
                while (i < this.starMark - 1) {
                    canvas.translate((starDistance + starSize).toFloat(), 0f)
                    canvas.drawRect(0f, 0f, starSize.toFloat(), starSize.toFloat(), paint)
                    i++
                }
                canvas.translate((starDistance + starSize).toFloat(), 0f)
                canvas.drawRect(0f, 0f, starSize * (Math.round((this.starMark - this.starMark.toInt()) * 10) * 1.0f / 10), starSize.toFloat(), paint!!)
            }
        } else {
            canvas.drawRect(0f, 0f, starSize * this.starMark, starSize.toFloat(), paint)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (indicator) {
            var x = event.x.toInt()

            if (x < 0) x = 0
            if (x > measuredWidth) x = measuredWidth
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    starMark = x * 1.0f / (measuredWidth * 1.0f / starCount)
                }
                MotionEvent.ACTION_MOVE -> {
                    starMark = x * 1.0f / (measuredWidth * 1.0f / starCount)
                }
                MotionEvent.ACTION_UP -> {
                }
            }
            invalidate()
        }
        return super.onTouchEvent(event)
    }

    /**
     * drawable转bitmap

     * @param drawable
     * *
     * @return
     */
    private fun drawableToBitmap(drawable: Drawable?): Bitmap {
//        if (drawable == null) return null
        val bitmap = Bitmap.createBitmap(starSize, starSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, starSize, starSize)
        drawable?.draw(canvas)
        return bitmap
    }
}

//inline fun ViewManager.starBar(theme: Int = 0, init: StarBar.() -> Unit): StarBar {
//    return ankoView({ StarBar(it) }, theme) { init() }
//}

