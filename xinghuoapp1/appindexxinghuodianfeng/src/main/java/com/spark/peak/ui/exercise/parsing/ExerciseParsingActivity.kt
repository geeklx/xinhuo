package com.spark.peak.ui.exercise.parsing

import android.Manifest
import com.example.questions.Question
import com.example.questions.Questions
import com.example.questions.QuestionsView
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.ExerciseFrameItem
import com.spark.peak.ui.dialog.FeedbackDialog
import com.spark.peak.ui.exercise.detail.ExerciseDetailPresenter
import com.spark.peak.utlis.DownloadManager
import com.spark.peak.utlis.PermissionUtlis
import kotlinx.android.synthetic.main.activity_parsing_detail.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class ExerciseParsingActivity : LifeActivity<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_parsing_detail
    private var dialog: FeedbackDialog? = null

    val practisekey by lazy { intent.getStringExtra(KEY)?:"" }
    val paperName by lazy { intent.getStringExtra(NAME)?:"" }
    val userpractisekey by lazy { intent.getStringExtra(P_KET)?:"" }
    val selectSort by lazy { intent.getIntExtra(SELECT_SORT, -1) }

    private var mData: MutableList<Question>? = null
    var fragmentList = ArrayList<ExerciseParsingFragment>()

    companion object {
        const val KEY = "key"
        const val NAME = "name"
        const val P_KET = "p_key"

        const val SELECT_SORT = "select_sort"
    }

    override fun configView() {
        super.configView()
//        ch_time.visibility = View.GONE
//        ic_answers.visibility = View.GONE
    }

    var remarks = ""
    override fun initData() {
//        tv_paper_name.text = paperName
//        initHtmlStr()
//        view_pager.offscreenPageLimit = 50
//        presenter.getExerciseParsingFrame(practisekey,userpractisekey,"0"){
//            remarks = it.remarks
//            try {
//                it.questionlist?.let { if (it.isNotEmpty()) initExerciseData(it) else toast("数据异常~请您稍候再试~")}
//            } catch (e: Exception) {
//            }
//        }
        presenter.getExerciseParsingFrame1(practisekey, userpractisekey, "0") {
            var index = 1
            val data = mutableListOf<Question>()
            val s = it.toString().replace("\"item\": {", "\"itemAny\": {")
                    .replace("\"item\":{", "\"itemAny\": {")
            val options: Questions? = Gson().fromJson(s, Questions::class.java)
            options?.let {
                it.questionlist?.let {
                    it.forEach {
                        //                        data.add(it)
                        val subtitle = it.isSubtitle()
                        val questionlist = it
                        it.questionlist()?.let {
                            it.forEach {
                                it.sort("$index")
                                index++
                                if (subtitle == "2") {
                                    it.groupName(questionlist.groupName())
                                }
                            }
                            data.addAll(it)
                        }
                    }
                }
            }
            mData = data
            questions_view.setData(mData, paperName, true)
            if (selectSort>0) {
                questions_view.postDelayed({
                    questions_view.scrollToItem(selectSort.toString())
                },50)
            }
        }

    }

    override fun handleEvent() {
        questions_view.onBackPressed = {
            onBackPressed()
        }
        questions_view.submit = {
            questions_view.ivAnswers.performClick()
        }
//        ic_back.setOnClickListener {
//            onBackPressed()
//        }
//        view_pager.onPageChangeListener {
//            onPageSelected {
//                try {
//                    fragmentList[it-1].musicStop()
//                } catch (e: Exception) {
//                }
//
//                try {
//                    fragmentList[it+1].musicStop()
//                } catch (e: Exception) {
//                }
//
//                if (fragmentList[it].isNode()){
//                    rl_title.visibility = View.GONE
//                }else{
//                    rl_title.visibility = View.VISIBLE
//                }
//                setQIndex((fragmentList[it].qIndex + fragmentList[it].answerIndex +1).toString())
//            }
//        }
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
                    PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
                        var path = DownloadManager.getFilePath(url)
                        var tempFile = File(path + ".temp")
                        if (tempFile.exists()) {
                            tempFile.delete()
                        }
                        FileDownloader.setup(ctx)
                        FileDownloader.getImpl().create(url).setPath(DownloadManager.getFilePath(url))
                                .setListener(object : FileDownloadListener() {
                                    override fun warn(task: BaseDownloadTask?) {}

                                    override fun completed(task: BaseDownloadTask?) {
                                        try {
                                            val file = File(task?.path ?: "")
                                            function(file)
                                        } catch (e: Exception) {
                                        }
                                    }

                                    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {}

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

                                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {}

                                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {}
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

    fun setQIndex(index: String) {
//        tv_index.text = index
    }

    /**
     * 构造显示题目及答题卡的list
     */
    var current = 0 //存储当前题目的题号
    var qIndexs = mutableListOf<Int>() //大题的索引
    var qIndex = 0
    var mIndexs = mutableListOf<Int>() //大题对应小题的索引
    private fun initExerciseData(dataList: List<ExerciseFrameItem>) {
        for (index in 0 until dataList.size) { // 大题分类的结构
            var item: ExerciseFrameItem = dataList[index]
            fragmentList.add(ExerciseParsingFragment.newInstance(ExerciseFrameItem(true, if (index == 0) paperName else "", item.groupName
                    ?: "", item.paperExplain ?: "", if (index == 0) remarks else "")))
            qIndex++
//            var answer = Answer(item.questionSort?:"",item.questionType?:"",item.groupName) //答题卡中题目的结构

            item.questionlist?.forEach {
                //题目的结构
                var instance = ExerciseParsingFragment.newInstance(it)
                instance.qIndex = current
                fragmentList.add(instance)
                if (it.questionlist != null && it.questionlist!!.isNotEmpty()) { //如果当前题目中存在小题（材料题、多选题，填空题都算做一个题），则解析该题结构
                    var mIndex = 0
                    it.questionlist?.forEach {
                        mIndexs.add(mIndex)
                        mIndex++
                        qIndexs.add(qIndex)
                        current++
                    }
                } else {
                    mIndexs.add(0)
                    qIndexs.add(qIndex)
                    current++
                }
                qIndex++
            }
        }

//        tv_total.text = "/"+current.toString()
//        var oAdapter = ExercisePagerAdapter(fragmentManager,fragmentList)
//        view_pager.adapter = oAdapter
//
//        if (selectSort != -1){
//            var handler = Handler()
//            handler.postDelayed({
//                view_pager.currentItem = qIndexs[selectSort-1]
//                fragmentList[qIndexs[selectSort-1]].jumpIndex(mIndexs[selectSort-1])
//            },500)
//        }
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
    /**
     * 非最后一题，作答后 跳转下一题
     */
    fun goNext(){
//        if (view_pager.currentItem == fragmentList.size -1){
//            //do nothing
//        }else{
//            var index = view_pager.currentItem+1
//            view_pager.currentItem = index
//        }
    }

    /**
     * 跳转前一题
     */
    fun goBefore(){
//        if (view_pager.currentItem != 0){
//            var index = view_pager.currentItem-1
//            view_pager.currentItem = index
//        }
    }

    override fun onPause() {
        super.onPause()
//        if (fragmentList.isNotEmpty()){
//            fragmentList[view_pager.currentItem].musicStop()//结束当前页面时，调用结束当前题目音频的方法
//        }
    }
}
