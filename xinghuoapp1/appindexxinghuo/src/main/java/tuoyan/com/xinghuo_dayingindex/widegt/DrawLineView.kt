package tuoyan.com.xinghuo_dayingindex.widegt

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import java.util.*
import kotlin.math.ceil


/**
 * Created on 2021/7/14
 */

class DrawLineView : View {

//    private val EFFECT_RADIUS = DeviceUtil.dp2px(context, 10f)

    /**
     * 纵坐标平均每一行的高度
     */
    private val ROW_HEIGHT = DeviceUtil.dp2px(context, 30f)

    //纵坐标文字宽度 居右展示
    private val V_WIDTH = DeviceUtil.dp2px(context, 40f)

    /**
     * 圆的半径 单位dp
     */
    private val CIRCLE_RADIUS = DeviceUtil.dp2px(context, 5.5f)
    private val INNER_RADIUS = DeviceUtil.dp2px(context, 2.5f)

    /**
     * 横线起始位置
     */
    private val X_START = DeviceUtil.dp2px(context, 60f)

    //文字大小
    private val TEXTSIZE = DeviceUtil.dp2px(context, 12f)

    //padding
    private val PADDING_TOP = DeviceUtil.dp2px(context, 21.5f)
    private val PADDING_RIGHT = DeviceUtil.dp2px(context, 35f)

    //底部文字距离顶部横线
    private val V_PADDING_TOP = DeviceUtil.dp2px(context, 16.5f)

    //y坐标的等分数量
    private val Y_SIZE = 5

    //文字高度用于横线居文字中间
    private var vHeight = 0

    //超过7个不画圆点
    private val DATA_SIZE = 7

    //超过7个 每一组画一个横向坐标
    private val GROUP_SIZE = 5

    /**
     * 画图区域的宽高
     */
    private var width = 0f
    private var height = DeviceUtil.dp2px(context, 225f)

    /**
     * 横坐标平均每一列的宽度
     */
    private var fColWidth = 0f

    /**
     * 纵坐标、横坐标的字体画笔
     */
    private var textPaint: Paint? = null

    /**
     * 画Y轴的刻度线
     */
    private var yLinePaint: Paint? = null

    /**
     * 画空心圆
     */
    private var circlePaint: Paint? = null
    private var innerPaint: Paint? = null

    /**
     * 折线画笔
     */
    private var linePaint: Paint? = null

    /**
     * 阴影画笔
     */
    private var shadowPaint: Paint? = null

    /**
     * 阴影走过的路径
     */
    private var shadowPath: Path? = null

    /**
     * x对应的数据 ，y对应的数据  数组length相同
     */
    private var yList: List<Float> = ArrayList()
    private var xList: List<String> = ArrayList()

    /**
     * Y轴的刻度
     */
    private val yRateList: MutableList<String> = ArrayList()

    /**
     * Y轴最大值
     */
    private var maxY = 1.0f

    /**
     * 纵向设置的Y每一段的值
     */
    private var verticalAnnualValueY = 0.0f

    private var xPoint: FloatArray? = null
    private var yPoint: FloatArray? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    /**
     * 初始化将使用到的参数
     */
    init {
//        val pathEffect = CornerPathEffect(EFFECT_RADIUS)
        textPaint = Paint()
        textPaint!!.color = ContextCompat.getColor(context, R.color.color_afb3bf)
        textPaint!!.isAntiAlias = true
        textPaint!!.textSize = TEXTSIZE

        yLinePaint = Paint()
        yLinePaint!!.color = ContextCompat.getColor(context, R.color.color_f9fafc)
        yLinePaint!!.isAntiAlias = true
        yLinePaint!!.strokeWidth = DeviceUtil.dp2px(context, 1f)

        circlePaint = Paint()
        circlePaint!!.color = ContextCompat.getColor(context, android.R.color.white)
        circlePaint!!.style = Paint.Style.FILL
        circlePaint!!.isAntiAlias = true

        linePaint = Paint()
        linePaint!!.color = ContextCompat.getColor(context, R.color.color_008aff)
        linePaint!!.strokeWidth = DeviceUtil.dp2px(context, 2f)
        linePaint!!.style = Paint.Style.STROKE
        linePaint!!.isAntiAlias = true
//        linePaint!!.pathEffect = pathEffect

        innerPaint = Paint()
        innerPaint!!.color = ContextCompat.getColor(context, R.color.color_008aff)
        innerPaint!!.style = Paint.Style.FILL
        innerPaint!!.isAntiAlias = true

        shadowPaint = Paint()
        shadowPaint!!.isAntiAlias = true
//        shadowPaint!!.pathEffect = pathEffect
    }

    /**
     * 设置将要绘制的数据
     *
     * type 0 :展示小时    1：展示正确率
     */
    fun setData(xValuesList: List<String>, yValuesList: List<Float>, type: String = "0") {
        xList = xValuesList
        yList = yValuesList
        maxY = if ("1" == type) {
            100.0f
        } else {
            1.0f
        }
        yRateList.clear()
        if ("0" == type) {
            for (i in yList.indices) {
                if (maxY < yList[i]) maxY = yList[i]
            }
            maxY = ceil(maxY.toDouble()).toFloat()
        }
        xPoint = FloatArray(xList.size)
        yPoint = FloatArray(xList.size)
        verticalAnnualValueY = maxY / 5
        for (i in Y_SIZE downTo 0) {
            yRateList.add(if ("1" == type) "${(verticalAnnualValueY * i).toInt()}%" else "${String.format("%.1f", (verticalAnnualValueY * i))}h")
        }
        val verticalContent = yRateList[0]
        val rect = Rect()
        textPaint!!.getTextBounds(verticalContent, 0, verticalContent.length, rect)
        vHeight = rect.height()
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        width = getWidth().toFloat()
        fColWidth = (width - X_START - PADDING_RIGHT) / (xList.size - 1)
        drawVertical(canvas)
        drawHorizontal(canvas)
        drawYLine(canvas)
        drawLine(canvas)
        drawCircle(canvas)
    }

    /**
     * 画y坐标刻度文字
     */
    private fun drawVertical(canvas: Canvas) {
        textPaint!!.textAlign = Paint.Align.RIGHT
        for (i in yRateList.indices) {
            val verticalContent = yRateList[i]
            canvas.drawText(verticalContent, V_WIDTH, i * ROW_HEIGHT + TEXTSIZE + PADDING_TOP, textPaint!!)
        }
    }

    /**
     * 画x坐标刻度文字
     */
    private fun drawHorizontal(canvas: Canvas) {
        textPaint!!.textAlign = Paint.Align.CENTER
        for (i in xList.indices) {
            val horizontalContent = xList[i]
            if (xList.size <= DATA_SIZE || (xList.size > DATA_SIZE && (i + 1) % GROUP_SIZE == 0)) {
                canvas.drawText(horizontalContent, X_START + i * fColWidth, Y_SIZE * ROW_HEIGHT + TEXTSIZE + V_PADDING_TOP + PADDING_TOP, textPaint!!)
            }
        }
    }

    /**
     * 画Y轴的刻度线
     */
    private fun drawYLine(canvas: Canvas) {
        for (i in yRateList.indices) {
            canvas.drawLine(
                X_START,
                i * ROW_HEIGHT + TEXTSIZE + PADDING_TOP - vHeight / 2,
                fColWidth * (xList.size - 1) + X_START,
                i * ROW_HEIGHT + TEXTSIZE + PADDING_TOP - vHeight / 2,
                yLinePaint!!
            )
        }
    }

    /**
     * 画空心圆
     */
    private fun drawCircle(canvas: Canvas) {
        if (xList.size <= DATA_SIZE) {
            for (i in xList.indices) {
                canvas.drawCircle(xPoint!![i], yPoint!![i], CIRCLE_RADIUS, circlePaint!!)
                canvas.drawCircle(xPoint!![i], yPoint!![i], INNER_RADIUS, innerPaint!!)
            }
        }
    }

    /**
     * 画折线 和 阴影
     */
    private fun drawLine(canvas: Canvas) {
        var maxHeightPoint = 0 //记录最高的点
        var maxHeight = 0f
        val linePath = Path()
        val shadowPath = Path()
        for (i in xList.indices) {
            xPoint!![i] = X_START + i * fColWidth
            if (maxHeight < yList[i]) {
                maxHeight = yList[i]
                maxHeightPoint = i
            }
            yPoint!![i] = (maxY - yList[i]) / maxY * Y_SIZE * ROW_HEIGHT + TEXTSIZE + PADDING_TOP - vHeight / 2
            if (i == 0) {
                linePath.moveTo(xPoint!![i], yPoint!![i])
                shadowPath.moveTo(xPoint!![i], yPoint!![i])
            } else {
                linePath.lineTo(xPoint!![i], yPoint!![i])
                shadowPath.lineTo(xPoint!![i], yPoint!![i])
            }
//            if (i > 0) {
//                canvas.drawLine(xPoint!![i - 1], yPoint!![i - 1], xPoint!![i], yPoint!![i], linePaint!!)
//                if (i == 1) {
//                    shadowPath!!.moveTo(xPoint!![i - 1], yPoint!![i - 1])
//                }
//                shadowPath!!.lineTo(xPoint!![i], yPoint!![i])
//            }
        }
        canvas.drawPath(linePath, linePaint!!)
        xPoint?.let { points ->
            shadowPath.lineTo(points[xList.size - 1], Y_SIZE * ROW_HEIGHT + TEXTSIZE + PADDING_TOP - vHeight / 2)
            shadowPath.lineTo(points[0], Y_SIZE * ROW_HEIGHT + TEXTSIZE + PADDING_TOP - vHeight / 2)
            shadowPath.close()
            val lShader: Shader = LinearGradient(
                0f,
                yPoint!![maxHeightPoint],
                0f,
                Y_SIZE * ROW_HEIGHT + TEXTSIZE + PADDING_TOP - vHeight / 2,
                ContextCompat.getColor(context, R.color.color_26008aff),
                ContextCompat.getColor(context, R.color.color_26fff),
                Shader.TileMode.REPEAT
            )
            shadowPaint!!.shader = lShader
            canvas.drawPath(shadowPath, shadowPaint!!)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureHeight(measureSpec: Int): Int {
        var result = 0
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)

        if (mode == MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = height.toInt()
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size)
            }
        }
        return result

    }

    private fun measureWidth(measureSpec: Int): Int {
        var result = 0
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)

        if (mode == MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = 75//根据自己的需要更改
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size)
            }
        }
        return result
    }
}