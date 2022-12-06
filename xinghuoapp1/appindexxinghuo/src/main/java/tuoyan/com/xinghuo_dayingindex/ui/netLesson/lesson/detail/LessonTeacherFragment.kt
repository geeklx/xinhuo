package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import org.jetbrains.anko.recyclerview.v7.recyclerView
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.TeacherListBean
import kotlin.properties.Delegates

/**
 * Created by  on 2018/10/9.
 */
class LessonTeacherFragment : BaseV4Fragment() {
    override val layoutResId: Int
        get() = 0

    var rv_teacher by Delegates.notNull<RecyclerView>()

    val dataList by lazy { arguments!!.getSerializable(LIST) as ArrayList<TeacherListBean> }

    companion object {
        val LIST = "list"
        fun newInstance(content: ArrayList<TeacherListBean>): LessonTeacherFragment {
            var f = LessonTeacherFragment()

            var arg = Bundle()
            arg.putSerializable(LIST, content)
            f.arguments = arg
            return f
        }
    }

    override fun initView(): View? = with(this.requireContext()) {
        rv_teacher = recyclerView {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }
        rv_teacher
    }

    override fun initData() {
        var mAdapter = TeacherAdapter()
        mAdapter.setData(dataList)
        rv_teacher.adapter = mAdapter
    }

}