package tuoyan.com.xinghuo_dayingindex.ui.practice.real
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_real.*
import org.jetbrains.anko.ctx
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.RealListItem
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.PracticePresenter
import tuoyan.com.xinghuo_dayingindex.ui.practice.real.adapter.RealAdapter

class RealActivity : LifeActivity<PracticePresenter>() {
    override val presenter: PracticePresenter
        get() = PracticePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_real

    private val gradKey by lazy { intent.getStringExtra(GRAD_KEY) ?: "" }

    companion object {
        val GRAD_KEY = "grad_key"
    }

    override fun configView() {
        setSupportActionBar(tb_real)
        rv_real.layoutManager = LinearLayoutManager(ctx)
    }

    override fun handleEvent() {
        tb_real.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        presenter.getRealList(gradKey) {
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

        val mAdapter = RealAdapter { item ->
            isLogin {
                if ("1" != item.isFinish) {
                    startActivity<ExerciseDetailKActivity>(
                        ExerciseDetailKActivity.KEY to item.paperKey,
                        ExerciseDetailKActivity.NAME to item.paperName,
                        ExerciseDetailKActivity.CAT_KEY to gradKey,
                        ExerciseDetailKActivity.USER_PRACTISE_KEY to item.userPracticeKey,
                    )
                } else {
                    presenter.getReport(item.paperKey, item.userPracticeKey) {
                        startActivity<ReportActivity>(
                            ReportActivity.DATA to it,
                            ReportActivity.TIME to "",
                            ReportActivity.KEY to item.paperKey,
                            ReportActivity.CAT_KEY to gradKey,
                            ReportActivity.NAME to item.paperName,
                            ReportActivity.TYPE to "1",
                            ReportActivity.EVAL_STATE to "1"
                        )
                    }
                }
            }
        }
        mAdapter.setData(list)
        rv_real.adapter = mAdapter
    }


}
