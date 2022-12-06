package tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing


import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_exercise_detail.*
import org.jetbrains.anko.centerInParent
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSheet
import tuoyan.com.xinghuo_dayingindex.bean.CorrectBean
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerAdapter
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailPresenter
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class ExerciseParsingActivity : LifeActivity<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_exercise_detail

    val practisekey by lazy { intent.getStringExtra(KEY) ?: "" }
    val evalkey by lazy { intent.getStringExtra(EVAL_KEY) ?: "" }
    val paperName by lazy { intent.getStringExtra(NAME) ?: "" }
    val userpractisekey by lazy { intent.getStringExtra(P_KET) ?: "" }
    val selectSort by lazy { intent.getIntExtra(SELECT_SORT, -1) }
    val exType by lazy { intent.getIntExtra(EX_TYPE, EX_TYPE_0) }
    val spQKey by lazy { intent.getStringExtra(SP_Q_KEY) ?: "" }
    val spGName by lazy { intent.getStringExtra(SP_G_NAME) ?: "" }

    //    val needUpLoad by lazy { intent.getBooleanExtra(NEED_UP_LOAD, false) }
    var fragmentList = ArrayList<ExerciseParsingFragment>()

    companion object {
        const val KEY = "key"
        const val NAME = "name"
        const val EVAL_KEY = "evalkey"
        const val P_KET = "p_key"
        const val SP_Q_KEY = "sp_q_key"
        const val SP_G_NAME = "sp_g_name"

        const val SELECT_SORT = "select_sort"

        const val EX_TYPE = "ex_type"//做题类型
        const val EX_TYPE_0 = 0 //正常做题（最普通的类型）
        const val EX_TYPE_SP = 1 //专项练习类型
        const val EX_TYPE_PG = 2 //过级包类型
        const val EX_TYPE_WORK = 3 //网课课后作业

        const val NEED_UP_LOAD = "needUpLoad" //过级包测评 需要上传照片提供阅卷的情况
    }

    override fun configView() {
        super.configView()
        ch_time.visibility = View.GONE
        ic_answers.visibility = View.GONE
        iv_catalog.visibility = View.GONE
        iv_collection.visibility = View.GONE
    }

    var remarks = ""
    override fun onBackPressed() {
        if (rl_img_detail.isShown) {
            rl_img_detail.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    override fun initData() {
        tv_paper_name.text = paperName
        initHtmlStr()
        view_pager.offscreenPageLimit = 50
        if (exType == EX_TYPE_SP) {
            presenter.getSpAnalyzes(practisekey, spQKey, userpractisekey) {
                it.questionType = "5" //TODO 接口返回解析的时候，没有返回题目类型，专项练习下，类型为材料题

                var list = ArrayList<ExerciseFrameItem>()
                var item = ExerciseFrameItem()

                var itemList = ArrayList<ExerciseFrameItem>()
                itemList.add(it)
                item.questionlist = itemList
                list.add(item)
                initExerciseData(list)
            }
        } else {
            presenter.getExerciseParsingFrame(practisekey, userpractisekey, evalkey, "0") {
                remarks = it.remarks
                try {
                    it.questionlist?.let { if (it.isNotEmpty()) initExerciseData(it) else toast("数据异常~请您稍候再试~") }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun handleEvent() {
        ic_back.setOnClickListener {
            onBackPressed()
        }
        view_pager.onPageChangeListener {
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
                setQSort(fragmentList[it].qIndex)
            }
        }
        rl_img_detail.setOnClickListener {
            rl_img_detail.visibility = View.GONE
        }
    }

    fun setQSort(index: Int) {
        tv_index.text = answerSheets[index].questionSort
    }

    /**
     * 构造显示题目及答题卡的list
     */
    var current = 0 //存储当前题目的题号
    var qIndexs = mutableListOf<Int>() //大题的索引
    var qIndex = 0
    var mIndexs = mutableListOf<Int>() //大题对应小题的索引
    val answerSheets = mutableListOf<AnswerSheet>()
    private fun initExerciseData(dataList: List<ExerciseFrameItem>) {
        for (index in 0 until dataList.size) { // 大题分类的结构
            var item: ExerciseFrameItem = dataList[index]
            var exerItem = ExerciseFrameItem(
                true,
                if (index == 0) paperName ?: "" else "",
                if (exType == EX_TYPE_SP) spGName else item.groupName,
                if (item.paperExplain != null) {
                    item.paperExplain
                } else {
                    ""
                },
                if (index == 0) remarks else ""
            )
            exerItem.isSubtitle = item.isSubtitle
            fragmentList.add(ExerciseParsingFragment.newInstance(exerItem))
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
                        val sheet = AnswerSheet()
                        sheet.questionSort = it.questionSort ?: ""
                        answerSheets.add(sheet)
                    }
                } else {
                    mIndexs.add(0)
                    qIndexs.add(qIndex)
                    current++
                    val sheet = AnswerSheet()
                    sheet.questionSort = it.questionSort ?: ""
                    answerSheets.add(sheet)
                }
                qIndex++
            }
        }

        tv_total.text = "/" + current.toString()
        var oAdapter = ExercisePagerAdapter(supportFragmentManager, fragmentList)
        view_pager.adapter = oAdapter

        if (selectSort != -1) {
            var handler = Handler()
            handler.postDelayed({
                try {
                    view_pager.currentItem = qIndexs[selectSort - 1]
                    fragmentList[qIndexs[selectSort - 1]].jumpIndex(mIndexs[selectSort - 1])
                } catch (e: Exception) {
                }
            }, 500)
        }
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
    fun goNext() {
        if (view_pager.currentItem == fragmentList.size - 1) {
            //do nothing
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

    override fun onPause() {
        super.onPause()
        if (fragmentList.isNotEmpty()) {
            fragmentList[view_pager.currentItem].musicStop()//结束当前页面时，调用结束当前题目音频的方法
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == RESULT_OK) {
            fragmentList.get(view_pager.currentItem).onActivityResult(requestCode, resultCode, data)
        }
    }

    fun showImg(item: CorrectBean) {
        rl_img_detail.removeAllViews()
        var imgView = ImageView(this)
        var params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.centerInParent()
        rl_img_detail.addView(imgView, params)
        Glide.with(this).load(item.imgUrl).into(imgView)
        rl_img_detail.visibility = View.VISIBLE
    }
}
