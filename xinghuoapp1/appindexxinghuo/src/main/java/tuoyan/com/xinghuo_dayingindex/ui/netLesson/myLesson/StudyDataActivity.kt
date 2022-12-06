package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_study_data.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.LessonStudyData
import tuoyan.com.xinghuo_dayingindex.bean.LessonStudyWeekData
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.LessonsPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import java.text.SimpleDateFormat
import java.util.*

class StudyDataActivity : LifeActivity<LessonsPresenter>() {
    override val presenter: LessonsPresenter
        get() = LessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_study_data
    private val weekStudyAdapter by lazy {
        WeekStudyAdapter()
    }
    private val courseKey by lazy { intent.getStringExtra(COURSE_KEY) ?: "" }

    override fun configView() {
        super.configView()
        rlv_week.layoutManager = LinearLayoutManager(this)
        rlv_week.adapter = weekStudyAdapter
        rlv_week.isNestedScrollingEnabled = false
        tv_today_date.text = SimpleDateFormat("yyyy年 • MM月dd日").format(Date()).toString()
    }

    override fun initData() {
        super.initData()
        presenter.getLearnedDetail(courseKey) {
            bindData(it)
        }
    }

    private fun bindData(it: LessonStudyData) {
        try {//防止转化为数字错误
            if (!it.ymNum.isNullOrEmpty() && it.ymNum.toFloat() > 0) {
                tv_month_num.visibility = View.VISIBLE
                tv_month.visibility = View.VISIBLE
                tv_month_num.text = it.ymNum
            } else {
                tv_month_num.visibility = View.GONE
                tv_month.visibility = View.GONE
            }

            tv_today_hour.text = it.curDuration.split("小时")[0]
            tv_today_m.text = it.curDuration.split("小时")[1].split("分钟")[0]

            tv_mantra.text = it.mantra
            tv_week.text = it.curWeek.replace("-", " - ")

            tv_total_hour.text = it.totalDuration.split("小时")[0]
            tv_total_m.text = it.totalDuration.split("小时")[1].split("分钟")[0]

            tv_class_num.text = it.weekLearnedNum
            tv_test_num.text = it.weekWorkLearnedNum

            tv_time_h.text = it.weekDuration.split("小时")[0]
            tv_time_m.text = it.weekDuration.split("小时")[1].split("分钟")[0]

            if (!it.totalWorkNum.isNullOrEmpty() && it.totalWorkNum.toFloat() > 0) {
                pb_test_me.progress = (it.totalWorkLearnedNum.toFloat() * 100 / it.totalWorkNum.toFloat()).toInt()
                pb_test_class.progress = (it.averageWorkNum.toFloat() * 100 / it.totalWorkNum.toFloat()).toInt()
                tv_test_me_num.text = Html.fromHtml("<font color='#222'><big>${it.totalWorkLearnedNum}</big></font>/${it.totalWorkNum}")
                tv_test_class_num.text = Html.fromHtml("<font color='#222'><big>${it.averageWorkNum}</big></font>/${it.totalWorkNum}")
            } else {
                ctl_test.visibility = View.GONE
            }
            if (!it.totalVideoNum.isNullOrEmpty() && it.totalVideoNum.toFloat() > 0) {
                pb_listen_me.progress = (it.totalLearnedNum.toFloat() * 100 / it.totalVideoNum.toFloat()).toInt()
                pb_listen_class.progress = (it.averageVideoNum.toFloat() * 100 / it.totalVideoNum.toFloat()).toInt()
                tv_listen_me_num.text = Html.fromHtml("<font color='#222'><big>${it.totalLearnedNum}</big></font>/${it.totalVideoNum}")
                tv_listen_class_num.text = Html.fromHtml("<font color='#222'><big>${it.averageVideoNum}</big></font>/${it.totalVideoNum}")
            } else {
                ctl_listen_complate.visibility = View.GONE
            }
        } catch (e: Exception) {
        }
        weekStudyAdapter.maxNum = it.maxNum
        weekStudyAdapter.setData(it.weekList)
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        tv_month.setOnClickListener {
            val intent = Intent(this, StudyMonthActivity::class.java)
            intent.putExtra(StudyMonthActivity.COURSE_KEY, courseKey)
            startActivity(intent)
        }
    }

    companion object {
        const val COURSE_KEY = "courseKey"
    }
}

class WeekStudyAdapter() : BaseAdapter<LessonStudyWeekData>(isEmpty = true) {
    var maxNum = ""
        set(value) {
            field = value
        }

    override fun convert(holder: ViewHolder, item: LessonStudyWeekData) {
        val context = holder.itemView.context
        val llWeek = holder.getView(R.id.ll_week) as LinearLayout
        if (0 == holder.adapterPosition) {
            llWeek.setPadding(0, 0, 0, 0)
        } else {
            llWeek.setPadding(0, DeviceUtil.dp2px(context, 15f).toInt(), 0, 0)
        }
        holder.setText(R.id.tv_week, item.week)
        try {//防止数字转化问题
            if (!item.userDuration.isNullOrEmpty() && item.userDuration.toFloat() > 0) {
                val pbMe = holder.getView(R.id.pb_me) as ProgressBar
                holder.setVisible(R.id.point_me, View.GONE).setVisible(R.id.pb_me, View.VISIBLE)
                pbMe.progress = (item.userDuration.toFloat() * 100 / maxNum.toFloat()).toInt()
            } else {
                holder.setVisible(R.id.point_me, View.VISIBLE).setVisible(R.id.pb_me, View.GONE)
            }
            if (!item.avgDuration.isNullOrEmpty() && item.avgDuration.toFloat() > 0) {
                holder.setVisible(R.id.point_class, View.GONE).setVisible(R.id.pb_class, View.VISIBLE)
                val pbClass = holder.getView(R.id.pb_class) as ProgressBar
                pbClass.progress = (item.avgDuration.toFloat() * 100 / maxNum.toFloat()).toInt()
            } else {
                holder.setVisible(R.id.point_class, View.VISIBLE).setVisible(R.id.pb_class, View.GONE)
            }
        } catch (e: Exception) {
            holder.setVisible(R.id.point_me, View.VISIBLE).setVisible(R.id.pb_me, View.GONE)
            holder.setVisible(R.id.point_class, View.VISIBLE).setVisible(R.id.pb_class, View.GONE)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_week_study_item, parent, false)
    }

    override fun emptyView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_empty_work, parent, false)
    }

    override fun empty(holder: ViewHolder) {
        val context = holder.itemView.context
        holder.setText(R.id.tv_empty, "现在还没有数据~")
        val llEmpty = holder.getView(R.id.ll_empty) as LinearLayout
        llEmpty.background = ContextCompat.getDrawable(context, R.drawable.shape_12_ff)
    }
}