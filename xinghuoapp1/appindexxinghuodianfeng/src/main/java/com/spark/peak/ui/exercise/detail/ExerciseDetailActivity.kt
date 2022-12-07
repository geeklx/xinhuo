package com.spark.peak.ui.exercise.detail

import android.app.AlertDialog
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.Answer
import com.spark.peak.bean.AnswerQuestion
import com.spark.peak.bean.AnswerSubmit
import com.spark.peak.bean.ExerciseFrameItem
import com.spark.peak.ui.exercise.adapter.ExercisePagerAdapter
import com.spark.peak.ui.exercise.report.ReportActivity
import kotlinx.android.synthetic.main.activity_exercise_detail.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.toast
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class ExerciseDetailActivity : LifeActivity<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_exercise_detail

    val practisekey by lazy { intent.getStringExtra(KEY)?:"" }
    val paperName by lazy { intent.getStringExtra(NAME) ?:""}
    val parentKey by lazy { intent.getStringExtra(PARENT_KEY)?:"" }
    val bookKey by lazy { intent.getStringExtra(BOOK_KEY) ?:""}
    val type by lazy { intent.getStringExtra(TYPE)?:"" }

    var fragmentList = ArrayList<ExerciseDetailFragment>()

    companion object {
        const val KEY = "key"
        const val NAME = "name"
        const val PARENT_KEY = "parentKey"
        const val BOOK_KEY = "bookKey"

        const val TYPE = "type"
        const val TYPE_QYT = "type_qyt" // 区分全易通和巅峰的做题类型，交卷时调不同的接口
        const val TYPE_DFXL = "type_dfxl"

    }


    var haveDone = false
    var remarks = ""
    override fun initData() {
        tv_paper_name.text = paperName
//        initHtmlStr()
//        showAlert()
//        initHtmlStr()
//        view_pager.offscreenPageLimit = 50
        presenter.getExerciseFrame(practisekey, "0") {
            tv_sub.text = "题目数量：${it.totalCount ?: "0"}题"
            tv_sub_type.text = "试卷类型：${it.paperType ?: ""}"
            tv_sub_content.text = it.remarks ?: ""
//            remarks = it.remarks
//            try {
//                it.questionlist?.let { if (it.isNotEmpty()) initExerciseData(it) else toast("数据异常~请您稍候再试~")}
//            } catch (e: Exception) {
//            }
        }

//        initAnswerCard()
        tv_name.text = paperName
    }

//    override fun onBackPressed() {
//        if (fl_answer_card.visibility == View.VISIBLE) {
//            ic_answers.performClick()
//        } else {
//            AlertDialog.Builder(ctx).setMessage("您确定要退出当前试卷吗？").setPositiveButton("确定", { dialogInterface, i ->
//                super.onBackPressed()
//                dialogInterface.dismiss()
//            }).setNegativeButton("取消", { dialogInterface, i ->
//                dialogInterface.dismiss()
//            }).show()
//        }
//    }

    override fun handleEvent() {
        ic_back.setOnClickListener {
            if (fl_answer_card.visibility == View.VISIBLE) {
                ic_answers.performClick()
            } else {
                onBackPressed()
            }
        }
        bt_start.setOnClickListener {
            startActivity<ExerciseDetailActivity2>(
                    ExerciseDetailActivity2.BOOK_KEY to bookKey,
                    ExerciseDetailActivity2.PARENT_KEY to parentKey,
                    ExerciseDetailActivity2.NAME to paperName,
                    ExerciseDetailActivity2.KEY to practisekey,
                    ExerciseDetailActivity2.TYPE to type)

        }

        view_pager.onPageChangeListener {
            var flag = false

            onPageScrollStateChanged {
                when (it) {
                    ViewPager.SCROLL_STATE_DRAGGING -> flag = true
                    ViewPager.SCROLL_STATE_SETTLING -> flag = false
                    ViewPager.SCROLL_STATE_IDLE -> {
                        if (flag && view_pager.currentItem == fragmentList.size - 1) {//最后一页继续滑动，弹出答题卡
                            ic_answers.performClick()
                        }
                    }
                }
            }

            onPageSelected {

                try {
                    fragmentList[it - 1].musicStop()
                } catch (e: Exception) {
                }

                try {
                    fragmentList[it + 1].musicStop()
                } catch (e: Exception) {
                }

                if (fragmentList[it].isNode()) {
                    rl_title.visibility = View.GONE
                } else {
                    rl_title.visibility = View.VISIBLE
                }
                setQIndex((fragmentList[it].qIndex + fragmentList[it].answerIndex + 1).toString())
            }
        }
//        ic_answers.setOnClickListener {
//            fl_answer_card.visibility = if (fl_answer_card.visibility == View.VISIBLE) {
//                View.GONE
//            } else {
//                answerCardFragment.showList()
//                View.VISIBLE
//            }
//        }


    }

    fun onBack(v: View) {
        onBackPressed()
    }

    fun setQIndex(index: String) {
        tv_index.text = index
    }

    /**
     * 构造显示题目及答题卡的list
     */
    var answerList = mutableListOf<Answer>()
    var current = 0 //存储当前题目的题号
    private fun initExerciseData(dataList: List<ExerciseFrameItem>) {
        Log.d("dataList", dataList.toString())
        for (index in 0 until dataList.size) { // 大题分类的结构
            var item: ExerciseFrameItem = dataList[index]
            fragmentList.add(ExerciseDetailFragment.newInstance(ExerciseFrameItem(true, if (index == 0) paperName else "", item.groupName
                    ?: "", item.paperExplain ?: "", if (index == 0) remarks else "")))
//            var answer = Answer(item.questionSort?:"",item.questionType?:"",item.groupName) //答题卡中题目的结构

            var answerQuestions = mutableListOf<AnswerQuestion>() //当前题型下的题目列表
            item.questionlist?.forEach {
                //题目的结构
                var instance = ExerciseDetailFragment.newInstance(it)
                instance.qIndex = current
                fragmentList.add(instance)
                if (it.questionlist != null && it.questionlist!!.isNotEmpty()) { //如果当前题目中存在小题（材料题、多选题，填空题都算做一个题），则解析该题结构
                    var mPosition = 0
                    it.questionlist?.forEach {
                        val question = AnswerQuestion()
                        question.index = ++current
                        question.qPosition = fragmentList.size - 1
                        question.mPosition = mPosition
                        question.haveDone = false
                        question.sort = it.questionSort ?: ""
                        question.type = it.questionType ?: ""
                        question.questionKey = it.questionKey ?: ""
                        answerQuestions.add(question)
                        mPosition++
                    }
                } else { //如果当前题目中没有小题（单选题），直接使用当前数据
                    val question = AnswerQuestion()
                    question.index = ++current
                    question.qPosition = fragmentList.size - 1
                    question.mPosition = 0
                    question.haveDone = false
                    question.sort = it.questionSort ?: ""
                    question.type = it.questionType ?: ""
                    question.questionKey = it.questionKey ?: ""
                    answerQuestions.add(question)
                }
            }
            var answer = Answer(item.groupName, answerQuestions)
            answerList.add(answer)
        }

        tv_total.text = "/" + current.toString()
        var oAdapter = ExercisePagerAdapter(supportFragmentManager, fragmentList)
        view_pager.adapter = oAdapter
        startTime()

    }

    private fun startTime() {
        ch_time.base = SystemClock.elapsedRealtime()
        ch_time.start()
    }

    var htmlStr: StringBuilder = StringBuilder()
    private fun initHtmlStr() {
        var ips: InputStream = resources.assets.open("index.html")
        var reder = BufferedReader(InputStreamReader(ips))
        var str = reder.readLine()

        while (str != null) {
            htmlStr.append(str + "\n")
            str = reder.readLine()
        }
    }


//    private var answerCardFragment = AnswerCardFragment()
//
//    private fun initAnswerCard() {
//        val transaction = fragmentManager.beginTransaction()
//        transaction.add(R.id.fl_answer_card, answerCardFragment)
//        transaction.show(answerCardFragment).commit()
//    }

    /**
     * 答题卡上点击题号后，切换
     */
    fun positionQuestion(qPosition: Int, mPosition: Int) {
        ic_answers.performClick()
        view_pager.currentItem = qPosition
        fragmentList[qPosition].jumpIndex(mPosition)
    }

    /**
     * 非最后一题，作答后 跳转下一题
     */
    fun goNext() {
        if (view_pager.currentItem == fragmentList.size - 1) {
            //是最后一题，则显示答题卡，非最后一题，则跳转到下一题
            ic_answers.performClick()
            fragmentList[fragmentList.size - 1].musicStop()
        } else {
            var index = view_pager.currentItem + 1
            view_pager.currentItem = index
        }
    }

    /**
     * 跳转前一题
     */
    fun goBefore() {
        if (view_pager.currentItem != 0) {
            var index = view_pager.currentItem - 1
            view_pager.currentItem = index
        }
    }


    /**
     * 交卷
     */
    fun submit() {
        if (!haveDone) {
            toast("不能交白卷喔~")
            return
        }

        var answers = mutableListOf<AnswerQuestion>()
        answerList.forEach {
            answers.addAll(it.qList)
        }

        //检查是否已全部作答
        checkAnswers(answers) {
            var submit = AnswerSubmit(practisekey, answers, getTime(ch_time.text.toString()), if (type == TYPE_QYT) parentKey else "", if (type == TYPE_QYT) bookKey else "", "STLX", if (type == TYPE_DFXL) parentKey else "", if (type == TYPE_DFXL) bookKey else "")
            presenter.submit(submit, type) {
                startActivity<ReportActivity>(ReportActivity.DATA to it, ReportActivity.TIME to ch_time.text.toString(), ReportActivity.KEY to practisekey, ReportActivity.NAME to paperName,
                        ReportActivity.BOOK_KEY to bookKey, ReportActivity.PARENT_KEY to parentKey, ReportActivity.TYPE to type)
                finish()
            }
        }
    }

    private fun checkAnswers(answerList: List<AnswerQuestion>, action: () -> Unit) {
        var allDone = true
        answerList.forEach {
            if (!it.haveDone) {
                allDone = false
                return@forEach
            }
        }

        if (allDone) {
            action()
        } else {
            AlertDialog.Builder(ctx).setMessage("存在未作答题目，确定要交卷吗？").setPositiveButton("交卷", { _, _ ->
                action()
            }).setNegativeButton("取消", { dialog, _ ->
                dialog.dismiss()
            }).create().show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (fragmentList.isNotEmpty()) {
            fragmentList[view_pager.currentItem].musicStop()//结束当前页面时，调用结束当前题目音频的方法
        }
    }

    fun getTime(timeStr: String): String {
        var timeArray = timeStr.split(":")

        return when (timeArray.size) {
            1 -> timeArray[0]
            2 -> (timeArray[0].toInt() * 60 + timeArray[1].toInt()).toString()
            3 -> (timeArray[0].toInt() * 60 * 60 + timeArray[1].toInt() * 60 + timeArray[2].toInt()).toString()
            else -> ""
        }
    }

    /**
     * 第一次进来时，弹出防智障提示
     */
    private fun showAlert() {
//        if (SpUtil.showAlert){
//            rl_alert.visibility = View.VISIBLE
//            rl_alert.setOnClickListener {
//                rl_alert.visibility = View.GONE
//            }
//            SpUtil.showAlert = false
//        }
    }
}
