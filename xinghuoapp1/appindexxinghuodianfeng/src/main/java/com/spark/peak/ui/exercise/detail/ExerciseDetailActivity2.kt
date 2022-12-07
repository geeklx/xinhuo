package com.spark.peak.ui.exercise.detail

import android.Manifest
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.questions.Question
import com.example.questions.Questions
import com.example.questions.QuestionsView
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.AnswerItem
import com.spark.peak.bean.AnswerQuestion
import com.spark.peak.bean.AnswerSubmit
import com.spark.peak.ui.dialog.FeedbackDialog
import com.spark.peak.ui.exercise.report.ReportActivity
import com.spark.peak.utlis.DownloadManager
import com.spark.peak.utlis.PermissionUtlis
import kotlinx.android.synthetic.main.activity_exercise_detail2df.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.File

class ExerciseDetailActivity2 : LifeActivity<ExerciseDetailPresenter>() {
    override val presenter = ExerciseDetailPresenter(this)
    override val layoutResId = R.layout.activity_exercise_detail2df
    val practisekey by lazy { intent.getStringExtra(KEY) ?: "" }
    val paperName by lazy { intent.getStringExtra(NAME) ?: "" }
    val parentKey by lazy { intent.getStringExtra(PARENT_KEY) ?: "" }
    val bookKey by lazy { intent.getStringExtra(BOOK_KEY) ?: "" }
    val bookTitle by lazy { intent.getStringExtra(BOOK_TITLE) ?: "" }
    val type by lazy { intent.getStringExtra(TYPE) ?: "" }
    val source by lazy { intent.getStringExtra(SOURCE) ?: "STLX" }
    private var mData: MutableList<Question>? = null

    //    var answerList = mutableListOf<Answer>()
    private var dialog: FeedbackDialog? = null

    private fun feedback(key: String, content: String, type: String) {
        val map = hashMapOf<String, String>()
        map["paperkey"] = practisekey
        map["questionkey"] = key
        map["content"] = content
        map["type"] = type
        presenter.paperFB(map) {
            toast("反馈成功")
            dialog?.dismiss()
        }
    }

    companion object {
        const val KEY = "key"
        const val NAME = "name"
        const val PARENT_KEY = "parentKey"
        const val BOOK_KEY = "bookKey"
        const val BOOK_TITLE = "BOOK_TITLE"

        const val TYPE = "type"
        const val TYPE_QYT = "type_qyt" // 区分全易通和巅峰的做题类型，交卷时调不同的接口 正常错题类型
        const val TYPE_DFXL = "type_dfxl"//闯关
        const val SOURCE = "source"//8:专项  记录是否做过，默认 STLX

    }

    override fun initData() {

        presenter.getExerciseFrame1(practisekey, "0") {
            var index = 1
            val data = mutableListOf<Question>()
            val s = it.toString().replace("\"item\": {", "\"itemAny\": {")
                .replace("\"item\":{", "\"itemAny\": {")
            val questions: Questions? = Gson().fromJson(s, Questions::class.java)
            questions?.let { ques ->
                ques.questionlist?.let { beanList ->
                    beanList.forEach { bean ->
                        val subtitle = bean.isSubtitle()
                        val questionlist = bean
                        bean.questionlist()?.let { list ->
                            list.forEach { question ->
                                question.sort("$index")
                                index++
                                if (subtitle == "2") {
                                    question.groupName(questionlist.groupName())
                                }
                            }
                            data.addAll(list)
                        }
                    }
                }
            }
            mData = data
            questions_view.setData(mData, paperName, false)
            val dataAnswer = mutableListOf<Question>()
            // TODO: 2019/2/14 17:29 霍述雷 答题卡数据结构
            questions?.let { ques ->
                ques.questionlist?.let { list ->
                    dataAnswer.addAll(list)
                }
            }
            answerCardFragment.data = dataAnswer
            questions?.totalCount = data.size.toString()
            if (questions != null) {
                saPaper(questions)
            }
        }
        initAnswerCard()
    }

    override fun onBackPressed() {
        if (fl_answer_card.visibility == View.VISIBLE) {
            questions_view.ivAnswers.performClick()
        } else {
            AlertDialog.Builder(ctx).setMessage("您确定要退出当前试卷吗？")
                .setPositiveButton("确定", { dialogInterface, i ->
                    super.onBackPressed()
                    dialogInterface.dismiss()
                }).setNegativeButton("取消", { dialogInterface, i ->
                    dialogInterface.dismiss()
                }).show()
        }
    }

    override fun handleEvent() {
        questions_view.onBackPressed = {
            onBackPressed()
        }
        questions_view.userAnswers = {
            fl_answer_card.visibility = if (fl_answer_card.visibility == View.VISIBLE) {
                questions_view.start()
                View.GONE
            } else {
                answerCardFragment.showList()// : 2019/2/14 13:42 霍述雷 数据为空
                questions_view.pause()
                View.VISIBLE
            }
        }
        questions_view.submit = {
            questions_view.ivAnswers.performClick()
        }
        questions_view.resInfo = { key, function ->
            presenter.getResInfo(key, "3", "0") {
                function(it.url)
            }
        }
        questions_view.lrcInfo = { url, function ->
            if (url.isNotEmpty()) {
                val path = DownloadManager.getFilePath(url)
                val lycFile = File(path)
                if (lycFile.exists()) {
                    function(lycFile)
                } else {
                    PermissionUtlis.checkPermissions(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) {
                        var path = DownloadManager.getFilePath(url)
                        var tempFile = File(path + ".temp")
                        if (tempFile.exists()) {
                            tempFile.delete()
                        }
                        FileDownloader.setup(ctx)
                        FileDownloader.getImpl().create(url)
                            .setPath(DownloadManager.getFilePath(url))
                            .setListener(object : FileDownloadListener() {
                                override fun warn(task: BaseDownloadTask?) {}

                                override fun completed(task: BaseDownloadTask?) {
                                    try {
                                        val file = File(task?.path ?: "")
                                        function(file)
                                    } catch (e: Exception) {
                                    }
                                }

                                override fun pending(
                                    task: BaseDownloadTask?,
                                    soFarBytes: Int,
                                    totalBytes: Int
                                ) {
                                }

                                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                                    //下载报错temp文件内容正常，取巧
                                    try {
                                        var tempFile = File(task?.path + ".temp")
                                        var file = File(task?.path)
                                        tempFile.renameTo(file)
                                        function(file)
                                    } catch (e: Exception) {
                                    }
                                }

                                override fun progress(
                                    task: BaseDownloadTask?,
                                    soFarBytes: Int,
                                    totalBytes: Int
                                ) {
                                }

                                override fun paused(
                                    task: BaseDownloadTask?,
                                    soFarBytes: Int,
                                    totalBytes: Int
                                ) {
                                }
                            }).start()
                    }
                }
            }
        }
        /**
         * 题目反馈
         */
        QuestionsView.feedback = { questionkey ->
            dialog = FeedbackDialog(ctx, presenter) { type, content ->
                feedback(questionkey ?: "", content, type)
            }
            dialog?.show()
        }
    }

    /**
     * 答题卡上点击题号后，切换
     */
    fun positionQuestion(sort: String) {
        questions_view.ivAnswers.performClick()
        questions_view.scrollToItem(sort)
    }

    /**
     * 交卷
     */
    fun submit() {
        val answers = mutableListOf<AnswerQuestion>()
        mData?.forEach {
            if (it.questionlist() == null || it.questionlist()!!.isEmpty()) {
                if (it.questionInfo()?.questionType() == "4") {
                    val question = AnswerQuestion()
                    question.sort = it.questionInfo()?.sort() ?: ""
                    question.type = it.questionInfo()?.questionType() ?: ""
                    question.questionKey = it.questionInfo()?.questionKey() ?: ""
                    it.questionInfo()?.item()?.let {
                        it.forEach {
                            question.answers.add(
                                AnswerItem(
                                    it.order() ?: "1",
                                    it.useranswer() ?: ""
                                )
                            )
                        }
                    }
                    answers.add(question)
                } else {
                    val question = AnswerQuestion()
                    question.sort = it.questionInfo()?.sort() ?: ""
                    question.type = it.questionInfo()?.questionType() ?: ""
                    question.questionKey = it.questionInfo()?.questionKey() ?: ""
                    question.answers.add(AnswerItem("1", it.questionInfo()?.useranswer() ?: ""))
                    answers.add(question)
                }
            } else {
                it.questionInfo()?.item()?.let {
                    it.forEach {
                        val question = AnswerQuestion()
                        question.sort = it.sort() ?: ""
                        question.type = it.questionType() ?: ""
                        question.questionKey = it.questionKey() ?: ""
                        question.answers.add(AnswerItem("1", it.useranswer() ?: ""))
                        answers.add(question)
                    }
                }
            }
        }
        //检查是否已全部作答
        checkAnswers(answers) {
            var submit = AnswerSubmit(
                practisekey,
                answers,
                getTime(questions_view.chTime.text.toString()),
                parentKey,
                bookKey,
                source,
                parentKey,
                bookKey
            )
            presenter.submit(submit, type) {
                startActivity<ReportActivity>(
                    ReportActivity.DATA to it,
                    ReportActivity.TIME to questions_view.chTime.text.toString(),
                    ReportActivity.KEY to practisekey,
                    ReportActivity.NAME to paperName,
                    ReportActivity.BOOK_KEY to bookKey,
                    ReportActivity.PARENT_KEY to parentKey,
                    ReportActivity.TYPE to type
                )
                finish()
            }
        }
    }

    private fun checkAnswers(answerList: List<AnswerQuestion>, action: () -> Unit) {
        var allDone = true
        answerList.forEach lit@{
            it.answers.forEach {
                if (it.answer.isNullOrBlank()) {
                    allDone = false
                    return@lit
                }
            }
        }

        if (allDone) {
            action()
        } else {
            android.app.AlertDialog.Builder(ctx).setMessage("存在未作答题目，确定要交卷吗？")
                .setPositiveButton("交卷", { _, _ ->
                    action()
                }).setNegativeButton("取消", { dialog, _ ->
                    dialog.dismiss()
                }).create().show()
        }
    }

    private var answerCardFragment = AnswerCardFragment2()

    private fun initAnswerCard() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_answer_card, answerCardFragment)
        transaction.show(answerCardFragment).commit()
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

    private fun saPaper(questions: Questions) {
        try {
            val property = JSONObject()
            property.put("is_there_a_score", "1" == questions.scoreSwitch)
            property.put("number_of_topics", questions.totalCount)
            property.put("paper_name", paperName)
            property.put("presentation_form_paper", "普通试卷")
            property.put("section", questions.gradeName)
            property.put("test_paper_form", if ("2" == questions.issubtitle) "包含大题" else "仅包含小题")
            property.put("test_paper_id", practisekey)
            property.put("special_question_id", if ("8" == source) practisekey else "")
            property.put("special_question_name", paperName)
            property.put("book_matching_name", bookTitle)
            property.put("book_matching_id", bookKey)
            SensorsDataAPI.sharedInstance().track("df_start_paper", property)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
