package tuoyan.com.xinghuo_dayingindex.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import tuoyan.com.xinghuo_dayingindex.R
import kotlin.properties.Delegates

/**
 * 创建者： huoshulei
 * 时间：  2017/5/6.
 */
abstract class BaseComponent<in T : AppCompatActivity> : AnkoComponent<T> {
    abstract val isShowToolbar: Boolean
    var contentView by Delegates.notNull<ViewGroup>()
    var titleText: TextView? = null
    var toolbar: Toolbar? = null
    override fun createView(ui: AnkoContext<T>): View = with(ui) {
        //        frameLayout {
//            val imageView = imageView {
//                backgroundResource = R.color.bg_f7
//            }.lparams(matchParent, matchParent)
//            swipeBackLayout {
//                //滑动返回布局
//                lparams(matchParent, matchParent)
//                setDragEdge(SwipeBackLayout.DragEdge.LEFT)
//                setOnSwipeBackListener { _, fl -> imageView.alpha = 1 - fl }
        verticalLayout {
//            dividerDrawable = resources.getDrawable(R.drawable.divider)
            showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
//            backgroundResource = R.color.alpha_white
            if (isShowToolbar)//是否显示toolbar
                toolbar = toolbar {
                    lparams(matchParent, dip(44)) { }
                    titleText = textView {
                        lines = 1
//                        textColor = resources.getColor(R.color.color_white)
                        gravity = Gravity.CENTER
//                        typeface = Typeface.DEFAULT_BOLD
                        textSize = 16f
                    }.lparams(wrapContent, dip(43)) {
                        gravity = Gravity.CENTER
                    }
                    backgroundResource = R.color.colorPrimary
                    ui.owner.setSupportActionBar(this)
                    ui.owner.supportActionBar?.setDisplayShowTitleEnabled(false)
//                    navigationIconResource = R.mipmap.ic_back
                    setNavigationOnClickListener { ui.owner.onBackPressed() }
                }
            contentView(this@with.owner)
        }
//            }
//        }
    }

    abstract fun _LinearLayout.contentView(activity: T)

}

