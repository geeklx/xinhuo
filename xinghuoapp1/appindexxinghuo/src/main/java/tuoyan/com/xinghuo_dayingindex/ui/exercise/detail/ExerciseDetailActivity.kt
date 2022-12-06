//package tuoyan.com.xinghuo_daying.ui.exercise.detail
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.content.Intent
//import android.os.Handler
//import android.os.SystemClock
//import android.util.Log
//import android.view.View
//import androidx.viewpager.widget.ViewPager
//import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
//import kotlinx.android.synthetic.main.activity_exercise_detail.*
//import org.jetbrains.anko.ctx
//import org.jetbrains.anko.startActivity
//import org.jetbrains.anko.support.v4.onPageChangeListener
//import org.jetbrains.anko.toast
//import org.json.JSONObject
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
//import tuoyan.com.xinghuo_dayingindex.bean.*
//import tuoyan.com.xinghuo_dayingindex.ui.dialog.SelectedDialog
//import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerAdapter
//import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
//import tuoyan.com.xinghuo_dayingindex.ui.practice.special.SpCatalogActivity
//import tuoyan.com.xinghuo_dayingindex.ui.practice.special.SpecialDataManager
//import tuoyan.com.xinghuo_dayingindex.ui.practice.special.SpecialDataManager.questionList
//import tuoyan.com.xinghuo_dayingindex.utlis.Base64Utils
//import tuoyan.com.xinghuo_dayingindex.utlis.ImageSeletedUtil
//import tuoyan.com.xinghuo_dayingindex.utlis.TypeUtil
//import java.io.BufferedReader
//import java.io.File
//import java.io.InputStream
//import java.io.InputStreamReader
//
//
//class ExerciseDetailActivity : LifeActivity<ExerciseDetailPresenter>() {
//    override val presenter: ExerciseDetailPresenter
//        get() = ExerciseDetailPresenter(this)
//    override val layoutResId: Int
//        get() = R.layout.activity_exercise_detail
//
//    val practisekey by lazy { intent.getStringExtra(KEY) }
//    val catKey by lazy { intent.getStringExtra(CAT_KEY) ?: "" }
//    val praKey by lazy { intent.getStringExtra(PRA_KEY) ?: "" }
//
//    val paperName by lazy { intent.getStringExtra(NAME) }
//    val exType by lazy { intent.getIntExtra(EX_TYPE, EX_TYPE_0) }
//    val exItem by lazy { intent.getSerializableExtra(EX_ITEM) as ExerciseFrameItem? }
//    val exItemIndex by lazy { intent.getIntExtra(EX_ITEM_INDEX, 0) }
//
//    val netCourseKey by lazy { intent.getStringExtra(NET_COURSE_KEY) ?: "" }//网课跳入测评时，传入的网课key和小节key，用于统计当前测评是否做完，数据展示
//    val netCourseVideoKey by lazy { intent.getStringExtra(NET_COURSE_VIDEO_KEY) ?: "" }//网课跳入测评时，传入的网课key和小节key，用于统计当前测评是否做完，数据展示
//    val netCourseResourceKey by lazy { intent.getStringExtra(NET_COURSE_RESOURCE_KEY) ?: "" }//网课跳入测评时，传入的网课key和小节key，用于统计当前测评是否做完，数据展示
//    val source by lazy { intent.getStringExtra(SOURCE) ?: "" }//预习列表中的 试卷、测评，调用对应的提交接口是传source=7,其他情况不用传
//    val typeName by lazy { intent.getStringExtra(TYPE) ?: "普通试卷" }//试卷类型
//    var needUpLoad = false
//
//    val selectDialog by lazy {
//        //TODO 主观题上传图片
//        SelectedDialog("拍照", "从相册选择", this) {
//            ImageSeletedUtil.phoneClick(it, this, false)
//        }
//    }
//    var currentIndex = 0
//    private var fragmentList = ArrayList<ExerciseDetailFragment>()
//    val imageSortList = mutableListOf<String>() //TODO 记录每一张图片对应主观题的索引，用于上传后交卷
//    val imageFileList = mutableListOf<File>() //TODO 试卷中所有图片的地址，用于批量上传
//    val sensorsData by lazy { SensorsExercise() }
//    companion object {
//        const val KEY = "key"
//        const val NAME = "name"
//
//        const val CAT_KEY = "cat_key" //TODO 某些做题类型下 需要传相关的key
//        const val PRA_KEY = "pra_key"
//
//        const val EX_TYPE = "ex_type"//做题类型
//        const val EX_TYPE_0 = 0 //正常做题（最普通的类型）
//        const val EX_TYPE_SP = 1 //专项练习类型
//        const val EX_TYPE_PG = 2 //过级包类型（测评）
//        const val EX_TYPE_WORK = 3 //网课课后作业
//
//        const val NEED_UP_LOAD = "needUpLoad" //过级包测评 需要上传照片提供阅卷的情况
//
//        const val EX_ITEM = "ex_item"//专项练习类型时，直接传题目信息，不用调接口
//
//        //        const val EX_ITEM_LIST = "ex_item_list"//专项练习类型时，直接传题目信息，不用调接口,其中主观题类型（翻译、写作），传题目列表，不单独处理交卷，选择“我答对了”“我打错了”即处理交卷
//        const val EX_ITEM_INDEX = "ex_item_index"//专项练习类型时，主观题类型,需要第一次默认显示的题目索引
//        const val NET_COURSE_KEY = "net_course_key "//网课中有测评,网课作业跳入做题时，需要传网课key 和小节 key
//        const val NET_COURSE_VIDEO_KEY = "net_course_video_key"//网课中有测评，网课作业跳入做题时，需要传网课key 和小节key
//        const val NET_COURSE_RESOURCE_KEY = "netCourseResourceKey"//网课中有测评，网课作业跳入做题时，需要传网课key 和小节key
//        const val SOURCE = "source"//预习列表中的 试卷、测评，调用对应的提交接口是传source=5 课后作业 source=6  测评小节 source=7其他情况不用传
//        const val TYPE = "typeName"
//    }
//
//    var haveDone = false
//    var remarks = ""
//    var issubtitle = ""//是否有大题 2：有大题
//    var paperKey = ""//虚拟试卷时返回真正的试卷可以
//    var sourcePaperKey = ""//虚拟试卷时返回
//    override fun initData() {
//        tv_paper_name.text = paperName
//        initHtmlStr()
//        view_pager.offscreenPageLimit = 10
//        if (exType == EX_TYPE_SP) {
//            if (exItem != null) {
//                initSpExerciseData(exItem!!)
//            } else if (questionList.isNotEmpty()) {
//                rl_title.visibility = View.VISIBLE
//                ic_answers.visibility = View.GONE
//                ch_time.visibility = View.GONE
//                initSpExerciseDataList(questionList)
//            }
//        } else {
//            iv_catalog.visibility = View.GONE
//            presenter.getExerciseFrame(practisekey, "0", catKey) {
//                paperKey = it.paperKey ?: ""
//                sourcePaperKey = it.sourcePaperKey ?: ""
//                remarks = it.remarks ?: ""
//                issubtitle = it.issubtitle ?: ""
//                scoreSwitch = it.scoreSwitch ?: ""
//                gradeName = it.gradeName ?: ""
//                try {
//                    it.questionlist?.let { list -> if (list.isNotEmpty()) initExerciseData(list) else toast("数据异常~请您稍候再试~") }
//                } catch (e: Exception) {
//                    Log.e("asdas", e.toString())
//                }
//                sensorsData.test_paper_id = practisekey ?: ""
//                sensorsData.paper_name = paperName ?: ""
//                sensorsData.section = it.gradeName ?: ""
//                sensorsData.is_there_a_score = "1" == scoreSwitch
//                sensorsData.number_of_topics = totalNum
//                sensorsData.test_paper_form = if ("2" == issubtitle) "包含大题" else "仅包含小题"
//                sensorsData.presentation_form_paper = typeName ?: ""
//                startSa()
//            }
//        }
//        initAnswerCard()
//    }
//
//    override fun onBackPressed() {
//        if (fl_answer_card.visibility == View.VISIBLE) {
//            ic_answers.performClick()
//        } else {
//            saOption("返回")
//            AlertDialog.Builder(ctx).setMessage("您确定要退出当前试卷吗？")
//                .setPositiveButton("确定") { dialogInterface, i ->
//                    super.onBackPressed()
//                    dialogInterface.dismiss()
//                }.setNegativeButton("取消") { dialogInterface, i ->
//                    dialogInterface.dismiss()
//                }.show()
//        }
//    }
//
//    override fun handleEvent() {
//        ic_back.setOnClickListener {
//            if (fl_answer_card.visibility == View.VISIBLE) {
//                ic_answers.performClick()
//            } else {
//                onBackPressed()
//            }
//        }
//        view_pager.onPageChangeListener {
//            var flag = false
//
//            onPageScrollStateChanged {
//                when (it) {
//                    ViewPager.SCROLL_STATE_DRAGGING -> flag = true
//                    ViewPager.SCROLL_STATE_SETTLING -> flag = false
//                    ViewPager.SCROLL_STATE_IDLE -> {
//                        if (flag && view_pager.currentItem == fragmentList.size - 1) {//最后一页继续滑动，弹出答题卡
//                            ic_answers.performClick()
//                        }
//                    }
//                }
//            }
//
//            onPageSelected {
//                currentIndex = it
//                try {
//                    fragmentList[it - 1].musicStop()
//                } catch (e: Exception) {
//                }
//
//                try {
//                    fragmentList[it + 1].musicStop()
//                } catch (e: Exception) {
//                }
//
//                if (fragmentList[it].isNode()) {
//                    rl_title.visibility = View.GONE
//                } else {
//                    fragmentList[it].getAudioUrl()
//                    rl_title.visibility = View.VISIBLE
//                }
//                setQIndex((fragmentList[it].qIndex + fragmentList[it].answerIndex + 1).toString())
//
//                iv_collection.isSelected =
//                    (!fragmentList[it].isNode()) && fragmentList[it].getCollection() == "1"
//                iv_collection.visibility = if (fragmentList[it].isNode()) {
//                    View.GONE
//                } else {
//                    View.VISIBLE
//                }
//            }
//        }
//        ic_answers.setOnClickListener {
//            saOption("去答题卡")
//            try {
//                fl_answer_card.visibility = if (fl_answer_card.visibility == View.VISIBLE) {
//                    if (!fragmentList[currentIndex].isNode()) {
//                        iv_collection.visibility = View.VISIBLE
//                    }
//                    ch_time.visibility = View.VISIBLE
//                    if (exType == EX_TYPE_SP) {
//                        iv_catalog.visibility = View.VISIBLE
//                    } else {
//                        iv_catalog.visibility = View.GONE
//                    }
//                    ic_answers.visibility = View.VISIBLE
//                    tv_title.visibility = View.GONE
//                    View.GONE
//                } else {
//                    answerCardFragment.showList()
//                    iv_collection.visibility = View.GONE
//                    ch_time.visibility = View.GONE
//                    iv_catalog.visibility = View.GONE
//                    ic_answers.visibility = View.GONE
//                    tv_title.visibility = View.VISIBLE
//                    View.VISIBLE
//                }
//            } catch (e: Exception) {
//            }
//        }
//        iv_catalog.setOnClickListener {
//            var intent = Intent()
//            intent.setClass(ctx, SpCatalogActivity::class.java)
//            startActivityForResult(intent, 100)
//        }
//
//        iv_collection.setOnClickListener {
//            saOption("收藏")
//            if ((!fragmentList[currentIndex].isNode()) && fragmentList[currentIndex].getCollection() == "0") {
//                presenter.addCollection(
//                    mutableMapOf(
//                        "targetkey" to (fragmentList[currentIndex].item?.questionKey
//                            ?: ""),
//                        "type" to "4"
//                    )
//                ) {
//                    iv_collection.isSelected = true
//                    fragmentList[currentIndex].setCollection("1")
//                }
//            } else if ((!fragmentList[currentIndex].isNode()) && fragmentList[currentIndex].getCollection() == "1") {
//                presenter.deleteCollection(fragmentList[currentIndex].item?.questionKey ?: "") {
//                    iv_collection.isSelected = false
//                    fragmentList[currentIndex].setCollection("0")
//                }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
//            fragmentList[currentIndex].onActivityResult(requestCode, resultCode, data)
//        } else if (resultCode == 200 && data != null) {//TODO 点击未做的题目的回调，打开对应的题目
//            var item = data.getSerializableExtra("data") as ExerciseFrameItem
//            startActivity<ExerciseDetailActivity>(KEY to practisekey, EX_ITEM to item, NAME to paperName, EX_TYPE to EX_TYPE_SP)
//            finish()
//        } else if (resultCode == 201 && data != null) {//TODO 点击已交卷的题目的回调，打开对应的题目的练习报告
//            var item = data.getSerializableExtra("data") as ExerciseFrameItem
//            presenter.getReport(practisekey, item.questionKey, item.userPracticeKey ?: "") {
//                it.userpractisekey = item.userPracticeKey ?: ""
//                startActivity<ReportActivity>(
//                    ReportActivity.DATA to it,
//                    ReportActivity.TIME to "",
//                    ReportActivity.KEY to practisekey,
//                    ReportActivity.NAME to paperName,
//                    ReportActivity.EX_TYPE to exType,
//                    ReportActivity.CAT_KEY to catKey,
//                    ReportActivity.PRA_KEY to praKey,
//                    ReportActivity.SP_Q_KEY to item.questionKey,
//                    ReportActivity.SP_G_NAME to item.groupName
//                )
//                finish()
//            }
//        } else if (resultCode == 202 && data != null) {
//            var position = data.getIntExtra("position", 0)
//            view_pager.currentItem = position
//        } else {//TODO 选择图片后返回
//            ImageSeletedUtil.onActivityResult(this, requestCode, resultCode, data) {
//                it?.let {
//                    fragmentList[currentIndex].addImages(Base64Utils.imageToBase64(it), it)
//                }
//            }
//        }
//
//    }
//
//    fun setQIndex(index: String) {
//        tv_index.text = index
//    }
//
//    /**
//     * 构造显示题目及答题卡的list
//     * 普通做题模块下调用
//     */
//    var answerList = mutableListOf<Answer>()
//    var current = 0 //存储当前题目的题号
//    var totalNum = 0;//小题数量
//    var scoreSwitch = "0";//
//    var gradeName = "";//
//    private fun initExerciseData(dataList: List<ExerciseFrameItem>) {
//        totalNum = 0
//        for (index in dataList.indices) { // 大题分类的结构
//            var item: ExerciseFrameItem = dataList[index]
//            var exerItem = ExerciseFrameItem(
//                true,
//                if (index == 0) paperName else "",
//                item.groupName,
//                if (item.paperExplain != null) {
//                    item.paperExplain
//                } else {
//                    ""
//                },
//                if (index == 0) remarks else ""
//            )
//            exerItem.isSubtitle = item.isSubtitle
//            fragmentList.add(ExerciseDetailFragment.newInstance(exerItem))
//
//            var answerQuestions = mutableListOf<AnswerQuestion>() //当前题型下的题目列表
//            item.questionlist?.forEach { exerciseFrameItem ->
//                //题目的结构
//                var instance = ExerciseDetailFragment.newInstance(exerciseFrameItem)
//                instance.qIndex = current
//                fragmentList.add(instance)
//                if (exerciseFrameItem.questionlist != null && exerciseFrameItem.questionlist!!.isNotEmpty()) {
//                    //如果当前题目中存在小题（材料题、多选题，填空题都算做一个题），则解析该题结构
//                    var mPosition = 0
//                    exerciseFrameItem.questionlist?.forEach { frameItem ->
//                        var tempItem = AnswerQuestion()
//                        tempItem.index = ++current
//                        tempItem.qPosition = fragmentList.size - 1
//                        tempItem.mPosition = mPosition
//                        tempItem.haveDone = false
//                        tempItem.sort = frameItem.questionSort ?: ""
//                        tempItem.type = frameItem.questionType ?: ""
//                        tempItem.questionKey = frameItem.questionKey
//                        tempItem.parentType = exerciseFrameItem.questionType?:""
//                        answerQuestions.add(tempItem)
//                        mPosition++
//                    }
//                } else { //如果当前题目中没有小题（单选题,主观题），直接使用当前数据
//                    var tempItem = AnswerQuestion()
//                    tempItem.index = ++current
//                    tempItem.qPosition = fragmentList.size - 1
//                    tempItem.mPosition = 0
//                    tempItem.haveDone = false
//                    tempItem.sort = exerciseFrameItem.questionSort ?: ""
//                    tempItem.type = exerciseFrameItem.questionType ?: ""
//                    tempItem.questionKey = exerciseFrameItem.questionKey
//                    tempItem.parentType = exerciseFrameItem.questionType?:""
//                    answerQuestions.add(tempItem)
//                }
//            }
//            var title = if ("1" != item!!.isSubtitle) {
//                item.groupName
//            } else {
//                ""
//            }
//            var answer = Answer(title, answerQuestions)
//            totalNum += answerQuestions.size
//            answerList.add(answer)
//        }
//
//        tv_total.text = "/" + current.toString()
//        var oAdapter = ExercisePagerAdapter(supportFragmentManager, fragmentList)
//        view_pager.adapter = oAdapter
//        startTime()
//    }
//
//    /**
//     * 专项练习模块下的数据构建
//     */
//    private fun initSpExerciseData(question: ExerciseFrameItem) {
//        fragmentList.add(
//            ExerciseDetailFragment.newInstance(
//                ExerciseFrameItem(
//                    true,
//                    paperName,
//                    question.groupName,
//                    "",
//                    ""
//                )
//            )
//        )
//
//        var answerQuestions = mutableListOf<AnswerQuestion>() //当前题型下的题目列表
//        var instance = ExerciseDetailFragment.newInstance(question)
//        instance.qIndex = current
//        fragmentList.add(instance)
//        if (question.questionlist != null && question.questionlist!!.isNotEmpty()) { //如果当前题目中存在小题（材料题、多选题，填空题都算做一个题），则解析该题结构
//            var mPosition = 0
//            question.questionlist?.forEach {
//                var tempItem = AnswerQuestion()
//                tempItem.index = ++current
//                tempItem.qPosition = fragmentList.size - 1
//                tempItem.mPosition = mPosition
//                tempItem.haveDone = false
//                tempItem.sort = it.questionSort ?: ""
//                tempItem.type = it.questionType ?: ""
//                tempItem.questionKey = it.questionKey
//                answerQuestions.add(tempItem)
//                mPosition++
//            }
//        } else { //如果当前题目中没有小题（单选题），直接使用当前数据
//            var tempItem = AnswerQuestion()
//            tempItem.index = ++current
//            tempItem.qPosition = fragmentList.size - 1
//            tempItem.mPosition = 0
//            tempItem.haveDone = false
//            tempItem.sort = question.questionSort ?: ""
//            tempItem.type = question.questionType ?: ""
//            tempItem.questionKey = question.questionKey
//            answerQuestions.add(tempItem)
//        }
//        var answer = Answer(question.groupName, answerQuestions)
//        answerList.add(answer)
//
//        tv_total.text = "/" + current.toString()
//        var oAdapter = ExercisePagerAdapter(supportFragmentManager, fragmentList)
//        view_pager.adapter = oAdapter
//        startTime()
//    }
//
//    /**
//     * 专项练习-主观题 下的数据构建
//     */
//    private fun initSpExerciseDataList(questionList: List<ExerciseFrameItem>) {
//        questionList.forEach {
//            var instance = ExerciseDetailFragment.newInstance(it)
//            instance.qIndex = (it.questionSort ?: "1").toInt() - 1
//            fragmentList.add(instance)
//        }
//
//        tv_total.text = "/${questionList.size}"
//        var oAdapter = ExercisePagerAdapter(supportFragmentManager, fragmentList)
//        view_pager.adapter = oAdapter
//        Handler().postDelayed({
//            view_pager.currentItem = exItemIndex
//        }, 1000)
//    }
//
//    private fun startTime() {
//        ch_time.base = SystemClock.elapsedRealtime()
//        ch_time.start()
//    }
//
//    var htmlStr: StringBuilder = StringBuilder()
//    private fun initHtmlStr() {
//        var ips: InputStream = resources.assets.open("index.html")
//        var reder = BufferedReader(InputStreamReader(ips))
//        var str = reder.readLine()
//
//        while (str != null) {
//            htmlStr.append(str + "\n")
//            str = reder.readLine()
//        }
//    }
//
//
//    private var answerCardFragment = AnswerCardFragment()
//
//    private fun initAnswerCard() {
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.add(R.id.fl_answer_card, answerCardFragment)
//        transaction.show(answerCardFragment).commit()
//    }
//
//    /**
//     * 答题卡上点击题号后，切换
//     */
//    fun positionQuestion(qPosition: Int, mPosition: Int) {
//        ic_answers.performClick()
//        view_pager.currentItem = qPosition
//        fragmentList[qPosition].jumpIndex(mPosition)
//    }
//
//    /**
//     * 非最后一题，作答后 跳转下一题
//     */
//    fun goNext() {
//        if (view_pager.currentItem == fragmentList.size - 1) {
//            //是最后一题，则显示答题卡，非最后一题，则跳转到下一题
//            ic_answers.performClick()
//            fragmentList[fragmentList.size - 1].musicStop()
//        } else {
//            var index = view_pager.currentItem + 1
//            view_pager.currentItem = index
//        }
//    }
//
//    /**
//     * 跳转前一题
//     */
//    fun goBefore() {
//        if (view_pager.currentItem != 0) {
//            var index = view_pager.currentItem - 1
//            view_pager.currentItem = index
//        }
//    }
//
//    private fun uploadImages(answers: List<AnswerQuestion>, onNext: () -> Unit) {
//        var upSize = 0
//        var httpSize = 0
//        fragmentList.forEach {
//            var f = it
//            if (f.item?.questionType == "6" && it.imgPaths.isNotEmpty()) {
//                upSize++
//                presenter.upload("6", it.imgPaths, {
//                    toast("图片上传失败,$it")
//                }) {
//                    httpSize++
//                    kotlin.run breaking@{
//                        answers.forEach { answers ->
//                            if (answers.questionKey == f.item?.questionInfo?.questionKey) {
//                                answers.imgs.addAll(it)
//                                answers.evalMode = "1"
//                                needUpLoad = true
//                                return@breaking
//                            }
//                        }
//                    }
//                    if (httpSize == upSize) {
//                        onNext()
//                    }
////                    for (i in 0 until it.size) {
////                        var sort = imageSortList[i]
////                        var img = it[i]
////                        answers.forEach {
////                            if (it.sort == sort) {
////                                it.imgs.add(img)
////                                it.evalMode = "1"
////                                needUpLoad = true
////                            }
////                        }
////                    }
////                    onNext()
//                }
//            }
//        }
//        if (upSize == 0) {
//            onNext()
//        }
////        imageFileList.clear()
////        imageSortList.clear()
////        fragmentList.forEach {
////            var f = it
////            if (it.imgPaths.isNotEmpty()) {
////                it.imgPaths.forEach {
////                    imageFileList.add(File(it["str"]))
////                    imageSortList.add(
////                        (if (f.item?.questionType == "6") f.item?.questionSort
////                            ?: "" else f.item?.questionlist!![(it["index"]!!.toInt())].questionSort
////                            ?: "")
////                    )
////                }
////            }
////        }
////        if (imageFileList.isEmpty()) {
////            onNext()
////        } else {
////            presenter.upload("6", imageFileList, {
////                toast("图片上传失败,$it")
////            }) {
////                for (i in 0 until it.size) {
////                    var sort = imageSortList[i]
////                    var img = it[i]
////                    answers.forEach {
////                        if (it.sort == sort) {
////                            it.imgs.add(img)
////                            it.evalMode = "1"
////                            needUpLoad = true
////                        }
////                    }
////                }
////                onNext()
////            }
////        }
//    }
//
//    /**
//     * 交卷
//     */
//    fun submit() {
//        if (!haveDone) {
//            toast("不能交白卷喔~")
//            return
//        }
//        var answers = mutableListOf<AnswerQuestion>()
//        answerList.forEach {
//            answers.addAll(it.qList)
//        }
//
//        //检查是否已全部作答
//        checkAnswers(answers) {
//            uploadImages(answers) {
//                val submit = AnswerSubmit()
//                submit.answerlist = answers as ArrayList<AnswerQuestion>
//                submit.time = getTime(ch_time.text.toString())
//                submit.catalogkey = catKey
//                submit.practicekey = praKey
//                submit.source = source
//                submit.matpkey = if (exType == EX_TYPE_SP) exItem!!.questionKey ?: "" else ""
//                submit.groupkey = if (exType == EX_TYPE_SP) SpecialDataManager.groupKey else ""
//                if (sourcePaperKey.isNullOrEmpty()) {
//                    submit.paperkey = practisekey
//                } else {
//                    //虚拟试卷时用的虚拟key获取的试卷结构，提交时需要把真正的key传入并把虚拟key传给sourcePaperKey
//                    submit.paperkey = paperKey
//                    submit.sourcePaperKey = sourcePaperKey
//                }
//                //网课跳进做题时需要传当前网课 netCourseKey 和小节 netCourseVideoKey
//                submit.netCourseKey = netCourseKey
//                submit.netCourseVideoKey = netCourseVideoKey
//                submit.netCourseResourceKey = netCourseResourceKey
//                presenter.submit(submit, exType) {
//                    var mPaperKey = practisekey
//                    if (!sourcePaperKey.isNullOrEmpty()) {
//                        //sourcePaperKey为空，practisekey是真正的paperkey否则practisekey是虚拟key，真正的paperkey 为ExerciseDetailActivity.paperkey
//                        mPaperKey = paperKey
//                    }
//                    startActivity<ReportActivity>(
//                        ReportActivity.DATA to it,
//                        ReportActivity.TIME to ch_time.text.toString(),
//                        ReportActivity.KEY to mPaperKey,
//                        ReportActivity.NAME to /*if (exType == EX_TYPE_SP) exItem?.groupName else*/ paperName,
//                        ReportActivity.EX_TYPE to exType,
//                        ReportActivity.CAT_KEY to catKey,
//                        ReportActivity.PRA_KEY to praKey,
//                        ReportActivity.SP_Q_KEY to if (exType == EX_TYPE_SP) exItem!!.questionKey else "",
//                        ReportActivity.SP_G_NAME to if (exType == EX_TYPE_SP) exItem!!.groupName else "",
//                        ReportActivity.NEED_UP_LOAD to needUpLoad,
//                        ReportActivity.EVAL_STATE to if (needUpLoad) "0" else "1",
//                        "SensorsExercise" to sensorsData
//                    )
//                    finish()
//                }
//            }
//        }
//    }
//
//    /**
//     * 专项练习-主观题-单独处理的交卷
//     */
//    fun spSubmit(answers: List<AnswerQuestion>) {
//        var submit = AnswerSubmit()
//        submit.paperkey = practisekey
//        submit.answerlist = answers as ArrayList<AnswerQuestion>
//        submit.time = getTime(ch_time.text.toString())
//        submit.catalogkey = catKey
//        submit.practicekey = praKey
//        submit.source = source
//        submit.matpkey = if (exType == EX_TYPE_SP) if (exItem == null) fragmentList[view_pager.currentItem].item?.questionKey ?: "" else exItem!!.questionKey else ""
//        submit.groupkey = if (exType == EX_TYPE_SP) SpecialDataManager.groupKey else ""
//        submit.isTeacherEvaluate = if (exType == EX_TYPE_PG && needUpLoad) "1" else "0"
//
//        presenter.submit(submit, exType) {
//
//        }
//    }
//
//    private fun checkAnswers(answerList: List<AnswerQuestion>, action: () -> Unit) {
//        var allDone = true
//        answerList.forEach {
//            if (!it.haveDone) {
//                allDone = false
//                return@forEach
//            }
//        }
//
//        if (allDone) {
//            action()
//        } else if (exType == EX_TYPE_PG) {
//            toast("请将全部题目作答完成再交卷")
//        } else {
//            AlertDialog.Builder(ctx).setMessage("存在未作答题目，确定要交卷吗？").setPositiveButton("交卷") { _, _ ->
//                action()
//            }.setNegativeButton("取消") { dialog, _ ->
//                dialog.dismiss()
//            }.create().show()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (fragmentList.isNotEmpty()) {
//            fragmentList[view_pager.currentItem].musicStop()//结束当前页面时，调用结束当前题目音频的方法
//        }
//    }
//
//    fun getTime(timeStr: String): String {
//        var timeArray = timeStr.split(":")
//
//        return when (timeArray.size) {
//            1 -> timeArray[0]
//            2 -> (deleteChar(timeArray[0]) * 60 + deleteChar(timeArray[1])).toString()
//            3 -> (deleteChar(timeArray[0]) * 60 * 60 + deleteChar(timeArray[1]) * 60 + deleteChar(
//                timeArray[2]
//            )).toString()
//            else -> ""
//        }
//    }
//
//    /**
//     *华为nova 7  EMUI英文语言系统时间数字有特殊符号
//     * ‎‎‘‎\u200e’'\u200f'
//     */
//    private fun deleteChar(str: String): Int {
//        return str.replace("\u200e", "").replace("\u200f", "").replace(" ", "").toInt()
//    }
//
//    ///////////////////////////////////////////////专项练习//////////////////////////////////////////////////////////
////请求数据之后调用
//    fun saProperty(): JSONObject {
//        val property = JSONObject()
//        property.put("test_paper_id", practisekey)
//        property.put("paper_name", paperName)
//        property.put("section", gradeName)
//        property.put("is_there_a_score", "1"==scoreSwitch)
//        property.put("number_of_topics", totalNum)
//        property.put("test_paper_form", if ("2" == issubtitle) "包含大题" else "仅包含小题")
//        property.put("presentation_form_paper", typeName)
//        return property
//    }
//
//    fun startSa() {
//        try {
//            val property = saProperty()
//            SensorsDataAPI.sharedInstance().track("start_paper", property)
//        } catch (e: Exception) {
//        }
//    }
//
//    /**
//     * id:小题id
//     * type:小题类型
//     * answer：用户答案
//     */
//    fun saPaperAnswer(answerQuestion: AnswerQuestion) {
//        try {
//            val property = saProperty()
//            property.put("title_id", answerQuestion.questionKey)
//            property.put("topic_type", TypeUtil.getQType(answerQuestion.parentType))
//            property.put("title_number", answerQuestion.sort)
//            if ("1" == answerQuestion.type) {
//                property.put("user_answers", answerQuestion.answers[0].answer)
//            }
//            SensorsDataAPI.sharedInstance().track("choose_option", property)
//        } catch (e: Exception) {
//        }
//    }
//
//    fun saOption(name: String) {
//        try {
//            val property = saProperty()
//            property.put("button_name", name)
//            SensorsDataAPI.sharedInstance().track("submit_order", property)
//        } catch (e: Exception) {
//        }
//    }
//}
