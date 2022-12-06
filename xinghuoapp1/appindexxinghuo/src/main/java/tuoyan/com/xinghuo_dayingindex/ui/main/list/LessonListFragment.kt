package tuoyan.com.xinghuo_dayingindex.ui.main.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_lesson_list.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.ui.home.HomePresenter
import tuoyan.com.xinghuo_dayingindex.ui.main.Adapter
import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity

class LessonListFragment : LifeV4Fragment<HomePresenter>() {
    private val key by lazy { arguments?.getString(KEY) ?: "" }
    private val gradeKey by lazy { arguments?.getString(GRADE_KEY) ?: "" }
    val adapter by lazy {
        Adapter() {
            if ("1" == it.dataType) {
                val intent = Intent(this.requireContext(), LessonListActivity::class.java)
                intent.putExtra(LessonListActivity.KEY, it.key)
                intent.putExtra(LessonListActivity.TITLE, it.title)
                startActivity(intent)
            } else {
                val intent = Intent(this.requireContext(), LessonDetailActivity::class.java)
                intent.putExtra(LessonDetailActivity.KEY, it.key)
                startActivity(intent)
            }
        }
    }

    companion object {
        const val KEY = "key"
        const val GRADE_KEY = "grade_key"

        @JvmStatic
        fun newInstance(key: String, gradeKey: String) =
            LessonListFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY, key)
                    putString(GRADE_KEY, gradeKey)
                }
            }
    }

    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_lesson_list

    override fun configView(view: View?) {
        super.configView(view)
        rlv_lessons.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_lessons.adapter = adapter
    }

    override fun handleEvent() {
        super.handleEvent()
    }

    override fun initData() {
        super.initData()
        presenter.getHomeNets(key, gradeKey) {
            adapter.setData(it)
        }
    }
}