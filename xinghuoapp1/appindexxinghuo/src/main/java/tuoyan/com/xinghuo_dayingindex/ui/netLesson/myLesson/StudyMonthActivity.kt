package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_study_month.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.LessonStudyMonthData
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.LessonsPresenter

class StudyMonthActivity : LifeActivity<LessonsPresenter>() {
    override val presenter: LessonsPresenter
        get() = LessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_study_month
    private val courseKey by lazy { intent.getStringExtra(COURSE_KEY) ?: "" }
    private val monthAdapter by lazy {
        MonthAdapter() {
            val intent = Intent(this, MonthDataActivity::class.java)
            intent.putExtra(MonthDataActivity.DATA, it)
            startActivity(intent)
        }
    }

    override fun configView() {
        super.configView()
        rlv_month.layoutManager = LinearLayoutManager(this)
        rlv_month.adapter = monthAdapter
    }

    override fun initData() {
        super.initData()
        presenter.getLearnYmList(courseKey) {
            monthAdapter.setData(it)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    companion object {
        const val COURSE_KEY = "courseKey"
    }
}

class MonthAdapter(val OnClick: (LessonStudyMonthData) -> Unit) : BaseAdapter<LessonStudyMonthData>() {
    override fun convert(holder: ViewHolder, item: LessonStudyMonthData) {
        holder.itemView.setOnClickListener {
            OnClick(item)
        }
        holder.setText(R.id.tv_title, item.name)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_month_item, parent, false)
    }

}