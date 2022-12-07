package com.spark.peak.ui.lesson

import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.net.Uri
import android.widget.Toast
import com.google.gson.Gson
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.R
import com.spark.peak.base.EventMsg
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.NetLesson
import com.spark.peak.bean.ResourceItem
import com.spark.peak.servicePhone
import com.spark.peak.ui.mine.order.comment.OrderCommentActivity
import com.spark.peak.ui.netLessons.NetDownFragment
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_lessons.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class LessonDetailActivity(override val layoutResId: Int = R.layout.activity_lessons) :
    LifeActivity<LessonDetailPresenter>() {
    override val presenter = LessonDetailPresenter(this)
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private val grade by lazy { intent.getStringExtra(GRADE) ?: "" }
    private var lesson: NetLesson? = null

    override fun configView() {
        EventBus.getDefault().register(this)
//        supportFragmentManager.beginTransaction().replace(R.id.fl_lessons, NetListFragment.instance(key)).commit()
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_phone.setOnClickListener {
            saOperation("点击客服热线")
            val intent = Intent(ACTION_DIAL, Uri.parse("tel:$servicePhone"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        tv_buy.setOnClickListener {
            saOperation("点击评论")
            if (lesson?.iscomment == 0) {
                startActivity<OrderCommentActivity>(OrderCommentActivity.KEY to key)
            }
        }
        tv_lesson_collect.setOnClickListener {
            saOperation("点击收藏")
            if (tv_lesson_collect.isSelected) {
                //取消收藏
                val map = hashMapOf<String, String>()
                map.put("key", key)
                presenter.deleteMyNetClass(map) {
                    tv_lesson_collect.isSelected = false
                    tv_lesson_collect.text = "收藏课程"
                    Toast.makeText(this, "取消收藏成功", Toast.LENGTH_LONG).show()
                }
            } else {
                //收藏
                presenter.freeBuyNetCourse(key) {
                    tv_lesson_collect.isSelected = true
                    tv_lesson_collect.text = "已收藏"
                    Toast.makeText(this, "收藏成功", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun initData() {
        presenter.loadData(key) {
            lesson = it
            tv_title.text = it.title
            tv_time.text = "${it.validitytime}"
            val isown = it.isown
            tv_lesson_collect.isSelected = isown == 1
            tv_lesson_collect.text = if (isown == 1) "已收藏" else "收藏课程"
            if (isown == 1) {
                if (it.iscomment == 0) {
                    tv_buy.text = "去评论"
                } else {
                    tv_buy.text = "已评论"
                    tv_buy.isEnabled = false
                }
            }
            presenter.getNetLessonCatalogue(key) {
                try {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fl_lessons, NetDownFragment.instance(
                                list = it.children,
                                count = it.mediacount,
                                type = "wk",
                                name = lesson?.title ?: "",
                                size = it.mediasize,
                                isOwn = "1",
                                enable = true
                            )
                        )
                        .commit()
                } catch (e: Exception) {
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun update(msg: EventMsg) {
        if (msg.action == "order_comment") {
            initData()
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    companion object {
        const val KEY = "key"
        const val GRADE = "GRADE"
    }

    private fun saOperation(name: String) {
        try {
            val property = JSONObject()
            property.put("course_key", key)
            lesson?.let {
                property.put("course_name", it.title)
            }
            property.put("course_section", grade)
            property.put("operation_name", name)
            SensorsDataAPI.sharedInstance().track("df_minicourse_study", property)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saOperation(name: String, item: ResourceItem) {
        try {
            val property = JSONObject()
            property.put("course_key", item.id)
            lesson?.let {
                property.put("course_name", it.title)
            }
            property.put("course_section", grade)
            property.put("operation_name", name)
            property.put("period_key", key)
            property.put("peroid_name", item.name)
            property.put("teacher_name", item.teacher)
            SensorsDataAPI.sharedInstance().track("df_minicourse_study", property)

            val property1 = JSONObject()
            property1.put("course_id", item.id)
            lesson?.let {
                property1.put("course_name", it.title)
                property1.put("course_validity", it.validitytime)
            }
            property1.put("period_id", key)
            property1.put("period_name", item.name)
            property1.put("teacher_id", "")
            property1.put("teacher_name", item.teacher)
            SpUtil.SenorData = property1.toString()
            SensorsDataAPI.sharedInstance().track("df_start_watch_course_video", property1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saVideo() {

    }
}
