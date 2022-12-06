//package tuoyan.com.xinghuo_daying.widegt
//
//import android.content.Context
//import androidx.viewpager.widget.ViewPager
//import android.util.AttributeSet
//import android.view.MotionEvent
//
///**
// * 创建者：
// * 时间：  2018/10/10.
// */
//class ViewPagerScroll : ViewPager {
//    constructor(context: Context) : super(context) {}
//
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
//
//    var notCanScroll = false
//    private var startX: Float = 0.toFloat()
//    private var startY: Float = 0.toFloat()
//    private var moveX: Float = 0.toFloat()
////    var isScroll = true
////    var beforeX: Float = 0f
////    var beforeY: Float = 0f
//
//    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        if (notCanScroll) {
//            return false
//        } else {
////            when (ev.action) {
////                MotionEvent.ACTION_DOWN -> {
////                    startX = ev.x
////                    startY = ev.y
////                }
////                MotionEvent.ACTION_MOVE -> {
////                    moveX = ev.x - startX
////                    val moveY = ev.y - startY
////                    if (moveX > 0 && moveX > Math.abs(moveY)) {//只能向左滑动
////                        return true
////                    }
////                    startX = ev.x
////                }
////            }
//            return super.onInterceptTouchEvent(ev)
//        }
//    }
//
//    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        if (notCanScroll) {
//            return false
//        } else {
////            when (ev.action) {
////                MotionEvent.ACTION_DOWN -> {
////                }
////                MotionEvent.ACTION_MOVE -> {
////                    moveX = ev.x - startX
////                    val moveY = ev.y - startY
////                    if (moveX > 0 && moveX > Math.abs(moveY)) {//只能向左滑动
////                        return true
////                    }
////                    startX = ev.x
////                }
////            }
//            return super.onTouchEvent(ev)
//        }
//
//    }
////    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
////        when (ev.action) {
////            MotionEvent.ACTION_DOWN -> {
////                //按下如果‘仅’作为‘上次坐标’，不妥，因为可能存在左滑，motionValue大于0的情况（来回滑，只要停止坐标在按下坐标的右边，左滑仍然能滑过去）
////                beforeX = ev.x
////                beforeY = ev.y
////            }
////            MotionEvent.ACTION_MOVE -> {
////                val motionValue = ev.x - beforeX
////                val motionValueY = ev.y - beforeY
////                if (motionValueY < motionValue)
////                    if (motionValue > 0) {//禁止右滑
////                        return true
////                    }
////                beforeX = ev.x//手指移动时，再把当前的坐标作为下一次的‘上次坐标’，解决上述问题
////            }
////            else -> {
////
////            }
////        }
////
////        return super.dispatchTouchEvent(ev)
////    }
//}
