package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_lesson_works.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.ClassWork
import tuoyan.com.xinghuo_dayingindex.bean.LessonWork
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * 网课回放列表 课后作业列表
 */
class LessonWorksActivity : LifeActivity<NetLessonsPresenter>() {
    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_lesson_works

    private val mAdapter by lazy {
        LessonWorkAdapter { work ->
            if ("1" != work.isFinish) {
                startActivity<ExerciseDetailKActivity>(
                    ExerciseDetailKActivity.KEY to work.paperKey,
                    ExerciseDetailKActivity.NAME to work.paperName,
                    ExerciseDetailKActivity.CAT_KEY to work.videoKey,
                    ExerciseDetailKActivity.NET_COURSE_KEY to courseKey,
                    ExerciseDetailKActivity.NET_COURSE_VIDEO_KEY to videoKey,
                    ExerciseDetailKActivity.SOURCE to "6",
                    ExerciseDetailKActivity.USER_PRACTISE_KEY to work.userPracticeKey
                )
            } else {
                presenter.getReport(work.paperKey, work.userPracticeKey) {
                    startActivity<ReportActivity>(
                        ReportActivity.DATA to it,
                        ReportActivity.TIME to "",
                        ReportActivity.KEY to work.paperKey,
                        ReportActivity.CAT_KEY to work.videoKey,
                        ReportActivity.NAME to work.paperName,
                        ReportActivity.TYPE to "1",
                        ReportActivity.EVAL_STATE to "1"
                    )
                }
            }
        }
    }

    private val courseKey by lazy { intent.getStringExtra(COURSE_KEY) ?: "" }
    private val videoKey by lazy { intent.getStringExtra(VIDEO_KEY) ?: "" }

    companion object {
        val COURSE_KEY = "courseKey"
        val VIDEO_KEY = "videoKey"
    }

    override fun configView() {
        setSupportActionBar(tb_works)
        rv_work_list.layoutManager = LinearLayoutManager(this)
        rv_work_list.adapter = mAdapter
    }

    override fun handleEvent() {
        tb_works.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        presenter.getWorks(courseKey, videoKey, 1) { list ->
            mAdapter.setData(list)
        }
    }
}

private class LessonWorkAdapter(var onclick: (LessonWork) -> Unit) :
    BaseAdapter<ClassWork>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: ClassWork) {
        val context = holder.itemView.context
        val tv_title = holder.getView(R.id.tv_title) as TextView
        val img_sel = holder.getView(R.id.img_sel) as ImageView
        val ctl_title = holder.getView(R.id.ctl_title) as ConstraintLayout
        val rlv_lesson = holder.getView(R.id.rlv_lesson) as RecyclerView
        val params = rlv_lesson.layoutParams as ConstraintLayout.LayoutParams
        if ("XUNIMULU_AAAAA_BBBBB" == item.catalogName) {
            ctl_title.visibility = View.GONE
            rlv_lesson.background = ContextCompat.getDrawable(context, R.color.white)
            params.setMargins(0, 0, 0, 0)
        } else {
            rlv_lesson.background = ContextCompat.getDrawable(context, R.drawable.shape_12_f9fafc)
            params.setMargins(DeviceUtil.dp2px(context, 40f).toInt(), 0, DeviceUtil.dp2px(context, 30f).toInt(), 0)
            ctl_title.visibility = View.VISIBLE
            tv_title.text = item.catalogName
            if (holder.adapterPosition == 0) {
                holder.setImageResource(R.id.img_pre, R.mipmap.icon_work_pre)
            } else {
                holder.setImageResource(R.id.img_pre, R.mipmap.icon_work_up)
            }
        }
        rlv_lesson.layoutParams = params
        img_sel.setOnClickListener {
            item.isExpand = !item.isExpand
            notifyItemChanged(holder.adapterPosition)
        }
        val adapter = ItemAdapter("XUNIMULU_AAAAA_BBBBB" == item.catalogName) {
            onclick(it)
        }
        rlv_lesson.layoutManager = LinearLayoutManager(mContext)
        rlv_lesson.adapter = adapter
        adapter.setData(item.resourceList)
        if (item.isExpand) {
            rlv_lesson.visibility = View.VISIBLE
        } else {
            rlv_lesson.visibility = View.GONE
        }
        img_sel.isSelected = item.isExpand
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_lesson_work_res, parent, false)
    }

    override fun emptyImageRes(): Int {
        return R.drawable.empty_study
    }

    override fun emptyText(): String {
        return "暂无内容"
    }
}

private class ItemAdapter(var isSingle: Boolean, var ItemClick: (LessonWork) -> Unit) : BaseAdapter<LessonWork>() {
    override fun convert(holder: ViewHolder, item: LessonWork) {
        val context = holder.itemView.context
        val ctl_item = holder.getView(R.id.ctl_item) as ConstraintLayout
        val tv_title = holder.getView(R.id.tv_title) as TextView
        if (isSingle) {
            val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 65f).toInt())
            ctl_item.setPadding(DeviceUtil.dp2px(context, 20f).toInt(), 0, DeviceUtil.dp2px(context, 20f).toInt(), 0)
            ctl_item.layoutParams = params
            tv_title.textSize = 16f
        } else {
            val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
            ctl_item.setPadding(DeviceUtil.dp2px(context, 15f).toInt(), 0, DeviceUtil.dp2px(context, 15f).toInt(), 0)
            ctl_item.layoutParams = params
            tv_title.textSize = 15f
        }
        //做题是否完成
        val tvRight = holder.getView(R.id.tv_right) as TextView
        tvRight.isSelected = "2" == item.state
        tvRight.text = if ("2" == item.state) "已完成" else if ("1" == item.state) "继续做题" else ""
        holder.setText(R.id.tv_title, item.paperName)
        holder.itemView.setOnClickListener {
            ItemClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_lesson_work, parent, false)
    }
}
