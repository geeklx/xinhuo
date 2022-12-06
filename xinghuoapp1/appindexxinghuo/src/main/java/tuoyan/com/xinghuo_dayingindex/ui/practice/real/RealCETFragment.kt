package tuoyan.com.xinghuo_dayingindex.ui.practice.real
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_real_cet.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.RealListItem
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.PracticePresenter
import tuoyan.com.xinghuo_dayingindex.ui.practice.real.adapter.RealAdapter

class RealCETFragment : LifeV4Fragment<PracticePresenter>() {
    override val presenter: PracticePresenter
        get() = PracticePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_real_cet
    val gradeKey by lazy { arguments?.getString(GRAD_KEY) ?: "" }
    val mAdapter by lazy {
        RealAdapter { item ->
            isLogin {
                if ("1" != item.isFinish) {
                    startActivity<ExerciseDetailKActivity>(
                        ExerciseDetailKActivity.KEY to item.paperKey,
                        ExerciseDetailKActivity.NAME to item.paperName,
                        ExerciseDetailKActivity.CAT_KEY to gradeKey,
                        ExerciseDetailKActivity.USER_PRACTISE_KEY to item.userPracticeKey
                    )
                } else {
                    presenter.getReport(item.paperKey, item.userPracticeKey) {
                        startActivity<ReportActivity>(
                            ReportActivity.DATA to it,
                            ReportActivity.TIME to "",
                            ReportActivity.KEY to item.paperKey,
                            ReportActivity.CAT_KEY to gradeKey,
                            ReportActivity.NAME to item.paperName,
                            ReportActivity.TYPE to "1",
                            ReportActivity.EVAL_STATE to "1"
                        )
                    }
                }
            }
        }
    }

    companion object {
        val GRAD_KEY = "grad_key"

        fun newInstance(gradKey: String): RealCETFragment {
            val f = RealCETFragment()
            val args = Bundle()
            args.putString(GRAD_KEY, gradKey)
            f.arguments = args
            return f
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_real_cet.layoutManager = LinearLayoutManager(activity)
        rlv_real_cet.adapter = mAdapter
    }

    override fun handleEvent() {
    }

    override fun initData() {
        presenter.getRealList(gradeKey) {
            initList(it)
        }
    }

    fun setData() {
        presenter.getRealList(gradeKey) {
            initList(it)
        }
    }

    private fun initList(list: List<RealListItem>) {
        var yearStr = "1"
        list.forEach {
            if (it.year != yearStr) {
                it.showYear = true
                yearStr = it.year
            } else {
                it.showYear = false
            }
        }
        mAdapter.setData(list)
    }
}