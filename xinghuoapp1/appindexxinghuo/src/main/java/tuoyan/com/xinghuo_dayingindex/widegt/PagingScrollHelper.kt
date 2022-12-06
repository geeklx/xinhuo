package tuoyan.com.xinghuo_dayingindex.widegt


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * 实现RecycleView分页滚动的工具类
 * Created by zhuguohui on 2016/11/10.
 */

class PagingScrollHelper {

    internal var mRecyclerView: RecyclerView? = null

    private val mOnScrollListener = MyOnScrollListener()

    private val mOnFlingListener = MyOnFlingListener()
    private var offsetY = 0
    private var offsetX = 0

    internal var startY = 0
    internal var startX = 0

    private var mOrientation = ORIENTATION.HORIZONTAL

    /**
     * 获取总共的页数
     */
    val pageCount: Int
        get() {
            if (mRecyclerView != null) {
                if (mOrientation == ORIENTATION.NULL) {
                    return 0
                }
                if (mOrientation == ORIENTATION.VERTICAL && mRecyclerView!!.computeVerticalScrollExtent() != 0) {
                    return mRecyclerView!!.computeVerticalScrollRange() / mRecyclerView!!.computeVerticalScrollExtent()
                } else if (mRecyclerView!!.computeHorizontalScrollExtent() != 0) {
                    Log.i(
                        "zzz",
                        "rang=" + mRecyclerView!!.computeHorizontalScrollRange() + " extent=" + mRecyclerView!!.computeHorizontalScrollExtent()
                    )
                    return mRecyclerView!!.computeHorizontalScrollRange() / mRecyclerView!!.computeHorizontalScrollExtent()
                }
            }
            return 0
        }


    internal var mAnimator: ValueAnimator? = null

    private val mOnTouchListener = MyOnTouchListener()

    private var firstTouch = true

    private val pageIndex: Int
        //        get() {
//            var p = 0
//            if (mRecyclerView!!.height == 0 || mRecyclerView!!.width == 0) {
//                return p
//            }
//            if (mOrientation == ORIENTATION.VERTICAL) {
//                p = offsetY / mRecyclerView!!.height
//            } else {
//                p = offsetX / mRecyclerView!!.width
//            }
//            return p
//        }
        get() {
            if (mRecyclerView != null) {
                if (mOrientation == ORIENTATION.NULL) {
                    return 0
                }
                val layoutManager = mRecyclerView!!.layoutManager
                if (layoutManager is LinearLayoutManager)
                    return layoutManager.findFirstVisibleItemPosition()
                if (layoutManager is StaggeredGridLayoutManager) {
                    val ints = layoutManager.findFirstVisibleItemPositions(null)
                    if (ints.isNotEmpty())
                        return ints[0]
                }
            }
            return 0
        }

    private//没有宽高无法处理
    val startPageIndex: Int
        get() {
            var p = 0
            if (mRecyclerView!!.height == 0 || mRecyclerView!!.width == 0) {
                return p
            }
            if (mOrientation == ORIENTATION.VERTICAL) {
                p = startY / mRecyclerView!!.height
            } else {
                p = startX / mRecyclerView!!.width
            }
            return p
        }

    internal var mOnPageChangeListener: onPageChangeListener? = null


    internal enum class ORIENTATION {
        HORIZONTAL, VERTICAL, NULL
    }

    fun setUpRecycleView(recycleView: RecyclerView?) {
        if (recycleView == null) {
            throw IllegalArgumentException("recycleView must be not null")
        }
        mRecyclerView = recycleView
        //处理滑动
        recycleView.onFlingListener = mOnFlingListener
        //设置滚动监听，记录滚动的状态，和总的偏移量
        recycleView.addOnScrollListener(mOnScrollListener)
        //记录滚动开始的位置
        recycleView.setOnTouchListener(mOnTouchListener)
        //获取滚动的方向
        updateLayoutManger()

    }

    fun updateLayoutManger() {
        val layoutManager = mRecyclerView!!.layoutManager
        if (layoutManager != null) {
            if (layoutManager.canScrollVertically()) {
                mOrientation = ORIENTATION.VERTICAL
            } else if (layoutManager.canScrollHorizontally()) {
                mOrientation = ORIENTATION.HORIZONTAL
            } else {
                mOrientation = ORIENTATION.NULL
            }
            if (mAnimator != null) {
                mAnimator!!.cancel()
            }
            startX = 0
            startY = 0
            offsetX = 0
            offsetY = 0

        }

    }

    fun scrollToPosition(position: Int) {
        if (mAnimator == null) {
            mOnFlingListener.onFling(0, 0)
        }
        if (mAnimator != null) {
            val startPoint = if (mOrientation == ORIENTATION.VERTICAL) offsetY else offsetX
            var endPoint = 0
            if (mOrientation == ORIENTATION.VERTICAL) {
                endPoint = mRecyclerView!!.height * position
            } else {
                endPoint = mRecyclerView!!.width * position
            }
            if (startPoint != endPoint) {
                mAnimator!!.setIntValues(startPoint, endPoint)
                mAnimator!!.start()
            }
        }
    }

    inner class MyOnFlingListener : RecyclerView.OnFlingListener() {
        //        var startIndex = 0
        private var b = false//滚动动画执行期间禁止手势滑动

        override fun onFling(velocityX: Int, velocityY: Int): Boolean {
//            if (millis != 0L && System.currentTimeMillis() - millis < 10) {
//                millis = System.currentTimeMillis()
//                return false
//            }
            if (b) {
                return false
            }
            if (mOrientation == ORIENTATION.NULL) {
                return false
            }
            //获取开始滚动时所在页面的index
            var p = startPageIndex
//            startIndex = pageIndex
            //记录滚动开始和结束的位置
            var endPoint = 0
            var startPoint = 0

            //如果是垂直方向
            if (mOrientation == ORIENTATION.VERTICAL) {
                startPoint = offsetY

                if (velocityY < 0) {
                    p--
                } else if (velocityY > 0) {
                    p++
                }
                //更具不同的速度判断需要滚动的方向
                //注意，此处有一个技巧，就是当速度为0的时候就滚动会开始的页面，即实现页面复位
                endPoint = p * mRecyclerView!!.height

            } else {
                startPoint = offsetX
                if (velocityX < 0) {
                    p--
                } else if (velocityX > 0) {
                    p++
                }
                endPoint = p * mRecyclerView!!.width

            }
            if (endPoint < 0) {
                endPoint = 0
            }

            //使用动画处理滚动
            if (mAnimator == null) {
                mAnimator = ValueAnimator.ofInt(startPoint, endPoint)

                mAnimator!!.duration = 300
                mAnimator!!.addUpdateListener { animation ->
                    val nowPoint = animation.animatedValue as Int

                    if (mOrientation == ORIENTATION.VERTICAL) {
                        val dy = nowPoint - offsetY
                        //这里通过RecyclerView的scrollBy方法实现滚动。
                        mRecyclerView!!.scrollBy(0, dy)
                    } else {
                        val dx = nowPoint - offsetX
                        mRecyclerView!!.scrollBy(dx, 0)
                    }
                }
                mAnimator!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        //回调监听
                        if (null != mOnPageChangeListener) {
                            if (0 != pageIndex)
                                mOnPageChangeListener!!.onPageChange(pageIndex)
                        }
                        //修复双击item bug
                        mRecyclerView!!.stopScroll()
                        startY = offsetY
                        startX = offsetX
                        b = false
                    }
                })
            } else {
                mAnimator!!.cancel()
                mAnimator!!.setIntValues(startPoint, endPoint)
            }
//            if (!mAnimator!!.isRunning)
            mAnimator!!.start()
            b = true
            return true
        }
    }

    inner class MyOnScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            //newState==0表示滚动停止，此时需要处理回滚
            if (newState == 0 && mOrientation != ORIENTATION.NULL) {
                val move: Boolean
                var vX = 0
                var vY = 0
                if (mOrientation == ORIENTATION.VERTICAL) {
                    val absY = Math.abs(offsetY - startY)
                    //如果滑动的距离超过屏幕的一半表示需要滑动到下一页
                    move = absY > recyclerView.height / 2
                    vY = 0

                    if (move) {
                        vY = if (offsetY - startY < 0) -1000 else 1000
                    }

                } else {
                    val absX = Math.abs(offsetX - startX)
                    move = absX > recyclerView.width / 2
                    if (move) {
                        vX = if (offsetX - startX < 0) -1000 else 1000
                    }

                }
                val manager = recyclerView.layoutManager
                if (manager is LinearLayoutManager) {
                    val position = manager.findFirstVisibleItemPosition()
                    val view = manager.findViewByPosition(position)
                    if (view?.left != 0 || view.top != 0)
                        mOnFlingListener.onFling(vX, vY)
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            //滚动结束记录滚动的偏移量
            offsetY += dy
            offsetX += dx
        }
    }

    inner class MyOnTouchListener : View.OnTouchListener {

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            //手指按下的时候记录开始滚动的坐标
            if (firstTouch) {
                //第一次touch可能是ACTION_MOVE或ACTION_DOWN,所以使用这种方式判断
                firstTouch = false
                startY = offsetY
                startX = offsetX
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                firstTouch = true
            }

            return false
        }

    }

    fun setOnPageChangeListener(listener: onPageChangeListener) {
        mOnPageChangeListener = listener
    }

    interface onPageChangeListener {
        fun onPageChange(index: Int)
    }
}