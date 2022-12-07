package com.spark.peak.ui.netLessons

import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.NetLesson
import com.spark.peak.servicePhone
import com.spark.peak.ui.dialog.ShareDialog
import com.spark.peak.ui.lesson.LessonDetailActivity
import com.spark.peak.utlis.ShareUtils
import kotlinx.android.synthetic.main.activity_net_lessons.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject


/**
 * 创建者：
 * 时间：
 */
class NetLessonsActivity() : LifeActivity<NetLessonsPresenter>() {
    override val presenter by lazy { NetLessonsPresenter(this) }
    override val layoutResId: Int get() = R.layout.activity_net_lessons
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private val grade by lazy { intent.getStringExtra(GRADE) ?: "" }
    private var lesson: NetLesson? = null
    private val shareDialog by lazy {
        ShareDialog(this) {
            ShareUtils.share(
                this, it, lesson?.title ?: "", lesson?.content ?: "",
                "${WEB_BASE_URL}network/network?key=$key", lesson?.coverimg
            )
        }
    }
    private val netCommentFragment by lazy { NetCommentFragment.instance(key) }

    override fun configView() {
        setSupportActionBar(toolbar)
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        img_share.setOnClickListener {
            shareDialog.show()
        }
        rg_lesson.setOnCheckedChangeListener { group, checkedId ->
            view_pager.currentItem = group.indexOfChild(group.findViewById(checkedId))
        }
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                (rg_lesson.getChildAt(position) as RadioButton).isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        tv_buy.setOnClickListener {
            startActivity<LessonDetailActivity>(
                LessonDetailActivity.KEY to key,
                LessonDetailActivity.GRADE to grade
            )
        }
        tv_phone.setOnClickListener {
            val intent = Intent(ACTION_DIAL, Uri.parse("tel:$servicePhone"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        tv_lesson_collect.setOnClickListener {
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
            initView(it)
            saDetail(lesson!!)
        }
        presenter.getNetLessonCatalogue(key) {
            val dataList = arrayListOf(
                NetDownFragment.instance(
                    list = it.children,
                    count = it.mediacount,
                    type = "wk",
                    name = lesson?.title ?: "",
                    size = it.mediasize,
                    isOwn = "0",
                    enable = false
                ), netCommentFragment
            )
            view_pager.adapter = NetPagerAdapter(dataList, supportFragmentManager)
        }
    }

    private fun initView(it: NetLesson) {
        tv_lesson_name.text = it.title
        tv_lesson_msg.text = "${it.period}课时|${it.validitytime}|${it.buyers}人已学习"
        if (it.state.toInt() == 1) {
            mToast("未上架")
            onBackPressed()
        } else if (it.state.toInt() == 9) {
            mToast("已删除")
            onBackPressed()
        }
        if (it.videourl.isNullOrBlank()) {
            iv_cover.visibility = View.VISIBLE
            video_player.visibility = View.GONE
            Glide.with(this).load(it.coverTopImg).placeholder(R.drawable.default_lesson)
                .into(iv_cover)
        } else {
            iv_cover.visibility = View.GONE
            video_player.visibility = View.VISIBLE
            try {
                initCoverVideo(it.videourl, it.coverTopImg)
            } catch (e: Exception) {
            }
        }
        tv_lesson_collect.isSelected = it.isown == 1
        tv_lesson_collect.text = if (it.isown == 1) "已收藏" else "收藏课程"
    }

    /**
     * 测试视频播放
     */
    private fun initCoverVideo(url: String, img: String?) {
        img?.let {
            val imgView = ImageView(this)
            val lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            imgView.layoutParams = lp
            Glide.with(this).load(img).into(imgView)
            video_player.thumbImageView = imgView
        }
        video_player.setUp(url, false, "")
        video_player.setIsTouchWiget(true)
        video_player.fullscreenButton.setOnClickListener {
            video_player.startWindowFullscreen(this, false, false)
        }
        video_player.backButton.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        video_player.onVideoResume()
    }

    override fun onPause() {
        super.onPause()
        video_player.onVideoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        video_player.releaseOU()
        video_player.setVideoAllCallBack(null)
    }

    companion object {
        const val KEY = "key"
        const val GRADE = "grade"
    }

    private fun saDetail(lesson: NetLesson) {
        try {
            val property = JSONObject()
            property.put("course_key", key)
            property.put("course_name", lesson.title)
            property.put("course_section", grade)
            SensorsDataAPI.sharedInstance().track("df_minicourse_detail", property)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}