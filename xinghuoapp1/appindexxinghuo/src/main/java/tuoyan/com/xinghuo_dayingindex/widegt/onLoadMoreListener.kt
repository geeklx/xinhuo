package tuoyan.com.xinghuo_dayingindex.widegt

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*

abstract class onLoadMoreListener : OnScrollListener() {
    private var countItem = 0
    private var lastItem = 0
    private var isScrolled = false
    private var layoutManager: LayoutManager? = null
    abstract fun onLoading(countItem: Int, lastItem: Int)
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        isScrolled = newState == SCROLL_STATE_DRAGGING || newState == SCROLL_STATE_SETTLING
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (recyclerView.layoutManager is LinearLayoutManager) {
            layoutManager = recyclerView.layoutManager
            countItem = layoutManager?.itemCount!!
            lastItem =
                (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        }
        if (isScrolled && countItem != lastItem && lastItem == countItem - 1) {
            onLoading(countItem, lastItem)
        }
    }
}