package com.spark.peak.widegt


import android.content.Context
import androidx.appcompat.widget.AppCompatSpinner
import android.util.AttributeSet

class MySpinner : AppCompatSpinner {
    constructor(context: Context) : super(context)

    constructor(context: Context, mode: Int) : super(context, mode)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, mode: Int) : super(context, attrs, defStyleAttr, mode)

    fun popDismiss() {
        super.onDetachedFromWindow()
    }
}
