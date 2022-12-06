//package tuoyan.com.xinghuo_daying.widegt
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.graphics.Path
//import android.os.Handler
//import android.util.AttributeSet
//import android.view.View
//import android.view.ViewManager
//import org.jetbrains.anko.custom.ankoView
//
//
///**
// * 创建者： huoshulei
// * 时间：  2017/5/11.
// */
//
//class PlayView : View {
//    /**
//     * view 尺寸
//     */
//    private val width = 321f
//    private val height = 216f
//    /**
//     * 中心点
//     */
//    private var centerX: Float = 0f
//    private var centerY: Float = 0f
//    /**
//     * 缩放
//     */
//    private var scaleW: Float = 1f
//    private var scaleH: Float = 1f
//    /**
//     * 画笔
//     */
//    private val paint by lazy {
//        with(Paint()) {
//            isAntiAlias = true
//            strokeCap = android.graphics.Paint.Cap.ROUND
//            style = android.graphics.Paint.Style.STROKE
//            color = android.graphics.Color.parseColor("#17c8ce")
//            strokeWidth = dip(4f)
//            this
//        }
//    }
//    //屏幕密度
//    private val density by lazy { resources.displayMetrics.density }
//
//    private var scale: Float = 0.toFloat()
//    private val paths = mutableListOf<Path>()
//    private var count = 0
//    var isAnim: Boolean = false
//        private set
//    internal val handler by lazy { Handler() }
//    private val runnable: Runnable by lazy {
//        object : Runnable {
//            override fun run() {
//                isAnim = true
//                count++
//                if (count >= paths.size)
//                    count = 0
//                invalidate()
//                handler.postDelayed(this, 250)
//            }
//        }
//    }
//
//    constructor(context: Context) : super(context)
//
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
//
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        /*
//      内容尺寸
//     */
//        val contentW = (w - paddingLeft - paddingRight).toFloat()
//        centerX = contentW / 2f + paddingLeft
//        val contentH = (h - paddingTop - paddingBottom).toFloat()
//        centerY = contentH / 2f + paddingTop
//        val fw = contentW / width
//        val fh = contentH / height
//        scaleW = fw / fh
//        scaleH = fh / fw
//        scaleW = if (scaleW > 1) 1f else scaleW
//        scaleH = if (scaleH > 1) 1f else scaleH
//        paint.strokeWidth = dip(4f) * Math.min(fw, fh)
//        initPath()
//    }
//
//    private fun initPath() {
//        paths.clear()
//        paths.add(leftPath())
//        paths.add(line1())
//        paths.add(line2())
//        paths.add(line3())
//        count = paths.size
//    }
//
//    /**
//     * 开始动画
//     */
//    fun startAnim() {
////        if (isAnim)
////            stopAnim()
////        else
////            handler.post(runnable)
//    }
//
//    /**
//     * 中止动画
//     */
//    fun stopAnim() {
////        handler.removeCallbacks(runnable)
////        isAnim = false
////        count = paths.size
////        invalidate()
//    }
//
//
////    override fun onAttachedToWindow() {
////        super.onAttachedToWindow()
//////                startAnim();
////    }
//
//    override fun onDetachedFromWindow() {
//        stopAnim()
//        super.onDetachedFromWindow()
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        (0..count)
//                .filter { it < paths.size }
//                .forEach { canvas.drawPath(paths[it], paint) }
//    }
//
//    private fun line1(): Path {
//        val path = Path()
//        path.moveTo(centerX + centerX * 0.125f * scaleH, centerY - centerY * scaleW * 0.4f)
//        path.quadTo(centerX + centerX * 0.438f * scaleH, centerY,
//                centerX + centerX * scaleH * 0.125f, centerY + centerY * scaleW * 0.4f)
//        return path
//    }
//
//    private fun line2(): Path {
//        val path = Path()
//        path.moveTo(centerX + centerX * scaleH * 0.406f, centerY - centerY * scaleW * 0.6f)
//        path.quadTo(centerX + centerX * scaleH * 0.78f, centerY,
//                centerX + centerX * scaleH * 0.406f, centerY + centerY * scaleW * 0.6f)
//        return path
//    }
//
//    private fun line3(): Path {
//        val path = Path()
//        path.moveTo(centerX + centerX * scaleH * 0.685f, centerY - centerY * scaleW * 0.8f)
//        path.quadTo(centerX + centerX * scaleH * 1.18f, centerY,
//                centerX + centerX * scaleH * 0.685f, centerY + centerY * scaleW * 0.8f)
//        return path
//    }
//
//    /**
//     * w=321 /3=107 /4=80.25
//     * h=216 /12=18 /4=54 /2.3=94 /2.4f=90
//     * cx=160.5 *0.65=104 *0.8=128 *0.1=16 *0.25=40 *0.35=56 *0.685=110
//     * cy=108 *0.5=54 *0.23=25 *0.87=94 *0.685=74
//     */
//    private fun leftPath(): Path {
//        val path = Path()
//        path.moveTo(centerX - centerX * scaleH * 0.9f, centerY - centerY * scaleW * 0.23f)
//        path.quadTo(centerX - centerX * scaleH * 0.9f, centerY - centerY * scaleW * 0.4f,
//                centerX - centerX * scaleH * 0.75f, centerY - centerY * scaleW * 0.4f)
//        path.lineTo(centerX - centerX * scaleH * 0.65f, centerY - centerY * scaleW * 0.4f)
//        path.lineTo(centerX - centerX * scaleH * 0.35f, centerY - centerY * scaleW * 0.8f)
//        path.quadTo(centerX - centerX * scaleH * 0.2f, centerY - centerY * scaleW,
//                centerX - centerX * scaleH * 0.2f, centerY)
//        path.quadTo(centerX - centerX * scaleH * 0.2f, centerY + centerY * scaleW,
//                centerX - centerX * scaleH * 0.35f, centerY + centerY * scaleW * 0.8f)
//        path.lineTo(centerX - centerX * scaleH * 0.65f, centerY + centerY * scaleW * 0.4f)
//        path.lineTo(centerX - centerX * scaleH * 0.75f, centerY + centerY * scaleW * 0.4f)
//        path.quadTo(centerX - centerX * scaleH * 0.9f, centerY + centerY * scaleW * 0.4f,
//                centerX - centerX * scaleH * 0.9f, centerY + centerY * scaleW * 0.23f)
//        path.close()
//        return path
//    }
//
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
//        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
//        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
//        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
//        //Measure Width
//        val desiredWidth = when (widthMode) {
//            View.MeasureSpec.EXACTLY -> widthSize.toFloat() //Must be this size
//            View.MeasureSpec.AT_MOST -> Math.min(width, widthSize.toFloat())//Can't be bigger than...
//            View.MeasureSpec.UNSPECIFIED -> width
//            else -> width//Be whatever you want
//        }
//        //Measure Height
//        val desiredHeight = when (heightMode) {
//            View.MeasureSpec.EXACTLY -> //Must be this size
//                heightSize.toFloat()
//            View.MeasureSpec.AT_MOST -> //Can't be bigger than...
//                Math.min(height, heightSize.toFloat())
//            View.MeasureSpec.UNSPECIFIED -> height
//            else -> height
//        }
//        //MUST CALL THIS
//        setMeasuredDimension(desiredWidth.toInt(), desiredHeight.toInt())
//    }
//
//    fun dip(dpValue: Float): Float {
//        return dpValue * density + 0.5f
//    }
//}
//
//inline fun ViewManager.playView(theme: Int = 0, init: PlayView.() -> Unit): PlayView {
//    return ankoView({ PlayView(it) }, theme) { init() }
//}