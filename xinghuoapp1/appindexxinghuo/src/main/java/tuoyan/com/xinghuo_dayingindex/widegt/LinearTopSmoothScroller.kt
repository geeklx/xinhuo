package tuoyan.com.xinghuo_dayingindex.widegt

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

class LinearTopSmoothScroller(context: Context?) : LinearSmoothScroller(context) {
    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }
}