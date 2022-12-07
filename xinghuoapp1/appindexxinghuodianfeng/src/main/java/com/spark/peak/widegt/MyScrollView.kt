package com.spark.peak.widegt

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * Created by 李昊 on 2018/7/27.
 * 解决竖向ScrollView嵌套横向ScrollView时滑动不流畅的问题
 */

class MyScrollView : ScrollView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    var startY = 0
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        return super.onInterceptTouchEvent(ev)
        when(ev?.action){
            MotionEvent.ACTION_DOWN -> startY = ev.y.toInt()
            MotionEvent.ACTION_MOVE -> {
                var disY = Math.abs(ev.y.toInt() - startY)
                if (disY > 20){
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return super.onTouchEvent(ev)
    }
}
