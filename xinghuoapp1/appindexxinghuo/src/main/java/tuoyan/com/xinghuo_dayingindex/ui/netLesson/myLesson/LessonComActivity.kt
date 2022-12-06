package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_lesson_com.*
import org.jetbrains.anko.backgroundColor
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.LessonsPresenter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.ComAdapter


class LessonComActivity : LifeActivity<LessonsPresenter>() {
    override val presenter: LessonsPresenter
        get() = LessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_lesson_com

    val pKey by lazy { intent.getStringExtra(P_KEY) ?: "" }
    val key by lazy { intent.getStringExtra(KEY) ?: "" }

    //6:课程评价;8:主观题 单题评价;
    val type by lazy { intent.getStringExtra(TYPE) ?: "" }
    var isComment = "0"
    var page = 1

    companion object {
        val P_KEY = "p_key"
        val KEY = "key"
        val IS_COMMENT = "IS_COMMENT"
        val TYPE = "type"
    }

    override fun onBackPressed() {
        if (type == "8") {
            intent.putExtra("isComment", isComment)
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }

    override fun configView() {
        if (type == "8") {
            tv_title.text = "评价列表"
        }
        setSupportActionBar(tb_lesson_com)
        ll_comment.visibility = View.GONE
        rv_lesson_com.layoutManager = LinearLayoutManager(this)
        rv_lesson_com.adapter = mAdapter
        isComment = intent.getStringExtra(IS_COMMENT) ?: ""
        //1:已评价；0：未评价
        if ("1" == isComment) {
            btn_com.visibility = View.GONE
            shadow.visibility = View.GONE
        }
    }

    override fun handleEvent() {
        tb_lesson_com.setNavigationOnClickListener { onBackPressed() }
        btn_com.setOnClickListener {
            showComment()
        }

        tv_cancel.setOnTouchListener { _, _ ->
            dismissComment()
            false
        }

        tv_confirm.setOnTouchListener { _, _ ->
            confirmCom()
            dismissComment()
            false
        }

        srl_lesson_com.setOnRefreshListener {
            refreshData()
        }
    }

    var mAdapter = ComAdapter(false) {
        loadMore()
    }

    override fun initData() {
        rv_lesson_com.adapter = mAdapter
        presenter.loadComment(key, "", page) { list, total ->
            mAdapter.setData(list)
            mAdapter.setMore(true)
            mAdapter.total = total
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun refreshData() {
        page = 1
        presenter.loadComment(key, "", page) { list, total ->
            mAdapter.setData(list)
            mAdapter.setMore(true)
            mAdapter.total = total
            srl_lesson_com.isRefreshing = false
        }
    }

    private fun loadMore() {
        page++
        presenter.loadComment(key, "", page) { list, total ->
            if (list.isEmpty()) {
                mAdapter.setMore(false)
            } else {
                mAdapter.addData(list)
            }
        }
    }

    private fun showComment() {
        ll_comment.visibility = View.VISIBLE
        shadow.visibility = View.GONE
        btn_com.visibility = View.GONE
        edit_com.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(edit_com, 0)
    }

    private fun dismissComment() {
        ll_comment.visibility = View.GONE
        shadow.visibility = View.VISIBLE
        btn_com.visibility = View.VISIBLE
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(edit_com.windowToken, 0)
    }

    private fun confirmCom() {
        val trim = edit_com.text.toString().trim()
        if (trim.isBlank()) {
            mToast("评论不能为空")
            return
        }
        if (trim.length > 200) {
            mToast("评论不能超过200字")
            return
        }
        presenter.comment(key, trim, start.toString(), pKey, type) {
            btn_com.visibility = View.GONE
            shadow.visibility = View.GONE
            mToast("评论成功")
            isComment = "1"
            refreshData()
        }
    }

    /**
     * 评价星星
     * 我也不想这样写 暂时先这样吧。。。
     */
    var start = 5 //TODO 评分星星

    @SensorsDataTrackViewOnClick
    fun star(view: View) {
        var index = -1
        for (i in 0 until ll_stars.childCount) {
            var star = ll_stars.getChildAt(i)
            when {
                star == view -> {
                    index = i
                    start = i + 1
                    star.backgroundColor = Color.parseColor("#FFAF30")
                }
                index == -1 -> star.backgroundColor = Color.parseColor("#FFAF30")
                else -> star.backgroundColor = Color.parseColor("#ffffff")
            }
        }
    }
}
