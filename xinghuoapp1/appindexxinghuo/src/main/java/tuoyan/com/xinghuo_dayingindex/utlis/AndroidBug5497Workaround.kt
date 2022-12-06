//package tuoyan.com.xinghuo_daying.utlis
//
//import android.app.Activity
//import android.graphics.Rect
//import android.view.View
//import android.widget.FrameLayout
//
///**
// * 创建者：
// * 时间：  2018/7/5.
// */
//class AndroidBug5497Workaround private constructor(activity: Activity) {
//
//    private val mChildOfContent: View
//    private var usableHeightPrevious: Int = 0
//    private val frameLayoutParams: FrameLayout.LayoutParams
//
//    init {
//        val content = activity.findViewById<View>(android.R.id.content) as FrameLayout
//        mChildOfContent = content.getChildAt(0)
//        mChildOfContent.viewTreeObserver.addOnGlobalLayoutListener { possiblyResizeChildOfContent() }
//        frameLayoutParams = mChildOfContent.layoutParams as FrameLayout.LayoutParams
//    }
//
//    private fun possiblyResizeChildOfContent() {
//        val usableHeightNow = computeUsableHeight()
//        if (usableHeightNow != usableHeightPrevious) {
//            val usableHeightSansKeyboard = mChildOfContent.rootView.height
//            val heightDifference = usableHeightSansKeyboard - usableHeightNow
//            if (heightDifference > usableHeightSansKeyboard / 4) {
//                // keyboard probably just became visible
//                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference
//            } else {
//                // keyboard probably just became hidden
//                frameLayoutParams.height = usableHeightSansKeyboard
//            }
//            mChildOfContent.requestLayout()
//            usableHeightPrevious = usableHeightNow
//        }
//    }
//
//    private fun computeUsableHeight(): Int {
//        val r = Rect()
//        mChildOfContent.getWindowVisibleDisplayFrame(r)
//        return r.bottom - r.top
//    }
//
//    companion object {
//
//        // For more information, see https://issuetracker.google.com/issues/36911528
//        // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
//
//        fun assistActivity(activity: Activity) {
//            AndroidBug5497Workaround(activity)
//        }
//    }
//}
