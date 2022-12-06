package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson

import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.activity_moth_data.*
import kotlinx.android.synthetic.main.activity_study_data.toolbar
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.LessonStudyMonthData
import tuoyan.com.xinghuo_dayingindex.bean.LessonStudyMonthDataDetail
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.LessonsPresenter

class MonthDataActivity : LifeActivity<LessonsPresenter>() {

    override val presenter: LessonsPresenter
        get() = LessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_moth_data

    private val data by lazy { intent.getSerializableExtra(DATA) as LessonStudyMonthData }

    override fun configView() {
        tv_title.text = data.name.replace("年","年 ")
    }

    override fun initData() {
        super.initData()
        presenter.getYmDetail(data.courseKey, data.ym) {
            try {//转数字异常
                setTimeProgress(it)
                setVideoProgress(it)
                setWorkProgress(it)
            } catch (e: Exception) {
            }
        }
    }

    private fun setVideoProgress(data: LessonStudyMonthDataDetail) {
        tv_listen_time.text = Html.fromHtml("我 • 完成<font color='#5467ff'> ${data.videoNum} </font>节")
        tv_class_listen.text = Html.fromHtml("班级平均 • 完成<font color='#15D25F'> ${data.videoAvgNum} </font>节")
        tv_listen_rate.text = "超越了 ${data.videoBeat}% 的同学"
        if (data.videoMax.toFloat() > 0) {
            if (data.videoNum.toFloat() > 0) {
                pb_me_listen.progress = (data.videoNum.toFloat() * 100 / data.videoMax.toFloat()).toInt()
            } else {
                pb_me_listen.visibility = View.INVISIBLE
                point_me_listen.visibility = View.VISIBLE
            }
            if (data.videoAvgNum.toFloat() > 0) {
                pb_class_listen.progress = (data.videoAvgNum.toFloat() * 100 / data.videoMax.toFloat()).toInt()
            } else {
                pb_class_listen.visibility = View.INVISIBLE
                point_class_listen.visibility = View.VISIBLE
            }
        } else {
            pb_me_listen.visibility = View.INVISIBLE
            pb_class_listen.visibility = View.INVISIBLE
            point_class_listen.visibility = View.VISIBLE
            point_me_listen.visibility = View.VISIBLE
        }
    }

    private fun setTimeProgress(data: LessonStudyMonthDataDetail) {
        tv_me_time.text = Html.fromHtml("我 • <font color='#5467ff'> ${data.durationNumStr} </font>")
        tv_class_time.text = Html.fromHtml("班级平均 • <font color='#15D25F'> ${data.durationAvgNumStr} </font>")
        tv_time_rate.text = "超越了 ${data.durationBeat}% 的同学"
        if (data.durationMax.toFloat() > 0) {
            if (data.durationNum.toFloat() > 0) {
                pb_me_time.progress = (data.durationNum.toFloat() * 100 / data.durationMax.toFloat()).toInt()
            } else {
                pb_me_time.visibility = View.INVISIBLE
                point_me_time.visibility = View.VISIBLE
            }
            if (data.durationAvgNum.toFloat() > 0) {
                pb_class_time.progress = (data.durationAvgNum.toFloat() * 100 / data.durationMax.toFloat()).toInt()
            } else {
                pb_class_time.visibility = View.INVISIBLE
                point_class_time.visibility = View.VISIBLE
            }
        } else {
            pb_me_time.visibility = View.INVISIBLE
            pb_class_time.visibility = View.INVISIBLE
            point_me_time.visibility = View.VISIBLE
            point_class_time.visibility = View.VISIBLE
        }
    }

    private fun setWorkProgress(data: LessonStudyMonthDataDetail) {
        if ("0" == data.homeworkState) {
            //无作业
            ll_test.visibility = View.GONE
            tv4.visibility = View.GONE
        } else {
            tv_test_time.text = Html.fromHtml("我 • 完成<font color='#5467ff'> ${data.workNum} </font>个")
            tv_class_test.text = Html.fromHtml("班级平均 • 完成<font color='#15D25F'> ${data.workAvgNum} </font>个")
            tv_test_rate.text = "超越了 ${data.workBeat}% 的同学"
            if (data.workMax.toFloat() > 0) {
                if (data.workNum.toFloat() > 0) {
                    pb_me_test.progress = (data.workNum.toFloat() * 100 / data.workMax.toFloat()).toInt()
                } else {
                    pb_me_test.visibility = View.INVISIBLE
                    point_me_test.visibility = View.VISIBLE
                }
                if (data.workAvgNum.toFloat() > 0) {
                    pb_class_test.progress = (data.workAvgNum.toFloat() * 100 / data.workMax.toFloat()).toInt()
                } else {
                    pb_class_test.visibility = View.INVISIBLE
                    point_class_test.visibility = View.VISIBLE
                }
            } else {
                pb_me_test.visibility = View.INVISIBLE
                pb_class_test.visibility = View.INVISIBLE
                point_me_test.visibility = View.VISIBLE
                point_class_test.visibility = View.VISIBLE
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    companion object {
        const val DATA = "data"
    }
}