package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.evaluation
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_entry.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.toast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.TYPE_TEST
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui.dialog.SelectedDialog
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailPresenter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.EvalCardActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.TeacherModifyDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.ImageSeletedUtil
import tuoyan.com.xinghuo_dayingindex.utlis.TypeUtil
import java.io.File

class EntryActivity : LifeActivity<ExerciseDetailPresenter>() {
    override val presenter = ExerciseDetailPresenter(this)
    override val layoutResId = R.layout.activity_entry
    private val tabMaxHeight by lazy { dip(550) }
    private val tabMinHeight by lazy { dip(50) }
    var exModelList = mutableListOf<ExerciseModel>()
    var exPagerModelList = mutableListOf<ExerciseModel>()

    private var currentData: ExerciseModel? = null

    val practisekey by lazy { intent.getStringExtra(KEY) ?: "" }
    val userPractiseKey by lazy { intent.getStringExtra(USER_PRACTISE_KEY) ?: "" }
    val catKey by lazy { intent.getStringExtra(CAT_KEY) ?: "" }
    val paperName by lazy { intent.getStringExtra(NAME) ?: "" }
    var needUpload: Boolean = false
    var evalState = "1"
    var exerItem = ExerciseModel()

    private var issubtitle = ""
    private var scoreSwitch = ""
    private var gradeName = ""
    private var typeName = "??????"

    private var lastCommitTime = 0L

    val selectDialog by lazy {
        //TODO ?????????????????????
        SelectedDialog("??????", "???????????????", this) {
            ImageSeletedUtil.phoneClick(it, this, false)
        }
    }

    companion object {
        const val KEY = "key"
        const val USER_PRACTISE_KEY = "USERPRACTISEKEY"
        const val NAME = "name"

        const val CAT_KEY = "cat_key" //TODO ????????????????????? ??????????????????key
        const val PRA_KEY = "pra_key"

        const val NEED_UP_LOAD = "needUpLoad" //??????????????? ???????????????????????????????????????

        private const val FAST_CLICK_DELAY_TIME = 2000L;
    }

    private val adapter by lazy {
        EntryModifyAdapter({ item ->
            run {
                ll_tab.visibility = View.VISIBLE
                val index = pageAdapter.index(item)
                view_pager.currentItem = index
                if (index == 0) {
                    notifyItem(item)
                    currentData = item
                    tv_tab_num.text = item.sort
                    if ("1" != item.isSubtitle) {
                        tv_tab_title.text = item.nodeName
                    } else {
                        tv_tab_title.text = ""
                    }
                }

            }
        }, { item ->
            exerItem = item
            ll_tab.visibility = View.GONE
            if (item.questionInfo!!.evalMode == "2" || item.questionInfo!!.evalMode == "20") {
                //???????????????
                // evalMode  0?????? ,1????????????, 2????????????, 10??????+????????????,20??????+????????????
                val intent = Intent(this, TeacherModifyDetailActivity::class.java)
                intent.putExtra(TeacherModifyDetailActivity.QUESTION_KEY, item.questionKey)
                intent.putExtra(TeacherModifyDetailActivity.EVAL_KEY, catKey)
                intent.putExtra(TeacherModifyDetailActivity.NAME, paperName)
                startActivityForResult(intent, 999)
            } else if (item.questionInfo!!.evalMode == "10") {
                //    1???????????????????????????10??????+????????????????????????
                var isOwn = pageAdapter.getData(exPagerModelList.indexOf(exerItem)).questionInfo?.isOwn
                if ("1" == isOwn) {
                    isOwn = "0"
                } else {
                    isOwn = "1"
                }
                pageAdapter.initFList()
                pageAdapter.notifyDataSetChanged()
            }

        })
    }


    private val pageAdapter by lazy { EntryPagerAdapter(supportFragmentManager) }

    override fun configView() {
        recycler_view.layoutManager = GridLayoutManager(this, 3)
        recycler_view.adapter = adapter
        view_pager.adapter = pageAdapter
    }

    override fun handleEvent() {
        var y = 0f
        var params: ViewGroup.LayoutParams? = null
        view_tab.setOnTouchListener { v, event ->
            when (event.action) {
                ACTION_DOWN -> {
                    params = ll_tab.layoutParams
                    y = event.rawY
                }
                ACTION_MOVE -> {
                    params!!.height += (y - event.rawY).toInt()//??????????????????
                    y = event.rawY
                    if (params!!.height in tabMinHeight..tabMaxHeight) {//????????????????????????
                        ll_tab.layoutParams = params //????????????
                    }
                }
                ACTION_UP -> {
                    if (params!!.height < tabMinHeight) params!!.height = tabMinHeight
                    if (params!!.height > tabMaxHeight) params!!.height = tabMaxHeight
                    ll_tab.layoutParams = params
                }
            }
            true
        }
        view_pager.onPageChangeListener {
            this.onPageSelected { pos ->
                currentData?.let { model ->
                    model.state = "2"
                    adapter.notifyItemChanged(model)
                    saPaperAnswer(model)
                }
                val data = pageAdapter.getData(pos)
                notifyItem(data)
                currentData = data
                tv_tab_num.text = data.sort
                if ("1" != data.isSubtitle) {
                    tv_tab_title.text = data.nodeName
                } else {
                    tv_tab_title.text = ""
                }
            }
        }
        toolbar.setNavigationOnClickListener {
            saOption("??????")
            onBackPressed()
        }
    }

    /**
     * ??????item??????
     */
    private fun notifyItem(item: ExerciseModel) {
        item.state = "1"
        adapter.notifyItemChanged(item)
    }

    override fun initData() {
        tv_title.text = paperName
        presenter.getExerciseParsingFrame(practisekey, userPractiseKey, catKey) { paper ->
//        presenter.getExerciseFrame(practisekey, "0", catKey) {paper->
            issubtitle = paper.issubtitle
            scoreSwitch = paper.scoreSwitch
            gradeName = paper.gradeName
            try {
                paper.questionlist?.let { list -> if (list.isNotEmpty()) initExerciseData(list) else toast("????????????~??????????????????~") }
            } catch (e: Exception) {
                Log.e("asdas", e.toString())
            }
            startSa()
        }
    }

    /**
     * ?????????????????????????????????list
     * ???????????????????????????
     */
    private fun initExerciseData(dataList: List<ExerciseFrameItem>) {
        exModelList = mutableListOf<ExerciseModel>()
        exPagerModelList = mutableListOf<ExerciseModel>()
        for (index in 0 until dataList.size) { // ?????????????????????
            val item: ExerciseFrameItem = dataList[index]
            if ("1" != item.isSubtitle) {
                exModelList.add(ExerciseModel("NODE", item.groupName))
            }
            item.questionlist?.forEach { exerciseFrameItem ->
                if ("5" == exerciseFrameItem.questionType) { //????????????????????????????????????????????????????????????????????????
                    val itemJson = Gson().toJson(exerciseFrameItem.questionInfo?.item)
                    val itemArray: List<QuestionInfo> = Gson().fromJson(itemJson, object : TypeToken<List<QuestionInfo>>() {}.type)
                    itemArray.forEach { frameItem ->
                        val model = ExerciseModel("DATA", item.groupName)
                        model.questionInfo = exerciseFrameItem.questionInfo
                        model.sort = frameItem.sort ?: ""
                        model.type = frameItem.questionType ?: ""
                        model.questionKey = frameItem.questionKey ?: ""
                        model.parentType = exerciseFrameItem.questionType ?: ""
                        if (frameItem.useranswer.isNotEmpty()) {
                            havaDone = true
                            val answer = AnswerItem("1", frameItem.useranswer)
                            model.answers.add(answer)
                            model.state = "2"
                        }
                        exModelList.add(model)
                        exPagerModelList.add(model)
                    }
                } else { //?????????????????????????????????????????????,???????????????????????????????????????
                    val model = ExerciseModel("DATA", item.groupName)
                    model.questionInfo = exerciseFrameItem.questionInfo
                    model.sort = exerciseFrameItem.questionSort ?: ""
                    model.type = exerciseFrameItem.questionType ?: ""
                    model.questionKey = exerciseFrameItem.questionKey ?: ""
                    model.parentType = exerciseFrameItem.questionType ?: ""
                    if ("1" == model.type) {
                        exerciseFrameItem.questionInfo?.useranswer?.let { userAnswer ->
                            if (userAnswer.isNotEmpty()) {
                                havaDone = true
                                val answer = AnswerItem("1", userAnswer)
                                model.answers.add(answer)
                                model.state = "2"
                            }
                        }
                    }
                    exModelList.add(model)
                    exPagerModelList.add(model)
                }
            }
        }

        adapter.setData(exModelList)
        pageAdapter.setData(exPagerModelList)
        tv_tab_total.text = "/${exPagerModelList.size}"
    }

    var havaDone = false
    fun onCheck() { //TODO ???????????????????????????
        havaDone = true
        currentData?.let {
            it.state = "2"
            adapter.notifyItemChanged(it)
        }
        if (view_pager.currentItem != exPagerModelList.size - 1) {
            var nextIndex = view_pager.currentItem + 1
            view_pager.currentItem = nextIndex
        }
    }

    override fun onBackPressed() {
        save()
    }

    private fun save() {
        val answerList = mutableListOf<AnswerQuestion>()
        exPagerModelList.forEach { pageModel ->
            if ("6" != pageModel.type) {
                val tempItem = AnswerQuestion()
                tempItem.index = 0
                tempItem.qPosition = 0
                tempItem.mPosition = 0
                tempItem.haveDone = true
                tempItem.sort = pageModel.sort
                tempItem.type = pageModel.type
                tempItem.answers = pageModel.answers
                tempItem.imgs = pageModel.imgs
                tempItem.evalMode = pageModel.evalMode
                tempItem.questionKey = pageModel.questionKey
                answerList.add(tempItem)
            }
        }
        //??????????????????????????????
        val submit = AnswerSubmit()
        submit.paperkey = practisekey
        submit.answerlist = answerList as ArrayList<AnswerQuestion>
        submit.time = "0"
        submit.catalogkey = catKey
        submit.source = "STLX"
        submit.submitType = "0"
        submit.userPractiseKey = userPractiseKey
        presenter.submitType(submit, TYPE_TEST) {
            super.onBackPressed()
        }
    }

    @SensorsDataTrackViewOnClick
    fun commit(v: View) {
        saOption("??????")
        //??????????????????
        if (System.currentTimeMillis() - lastCommitTime < FAST_CLICK_DELAY_TIME) {
            lastCommitTime = System.currentTimeMillis()
        } else {
            lastCommitTime = System.currentTimeMillis()
            // TODO ????????????
            checkAnswers {
                uploadImages {
                    val answerList = mutableListOf<AnswerQuestion>()
                    exPagerModelList.forEach { pageModel ->
                        val tempItem = AnswerQuestion()
                        tempItem.index = 0
                        tempItem.qPosition = 0
                        tempItem.mPosition = 0
                        tempItem.haveDone = true
                        tempItem.sort = pageModel.sort
                        tempItem.type = pageModel.type
                        tempItem.answers = pageModel.answers
                        tempItem.imgs = pageModel.imgs
                        tempItem.evalMode = pageModel.evalMode
                        tempItem.questionKey = pageModel.questionKey
                        answerList.add(tempItem)
                    }
                    //??????????????????????????????
                    val submit = AnswerSubmit()
                    submit.paperkey = practisekey
                    submit.answerlist = answerList as ArrayList<AnswerQuestion>
                    submit.time = "0"
                    submit.catalogkey = catKey
                    submit.source = "STLX"
                    submit.submitType = "1"
                    submit.userPractiseKey = userPractiseKey
                    presenter.submitType(submit, TYPE_TEST) {
                        startActivity<EvalCardActivity>(
                            EvalCardActivity.KEY to practisekey,
                            EvalCardActivity.NAME to paperName,
                            EvalCardActivity.DATA to it,
                            EvalCardActivity.CAT_KEY to catKey,
                            EvalCardActivity.EVAL_STATE to evalState,
                        )
                        finish()
                    }
                }
            }
        }
    }

    private fun checkAnswers(action: () -> Unit) {
        if (!havaDone) {
            toast("??????????????????~")
            return
        }
//        var allDone = true
//        exPagerModelList.forEach {
//            if (it.answers.isEmpty() && it.imgPaths.isEmpty()) {//TODO ??????????????????????????????????????????????????????????????????
//                allDone = false
//                return@forEach
//            }
//        }

//        if (allDone) {
        action()
//        } else {
//            toast("???????????????????????????????????????")
//        }
    }

    private fun uploadImages(onNext: () -> Unit) {
        var indexList = mutableListOf<Int>()//TODO ??????????????????????????????????????????
        var fileList = mutableListOf<File>()//TODO ?????????????????????????????????????????????
        for (i in 0 until exPagerModelList.size) {
            if (exPagerModelList[i].imgPaths.isNotEmpty()) {
                exPagerModelList[i].imgPaths.forEach {
                    fileList.add(File(it))
                    indexList.add(i)
                }
            }
        }
        if (fileList.isEmpty()) {
            evalState = "1"
            onNext()
        } else {
            presenter.upload("6", fileList) {
                evalState = "0"
                for (i in 0 until it.size) {
                    exPagerModelList[indexList[i]].imgs.add(it[i])
                    exPagerModelList[indexList[i]].evalMode = "1"
                }
                onNext()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            pageAdapter.getData(exPagerModelList.indexOf(exerItem)).questionInfo?.isOwn = "1"
            exerItem.questionInfo?.isOwn = "1"
            adapter.notifyItemChanged(exerItem)
            pageAdapter.initFList()
            pageAdapter.notifyDataSetChanged()
            //??????????????????
            exerItem?.let {
                it.state = "1"
                it.answers.clear()
            }
        } else {
            //TODO ?????????????????????
            ImageSeletedUtil.onActivityResult(this, requestCode, resultCode, data) {
                it?.let {
                    var f = pageAdapter.getFragment(view_pager.currentItem)
                    if (f is TranslationEditFragment) {
                        f.addImg(it)
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
        property.put("number_of_topics", exModelList.size)
        property.put("test_paper_form", if ("2" == issubtitle) "????????????" else "???????????????")
        property.put("presentation_form_paper", typeName)
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
     * id:??????id
     * type:????????????
     * answer???????????????
     */
    fun saPaperAnswer(model: ExerciseModel) {
        try {
            val property = saProperty()
            property.put("title_id", model.questionKey)
            property.put("topic_type", TypeUtil.getQType(model.parentType))
            property.put("title_number", model.sort)
            if ("1" == model.type) {
                property.put("user_answers", model.answers[0].answer)
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
}


