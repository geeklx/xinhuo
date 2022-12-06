package tuoyan.com.xinghuo_dayingindex.widegt

import android.content.Context
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.MotionEvent


class MySwipeRefreshLayout : SwipeRefreshLayout {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    var startX = 0

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        return super.onInterceptTouchEvent(ev)
        when(ev?.action){
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                var disX = Math.abs(ev.x.toInt() - startX)
                if (disX >20){ //TODO 横向滑动超过20 时，不消费事件，解决事业部说的 下拉刷新会影响banner横向滑动的问题，呵呵哒。
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}
