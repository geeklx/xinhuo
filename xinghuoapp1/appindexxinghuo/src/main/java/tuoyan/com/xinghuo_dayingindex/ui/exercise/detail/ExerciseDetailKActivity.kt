package tuoyan.com.xinghuo_dayingindex.ui.exercise.detail
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_exercise_detail.*
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.toast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.TYPE_EX
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui.dialog.SelectedDialog
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerV4Adapter
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.utlis.ImageSeletedUtil
import tuoyan.com.xinghuo_dayingindex.utlis.TypeUtil

/**
 * 做题模块转原生
 *对应ExerciseDetailActivity
 */
class ExerciseDetailKActivity : LifeActivity<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_exercise_detail
    private val answerCardFragment by lazy { AnswerCardKFragment() }
    private var remarks = ""//加载首页的提示语位于做题第一个页面中，remarks在返回数据list之外，需要单独记录，添加到第一个页面中
    var paperKey = ""//虚拟试卷时返回真正的试卷可以
    var sourcePaperKey = ""//虚拟试卷时返回
    private val practisekey by lazy { intent.getStringExtra(KEY) ?: "" }
    private val userPractiseKey by lazy { intent.getStringExtra(USER_PRACTISE_KEY) ?: "" }
    val catKey by lazy { intent.getStringExtra(CAT_KEY) ?: "" }
    val paperName: String by lazy { intent.getStringExtra(NAME) ?: "" }
    val exType by lazy { intent.getIntExtra(EX_TYPE, EX_TYPE_0) }
    val typeName by lazy { intent.getStringExtra(TYPE) ?: TYPE_EX }//试卷类型
    val source by lazy { intent.getStringExtra(SOURCE) ?: "" }//预习列表中的 试卷、测评，调用对应的提交接口是传source=7,其他情况不用传
    val netCourseKey by lazy { intent.getStringExtra(NET_COURSE_KEY) ?: "" }//网课跳入测评时，传入的网课key和小节key，用于统计当前测评是否做完，数据展示
    val netCourseVideoKey by lazy { intent.getStringExtra(NET_COURSE_VIDEO_KEY) ?: "" }//网课跳入测评时，传入的网课key和小节key，用于统计当前测评是否做完，数据展示
    val netCourseResourceKey by lazy { intent.getStringExtra(NET_COURSE_RESOURCE_KEY) ?: "" }//网课跳入测评时，传入的网课key和小节key，用于统计当前测评是否做完，数据展示

    private var fragmentList = ArrayList<BaseV4Fragment>()
    private var currentIndex = 0//当前viewpager位置
    var isDone = false//是否做题，不能交白卷
    val sensorsData by lazy { SensorsExercise() }
    var scoreSwitch = "0";//
    var issubtitle = "";//是否有大题 2：有大题
    var gradeName = "";//学段
    var saveTime = 0//上次保存的做题时长

    /**
     * 构造显示题目及答题卡的list
     * 普通做题模块下调用
     * 答题卡页面调用
     */
    var answerList = mutableListOf<Answer>()

    /**
     * 当前小题的位置 循环+1 赋值
     */
    private var current = 0

    //TODO 主观题上传图片
    val selectDialog by lazy {
        SelectedDialog("拍照", "从相册选择", this) {
            ImageSeletedUtil.phoneClick(it, this, false)
        }
    }

    companion object {
        //普通试卷传递参数
        const val KEY = "key"
        const val USER_PRACTISE_KEY = "USERPRACTISEKEY"
        const val NAME = "name" //        试卷名称
        const val CAT_KEY = "cat_key" //TODO 某些做题类型下 需要传相关的key

        @Deprecated("TYPE replace")
        const val EX_TYPE = "ex_type" //试卷类型
        const val WritingAndTranslation = "6"//主观题类型 翻译写作
        const val Material = "5"//材料题
        const val SingleChoice = "1"//单选题
        const val EX_TYPE_0 = 0 //正常做题（最普通的类型）
        const val EX_TYPE_SP = 1 //专项练习类型
        const val EX_TYPE_PG = 2 //过级包类型（测评）
        const val EX_TYPE_WORK = 3 //网课课后作业
        const val SOURCE = "source"//预习列表中的 试卷、测评，调用对应的提交接口是传source=5 课后作业 source=6  测评小节 source=7其他情况不用传

        //        const val WritingAndTranslation = "5"//主观题类型 翻译写作
        const val NET_COURSE_KEY = "net_course_key "//网课中有测评,网课作业跳入做题时，需要传网课key 和小节 key
        const val NET_COURSE_VIDEO_KEY = "net_course_video_key"//网课中有测评，网课作业跳入做题时，需要传网课key 和小节key
        const val NET_COURSE_RESOURCE_KEY = "netCourseResourceKey"//网课中有测评，网课作业跳入做题时，需要传网课key 和小节key 预习key
        const val TYPE = "TYPE"//
    }

    override fun handleEvent() {
        super.handleEvent()
        ic_back.setOnClickListener {
            onBackPressed()
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
                setMusicStop(currentIndex)
                currentIndex = it
                setTLTileView(it)
            }
        }
        ic_answers.setOnClickListener {
            saOption("去答题卡")
            fl_answer_card.visibility = if (fl_answer_card.visibility == View.VISIBLE) {
                if (fragmentList[currentIndex] is ExerciseDetailTitleFragment) {
                    iv_collection.visibility = View.GONE
                } else {
                    iv_collection.visibility = View.VISIBLE
                }
                ch_time.visibility = View.VISIBLE
                ic_answers.visibility = View.VISIBLE
                tv_title.visibility = View.GONE
                View.GONE
            } else {
                setMusicStop(currentIndex)
                answerCardFragment.showList()
                iv_collection.visibility = View.GONE
                ch_time.visibility = View.GONE
                iv_catalog.visibility = View.GONE
                ic_answers.visibility = View.GONE
                tv_title.visibility = View.VISIBLE
                View.VISIBLE
            }
        }
        iv_collection.setOnClickListener {
            saOption("收藏")
            when (val fragment = fragmentList[currentIndex]) {
                is ExerciseDetailTranslateAndWritingFragment -> {
                    if ("0" == fragment.getCollection()) {
                        presenter.addCollection(mutableMapOf("targetkey" to (fragment.itemData?.questionKey ?: ""), "type" to "4")) {
                            iv_collection.isSelected = true
                            fragment.setCollection("1")
                        }
                    } else {
                        presenter.deleteCollection(fragment.itemData?.questionKey ?: "") {
                            iv_collection.isSelected = false
                            fragment.setCollection("0")
                        }
                    }
                }
                is ExerciseDetailChoiceFragment -> {
                    if ("0" == fragment.getCollection()) {
                        presenter.addCollection(mutableMapOf("targetkey" to (fragment.itemData?.questionKey ?: ""), "type" to "4")) {
                            iv_collection.isSelected = true
                            fragment.setCollection("1")
                        }
                    } else {
                        presenter.deleteCollection(fragment.itemData?.questionKey ?: "") {
                            iv_collection.isSelected = false
                            fragment.setCollection("0")
                        }
                    }
                }
                is ExerciseSingleChoiceFragment -> {
                    if ("0" == fragment.getCollection()) {
                        presenter.addCollection(mutableMapOf("targetkey" to (fragment.itemData?.questionKey ?: ""), "type" to "4")) {
                            iv_collection.isSelected = true
                            fragment.setCollection("1")
                        }
                    } else {
                        presenter.deleteCollection(fragment.itemData?.questionKey ?: "") {
                            iv_collection.isSelected = false
                            fragment.setCollection("0")
                        }
                    }
                }
            }
        }
    }

    /**
     *设置前一个页面暂停播放
     */
    private fun setMusicStop(pos: Int) {
        when (val fragment = fragmentList[pos]) {
            is ExerciseDetailChoiceFragment -> {
                fragment.musicStop()
            }
            is ExerciseSingleChoiceFragment -> {
                fragment.musicStop()
            }
        }
    }

    /**
     *是否显示顶部试卷名称 当前题号 总题号
     */
    private fun setTLTileView(pos: Int) {
        when (val fragment = fragmentList[pos]) {
            is ExerciseDetailTitleFragment -> {
                rl_title.visibility = View.GONE
                iv_collection.visibility = View.GONE
            }
            is ExerciseDetailTranslateAndWritingFragment -> {
                rl_title.visibility = View.VISIBLE
                iv_collection.visibility = View.VISIBLE
                setIndex(fragment.qSort)
                //获取当前收藏状态
                iv_collection.isSelected = fragment.getCollection() == "1"
            }
            is ExerciseDetailChoiceFragment -> {
                rl_title.visibility = View.VISIBLE
                iv_collection.visibility = View.VISIBLE
                setIndex(fragment.qSort)
                //获取当前收藏状态
                iv_collection.isSelected = fragment.getCollection() == "1"
            }
            is ExerciseSingleChoiceFragment -> {
                rl_title.visibility = View.VISIBLE
                iv_collection.visibility = View.VISIBLE
                setIndex(fragment.qSort)
                //获取当前收藏状态
                iv_collection.isSelected = fragment.getCollection() == "1"
            }
        }
    }

    override fun configView() {
        super.configView()
        tv_paper_name.text = paperName
        initAnswerCard()
    }

    override fun initData() {
        super.initData()
        iv_catalog.visibility = View.GONE
        presenter.getExerciseParsingFrame(practisekey, userPractiseKey, catKey) { paper ->
            saveTime = paper.lastAudioTime.toInt() ?: 0
            paperKey = paper.paperKey ?: ""
            sourcePaperKey = paper.sourcePaperKey ?: ""
            remarks = paper.remarks ?: ""
            scoreSwitch = paper.scoreSwitch ?: ""
            issubtitle = paper.issubtitle ?: ""
            gradeName = paper.gradeName ?: ""
            try {
                paper.questionlist?.let { list -> if (list.isNotEmpty()) initExerciseData(paper.lastQuestionKey, list) else toast("数据异常~请您稍候再试~") }
            } catch (e: Exception) {
            }
            sensorsData.test_paper_id = practisekey
            sensorsData.paper_name = paperName
            sensorsData.section = paper.gradeName
            sensorsData.is_there_a_score = "1" == scoreSwitch
            sensorsData.number_of_topics = current
            sensorsData.test_paper_form = if ("2" == issubtitle) "包含大题" else "仅包含小题"
            sensorsData.presentation_form_paper = TypeUtil.getType(typeName)
            startSa()
        }
        postLockPaper(practisekey)
    }

    private fun initExerciseData(lastQuestionKey: String, dataList: List<ExerciseFrameItem>) {
        var pIndex = -1
        for (index in dataList.indices) { // 大题分类的结构 body->questionlist
            val item: ExerciseFrameItem = dataList[index]
            //idNode,当前页面无用，前人写在构造函数里了
            val partItem = ExerciseFrameItem(true, if (index == 0) paperName else "", item.groupName, item.paperExplain, remarks)
            //  这个当前也没用到
            partItem.isSubtitle = item.isSubtitle
            fragmentList.add(ExerciseDetailTitleFragment.newInstance(partItem))
            pIndex++
            val answerQuestions = mutableListOf<AnswerQuestion>() //当前题型下的题目列表
            item.questionlist?.forEach { question ->//body->questionlist->questionlist
                when (question.questionType) {
                    WritingAndTranslation -> {//主观题 翻译写作
                        val instance = ExerciseDetailTranslateAndWritingFragment.newInstance(question)
                        instance.qIndex = current
                        fragmentList.add(instance)
                        pIndex++
                        singleSheet(question, answerQuestions, lastQuestionKey, pIndex)
                    }
                    Material -> {//材料题
                        val instance = ExerciseDetailChoiceFragment.newInstance(question)
                        instance.qIndex = current
                        fragmentList.add(instance)
                        pIndex++
                        multipleSheet(question, answerQuestions, lastQuestionKey, pIndex, instance)
                    }
                    SingleChoice -> {
                        val instance = ExerciseSingleChoiceFragment.newInstance(question)
                        instance.qIndex = current
                        fragmentList.add(instance)
                        pIndex++
                        singleSheet(question, answerQuestions, lastQuestionKey, pIndex)
                    }
                }
            }
            val answer = Answer(
                if ("1" != item.isSubtitle) {
                    item.groupName
                } else {
                    ""
                }, answerQuestions
            )
            answerList.add(answer)
        }
        tv_total.text = "/${current}"
        view_pager.offscreenPageLimit = 100
        val oAdapter = ExercisePagerV4Adapter(supportFragmentManager, fragmentList)
        view_pager.adapter = oAdapter
        startTime()
        setTLTileView(0)
    }

    private fun singleSheet(question: ExerciseFrameItem, answerQuestions: MutableList<AnswerQuestion>, lastQuestionKey: String, pIndex: Int) {
        //如果当前题目中没有小题（单选题,主观题），直接使用当前数据
        ++current
        val answerQuestionItem = AnswerQuestion()
        answerQuestionItem.sort = question.questionInfo?.sort ?: ""
        answerQuestionItem.type = question.questionInfo?.questionType ?: ""
        answerQuestionItem.questionKey = question.questionInfo?.questionKey ?: ""
        answerQuestionItem.evalMode = "0"
        answerQuestionItem.haveDone = false
        answerQuestionItem.qPosition = fragmentList.size - 1
        answerQuestionItem.mPosition = 0
        answerQuestionItem.parentType = question.questionInfo?.questionType ?: ""
        answerQuestions.add(answerQuestionItem)
        if (question.questionInfo?.questionKey == lastQuestionKey) {
            currentIndex = pIndex
            Handler().postDelayed({
                view_pager.currentItem = pIndex
            }, 1000)
        }
    }

    private fun multipleSheet(question: ExerciseFrameItem, answerQuestions: MutableList<AnswerQuestion>, lastQuestionKey: String, pIndex: Int, instance: ExerciseDetailChoiceFragment) {
        //如果当前题目中存在小题（材料题、多选题，填空题都算做一个题），则解析该题结构
        var mPosition = 0
        question.questionlist!!.forEachIndexed { infoIndex, info ->//body->questionlist->questionlist->questionlist
            ++current
            val answerQuestionItem = AnswerQuestion()
            answerQuestionItem.sort = info.questionSort ?: ""
            answerQuestionItem.type = info.questionType ?: ""
            answerQuestionItem.questionKey = info.questionKey
            answerQuestionItem.evalMode = "0"
            answerQuestionItem.haveDone = false
            answerQuestionItem.qPosition = fragmentList.size - 1
            answerQuestionItem.mPosition = mPosition
            answerQuestionItem.parentType = question.questionType ?: ""
            answerQuestions.add(answerQuestionItem)
            mPosition++
            if (info.questionKey == lastQuestionKey) {
                currentIndex = pIndex
                Handler().postDelayed({
                    view_pager.currentItem = pIndex
                    instance.jumpIndex(infoIndex)
                }, 1000)
            }
        }
    }

//    fun setIndex(index: Int) {
//        tv_index.text = "${index}"
//    }

    fun setIndex(index: String) {
        tv_index.text = index
    }

    private fun startTime() {
//        ch_time.base = SystemClock.elapsedRealtime()
        ch_time.base = SystemClock.elapsedRealtime() - saveTime * 1000
        ch_time.start()
    }

    override fun onBackPressed() {
        if (fl_answer_card.visibility == View.VISIBLE) {
            ic_answers.performClick()
        } else {
            saOption("返回")
            AlertDialog.Builder(this).setMessage("您确定要退出当前试卷吗？").setPositiveButton("确定") { dialogInterface, i ->
                dialogInterface.dismiss()
                save()
            }.setNegativeButton("取消") { dialogInterface, i ->
                dialogInterface.dismiss()
            }.show()
        }
    }

    /**
     * 当前页面加载答题卡，显示隐藏控制答题卡页面
     */
    private fun initAnswerCard() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_answer_card, answerCardFragment)
        transaction.show(answerCardFragment).commit()
    }

    /**
     * 答题卡页面提交答案
     */
    fun submit() {
        if (!isDone) {
            Toast.makeText(this, "不能交白卷喔~", Toast.LENGTH_LONG).show()
            return
        }
        val answers = mutableListOf<AnswerQuestion>()
        answerList.forEach {
            answers.addAll(it.qList)
        }
        checkAnswers(answers) {
            uploadImages(answers) {
                val submit = AnswerSubmit()
                submit.answerlist = answers as ArrayList<AnswerQuestion>
                submit.time = getTime(ch_time.text.toString())
                submit.catalogkey = catKey
                submit.source = source
                if (sourcePaperKey.isNullOrEmpty()) {
                    submit.paperkey = practisekey
                } else {
                    //虚拟试卷时用的虚拟key获取的试卷结构，提交时需要把真正的key传入并把虚拟key传给sourcePaperKey
                    submit.paperkey = paperKey
                    submit.sourcePaperKey = sourcePaperKey
                }
                //网课跳进做题时需要传当前网课 netCourseKey 和小节 netCourseVideoKey
                submit.netCourseKey = netCourseKey
                submit.practicekey = netCourseKey
                submit.netCourseVideoKey = netCourseVideoKey
                submit.netCourseResourceKey = netCourseResourceKey
                submit.submitType = "1"
                submit.userPractiseKey = ""
                presenter.submitType(submit, typeName) {
                    startActivity<ReportActivity>(
                        ReportActivity.DATA to it,
                        ReportActivity.TIME to ch_time.text.toString(),
                        ReportActivity.KEY to practisekey,
                        ReportActivity.NAME to paperName,
                        ReportActivity.CAT_KEY to catKey,
                        ReportActivity.EX_TYPE to exType,
                        ReportActivity.TYPE to typeName,
                        ReportActivity.EVAL_STATE to if (evalState) "0" else "1"
                    )
                    finish()
                }
            }
        }
    }

    /**
     * 退出保存试卷
     */
    fun save() {
        if (!isDone) {
            super.onBackPressed()
            return
        }
        val answers = mutableListOf<AnswerQuestion>()
        answerList.forEach { answer ->
            answer.qList.forEach { qItem ->
                if (WritingAndTranslation != qItem.type) {
                    answers.add(qItem)
                }
            }
        }
        val submit = AnswerSubmit()
        submit.answerlist = answers as ArrayList<AnswerQuestion>
        submit.time = getTime(ch_time.text.toString())
        submit.catalogkey = catKey
        submit.source = source
        if (sourcePaperKey.isNullOrEmpty()) {
            submit.paperkey = practisekey
        } else {
            //虚拟试卷时用的虚拟key获取的试卷结构，提交时需要把真正的key传入并把虚拟key传给sourcePaperKey
            submit.paperkey = paperKey
            submit.sourcePaperKey = sourcePaperKey
        }
        //网课跳进做题时需要传当前网课 netCourseKey 和小节 netCourseVideoKey
        submit.netCourseKey = netCourseKey
        submit.netCourseVideoKey = netCourseVideoKey
        submit.netCourseResourceKey = netCourseResourceKey
        submit.submitType = "0"
        submit.lastQuestionKey = getQuestionKey(currentIndex)
        submit.userPractiseKey = userPractiseKey
        presenter.submitType(submit, typeName) {
            super.onBackPressed()
        }
    }

    private fun getQuestionKey(index: Int): String {
        return when (val fragment = fragmentList[index]) {
            is ExerciseDetailTitleFragment -> {
                getQuestionKey(index + 1)
            }
            is ExerciseDetailTranslateAndWritingFragment -> {
                fragment.questionKey
            }
            is ExerciseDetailChoiceFragment -> {
                fragment.questionKey
            }
            is ExerciseSingleChoiceFragment -> {
                fragment.questionKey
            }
            else -> {
                ""
            }
        }
    }

    private fun checkAnswers(answerList: List<AnswerQuestion>, action: () -> Unit) {
        var allDone = true
        kotlin.run breaking@{
            answerList.forEach {
                if (!it.haveDone) {
                    allDone = false
                    return@breaking
                }
            }
        }
        when {
            allDone -> {
                action()
            }
//            exType == EX_TYPE_PG -> {
//                //过级包类型，必须全部作答完毕
//                toast("请将全部题目作答完成再交卷")
//            }
            else -> {
                AlertDialog.Builder(this).setMessage("存在未作答题目，确定要交卷吗？").setPositiveButton("交卷") { _, _ ->
                    action()
                }.setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
            }
        }
    }

    //   是否人工批改
    private var evalState = false

    private fun uploadImages(answers: List<AnswerQuestion>, onNext: () -> Unit) {
        var upSize = 0
        var httpSize = 0
        fragmentList.forEach { fragment ->
            if (fragment is ExerciseDetailTranslateAndWritingFragment && fragment.imgPaths.isNotEmpty() && fragment.haveDown) {
                evalState = true
//                val imgPaths = mutableListOf<File>()
//                val imgKes = mutableListOf<String>()
//                fragment.imgPaths.forEach { item ->
//                    if (item.isLocal) {
//                        item.file?.let { imgPaths.add(it) }
//                    } else {
//                        imgKes.add(item.key)
//                    }
//                }
//                if (imgPaths.isNotEmpty()) {
                upSize++
                presenter.upload("6", fragment.imgPaths, {
                    toast("图片上传失败,$it")
                }) {
                    httpSize++
                    kotlin.run breaking@{
                        answers.forEach { answer ->
                            if (answer.questionKey == fragment.itemData?.questionInfo?.questionKey) {
                                answer.imgs.clear()
                                answer.imgs.addAll(it)
                                answer.evalMode = "1"
                                return@breaking
                            }
                        }
                    }
                    if (httpSize == upSize) {
                        onNext()
                    }
                }
//                } else {
//                    kotlin.run breaking@{
//                        answers.forEach { answer ->
//                            if (answer.questionKey == fragment.itemData?.questionInfo?.questionKey) {
//                                answer.imgs.clear()
//                                answer.imgs.addAll(imgKes)
//                                answer.evalMode = "1"
//                                return@breaking
//                            }
//                        }
//                    }
//                }
            }
        }
        if (upSize == 0) {
            onNext()
        }
    }

    /**
     *获得做题时间00：00 转为数字秒
     */
    fun getTime(timeStr: String): String {
        val timeArray = timeStr.split(":")
        return when (timeArray.size) {
            1 -> timeArray[0]
            2 -> (timeArray[0].toInt() * 60 + timeArray[1].toInt()).toString()
            3 -> (timeArray[0].toInt() * 60 * 60 + timeArray[1].toInt() * 60 + timeArray[2].toInt()).toString()
            else -> ""
        }
    }

    /**
     * 答题卡页面点击题号跳转到对应页面
     */
    fun positionQuestion(qPosition: Int, mPosition: Int) {
        ic_answers.performClick()
        view_pager.currentItem = qPosition
        when (val fragment = fragmentList[qPosition]) {
            is ExerciseDetailChoiceFragment -> {
                fragment.jumpIndex(mPosition)
            }
        }
    }

    /**
     * 非最后一题，作答后 跳转下一题
     */
    fun goNext() {
        when (val fragment = fragmentList[currentIndex]) {
            is ExerciseDetailChoiceFragment -> {
                fragment.musicStop()
            }
            is ExerciseSingleChoiceFragment -> {
                fragment.musicStop()
            }
        }
        if (view_pager.currentItem == fragmentList.size - 1) {
            //是最后一题，则显示答题卡，非最后一题，则跳转到下一题
            ic_answers.performClick()
        } else {
            currentIndex += 1
            view_pager.currentItem = currentIndex
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = fragmentList[currentIndex]
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            //主观题购买成功
            if (fragment is ExerciseDetailTranslateAndWritingFragment) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        } else if ((requestCode == 257 || requestCode == 256) && resultCode == Activity.RESULT_OK) {
            //选择图片批改方式
            ImageSeletedUtil.onActivityResult(this, requestCode, resultCode, data) {
                it?.let { path ->
                    if (fragment is ExerciseDetailTranslateAndWritingFragment) {
                        fragment.updateImg(path)
                    }
                }
            }
        }
    }

    fun saProperty(): JSONObject {
        val property = JSONObject()
        property.put("test_paper_id", practisekey)
        property.put("paper_name", paperName)
        property.put("section", gradeName)
        property.put("is_there_a_score", "1" == scoreSwitch)
        property.put("number_of_topics", current)
        property.put("test_paper_form", if ("2" == issubtitle) "包含大题" else "仅包含小题")
        property.put("presentation_form_paper", TypeUtil.getType(typeName))
        return property
    }

    fun startSa() {
        try {
            val property = saProperty()
            SensorsDataAPI.sharedInstance().track("start_paper", property)
        } catch (e: Exception) {
        }
    }

    /**
     * id:小题id
     * type:小题类型
     * answer：用户答案
     */
    fun saPaperAnswer(answerQuestion: AnswerQuestion) {
        try {
            val property = saProperty()
            property.put("title_id", answerQuestion.questionKey)
            property.put("topic_type", TypeUtil.getQType(answerQuestion.parentType))
            property.put("title_number", answerQuestion.sort)
            if ("1" == answerQuestion.type) {
                property.put("user_answers", answerQuestion.answers[0].answer)
            }
            SensorsDataAPI.sharedInstance().track("choose_option", property)
        } catch (e: Exception) {
        }
    }

    fun saOption(name: String) {
        try {
            val property = saProperty()
            property.put("button_name", name)
            SensorsDataAPI.sharedInstance().track("submit_order", property)
        } catch (e: Exception) {
        }
    }

    private fun postLockPaper(paperKey: String) {
        val map = HashMap<String, String>()
        map["key"] = paperKey
        presenter.postLockPaper(map) {
        }
        Handler().postDelayed({
            runOnUiThread {
                postLockPaper(paperKey)
            }
        }, 60000)
    }
}
