package tuoyan.com.xinghuo_dayingindex.ui.mine.exam

import android.os.Bundle
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_exam_time.*
import kotlinx.android.synthetic.main.activity_wrong.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.user.UserPresenter

class ExamTimeActivity :  LifeActivity<UserPresenter>()  {
    private val adapter by lazy { ExamTimeAdapter() }


    override val presenter: UserPresenter
        get() = UserPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_exam_time

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)

            .statusBarColor(R.color.color_f5f6f9)
            .statusBarDarkFont(true)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()
        fullScreen=true

        super.onCreate(savedInstanceState)

    }
    override fun configView() {

//        rv_exam_time.layoutManager = LinearLayoutManager(ctx)
//        val decoration = DividerItemDecoration(ctx, LinearLayout.VERTICAL)
//        decoration.setDrawable(resources.getDrawable(R.drawable.divider))
//        rv_exam_time.addItemDecoration(decoration)
    rv_exam_time.adapter=adapter;
    }

    override fun initData() {
        presenter.getExamList {

            adapter.setData(it)
            adapter.notifyDataSetChanged()
        }

    }
}