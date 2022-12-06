package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_sentence_list.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.bean.LrcDetail
import tuoyan.com.xinghuo_dayingindex.ui.book.adapter.SentenceDialogAdapter
import tuoyan.com.xinghuo_dayingindex.widegt.LinearTopSmoothScroller

/**
 */
class SentenceListDialog(
    context: Context,
    private val click: (LrcDetail) -> Unit
) : Dialog(context, R.style.custom_dialog) {
    private var collectEmpty = true
    private var position = 0
    private var isTouch = false
    private var onScreen = true//isTouch 之后是否出现在屏幕中，手动触发的时候只有文本高亮出现在屏幕中时，字幕可自动滚动

    private val adapter by lazy {
        SentenceDialogAdapter {
            click(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_sentence_list)
        img_close.setOnClickListener { dismiss() }
        rlv_content.layoutManager = LinearLayoutManager(context)
        rlv_content.adapter = adapter
        rd_group.setOnCheckedChangeListener { radioGroup, checkedId ->
            for (index in 0 until radioGroup.childCount) {
                val tempV = radioGroup.getChildAt(index)
                if (tempV is RadioButton) {
                    tempV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
            val v = radioGroup.findViewById(checkedId) as RadioButton
            v.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_book_checked, 0, 0, 0)
            adapter.onlyShowCollect = radioGroup.indexOfChild(radioGroup.findViewById(checkedId)) / 2 == 1
            initLrcView()
        }
        val handler = Handler()
        val runnable = Runnable {
            isTouch = false
        }
        rlv_content.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        isTouch = true
                        onScreen = false
                        handler.removeCallbacks(runnable)
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        handler.postDelayed(runnable, 3000)
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    fun setData(data: List<LrcDetail>, collectEmpty: Boolean) {
        adapter.setData(data)
        this.collectEmpty = collectEmpty
    }

    fun setDuration(duration: Long) {
        adapter.duration = duration
    }

    fun setPlayId(id: String, pos: Int) {
        adapter.setCurrentId(id)
        this.position = pos
        scroll2Pos()
    }

    fun notifyDataSetChanged(collectEmpty: Boolean) {
        this.collectEmpty = collectEmpty
        adapter.notifyDataSetChanged()
        initLrcView()
    }

    private fun initLrcView(){
        layout_empty?.visibility = if (adapter.onlyShowCollect && collectEmpty) {
            View.VISIBLE
        } else {
            View.GONE
        }
        isTouch = false
        onScreen = true
        Handler().postDelayed({
            scroll2Pos()
        }, 500)
    }
    private fun scroll2Pos() {
        val layoutManager = rlv_content?.layoutManager as? LinearLayoutManager
        val firstPos = layoutManager?.findFirstCompletelyVisibleItemPosition() ?: 0
        val lastPos = layoutManager?.findLastCompletelyVisibleItemPosition() ?: 0
        onScreen = position in firstPos..lastPos || onScreen
        if ((firstPos > position || lastPos < position) && !isTouch && onScreen) {
            val smoothScroller = LinearTopSmoothScroller(context)
            smoothScroller.targetPosition = position
            layoutManager?.startSmoothScroll(smoothScroller)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
    }
}