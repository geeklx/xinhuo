package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.support.v4.ctx
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.LessonsPresenter
import kotlin.properties.Delegates

/**
 * Created by  on 2018/10/10.
 */
class LessonComFragment : LifeV4Fragment<LessonsPresenter>() {
    override val presenter: LessonsPresenter
        get() = LessonsPresenter(this)
    override val layoutResId: Int
        get() = 0

    private val parent by lazy { activity as LessonDetailActivity }
    private var rv_com by Delegates.notNull<RecyclerView>()
    var page = 1
    override fun initView(): View? = this.requireContext().relativeLayout {
        rv_com = recyclerView {
            layoutManager = LinearLayoutManager(ctx)
            adapter = mAdapter
        }.lparams(matchParent, matchParent)
    }

    var mAdapter = ComAdapter {
        loadMore()
    }

    override fun initData() {
        try {
            mAdapter.points = parent.detail?.points ?: 5.0
        } catch (e: Exception) {
        }
        presenter.loadComment(parent.key, "1", page) { list, total ->
            mAdapter.setData(list)
            mAdapter.setMore(true)
            mAdapter.total = total
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun loadMore() {
        page++
        presenter.loadComment(parent.key, "1", page) { list, total ->
            if (list.isEmpty()) {
                mAdapter.setMore(false)
            } else {
                mAdapter.addData(list)
            }
        }
    }

}